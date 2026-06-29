package com.cv.simulator.videoosd.sample.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class OsdWebSocketConfig implements WebSocketConfigurer {

    private final OsdBroadcastWebSocketHandler osdBroadcastWebSocketHandler;

    public OsdWebSocketConfig(OsdBroadcastWebSocketHandler osdBroadcastWebSocketHandler) {
        this.osdBroadcastWebSocketHandler = osdBroadcastWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(osdBroadcastWebSocketHandler, "/ws/osd")
                .setAllowedOrigins("*");
    }
}
