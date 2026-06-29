package com.cv.simulator.videoosd.sample.service;

import com.cv.simulator.videoosd.sample.pojo.entity.DeviceTelemetryEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PublishTimeReplayExecutorTest {

    @Test
    void replaysRowsUsingPublishTimeGap() {
        PublishTimeReplayExecutor executor = new PublishTimeReplayExecutor();
        LocalDateTime baseTime = LocalDateTime.of(2026, 6, 29, 10, 0, 0);
        List<Long> slept = new ArrayList<>();
        List<Long> sentIds = new ArrayList<>();

        int sent = executor.replay(List.of(
                row(1L, baseTime),
                row(2L, baseTime.plusSeconds(1)),
                row(3L, baseTime.plusSeconds(3))
        ), row -> sentIds.add(row.getId()), slept::add);

        assertEquals(3, sent);
        assertEquals(List.of(1L, 2L, 3L), sentIds);
        assertEquals(List.of(1000L, 2000L), slept);
    }

    @Test
    void skipsSleepForSamePublishTime() {
        PublishTimeReplayExecutor executor = new PublishTimeReplayExecutor();
        LocalDateTime baseTime = LocalDateTime.of(2026, 6, 29, 10, 0, 0);
        List<Long> slept = new ArrayList<>();
        List<Long> sentIds = new ArrayList<>();

        int sent = executor.replay(List.of(
                row(1L, baseTime),
                row(2L, baseTime),
                row(3L, baseTime)
        ), row -> sentIds.add(row.getId()), slept::add);

        assertEquals(3, sent);
        assertEquals(List.of(1L, 2L, 3L), sentIds);
        assertEquals(List.of(), slept);
    }

    @Test
    void doesNotSleepOnNegativeGap() {
        PublishTimeReplayExecutor executor = new PublishTimeReplayExecutor();
        LocalDateTime baseTime = LocalDateTime.of(2026, 6, 29, 10, 0, 0);
        List<Long> slept = new ArrayList<>();
        List<Long> sentIds = new ArrayList<>();

        int sent = executor.replay(List.of(
                row(1L, baseTime.plusSeconds(2)),
                row(2L, baseTime)
        ), row -> sentIds.add(row.getId()), slept::add);

        assertEquals(2, sent);
        assertEquals(List.of(1L, 2L), sentIds);
        assertEquals(List.of(), slept);
    }

    @Test
    void sendsSingleRowWithoutSleep() {
        PublishTimeReplayExecutor executor = new PublishTimeReplayExecutor();
        LocalDateTime baseTime = LocalDateTime.of(2026, 6, 29, 10, 0, 0);
        List<Long> slept = new ArrayList<>();
        List<Long> sentIds = new ArrayList<>();

        int sent = executor.replay(List.of(row(1L, baseTime)), row -> sentIds.add(row.getId()), slept::add);

        assertEquals(1, sent);
        assertEquals(List.of(1L), sentIds);
        assertEquals(List.of(), slept);
    }

    @Test
    void ignoresRowsWithoutPublishTime() {
        PublishTimeReplayExecutor executor = new PublishTimeReplayExecutor();
        List<Long> slept = new ArrayList<>();
        List<Long> sentIds = new ArrayList<>();

        int sent = executor.replay(List.of(row(1L, null)), row -> sentIds.add(row.getId()), slept::add);

        assertEquals(0, sent);
        assertEquals(List.of(), sentIds);
        assertEquals(List.of(), slept);
    }

    private DeviceTelemetryEntity row(Long id, LocalDateTime publishTime) {
        DeviceTelemetryEntity entity = new DeviceTelemetryEntity();
        entity.setId(id);
        entity.setPublishTime(publishTime);
        return entity;
    }
}
