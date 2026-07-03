package com.cv.simulator.videoosd.v1.bootstrap;

import com.cv.simulator.videoosd.v1.config.SimulatorOsdProperties;
import com.cv.simulator.videoosd.v1.service.MqttOsdRelayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class MqttDirectConsumeBootstrap {

    private static final Logger log = LoggerFactory.getLogger(MqttDirectConsumeBootstrap.class);

    private final MqttOsdRelayService relayService;
    private final SimulatorOsdProperties properties;

    public MqttDirectConsumeBootstrap(MqttOsdRelayService relayService, SimulatorOsdProperties properties) {
        this.relayService = relayService;
        this.properties = properties;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startIfEnabled() {
        if (!properties.isEnabled()) {
            return;
        }
        if (!properties.getMqtt().isDirectConsumeEnabled()) {
            return;
        }
        log.info("MQTT_TRACE [DIRECT_CONSUME_ENABLED] topic={}", properties.getMqtt().getTopic());
        relayService.startDirectRealtimeMqtt();
    }

    @PreDestroy
    public void stopDirectConsume() {
        if (!properties.isEnabled()) {
            return;
        }
        if (!properties.getMqtt().isDirectConsumeEnabled()) {
            return;
        }
        relayService.stop();
    }
}
