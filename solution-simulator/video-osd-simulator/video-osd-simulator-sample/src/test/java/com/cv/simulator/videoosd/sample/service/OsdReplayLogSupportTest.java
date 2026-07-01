package com.cv.simulator.videoosd.sample.service;

import com.cv.simulator.videoosd.sample.pojo.entity.DeviceTelemetryEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OsdReplayLogSupportTest {

    @Test
    void summarizesReplayRowsWithFirstAndLastPublishTime() {
        List<DeviceTelemetryEntity> rows = List.of(
                row(1L, "drone-001", LocalDateTime.of(2026, 7, 1, 10, 13, 23)),
                row(2L, "drone-001", LocalDateTime.of(2026, 7, 1, 10, 13, 25))
        );

        String summary = OsdReplayLogSupport.summarizeRows(rows);

        assertEquals("rowCount=2, publishTimeReadyCount=2, firstId=1, firstPublishTime=2026-07-01T10:13:23, lastId=2, lastPublishTime=2026-07-01T10:13:25", summary);
    }

    @Test
    void truncatesPayloadPreviewToAvoidHugeLogs() {
        String payload = "{\"device_sn\":\"drone-001\",\"payload\":\"abcdefghijklmnopqrstuvwxyz\"}";
        String preview = OsdReplayLogSupport.payloadPreview(payload, 32);

        assertEquals(payload.substring(0, 32) + "...(" + payload.length() + " chars)", preview);
    }

    @Test
    void buildsStableLogMarker() {
        assertEquals("OSD_TRACE [MYSQL_QUERY]", OsdReplayLogSupport.marker("MYSQL_QUERY"));
    }

    private DeviceTelemetryEntity row(Long id, String deviceSn, LocalDateTime publishTime) {
        DeviceTelemetryEntity entity = new DeviceTelemetryEntity();
        entity.setId(id);
        entity.setDeviceSn(deviceSn);
        entity.setLatitude(new BigDecimal("30.1856666496194"));
        entity.setLongitude(new BigDecimal("120.1979761985018"));
        entity.setPublishTime(publishTime);
        return entity;
    }
}
