package com.cv.simulator.videoosd.core.runtime;

import com.cv.simulator.videoosd.core.enums.TaskStatus;

import java.time.LocalDateTime;

public class StreamTaskSnapshot {

    private final Long taskId;
    private final TaskStatus status;
    private final LocalDateTime snapshotTime;
    private final String message;

    public StreamTaskSnapshot(Long taskId, TaskStatus status, LocalDateTime snapshotTime, String message) {
        this.taskId = taskId;
        this.status = status;
        this.snapshotTime = snapshotTime;
        this.message = message;
    }

    public Long getTaskId() {
        return taskId;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public LocalDateTime getSnapshotTime() {
        return snapshotTime;
    }

    public String getMessage() {
        return message;
    }
}
