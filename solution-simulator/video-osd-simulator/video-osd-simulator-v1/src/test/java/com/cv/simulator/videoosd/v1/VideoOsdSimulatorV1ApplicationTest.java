package com.cv.simulator.videoosd.v1;

import com.cv.simulator.videoosd.v1.config.SimulatorOsdProperties;
import com.cv.simulator.videoosd.v1.websocket.OsdBroadcastWebSocketHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(
        classes = {VideoOsdSimulatorV1Application.class},
        properties = {
                "server.port=0",
                "simulator.osd.mqtt.direct-consume-enabled=false",
                "simulator.osd.mqtt.broker-url=tcp://127.0.0.1:2883",
                "simulator.osd.mqtt.client-id=test-client",
                "simulator.osd.mqtt.topic=test/topic"
        }
)
class VideoOsdSimulatorV1ApplicationTest {

    @Autowired
    private SimulatorOsdProperties properties;

    @Autowired
    private OsdBroadcastWebSocketHandler osdBroadcastWebSocketHandler;

    @Test
    void contextLoadsAndBindsOverriddenProperties() {
        assertNotNull(osdBroadcastWebSocketHandler);
        assertEquals("tcp://127.0.0.1:2883", properties.getMqtt().getBrokerUrl());
        assertEquals("test-client", properties.getMqtt().getClientId());
        assertEquals("test/topic", properties.getMqtt().getTopic());
        assertEquals("/ws/osd", properties.getWebsocketPath());
        assertEquals(5000L, properties.getWsDropLogIntervalMillis());
    }
}
