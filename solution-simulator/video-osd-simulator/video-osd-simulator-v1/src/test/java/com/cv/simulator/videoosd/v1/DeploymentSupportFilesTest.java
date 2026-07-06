package com.cv.simulator.videoosd.v1;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DeploymentSupportFilesTest {

    @Test
    void startupScriptSupportsPidFileBackgroundStartupAndLogHints() throws IOException {
        Path scriptPath = Paths.get("startup.sh");
        String content = new String(Files.readAllBytes(scriptPath), StandardCharsets.UTF_8);

        assertTrue(content.contains("PID_FILE="));
        assertTrue(content.contains("LOG_DIR="));
        assertTrue(content.contains("ARCHIVE_DIR_PATTERN="));
        assertTrue(content.contains("mkdir -p \"$LOG_DIR\""));
        assertTrue(content.contains("nohup \"$JAVA_CMD\""));
        assertTrue(content.contains("echo \"$APP_PID\" > \"$PID_FILE\""));
        assertTrue(content.contains("Tail logs:"));
        assertTrue(content.contains("Show process:"));
        assertTrue(content.contains("Stop process:"));
        assertTrue(content.contains("\\$(cat \\\"$PID_FILE\\\")"));
        assertTrue(content.contains("-Dapp.log.dir=$LOG_DIR"));
        assertTrue(content.contains("-Dapp.name=$APP_NAME"));
    }

    @Test
    void logbackConfigWritesActiveLogsAndDateArchives() throws IOException {
        String content = readClasspathResource("/logback-spring.xml");

        assertTrue(content.contains("${app.log.dir:-logs}"));
        assertTrue(content.contains("video-osd-simulator-v1.log"));
        assertTrue(content.contains("/%d{yyyyMMdd}/"));
        assertTrue(content.contains("%i.log.gz"));
        assertTrue(content.contains("200MB"));
        assertTrue(content.contains("SizeAndTimeBasedRollingPolicy"));
    }

    private static String readClasspathResource(String path) throws IOException {
        InputStream inputStream = DeploymentSupportFilesTest.class.getResourceAsStream(path);
        if (inputStream == null) {
            throw new IOException("Resource not found: " + path);
        }
        byte[] bytes = new byte[inputStream.available()];
        int ignored = inputStream.read(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
