package com.cv.simulator.videoosd.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public class OsdWebSocketPayload {

    private Long timestamp;
    @JsonProperty("attitude_head")
    private Float attitudeHead;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Float height;
    @JsonProperty("speed_x")
    private Float speedX;
    @JsonProperty("speed_y")
    private Float speedY;
    @JsonProperty("speed_z")
    private Float speedZ;
    @JsonProperty("gimbal_pitch")
    private Double gimbalPitch;
    @JsonProperty("gimbal_roll")
    private Double gimbalRoll;
    @JsonProperty("gimbal_yaw")
    private Double gimbalYaw;
    @JsonProperty("frame_center")
    private GeoPoint frameCenter;
    private List<GeoPoint> corners;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Float getAttitudeHead() {
        return attitudeHead;
    }

    public void setAttitudeHead(Float attitudeHead) {
        this.attitudeHead = attitudeHead;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public Float getSpeedX() {
        return speedX;
    }

    public void setSpeedX(Float speedX) {
        this.speedX = speedX;
    }

    public Float getSpeedY() {
        return speedY;
    }

    public void setSpeedY(Float speedY) {
        this.speedY = speedY;
    }

    public Float getSpeedZ() {
        return speedZ;
    }

    public void setSpeedZ(Float speedZ) {
        this.speedZ = speedZ;
    }

    public Double getGimbalPitch() {
        return gimbalPitch;
    }

    public void setGimbalPitch(Double gimbalPitch) {
        this.gimbalPitch = gimbalPitch;
    }

    public Double getGimbalRoll() {
        return gimbalRoll;
    }

    public void setGimbalRoll(Double gimbalRoll) {
        this.gimbalRoll = gimbalRoll;
    }

    public Double getGimbalYaw() {
        return gimbalYaw;
    }

    public void setGimbalYaw(Double gimbalYaw) {
        this.gimbalYaw = gimbalYaw;
    }

    public GeoPoint getFrameCenter() {
        return frameCenter;
    }

    public void setFrameCenter(GeoPoint frameCenter) {
        this.frameCenter = frameCenter;
    }

    public List<GeoPoint> getCorners() {
        return corners;
    }

    public void setCorners(List<GeoPoint> corners) {
        this.corners = corners;
    }
}
