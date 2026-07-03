package com.cv.simulator.videoosd.sample.service;

import com.cv.simulator.videoosd.core.mqtt.MqttOsdProperties;
import com.cv.simulator.videoosd.core.mqtt.MqttOsdMessageHandler;
import com.cv.simulator.videoosd.core.mqtt.MqttOsdRecordMapper;
import com.cv.simulator.videoosd.core.mqtt.MqttOsdSubscriber;
import com.cv.simulator.videoosd.core.mqtt.MqttOsdSubscriberSession;
import com.cv.simulator.videoosd.core.osd.OsdExcelImporter;
import com.cv.simulator.videoosd.core.osd.DeviceTelemetryRecord;
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
import com.cv.simulator.videoosd.sample.pojo.sqlite.SqliteOsdSampleRow;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.test.util.ReflectionTestUtils;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
                new FixedIntervalReplayExecutor(),
                mock(MqttOsdSubscriber.class),
                mock(SqliteOsdSampleLoader.class),
                new SqliteOsdSampleRecordMapper(new OsdFrameGeometryCalculator())
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
                new FixedIntervalReplayExecutor(),
                mock(MqttOsdSubscriber.class),
                mock(SqliteOsdSampleLoader.class),
                new SqliteOsdSampleRecordMapper(new OsdFrameGeometryCalculator())
        );
        ReflectionTestUtils.setField(service, "baseMapper", mapper);

        service.replayByTask(task);

        verify(mapper).selectList(any());
        assertEquals(1, payloads.size());
        JsonNode payload = readJson(payloads.get(0));
        assertEquals("drone-001", payload.path("device_sn").asText());
        assertEquals(12L, payload.path("task_id").asLong());
        assertEquals(91.5D, payload.path("attitude_head").asDouble());
        assertEquals(1.8D, payload.path("attitude_pitch").asDouble());
        assertEquals(0.4D, payload.path("attitude_roll").asDouble());
        assertEquals(30.1856666496194D, payload.path("latitude").asDouble());
        assertEquals(120.1979761985018D, payload.path("longitude").asDouble());
        assertEquals(40.7109D, payload.path("height").asDouble());
        assertEquals(0, payload.path("action_type").asInt());
        assertEquals(2501230.409367526D, payload.path("total_flight_distance").asDouble());
        assertEquals(1004240D, payload.path("total_flight_time").asDouble());
        assertEquals(2660, payload.path("total_flight_sorties").asInt());
        assertEquals("CN", payload.path("country").asText());
        assertEquals("admin", payload.path("created_by").asText());
        assertEquals("admin", payload.path("updated_by").asText());
        assertTrue(payload.has("battery"));
        assertTrue(payload.has("position_state"));
        assertTrue(payload.has("distance_limit_status"));
        assertTrue(payload.has("maintain_status"));
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
                new FixedIntervalReplayExecutor(),
                mock(MqttOsdSubscriber.class),
                mock(SqliteOsdSampleLoader.class),
                new SqliteOsdSampleRecordMapper(new OsdFrameGeometryCalculator())
        );
        ReflectionTestUtils.setField(service, "baseMapper", mapper);

        StreamTaskEntity task = new StreamTaskEntity();
        task.setId(7L);
        service.replayByTask(task);

        verify(mapper, never()).selectList(any());
        assertEquals(List.of("{\"device_sn\":\"json-1\"}"), payloads);
    }

    @Test
    void mqttReplaySubscribesAndBroadcastsMappedPayload() {
        DeviceTelemetryMapper mapper = mock(DeviceTelemetryMapper.class);
        StaticJsonOsdPayloadLoader staticJsonLoader = mock(StaticJsonOsdPayloadLoader.class);
        TaskRunLogService runLogService = mock(TaskRunLogService.class);
        CapturingMqttOsdSubscriber mqttSubscriber = new CapturingMqttOsdSubscriber();
        SimulatorProperties properties = new SimulatorProperties();
        properties.getOsd().setSourceType(OsdSourceType.MQTT);
        properties.getOsd().getMqtt().setTopic("thing/product/8UUXN4E00A05F5/drc/up");
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
                new FixedIntervalReplayExecutor(),
                mqttSubscriber,
                mock(SqliteOsdSampleLoader.class),
                new SqliteOsdSampleRecordMapper(new OsdFrameGeometryCalculator())
        );
        ReflectionTestUtils.setField(service, "baseMapper", mapper);
        StreamTaskEntity task = new StreamTaskEntity();
        task.setId(8L);

        service.replayByTask(task);

        assertNotNull(mqttSubscriber.properties);
        assertEquals("thing/product/8UUXN4E00A05F5/drc/up", mqttSubscriber.properties.getTopic());
        assertEquals(1, payloads.size());
        JsonNode payload = readJson(payloads.get(0));
        assertEquals(87D, payload.path("attitude_head").asDouble());
        assertEquals(30.18566738053386D, payload.path("frame_center").path("lat").asDouble());
        assertEquals(4, payload.path("corners").size());
    }

    @Test
    void mqttReplayLogsTimestampAndPipelineCost() {
        TaskRunLogService runLogService = mock(TaskRunLogService.class);
        CapturingMqttOsdSubscriber mqttSubscriber = new CapturingMqttOsdSubscriber();
        SimulatorProperties properties = new SimulatorProperties();
        properties.getOsd().setSourceType(OsdSourceType.MQTT);
        properties.getOsd().getMqtt().setTopic("thing/product/8UUXN4E00A05F5/drc/up");
        DeviceTelemetryService service = new DeviceTelemetryService(
                new OsdExcelImporter(),
                new OsdPayloadSupport(),
                new OsdFrameGeometryCalculator(),
                new CapturingBroadcastHandler(new ArrayList<>()),
                properties,
                runLogService,
                new PublishTimeReplayExecutor(),
                mock(StaticJsonOsdPayloadLoader.class),
                new FixedIntervalReplayExecutor(),
                mqttSubscriber,
                mock(SqliteOsdSampleLoader.class),
                new SqliteOsdSampleRecordMapper(new OsdFrameGeometryCalculator())
        );
        StreamTaskEntity task = new StreamTaskEntity();
        task.setId(18L);
        Logger logger = (Logger) LoggerFactory.getLogger(DeviceTelemetryService.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        try {
            service.replayByTask(task);
        } finally {
            logger.detachAppender(appender);
        }

        assertTrue(appender.list.stream().anyMatch(event ->
                event.getLevel() == Level.INFO
                        && event.getFormattedMessage().contains("MQTT_TRACE [MQTT_TO_WS_COST]")
                        && event.getFormattedMessage().contains("timestamp=1782972590947")));
    }

    @Test
    void mqttReplayWarnsWhenPipelineCostExceedsOneSecond() {
        TaskRunLogService runLogService = mock(TaskRunLogService.class);
        CapturingMqttOsdSubscriber mqttSubscriber = new CapturingMqttOsdSubscriber();
        SimulatorProperties properties = new SimulatorProperties();
        properties.getOsd().setSourceType(OsdSourceType.MQTT);
        properties.getOsd().getMqtt().setTopic("thing/product/8UUXN4E00A05F5/drc/up");
        DeviceTelemetryService service = new DeviceTelemetryService(
                new OsdExcelImporter(),
                new OsdPayloadSupport(),
                new OsdFrameGeometryCalculator(),
                new SlowCapturingBroadcastHandler(new ArrayList<>(), 1_100L),
                properties,
                runLogService,
                new PublishTimeReplayExecutor(),
                mock(StaticJsonOsdPayloadLoader.class),
                new FixedIntervalReplayExecutor(),
                mqttSubscriber,
                mock(SqliteOsdSampleLoader.class),
                new SqliteOsdSampleRecordMapper(new OsdFrameGeometryCalculator())
        );
        StreamTaskEntity task = new StreamTaskEntity();
        task.setId(19L);
        Logger logger = (Logger) LoggerFactory.getLogger(DeviceTelemetryService.class);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);

        try {
            service.replayByTask(task);
        } finally {
            logger.detachAppender(appender);
        }

        assertTrue(appender.list.stream().anyMatch(event ->
                event.getLevel() == Level.WARN
                        && event.getFormattedMessage().contains("MQTT_TRACE [MQTT_TO_WS_COST]")
                        && event.getFormattedMessage().contains("thresholdMillis=1000")));
    }

    @Test
    void stopReplayClosesActiveMqttSubscription() {
        StaticJsonOsdPayloadLoader staticJsonLoader = mock(StaticJsonOsdPayloadLoader.class);
        TaskRunLogService runLogService = mock(TaskRunLogService.class);
        CapturingMqttOsdSubscriber mqttSubscriber = new CapturingMqttOsdSubscriber();
        SimulatorProperties properties = new SimulatorProperties();
        properties.getOsd().setSourceType(OsdSourceType.MQTT);
        DeviceTelemetryService service = new DeviceTelemetryService(
                new OsdExcelImporter(),
                new OsdPayloadSupport(),
                new OsdFrameGeometryCalculator(),
                new CapturingBroadcastHandler(new ArrayList<>()),
                properties,
                runLogService,
                new PublishTimeReplayExecutor(),
                staticJsonLoader,
                new FixedIntervalReplayExecutor(),
                mqttSubscriber,
                mock(SqliteOsdSampleLoader.class),
                new SqliteOsdSampleRecordMapper(new OsdFrameGeometryCalculator())
        );
        StreamTaskEntity task = new StreamTaskEntity();
        task.setId(9L);

        service.replayByTask(task);
        service.stopReplay(9L);

        assertTrue(mqttSubscriber.closed);
    }

    @Test
    void sqliteReplayUsesSqliteFileInsteadOfMysqlOrMqtt() {
        DeviceTelemetryMapper mapper = mock(DeviceTelemetryMapper.class);
        StaticJsonOsdPayloadLoader staticJsonLoader = mock(StaticJsonOsdPayloadLoader.class);
        TaskRunLogService runLogService = mock(TaskRunLogService.class);
        MqttOsdSubscriber mqttSubscriber = mock(MqttOsdSubscriber.class);
        SqliteOsdSampleLoader sqliteLoader = mock(SqliteOsdSampleLoader.class);
        SimulatorProperties properties = new SimulatorProperties();
        properties.getOsd().setSourceType(OsdSourceType.SQLITE_FILE);
        when(sqliteLoader.loadOrderedRows()).thenReturn(List.of(sqliteRow(1L, 1782976544131L), sqliteRow(2L, 1782976544237L)));
        List<String> payloads = new ArrayList<>();
        RecordingDeviceTelemetryService service = new RecordingDeviceTelemetryService(
                new OsdExcelImporter(),
                new OsdPayloadSupport(),
                new OsdFrameGeometryCalculator(),
                new CapturingBroadcastHandler(payloads),
                properties,
                runLogService,
                new PublishTimeReplayExecutor(),
                staticJsonLoader,
                new FixedIntervalReplayExecutor(),
                mqttSubscriber,
                sqliteLoader,
                new SqliteOsdSampleRecordMapper(new OsdFrameGeometryCalculator())
        );
        ReflectionTestUtils.setField(service, "baseMapper", mapper);
        StreamTaskEntity task = new StreamTaskEntity();
        task.setId(21L);

        service.replayByTask(task);

        verify(mapper, never()).selectList(any());
        verify(mqttSubscriber, never()).subscribe(any(), any());
        assertEquals(List.of(106L), service.sleptMillis);
        assertEquals(2, payloads.size());
        JsonNode payload = readJson(payloads.get(0));
        assertEquals("1581F8HGX255D00A0DJQ", payload.path("device_sn").asText());
        assertEquals(21L, payload.path("task_id").asLong());
        assertEquals(4, payload.path("corners").size());
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
        entity.setAttitudeHead(91.5F);
        entity.setAttitudePitch(1.8D);
        entity.setAttitudeRoll(0.4D);
        entity.setElevation(0F);
        entity.setBattery("{\"capacityPercent\":90}");
        entity.setFirmwareVersion("37.03.01.39");
        entity.setGear("1");
        entity.setHeight(40.7109F);
        entity.setHomeDistance(0.0174192F);
        entity.setHorizontalSpeed(0F);
        entity.setLatitude(new BigDecimal("30.1856666496194"));
        entity.setLongitude(new BigDecimal("120.1979761985018"));
        entity.setModeCode(3);
        entity.setActionType(0);
        entity.setTotalFlightDistance(2501230.409367526D);
        entity.setTotalFlightTime(1004240F);
        entity.setVerticalSpeed(0F);
        entity.setWindDirection("0");
        entity.setWindSpeed(0F);
        entity.setPositionState("{\"isFixed\":2}");
        entity.setPayloads("[{\"payloadIndex\":\"99-0-0\"}]");
        entity.setStorage("{\"used\":37000}");
        entity.setNightLightsState("0");
        entity.setHeightLimit(500);
        entity.setDistanceLimitStatus("{\"state\":0}");
        entity.setObstacleAvoidance("{\"upside\":1}");
        entity.setActivationTime(1754077874L);
        entity.setCameras("[{\"cameraMode\":0}]");
        entity.setRcLostAction("2");
        entity.setRthAltitude(100);
        entity.setTotalFlightSorties(2660);
        entity.setExitWaylineWhenRcLost("NULL");
        entity.setCountry("CN");
        entity.setRidState(true);
        entity.setNearAreaLimit(false);
        entity.setNearHeightLimit(false);
        entity.setMaintainStatus("{\"maintainStatusArray\":[]}");
        entity.setTrackId("track-1");
        entity.setPublishTime(LocalDateTime.of(2026, 7, 1, 10, 13, 25));
        entity.setCreateTime(LocalDateTime.of(2026, 7, 1, 10, 13, 26));
        entity.setUpdateTime(LocalDateTime.of(2026, 7, 1, 10, 13, 27));
        entity.setCreatedBy("admin");
        entity.setUpdatedBy("admin");
        entity.setIsDeleted(0);
        return entity;
    }

    private JsonNode readJson(String value) {
        try {
            return objectMapper.readTree(value);
        } catch (Exception e) {
            throw new AssertionError("payload should be valid json", e);
        }
    }

    private SqliteOsdSampleRow sqliteRow(Long id, Long messageTimestampMs) {
        SqliteOsdSampleRow row = new SqliteOsdSampleRow();
        row.setId(id);
        row.setMessageTimestampMs(messageTimestampMs);
        row.setDockSn("8UUXN4E00A05F5");
        row.setDroneSn("1581F8HGX255D00A0DJQ");
        row.setAttitudeHead(87.2D);
        row.setLatitude(30.185666333377757D);
        row.setLongitude(120.19797618637993D);
        row.setHeight(122.29833374023438D);
        row.setSpeedZ(-6D);
        row.setGimbalPitch(0D);
        row.setGimbalRoll(0D);
        row.setGimbalYaw(87.6218311538285D);
        row.setRawJson("{\"sample\":true}");
        return row;
    }

    private String sampleMqttPayload() {
        return """
                {"data":{"attitude_head":87,"elevation":59.7,"gimbal_pitch":-0.1,"gimbal_roll":1.5,"gimbal_yaw":87.3602828699535,"height":100.24636383056641,"home_distance":0.0902225822210312,"horizontal_speed":0,"latitude":30.18566738053386,"longitude":120.19797559348879,"speed_x":0,"speed_y":0,"speed_z":1,"ultrasonic_height":-1,"vertical_speed":-1,"wind_direction":4,"wind_speed":40},"method":"osd_info_push","seq":4392,"timestamp":1782972590947}
                """;
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

    private static final class SlowCapturingBroadcastHandler extends CapturingBroadcastHandler {
        private final long delayMillis;

        private SlowCapturingBroadcastHandler(List<String> payloads, long delayMillis) {
            super(payloads);
            this.delayMillis = delayMillis;
        }

        @Override
        public void broadcast(String payload) {
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new AssertionError("sleep interrupted", e);
            }
            super.broadcast(payload);
        }
    }

    private final class CapturingMqttOsdSubscriber implements MqttOsdSubscriber {
        private MqttOsdProperties properties;
        private boolean closed;

        @Override
        public MqttOsdSubscriberSession subscribe(MqttOsdProperties properties, MqttOsdMessageHandler handler) {
            this.properties = properties;
            handler.handle(new MqttOsdRecordMapper(new OsdFrameGeometryCalculator()).map(sampleMqttPayload(), 60.0, 40.0),
                    System.nanoTime());
            return () -> closed = true;
        }
    }

    private static final class RecordingDeviceTelemetryService extends DeviceTelemetryService {
        private final List<Long> sleptMillis = new ArrayList<>();

        private RecordingDeviceTelemetryService(OsdExcelImporter excelImporter,
                                               OsdPayloadSupport payloadSupport,
                                               OsdFrameGeometryCalculator frameGeometryCalculator,
                                               OsdBroadcastWebSocketHandler osdBroadcastWebSocketHandler,
                                               SimulatorProperties properties,
                                               TaskRunLogService runLogService,
                                               PublishTimeReplayExecutor replayExecutor,
                                               StaticJsonOsdPayloadLoader staticJsonOsdPayloadLoader,
                                               FixedIntervalReplayExecutor fixedIntervalReplayExecutor,
                                               MqttOsdSubscriber mqttOsdSubscriber,
                                               SqliteOsdSampleLoader sqliteOsdSampleLoader,
                                               SqliteOsdSampleRecordMapper sqliteOsdSampleRecordMapper) {
            super(excelImporter, payloadSupport, frameGeometryCalculator, osdBroadcastWebSocketHandler, properties,
                    runLogService, replayExecutor, staticJsonOsdPayloadLoader, fixedIntervalReplayExecutor,
                    mqttOsdSubscriber, sqliteOsdSampleLoader, sqliteOsdSampleRecordMapper);
        }

        @Override
        void sleep(long millis) {
            sleptMillis.add(millis);
        }
    }
}
