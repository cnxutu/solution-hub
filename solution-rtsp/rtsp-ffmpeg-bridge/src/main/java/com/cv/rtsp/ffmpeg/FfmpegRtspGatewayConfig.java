package com.cv.rtsp.ffmpeg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FfmpegRtspGatewayConfig {

    private final String ffmpegBinary;
    private final String rtspTransport;
    private final int logTailSize;
    private final List<String> extraArgs;

    public FfmpegRtspGatewayConfig(String ffmpegBinary, String rtspTransport, int logTailSize, List<String> extraArgs) {
        this.ffmpegBinary = ffmpegBinary;
        this.rtspTransport = rtspTransport;
        this.logTailSize = logTailSize;
        this.extraArgs = new ArrayList<String>(extraArgs);
    }

    public String getFfmpegBinary() {
        return ffmpegBinary;
    }

    public String getRtspTransport() {
        return rtspTransport;
    }

    public int getLogTailSize() {
        return logTailSize;
    }

    public List<String> getExtraArgs() {
        return Collections.unmodifiableList(extraArgs);
    }
}
