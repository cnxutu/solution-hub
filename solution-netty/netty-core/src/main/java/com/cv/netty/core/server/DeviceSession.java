package com.cv.netty.core.server;

public class DeviceSession {

    private final String deviceId;
    private final String channelId;
    private final String remoteAddress;
    private volatile long connectedAt;
    private volatile long lastSeenAt;

    public DeviceSession(String deviceId, String channelId, String remoteAddress, long connectedAt, long lastSeenAt) {
        this.deviceId = deviceId;
        this.channelId = channelId;
        this.remoteAddress = remoteAddress;
        this.connectedAt = connectedAt;
        this.lastSeenAt = lastSeenAt;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public long getConnectedAt() {
        return connectedAt;
    }

    public void setConnectedAt(long connectedAt) {
        this.connectedAt = connectedAt;
    }

    public long getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(long lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }
}
