package com.cv.simulator.videoosd.core.mqtt;

public interface MqttOsdSubscriber {

    MqttOsdSubscriberSession subscribe(MqttOsdProperties properties, MqttOsdMessageHandler handler);
}
