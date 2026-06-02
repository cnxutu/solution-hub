package com.cv.netty.core;

import com.cv.netty.core.client.ClientMessageListener;
import com.cv.netty.core.client.NettyTcpClient;
import com.cv.netty.core.model.TransportMessage;
import com.cv.netty.core.server.DeviceSession;
import com.cv.netty.core.server.NettyTcpServer;
import com.cv.netty.core.server.ServerMessageListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class NettyTcpIntegrationTest {

    private NettyTcpServer server;
    private NettyTcpClient client;

    @AfterEach
    void tearDown() {
        if (client != null) {
            client.stop();
        }
        if (server != null) {
            server.stop();
        }
    }

    @Test
    void should_register_and_reply_command() throws Exception {
        CountDownLatch registerLatch = new CountDownLatch(1);
        CountDownLatch replyLatch = new CountDownLatch(1);

        server = new NettyTcpServer(19091, 1, 1, 20, new ServerMessageListener() {
            @Override
            public void onSessionRegistered(DeviceSession session, TransportMessage message) {
                registerLatch.countDown();
            }

            @Override
            public void onHeartbeat(DeviceSession session, TransportMessage message) {
            }

            @Override
            public void onTelemetry(DeviceSession session, TransportMessage message) {
            }

            @Override
            public void onCommandReply(DeviceSession session, TransportMessage message) {
                replyLatch.countDown();
            }

            @Override
            public void onSessionClosed(DeviceSession session) {
            }

            @Override
            public void onException(Throwable throwable) {
            }
        });
        server.start();

        client = new NettyTcpClient("127.0.0.1", 19091, "device-test-01", 3, 1, new ClientMessageListener() {
            @Override
            public void onConnected(String deviceId) {
            }

            @Override
            public void onDisconnected(String deviceId) {
            }

            @Override
            public void onServerMessage(String deviceId, TransportMessage message) {
            }

            @Override
            public void onAck(String deviceId, TransportMessage message) {
            }

            @Override
            public void onException(String deviceId, Throwable throwable) {
            }
        });
        client.start();

        Assertions.assertTrue(registerLatch.await(5, TimeUnit.SECONDS), "device should register");

        Map<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("priority", "HIGH");
        Assertions.assertTrue(server.sendCommandToDevice("device-test-01", "capture-photo", payload));
        Assertions.assertTrue(replyLatch.await(5, TimeUnit.SECONDS), "device should reply command");
    }
}
