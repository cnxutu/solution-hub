package com.cv.simulator.videoosd.sample.service;

import com.cv.boot.mybatisplus.pojo.vo.PageInfoVO;
import com.cv.simulator.videoosd.core.ffmpeg.FfmpegCommandBuilder;
import com.cv.simulator.videoosd.core.ffmpeg.FfmpegPathResolver;
import com.cv.simulator.videoosd.core.media.VideoSourceScanner;
import com.cv.simulator.videoosd.core.runtime.StreamTaskRuntime;
import com.cv.simulator.videoosd.core.webrtc.ExternalWebRtcCommandBuilder;
import com.cv.simulator.videoosd.sample.config.OsdSourceType;
import com.cv.simulator.videoosd.sample.config.SimulatorProperties;
import com.cv.simulator.videoosd.sample.pojo.entity.StreamTaskEntity;
import com.cv.simulator.videoosd.sample.pojo.query.DeleteIdsQuery;
import com.cv.simulator.videoosd.sample.pojo.query.StreamTaskPageQuery;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class StreamTaskServiceTest {

    @Test
    void mqttSenderUsesConfiguredDelayAndScriptCommand() {
        StreamTaskRuntime runtime = mock(StreamTaskRuntime.class);
        SimulatorProperties properties = new SimulatorProperties();
        properties.getOsd().setSourceType(OsdSourceType.MQTT);
        properties.getOsd().getMqtt().setSenderStartDelayMillis(250L);
        properties.getOsd().getMqtt().setSenderScriptPath("D:\\drc-osd-collector\\回放.bat");
        RecordingStreamTaskService service = new RecordingStreamTaskService(properties, runtime);
        StreamTaskEntity task = new StreamTaskEntity();
        task.setId(8L);

        service.startMqttSender(task);

        assertEquals(250L, service.lastPausedMillis);
        verify(runtime).startSidecar(eq(8L),
                eq(List.of("cmd.exe", "/c", "D:\\drc-osd-collector\\回放.bat")),
                eq("mqtt-osd-sender"));
    }

    @Test
    void mqttSenderDoesNothingWhenSourceTypeIsNotMqtt() {
        StreamTaskRuntime runtime = mock(StreamTaskRuntime.class);
        SimulatorProperties properties = new SimulatorProperties();
        properties.getOsd().setSourceType(OsdSourceType.MYSQL);
        RecordingStreamTaskService service = new RecordingStreamTaskService(properties, runtime);
        StreamTaskEntity task = new StreamTaskEntity();
        task.setId(9L);

        service.startMqttSender(task);

        verify(runtime, never()).startSidecar(eq(9L), eq(List.of()), eq("mqtt-osd-sender"));
        assertEquals(-1L, service.lastPausedMillis);
    }

    private static final class RecordingStreamTaskService extends StreamTaskService {
        private long lastPausedMillis = -1L;

        private RecordingStreamTaskService(SimulatorProperties properties, StreamTaskRuntime runtime) {
            super(properties,
                    mock(FfmpegPathResolver.class),
                    mock(VideoSourceScanner.class),
                    mock(FfmpegCommandBuilder.class),
                    mock(ExternalWebRtcCommandBuilder.class),
                    runtime,
                    mock(TaskRunLogService.class));
        }

        @Override
        void pause(long millis) {
            lastPausedMillis = millis;
        }

        @Override
        public PageInfoVO<StreamTaskEntity> pageList(StreamTaskPageQuery query) {
            return null;
        }

        @Override
        public Long add(StreamTaskEntity entity) {
            return null;
        }

        @Override
        public Long edit(StreamTaskEntity entity) {
            return null;
        }

        @Override
        public void delete(DeleteIdsQuery query) {
        }
    }
}
