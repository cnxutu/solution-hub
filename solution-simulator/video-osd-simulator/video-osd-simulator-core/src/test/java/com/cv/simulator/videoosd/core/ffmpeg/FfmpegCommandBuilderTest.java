package com.cv.simulator.videoosd.core.ffmpeg;

import com.cv.simulator.videoosd.core.enums.StreamProtocol;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FfmpegCommandBuilderTest {

    private final FfmpegCommandBuilder builder = new FfmpegCommandBuilder();

    @Test
    void buildsRtmpCommandForZlm() {
        FfmpegCommandRequest request = new FfmpegCommandRequest(
                "ffmpeg",
                Path.of("D:/video dir/demo.mp4"),
                StreamProtocol.RTMP,
                "127.0.0.1",
                1935,
                "live",
                "drone001",
                true,
                List.of("-re")
        );

        List<String> command = builder.build(request);

        assertEquals(List.of(
                "ffmpeg", "-re", "-stream_loop", "-1",
                "-i", "D:\\video dir\\demo.mp4",
                "-c", "copy",
                "-f", "flv",
                "rtmp://127.0.0.1:1935/live/drone001"
        ), command);
    }

    @Test
    void buildsRtspCommandForZlm() {
        FfmpegCommandRequest request = new FfmpegCommandRequest(
                "ffmpeg",
                Path.of("D:/videos/demo.mp4"),
                StreamProtocol.RTSP,
                "127.0.0.1",
                554,
                "live",
                "drone001",
                false,
                List.of()
        );

        List<String> command = builder.build(request);

        assertEquals(List.of(
                "ffmpeg", "-re",
                "-i", "D:\\videos\\demo.mp4",
                "-c", "copy",
                "-f", "rtsp",
                "-rtsp_transport", "tcp",
                "rtsp://127.0.0.1:554/live/drone001"
        ), command);
    }

    @Test
    void rejectsUnsupportedWebrtcCommand() {
        FfmpegCommandRequest request = new FfmpegCommandRequest(
                "ffmpeg",
                Path.of("D:/videos/demo.mp4"),
                StreamProtocol.WEBRTC,
                "127.0.0.1",
                8000,
                "live",
                "drone001",
                false,
                List.of()
        );

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> builder.build(request));

        assertEquals("WEBRTC is reserved but not supported by the ffmpeg command builder", exception.getMessage());
    }
}
