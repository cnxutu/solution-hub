package com.cv.simulator.videoosd.sample.service;

import com.cv.simulator.videoosd.sample.config.OsdSourceType;
import com.cv.simulator.videoosd.sample.config.SimulatorProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
@Slf4j
public class MqttDirectConsumeBootstrap {

    private final DeviceTelemetryService telemetryService;
    private final SimulatorProperties properties;

    public MqttDirectConsumeBootstrap(DeviceTelemetryService telemetryService, SimulatorProperties properties) {
        this.telemetryService = telemetryService;
        this.properties = properties;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startIfEnabled() {
        if (properties.getOsd().getSourceType() != OsdSourceType.MQTT) {
            return;
        }
        if (!properties.getOsd().getMqtt().isDirectConsumeEnabled()) {
            return;
        }
        log.info("MQTT_TRACE [DIRECT_CONSUME_ENABLED] topic={}", properties.getOsd().getMqtt().getTopic());
        telemetryService.startDirectRealtimeMqtt();
    }

    @PreDestroy
    public void stopDirectConsume() {
        if (properties.getOsd().getSourceType() != OsdSourceType.MQTT) {
            return;
        }
        if (!properties.getOsd().getMqtt().isDirectConsumeEnabled()) {
            return;
        }
        telemetryService.stopReplay(DeviceTelemetryService.DIRECT_MQTT_SESSION_KEY);
    }
}
