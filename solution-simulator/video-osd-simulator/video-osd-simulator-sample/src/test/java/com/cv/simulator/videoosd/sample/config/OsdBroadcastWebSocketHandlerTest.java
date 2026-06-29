package com.cv.simulator.videoosd.sample.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.socket.CloseStatus;
import org.springframework.http.HttpHeaders;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

class OsdBroadcastWebSocketHandlerTest {

    @Test
    void broadcastsPayloadToAllConnectedSessions() throws Exception {
        OsdBroadcastWebSocketHandler handler = new OsdBroadcastWebSocketHandler();
        FakeWebSocketSession sessionA = new FakeWebSocketSession("A");
        FakeWebSocketSession sessionB = new FakeWebSocketSession("B");

        handler.afterConnectionEstablished(sessionA);
        handler.afterConnectionEstablished(sessionB);
        handler.broadcast("{\"device_sn\":\"dock-001\"}");

        assertEquals(List.of("{\"device_sn\":\"dock-001\"}"), sessionA.sentPayloads);
        assertEquals(List.of("{\"device_sn\":\"dock-001\"}"), sessionB.sentPayloads);
    }

    @Test
    void skipsClosedSessionsWhenBroadcasting() throws Exception {
        OsdBroadcastWebSocketHandler handler = new OsdBroadcastWebSocketHandler();
        FakeWebSocketSession openSession = new FakeWebSocketSession("open");
        FakeWebSocketSession closedSession = new FakeWebSocketSession("closed");

        handler.afterConnectionEstablished(openSession);
        handler.afterConnectionEstablished(closedSession);
        handler.afterConnectionClosed(closedSession, CloseStatus.NORMAL);
        handler.broadcast("{\"device_sn\":\"dock-002\"}");

        assertEquals(List.of("{\"device_sn\":\"dock-002\"}"), openSession.sentPayloads);
        assertEquals(Collections.emptyList(), closedSession.sentPayloads);
    }

    private static class FakeWebSocketSession implements WebSocketSession {
        private final String id;
        private final List<String> sentPayloads = new ArrayList<>();
        private boolean open = true;

        private FakeWebSocketSession(String id) {
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
    }
}
