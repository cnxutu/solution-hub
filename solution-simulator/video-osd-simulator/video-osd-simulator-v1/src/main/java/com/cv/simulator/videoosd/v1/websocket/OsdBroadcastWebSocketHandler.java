package com.cv.simulator.videoosd.v1.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class OsdBroadcastWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(OsdBroadcastWebSocketHandler.class);

    private final ConcurrentMap<String, SessionState> sessions = new ConcurrentHashMap<String, SessionState>();
    private final ExecutorService senderExecutor;
    private final long dropSummaryIntervalMillis;
    private final long sendSlowThresholdMillis;

    public OsdBroadcastWebSocketHandler(int senderThreads,
                                        long dropSummaryIntervalMillis,
                                        long sendSlowThresholdMillis) {
        this.senderExecutor = Executors.newFixedThreadPool(senderThreads, new NamedThreadFactory());
        this.dropSummaryIntervalMillis = dropSummaryIntervalMillis;
        this.sendSlowThresholdMillis = sendSlowThresholdMillis;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), new SessionState(session));
        log.info("OSD_TRACE [WS_CONNECTED] sessionId={}, activeSessions={}", session.getId(), sessions.size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        removeSession(session.getId(), "closed:" + status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        removeSession(session.getId(), "transport_error:" + exception.getMessage());
        super.handleTransportError(session, exception);
    }

    public void broadcast(String payloadJson) {
        removeClosedSessions();
        if (sessions.isEmpty()) {
            log.warn("OSD_TRACE [WS_NO_ACTIVE_SESSION] payloadSize={}, message=no websocket clients connected to relay",
                    payloadJson.length());
            return;
        }
        for (SessionState state : sessions.values()) {
            state.offer(payloadJson);
        }
    }

    public int activeSessionCount() {
        removeClosedSessions();
        return sessions.size();
    }

    @PreDestroy
    public void shutdown() {
        senderExecutor.shutdownNow();
    }

    SessionMetrics getSessionMetrics(String sessionId) {
        SessionState state = sessions.get(sessionId);
        return state == null ? null : state.snapshot();
    }

    private void removeClosedSessions() {
        for (SessionState state : sessions.values()) {
            if (!state.session.isOpen()) {
                removeSession(state.session.getId(), "not_open");
            }
        }
    }

    private void removeSession(String sessionId, String reason) {
        SessionState removed = sessions.remove(sessionId);
        if (removed != null) {
            removed.closed.set(true);
            removed.logDropSummaryIfDue(System.currentTimeMillis(), true);
            log.warn("OSD_TRACE [WS_SESSION_REMOVED] sessionId={}, reason={}, activeSessions={}",
                    sessionId, reason, sessions.size());
        }
    }

    private final class SessionState {
        private final WebSocketSession session;
        private final AtomicBoolean sending = new AtomicBoolean(false);
        private final AtomicBoolean closed = new AtomicBoolean(false);
        private final AtomicReference<String> latestPendingPayload = new AtomicReference<String>();
        private final AtomicLong droppedCount = new AtomicLong();
        private final AtomicLong lastDroppedAtMillis = new AtomicLong();
        private final AtomicLong lastSentAtMillis = new AtomicLong();
        private final AtomicLong summaryWindowStartMillis = new AtomicLong(System.currentTimeMillis());
        private final AtomicLong summaryDroppedCount = new AtomicLong();

        private SessionState(WebSocketSession session) {
            this.session = session;
        }

        private void offer(String payload) {
            if (closed.get()) {
                return;
            }
            if (sending.compareAndSet(false, true)) {
                dispatch(payload);
                return;
            }
            String replaced = latestPendingPayload.getAndSet(payload);
            if (replaced != null) {
                long now = System.currentTimeMillis();
                droppedCount.incrementAndGet();
                summaryDroppedCount.incrementAndGet();
                lastDroppedAtMillis.set(now);
                logDropSummaryIfDue(now, false);
            }
        }

        private void dispatch(final String initialPayload) {
            senderExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    sendLoop(initialPayload);
                }
            });
        }

        private void sendLoop(String currentPayload) {
            try {
                String payloadToSend = currentPayload;
                while (payloadToSend != null && !closed.get()) {
                    sendOnce(payloadToSend);
                    payloadToSend = latestPendingPayload.getAndSet(null);
                }
            } finally {
                sending.set(false);
                String latePayload = latestPendingPayload.getAndSet(null);
                if (latePayload != null && !closed.get() && sending.compareAndSet(false, true)) {
                    dispatch(latePayload);
                }
            }
        }

        private void sendOnce(String payload) {
            if (!session.isOpen()) {
                removeSession(session.getId(), "send_on_closed_session");
                return;
            }
            long startedAtMillis = System.currentTimeMillis();
            try {
                session.sendMessage(new TextMessage(payload));
                long finishedAtMillis = System.currentTimeMillis();
                lastSentAtMillis.set(finishedAtMillis);
                long durationMillis = finishedAtMillis - startedAtMillis;
                if (durationMillis >= sendSlowThresholdMillis) {
                    log.warn("OSD_TRACE [WS_SEND_SLOW] sessionId={}, durationMillis={}, thresholdMillis={}, activeSessions={}",
                            session.getId(), durationMillis, sendSlowThresholdMillis, sessions.size());
                }
                logDropSummaryIfDue(finishedAtMillis, false);
            } catch (IOException e) {
                removeSession(session.getId(), "send_failed");
                throw new IllegalStateException("failed to broadcast osd payload", e);
            }
        }

        private void logDropSummaryIfDue(long now, boolean force) {
            long dropped = summaryDroppedCount.get();
            long windowStart = summaryWindowStartMillis.get();
            if (dropped <= 0L) {
                return;
            }
            if (!force && now - windowStart < dropSummaryIntervalMillis) {
                return;
            }
            if (summaryWindowStartMillis.compareAndSet(windowStart, now)) {
                long flushedDropped = summaryDroppedCount.getAndSet(0L);
                log.warn("OSD_TRACE [WS_DROP_SUMMARY] sessionId={}, windowStartMillis={}, windowEndMillis={}, droppedCount={}, lastDroppedAtMillis={}, lastSentAtMillis={}, activeSessions={}",
                        session.getId(),
                        windowStart,
                        now,
                        flushedDropped,
                        lastDroppedAtMillis.get(),
                        lastSentAtMillis.get(),
                        sessions.size());
            }
        }

        private SessionMetrics snapshot() {
            return new SessionMetrics(
                    session.getId(),
                    droppedCount.get(),
                    lastDroppedAtMillis.get(),
                    lastSentAtMillis.get());
        }
    }

    static final class SessionMetrics {
        private final String sessionId;
        private final long droppedCount;
        private final long lastDroppedAtMillis;
        private final long lastSentAtMillis;

        private SessionMetrics(String sessionId,
                               long droppedCount,
                               long lastDroppedAtMillis,
                               long lastSentAtMillis) {
            this.sessionId = sessionId;
            this.droppedCount = droppedCount;
            this.lastDroppedAtMillis = lastDroppedAtMillis;
            this.lastSentAtMillis = lastSentAtMillis;
        }

        public String getSessionId() {
            return sessionId;
        }

        public long getDroppedCount() {
            return droppedCount;
        }

        public long getLastDroppedAtMillis() {
            return lastDroppedAtMillis;
        }

        public long getLastSentAtMillis() {
            return lastSentAtMillis;
        }
    }

    private static final class NamedThreadFactory implements ThreadFactory {
        private final AtomicLong counter = new AtomicLong();

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable, "osd-ws-sender-" + counter.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        }
    }
}
