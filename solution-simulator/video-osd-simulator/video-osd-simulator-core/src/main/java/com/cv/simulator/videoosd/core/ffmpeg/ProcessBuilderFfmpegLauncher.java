package com.cv.simulator.videoosd.core.ffmpeg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ProcessBuilderFfmpegLauncher implements FfmpegProcessLauncher {

    private static final Logger log = LoggerFactory.getLogger(ProcessBuilderFfmpegLauncher.class);

    @Override
    public FfmpegProcessHandle launch(List<String> command) {
        try {
            log.info("starting stream process: {}", String.join(" ", command));
            Process process = new ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .start();
            log.info("stream process started: pid={}", process.pid());
            startOutputReader(process);
            return new JavaProcessHandle(process);
        } catch (IOException e) {
            String executable = command == null || command.isEmpty() ? "unknown" : command.get(0);
            throw new IllegalStateException("failed to start stream process: " + executable, e);
        }
    }

    private void startOutputReader(Process process) {
        Thread reader = new Thread(() -> {
            try (BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    log.info("stream process[pid={}] {}", process.pid(), line);
                }
            } catch (IOException e) {
                log.warn("failed to read stream process output: pid={}", process.pid(), e);
            }
        }, "stream-process-log-" + process.pid());
        reader.setDaemon(true);
        reader.start();
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
            log.info("destroying stream process: pid={}", process.pid());
            process.destroy();
        }
    }
}
