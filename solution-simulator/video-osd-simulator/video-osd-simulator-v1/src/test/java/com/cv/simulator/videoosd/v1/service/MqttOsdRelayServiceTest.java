package com.cv.simulator.videoosd.v1.service;

import com.cv.simulator.videoosd.v1.config.SimulatorOsdProperties;
import com.cv.simulator.videoosd.v1.model.DeviceTelemetryRecord;
import com.cv.simulator.videoosd.v1.mqtt.MqttOsdMessageHandler;
import com.cv.simulator.videoosd.v1.mqtt.MqttOsdProperties;
import com.cv.simulator.videoosd.v1.mqtt.MqttOsdRecordMapper;
import com.cv.simulator.videoosd.v1.mqtt.MqttOsdSubscriber;
import com.cv.simulator.videoosd.v1.mqtt.MqttOsdSubscriberSession;
import com.cv.simulator.videoosd.v1.osd.OsdFrameGeometryCalculator;
import com.cv.simulator.videoosd.v1.osd.OsdPayloadSupport;
import com.cv.simulator.videoosd.v1.websocket.OsdBroadcastWebSocketHandler;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MqttOsdRelayServiceTest {

    @Test
    void subscribesAndBroadcastsMappedPayload() {
        CapturingMqttOsdSubscriber subscriber = new CapturingMqttOsdSubscriber();
        SimulatorOsdProperties properties = new SimulatorOsdProperties();
        properties.getMqtt().setTopic("thing/product/8UUXN4E00A05F5/drc/up");
        List<String> payloads = new ArrayList<String>();
        MqttOsdRelayService service = new MqttOsdRelayService(
                properties,
                subscriber,
                new OsdPayloadSupport(),
                new CapturingBroadcastHandler(payloads)
        );

        service.startDirectRealtimeMqtt();

        assertNotNull(subscriber.properties);
        assertEquals("thing/product/8UUXN4E00A05F5/drc/up", subscriber.properties.getTopic());
        assertEquals(1, payloads.size());
        assertTrue(payloads.get(0).contains("\"attitude_head\":87.0"));
        assertTrue(payloads.get(0).contains("\"frame_center\""));
    }

    @Test
    void stopClosesActiveSubscription() {
        CapturingMqttOsdSubscriber subscriber = new CapturingMqttOsdSubscriber();
        MqttOsdRelayService service = new MqttOsdRelayService(
                new SimulatorOsdProperties(),
                subscriber,
                new OsdPayloadSupport(),
                new CapturingBroadcastHandler(new ArrayList<String>())
        );

        service.startDirectRealtimeMqtt();
        service.stop();

        assertTrue(subscriber.closed);
    }

    private static class CapturingBroadcastHandler extends OsdBroadcastWebSocketHandler {
        private final List<String> payloads;

        private CapturingBroadcastHandler(List<String> payloads) {
            this.payloads = payloads;
        }

        @Override
        public void broadcast(String payload) {
            payloads.add(payload);
        }
    }

    private static final class CapturingMqttOsdSubscriber implements MqttOsdSubscriber {
        private MqttOsdProperties properties;
        private boolean closed;

        @Override
        public MqttOsdSubscriberSession subscribe(MqttOsdProperties properties, MqttOsdMessageHandler handler) {
            this.properties = properties;
            DeviceTelemetryRecord record = new MqttOsdRecordMapper(new OsdFrameGeometryCalculator())
                    .map(sampleMqttPayload(), 60.0, 40.0);
            handler.handle(record, System.nanoTime());
            return new MqttOsdSubscriberSession() {
                @Override
                public void close() {
                    closed = true;
                }
            };
        }

        private static String sampleMqttPayload() {
            return "{\"data\":{\"attitude_head\":87,\"elevation\":59.7,\"gimbal_pitch\":-0.1,"
                    + "\"gimbal_roll\":1.5,\"gimbal_yaw\":87.3602828699535,\"height\":100.24636383056641,"
                    + "\"home_distance\":0.0902225822210312,\"horizontal_speed\":0,\"latitude\":30.18566738053386,"
                    + "\"longitude\":120.19797559348879,\"speed_x\":0,\"speed_y\":0,\"speed_z\":1,"
                    + "\"ultrasonic_height\":-1,\"vertical_speed\":-1,\"wind_direction\":4,\"wind_speed\":40},"
                    + "\"method\":\"osd_info_push\",\"seq\":4392,\"timestamp\":1782972590947}";
        }
    }
}
