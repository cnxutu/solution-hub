package com.cv.simulator.videoosd.v1.mqtt;

import com.cv.simulator.videoosd.v1.model.MappedOsdMessage;
import com.cv.simulator.videoosd.v1.model.OsdWebSocketPayload;
import com.cv.simulator.videoosd.v1.osd.OsdFrameGeometryCalculator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MqttOsdRecordMapperTest {

    private final MqttOsdRecordMapper mapper = new MqttOsdRecordMapper(new OsdFrameGeometryCalculator());

    @Test
    void mapsRealtimePayloadIntoCompactWsPayload() {
        MappedOsdMessage mappedMessage = mapper.map(samplePayload(), 60.0, 40.0);
        OsdWebSocketPayload payload = mappedMessage.getPayload();

        assertEquals(Long.valueOf(1782972590947L), mappedMessage.getMqttTimestamp());
        assertEquals("2026-07-02T14:09:50.947", mappedMessage.getPublishTime().toString());
        assertEquals(87.0F, payload.getAttitudeHead());
        assertEquals(-0.1D, payload.getGimbalPitch());
        assertEquals(0.0F, payload.getSpeedX());
        assertEquals(30.18566738053386D, payload.getFrameCenter().getLat().doubleValue());
        assertEquals(4, payload.getCorners().size());
    }

    @Test
    void rejectsInvalidJsonPayload() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> mapper.map("{broken", 60.0, 40.0));

        assertEquals("mqtt osd payload must be valid json object", exception.getMessage());
    }

    @Test
    void rejectsPayloadWithoutDataObject() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> mapper.map("{\"timestamp\":1782972590947}", 60.0, 40.0));

        assertEquals("mqtt osd payload must contain data object", exception.getMessage());
    }

    private String samplePayload() {
        return "{\"data\":{\"attitude_head\":87,\"elevation\":59.7,\"gimbal_pitch\":-0.1,"
                + "\"gimbal_roll\":1.5,\"gimbal_yaw\":87.3602828699535,\"height\":100.24636383056641,"
                + "\"home_distance\":0.0902225822210312,\"horizontal_speed\":0,\"latitude\":30.18566738053386,"
                + "\"longitude\":120.19797559348879,\"speed_x\":0,\"speed_y\":0,\"speed_z\":1,"
                + "\"ultrasonic_height\":-1,\"vertical_speed\":-1,\"wind_direction\":4,\"wind_speed\":40},"
                + "\"method\":\"osd_info_push\",\"seq\":4392,\"timestamp\":1782972590947}";
    }
}
