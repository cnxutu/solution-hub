package com.cv.simulator.videoosd.core.ffmpeg;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FfmpegPathResolver {

    public String resolve(String configuredPath) {
        if (configuredPath == null || configuredPath.trim().isEmpty()) {
            return "ffmpeg";
        }
        Path path = Paths.get(configuredPath);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("ffmpeg executable does not exist: " + path);
        }
        return path.toString();
    }
}
