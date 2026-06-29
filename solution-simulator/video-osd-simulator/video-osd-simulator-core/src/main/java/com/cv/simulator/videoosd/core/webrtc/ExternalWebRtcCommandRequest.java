package com.cv.simulator.videoosd.core.webrtc;

import java.nio.file.Path;

public class ExternalWebRtcCommandRequest {

    private final String commandTemplate;
    private final Path videoFile;
    private final String zlmHost;
    private final String app;
    private final String stream;

    public ExternalWebRtcCommandRequest(String commandTemplate, Path videoFile, String zlmHost, String app, String stream) {
        this.commandTemplate = commandTemplate;
        this.videoFile = videoFile;
        this.zlmHost = zlmHost;
        this.app = app;
        this.stream = stream;
    }

    public String getCommandTemplate() {
        return commandTemplate;
    }

    public Path getVideoFile() {
        return videoFile;
    }

    public String getZlmHost() {
        return zlmHost;
    }

    public String getApp() {
        return app;
    }

    public String getStream() {
        return stream;
    }
}
