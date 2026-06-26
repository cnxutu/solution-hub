package com.cv.simulator.videoosd.core.osd;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

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
}
