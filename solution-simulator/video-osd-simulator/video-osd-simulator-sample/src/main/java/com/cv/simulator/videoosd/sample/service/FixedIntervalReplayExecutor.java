package com.cv.simulator.videoosd.sample.service;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.LongConsumer;

@Component
public class FixedIntervalReplayExecutor {

    public int replay(List<String> payloads,
                      Consumer<String> sender,
                      LongConsumer sleeper,
                      long intervalMillis) {
        int sent = 0;
        for (int index = 0; index < payloads.size(); index++) {
            if (index > 0 && intervalMillis > 0) {
                sleeper.accept(intervalMillis);
            }
            sender.accept(payloads.get(index));
            sent++;
        }
        return sent;
    }
}
