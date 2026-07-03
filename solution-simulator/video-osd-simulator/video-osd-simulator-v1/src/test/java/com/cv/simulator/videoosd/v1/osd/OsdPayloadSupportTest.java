package com.cv.simulator.videoosd.v1.osd;

import com.cv.simulator.videoosd.v1.model.DeviceTelemetryRecord;
import com.cv.simulator.videoosd.v1.model.GeoPoint;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OsdPayloadSupportTest {

    private final OsdPayloadSupport support = new OsdPayloadSupport();

    @Test
    void mergesStructuredFieldsWithRawJson() {
        DeviceTelemetryRecord record = new DeviceTelemetryRecord();
        record.setTaskId(10L);
        record.setDeviceSn("dock-001");
        record.setLatitude(new BigDecimal("30.1234567890123"));
        record.setLongitude(new BigDecimal("120.1234567890123"));
        record.setRawJson("{\"mode_code\":5,\"custom\":\"ok\"}");

        String json = support.toPayloadJson(record);

        assertEquals("{\"mode_code\":5,\"custom\":\"ok\",\"task_id\":10,\"device_sn\":\"dock-001\",\"latitude\":30.1234567890123,\"longitude\":120.1234567890123}", json);
    }

    @Test
    void rejectsInvalidRawJson() {
        DeviceTelemetryRecord record = new DeviceTelemetryRecord();
        record.setDeviceSn("dock-001");
        record.setRawJson("{broken");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> support.toPayloadJson(record));

        assertEquals("raw_json must be a valid JSON object", exception.getMessage());
    }

    @Test
    void includesFrameCenterAndCornersWhenPresent() {
        DeviceTelemetryRecord record = new DeviceTelemetryRecord();
        record.setDeviceSn("dock-009");
        record.setFrameCenter(new GeoPoint(new BigDecimal("30.1856666496194"), new BigDecimal("120.1979761985018")));
        record.setCorners(Arrays.asList(
                new GeoPoint(new BigDecimal("30.1857000000000"), new BigDecimal("120.1980000000000")),
                new GeoPoint(new BigDecimal("30.1857000000000"), new BigDecimal("120.1981000000000")),
                new GeoPoint(new BigDecimal("30.1856000000000"), new BigDecimal("120.1981000000000")),
                new GeoPoint(new BigDecimal("30.1856000000000"), new BigDecimal("120.1980000000000"))
        ));

        String json = support.toPayloadJson(record);

        assertEquals("{\"device_sn\":\"dock-009\",\"frame_center\":{\"lat\":30.1856666496194,\"lon\":120.1979761985018},\"corners\":[{\"lat\":30.1857,\"lon\":120.198},{\"lat\":30.1857,\"lon\":120.1981},{\"lat\":30.1856,\"lon\":120.1981},{\"lat\":30.1856,\"lon\":120.198}]}", json);
    }

    @Test
    void serializesDateTimeFieldsAsIsoStrings() {
        DeviceTelemetryRecord record = new DeviceTelemetryRecord();
        record.setDeviceSn("dock-010");
        record.setPublishTime(LocalDateTime.of(2026, 7, 1, 10, 13, 25));
        record.setCreateTime(LocalDateTime.of(2026, 7, 1, 10, 13, 26));
        record.setUpdateTime(LocalDateTime.of(2026, 7, 1, 10, 13, 27));

        String json = support.toPayloadJson(record);

        assertEquals("{\"device_sn\":\"dock-010\",\"publish_time\":\"2026-07-01T10:13:25\",\"create_time\":\"2026-07-01T10:13:26\",\"update_time\":\"2026-07-01T10:13:27\"}", json);
    }
}
