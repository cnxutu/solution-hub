package com.cv.simulator.videoosd.sample.service;

import com.cv.simulator.videoosd.core.osd.OsdExcelImporter;
import com.cv.simulator.videoosd.core.osd.OsdFrameGeometryCalculator;
import com.cv.simulator.videoosd.core.osd.OsdPayloadSupport;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.cv.simulator.videoosd.sample.config.OsdBroadcastWebSocketHandler;
import com.cv.simulator.videoosd.sample.config.OsdSourceType;
import com.cv.simulator.videoosd.sample.config.SimulatorProperties;
import com.cv.simulator.videoosd.sample.mapper.DeviceTelemetryMapper;
import com.cv.simulator.videoosd.sample.pojo.entity.DeviceTelemetryEntity;
import com.cv.simulator.videoosd.sample.pojo.entity.StreamTaskEntity;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeviceTelemetryServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void mysqlReplayBroadcastsCalculatedFrameGeometry() {
        DeviceTelemetryMapper mapper = mock(DeviceTelemetryMapper.class);
        OsdBroadcastWebSocketHandler webSocketHandler = mock(OsdBroadcastWebSocketHandler.class);
        TaskRunLogService runLogService = mock(TaskRunLogService.class);
        StaticJsonOsdPayloadLoader staticJsonLoader = mock(StaticJsonOsdPayloadLoader.class);
        SimulatorProperties properties = mysqlProperties();
        DeviceTelemetryService service = new DeviceTelemetryService(
                new OsdExcelImporter(),
                new OsdPayloadSupport(),
                new OsdFrameGeometryCalculator(),
                webSocketHandler,
                properties,
                runLogService,
                new PublishTimeReplayExecutor(),
                staticJsonLoader,
                new FixedIntervalReplayExecutor()
        );
        ReflectionTestUtils.setField(service, "baseMapper", mapper);
        when(mapper.selectList(any())).thenReturn(List.of(mysqlRow()));

        StreamTaskEntity task = new StreamTaskEntity();
        task.setId(12L);
        task.setOsdPublishTimeStart(LocalDateTime.of(2026, 7, 1, 10, 13, 20));
        task.setOsdPublishTimeEnd(LocalDateTime.of(2026, 7, 1, 10, 13, 40));

        List<String> payloads = new ArrayList<>();
        service = new DeviceTelemetryService(
                new OsdExcelImporter(),
                new OsdPayloadSupport(),
                new OsdFrameGeometryCalculator(),
                new CapturingBroadcastHandler(payloads),
                properties,
                runLogService,
                new PublishTimeReplayExecutor(),
                staticJsonLoader,
                new FixedIntervalReplayExecutor()
        );
        ReflectionTestUtils.setField(service, "baseMapper", mapper);

        service.replayByTask(task);

        verify(mapper).selectList(any());
        assertEquals(1, payloads.size());
        JsonNode payload = readJson(payloads.get(0));
        assertEquals("drone-001", payload.path("device_sn").asText());
        assertEquals(30.1856666496194D, payload.path("frame_center").path("lat").asDouble());
        assertEquals(120.1979761985018D, payload.path("frame_center").path("lon").asDouble());
        assertEquals(4, payload.path("corners").size());
        assertTrue(payload.path("corners").get(0).has("lat"));
        assertTrue(payload.path("corners").get(0).has("lon"));
    }

    @Test
    void staticJsonReplayDoesNotQueryMysqlRows() {
        DeviceTelemetryMapper mapper = mock(DeviceTelemetryMapper.class);
        StaticJsonOsdPayloadLoader staticJsonLoader = mock(StaticJsonOsdPayloadLoader.class);
        TaskRunLogService runLogService = mock(TaskRunLogService.class);
        SimulatorProperties properties = new SimulatorProperties();
        properties.getOsd().setSourceType(OsdSourceType.STATIC_JSON);
        when(staticJsonLoader.load("classpath:/static/test-osd.json")).thenReturn(List.of("{\"device_sn\":\"json-1\"}"));
        properties.getOsd().setStaticJsonLocation("classpath:/static/test-osd.json");
        List<String> payloads = new ArrayList<>();
        DeviceTelemetryService service = new DeviceTelemetryService(
                new OsdExcelImporter(),
                new OsdPayloadSupport(),
                new OsdFrameGeometryCalculator(),
                new CapturingBroadcastHandler(payloads),
                properties,
                runLogService,
                new PublishTimeReplayExecutor(),
                staticJsonLoader,
                new FixedIntervalReplayExecutor()
        );
        ReflectionTestUtils.setField(service, "baseMapper", mapper);

        StreamTaskEntity task = new StreamTaskEntity();
        task.setId(7L);
        service.replayByTask(task);

        verify(mapper, never()).selectList(any());
        assertEquals(List.of("{\"device_sn\":\"json-1\"}"), payloads);
    }

    private SimulatorProperties mysqlProperties() {
        SimulatorProperties properties = new SimulatorProperties();
        properties.getOsd().setSourceType(OsdSourceType.MYSQL);
        properties.getOsd().setRequireTaskIdMatch(true);
        properties.getOsd().setFrameHfovDeg(60.0);
        properties.getOsd().setFrameVfovDeg(40.0);
        return properties;
    }

    private DeviceTelemetryEntity mysqlRow() {
        DeviceTelemetryEntity entity = new DeviceTelemetryEntity();
        entity.setId(1L);
        entity.setTaskId(12L);
        entity.setDeviceSn("drone-001");
        entity.setAttitudeHead(90F);
        entity.setHeight(40.7109F);
        entity.setLatitude(new BigDecimal("30.1856666496194"));
        entity.setLongitude(new BigDecimal("120.1979761985018"));
        entity.setModeCode(3);
        entity.setTrackId("track-1");
        entity.setPublishTime(LocalDateTime.of(2026, 7, 1, 10, 13, 25));
        return entity;
    }

    private JsonNode readJson(String value) {
        try {
            return objectMapper.readTree(value);
        } catch (Exception e) {
            throw new AssertionError("payload should be valid json", e);
        }
    }

    private static class CapturingBroadcastHandler extends OsdBroadcastWebSocketHandler {
        private final List<String> payloads;

        private CapturingBroadcastHandler(List<String> payloads) {
            this.payloads = payloads;
        }

        @Override
        public void broadcast(String payload) {
            payloads.add(payload);
        }
    }
}
