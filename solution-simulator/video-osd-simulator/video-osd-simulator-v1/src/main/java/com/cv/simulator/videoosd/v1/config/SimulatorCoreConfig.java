package com.cv.simulator.videoosd.v1.config;

import com.cv.simulator.videoosd.v1.mqtt.MqttOsdRecordMapper;
import com.cv.simulator.videoosd.v1.osd.OsdFrameGeometryCalculator;
import com.cv.simulator.videoosd.v1.osd.OsdPayloadSupport;
import com.cv.simulator.videoosd.v1.websocket.OsdBroadcastWebSocketHandler;
import com.cv.simulator.videoosd.v1.websocket.WsPayloadEncoder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SimulatorOsdProperties.class)
public class SimulatorCoreConfig {

    @Bean
    public OsdFrameGeometryCalculator osdFrameGeometryCalculator() {
        return new OsdFrameGeometryCalculator();
    }

    @Bean
    public OsdPayloadSupport osdPayloadSupport() {
        return new OsdPayloadSupport();
    }

    @Bean
    public MqttOsdRecordMapper mqttOsdRecordMapper(OsdFrameGeometryCalculator calculator) {
        return new MqttOsdRecordMapper(calculator);
    }

    @Bean
    public WsPayloadEncoder wsPayloadEncoder(SimulatorOsdProperties properties) {
        return new WsPayloadEncoder(properties.getWsCrypto());
    }

    @Bean
    public OsdBroadcastWebSocketHandler osdBroadcastWebSocketHandler(SimulatorOsdProperties properties) {
        return new OsdBroadcastWebSocketHandler(
                properties.getWsSenderThreads(),
                properties.getWsDropLogIntervalMillis(),
                properties.getWsSendSlowThresholdMillis());
    }
}
