package com.cv.netty.core;

import com.cv.netty.core.client.ClientMessageListener;
import com.cv.netty.core.client.NettyTcpClient;
import com.cv.netty.core.common.NettyConstants;
import com.cv.netty.core.model.TransportMessage;
import com.cv.netty.core.server.DeviceSession;
import com.cv.netty.core.server.NettyTcpServer;
import com.cv.netty.core.server.ServerMessageListener;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.List;

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
        CountingServerMessageListener serverListener = new CountingServerMessageListener();
        server = new NettyTcpServer(19091, 1, 1, 20, serverListener);
        server.start();

        client = new NettyTcpClient("127.0.0.1", 19091, "device-test-01", 3, 1, new SilentClientMessageListener());
        client.start();

        Assertions.assertTrue(serverListener.registerLatch.await(5, TimeUnit.SECONDS), "device should register");
        Assertions.assertNotNull(server.getSessionManager().getSession("device-test-01"));

        Map<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("priority", "HIGH");
        Assertions.assertTrue(server.sendCommandToDevice("device-test-01", "capture-photo", payload));
        Assertions.assertTrue(serverListener.commandReplyLatch.await(5, TimeUnit.SECONDS), "device should reply command");
    }

    @Test
    void should_receive_telemetry_and_heartbeat() throws Exception {
        CountingServerMessageListener serverListener = new CountingServerMessageListener();
        server = new NettyTcpServer(19092, 1, 1, 20, serverListener);
        server.start();

        client = new NettyTcpClient("127.0.0.1", 19092, "device-test-02", 1, 1, new SilentClientMessageListener());
        client.start();

        Assertions.assertTrue(serverListener.registerLatch.await(5, TimeUnit.SECONDS), "device should register");

        Map<String, Object> telemetryPayload = new LinkedHashMap<String, Object>();
        telemetryPayload.put("temperature", 25.6D);
        telemetryPayload.put("humidity", 66.8D);
        telemetryPayload.put("siteCode", "PARK-01");
        client.sendTelemetry(telemetryPayload);

        Assertions.assertTrue(serverListener.telemetryLatch.await(5, TimeUnit.SECONDS), "server should receive telemetry");
        Assertions.assertTrue(serverListener.heartbeatLatch.await(5, TimeUnit.SECONDS), "server should receive heartbeat");
        Assertions.assertTrue(serverListener.receivedTypes.contains(NettyConstants.TYPE_TELEMETRY));
        Assertions.assertTrue(serverListener.receivedTypes.contains(NettyConstants.TYPE_HEARTBEAT));
    }

    @Test
    void should_return_false_when_sending_command_to_offline_device() throws Exception {
        server = new NettyTcpServer(19093, 1, 1, 20, new CountingServerMessageListener());
        server.start();

        Map<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("priority", "LOW");
        boolean success = server.sendCommandToDevice("device-offline-01", "reboot", payload);

        Assertions.assertFalse(success, "offline device should not accept command");
    }

    private static class SilentClientMessageListener implements ClientMessageListener {

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
    }

    private static class CountingServerMessageListener implements ServerMessageListener {

        private final CountDownLatch registerLatch = new CountDownLatch(1);
        private final CountDownLatch heartbeatLatch = new CountDownLatch(1);
        private final CountDownLatch telemetryLatch = new CountDownLatch(1);
        private final CountDownLatch commandReplyLatch = new CountDownLatch(1);
        private final List<String> receivedTypes = new CopyOnWriteArrayList<String>();

        @Override
        public void onSessionRegistered(DeviceSession session, TransportMessage message) {
            receivedTypes.add(NettyConstants.TYPE_REGISTER);
            registerLatch.countDown();
        }

        @Override
        public void onHeartbeat(DeviceSession session, TransportMessage message) {
            receivedTypes.add(NettyConstants.TYPE_HEARTBEAT);
            heartbeatLatch.countDown();
        }

        @Override
        public void onTelemetry(DeviceSession session, TransportMessage message) {
            receivedTypes.add(NettyConstants.TYPE_TELEMETRY);
            telemetryLatch.countDown();
        }

        @Override
        public void onCommandReply(DeviceSession session, TransportMessage message) {
            receivedTypes.add(NettyConstants.TYPE_COMMAND_REPLY);
            commandReplyLatch.countDown();
        }

        @Override
        public void onSessionClosed(DeviceSession session) {
        }

        @Override
        public void onException(Throwable throwable) {
        }
    }
}
