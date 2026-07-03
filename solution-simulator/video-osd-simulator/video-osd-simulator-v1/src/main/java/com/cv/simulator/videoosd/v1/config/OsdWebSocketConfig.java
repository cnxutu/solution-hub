package com.cv.simulator.videoosd.v1.config;

import com.cv.simulator.videoosd.v1.websocket.OsdBroadcastWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class OsdWebSocketConfig implements WebSocketConfigurer {

    private final OsdBroadcastWebSocketHandler osdBroadcastWebSocketHandler;
    private final SimulatorOsdProperties properties;

    public OsdWebSocketConfig(OsdBroadcastWebSocketHandler osdBroadcastWebSocketHandler,
                              SimulatorOsdProperties properties) {
        this.osdBroadcastWebSocketHandler = osdBroadcastWebSocketHandler;
        this.properties = properties;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(osdBroadcastWebSocketHandler, properties.getWebsocketPath())
                .setAllowedOrigins("*");
    }
}
