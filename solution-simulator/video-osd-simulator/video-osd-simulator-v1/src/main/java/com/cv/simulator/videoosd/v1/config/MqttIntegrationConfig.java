package com.cv.simulator.videoosd.v1.config;

import com.cv.simulator.videoosd.v1.service.MqttOsdRelayService;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class MqttIntegrationConfig {

    @Bean
    public MqttPahoClientFactory mqttPahoClientFactory(SimulatorOsdProperties properties) {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{properties.getMqtt().getBrokerUrl()});
        options.setAutomaticReconnect(properties.getMqtt().isAutoReconnect());
        options.setCleanSession(properties.getMqtt().isCleanSession());
        if (hasText(properties.getMqtt().getUsername())) {
            options.setUserName(properties.getMqtt().getUsername());
        }
        if (properties.getMqtt().getPassword() != null) {
            options.setPassword(properties.getMqtt().getPassword().toCharArray());
        }
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer mqttInboundAdapter(SimulatorOsdProperties properties,
                                              MqttPahoClientFactory mqttPahoClientFactory,
                                              MessageChannel mqttInputChannel) {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                properties.getMqtt().getClientId(),
                mqttPahoClientFactory,
                properties.getMqtt().getTopic());
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(properties.getMqtt().getQos());
        adapter.setCompletionTimeout(5000L);
        adapter.setOutputChannel(mqttInputChannel);
        adapter.setAutoStartup(properties.isEnabled() && properties.getMqtt().isDirectConsumeEnabled());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler mqttInboundHandler(final MqttOsdRelayService relayService) {
        return message -> relayService.handleInboundMessage(
                String.valueOf(message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC)),
                String.valueOf(message.getPayload()));
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
