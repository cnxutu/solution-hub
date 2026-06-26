package com.cv.simulator.videoosd.core.osd;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OsdExcelImporterTest {

    @Test
    void importsTabularTelemetryRows() {
        String content = "device_sn,latitude,longitude,raw_json\n"
                + "dock-001,30.1,120.2,\"{\"\"battery\"\":{\"\"percent\"\":90}}\"\n";

        List<DeviceTelemetryRecord> records = new OsdExcelImporter().importCsvLike(
                new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));

        assertEquals(1, records.size());
        assertEquals("dock-001", records.get(0).getDeviceSn());
        assertEquals("30.1", records.get(0).getLatitude().toPlainString());
        assertEquals("{\"battery\":{\"percent\":90}}", records.get(0).getRawJson());
    }

    @Test
    void rejectsRowsWithoutDeviceSn() {
        String content = "device_sn,latitude\n,30.1\n";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new OsdExcelImporter().importCsvLike(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))));

        assertEquals("device_sn is required at row 2", exception.getMessage());
    }
}
