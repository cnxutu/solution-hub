package com.cv.simulator.videoosd.core.mqtt;

public interface MqttOsdSubscriberSession extends AutoCloseable {

    @Override
    void close();
}
