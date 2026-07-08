package com.cv.simulator.videoosd.v1.osd;

import com.cv.simulator.videoosd.v1.model.GeoPoint;
import com.cv.simulator.videoosd.v1.model.OsdWebSocketPayload;

import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class OsdFrameGeometryCalculator {

    private static final double METERS_PER_DEGREE_LAT = 111_320.0D;
    private static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;

    public void populateFrameGeometry(OsdWebSocketPayload payload, double hfovDeg, double vfovDeg) {
        if (payload == null || payload.getLatitude() == null || payload.getLongitude() == null
                || payload.getHeight() == null || payload.getHeight().floatValue() <= 0F) {
            return;
        }

        BigDecimal centerLat = payload.getLatitude();
        BigDecimal centerLon = payload.getLongitude();
        payload.setFrameCenter(new GeoPoint(centerLat, centerLon));

        double headingDeg = resolveViewYaw(payload);
        double halfWidthMeters = payload.getHeight().doubleValue() * Math.tan(Math.toRadians(hfovDeg / 2.0D));
        double halfHeightMeters = payload.getHeight().doubleValue() * Math.tan(Math.toRadians(vfovDeg / 2.0D));
        List<GeoPoint> generatedCorners = Arrays.asList(
                translateByView(centerLat, centerLon, -halfWidthMeters, halfHeightMeters, headingDeg),
                translateByView(centerLat, centerLon, halfWidthMeters, halfHeightMeters, headingDeg),
                translateByView(centerLat, centerLon, halfWidthMeters, -halfHeightMeters, headingDeg),
                translateByView(centerLat, centerLon, -halfWidthMeters, -halfHeightMeters, headingDeg)
        );
        payload.setCorners(normalizeCornerOrder(generatedCorners, centerLat, centerLon, headingDeg));
    }

    private double resolveViewYaw(OsdWebSocketPayload payload) {
        // 优先航线方向优先
        if (payload.getAttitudeHead() != null) {
            return payload.getAttitudeHead().doubleValue();
        }
        // 次优先选择：相机朝向
        if (payload.getGimbalYaw() != null) {
            return payload.getGimbalYaw().doubleValue();
        }
        return 0D;
    }

    private GeoPoint translateByView(BigDecimal centerLat,
                                     BigDecimal centerLon,
                                     double sideMeters,
                                     double frontMeters,
                                     double yawDeg) {
        double yawRad = Math.toRadians(yawDeg);
        double rotatedEast = sideMeters * Math.cos(yawRad) + frontMeters * Math.sin(yawRad);
        double rotatedNorth = frontMeters * Math.cos(yawRad) - sideMeters * Math.sin(yawRad);

        double latDegrees = rotatedNorth / METERS_PER_DEGREE_LAT;
        double metersPerDegreeLon = METERS_PER_DEGREE_LAT * Math.cos(Math.toRadians(centerLat.doubleValue()));
        double lonDegrees = rotatedEast / metersPerDegreeLon;

        return new GeoPoint(
                centerLat.add(BigDecimal.valueOf(latDegrees), MATH_CONTEXT),
                centerLon.add(BigDecimal.valueOf(lonDegrees), MATH_CONTEXT)
        );
    }

    private List<GeoPoint> normalizeCornerOrder(List<GeoPoint> corners,
                                                BigDecimal centerLat,
                                                BigDecimal centerLon,
                                                double yawDeg) {
        double yawRad = Math.toRadians(yawDeg);
        double cosLat = Math.cos(Math.toRadians(centerLat.doubleValue()));
        double forwardEast = Math.sin(yawRad);
        double forwardNorth = Math.cos(yawRad);
        double rightEast = Math.cos(yawRad);
        double rightNorth = -Math.sin(yawRad);

        List<ProjectedCorner> projectedCorners = new ArrayList<ProjectedCorner>(corners.size());
        for (GeoPoint corner : corners) {
            double east = (corner.getLon().doubleValue() - centerLon.doubleValue()) * METERS_PER_DEGREE_LAT * cosLat;
            double north = (corner.getLat().doubleValue() - centerLat.doubleValue()) * METERS_PER_DEGREE_LAT;
            double front = east * forwardEast + north * forwardNorth;
            double side = east * rightEast + north * rightNorth;
            projectedCorners.add(new ProjectedCorner(corner, front, side));
        }

        List<ProjectedCorner> top = new ArrayList<ProjectedCorner>(projectedCorners);
        top.sort(Comparator.comparingDouble(ProjectedCorner::getFront).reversed());
        top = new ArrayList<ProjectedCorner>(top.subList(0, 2));
        top.sort(Comparator.comparingDouble(ProjectedCorner::getSide));

        List<ProjectedCorner> bottom = new ArrayList<ProjectedCorner>(projectedCorners);
        bottom.sort(Comparator.comparingDouble(ProjectedCorner::getFront));
        bottom = new ArrayList<ProjectedCorner>(bottom.subList(0, 2));
        bottom.sort(Comparator.comparingDouble(ProjectedCorner::getSide));

        return Arrays.asList(
                top.get(0).getCorner(),
                top.get(1).getCorner(),
                bottom.get(1).getCorner(),
                bottom.get(0).getCorner()
        );
    }

    private static final class ProjectedCorner {
        private final GeoPoint corner;
        private final double front;
        private final double side;

        private ProjectedCorner(GeoPoint corner, double front, double side) {
            this.corner = corner;
            this.front = front;
            this.side = side;
        }

        private GeoPoint getCorner() {
            return corner;
        }

        private double getFront() {
            return front;
        }

        private double getSide() {
            return side;
        }
    }
}
