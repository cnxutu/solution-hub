package com.cv.simulator.videoosd.core.webrtc;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExternalWebRtcCommandBuilderTest {

    @Test
    void rendersTemplateWithTaskArguments() {
        ExternalWebRtcCommandBuilder builder = new ExternalWebRtcCommandBuilder();

        List<String> command = builder.build(new ExternalWebRtcCommandRequest(
                "webrtc-pusher --input \"{videoFile}\" --host {zlmHost} --app {app} --stream {stream}",
                Path.of("D:/video samples/demo clip.mp4"),
                "127.0.0.1",
                "live",
                "drone001"
        ));

        assertEquals(List.of(
                "webrtc-pusher",
                "--input",
                Path.of("D:/video samples/demo clip.mp4").toString(),
                "--host",
                "127.0.0.1",
                "--app",
                "live",
                "--stream",
                "drone001"
        ), command);
    }

    @Test
    void rejectsMissingTemplate() {
        ExternalWebRtcCommandBuilder builder = new ExternalWebRtcCommandBuilder();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> builder.build(new ExternalWebRtcCommandRequest(" ", Path.of("demo.mp4"), "127.0.0.1", "live", "drone001")));

        assertEquals("webrtc push command template must not be blank", exception.getMessage());
    }
}
