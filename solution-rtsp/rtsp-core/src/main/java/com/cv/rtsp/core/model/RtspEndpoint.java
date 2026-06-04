package com.cv.rtsp.core.model;

public class RtspEndpoint {

    private final String streamId;
    private final String rtspUrl;
    private final String username;
    private final String password;
    private final int connectTimeoutMillis;
    private final int readTimeoutMillis;
    private final int keepAliveIntervalSeconds;
    private final long framePullIntervalMillis;
    private final boolean autoReconnect;

    public RtspEndpoint(String streamId,
                        String rtspUrl,
                        String username,
                        String password,
                        int connectTimeoutMillis,
                        int readTimeoutMillis,
                        int keepAliveIntervalSeconds,
                        long framePullIntervalMillis,
                        boolean autoReconnect) {
        this.streamId = streamId;
        this.rtspUrl = rtspUrl;
        this.username = username;
        this.password = password;
        this.connectTimeoutMillis = connectTimeoutMillis;
        this.readTimeoutMillis = readTimeoutMillis;
        this.keepAliveIntervalSeconds = keepAliveIntervalSeconds;
        this.framePullIntervalMillis = framePullIntervalMillis;
        this.autoReconnect = autoReconnect;
    }

    public String getStreamId() {
        return streamId;
    }

    public String getRtspUrl() {
        return rtspUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public int getReadTimeoutMillis() {
        return readTimeoutMillis;
    }

    public int getKeepAliveIntervalSeconds() {
        return keepAliveIntervalSeconds;
    }

    public long getFramePullIntervalMillis() {
        return framePullIntervalMillis;
    }

    public boolean isAutoReconnect() {
        return autoReconnect;
    }
}
