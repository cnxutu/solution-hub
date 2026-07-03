package com.cv.simulator.videoosd.v1.osd;

import com.cv.simulator.videoosd.v1.model.DeviceTelemetryRecord;
import com.cv.simulator.videoosd.v1.model.GeoPoint;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;

public class OsdFrameGeometryCalculator {

    private static final double METERS_PER_DEGREE_LAT = 111_320.0D;
    private static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;

    public void populateFrameGeometry(DeviceTelemetryRecord record, double hfovDeg, double vfovDeg) {
        if (record == null || record.getLatitude() == null || record.getLongitude() == null
                || record.getHeight() == null || record.getHeight().floatValue() <= 0F) {
            return;
        }

        BigDecimal centerLat = record.getLatitude();
        BigDecimal centerLon = record.getLongitude();
        record.setFrameCenter(new GeoPoint(centerLat, centerLon));

        double headingDeg = record.getAttitudeHead() == null ? 0D : record.getAttitudeHead().doubleValue();
        double halfWidthMeters = record.getHeight().doubleValue() * Math.tan(Math.toRadians(hfovDeg / 2.0D));
        double halfHeightMeters = record.getHeight().doubleValue() * Math.tan(Math.toRadians(vfovDeg / 2.0D));
        double headingRad = Math.toRadians(headingDeg);

        record.setCorners(Arrays.asList(
                rotateAndTranslate(centerLat, centerLon, -halfWidthMeters, halfHeightMeters, headingRad),
                rotateAndTranslate(centerLat, centerLon, halfWidthMeters, halfHeightMeters, headingRad),
                rotateAndTranslate(centerLat, centerLon, halfWidthMeters, -halfHeightMeters, headingRad),
                rotateAndTranslate(centerLat, centerLon, -halfWidthMeters, -halfHeightMeters, headingRad)
        ));
    }

    private GeoPoint rotateAndTranslate(BigDecimal centerLat,
                                        BigDecimal centerLon,
                                        double eastMeters,
                                        double northMeters,
                                        double headingRad) {
        double rotatedEast = eastMeters * Math.cos(headingRad) - northMeters * Math.sin(headingRad);
        double rotatedNorth = eastMeters * Math.sin(headingRad) + northMeters * Math.cos(headingRad);

        double latDegrees = rotatedNorth / METERS_PER_DEGREE_LAT;
        double metersPerDegreeLon = METERS_PER_DEGREE_LAT * Math.cos(Math.toRadians(centerLat.doubleValue()));
        double lonDegrees = rotatedEast / metersPerDegreeLon;

        return new GeoPoint(
                centerLat.add(BigDecimal.valueOf(latDegrees), MATH_CONTEXT),
                centerLon.add(BigDecimal.valueOf(lonDegrees), MATH_CONTEXT)
        );
    }
}
