package com.cv.simulator.videoosd.sample.service;

import com.cv.simulator.videoosd.core.enums.TaskStatus;
import com.cv.simulator.videoosd.core.runtime.StreamTaskSnapshot;
import com.cv.simulator.videoosd.sample.config.OsdSourceType;
import com.cv.simulator.videoosd.sample.config.SimulatorProperties;
import com.cv.simulator.videoosd.sample.pojo.entity.StreamTaskEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StreamTaskLifecycleService {

    private final StreamTaskService streamTaskService;
    private final DeviceTelemetryService telemetryService;
    private final SimulatorProperties properties;

    public StreamTaskLifecycleService(StreamTaskService streamTaskService,
                                      DeviceTelemetryService telemetryService,
                                      SimulatorProperties properties) {
        this.streamTaskService = streamTaskService;
        this.telemetryService = telemetryService;
        this.properties = properties;
    }

    public StreamTaskSnapshot start(Long id) {
        log.info("OSD_TRACE [TASK_START_REQUEST] taskId={}, sourceType={}", id, properties.getOsd().getSourceType());
        StreamTaskSnapshot snapshot = streamTaskService.start(id);
        log.info("OSD_TRACE [TASK_START_STREAM_RESULT] taskId={}, status={}, message={}",
                id, snapshot.getStatus(), snapshot.getMessage());
        if (snapshot.getStatus() != TaskStatus.RUNNING) {
            return snapshot;
        }
        if (properties.getOsd().getSourceType() == OsdSourceType.MQTT
                && properties.getOsd().getMqtt().isDirectConsumeEnabled()) {
            log.info("MQTT_TRACE [TASK_DIRECT_CONSUME_ACTIVE] taskId={}, topic={}",
                    id, properties.getOsd().getMqtt().getTopic());
            return snapshot;
        }
        StreamTaskEntity task = streamTaskService.detail(id);
        if (properties.getOsd().getSourceType() == OsdSourceType.MQTT) {
            try {
                log.info("MQTT_TRACE [TASK_MQTT_SUBSCRIBE_BEGIN] taskId={}", id);
                telemetryService.startRealtimeMqtt(task);
                log.info("MQTT_TRACE [TASK_MQTT_SUBSCRIBE_READY] taskId={}", id);
                streamTaskService.startMqttSender(task);
                log.info("MQTT_TRACE [TASK_MQTT_SENDER_READY] taskId={}", id);
            } catch (RuntimeException e) {
                log.error("MQTT_TRACE [TASK_MQTT_START_FAILED] taskId={}, message={}", id, e.getMessage(), e);
                telemetryService.stopReplay(id);
                streamTaskService.stop(id);
                throw e;
            }
            return snapshot;
        }
        log.info("OSD_TRACE [TASK_REPLAY_ASYNC_BEGIN] taskId={}, sourceType={}", id, properties.getOsd().getSourceType());
        telemetryService.replayByTask(task);
        return snapshot;
    }

    public StreamTaskSnapshot stop(Long id) {
        log.info("OSD_TRACE [TASK_STOP_REQUEST] taskId={}, sourceType={}", id, properties.getOsd().getSourceType());
        if (!(properties.getOsd().getSourceType() == OsdSourceType.MQTT
                && properties.getOsd().getMqtt().isDirectConsumeEnabled())) {
            telemetryService.stopReplay(id);
        }
        StreamTaskSnapshot snapshot = streamTaskService.stop(id);
        log.info("OSD_TRACE [TASK_STOP_STREAM_RESULT] taskId={}, status={}, message={}",
                id, snapshot.getStatus(), snapshot.getMessage());
        return snapshot;
    }
}
