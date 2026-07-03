package com.cv.simulator.videoosd.v1.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class OsdBroadcastWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(OsdBroadcastWebSocketHandler.class);

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        log.info("OSD_TRACE [WS_CONNECTED] sessionId={}, activeSessions={}", session.getId(), sessions.size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        log.info("OSD_TRACE [WS_CLOSED] sessionId={}, status={}, activeSessions={}",
                session.getId(), status, sessions.size());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        sessions.remove(session);
        log.warn("OSD_TRACE [WS_ERROR] sessionId={}, activeSessions={}", session.getId(), sessions.size(), exception);
        super.handleTransportError(session, exception);
    }

    public void broadcast(String payloadJson) {
        TextMessage message = new TextMessage(payloadJson);
        sessions.removeIf(session -> !session.isOpen());
        if (sessions.isEmpty()) {
            log.warn("OSD_TRACE [WS_NO_ACTIVE_SESSION] payloadSize={}, message=no websocket clients connected to relay",
                    payloadJson.length());
        }
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(message);
            } catch (IOException e) {
                sessions.remove(session);
                throw new IllegalStateException("failed to broadcast osd payload", e);
            }
        }
    }

    public int activeSessionCount() {
        sessions.removeIf(session -> !session.isOpen());
        return sessions.size();
    }
}
