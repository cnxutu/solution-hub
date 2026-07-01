package com.cv.simulator.videoosd.sample.controller;

import com.cv.simulator.videoosd.core.enums.TaskStatus;
import com.cv.simulator.videoosd.core.runtime.StreamTaskSnapshot;
import com.cv.simulator.videoosd.sample.service.DeviceTelemetryService;
import com.cv.simulator.videoosd.sample.service.StreamTaskService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

class StreamTaskControllerTest {

    @Test
    void stopAlsoStopsMqttReplay() {
        StreamTaskService streamTaskService = mock(StreamTaskService.class);
        DeviceTelemetryService telemetryService = mock(DeviceTelemetryService.class);
        StreamTaskSnapshot snapshot = new StreamTaskSnapshot(7L, TaskStatus.STOPPED, LocalDateTime.now(), "stopped");
        when(streamTaskService.stop(7L)).thenReturn(snapshot);
        StreamTaskController controller = new StreamTaskController(streamTaskService, telemetryService);

        StreamTaskSnapshot result = controller.stop(7L).getData();

        assertEquals(snapshot, result);
        verify(streamTaskService).stop(7L);
        verify(telemetryService).stopReplay(7L);
    }
}
