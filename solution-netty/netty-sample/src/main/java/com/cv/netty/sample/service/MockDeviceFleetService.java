package com.cv.netty.sample.service;

import com.cv.netty.core.client.LoggingClientMessageListener;
import com.cv.netty.core.client.NettyTcpClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MockDeviceFleetService {

    private final boolean enabled;
    private final int count;
    private final String host;
    private final int port;
    private final int heartbeatSeconds;
    private final int reconnectSeconds;
    private final Map<String, NettyTcpClient> clients = new ConcurrentHashMap<String, NettyTcpClient>();

    public MockDeviceFleetService(boolean enabled, int count, String host, int port, int heartbeatSeconds, int reconnectSeconds) {
        this.enabled = enabled;
        this.count = count;
        this.host = host;
        this.port = port;
        this.heartbeatSeconds = heartbeatSeconds;
        this.reconnectSeconds = reconnectSeconds;
    }

    public void start() {
        if (!enabled) {
            return;
        }
        for (int i = 1; i <= count; i++) {
            String deviceId = String.format("mock-device-%02d", i);
            NettyTcpClient client = new NettyTcpClient(host, port, deviceId, heartbeatSeconds, reconnectSeconds,
                    new LoggingClientMessageListener());
            clients.put(deviceId, client);
            client.start();
        }
    }

    public void stop() {
        for (NettyTcpClient client : clients.values()) {
            client.stop();
        }
        clients.clear();
    }

    public Collection<String> listDeviceIds() {
        List<String> deviceIds = new ArrayList<String>(clients.keySet());
        Collections.sort(deviceIds);
        return deviceIds;
    }

    public boolean sendTelemetry(String deviceId, double temperature, double humidity) {
        NettyTcpClient client = clients.get(deviceId);
        if (client == null) {
            return false;
        }
        Map<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("temperature", temperature);
        payload.put("humidity", humidity);
        payload.put("reportedAt", System.currentTimeMillis());
        client.sendTelemetry(payload);
        return true;
    }

    public int broadcastTelemetry() {
        int success = 0;
        int index = 0;
        for (String deviceId : listDeviceIds()) {
            double temperature = 20D + index;
            double humidity = 50D + index;
            if (sendTelemetry(deviceId, temperature, humidity)) {
                success++;
            }
            index++;
        }
        return success;
    }
}
