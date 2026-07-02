package com.cv.simulator.videoosd.sample.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cv.boot.crud.mp.support.MpCrudSupport;
import com.cv.boot.mybatisplus.pojo.vo.PageInfoVO;
import com.cv.simulator.videoosd.core.enums.StreamProtocol;
import com.cv.simulator.videoosd.core.enums.TaskStatus;
import com.cv.simulator.videoosd.core.ffmpeg.FfmpegCommandBuilder;
import com.cv.simulator.videoosd.core.ffmpeg.FfmpegCommandRequest;
import com.cv.simulator.videoosd.core.ffmpeg.FfmpegPathResolver;
import com.cv.simulator.videoosd.core.media.VideoSourceScanner;
import com.cv.simulator.videoosd.core.runtime.StreamTaskRuntime;
import com.cv.simulator.videoosd.core.runtime.StreamTaskSnapshot;
import com.cv.simulator.videoosd.core.webrtc.ExternalWebRtcCommandBuilder;
import com.cv.simulator.videoosd.core.webrtc.ExternalWebRtcCommandRequest;
import com.cv.simulator.videoosd.sample.config.SimulatorProperties;
import com.cv.simulator.videoosd.sample.mapper.StreamTaskMapper;
import com.cv.simulator.videoosd.sample.pojo.entity.StreamTaskEntity;
import com.cv.simulator.videoosd.sample.pojo.query.DeleteIdsQuery;
import com.cv.simulator.videoosd.sample.pojo.query.StreamTaskPageQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StreamTaskService extends ServiceImpl<StreamTaskMapper, StreamTaskEntity> {

    private final SimulatorProperties properties;
    private final FfmpegPathResolver ffmpegPathResolver;
    private final VideoSourceScanner videoSourceScanner;
    private final FfmpegCommandBuilder commandBuilder;
    private final ExternalWebRtcCommandBuilder externalWebRtcCommandBuilder;
    private final StreamTaskRuntime runtime;
    private final TaskRunLogService runLogService;

    public StreamTaskService(SimulatorProperties properties,
                             FfmpegPathResolver ffmpegPathResolver,
                             VideoSourceScanner videoSourceScanner,
                             FfmpegCommandBuilder commandBuilder,
                             ExternalWebRtcCommandBuilder externalWebRtcCommandBuilder,
                             StreamTaskRuntime runtime,
                             TaskRunLogService runLogService) {
        this.properties = properties;
        this.ffmpegPathResolver = ffmpegPathResolver;
        this.videoSourceScanner = videoSourceScanner;
        this.commandBuilder = commandBuilder;
        this.externalWebRtcCommandBuilder = externalWebRtcCommandBuilder;
        this.runtime = runtime;
        this.runLogService = runLogService;
    }

    public PageInfoVO<StreamTaskEntity> pageList(StreamTaskPageQuery query) {
        Page<StreamTaskEntity> page = lambdaQuery()
                .eq(StreamTaskEntity::getIsDeleted, 0)
                .like(query.getTaskName() != null && !query.getTaskName().trim().isEmpty(),
                        StreamTaskEntity::getTaskName, query.getTaskName())
                .eq(query.getStatus() != null && !query.getStatus().trim().isEmpty(),
                        StreamTaskEntity::getStatus, query.getStatus())
                .orderByDesc(StreamTaskEntity::getId)
                .page(new Page<>(query.getCurrent(), query.getSize()));
        return MpCrudSupport.buildPage(page);
    }

    public Long add(StreamTaskEntity entity) {
        fillDefaults(entity);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setIsDeleted(0);
        save(entity);
        return entity.getId();
    }

    public Long edit(StreamTaskEntity entity) {
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);
        return entity.getId();
    }

    public void delete(DeleteIdsQuery query) {
        if (query.getIdList() == null || query.getIdList().isEmpty()) {
            return;
        }
        query.getIdList().forEach(id -> {
            StreamTaskEntity entity = new StreamTaskEntity();
            entity.setId(id);
            entity.setIsDeleted(1);
            entity.setUpdateTime(LocalDateTime.now());
            updateById(entity);
        });
    }

    public StreamTaskEntity detail(Long id) {
        return MpCrudSupport.required(getById(id), () -> new IllegalArgumentException("stream task not found"));
    }

    @Transactional(rollbackFor = Exception.class)
    public StreamTaskSnapshot start(Long id) {
        StreamTaskEntity task = detail(id);
        Path videoFile = resolveVideoFile(task);
        List<String> command;
        if (StreamProtocol.WEBRTC.name().equalsIgnoreCase(task.getProtocol())) {
            command = externalWebRtcCommandBuilder.build(new ExternalWebRtcCommandRequest(
                    resolveWebRtcCommandTemplate(task),
                    videoFile,
                    task.getZlmHost(),
                    task.getApp(),
                    task.getStream()
            ));
        } else {
            command = commandBuilder.build(new FfmpegCommandRequest(
                    ffmpegPathResolver.resolve(properties.getFfmpeg().getPath()),
                    videoFile,
                    StreamProtocol.valueOf(task.getProtocol()),
                    task.getZlmHost(),
                    task.getZlmPort(),
                    task.getApp(),
                    task.getStream(),
                    Boolean.TRUE.equals(task.getLoopEnabled()),
                    splitOptions(task.getFfmpegOptions())
            ));
        }
        log.info("stream-task start preparing: {}", buildDebugSummary(task, videoFile, command, properties.getOsd().getWebsocketEndpoint(), properties.getZlm().getHttpPort()));
        StreamTaskSnapshot snapshot = runtime.start(id, command);
        log.info("stream-task start result: taskId={}, status={}, message={}", id, snapshot.getStatus(), snapshot.getMessage());
        updateStatus(id, snapshot.getStatus(), snapshot.getMessage());
        runLogService.record(id, "STREAM_START", snapshot.getStatus().name(), snapshot.getMessage(), String.join(" ", command), 0);
        return snapshot;
    }

    public StreamTaskSnapshot stop(Long id) {
        StreamTaskSnapshot snapshot = runtime.stop(id);
        log.info("stream-task stop result: taskId={}, status={}, message={}", id, snapshot.getStatus(), snapshot.getMessage());
        updateStatus(id, snapshot.getStatus(), snapshot.getMessage());
        runLogService.record(id, "STREAM_STOP", snapshot.getStatus().name(), snapshot.getMessage(), null, 0);
        return snapshot;
    }

    public void startMqttSender(StreamTaskEntity task) {
        if (properties.getOsd().getSourceType() != com.cv.simulator.videoosd.sample.config.OsdSourceType.MQTT) {
            return;
        }
        SimulatorProperties.Mqtt mqtt = properties.getOsd().getMqtt();
        if (!mqtt.isSenderEnabled()) {
            log.info("MQTT_TRACE [SENDER_SKIPPED] taskId={}, reason=sender-disabled", task.getId());
            runLogService.record(task.getId(), "MQTT_SENDER_SKIP", "STOPPED", "mqtt sender script disabled", null, 0);
            return;
        }
        long delayMillis = Math.max(mqtt.getSenderStartDelayMillis(), 0L);
        String scriptPath = mqtt.getSenderScriptPath();
        log.info("MQTT_TRACE [SENDER_PREPARING] taskId={}, delayMillis={}, scriptPath={}",
                task.getId(), delayMillis, scriptPath);
        runLogService.record(task.getId(), "MQTT_SENDER_PREPARE", "RUNNING", "mqtt sender script preparing", scriptPath, 0);
        if (delayMillis > 0) {
            pause(delayMillis);
        }
        List<String> command = List.of("cmd.exe", "/c", scriptPath);
        runtime.startSidecar(task.getId(), command, "mqtt-osd-sender");
        log.info("MQTT_TRACE [SENDER_STARTED] taskId={}, command={}", task.getId(), String.join(" ", command));
        runLogService.record(task.getId(), "MQTT_SENDER_START", "RUNNING", "mqtt sender script started", String.join(" ", command), 0);
    }

    public StreamTaskSnapshot status(Long id) {
        StreamTaskSnapshot snapshot = runtime.status(id);
        log.info("stream-task runtime status: taskId={}, status={}, message={}", id, snapshot.getStatus(), snapshot.getMessage());
        return snapshot;
    }

    static String buildDebugSummary(StreamTaskEntity task,
                                    Path videoFile,
                                    List<String> command,
                                    String osdWebSocket,
                                    Integer zlmHttpPort) {
        String zlmHost = task.getZlmHost();
        String app = task.getApp();
        String stream = task.getStream();
        String protocol = task.getProtocol();
        String publishUrl = buildPublishUrl(task);
        String playUrl = String.format("http://%s:%s/webrtc/index.html?app=%s&stream=%s&type=play",
                zlmHost, zlmHttpPort, app, stream);
        return "taskId=" + task.getId()
                + ", taskName=" + task.getTaskName()
                + ", protocol=" + protocol
                + ", videoFile=" + videoFile
                + ", publishUrl=" + publishUrl
                + ", playUrl=" + playUrl
                + ", osdWebSocket=" + osdWebSocket
                + ", command=" + String.join(" ", command);
    }

    private static String buildPublishUrl(StreamTaskEntity task) {
        if (StreamProtocol.RTSP.name().equalsIgnoreCase(task.getProtocol())) {
            return String.format("rtsp://%s:%s/%s/%s", task.getZlmHost(), task.getZlmPort(), task.getApp(), task.getStream());
        }
        if (StreamProtocol.WEBRTC.name().equalsIgnoreCase(task.getProtocol())) {
            return String.format("webrtc-command://%s/%s/%s", task.getZlmHost(), task.getApp(), task.getStream());
        }
        return String.format("rtmp://%s:%s/%s/%s", task.getZlmHost(), task.getZlmPort(), task.getApp(), task.getStream());
    }

    private void fillDefaults(StreamTaskEntity entity) {
        if (entity.getProtocol() == null) {
            entity.setProtocol(StreamProtocol.RTMP.name());
        }
        if (entity.getStatus() == null) {
            entity.setStatus(TaskStatus.CREATED.name());
        }
        if (entity.getLoopEnabled() == null) {
            entity.setLoopEnabled(true);
        }
        if (entity.getVideoDirectory() == null || entity.getVideoDirectory().trim().isEmpty()) {
            entity.setVideoDirectory(properties.getVideo().getSourceDirectory());
        }
        if (entity.getVideoFilePath() == null || entity.getVideoFilePath().trim().isEmpty()) {
            entity.setVideoFilePath(properties.getVideo().getSourceFile());
        }
        if (entity.getFilePattern() == null || entity.getFilePattern().trim().isEmpty()) {
            entity.setFilePattern(properties.getFfmpeg().getInputPatterns());
        }
        if (entity.getZlmHost() == null || entity.getZlmHost().trim().isEmpty()) {
            entity.setZlmHost(properties.getZlm().getHost());
        }
        if (entity.getZlmPort() == null) {
            entity.setZlmPort(StreamProtocol.RTSP.name().equalsIgnoreCase(entity.getProtocol())
                    ? properties.getZlm().getRtspPort() : properties.getZlm().getRtmpPort());
        }
        if (entity.getApp() == null || entity.getApp().trim().isEmpty()) {
            entity.setApp(properties.getZlm().getApp());
        }
        if (entity.getStream() == null || entity.getStream().trim().isEmpty()) {
            entity.setStream(properties.getZlm().getStream());
        }
        if (entity.getWebrtcCommandTemplate() == null || entity.getWebrtcCommandTemplate().trim().isEmpty()) {
            entity.setWebrtcCommandTemplate(properties.getWebrtc().getPushCommandTemplate());
        }
        if (entity.getOsdPublishTimeStart() == null) {
            entity.setOsdPublishTimeStart(parseDateTime(properties.getOsd().getPublishTimeStart()));
        }
        if (entity.getOsdPublishTimeEnd() == null) {
            entity.setOsdPublishTimeEnd(parseDateTime(properties.getOsd().getPublishTimeEnd()));
        }
    }

    private void updateStatus(Long id, TaskStatus status, String message) {
        StreamTaskEntity entity = new StreamTaskEntity();
        entity.setId(id);
        entity.setStatus(status.name());
        entity.setLastMessage(message);
        entity.setUpdateTime(LocalDateTime.now());
        updateById(entity);
    }

    private List<String> splitOptions(String options) {
        if (options == null || options.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(options.trim().split("\\s+")).collect(Collectors.toList());
    }

    private Path resolveVideoFile(StreamTaskEntity task) {
        if (task.getVideoFilePath() != null && !task.getVideoFilePath().trim().isEmpty()) {
            return Paths.get(task.getVideoFilePath());
        }
        List<Path> files = videoSourceScanner.scan(Paths.get(task.getVideoDirectory()), task.getFilePattern());
        if (files.isEmpty()) {
            throw new IllegalArgumentException("no video files found in directory: " + task.getVideoDirectory());
        }
        return files.get(0);
    }

    private String resolveWebRtcCommandTemplate(StreamTaskEntity task) {
        if (task.getWebrtcCommandTemplate() != null && !task.getWebrtcCommandTemplate().trim().isEmpty()) {
            return task.getWebrtcCommandTemplate();
        }
        return properties.getWebrtc().getPushCommandTemplate();
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(value.trim());
    }

    void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("mqtt sender start interrupted", e);
        }
    }
}
