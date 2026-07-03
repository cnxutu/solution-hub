package com.cv.simulator.videoosd.core.mqtt;

import com.cv.simulator.videoosd.core.osd.DeviceTelemetryRecord;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
public class PahoMqttOsdSubscriber implements MqttOsdSubscriber {

    private static final String MQTT_TRACE_PREFIX = "MQTT_TRACE";
    private static final Logger log = LoggerFactory.getLogger(PahoMqttOsdSubscriber.class);
    private final MqttOsdRecordMapper recordMapper;

    public PahoMqttOsdSubscriber(MqttOsdRecordMapper recordMapper) {
        this.recordMapper = recordMapper;
    }

    @Override
    public MqttOsdSubscriberSession subscribe(MqttOsdProperties properties, MqttOsdMessageHandler handler) {
        validate(properties);
        try {
            String clientId = hasText(properties.getClientId()) ? properties.getClientId() : MqttClient.generateClientId();
            log.info("{} [CONNECTING] brokerUrl={}, clientId={}, topic={}, qos={}",
                    MQTT_TRACE_PREFIX, properties.getBrokerUrl(), clientId, properties.getTopic(), properties.getQos());
            MqttClient client = new MqttClient(properties.getBrokerUrl(), clientId, new MemoryPersistence());
            client.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    log.info("{} [CONNECTED] reconnect={}, serverUri={}, topic={}",
                            MQTT_TRACE_PREFIX, reconnect, serverURI, properties.getTopic());
                }

                @Override
                public void connectionLost(Throwable cause) {
                    log.error("{} [CONNECTION_LOST] brokerUrl={}, topic={}, message={}",
                            MQTT_TRACE_PREFIX, properties.getBrokerUrl(), properties.getTopic(),
                            cause == null ? "unknown" : cause.getMessage(), cause);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    long startedAtNanos = System.nanoTime();
                    String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                    try {
                        DeviceTelemetryRecord record = recordMapper.map(
                                payload,
                                properties.getFrameHfovDeg(),
                                properties.getFrameVfovDeg());
                        handler.handle(record, startedAtNanos);
                    } catch (RuntimeException e) {
                        log.error("{} [MESSAGE_DROPPED] topic={}, reason={}, payloadPreview={}",
                                MQTT_TRACE_PREFIX, topic, e.getMessage(), payloadPreview(payload, 300), e);
                    }
                }

                @Override
                public void deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken token) {
                }
            });
            client.connect(buildOptions(properties));
            client.subscribe(properties.getTopic(), properties.getQos());
            log.info("{} [SUBSCRIBED] brokerUrl={}, clientId={}, topic={}, qos={}",
                    MQTT_TRACE_PREFIX, properties.getBrokerUrl(), clientId, properties.getTopic(), properties.getQos());
            return () -> closeClient(client, properties.getTopic());
        } catch (MqttException e) {
            throw new IllegalStateException("failed to subscribe mqtt osd topic", e);
        }
    }

    private MqttConnectOptions buildOptions(MqttOsdProperties properties) {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(properties.isAutoReconnect());
        options.setCleanSession(properties.isCleanSession());
        if (hasText(properties.getUsername())) {
            options.setUserName(properties.getUsername());
        }
        if (properties.getPassword() != null) {
            options.setPassword(properties.getPassword().toCharArray());
        }
        return options;
    }

    private void closeClient(MqttClient client, String topic) {
        try {
            if (client.isConnected()) {
                log.info("{} [UNSUBSCRIBING] topic={}", MQTT_TRACE_PREFIX, topic);
                client.unsubscribe(topic);
                client.disconnect();
                log.info("{} [DISCONNECTED] topic={}", MQTT_TRACE_PREFIX, topic);
            }
        } catch (MqttException e) {
            throw new IllegalStateException("failed to close mqtt osd subscriber", e);
        } finally {
            try {
                client.close();
                log.info("{} [CLOSED] topic={}", MQTT_TRACE_PREFIX, topic);
            } catch (MqttException e) {
                throw new IllegalStateException("failed to release mqtt osd subscriber", e);
            }
        }
    }

    private void validate(MqttOsdProperties properties) {
        if (properties == null) {
            throw new IllegalArgumentException("mqtt osd properties must not be null");
        }
        if (!hasText(properties.getBrokerUrl())) {
            throw new IllegalArgumentException("mqtt broker url must not be blank");
        }
        if (!hasText(properties.getTopic())) {
            throw new IllegalArgumentException("mqtt topic must not be blank");
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String payloadPreview(String payload, int maxLength) {
        if (payload == null) {
            return "null";
        }
        if (payload.length() <= maxLength) {
            return payload;
        }
        return payload.substring(0, maxLength) + "...(" + payload.length() + " chars)";
    }
}
