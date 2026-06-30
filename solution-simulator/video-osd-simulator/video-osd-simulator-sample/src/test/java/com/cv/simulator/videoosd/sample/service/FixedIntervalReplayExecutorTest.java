package com.cv.simulator.videoosd.sample.service;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FixedIntervalReplayExecutorTest {

    @Test
    void replaysPayloadsUsingFixedInterval() {
        FixedIntervalReplayExecutor executor = new FixedIntervalReplayExecutor();
        List<String> sent = new ArrayList<>();
        List<Long> slept = new ArrayList<>();

        int count = executor.replay(List.of("{\"a\":1}", "{\"a\":2}", "{\"a\":3}"), sent::add, slept::add, 1000L);

        assertEquals(3, count);
        assertEquals(List.of("{\"a\":1}", "{\"a\":2}", "{\"a\":3}"), sent);
        assertEquals(List.of(1000L, 1000L), slept);
    }

    @Test
    void doesNotSleepWhenSinglePayload() {
        FixedIntervalReplayExecutor executor = new FixedIntervalReplayExecutor();
        List<String> sent = new ArrayList<>();
        List<Long> slept = new ArrayList<>();

        int count = executor.replay(List.of("{\"a\":1}"), sent::add, slept::add, 1000L);

        assertEquals(1, count);
        assertEquals(List.of("{\"a\":1}"), sent);
        assertEquals(List.of(), slept);
    }

    @Test
    void ignoresEmptyPayloadList() {
        FixedIntervalReplayExecutor executor = new FixedIntervalReplayExecutor();
        List<String> sent = new ArrayList<>();
        List<Long> slept = new ArrayList<>();

        int count = executor.replay(List.of(), sent::add, slept::add, 1000L);

        assertEquals(0, count);
        assertEquals(List.of(), sent);
        assertEquals(List.of(), slept);
    }
}
