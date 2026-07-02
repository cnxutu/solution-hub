package com.cv.simulator.videoosd.sample.service;

import com.cv.simulator.videoosd.sample.config.OsdSourceType;
import com.cv.simulator.videoosd.sample.config.SimulatorProperties;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class MqttDirectConsumeBootstrapTest {

    @Test
    void startsAlwaysOnDirectConsumeWhenEnabledForMqttSource() {
        DeviceTelemetryService telemetryService = mock(DeviceTelemetryService.class);
        SimulatorProperties properties = new SimulatorProperties();
        properties.getOsd().setSourceType(OsdSourceType.MQTT);
        properties.getOsd().getMqtt().setDirectConsumeEnabled(true);
        MqttDirectConsumeBootstrap bootstrap = new MqttDirectConsumeBootstrap(telemetryService, properties);

        bootstrap.startIfEnabled();

        verify(telemetryService).startDirectRealtimeMqtt();
    }

    @Test
    void doesNothingWhenDirectConsumeDisabled() {
        DeviceTelemetryService telemetryService = mock(DeviceTelemetryService.class);
        SimulatorProperties properties = new SimulatorProperties();
        properties.getOsd().setSourceType(OsdSourceType.MQTT);
        properties.getOsd().getMqtt().setDirectConsumeEnabled(false);
        MqttDirectConsumeBootstrap bootstrap = new MqttDirectConsumeBootstrap(telemetryService, properties);

        bootstrap.startIfEnabled();

        verify(telemetryService, never()).startDirectRealtimeMqtt();
    }
}
