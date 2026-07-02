package com.cv.simulator.videoosd.sample.config;

import lombok.extern.slf4j.Slf4j;
import com.cv.simulator.videoosd.sample.service.OsdReplayLogSupport;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class OsdBroadcastWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        log.info("{} sessionId={}, activeSessions={}",
                OsdReplayLogSupport.marker("WS_CONNECTED"), session.getId(), sessions.size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        log.info("{} sessionId={}, status={}, activeSessions={}",
                OsdReplayLogSupport.marker("WS_CLOSED"), session.getId(), status, sessions.size());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        sessions.remove(session);
        log.warn("{} sessionId={}, activeSessions={}",
                OsdReplayLogSupport.marker("WS_ERROR"), session.getId(), sessions.size(), exception);
        super.handleTransportError(session, exception);
    }

    public void broadcast(String payloadJson) {
        TextMessage message = new TextMessage(payloadJson);
        sessions.removeIf(session -> !session.isOpen());
        log.info("{} activeSessions={}, payloadSize={}",
                OsdReplayLogSupport.marker("WS_BROADCAST_START"), sessions.size(), payloadJson.length());
        if (sessions.isEmpty()) {
            log.warn("OSD_TRACE [WS_NO_ACTIVE_SESSION] payloadSize={}, message=no websocket clients connected to /ws/osd",
                    payloadJson.length());
        }
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(message);
                log.info("{} sessionId={}, payloadSize={}",
                        OsdReplayLogSupport.marker("WS_BROADCAST_SUCCESS"), session.getId(), payloadJson.length());
            } catch (IOException e) {
                sessions.remove(session);
                log.error("{} sessionId={}, activeSessions={}",
                        OsdReplayLogSupport.marker("WS_BROADCAST_FAILED"), session.getId(), sessions.size(), e);
                throw new IllegalStateException("failed to broadcast osd payload", e);
            }
        }
    }

    public int activeSessionCount() {
        sessions.removeIf(session -> !session.isOpen());
        return sessions.size();
    }
}
