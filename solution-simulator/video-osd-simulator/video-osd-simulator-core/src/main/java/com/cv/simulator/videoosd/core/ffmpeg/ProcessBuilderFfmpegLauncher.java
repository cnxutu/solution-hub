package com.cv.simulator.videoosd.core.ffmpeg;

import java.io.IOException;
import java.util.List;

public class ProcessBuilderFfmpegLauncher implements FfmpegProcessLauncher {

    @Override
    public FfmpegProcessHandle launch(List<String> command) {
        try {
            Process process = new ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .start();
            return new JavaProcessHandle(process);
        } catch (IOException e) {
            String executable = command == null || command.isEmpty() ? "unknown" : command.get(0);
            throw new IllegalStateException("failed to start stream process: " + executable, e);
        }
    }

    private static class JavaProcessHandle implements FfmpegProcessHandle {
        private final Process process;

        private JavaProcessHandle(Process process) {
            this.process = process;
        }

        @Override
        public boolean isAlive() {
            return process.isAlive();
        }

        @Override
        public void destroy() {
            process.destroy();
        }
    }
}
