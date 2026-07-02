package com.cv.simulator.videoosd.sample.service;

import com.cv.simulator.videoosd.core.osd.DeviceTelemetryRecord;
import com.cv.simulator.videoosd.core.osd.OsdFrameGeometryCalculator;
import com.cv.simulator.videoosd.sample.pojo.sqlite.SqliteOsdSampleRow;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SqliteOsdSampleRecordMapperTest {

    @Test
    void mapsSqliteRowToTelemetryRecordWithFrameGeometry() {
        SqliteOsdSampleRecordMapper mapper = new SqliteOsdSampleRecordMapper(new OsdFrameGeometryCalculator());
        SqliteOsdSampleRow row = new SqliteOsdSampleRow();
        row.setId(1L);
        row.setMessageTimestampMs(1782976544131L);
        row.setDroneSn("1581F8HGX255D00A0DJQ");
        row.setAttitudeHead(87.2D);
        row.setLatitude(30.185666333377757D);
        row.setLongitude(120.19797618637993D);
        row.setHeight(122.29833374023438D);
        row.setSpeedZ(-6D);
        row.setRawJson("{\"sample\":true}");

        DeviceTelemetryRecord record = mapper.map(row, 60.0D, 40.0D);

        assertEquals("1581F8HGX255D00A0DJQ", record.getDeviceSn());
        assertEquals(87.2D, record.getAttitudeHead().doubleValue(), 0.0001D);
        assertEquals(30.185666333377757D, record.getLatitude().doubleValue());
        assertEquals(120.19797618637993D, record.getLongitude().doubleValue());
        assertEquals(122.29833D, record.getHeight(), 0.0001D);
        assertEquals(-6D, record.getVerticalSpeed().doubleValue(), 0.0001D);
        assertEquals("{\"sample\":true}", record.getRawJson());
        assertNotNull(record.getPublishTime());
        assertNotNull(record.getFrameCenter());
        assertEquals(4, record.getCorners().size());
    }
}
