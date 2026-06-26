package com.cv.simulator.videoosd.core.runtime;

import com.cv.simulator.videoosd.core.enums.TaskStatus;
import com.cv.simulator.videoosd.core.ffmpeg.FfmpegProcessHandle;
import com.cv.simulator.videoosd.core.ffmpeg.FfmpegProcessLauncher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StreamTaskRuntime {

    private final FfmpegProcessLauncher processLauncher;
    private final Map<Long, FfmpegProcessHandle> handles = new ConcurrentHashMap<>();

    public StreamTaskRuntime(FfmpegProcessLauncher processLauncher) {
        this.processLauncher = processLauncher;
    }

    public StreamTaskSnapshot start(Long taskId, List<String> command) {
        FfmpegProcessHandle handle = processLauncher.launch(command);
        handles.put(taskId, handle);
        return snapshot(taskId, TaskStatus.RUNNING, "ffmpeg process started");
    }

    public StreamTaskSnapshot stop(Long taskId) {
        FfmpegProcessHandle handle = handles.remove(taskId);
        if (handle != null && handle.isAlive()) {
            handle.destroy();
        }
        return snapshot(taskId, TaskStatus.STOPPED, "ffmpeg process stopped");
    }

    public StreamTaskSnapshot status(Long taskId) {
        FfmpegProcessHandle handle = handles.get(taskId);
        if (handle == null) {
            return snapshot(taskId, TaskStatus.STOPPED, "no active ffmpeg process");
        }
        return snapshot(taskId, handle.isAlive() ? TaskStatus.RUNNING : TaskStatus.FAILED, "runtime status checked");
    }

    private StreamTaskSnapshot snapshot(Long taskId, TaskStatus status, String message) {
        return new StreamTaskSnapshot(taskId, status, LocalDateTime.now(), message);
    }
}
