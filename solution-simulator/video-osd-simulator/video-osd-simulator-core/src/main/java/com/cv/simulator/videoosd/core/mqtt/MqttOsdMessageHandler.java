package com.cv.simulator.videoosd.core.mqtt;

import com.cv.simulator.videoosd.core.osd.DeviceTelemetryRecord;

@FunctionalInterface
public interface MqttOsdMessageHandler {

    void handle(DeviceTelemetryRecord record, long startedAtNanos);
}
