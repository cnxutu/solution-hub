package com.cv.simulator.videoosd.v1.bootstrap;

import com.cv.simulator.videoosd.v1.config.SimulatorOsdProperties;
import com.cv.simulator.videoosd.v1.service.MqttOsdRelayService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class MqttDirectConsumeBootstrapTest {

    @Test
    void startsAlwaysOnDirectConsumeWhenEnabled() {
        MqttOsdRelayService relayService = mock(MqttOsdRelayService.class);
        SimulatorOsdProperties properties = new SimulatorOsdProperties();
        properties.setEnabled(true);
        properties.getMqtt().setDirectConsumeEnabled(true);
        MqttDirectConsumeBootstrap bootstrap = new MqttDirectConsumeBootstrap(relayService, properties);

        bootstrap.startIfEnabled();

        verify(relayService).startDirectRealtimeMqtt();
    }

    @Test
    void doesNothingWhenDirectConsumeDisabled() {
        MqttOsdRelayService relayService = mock(MqttOsdRelayService.class);
        SimulatorOsdProperties properties = new SimulatorOsdProperties();
        properties.setEnabled(true);
        properties.getMqtt().setDirectConsumeEnabled(false);
        MqttDirectConsumeBootstrap bootstrap = new MqttDirectConsumeBootstrap(relayService, properties);

        bootstrap.startIfEnabled();

        verify(relayService, never()).startDirectRealtimeMqtt();
    }
}
