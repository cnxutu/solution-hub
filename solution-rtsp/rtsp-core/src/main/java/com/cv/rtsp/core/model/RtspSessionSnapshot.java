package com.cv.rtsp.core.model;

public class RtspSessionSnapshot {

    private final String streamId;
    private final String sessionId;
    private final String rtspUrl;
    private final RtspSessionState state;
    private final String remoteSessionId;
    private final long startedAt;
    private final long lastKeepAliveAt;
    private final long lastFrameAt;
    private final String lastError;
    private final RtspSessionMetrics metrics;

    public RtspSessionSnapshot(String streamId,
                               String sessionId,
                               String rtspUrl,
                               RtspSessionState state,
                               String remoteSessionId,
                               long startedAt,
                               long lastKeepAliveAt,
                               long lastFrameAt,
                               String lastError,
                               RtspSessionMetrics metrics) {
        this.streamId = streamId;
        this.sessionId = sessionId;
        this.rtspUrl = rtspUrl;
        this.state = state;
        this.remoteSessionId = remoteSessionId;
        this.startedAt = startedAt;
        this.lastKeepAliveAt = lastKeepAliveAt;
        this.lastFrameAt = lastFrameAt;
        this.lastError = lastError;
        this.metrics = metrics;
    }

    public static RtspSessionSnapshot from(RtspPullSession session) {
        return new RtspSessionSnapshot(
                session.getEndpoint().getStreamId(),
                session.getSessionId(),
                session.getEndpoint().getRtspUrl(),
                session.getState(),
                session.getRemoteSessionId(),
                session.getStartedAt(),
                session.getLastKeepAliveAt(),
                session.getLastFrameAt(),
                session.getLastError(),
                session.getMetrics().copy()
        );
    }

    public String getStreamId() {
        return streamId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getRtspUrl() {
        return rtspUrl;
    }

    public RtspSessionState getState() {
        return state;
    }

    public String getRemoteSessionId() {
        return remoteSessionId;
    }

    public long getStartedAt() {
        return startedAt;
    }

    public long getLastKeepAliveAt() {
        return lastKeepAliveAt;
    }

    public long getLastFrameAt() {
        return lastFrameAt;
    }

    public String getLastError() {
        return lastError;
    }

    public RtspSessionMetrics getMetrics() {
        return metrics;
    }
}
