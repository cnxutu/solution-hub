package com.cv.simulator.videoosd.core.websocket;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.TimeUnit;

public class WebSocketOsdSender {

    private final StandardWebSocketClient client = new StandardWebSocketClient();

    public void send(String endpoint, String payloadJson) {
        try {
            WebSocketSession session = client.doHandshake(new TextWebSocketHandler(), endpoint)
                    .get(10, TimeUnit.SECONDS);
            session.sendMessage(new TextMessage(payloadJson));
            session.close();
        } catch (Exception e) {
            throw new IllegalStateException("failed to send osd payload by websocket", e);
        }
    }
}
