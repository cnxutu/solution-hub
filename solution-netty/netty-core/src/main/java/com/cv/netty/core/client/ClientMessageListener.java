package com.cv.netty.core.client;

import com.cv.netty.core.model.TransportMessage;

public interface ClientMessageListener {

    void onConnected(String deviceId);

    void onDisconnected(String deviceId);

    void onServerMessage(String deviceId, TransportMessage message);

    void onAck(String deviceId, TransportMessage message);

    void onException(String deviceId, Throwable throwable);
}
