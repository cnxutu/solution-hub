package com.cv.simulator.videoosd.core.runtime;

import com.cv.simulator.videoosd.core.enums.TaskStatus;
import com.cv.simulator.videoosd.core.ffmpeg.FfmpegProcessHandle;
import com.cv.simulator.videoosd.core.ffmpeg.FfmpegProcessLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StreamTaskRuntime {

    private static final Logger log = LoggerFactory.getLogger(StreamTaskRuntime.class);

    private final FfmpegProcessLauncher processLauncher;
    private final Map<Long, FfmpegProcessHandle> handles = new ConcurrentHashMap<>();
    private final Map<Long, List<FfmpegProcessHandle>> sidecarHandles = new ConcurrentHashMap<>();

    public StreamTaskRuntime(FfmpegProcessLauncher processLauncher) {
        this.processLauncher = processLauncher;
    }

    public StreamTaskSnapshot start(Long taskId, List<String> command) {
        log.info("stream runtime starting: taskId={}, command={}", taskId, String.join(" ", command));
        FfmpegProcessHandle handle = processLauncher.launch(command);
        handles.put(taskId, handle);
        log.info("stream runtime started: taskId={}, alive={}", taskId, handle.isAlive());
        return snapshot(taskId, TaskStatus.RUNNING, "stream process started");
    }

    public void startSidecar(Long taskId, List<String> command, String processType) {
        log.info("stream runtime sidecar starting: taskId={}, processType={}, command={}",
                taskId, processType, String.join(" ", command));
        FfmpegProcessHandle handle = processLauncher.launch(command);
        sidecarHandles.computeIfAbsent(taskId, key -> new ArrayList<>()).add(handle);
        log.info("stream runtime sidecar started: taskId={}, processType={}, alive={}",
                taskId, processType, handle.isAlive());
    }

    public StreamTaskSnapshot stop(Long taskId) {
        FfmpegProcessHandle handle = handles.remove(taskId);
        List<FfmpegProcessHandle> sidecars = sidecarHandles.remove(taskId);
        log.info("stream runtime stopping: taskId={}, hasHandle={}", taskId, handle != null);
        if (handle != null && handle.isAlive()) {
            handle.destroy();
        }
        if (sidecars != null) {
            sidecars.stream()
                    .filter(FfmpegProcessHandle::isAlive)
                    .forEach(FfmpegProcessHandle::destroy);
        }
        return snapshot(taskId, TaskStatus.STOPPED, "ffmpeg process stopped");
    }

    public StreamTaskSnapshot status(Long taskId) {
        FfmpegProcessHandle handle = handles.get(taskId);
        if (handle == null) {
            log.info("stream runtime status checked: taskId={}, hasHandle=false", taskId);
            return snapshot(taskId, TaskStatus.STOPPED, "no active stream process");
        }
        log.info("stream runtime status checked: taskId={}, hasHandle=true, alive={}", taskId, handle.isAlive());
        return snapshot(taskId, handle.isAlive() ? TaskStatus.RUNNING : TaskStatus.FAILED, "runtime status checked");
    }

    private StreamTaskSnapshot snapshot(Long taskId, TaskStatus status, String message) {
        return new StreamTaskSnapshot(taskId, status, LocalDateTime.now(), message);
    }
}
