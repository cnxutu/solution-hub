package com.cv.simulator.videoosd.core.ffmpeg;

import com.cv.simulator.videoosd.core.enums.StreamProtocol;

import java.util.ArrayList;
import java.util.List;

public class FfmpegCommandBuilder {

    public List<String> build(FfmpegCommandRequest request) {
        if (request.getProtocol() == StreamProtocol.WEBRTC) {
            throw new IllegalArgumentException("WEBRTC is reserved but not supported by the ffmpeg command builder");
        }
        List<String> command = new ArrayList<>();
        command.add(request.getFfmpegPath());
        if (request.getInputOptions().isEmpty()) {
            command.add("-re");
        } else {
            command.addAll(request.getInputOptions());
        }
        if (request.isLoop()) {
            command.add("-stream_loop");
            command.add("-1");
        }
        command.add("-i");
        command.add(request.getInputFile().toString());
        command.add("-c");
        command.add("copy");
        if (request.getProtocol() == StreamProtocol.RTMP) {
            command.add("-f");
            command.add("flv");
            command.add(buildTargetUrl("rtmp", request));
            return command;
        }
        command.add("-f");
        command.add("rtsp");
        command.add("-rtsp_transport");
        command.add("tcp");
        command.add(buildTargetUrl("rtsp", request));
        return command;
    }

    private String buildTargetUrl(String scheme, FfmpegCommandRequest request) {
        return scheme + "://" + request.getZlmHost() + ":" + request.getZlmPort()
                + "/" + trimSlash(request.getApp()) + "/" + trimSlash(request.getStream());
    }

    private String trimSlash(String value) {
        if (value == null) {
            return "";
        }
        String result = value;
        while (result.startsWith("/")) {
            result = result.substring(1);
        }
        while (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
