package com.cv.rtsp.core.model;

import java.util.concurrent.atomic.AtomicInteger;

public class RtspPullSession {

    private final String sessionId;
    private final RtspEndpoint endpoint;
    private final RtspSessionMetrics metrics = new RtspSessionMetrics();
    private final AtomicInteger cSeq = new AtomicInteger(0);
    private volatile RtspSessionState state = RtspSessionState.CREATED;
    private volatile String remoteSessionId;
    private volatile RtspDescribeResult describeResult;
    private volatile long startedAt;
    private volatile long lastKeepAliveAt;
    private volatile long lastFrameAt;
    private volatile String lastError;

    public RtspPullSession(String sessionId, RtspEndpoint endpoint) {
        this.sessionId = sessionId;
        this.endpoint = endpoint;
    }

    public int nextCSeq() {
        return cSeq.incrementAndGet();
    }

    public String getSessionId() {
        return sessionId;
    }

    public RtspEndpoint getEndpoint() {
        return endpoint;
    }

    public RtspSessionMetrics getMetrics() {
        return metrics;
    }

    public RtspSessionState getState() {
        return state;
    }

    public void setState(RtspSessionState state) {
        this.state = state;
    }

    public String getRemoteSessionId() {
        return remoteSessionId;
    }

    public void setRemoteSessionId(String remoteSessionId) {
        this.remoteSessionId = remoteSessionId;
    }

    public RtspDescribeResult getDescribeResult() {
        return describeResult;
    }

    public void setDescribeResult(RtspDescribeResult describeResult) {
        this.describeResult = describeResult;
    }

    public long getStartedAt() {
        return startedAt;
    }

    public void markStarted() {
        this.startedAt = System.currentTimeMillis();
    }

    public long getLastKeepAliveAt() {
        return lastKeepAliveAt;
    }

    public void markKeepAlive() {
        this.lastKeepAliveAt = System.currentTimeMillis();
    }

    public long getLastFrameAt() {
        return lastFrameAt;
    }

    public void markFrame() {
        this.lastFrameAt = System.currentTimeMillis();
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }
}
