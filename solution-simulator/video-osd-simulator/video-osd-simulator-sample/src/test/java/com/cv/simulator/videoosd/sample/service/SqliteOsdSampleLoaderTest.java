package com.cv.simulator.videoosd.sample.service;

import com.cv.simulator.videoosd.sample.config.SimulatorProperties;
import com.cv.simulator.videoosd.sample.pojo.sqlite.SqliteOsdSampleRow;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqliteOsdSampleLoaderTest {

    @Test
    void loadsRowsFromSqliteFileOrderedByMessageTimestamp() {
        SimulatorProperties properties = new SimulatorProperties();
        SqliteOsdSampleLoader loader = new SqliteOsdSampleLoader(new DefaultResourceLoader(), properties);

        List<SqliteOsdSampleRow> rows = loader.loadOrderedRows();

        assertFalse(rows.isEmpty());
        assertNotNull(rows.get(0).getMessageTimestampMs());
        assertTrue(rows.get(0).getMessageTimestampMs() <= rows.get(1).getMessageTimestampMs());
        assertEquals("8UUXN4E00A05F5", rows.get(0).getDockSn());
        assertEquals("1581F8HGX255D00A0DJQ", rows.get(0).getDroneSn());
    }
}
