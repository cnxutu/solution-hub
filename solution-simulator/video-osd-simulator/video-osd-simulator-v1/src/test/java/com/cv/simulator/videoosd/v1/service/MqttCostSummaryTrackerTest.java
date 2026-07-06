package com.cv.simulator.videoosd.v1.service;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MqttCostSummaryTrackerTest {

    @Test
    void aggregatesWindowStatisticsAndFlushesAfterFiveSeconds() {
        AtomicLong now = new AtomicLong(1_000L);
        MqttCostSummaryTracker tracker = new MqttCostSummaryTracker(5_000L, now::get);

        tracker.record(2L, false);
        tracker.record(6L, true);
        now.set(5_999L);
        assertFalse(tracker.drainReadySummary().isPresent());

        now.set(6_000L);
        MqttCostSummaryTracker.CostSummary summary = tracker.drainReadySummary().get();

        assertEquals(1_000L, summary.getWindowStartMillis());
        assertEquals(6_000L, summary.getWindowEndMillis());
        assertEquals(2L, summary.getCount());
        assertEquals(4L, summary.getAvgDurationMillis());
        assertEquals(6L, summary.getMaxDurationMillis());
        assertEquals(1L, summary.getOverThresholdCount());
        assertFalse(tracker.drainReadySummary().isPresent());
    }

    @Test
    void doesNotFlushWhenWindowHasNoMessages() {
        AtomicLong now = new AtomicLong(1_000L);
        MqttCostSummaryTracker tracker = new MqttCostSummaryTracker(5_000L, now::get);

        now.set(6_000L);

        assertFalse(tracker.drainReadySummary().isPresent());
        tracker.record(1L, false);
        now.set(11_000L);
        assertTrue(tracker.drainReadySummary().isPresent());
    }
}
