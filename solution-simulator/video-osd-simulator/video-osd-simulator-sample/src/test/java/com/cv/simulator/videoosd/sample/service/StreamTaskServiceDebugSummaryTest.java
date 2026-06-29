package com.cv.simulator.videoosd.sample.service;

import com.cv.simulator.videoosd.core.enums.StreamProtocol;
import com.cv.simulator.videoosd.sample.pojo.entity.StreamTaskEntity;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StreamTaskServiceDebugSummaryTest {

    @Test
    void buildsDebugSummaryContainingPublishAndPlayAddresses() {
        StreamTaskEntity task = new StreamTaskEntity();
        task.setId(7L);
        task.setTaskName("demo");
        task.setProtocol(StreamProtocol.RTMP.name());
        task.setVideoFilePath("C:/videos/demo.mp4");
        task.setZlmHost("127.0.0.1");
        task.setZlmPort(7935);
        task.setApp("live");
        task.setStream("drone001");

        String summary = StreamTaskService.buildDebugSummary(
                task,
                Path.of("C:/videos/demo.mp4"),
                List.of("ffmpeg", "-re", "-stream_loop", "-1"),
                "ws://127.0.0.1:18083/ws/osd",
                8086
        );

        assertTrue(summary.contains("taskId=7"));
        assertTrue(summary.contains("protocol=RTMP"));
        assertTrue(summary.contains("videoFile=C:\\videos\\demo.mp4") || summary.contains("videoFile=C:/videos/demo.mp4"));
        assertTrue(summary.contains("publishUrl=rtmp://127.0.0.1:7935/live/drone001"));
        assertTrue(summary.contains("playUrl=http://127.0.0.1:8086/webrtc/index.html?app=live&stream=drone001&type=play"));
        assertTrue(summary.contains("osdWebSocket=ws://127.0.0.1:18083/ws/osd"));
    }
}
