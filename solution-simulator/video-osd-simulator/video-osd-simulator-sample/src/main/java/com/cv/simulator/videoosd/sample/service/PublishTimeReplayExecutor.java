package com.cv.simulator.videoosd.sample.service;

import com.cv.simulator.videoosd.sample.pojo.entity.DeviceTelemetryEntity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

public class PublishTimeReplayExecutor {

    public int replay(List<DeviceTelemetryEntity> rows,
                      Consumer<DeviceTelemetryEntity> sender,
                      LongConsumer sleeper) {
        if (rows == null || rows.isEmpty()) {
            return 0;
        }
        DeviceTelemetryEntity previous = null;
        int sent = 0;
        for (DeviceTelemetryEntity row : rows) {
            if (row.getPublishTime() == null) {
                continue;
            }
            if (previous != null) {
                long delayMillis = calculateDelayMillis(previous.getPublishTime(), row.getPublishTime());
                if (delayMillis > 0) {
                    sleeper.accept(delayMillis);
                }
            }
            sender.accept(row);
            previous = row;
            sent++;
        }
        return sent;
    }

    private long calculateDelayMillis(LocalDateTime previousTime, LocalDateTime currentTime) {
        return Duration.between(previousTime, currentTime).toMillis();
    }
}
