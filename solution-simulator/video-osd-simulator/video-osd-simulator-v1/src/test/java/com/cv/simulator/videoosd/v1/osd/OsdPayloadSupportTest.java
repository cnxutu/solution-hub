package com.cv.simulator.videoosd.v1.osd;

import com.cv.simulator.videoosd.v1.model.GeoPoint;
import com.cv.simulator.videoosd.v1.model.OsdWebSocketPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OsdPayloadSupportTest {

    private final OsdPayloadSupport support = new OsdPayloadSupport();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void serializesOnlyCompactWsFields() throws Exception {
        OsdWebSocketPayload payload = new OsdWebSocketPayload();
        payload.setTimestamp(1782972590947L);
        payload.setAttitudeHead(87.0F);
        payload.setLatitude(new BigDecimal("30.1234567890123"));
        payload.setLongitude(new BigDecimal("120.1234567890123"));
        payload.setHeight(100.2F);
        payload.setSpeedX(1.0F);
        payload.setSpeedY(2.0F);
        payload.setSpeedZ(3.0F);
        payload.setGimbalPitch(-0.1D);
        payload.setGimbalRoll(1.5D);
        payload.setGimbalYaw(87.3D);
        payload.setFrameCenter(new GeoPoint(new BigDecimal("30.1856666496194"), new BigDecimal("120.1979761985018")));
        payload.setCorners(Arrays.asList(
                new GeoPoint(new BigDecimal("30.1857000000000"), new BigDecimal("120.1980000000000")),
                new GeoPoint(new BigDecimal("30.1857000000000"), new BigDecimal("120.1981000000000")),
                new GeoPoint(new BigDecimal("30.1856000000000"), new BigDecimal("120.1981000000000")),
                new GeoPoint(new BigDecimal("30.1856000000000"), new BigDecimal("120.1980000000000"))
        ));

        String json = support.toPayloadJson(payload);

        assertEquals(
                objectMapper.readTree("{\"timestamp\":1782972590947,\"attitude_head\":87.0,\"latitude\":30.1234567890123,\"longitude\":120.1234567890123,\"height\":100.2,\"speed_x\":1.0,\"speed_y\":2.0,\"speed_z\":3.0,\"gimbal_pitch\":-0.1,\"gimbal_roll\":1.5,\"gimbal_yaw\":87.3,\"frame_center\":{\"lat\":30.1856666496194,\"lon\":120.1979761985018},\"corners\":[{\"lat\":30.1857,\"lon\":120.198},{\"lat\":30.1857,\"lon\":120.1981},{\"lat\":30.1856,\"lon\":120.1981},{\"lat\":30.1856,\"lon\":120.198}]}"),
                objectMapper.readTree(json));
    }
}
