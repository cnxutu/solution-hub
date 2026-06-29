package com.cv.simulator.videoosd.core.ffmpeg;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProcessBuilderFfmpegLauncherTest {

    @Test
    void includesExecutableNameWhenProcessCannotStart() {
        ProcessBuilderFfmpegLauncher launcher = new ProcessBuilderFfmpegLauncher();

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> launcher.launch(List.of("missing-webrtc-pusher-for-test", "--version")));

        assertEquals("failed to start stream process: missing-webrtc-pusher-for-test", exception.getMessage());
        assertTrue(exception.getCause() instanceof java.io.IOException);
    }
}
