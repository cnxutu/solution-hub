package com.cv.simulator.videoosd.core.ffmpeg;

import com.cv.simulator.videoosd.core.enums.StreamProtocol;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FfmpegCommandRequest {

    private final String ffmpegPath;
    private final Path inputFile;
    private final StreamProtocol protocol;
    private final String zlmHost;
    private final int zlmPort;
    private final String app;
    private final String stream;
    private final boolean loop;
    private final List<String> inputOptions;

    public FfmpegCommandRequest(String ffmpegPath,
                                Path inputFile,
                                StreamProtocol protocol,
                                String zlmHost,
                                int zlmPort,
                                String app,
                                String stream,
                                boolean loop,
                                List<String> inputOptions) {
        this.ffmpegPath = ffmpegPath;
        this.inputFile = inputFile;
        this.protocol = protocol;
        this.zlmHost = zlmHost;
        this.zlmPort = zlmPort;
        this.app = app;
        this.stream = stream;
        this.loop = loop;
        this.inputOptions = inputOptions == null ? Collections.emptyList() : new ArrayList<>(inputOptions);
    }

    public String getFfmpegPath() {
        return ffmpegPath;
    }

    public Path getInputFile() {
        return inputFile;
    }

    public StreamProtocol getProtocol() {
        return protocol;
    }

    public String getZlmHost() {
        return zlmHost;
    }

    public int getZlmPort() {
        return zlmPort;
    }

    public String getApp() {
        return app;
    }

    public String getStream() {
        return stream;
    }

    public boolean isLoop() {
        return loop;
    }

    public List<String> getInputOptions() {
        return Collections.unmodifiableList(inputOptions);
    }
}
