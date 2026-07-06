package com.cv.simulator.videoosd.v1.model;

import java.time.LocalDateTime;

public class MappedOsdMessage {

    private final OsdWebSocketPayload payload;
    private final Long mqttTimestamp;
    private final LocalDateTime publishTime;

    public MappedOsdMessage(OsdWebSocketPayload payload, Long mqttTimestamp, LocalDateTime publishTime) {
        this.payload = payload;
        this.mqttTimestamp = mqttTimestamp;
        this.publishTime = publishTime;
    }

    public OsdWebSocketPayload getPayload() {
        return payload;
    }

    public Long getMqttTimestamp() {
        return mqttTimestamp;
    }

    public LocalDateTime getPublishTime() {
        return publishTime;
    }
}
