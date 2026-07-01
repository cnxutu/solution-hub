package com.cv.simulator.videoosd.core.mqtt;

import com.cv.simulator.videoosd.core.osd.DeviceTelemetryRecord;
import com.cv.simulator.videoosd.core.osd.OsdFrameGeometryCalculator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MqttOsdRecordMapperTest {

    private final MqttOsdRecordMapper mapper = new MqttOsdRecordMapper(new OsdFrameGeometryCalculator());

    @Test
    void mapsOsdInfoPushPayloadToTelemetryRecord() {
        String json = """
                {"data":{"attitude_head":87,"elevation":59.7,"gimbal_pitch":-0.1,"gimbal_roll":1.5,"gimbal_yaw":87.3602828699535,"height":100.24636383056641,"home_distance":0.0902225822210312,"horizontal_speed":0,"latitude":30.18566738053386,"longitude":120.19797559348879,"speed_x":0,"speed_y":0,"speed_z":1,"ultrasonic_height":-1,"vertical_speed":-1,"wind_direction":4,"wind_speed":40},"method":"osd_info_push","seq":4392,"timestamp":1782972590947}
                """;

        DeviceTelemetryRecord record = mapper.map(json, 60.0, 40.0);

        assertEquals(87F, record.getAttitudeHead());
        assertEquals(59.7F, record.getElevation());
        assertEquals(100.24636F, record.getHeight());
        assertEquals(0.09022258F, record.getHomeDistance());
        assertEquals(0F, record.getHorizontalSpeed());
        assertEquals(new BigDecimal("30.18566738053386"), record.getLatitude());
        assertEquals(new BigDecimal("120.19797559348879"), record.getLongitude());
        assertEquals(-1F, record.getVerticalSpeed());
        assertEquals("4", record.getWindDirection());
        assertEquals(40F, record.getWindSpeed());
        assertNotNull(record.getPublishTime());
        assertNotNull(record.getFrameCenter());
        assertNotNull(record.getCorners());
        assertEquals(4, record.getCorners().size());
        assertTrue(record.getRawJson().contains("\"method\":\"osd_info_push\""));
        assertTrue(record.getRawJson().contains("\"gimbal_pitch\":-0.1"));
    }

    @Test
    void rejectsInvalidPayloadWithoutDataObject() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> mapper.map("{\"method\":\"osd_info_push\"}", 60.0, 40.0));

        assertEquals("mqtt osd payload must contain data object", exception.getMessage());
    }
}
