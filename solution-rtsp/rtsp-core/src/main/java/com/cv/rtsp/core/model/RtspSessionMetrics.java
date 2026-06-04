package com.cv.rtsp.core.model;

public class RtspSessionMetrics {

    private long optionsCount;
    private long describeCount;
    private long setupCount;
    private long playCount;
    private long keepAliveCount;
    private long teardownCount;
    private long frameCount;
    private long reconnectCount;
    private long errorCount;

    public synchronized void incrementOptions() {
        optionsCount++;
    }

    public synchronized void incrementDescribe() {
        describeCount++;
    }

    public synchronized void incrementSetup() {
        setupCount++;
    }

    public synchronized void incrementPlay() {
        playCount++;
    }

    public synchronized void incrementKeepAlive() {
        keepAliveCount++;
    }

    public synchronized void incrementTeardown() {
        teardownCount++;
    }

    public synchronized void incrementFrame() {
        frameCount++;
    }

    public synchronized void incrementReconnect() {
        reconnectCount++;
    }

    public synchronized void incrementError() {
        errorCount++;
    }

    public synchronized RtspSessionMetrics copy() {
        RtspSessionMetrics copy = new RtspSessionMetrics();
        copy.optionsCount = optionsCount;
        copy.describeCount = describeCount;
        copy.setupCount = setupCount;
        copy.playCount = playCount;
        copy.keepAliveCount = keepAliveCount;
        copy.teardownCount = teardownCount;
        copy.frameCount = frameCount;
        copy.reconnectCount = reconnectCount;
        copy.errorCount = errorCount;
        return copy;
    }

    public synchronized long getOptionsCount() {
        return optionsCount;
    }

    public synchronized long getDescribeCount() {
        return describeCount;
    }

    public synchronized long getSetupCount() {
        return setupCount;
    }

    public synchronized long getPlayCount() {
        return playCount;
    }

    public synchronized long getKeepAliveCount() {
        return keepAliveCount;
    }

    public synchronized long getTeardownCount() {
        return teardownCount;
    }

    public synchronized long getFrameCount() {
        return frameCount;
    }

    public synchronized long getReconnectCount() {
        return reconnectCount;
    }

    public synchronized long getErrorCount() {
        return errorCount;
    }
}
