package com.cv.simulator.videoosd.v1.mqtt;

public interface MqttOsdSubscriber {

    MqttOsdSubscriberSession subscribe(MqttOsdProperties properties, MqttOsdMessageHandler handler);
}
