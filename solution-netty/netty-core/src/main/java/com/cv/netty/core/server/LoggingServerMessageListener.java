package com.cv.netty.core.server;

import com.cv.netty.core.model.TransportMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingServerMessageListener implements ServerMessageListener {

    private static final Logger log = LoggerFactory.getLogger(LoggingServerMessageListener.class);

    @Override
    public void onSessionRegistered(DeviceSession session, TransportMessage message) {
        log.info("Device registered, deviceId={}, remoteAddress={}", session.getDeviceId(), session.getRemoteAddress());
    }

    @Override
    public void onHeartbeat(DeviceSession session, TransportMessage message) {
        log.debug("Heartbeat received, deviceId={}, payload={}", session.getDeviceId(), message.getPayload());
    }

    @Override
    public void onTelemetry(DeviceSession session, TransportMessage message) {
        log.info("Telemetry received, deviceId={}, payload={}", session.getDeviceId(), message.getPayload());
    }

    @Override
    public void onCommandReply(DeviceSession session, TransportMessage message) {
        log.info("Command reply received, deviceId={}, payload={}", session.getDeviceId(), message.getPayload());
    }

    @Override
    public void onSessionClosed(DeviceSession session) {
        if (session != null) {
            log.info("Session closed, deviceId={}, channelId={}", session.getDeviceId(), session.getChannelId());
        }
    }

    @Override
    public void onException(Throwable throwable) {
        log.error("Server message listener exception", throwable);
    }
}
