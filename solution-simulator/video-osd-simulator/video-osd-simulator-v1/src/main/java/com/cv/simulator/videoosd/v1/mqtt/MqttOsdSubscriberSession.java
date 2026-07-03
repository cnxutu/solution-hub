package com.cv.simulator.videoosd.v1.mqtt;

public interface MqttOsdSubscriberSession extends AutoCloseable {

    @Override
    void close();
}
