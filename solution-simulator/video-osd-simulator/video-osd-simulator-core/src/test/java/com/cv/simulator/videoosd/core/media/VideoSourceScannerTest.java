package com.cv.simulator.videoosd.core.media;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VideoSourceScannerTest {

    @TempDir
    Path tempDir;

    @Test
    void scansVideoFilesByPatternInNameOrder() throws Exception {
        Path beta = Files.createFile(tempDir.resolve("beta.mp4"));
        Files.createFile(tempDir.resolve("note.txt"));
        Path alpha = Files.createFile(tempDir.resolve("alpha.flv"));

        List<Path> paths = new VideoSourceScanner().scan(tempDir, "*.mp4,*.flv");

        assertEquals(List.of(alpha, beta), paths);
    }

    @Test
    void returnsEmptyListForEmptyDirectory() throws Exception {
        assertEquals(List.of(), new VideoSourceScanner().scan(tempDir, "*.mp4"));
    }
}
