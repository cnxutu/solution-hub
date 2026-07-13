package com.cv.simulator.videoosd.v1.osd;

import com.cv.simulator.videoosd.v1.model.GeoPointDTO;
import com.cv.simulator.videoosd.v1.model.GeoSpatialPointDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 将 DJI 云台姿态转换为前端可直接贴图的视锥截面。四角始终按相机画面语义输出：左上、右上、右下、左下。
 */
@Component
public class DeviceViewConeGeometryCalculator {

    public static final String CAMERA_CORNER_ORDER = "CAMERA_TL_TR_BR_BL";
    public static final int GEOMETRY_VERSION = 2;
    private static final double METERS_PER_DEGREE_LAT = 111_320.0D;
    private static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;
    private static final double STRAIGHT_DOWN_PITCH = -90.0D;
    private static final double MIN_GROUND_RAY_DOWN_COMPONENT = -1.0E-8D;
    private static final long MODE_STATE_TTL_MILLIS = TimeUnit.SECONDS.toMillis(120L);
    private static final long MODE_STATE_CLEANUP_INTERVAL_SECONDS = 60L;

    private final double frameHfovDeg;
    private final double frameVfovDeg;
    private final double pitchTransitionDeg;
    private final double pitchHysteresisDeg;
    private final long modeStateTtlMillis;
    private final double yawOffsetDeg;
    private final double rollOffsetDeg;
    private final double rollDirection;
    private final ConcurrentHashMap<String, ViewConeModeState> viewConeModes = new ConcurrentHashMap<>();

    @Autowired
    public DeviceViewConeGeometryCalculator(
            @Value("${inspection.iot.view-cone.frame-hfov-deg:60}") double frameHfovDeg,
            @Value("${inspection.iot.view-cone.frame-vfov-deg:40}") double frameVfovDeg,
            @Value("${inspection.iot.view-cone.pitch-transition-deg:-60}") double pitchTransitionDeg,
            @Value("${inspection.iot.view-cone.pitch-hysteresis-deg:1}") double pitchHysteresisDeg,
            @Value("${inspection.iot.view-cone.yaw-offset-deg:0}") double yawOffsetDeg,
            @Value("${inspection.iot.view-cone.roll-offset-deg:0}") double rollOffsetDeg,
            @Value("${inspection.iot.view-cone.roll-direction:1}") double rollDirection) {
        this(frameHfovDeg, frameVfovDeg, pitchTransitionDeg, pitchHysteresisDeg, MODE_STATE_TTL_MILLIS,
                yawOffsetDeg, rollOffsetDeg, rollDirection);
    }

    public DeviceViewConeGeometryCalculator(double frameHfovDeg, double frameVfovDeg) {
        this(frameHfovDeg, frameVfovDeg, -60.0D, 1.0D, MODE_STATE_TTL_MILLIS, 0.0D, 0.0D, 1.0D);
    }

    DeviceViewConeGeometryCalculator(double frameHfovDeg, double frameVfovDeg,
                                     double pitchTransitionDeg, double pitchHysteresisDeg,
                                     long modeStateTtlMillis) {
        this(frameHfovDeg, frameVfovDeg, pitchTransitionDeg, pitchHysteresisDeg, modeStateTtlMillis,
                0.0D, 0.0D, 1.0D);
    }

    DeviceViewConeGeometryCalculator(double frameHfovDeg, double frameVfovDeg,
                                     double pitchTransitionDeg, double pitchHysteresisDeg,
                                     long modeStateTtlMillis, double yawOffsetDeg,
                                     double rollOffsetDeg, double rollDirection) {
        this.frameHfovDeg = frameHfovDeg;
        this.frameVfovDeg = frameVfovDeg;
        this.pitchTransitionDeg = pitchTransitionDeg;
        this.pitchHysteresisDeg = pitchHysteresisDeg;
        this.modeStateTtlMillis = modeStateTtlMillis;
        this.yawOffsetDeg = yawOffsetDeg;
        this.rollOffsetDeg = rollOffsetDeg;
        this.rollDirection = rollDirection >= 0.0D ? 1.0D : -1.0D;
    }

    public ViewConeGeometry calculate(BigDecimal latitude, BigDecimal longitude, Float height,
                                      Float attitudeHead, Double gimbalPitch, Double gimbalYaw, Double gimbalRoll) {
        return calculate(null, latitude, longitude, height, attitudeHead, gimbalPitch, gimbalYaw, gimbalRoll);
    }

    public ViewConeGeometry calculate(String deviceSn, BigDecimal latitude, BigDecimal longitude, Float height,
                                      Float attitudeHead, Double gimbalPitch, Double gimbalYaw, Double gimbalRoll) {
        ResolvedAttitude attitude = resolveAttitude(attitudeHead, gimbalPitch, gimbalYaw, gimbalRoll);
        if (!isValidLocation(latitude, longitude, height) || attitude == null) {
            return ViewConeGeometry.empty(attitude == null ? null : attitude.headingDeg,
                    attitude == null ? null : attitude.pitchDeg, attitude == null ? null : attitude.rollDeg);
        }
        ViewConeMode mode = deviceSn == null || deviceSn.trim().isEmpty()
                ? resolveInitialMode(attitude.pitchDeg) : resolveMode(deviceSn, attitude.pitchDeg);
        CameraFrame frame = CameraFrame.of(attitude.headingDeg, attitude.pitchDeg, attitude.rollDeg);
        return mode == ViewConeMode.PLANAR
                ? calculatePlanarGeometry(latitude, longitude, height, frame, attitude, mode)
                : calculateSpatialGeometry(latitude, longitude, height, frame, attitude, mode);
    }

    @Scheduled(fixedDelay = MODE_STATE_CLEANUP_INTERVAL_SECONDS, timeUnit = TimeUnit.SECONDS)
    public void cleanupExpiredModeStates() {
        long now = System.currentTimeMillis();
        viewConeModes.entrySet().removeIf(entry -> entry.getValue().isExpired(now, modeStateTtlMillis));
    }

    private ViewConeGeometry calculatePlanarGeometry(BigDecimal droneLat, BigDecimal droneLon, Float height,
                                                     CameraFrame frame, ResolvedAttitude attitude, ViewConeMode mode) {
        GeoPointDTO center = intersectGround(droneLat, droneLon, height, frame.forward);
        if (center == null) {
            return ViewConeGeometry.empty(attitude.headingDeg, attitude.pitchDeg, attitude.rollDeg, mode);
        }
        double halfWidth = Math.tan(Math.toRadians(frameHfovDeg / 2.0D));
        double halfHeight = Math.tan(Math.toRadians(frameVfovDeg / 2.0D));
        List<GeoPointDTO> corners = Arrays.asList(
                intersectGround(droneLat, droneLon, height, frame.ray(-halfWidth, halfHeight)),
                intersectGround(droneLat, droneLon, height, frame.ray(halfWidth, halfHeight)),
                intersectGround(droneLat, droneLon, height, frame.ray(halfWidth, -halfHeight)),
                intersectGround(droneLat, droneLon, height, frame.ray(-halfWidth, -halfHeight))
        );
        if (corners.contains(null)) {
            return ViewConeGeometry.empty(attitude.headingDeg, attitude.pitchDeg, attitude.rollDeg, mode);
        }
        return ViewConeGeometry.planar(center, corners, attitude.headingDeg, attitude.pitchDeg, attitude.rollDeg, mode);
    }

    private ViewConeGeometry calculateSpatialGeometry(BigDecimal droneLat, BigDecimal droneLon, Float height,
                                                      CameraFrame frame, ResolvedAttitude attitude, ViewConeMode mode) {
        // 空间分支以高度作为光轴前向截面距离；截面始终垂直光轴，和二维投影共享同一画面方向。
        double distance = height.doubleValue();
        double halfWidth = distance * Math.tan(Math.toRadians(frameHfovDeg / 2.0D));
        double halfHeight = distance * Math.tan(Math.toRadians(frameVfovDeg / 2.0D));
        Vector3 centerOffset = frame.forward.scale(distance);
        GeoSpatialPointDTO center = translateSpatial(droneLat, droneLon, BigDecimal.valueOf(height), centerOffset);
        List<GeoSpatialPointDTO> corners = Arrays.asList(
                translateSpatial(droneLat, droneLon, BigDecimal.valueOf(height), centerOffset
                        .add(frame.right.scale(-halfWidth)).add(frame.up.scale(halfHeight))),
                translateSpatial(droneLat, droneLon, BigDecimal.valueOf(height), centerOffset
                        .add(frame.right.scale(halfWidth)).add(frame.up.scale(halfHeight))),
                translateSpatial(droneLat, droneLon, BigDecimal.valueOf(height), centerOffset
                        .add(frame.right.scale(halfWidth)).add(frame.up.scale(-halfHeight))),
                translateSpatial(droneLat, droneLon, BigDecimal.valueOf(height), centerOffset
                        .add(frame.right.scale(-halfWidth)).add(frame.up.scale(-halfHeight)))
        );
        return ViewConeGeometry.spatial(center, corners, attitude.headingDeg, attitude.pitchDeg, attitude.rollDeg, mode);
    }

    private GeoPointDTO intersectGround(BigDecimal originLat, BigDecimal originLon, Float height, Vector3 ray) {
        if (ray.up >= MIN_GROUND_RAY_DOWN_COMPONENT) {
            return null;
        }
        double distance = -height / ray.up;
        if (!Double.isFinite(distance) || distance <= 0.0D) {
            return null;
        }
        return translate(originLat, originLon, ray.scale(distance));
    }

    private GeoSpatialPointDTO translateSpatial(BigDecimal originLat, BigDecimal originLon,
                                                BigDecimal originHeight, Vector3 offset) {
        GeoPointDTO point = translate(originLat, originLon, offset);
        return new GeoSpatialPointDTO(point.getLatitude(), point.getLongitude(),
                originHeight.add(BigDecimal.valueOf(offset.up), MATH_CONTEXT));
    }

    private GeoPointDTO translate(BigDecimal originLat, BigDecimal originLon, Vector3 offset) {
        if (Math.abs(offset.east) < 1.0E-10D && Math.abs(offset.north) < 1.0E-10D) {
            return new GeoPointDTO(originLat, originLon);
        }
        double metersPerDegreeLon = METERS_PER_DEGREE_LAT * Math.cos(Math.toRadians(originLat.doubleValue()));
        return new GeoPointDTO(
                originLat.add(BigDecimal.valueOf(offset.north / METERS_PER_DEGREE_LAT), MATH_CONTEXT),
                originLon.add(BigDecimal.valueOf(offset.east / metersPerDegreeLon), MATH_CONTEXT));
    }

    private ResolvedAttitude resolveAttitude(Float attitudeHead, Double gimbalPitch, Double gimbalYaw, Double gimbalRoll) {
        Double heading = validAngle(gimbalYaw) ? gimbalYaw : (validAngle(attitudeHead) ? attitudeHead.doubleValue() : null);
        if (heading == null || (gimbalPitch != null && !validAngle(gimbalPitch)) ||
                (gimbalRoll != null && !validAngle(gimbalRoll))) {
            return null;
        }
        double pitch = gimbalPitch == null ? STRAIGHT_DOWN_PITCH : gimbalPitch;
        if (pitch < -90.0D || pitch > 90.0D) {
            return null;
        }
        double roll = gimbalRoll == null ? 0.0D : gimbalRoll;
        return new ResolvedAttitude(normalizeHeading(heading + yawOffsetDeg), pitch,
                normalizeSignedAngle(roll * rollDirection + rollOffsetDeg));
    }

    private boolean isValidLocation(BigDecimal latitude, BigDecimal longitude, Float height) {
        return latitude != null && longitude != null && height != null && Float.isFinite(height) && height > 0.0F
                && latitude.compareTo(BigDecimal.valueOf(-90)) >= 0 && latitude.compareTo(BigDecimal.valueOf(90)) <= 0
                && longitude.compareTo(BigDecimal.valueOf(-180)) >= 0 && longitude.compareTo(BigDecimal.valueOf(180)) <= 0;
    }

    private boolean validAngle(Number value) {
        return value != null && Double.isFinite(value.doubleValue()) && Math.abs(value.doubleValue()) <= 720.0D;
    }

    private double normalizeHeading(double angleDeg) {
        double normalized = angleDeg % 360.0D;
        return normalized < 0.0D ? normalized + 360.0D : normalized;
    }

    private double normalizeSignedAngle(double angleDeg) {
        double normalized = angleDeg % 360.0D;
        if (normalized > 180.0D) {
            return normalized - 360.0D;
        }
        return normalized <= -180.0D ? normalized + 360.0D : normalized;
    }

    private ViewConeMode resolveInitialMode(double pitchDeg) {
        return pitchDeg <= pitchTransitionDeg ? ViewConeMode.PLANAR : ViewConeMode.SPATIAL;
    }

    private ViewConeMode resolveMode(String deviceSn, double pitchDeg) {
        long now = System.currentTimeMillis();
        return viewConeModes.compute(deviceSn, (key, previousState) -> {
            if (previousState == null || previousState.isExpired(now, modeStateTtlMillis)) {
                return new ViewConeModeState(resolveInitialMode(pitchDeg), now);
            }
            previousState.update(resolveNextMode(previousState.mode, pitchDeg), now);
            return previousState;
        }).mode;
    }

    private ViewConeMode resolveNextMode(ViewConeMode previousMode, double pitchDeg) {
        if (previousMode == ViewConeMode.PLANAR && pitchDeg > pitchTransitionDeg + pitchHysteresisDeg) {
            return ViewConeMode.SPATIAL;
        }
        if (previousMode == ViewConeMode.SPATIAL && pitchDeg <= pitchTransitionDeg - pitchHysteresisDeg) {
            return ViewConeMode.PLANAR;
        }
        return previousMode;
    }

    private enum ViewConeMode { PLANAR, SPATIAL }

    private static final class ViewConeModeState {
        private volatile ViewConeMode mode;
        private volatile long lastUpdatedAt;

        private ViewConeModeState(ViewConeMode mode, long lastUpdatedAt) {
            this.mode = mode;
            this.lastUpdatedAt = lastUpdatedAt;
        }

        private boolean isExpired(long now, long ttlMillis) {
            return now - lastUpdatedAt >= ttlMillis;
        }

        private void update(ViewConeMode nextMode, long now) {
            this.mode = nextMode;
            this.lastUpdatedAt = now;
        }
    }

    private static final class ResolvedAttitude {
        private final double headingDeg;
        private final double pitchDeg;
        private final double rollDeg;

        private ResolvedAttitude(double headingDeg, double pitchDeg, double rollDeg) {
            this.headingDeg = headingDeg;
            this.pitchDeg = pitchDeg;
            this.rollDeg = rollDeg;
        }
    }

    private static final class CameraFrame {
        private final Vector3 forward;
        private final Vector3 right;
        private final Vector3 up;

        private CameraFrame(Vector3 forward, Vector3 right, Vector3 up) {
            this.forward = forward;
            this.right = right;
            this.up = up;
        }

        private static CameraFrame of(double headingDeg, double pitchDeg, double rollDeg) {
            double heading = Math.toRadians(headingDeg);
            double pitch = Math.toRadians(pitchDeg);
            Vector3 forward = new Vector3(Math.sin(heading) * Math.cos(pitch),
                    Math.cos(heading) * Math.cos(pitch), Math.sin(pitch));
            Vector3 baseRight = new Vector3(Math.cos(heading), -Math.sin(heading), 0.0D);
            Vector3 baseUp = baseRight.cross(forward).normalize();
            // DJI roll 作用于画面自身轴；负号使默认 rollDirection=+1 时画面右转语义保持一致。
            double roll = -Math.toRadians(rollDeg);
            return new CameraFrame(forward, baseRight.rotateAround(forward, roll), baseUp.rotateAround(forward, roll));
        }

        private Vector3 ray(double horizontal, double vertical) {
            return forward.add(right.scale(horizontal)).add(up.scale(vertical)).normalize();
        }
    }

    private static final class Vector3 {
        private final double east;
        private final double north;
        private final double up;

        private Vector3(double east, double north, double up) {
            this.east = east;
            this.north = north;
            this.up = up;
        }

        private Vector3 add(Vector3 other) { return new Vector3(east + other.east, north + other.north, up + other.up); }
        private Vector3 scale(double scalar) { return new Vector3(east * scalar, north * scalar, up * scalar); }
        private double dot(Vector3 other) { return east * other.east + north * other.north + up * other.up; }
        private Vector3 cross(Vector3 other) {
            return new Vector3(north * other.up - up * other.north, up * other.east - east * other.up,
                    east * other.north - north * other.east);
        }
        private Vector3 normalize() {
            double length = Math.sqrt(dot(this));
            return new Vector3(east / length, north / length, up / length);
        }
        private Vector3 rotateAround(Vector3 axis, double radians) {
            double cos = Math.cos(radians);
            double sin = Math.sin(radians);
            return scale(cos).add(axis.cross(this).scale(sin)).add(axis.scale(axis.dot(this) * (1.0D - cos)));
        }
    }

    public static final class ViewConeGeometry {
        private final GeoPointDTO frameCenter;
        private final List<GeoPointDTO> corners;
        private final GeoSpatialPointDTO spatialCenter;
        private final List<GeoSpatialPointDTO> spatialCorners;
        private final Double resolvedHeading;
        private final Double resolvedPitch;
        private final Double resolvedRoll;
        private final String viewMode;
        private final String cornerOrder;
        private final Integer geometryVersion;

        private ViewConeGeometry(GeoPointDTO frameCenter, List<GeoPointDTO> corners, GeoSpatialPointDTO spatialCenter,
                                 List<GeoSpatialPointDTO> spatialCorners, Double resolvedHeading, Double resolvedPitch,
                                 Double resolvedRoll, ViewConeMode viewMode) {
            this.frameCenter = frameCenter;
            this.corners = corners;
            this.spatialCenter = spatialCenter;
            this.spatialCorners = spatialCorners;
            this.resolvedHeading = resolvedHeading;
            this.resolvedPitch = resolvedPitch;
            this.resolvedRoll = resolvedRoll;
            this.viewMode = viewMode == null ? null : viewMode.name();
            this.cornerOrder = CAMERA_CORNER_ORDER;
            this.geometryVersion = GEOMETRY_VERSION;
        }

        private static ViewConeGeometry planar(GeoPointDTO center, List<GeoPointDTO> corners, Double heading,
                                               Double pitch, Double roll, ViewConeMode mode) {
            return new ViewConeGeometry(center, corners, null, null, heading, pitch, roll, mode);
        }
        private static ViewConeGeometry spatial(GeoSpatialPointDTO center, List<GeoSpatialPointDTO> corners,
                                                Double heading, Double pitch, Double roll, ViewConeMode mode) {
            return new ViewConeGeometry(null, null, center, corners, heading, pitch, roll, mode);
        }
        private static ViewConeGeometry empty(Double heading, Double pitch, Double roll) {
            return empty(heading, pitch, roll, null);
        }
        private static ViewConeGeometry empty(Double heading, Double pitch, Double roll, ViewConeMode mode) {
            return new ViewConeGeometry(null, null, null, null, heading, pitch, roll, mode);
        }

        public GeoPointDTO getFrameCenter() { return frameCenter; }
        public List<GeoPointDTO> getCorners() { return corners; }
        public GeoSpatialPointDTO getSpatialCenter() { return spatialCenter; }
        public List<GeoSpatialPointDTO> getSpatialCorners() { return spatialCorners; }
        public Double getResolvedHeading() { return resolvedHeading; }
        public Double getResolvedPitch() { return resolvedPitch; }
        public Double getResolvedRoll() { return resolvedRoll; }
        public String getViewMode() { return viewMode; }
        public String getCornerOrder() { return cornerOrder; }
        public Integer getGeometryVersion() { return geometryVersion; }
        public boolean hasGeometry() { return frameCenter != null || spatialCenter != null; }
    }
}
