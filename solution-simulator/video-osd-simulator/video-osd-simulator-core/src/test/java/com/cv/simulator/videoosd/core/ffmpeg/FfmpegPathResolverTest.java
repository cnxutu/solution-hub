package com.cv.simulator.videoosd.core.ffmpeg;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FfmpegPathResolverTest {

    @TempDir
    Path tempDir;

    @Test
    void returnsDefaultExecutableWhenPathIsBlank() {
        assertEquals("ffmpeg", new FfmpegPathResolver().resolve(null));
        assertEquals("ffmpeg", new FfmpegPathResolver().resolve("   "));
    }

    @Test
    void returnsExistingCustomPath() throws Exception {
        Path executable = Files.createFile(tempDir.resolve("ffmpeg.exe"));

        assertEquals(executable.toString(), new FfmpegPathResolver().resolve(executable.toString()));
    }

    @Test
    void rejectsMissingCustomPath() {
        Path missing = tempDir.resolve("missing-ffmpeg.exe");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new FfmpegPathResolver().resolve(missing.toString()));

        assertEquals("ffmpeg executable does not exist: " + missing, exception.getMessage());
    }
}
