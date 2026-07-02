package com.cv.simulator.videoosd.sample.service;

import com.cv.simulator.videoosd.core.enums.TaskStatus;
import com.cv.simulator.videoosd.core.runtime.StreamTaskSnapshot;
import com.cv.simulator.videoosd.sample.config.OsdSourceType;
import com.cv.simulator.videoosd.sample.config.SimulatorProperties;
import com.cv.simulator.videoosd.sample.pojo.entity.StreamTaskEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StreamTaskLifecycleServiceTest {

    @Test
    void mqttStartSubscribesBeforeLaunchingSenderScript() {
        StreamTaskService streamTaskService = mock(StreamTaskService.class);
        DeviceTelemetryService telemetryService = mock(DeviceTelemetryService.class);
        SimulatorProperties properties = new SimulatorProperties();
        properties.getOsd().setSourceType(OsdSourceType.MQTT);
        StreamTaskEntity task = new StreamTaskEntity();
        task.setId(1L);
        StreamTaskSnapshot snapshot = new StreamTaskSnapshot(1L, TaskStatus.RUNNING, LocalDateTime.now(), "running");
        when(streamTaskService.start(1L)).thenReturn(snapshot);
        when(streamTaskService.detail(1L)).thenReturn(task);
        StreamTaskLifecycleService service = new StreamTaskLifecycleService(streamTaskService, telemetryService, properties);

        StreamTaskSnapshot result = service.start(1L);

        assertEquals(snapshot, result);
        var order = inOrder(streamTaskService, telemetryService);
        order.verify(streamTaskService).start(1L);
        order.verify(streamTaskService).detail(1L);
        order.verify(telemetryService).startRealtimeMqtt(task);
        order.verify(streamTaskService).startMqttSender(task);
        verify(telemetryService, never()).replayByTask(task);
    }

    @Test
    void mqttDirectConsumeSkipsTaskBoundSubscriptionAndSenderScript() {
        StreamTaskService streamTaskService = mock(StreamTaskService.class);
        DeviceTelemetryService telemetryService = mock(DeviceTelemetryService.class);
        SimulatorProperties properties = new SimulatorProperties();
        properties.getOsd().setSourceType(OsdSourceType.MQTT);
        properties.getOsd().getMqtt().setDirectConsumeEnabled(true);
        StreamTaskSnapshot snapshot = new StreamTaskSnapshot(11L, TaskStatus.RUNNING, LocalDateTime.now(), "running");
        when(streamTaskService.start(11L)).thenReturn(snapshot);
        StreamTaskLifecycleService service = new StreamTaskLifecycleService(streamTaskService, telemetryService, properties);

        StreamTaskSnapshot result = service.start(11L);

        assertEquals(snapshot, result);
        verify(streamTaskService).start(11L);
        verify(streamTaskService, never()).detail(11L);
        verify(telemetryService, never()).startRealtimeMqtt(any());
        verify(streamTaskService, never()).startMqttSender(any());
        verify(telemetryService, never()).replayByTask(any(StreamTaskEntity.class));
    }

    @Test
    void nonMqttStartKeepsExistingReplayFlowWithoutLaunchingSenderScript() {
        StreamTaskService streamTaskService = mock(StreamTaskService.class);
        DeviceTelemetryService telemetryService = mock(DeviceTelemetryService.class);
        SimulatorProperties properties = new SimulatorProperties();
        properties.getOsd().setSourceType(OsdSourceType.STATIC_JSON);
        StreamTaskEntity task = new StreamTaskEntity();
        task.setId(2L);
        StreamTaskSnapshot snapshot = new StreamTaskSnapshot(2L, TaskStatus.RUNNING, LocalDateTime.now(), "running");
        when(streamTaskService.start(2L)).thenReturn(snapshot);
        when(streamTaskService.detail(2L)).thenReturn(task);
        StreamTaskLifecycleService service = new StreamTaskLifecycleService(streamTaskService, telemetryService, properties);

        service.start(2L);

        verify(telemetryService).replayByTask(task);
        verify(streamTaskService, never()).startMqttSender(task);
        verify(telemetryService, never()).startRealtimeMqtt(task);
    }

    @Test
    void directConsumeStopDoesNotCloseAlwaysOnMqttSubscription() {
        StreamTaskService streamTaskService = mock(StreamTaskService.class);
        DeviceTelemetryService telemetryService = mock(DeviceTelemetryService.class);
        SimulatorProperties properties = new SimulatorProperties();
        properties.getOsd().setSourceType(OsdSourceType.MQTT);
        properties.getOsd().getMqtt().setDirectConsumeEnabled(true);
        StreamTaskSnapshot snapshot = new StreamTaskSnapshot(13L, TaskStatus.STOPPED, LocalDateTime.now(), "stopped");
        when(streamTaskService.stop(13L)).thenReturn(snapshot);
        StreamTaskLifecycleService service = new StreamTaskLifecycleService(streamTaskService, telemetryService, properties);

        StreamTaskSnapshot result = service.stop(13L);

        assertEquals(snapshot, result);
        verify(telemetryService, never()).stopReplay(13L);
        verify(streamTaskService).stop(13L);
    }

    @Test
    void stopAlwaysStopsRealtimeReplayBeforeStreamProcess() {
        StreamTaskService streamTaskService = mock(StreamTaskService.class);
        DeviceTelemetryService telemetryService = mock(DeviceTelemetryService.class);
        SimulatorProperties properties = new SimulatorProperties();
        StreamTaskSnapshot snapshot = new StreamTaskSnapshot(3L, TaskStatus.STOPPED, LocalDateTime.now(), "stopped");
        when(streamTaskService.stop(3L)).thenReturn(snapshot);
        StreamTaskLifecycleService service = new StreamTaskLifecycleService(streamTaskService, telemetryService, properties);

        StreamTaskSnapshot result = service.stop(3L);

        assertEquals(snapshot, result);
        var order = inOrder(telemetryService, streamTaskService);
        order.verify(telemetryService).stopReplay(3L);
        order.verify(streamTaskService).stop(3L);
    }
}
