package com.cv.simulator.videoosd.v1.websocket;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OsdBroadcastWebSocketHandlerTest {

    @Test
    void broadcastsPayloadToAllConnectedSessions() throws Exception {
        OsdBroadcastWebSocketHandler handler = new OsdBroadcastWebSocketHandler(2, 1000L, 1000L);
        FakeWebSocketSession sessionA = new FakeWebSocketSession("A");
        FakeWebSocketSession sessionB = new FakeWebSocketSession("B");

        handler.afterConnectionEstablished(sessionA);
        handler.afterConnectionEstablished(sessionB);
        handler.broadcast("{\"device_sn\":\"dock-001\"}");
        assertTrue(sessionA.awaitSentCount(1));
        assertTrue(sessionB.awaitSentCount(1));

        assertEquals(Collections.singletonList("{\"device_sn\":\"dock-001\"}"), sessionA.sentPayloads);
        assertEquals(Collections.singletonList("{\"device_sn\":\"dock-001\"}"), sessionB.sentPayloads);
    }

    @Test
    void skipsClosedSessionsWhenBroadcasting() throws Exception {
        OsdBroadcastWebSocketHandler handler = new OsdBroadcastWebSocketHandler(1, 1000L, 1000L);
        FakeWebSocketSession openSession = new FakeWebSocketSession("open");
        FakeWebSocketSession closedSession = new FakeWebSocketSession("closed");

        handler.afterConnectionEstablished(openSession);
        handler.afterConnectionEstablished(closedSession);
        handler.afterConnectionClosed(closedSession, CloseStatus.NORMAL);
        handler.broadcast("{\"device_sn\":\"dock-002\"}");
        assertTrue(openSession.awaitSentCount(1));

        assertEquals(Collections.singletonList("{\"device_sn\":\"dock-002\"}"), openSession.sentPayloads);
        assertEquals(Collections.emptyList(), closedSession.sentPayloads);
    }

    @Test
    void reportsActiveSessionCount() throws Exception {
        OsdBroadcastWebSocketHandler handler = new OsdBroadcastWebSocketHandler(1, 1000L, 1000L);
        FakeWebSocketSession openSession = new FakeWebSocketSession("open");
        FakeWebSocketSession closedSession = new FakeWebSocketSession("closed");

        handler.afterConnectionEstablished(openSession);
        handler.afterConnectionEstablished(closedSession);
        closedSession.close();
        handler.broadcast("{\"device_sn\":\"dock-003\"}");

        assertEquals(1, handler.activeSessionCount());
    }

    @Test
    void keepsOnlyLatestPendingPayloadForSlowSession() throws Exception {
        OsdBroadcastWebSocketHandler handler = new OsdBroadcastWebSocketHandler(1, 5L, 1000L);
        BlockingWebSocketSession slowSession = new BlockingWebSocketSession("slow");

        handler.afterConnectionEstablished(slowSession);
        handler.broadcast("{\"seq\":1}");
        assertTrue(slowSession.awaitFirstSendStarted());

        handler.broadcast("{\"seq\":2}");
        handler.broadcast("{\"seq\":3}");

        slowSession.releaseFirstSend();
        assertTrue(slowSession.awaitAllSends());

        assertEquals(2, slowSession.sentPayloads.size());
        assertEquals("{\"seq\":1}", slowSession.sentPayloads.get(0));
        assertEquals("{\"seq\":3}", slowSession.sentPayloads.get(1));

        OsdBroadcastWebSocketHandler.SessionMetrics metrics = handler.getSessionMetrics("slow");
        assertNotNull(metrics);
        assertEquals(1L, metrics.getDroppedCount());
        assertTrue(metrics.getLastDroppedAtMillis() > 0L);
    }

    private static class FakeWebSocketSession implements WebSocketSession {
        private final String id;
        protected final List<String> sentPayloads = new ArrayList<String>();
        private final AtomicInteger sentCount = new AtomicInteger();
        private boolean open = true;

        protected FakeWebSocketSession(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public URI getUri() {
            return URI.create("ws://127.0.0.1/ws/osd");
        }

        @Override
        public Map<String, Object> getAttributes() {
            return Collections.emptyMap();
        }

        @Override
        public HttpHeaders getHandshakeHeaders() {
            return HttpHeaders.EMPTY;
        }

        @Override
        public Principal getPrincipal() {
            return null;
        }

        @Override
        public InetSocketAddress getLocalAddress() {
            return null;
        }

        @Override
        public InetSocketAddress getRemoteAddress() {
            return null;
        }

        @Override
        public String getAcceptedProtocol() {
            return null;
        }

        @Override
        public void setTextMessageSizeLimit(int messageSizeLimit) {
        }

        @Override
        public int getTextMessageSizeLimit() {
            return 0;
        }

        @Override
        public void setBinaryMessageSizeLimit(int messageSizeLimit) {
        }

        @Override
        public int getBinaryMessageSizeLimit() {
            return 0;
        }

        @Override
        public List<WebSocketExtension> getExtensions() {
            return Collections.emptyList();
        }

        @Override
        public void sendMessage(WebSocketMessage<?> message) throws IOException {
            sentPayloads.add(String.valueOf(message.getPayload()));
            sentCount.incrementAndGet();
        }

        @Override
        public boolean isOpen() {
            return open;
        }

        @Override
        public void close() {
            open = false;
        }

        @Override
        public void close(CloseStatus status) {
            open = false;
        }

        protected boolean awaitSentCount(int expectedCount) throws InterruptedException {
            long deadline = System.currentTimeMillis() + 2000L;
            while (System.currentTimeMillis() < deadline) {
                if (sentCount.get() >= expectedCount) {
                    return true;
                }
                Thread.sleep(10L);
            }
            return sentCount.get() >= expectedCount;
        }
    }

    private static final class BlockingWebSocketSession extends FakeWebSocketSession {
        private final CountDownLatch firstSendStarted = new CountDownLatch(1);
        private final CountDownLatch releaseFirstSend = new CountDownLatch(1);
        private final CountDownLatch allSendsDone = new CountDownLatch(2);
        private final AtomicInteger sendCount = new AtomicInteger();

        private BlockingWebSocketSession(String id) {
            super(id);
        }

        @Override
        public void sendMessage(WebSocketMessage<?> message) throws IOException {
            int index = sendCount.incrementAndGet();
            if (index == 1) {
                firstSendStarted.countDown();
                try {
                    releaseFirstSend.await(2, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException("interrupted", e);
                }
            }
            super.sendMessage(message);
            allSendsDone.countDown();
        }

        private boolean awaitFirstSendStarted() throws InterruptedException {
            return firstSendStarted.await(2, TimeUnit.SECONDS);
        }

        private void releaseFirstSend() {
            releaseFirstSend.countDown();
        }

        private boolean awaitAllSends() throws InterruptedException {
            return allSendsDone.await(2, TimeUnit.SECONDS);
        }
    }
}
