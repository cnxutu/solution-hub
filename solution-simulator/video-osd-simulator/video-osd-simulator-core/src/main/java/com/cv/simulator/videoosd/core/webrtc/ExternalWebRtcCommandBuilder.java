package com.cv.simulator.videoosd.core.webrtc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExternalWebRtcCommandBuilder {

    private static final Pattern TOKEN_PATTERN = Pattern.compile("\"([^\"]*)\"|'([^']*)'|(\\S+)");

    public List<String> build(ExternalWebRtcCommandRequest request) {
        if (request.getCommandTemplate() == null || request.getCommandTemplate().trim().isEmpty()) {
            throw new IllegalArgumentException("webrtc push command template must not be blank");
        }
        String resolved = request.getCommandTemplate()
                .replace("{videoFile}", request.getVideoFile().toString())
                .replace("{zlmHost}", request.getZlmHost())
                .replace("{app}", trimSlash(request.getApp()))
                .replace("{stream}", trimSlash(request.getStream()));
        List<String> command = new ArrayList<>();
        Matcher matcher = TOKEN_PATTERN.matcher(resolved);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                command.add(matcher.group(1));
            } else if (matcher.group(2) != null) {
                command.add(matcher.group(2));
            } else {
                command.add(matcher.group(3));
            }
        }
        if (command.isEmpty()) {
            throw new IllegalArgumentException("webrtc push command template must resolve to a command");
        }
        return command;
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
