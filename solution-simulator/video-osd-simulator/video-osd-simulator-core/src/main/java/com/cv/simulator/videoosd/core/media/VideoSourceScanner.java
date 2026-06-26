package com.cv.simulator.videoosd.core.media;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class VideoSourceScanner {

    public List<Path> scan(Path directory, String patternExpression) {
        if (directory == null || !Files.isDirectory(directory)) {
            return new ArrayList<>();
        }
        Set<Path> result = new LinkedHashSet<>();
        for (String pattern : splitPatterns(patternExpression)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, pattern)) {
                for (Path path : stream) {
                    if (Files.isRegularFile(path)) {
                        result.add(path);
                    }
                }
            } catch (IOException e) {
                throw new IllegalStateException("failed to scan video directory: " + directory, e);
            }
        }
        List<Path> paths = new ArrayList<>(result);
        paths.sort(Comparator.comparing(path -> path.getFileName().toString()));
        return paths;
    }

    private List<String> splitPatterns(String patternExpression) {
        String source = patternExpression == null || patternExpression.trim().isEmpty()
                ? "*.mp4,*.flv,*.mov,*.mkv"
                : patternExpression;
        List<String> patterns = new ArrayList<>();
        for (String pattern : source.split(",")) {
            String trimmed = pattern.trim();
            if (!trimmed.isEmpty()) {
                patterns.add(trimmed);
            }
        }
        return patterns;
    }
}
