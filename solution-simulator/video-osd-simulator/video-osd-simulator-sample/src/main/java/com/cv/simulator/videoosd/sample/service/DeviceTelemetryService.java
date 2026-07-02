package com.cv.simulator.videoosd.sample.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cv.boot.crud.mp.support.MpCrudSupport;
import com.cv.boot.mybatisplus.pojo.vo.PageInfoVO;
import com.cv.simulator.videoosd.core.mqtt.MqttOsdProperties;
import com.cv.simulator.videoosd.core.mqtt.MqttOsdSubscriber;
import com.cv.simulator.videoosd.core.mqtt.MqttOsdSubscriberSession;
import com.cv.simulator.videoosd.core.osd.DeviceTelemetryRecord;
import com.cv.simulator.videoosd.core.osd.OsdExcelImporter;
import com.cv.simulator.videoosd.core.osd.OsdFrameGeometryCalculator;
import com.cv.simulator.videoosd.core.osd.OsdPayloadSupport;
import com.cv.simulator.videoosd.sample.config.OsdBroadcastWebSocketHandler;
import com.cv.simulator.videoosd.sample.config.OsdSourceType;
import com.cv.simulator.videoosd.sample.config.SimulatorProperties;
import com.cv.simulator.videoosd.sample.mapper.DeviceTelemetryMapper;
import com.cv.simulator.videoosd.sample.pojo.entity.DeviceTelemetryEntity;
import com.cv.simulator.videoosd.sample.pojo.entity.StreamTaskEntity;
import com.cv.simulator.videoosd.sample.pojo.query.DeleteIdsQuery;
import com.cv.simulator.videoosd.sample.pojo.query.TelemetryPageQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
public class DeviceTelemetryService extends ServiceImpl<DeviceTelemetryMapper, DeviceTelemetryEntity> {

    private final OsdExcelImporter excelImporter;
    private final OsdPayloadSupport payloadSupport;
    private final OsdFrameGeometryCalculator frameGeometryCalculator;
    private final OsdBroadcastWebSocketHandler osdBroadcastWebSocketHandler;
    private final SimulatorProperties properties;
    private final TaskRunLogService runLogService;
    private final PublishTimeReplayExecutor replayExecutor;
    private final StaticJsonOsdPayloadLoader staticJsonOsdPayloadLoader;
    private final FixedIntervalReplayExecutor fixedIntervalReplayExecutor;
    private final MqttOsdSubscriber mqttOsdSubscriber;
    private final ConcurrentMap<Long, MqttOsdSubscriberSession> mqttReplaySessions = new ConcurrentHashMap<>();

    public DeviceTelemetryService(OsdExcelImporter excelImporter,
                                  OsdPayloadSupport payloadSupport,
                                  OsdFrameGeometryCalculator frameGeometryCalculator,
                                  OsdBroadcastWebSocketHandler osdBroadcastWebSocketHandler,
                                  SimulatorProperties properties,
                                  TaskRunLogService runLogService,
                                  PublishTimeReplayExecutor replayExecutor,
                                  StaticJsonOsdPayloadLoader staticJsonOsdPayloadLoader,
                                  FixedIntervalReplayExecutor fixedIntervalReplayExecutor,
                                  MqttOsdSubscriber mqttOsdSubscriber) {
        this.excelImporter = excelImporter;
        this.payloadSupport = payloadSupport;
        this.frameGeometryCalculator = frameGeometryCalculator;
        this.osdBroadcastWebSocketHandler = osdBroadcastWebSocketHandler;
        this.properties = properties;
        this.runLogService = runLogService;
        this.replayExecutor = replayExecutor;
        this.staticJsonOsdPayloadLoader = staticJsonOsdPayloadLoader;
        this.fixedIntervalReplayExecutor = fixedIntervalReplayExecutor;
        this.mqttOsdSubscriber = mqttOsdSubscriber;
    }

    public PageInfoVO<DeviceTelemetryEntity> pageList(TelemetryPageQuery query) {
        Page<DeviceTelemetryEntity> page = lambdaQuery()
                .eq(DeviceTelemetryEntity::getIsDeleted, 0)
                .eq(query.getTaskId() != null, DeviceTelemetryEntity::getTaskId, query.getTaskId())
                .eq(query.getDeviceSn() != null && !query.getDeviceSn().trim().isEmpty(),
                        DeviceTelemetryEntity::getDeviceSn, query.getDeviceSn())
                .eq(query.getTrackId() != null && !query.getTrackId().trim().isEmpty(),
                        DeviceTelemetryEntity::getTrackId, query.getTrackId())
                .ge(query.getPublishTimeStart() != null,
                        DeviceTelemetryEntity::getPublishTime, query.getPublishTimeStart())
                .le(query.getPublishTimeEnd() != null,
                        DeviceTelemetryEntity::getPublishTime, query.getPublishTimeEnd())
                .orderByAsc(DeviceTelemetryEntity::getPublishTime)
                .orderByAsc(DeviceTelemetryEntity::getId)
                .page(new Page<>(query.getCurrent(), query.getSize()));
        return MpCrudSupport.buildPage(page);
    }

    public Long add(DeviceTelemetryEntity entity) {
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setIsDeleted(0);
        save(entity);
        return entity.getId();
    }

    public Long edit(DeviceTelemetryEntity entity) {
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);
        return entity.getId();
    }

    public void delete(DeleteIdsQuery query) {
        if (query.getIdList() == null || query.getIdList().isEmpty()) {
            return;
        }
        query.getIdList().forEach(id -> {
            DeviceTelemetryEntity entity = new DeviceTelemetryEntity();
            entity.setId(id);
            entity.setIsDeleted(1);
            entity.setUpdateTime(LocalDateTime.now());
            updateById(entity);
        });
    }

    public DeviceTelemetryEntity detail(Long id) {
        return MpCrudSupport.required(getById(id), () -> new IllegalArgumentException("telemetry not found"));
    }

    public Long uploadJson(DeviceTelemetryEntity entity) {
        return add(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public int importExcel(InputStream inputStream, Long taskId) {
        List<DeviceTelemetryRecord> records = excelImporter.importExcel(inputStream);
        records.forEach(record -> {
            DeviceTelemetryEntity entity = fromRecord(record);
            if (entity.getTaskId() == null) {
                entity.setTaskId(taskId);
            }
            add(entity);
        });
        return records.size();
    }

    @Async
    public void replayByTask(StreamTaskEntity task) {
        if (properties.getOsd().getSourceType() == OsdSourceType.STATIC_JSON) {
            replayStaticJson(task);
            return;
        }
        if (properties.getOsd().getSourceType() == OsdSourceType.MQTT) {
            replayMqtt(task);
            return;
        }
        Long taskId = task.getId();
        LocalDateTime publishTimeStart = resolvePublishTimeStart(task);
        LocalDateTime publishTimeEnd = resolvePublishTimeEnd(task);
        boolean requireTaskIdMatch = properties.getOsd().isRequireTaskIdMatch();
        log.info("{} taskId={}, publishTimeStart={}, publishTimeEnd={}, requireTaskIdMatch={}",
                OsdReplayLogSupport.marker("MYSQL_REPLAY_START"),
                taskId, publishTimeStart, publishTimeEnd, requireTaskIdMatch);
        List<DeviceTelemetryEntity> rows = loadReplayRows(task);
        log.info("{} taskId={}, {}", OsdReplayLogSupport.marker("MYSQL_QUERY_RESULT"), taskId, OsdReplayLogSupport.summarizeRows(rows));
        int sent = replayExecutor.replay(rows, row -> {
            log.info("{} taskId={}, rowId={}, publishTime={}, deviceSn={}",
                    OsdReplayLogSupport.marker("MYSQL_ROW_PICKED"),
                    taskId, row.getId(), row.getPublishTime(), row.getDeviceSn());
            String payload = payloadSupport.toPayloadJson(toRecord(row));
            log.info("{} taskId={}, rowId={}, payloadPreview={}",
                    OsdReplayLogSupport.marker("MYSQL_PAYLOAD_BUILT"),
                    taskId, row.getId(), OsdReplayLogSupport.payloadPreview(payload, 200));
            log.info("{} taskId={}, rowId={}, activeSessions={}",
                    OsdReplayLogSupport.marker("MYSQL_BROADCASTING"),
                    taskId, row.getId(), osdBroadcastWebSocketHandler.activeSessionCount());
            osdBroadcastWebSocketHandler.broadcast(payload);
        }, this::sleep);
        if (sent == 0) {
            runLogService.record(taskId, "OSD_REPLAY", "STOPPED", "no osd data found in publish_time window", null, 0);
            return;
        }
        log.info("{} taskId={}, sentCount={}", OsdReplayLogSupport.marker("MYSQL_REPLAY_COMPLETED"), taskId, sent);
        runLogService.record(taskId, "OSD_REPLAY", "STOPPED", "osd replay completed", null, sent);
    }

    public void startRealtimeMqtt(StreamTaskEntity task) {
        replayMqtt(task);
    }

    private void replayStaticJson(StreamTaskEntity task) {
        Long taskId = task.getId();
        String location = properties.getOsd().getStaticJsonLocation();
        List<String> payloads = staticJsonOsdPayloadLoader.load(location);
        log.info("{} taskId={}, location={}, intervalMillis={}",
                OsdReplayLogSupport.marker("STATIC_REPLAY_START"),
                taskId, location, properties.getOsd().getFixedIntervalMillis());
        int sent = fixedIntervalReplayExecutor.replay(payloads,
                payload -> {
                    log.info("{} taskId={}, activeSessions={}, payloadPreview={}",
                            OsdReplayLogSupport.marker("STATIC_BROADCASTING"),
                            taskId,
                            osdBroadcastWebSocketHandler.activeSessionCount(),
                            OsdReplayLogSupport.payloadPreview(payload, 200));
                    osdBroadcastWebSocketHandler.broadcast(payload);
                },
                this::sleep,
                properties.getOsd().getFixedIntervalMillis());
        if (sent == 0) {
            runLogService.record(taskId, "OSD_REPLAY", "STOPPED", "no osd data found in static json file", null, 0);
            return;
        }
        runLogService.record(taskId, "OSD_REPLAY", "STOPPED", "static json osd replay completed", null, sent);
    }

    public void replayByTask(Long taskId) {
        StreamTaskEntity task = new StreamTaskEntity();
        task.setId(taskId);
        replayByTask(task);
    }

    public void stopReplay(Long taskId) {
        MqttOsdSubscriberSession session = mqttReplaySessions.remove(taskId);
        if (session == null) {
            return;
        }
        session.close();
        log.info("MQTT_TRACE [SUBSCRIBE_STOPPED] taskId={}", taskId);
        log.info("{} taskId={}", OsdReplayLogSupport.marker("MQTT_SUBSCRIBE_STOP"), taskId);
        runLogService.record(taskId, "MQTT_OSD_STOP", "STOPPED", "mqtt osd subscription stopped", null, 0);
    }

    List<DeviceTelemetryEntity> loadReplayRows(StreamTaskEntity task) {
        Long taskId = task.getId();
        LocalDateTime publishTimeStart = resolvePublishTimeStart(task);
        LocalDateTime publishTimeEnd = resolvePublishTimeEnd(task);
        boolean requireTaskIdMatch = properties.getOsd().isRequireTaskIdMatch();
        log.info("{} taskId={}, publishTimeStart={}, publishTimeEnd={}, requireTaskIdMatch={}, strategy=lambdaQuery-all-records",
                OsdReplayLogSupport.marker("MYSQL_QUERY"),
                taskId, publishTimeStart, publishTimeEnd, requireTaskIdMatch);
        return lambdaQuery()
                .eq(DeviceTelemetryEntity::getIsDeleted, 0)
                .orderByAsc(DeviceTelemetryEntity::getPublishTime)
                .orderByAsc(DeviceTelemetryEntity::getId)
                .list();
    }

    private DeviceTelemetryEntity fromRecord(DeviceTelemetryRecord record) {
        DeviceTelemetryEntity entity = new DeviceTelemetryEntity();
        entity.setTaskId(record.getTaskId());
        entity.setDeviceSn(record.getDeviceSn());
        entity.setLatitude(record.getLatitude());
        entity.setLongitude(record.getLongitude());
        entity.setRawJson(record.getRawJson());
        entity.setPublishTime(record.getPublishTime());
        return entity;
    }

    private DeviceTelemetryRecord toRecord(DeviceTelemetryEntity entity) {
        DeviceTelemetryRecord record = new DeviceTelemetryRecord();
        record.setId(entity.getId());
        record.setTaskId(entity.getTaskId());
        record.setDeviceSn(entity.getDeviceSn());
        record.setAttitudeHead(entity.getAttitudeHead());
        record.setAttitudePitch(entity.getAttitudePitch());
        record.setAttitudeRoll(entity.getAttitudeRoll());
        record.setElevation(entity.getElevation());
        record.setBattery(entity.getBattery());
        record.setFirmwareVersion(entity.getFirmwareVersion());
        record.setGear(entity.getGear());
        record.setHeight(entity.getHeight());
        record.setHomeDistance(entity.getHomeDistance());
        record.setHorizontalSpeed(entity.getHorizontalSpeed());
        record.setLatitude(entity.getLatitude());
        record.setLongitude(entity.getLongitude());
        record.setModeCode(entity.getModeCode());
        record.setActionType(entity.getActionType());
        record.setTotalFlightDistance(entity.getTotalFlightDistance());
        record.setTotalFlightTime(entity.getTotalFlightTime());
        record.setVerticalSpeed(entity.getVerticalSpeed());
        record.setWindDirection(entity.getWindDirection());
        record.setWindSpeed(entity.getWindSpeed());
        record.setPositionState(entity.getPositionState());
        record.setPayloads(entity.getPayloads());
        record.setStorage(entity.getStorage());
        record.setNightLightsState(entity.getNightLightsState());
        record.setHeightLimit(entity.getHeightLimit());
        record.setDistanceLimitStatus(entity.getDistanceLimitStatus());
        record.setObstacleAvoidance(entity.getObstacleAvoidance());
        record.setActivationTime(entity.getActivationTime());
        record.setCameras(entity.getCameras());
        record.setRcLostAction(entity.getRcLostAction());
        record.setRthAltitude(entity.getRthAltitude());
        record.setTotalFlightSorties(entity.getTotalFlightSorties());
        record.setExitWaylineWhenRcLost(entity.getExitWaylineWhenRcLost());
        record.setCountry(entity.getCountry());
        record.setRidState(entity.getRidState());
        record.setNearAreaLimit(entity.getNearAreaLimit());
        record.setNearHeightLimit(entity.getNearHeightLimit());
        record.setMaintainStatus(entity.getMaintainStatus());
        record.setTrackId(entity.getTrackId());
        record.setPublishTime(entity.getPublishTime());
        record.setRawJson(entity.getRawJson());
        record.setCreateTime(entity.getCreateTime());
        record.setCreatedBy(entity.getCreatedBy());
        record.setUpdateTime(entity.getUpdateTime());
        record.setUpdatedBy(entity.getUpdatedBy());
        record.setIsDeleted(entity.getIsDeleted());
        frameGeometryCalculator.populateFrameGeometry(record,
                properties.getOsd().getFrameHfovDeg(),
                properties.getOsd().getFrameVfovDeg());
        return record;
    }

    private void replayMqtt(StreamTaskEntity task) {
        Long taskId = task.getId();
        stopReplay(taskId);
        MqttOsdProperties mqttProperties = buildMqttProperties();
        log.info("{} taskId={}, brokerUrl={}, topic={}, qos={}",
                OsdReplayLogSupport.marker("MQTT_SUBSCRIBE_START"),
                taskId,
                mqttProperties.getBrokerUrl(),
                mqttProperties.getTopic(),
                mqttProperties.getQos());
        log.info("MQTT_TRACE [SUBSCRIBE_PREPARING] taskId={}, brokerUrl={}, topic={}, qos={}",
                taskId,
                mqttProperties.getBrokerUrl(),
                mqttProperties.getTopic(),
                mqttProperties.getQos());
        try {
            MqttOsdSubscriberSession session = mqttOsdSubscriber.subscribe(mqttProperties, record -> {
                record.setTaskId(taskId);
                log.info("MQTT_TRACE [MESSAGE_ARRIVED] taskId={}, publishTime={}, latitude={}, longitude={}",
                        taskId, record.getPublishTime(), record.getLatitude(), record.getLongitude());
                log.info("{} taskId={}, payloadPreview={}",
                        OsdReplayLogSupport.marker("MQTT_MESSAGE_RECEIVED"),
                        taskId,
                        OsdReplayLogSupport.payloadPreview(record.getRawJson(), 200));
                runLogService.record(taskId, "MQTT_OSD_RECEIVED", "RUNNING", "mqtt osd message received", null, 1);
                log.info("MQTT_TRACE [MESSAGE_MAPPED] taskId={}, attitudeHead={}, elevation={}, height={}",
                        taskId, record.getAttitudeHead(), record.getElevation(), record.getHeight());
                String payload = payloadSupport.toPayloadJson(record);
                log.info("OSD_TRACE [MQTT_BROADCASTING] taskId={}, activeSessions={}",
                        taskId, osdBroadcastWebSocketHandler.activeSessionCount());
                osdBroadcastWebSocketHandler.broadcast(payload);
                log.info("OSD_TRACE [WS_BROADCAST_DISPATCHED] taskId={}, payloadPreview={}",
                        taskId, OsdReplayLogSupport.payloadPreview(payload, 200));
                log.info("{} taskId={}, activeSessions={}, payloadPreview={}",
                        OsdReplayLogSupport.marker("MQTT_BROADCASTING"),
                        taskId,
                        osdBroadcastWebSocketHandler.activeSessionCount(),
                        OsdReplayLogSupport.payloadPreview(payload, 200));
                runLogService.record(taskId, "MQTT_OSD_BROADCAST", "RUNNING", "mqtt osd message broadcasted", null, 1);
            });
            mqttReplaySessions.put(taskId, session);
            log.info("MQTT_TRACE [SUBSCRIBED] taskId={}, topic={}", taskId, mqttProperties.getTopic());
            runLogService.record(taskId, "MQTT_OSD_START", "RUNNING", "mqtt osd subscription started", null, 0);
        } catch (RuntimeException e) {
            log.error("MQTT_TRACE [SUBSCRIBE_FAILED] taskId={}, message={}", taskId, e.getMessage(), e);
            runLogService.record(taskId, "MQTT_OSD_START", "FAILED", e.getMessage(), null, 0);
            throw e;
        }
    }

    private MqttOsdProperties buildMqttProperties() {
        SimulatorProperties.Mqtt mqtt = properties.getOsd().getMqtt();
        MqttOsdProperties mqttProperties = new MqttOsdProperties();
        mqttProperties.setBrokerUrl(mqtt.getBrokerUrl());
        mqttProperties.setClientId(mqtt.getClientId());
        mqttProperties.setTopic(mqtt.getTopic());
        mqttProperties.setUsername(mqtt.getUsername());
        mqttProperties.setPassword(mqtt.getPassword());
        mqttProperties.setQos(mqtt.getQos());
        mqttProperties.setAutoReconnect(mqtt.isAutoReconnect());
        mqttProperties.setCleanSession(mqtt.isCleanSession());
        mqttProperties.setFrameHfovDeg(properties.getOsd().getFrameHfovDeg());
        mqttProperties.setFrameVfovDeg(properties.getOsd().getFrameVfovDeg());
        return mqttProperties;
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("osd replay interrupted", e);
        }
    }

    private LocalDateTime resolvePublishTimeStart(StreamTaskEntity task) {
        if (task.getOsdPublishTimeStart() != null) {
            return task.getOsdPublishTimeStart();
        }
        return parseDateTime(properties.getOsd().getPublishTimeStart());
    }

    private LocalDateTime resolvePublishTimeEnd(StreamTaskEntity task) {
        if (task.getOsdPublishTimeEnd() != null) {
            return task.getOsdPublishTimeEnd();
        }
        return parseDateTime(properties.getOsd().getPublishTimeEnd());
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(value.trim());
    }
}
