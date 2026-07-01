package com.cv.simulator.videoosd.core.osd;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OsdFrameGeometryCalculatorTest {

    private final OsdFrameGeometryCalculator calculator = new OsdFrameGeometryCalculator();

    @Test
    void computesCenterAndFourCornersForNadirView() {
        DeviceTelemetryRecord record = baseRecord();

        calculator.populateFrameGeometry(record, 60.0, 40.0);

        assertNotNull(record.getFrameCenter());
        assertNotNull(record.getCorners());
        assertEquals(4, record.getCorners().size());
        assertEquals(record.getLatitude(), record.getFrameCenter().getLat());
        assertEquals(record.getLongitude(), record.getFrameCenter().getLon());
    }

    @Test
    void rotatesCornerLayoutByHeading() {
        DeviceTelemetryRecord northHeading = baseRecord();
        northHeading.setAttitudeHead(0F);
        DeviceTelemetryRecord eastHeading = baseRecord();
        eastHeading.setAttitudeHead(90F);

        calculator.populateFrameGeometry(northHeading, 60.0, 40.0);
        calculator.populateFrameGeometry(eastHeading, 60.0, 40.0);

        List<GeoPoint> northCorners = northHeading.getCorners();
        List<GeoPoint> eastCorners = eastHeading.getCorners();

        assertEquals(4, northCorners.size());
        assertEquals(4, eastCorners.size());
        assertNotSame(northCorners, eastCorners);
        assertTrue(northCorners.stream().map(GeoPoint::getLat).distinct().count() >= 2);
        assertTrue(eastCorners.stream().map(GeoPoint::getLon).distinct().count() >= 2);
    }

    @Test
    void skipsFrameGeometryWhenHeightMissingOrNonPositive() {
        DeviceTelemetryRecord noHeight = baseRecord();
        noHeight.setHeight(null);
        DeviceTelemetryRecord zeroHeight = baseRecord();
        zeroHeight.setHeight(0F);

        calculator.populateFrameGeometry(noHeight, 60.0, 40.0);
        calculator.populateFrameGeometry(zeroHeight, 60.0, 40.0);

        assertNull(noHeight.getFrameCenter());
        assertNull(noHeight.getCorners());
        assertNull(zeroHeight.getFrameCenter());
        assertNull(zeroHeight.getCorners());
    }

    private DeviceTelemetryRecord baseRecord() {
        DeviceTelemetryRecord record = new DeviceTelemetryRecord();
        record.setLatitude(new BigDecimal("30.1856666496194"));
        record.setLongitude(new BigDecimal("120.1979761985018"));
        record.setHeight(40.7109F);
        record.setAttitudeHead(15F);
        return record;
    }
}
