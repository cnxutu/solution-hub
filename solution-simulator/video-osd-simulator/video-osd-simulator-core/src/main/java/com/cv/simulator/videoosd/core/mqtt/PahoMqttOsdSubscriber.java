package com.cv.simulator.videoosd.core.mqtt;

import com.cv.simulator.videoosd.core.osd.DeviceTelemetryRecord;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class PahoMqttOsdSubscriber implements MqttOsdSubscriber {

    private final MqttOsdRecordMapper recordMapper;

    public PahoMqttOsdSubscriber(MqttOsdRecordMapper recordMapper) {
        this.recordMapper = recordMapper;
    }

    @Override
    public MqttOsdSubscriberSession subscribe(MqttOsdProperties properties, Consumer<DeviceTelemetryRecord> consumer) {
        validate(properties);
        try {
            String clientId = hasText(properties.getClientId()) ? properties.getClientId() : MqttClient.generateClientId();
            MqttClient client = new MqttClient(properties.getBrokerUrl(), clientId, new MemoryPersistence());
            client.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                }

                @Override
                public void connectionLost(Throwable cause) {
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                    DeviceTelemetryRecord record = recordMapper.map(
                            payload,
                            properties.getFrameHfovDeg(),
                            properties.getFrameVfovDeg());
                    consumer.accept(record);
                }

                @Override
                public void deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken token) {
                }
            });
            client.connect(buildOptions(properties));
            client.subscribe(properties.getTopic(), properties.getQos());
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
                client.unsubscribe(topic);
                client.disconnect();
            }
        } catch (MqttException e) {
            throw new IllegalStateException("failed to close mqtt osd subscriber", e);
        } finally {
            try {
                client.close();
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
}
