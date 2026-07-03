package com.cv.simulator.videoosd.v1.service;

import com.cv.simulator.videoosd.v1.config.SimulatorOsdProperties;
import com.cv.simulator.videoosd.v1.model.DeviceTelemetryRecord;
import com.cv.simulator.videoosd.v1.mqtt.MqttOsdProperties;
import com.cv.simulator.videoosd.v1.mqtt.MqttOsdSubscriber;
import com.cv.simulator.videoosd.v1.mqtt.MqttOsdSubscriberSession;
import com.cv.simulator.videoosd.v1.osd.OsdPayloadSupport;
import com.cv.simulator.videoosd.v1.websocket.OsdBroadcastWebSocketHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MqttOsdRelayService {

    private static final Logger log = LoggerFactory.getLogger(MqttOsdRelayService.class);
    private static final long MQTT_WS_WARN_THRESHOLD_MILLIS = 1_000L;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final SimulatorOsdProperties properties;
    private final MqttOsdSubscriber mqttOsdSubscriber;
    private final OsdPayloadSupport payloadSupport;
    private final OsdBroadcastWebSocketHandler osdBroadcastWebSocketHandler;

    private MqttOsdSubscriberSession session;

    public MqttOsdRelayService(SimulatorOsdProperties properties,
                               MqttOsdSubscriber mqttOsdSubscriber,
                               OsdPayloadSupport payloadSupport,
                               OsdBroadcastWebSocketHandler osdBroadcastWebSocketHandler) {
        this.properties = properties;
        this.mqttOsdSubscriber = mqttOsdSubscriber;
        this.payloadSupport = payloadSupport;
        this.osdBroadcastWebSocketHandler = osdBroadcastWebSocketHandler;
    }

    public synchronized void startDirectRealtimeMqtt() {
        stop();
        MqttOsdProperties mqttProperties = buildMqttProperties();
        log.info("MQTT_TRACE [SUBSCRIBE_START] brokerUrl={}, topic={}, qos={}",
                mqttProperties.getBrokerUrl(), mqttProperties.getTopic(), mqttProperties.getQos());
        session = mqttOsdSubscriber.subscribe(mqttProperties, (record, startedAtNanos) ->
                handleRealtimeMqttRecord(record, startedAtNanos));
        log.info("MQTT_TRACE [SUBSCRIBED] topic={}", mqttProperties.getTopic());
    }

    public synchronized void stop() {
        if (session == null) {
            return;
        }
        session.close();
        session = null;
        log.info("MQTT_TRACE [SUBSCRIBE_STOPPED]");
    }

    private MqttOsdProperties buildMqttProperties() {
        SimulatorOsdProperties.Mqtt mqtt = properties.getMqtt();
        MqttOsdProperties mqttProperties = new MqttOsdProperties();
        mqttProperties.setBrokerUrl(mqtt.getBrokerUrl());
        mqttProperties.setClientId(mqtt.getClientId());
        mqttProperties.setTopic(mqtt.getTopic());
        mqttProperties.setUsername(mqtt.getUsername());
        mqttProperties.setPassword(mqtt.getPassword());
        mqttProperties.setQos(mqtt.getQos());
        mqttProperties.setAutoReconnect(mqtt.isAutoReconnect());
        mqttProperties.setCleanSession(mqtt.isCleanSession());
        mqttProperties.setFrameHfovDeg(properties.getFrameHfovDeg());
        mqttProperties.setFrameVfovDeg(properties.getFrameVfovDeg());
        return mqttProperties;
    }

    private void handleRealtimeMqttRecord(DeviceTelemetryRecord record, long startedAtNanos) {
        String payload = payloadSupport.toPayloadJson(record);
        osdBroadcastWebSocketHandler.broadcast(payload);

        Long mqttTimestamp = extractMqttTimestamp(record.getRawJson());
        long durationMillis = (System.nanoTime() - startedAtNanos) / 1_000_000L;
        if (durationMillis > MQTT_WS_WARN_THRESHOLD_MILLIS) {
            log.warn("MQTT_TRACE [MQTT_TO_WS_COST] timestamp={}, publishTime={}, durationMillis={}, thresholdMillis={}",
                    mqttTimestamp, record.getPublishTime(), durationMillis, MQTT_WS_WARN_THRESHOLD_MILLIS);
            return;
        }
        log.info("MQTT_TRACE [MQTT_TO_WS_COST] timestamp={}, publishTime={}, durationMillis={}, thresholdMillis={}",
                mqttTimestamp, record.getPublishTime(), durationMillis, MQTT_WS_WARN_THRESHOLD_MILLIS);
    }

    private Long extractMqttTimestamp(String rawJson) {
        if (rawJson == null || rawJson.trim().isEmpty()) {
            return null;
        }
        try {
            JsonNode root = OBJECT_MAPPER.readTree(rawJson);
            return root.hasNonNull("timestamp") ? Long.valueOf(root.get("timestamp").asLong()) : null;
        } catch (Exception e) {
            log.debug("MQTT_TRACE [TIMESTAMP_PARSE_SKIPPED] reason={}", e.getMessage());
            return null;
        }
    }
}
