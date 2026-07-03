package com.cv.simulator.videoosd.v1.mqtt;

import com.cv.simulator.videoosd.v1.model.DeviceTelemetryRecord;

public interface MqttOsdMessageHandler {

    void handle(DeviceTelemetryRecord record, long startedAtNanos);
}
