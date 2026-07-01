package com.cv.simulator.videoosd.core.mqtt;

import com.cv.simulator.videoosd.core.osd.DeviceTelemetryRecord;

import java.util.function.Consumer;

public interface MqttOsdSubscriber {

    MqttOsdSubscriberSession subscribe(MqttOsdProperties properties, Consumer<DeviceTelemetryRecord> consumer);
}
