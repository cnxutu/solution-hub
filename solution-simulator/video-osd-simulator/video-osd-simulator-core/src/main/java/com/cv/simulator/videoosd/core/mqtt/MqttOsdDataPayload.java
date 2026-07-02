package com.cv.simulator.videoosd.core.mqtt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MqttOsdDataPayload {

    @JsonProperty("attitude_head")
    private Float attitudeHead;
    private Float elevation;
    @JsonProperty("gimbal_pitch")
    private Double gimbalPitch;
    @JsonProperty("gimbal_roll")
    private Double gimbalRoll;
    @JsonProperty("gimbal_yaw")
    private Double gimbalYaw;
    private Float height;
    @JsonProperty("home_distance")
    private Float homeDistance;
    @JsonProperty("horizontal_speed")
    private Float horizontalSpeed;
    private BigDecimal latitude;
    private BigDecimal longitude;
    @JsonProperty("speed_x")
    private Float speedX;
    @JsonProperty("speed_y")
    private Float speedY;
    @JsonProperty("speed_z")
    private Float speedZ;
    @JsonProperty("ultrasonic_height")
    private Float ultrasonicHeight;
    @JsonProperty("vertical_speed")
    private Float verticalSpeed;
    @JsonProperty("wind_direction")
    private Object windDirection;
    @JsonProperty("wind_speed")
    private Float windSpeed;

    public Float getAttitudeHead() { return attitudeHead; }
    public void setAttitudeHead(Float attitudeHead) { this.attitudeHead = attitudeHead; }
    public Float getElevation() { return elevation; }
    public void setElevation(Float elevation) { this.elevation = elevation; }
    public Double getGimbalPitch() { return gimbalPitch; }
    public void setGimbalPitch(Double gimbalPitch) { this.gimbalPitch = gimbalPitch; }
    public Double getGimbalRoll() { return gimbalRoll; }
    public void setGimbalRoll(Double gimbalRoll) { this.gimbalRoll = gimbalRoll; }
    public Double getGimbalYaw() { return gimbalYaw; }
    public void setGimbalYaw(Double gimbalYaw) { this.gimbalYaw = gimbalYaw; }
    public Float getHeight() { return height; }
    public void setHeight(Float height) { this.height = height; }
    public Float getHomeDistance() { return homeDistance; }
    public void setHomeDistance(Float homeDistance) { this.homeDistance = homeDistance; }
    public Float getHorizontalSpeed() { return horizontalSpeed; }
    public void setHorizontalSpeed(Float horizontalSpeed) { this.horizontalSpeed = horizontalSpeed; }
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public Float getSpeedX() { return speedX; }
    public void setSpeedX(Float speedX) { this.speedX = speedX; }
    public Float getSpeedY() { return speedY; }
    public void setSpeedY(Float speedY) { this.speedY = speedY; }
    public Float getSpeedZ() { return speedZ; }
    public void setSpeedZ(Float speedZ) { this.speedZ = speedZ; }
    public Float getUltrasonicHeight() { return ultrasonicHeight; }
    public void setUltrasonicHeight(Float ultrasonicHeight) { this.ultrasonicHeight = ultrasonicHeight; }
    public Float getVerticalSpeed() { return verticalSpeed; }
    public void setVerticalSpeed(Float verticalSpeed) { this.verticalSpeed = verticalSpeed; }
    public Object getWindDirection() { return windDirection; }
    public void setWindDirection(Object windDirection) { this.windDirection = windDirection; }
    public Float getWindSpeed() { return windSpeed; }
    public void setWindSpeed(Float windSpeed) { this.windSpeed = windSpeed; }
}
