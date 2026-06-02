package com.cv.netty.core.client;

import com.cv.netty.core.model.TransportMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingClientMessageListener implements ClientMessageListener {

    private static final Logger log = LoggerFactory.getLogger(LoggingClientMessageListener.class);

    @Override
    public void onConnected(String deviceId) {
        log.info("Client connected, deviceId={}", deviceId);
    }

    @Override
    public void onDisconnected(String deviceId) {
        log.warn("Client disconnected, deviceId={}", deviceId);
    }

    @Override
    public void onServerMessage(String deviceId, TransportMessage message) {
        log.info("Server pushed message, deviceId={}, type={}, payload={}", deviceId, message.getType(), message.getPayload());
    }

    @Override
    public void onAck(String deviceId, TransportMessage message) {
        log.debug("Ack received, deviceId={}, payload={}", deviceId, message.getPayload());
    }

    @Override
    public void onException(String deviceId, Throwable throwable) {
        log.error("Client exception, deviceId=" + deviceId, throwable);
    }
}
