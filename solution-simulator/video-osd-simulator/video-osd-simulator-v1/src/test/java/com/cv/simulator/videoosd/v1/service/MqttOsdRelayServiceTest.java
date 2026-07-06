package com.cv.simulator.videoosd.v1.service;

import com.cv.simulator.videoosd.v1.config.SimulatorOsdProperties;
import com.cv.simulator.videoosd.v1.mqtt.MqttOsdRecordMapper;
import com.cv.simulator.videoosd.v1.osd.OsdFrameGeometryCalculator;
import com.cv.simulator.videoosd.v1.osd.OsdPayloadSupport;
import com.cv.simulator.videoosd.v1.websocket.WsPayloadEncoder;
import com.cv.simulator.videoosd.v1.websocket.OsdBroadcastWebSocketHandler;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MqttOsdRelayServiceTest {

    @Test
    void handlesInboundPayloadAndBroadcastsCompactJson() {
        SimulatorOsdProperties properties = new SimulatorOsdProperties();
        List<String> payloads = new ArrayList<String>();
        MqttOsdRelayService service = new MqttOsdRelayService(
                properties,
                new MqttOsdRecordMapper(new OsdFrameGeometryCalculator()),
                new OsdPayloadSupport(),
                new WsPayloadEncoder(properties.getWsCrypto()),
                new CapturingBroadcastHandler(payloads)
        );

        service.handleInboundMessage("thing/product/8UUXN4E00A05F5/drc/up", sampleMqttPayload());

        assertEquals(1, payloads.size());
        assertTrue(payloads.get(0).contains("\"timestamp\":1782972590947"));
        assertTrue(payloads.get(0).contains("\"attitude_head\":87.0"));
        assertTrue(payloads.get(0).contains("\"speed_x\":0.0"));
        assertTrue(payloads.get(0).contains("\"frame_center\""));
        assertTrue(payloads.get(0).contains("\"corners\""));
        assertTrue(!payloads.get(0).contains("\"wind_direction\""));
    }

    @Test
    void ignoresInvalidPayloadWithoutBroadcasting() {
        List<String> payloads = new ArrayList<String>();
        MqttOsdRelayService service = new MqttOsdRelayService(
                new SimulatorOsdProperties(),
                new MqttOsdRecordMapper(new OsdFrameGeometryCalculator()),
                new OsdPayloadSupport(),
                new WsPayloadEncoder(new SimulatorOsdProperties().getWsCrypto()),
                new CapturingBroadcastHandler(payloads)
        );

        service.handleInboundMessage("test/topic", "{broken");

        assertTrue(payloads.isEmpty());
    }

    @Test
    void ignoresHeartbeatPayloadWithoutLatitudeOrLongitude() {
        List<String> payloads = new ArrayList<String>();
        MqttOsdRelayService service = new MqttOsdRelayService(
                new SimulatorOsdProperties(),
                new MqttOsdRecordMapper(new OsdFrameGeometryCalculator()),
                new OsdPayloadSupport(),
                new WsPayloadEncoder(new SimulatorOsdProperties().getWsCrypto()),
                new CapturingBroadcastHandler(payloads)
        );

        service.handleInboundMessage("test/topic",
                "{\"data\":{\"height\":100.24636383056641,\"speed_x\":0,\"speed_y\":0,\"speed_z\":1},\"timestamp\":1782972590947}");

        assertTrue(payloads.isEmpty());
    }

    @Test
    void encryptsWsPayloadWhenWsCryptoEnabled() {
        SimulatorOsdProperties properties = new SimulatorOsdProperties();
        properties.getWsCrypto().setEnabled(true);
        properties.getWsCrypto().setMode("aes-gcm");
        properties.getWsCrypto().setKeyId("relay-key");
        properties.getWsCrypto().setKeyBase64("MDEyMzQ1Njc4OWFiY2RlZg==");
        List<String> payloads = new ArrayList<String>();
        MqttOsdRelayService service = new MqttOsdRelayService(
                properties,
                new MqttOsdRecordMapper(new OsdFrameGeometryCalculator()),
                new OsdPayloadSupport(),
                new WsPayloadEncoder(properties.getWsCrypto()),
                new CapturingBroadcastHandler(payloads)
        );

        service.handleInboundMessage("thing/product/8UUXN4E00A05F5/drc/up", sampleMqttPayload());

        assertEquals(1, payloads.size());
        assertTrue(payloads.get(0).contains("\"encrypted\":true"));
        assertTrue(payloads.get(0).contains("\"alg\":\"AES-GCM\""));
        assertTrue(payloads.get(0).contains("\"kid\":\"relay-key\""));
        assertTrue(!payloads.get(0).contains("\"attitude_head\":87.0"));
    }

    private static class CapturingBroadcastHandler extends OsdBroadcastWebSocketHandler {
        private final List<String> payloads;

        private CapturingBroadcastHandler(List<String> payloads) {
            super(1, 1000L, 1000L);
            this.payloads = payloads;
        }

        @Override
        public void broadcast(String payload) {
            payloads.add(payload);
        }
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
