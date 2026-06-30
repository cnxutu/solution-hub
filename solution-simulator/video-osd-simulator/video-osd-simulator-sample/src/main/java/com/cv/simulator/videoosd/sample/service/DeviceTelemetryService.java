package com.cv.simulator.videoosd.sample.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cv.boot.crud.mp.support.MpCrudSupport;
import com.cv.boot.mybatisplus.pojo.vo.PageInfoVO;
import com.cv.simulator.videoosd.core.osd.DeviceTelemetryRecord;
import com.cv.simulator.videoosd.core.osd.OsdExcelImporter;
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

@Service
@Slf4j
public class DeviceTelemetryService extends ServiceImpl<DeviceTelemetryMapper, DeviceTelemetryEntity> {

    private final OsdExcelImporter excelImporter;
    private final OsdPayloadSupport payloadSupport;
    private final OsdBroadcastWebSocketHandler osdBroadcastWebSocketHandler;
    private final SimulatorProperties properties;
    private final TaskRunLogService runLogService;
    private final PublishTimeReplayExecutor replayExecutor;
    private final StaticJsonOsdPayloadLoader staticJsonOsdPayloadLoader;
    private final FixedIntervalReplayExecutor fixedIntervalReplayExecutor;

    public DeviceTelemetryService(OsdExcelImporter excelImporter,
                                  OsdPayloadSupport payloadSupport,
                                  OsdBroadcastWebSocketHandler osdBroadcastWebSocketHandler,
                                  SimulatorProperties properties,
                                  TaskRunLogService runLogService,
                                  PublishTimeReplayExecutor replayExecutor,
                                  StaticJsonOsdPayloadLoader staticJsonOsdPayloadLoader,
                                  FixedIntervalReplayExecutor fixedIntervalReplayExecutor) {
        this.excelImporter = excelImporter;
        this.payloadSupport = payloadSupport;
        this.osdBroadcastWebSocketHandler = osdBroadcastWebSocketHandler;
        this.properties = properties;
        this.runLogService = runLogService;
        this.replayExecutor = replayExecutor;
        this.staticJsonOsdPayloadLoader = staticJsonOsdPayloadLoader;
        this.fixedIntervalReplayExecutor = fixedIntervalReplayExecutor;
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
        Long taskId = task.getId();
        List<DeviceTelemetryEntity> rows = loadReplayRows(task);
        int sent = replayExecutor.replay(rows, row -> {
            osdBroadcastWebSocketHandler.broadcast(payloadSupport.toPayloadJson(toRecord(row)));
        }, this::sleep);
        if (sent == 0) {
            runLogService.record(taskId, "OSD_REPLAY", "STOPPED", "no osd data found in publish_time window", null, 0);
            return;
        }
        runLogService.record(taskId, "OSD_REPLAY", "STOPPED", "osd replay completed", null, sent);
    }

    private void replayStaticJson(StreamTaskEntity task) {
        Long taskId = task.getId();
        String location = properties.getOsd().getStaticJsonLocation();
        List<String> payloads = staticJsonOsdPayloadLoader.load(location);
        log.info("osd replay using static json: taskId={}, location={}, intervalMillis={}",
                taskId, location, properties.getOsd().getFixedIntervalMillis());
        int sent = fixedIntervalReplayExecutor.replay(payloads,
                osdBroadcastWebSocketHandler::broadcast,
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

    List<DeviceTelemetryEntity> loadReplayRows(StreamTaskEntity task) {
        Long taskId = task.getId();
        LocalDateTime publishTimeStart = resolvePublishTimeStart(task);
        LocalDateTime publishTimeEnd = resolvePublishTimeEnd(task);
        boolean requireTaskIdMatch = properties.getOsd().isRequireTaskIdMatch();
        return lambdaQuery()
                .eq(requireTaskIdMatch && taskId != null, DeviceTelemetryEntity::getTaskId, taskId)
                .eq(DeviceTelemetryEntity::getIsDeleted, 0)
                .isNotNull(DeviceTelemetryEntity::getPublishTime)
                .ge(publishTimeStart != null, DeviceTelemetryEntity::getPublishTime, publishTimeStart)
                .le(publishTimeEnd != null, DeviceTelemetryEntity::getPublishTime, publishTimeEnd)
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
        record.setLatitude(entity.getLatitude());
        record.setLongitude(entity.getLongitude());
        record.setModeCode(entity.getModeCode());
        record.setTrackId(entity.getTrackId());
        record.setRawJson(entity.getRawJson());
        return record;
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
