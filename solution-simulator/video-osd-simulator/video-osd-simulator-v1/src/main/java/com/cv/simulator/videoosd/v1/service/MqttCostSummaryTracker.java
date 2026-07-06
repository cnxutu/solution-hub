package com.cv.simulator.videoosd.v1.service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;

class MqttCostSummaryTracker {

    private final long windowMillis;
    private final LongSupplier nowSupplier;

    private final AtomicLong windowStartMillis = new AtomicLong();
    private final AtomicLong count = new AtomicLong();
    private final AtomicLong totalDurationMillis = new AtomicLong();
    private final AtomicLong maxDurationMillis = new AtomicLong();
    private final AtomicLong overThresholdCount = new AtomicLong();

    MqttCostSummaryTracker(long windowMillis, LongSupplier nowSupplier) {
        this.windowMillis = windowMillis;
        this.nowSupplier = nowSupplier;
        this.windowStartMillis.set(nowSupplier.getAsLong());
    }

    void record(long durationMillis, boolean overThreshold) {
        count.incrementAndGet();
        totalDurationMillis.addAndGet(durationMillis);
        updateMax(durationMillis);
        if (overThreshold) {
            overThresholdCount.incrementAndGet();
        }
    }

    Optional<CostSummary> drainReadySummary() {
        long now = nowSupplier.getAsLong();
        long start = windowStartMillis.get();
        if (now - start < windowMillis || count.get() <= 0L) {
            return Optional.empty();
        }
        synchronized (this) {
            start = windowStartMillis.get();
            long currentCount = count.get();
            if (now - start < windowMillis || currentCount <= 0L) {
                return Optional.empty();
            }
            long total = totalDurationMillis.get();
            long max = maxDurationMillis.get();
            long overThreshold = overThresholdCount.get();
            windowStartMillis.set(now);
            count.set(0L);
            totalDurationMillis.set(0L);
            maxDurationMillis.set(0L);
            overThresholdCount.set(0L);
            return Optional.of(new CostSummary(
                    start,
                    now,
                    currentCount,
                    total / currentCount,
                    max,
                    overThreshold));
        }
    }

    private void updateMax(long durationMillis) {
        long current;
        do {
            current = maxDurationMillis.get();
            if (durationMillis <= current) {
                return;
            }
        } while (!maxDurationMillis.compareAndSet(current, durationMillis));
    }

    static final class CostSummary {
        private final long windowStartMillis;
        private final long windowEndMillis;
        private final long count;
        private final long avgDurationMillis;
        private final long maxDurationMillis;
        private final long overThresholdCount;

        CostSummary(long windowStartMillis,
                    long windowEndMillis,
                    long count,
                    long avgDurationMillis,
                    long maxDurationMillis,
                    long overThresholdCount) {
            this.windowStartMillis = windowStartMillis;
            this.windowEndMillis = windowEndMillis;
            this.count = count;
            this.avgDurationMillis = avgDurationMillis;
            this.maxDurationMillis = maxDurationMillis;
            this.overThresholdCount = overThresholdCount;
        }

        long getWindowStartMillis() {
            return windowStartMillis;
        }

        long getWindowEndMillis() {
            return windowEndMillis;
        }

        long getCount() {
            return count;
        }

        long getAvgDurationMillis() {
            return avgDurationMillis;
        }

        long getMaxDurationMillis() {
            return maxDurationMillis;
        }

        long getOverThresholdCount() {
            return overThresholdCount;
        }
    }
}
