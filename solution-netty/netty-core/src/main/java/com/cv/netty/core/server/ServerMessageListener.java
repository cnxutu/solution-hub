package com.cv.netty.core.server;

import com.cv.netty.core.model.TransportMessage;

public interface ServerMessageListener {

    void onSessionRegistered(DeviceSession session, TransportMessage message);

    void onHeartbeat(DeviceSession session, TransportMessage message);

    void onTelemetry(DeviceSession session, TransportMessage message);

    void onCommandReply(DeviceSession session, TransportMessage message);

    void onSessionClosed(DeviceSession session);

    void onException(Throwable throwable);
}
