package com.cv.simulator.videoosd.v1.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class DeviceTelemetryRecord {

    private Long id;
    private Long taskId;
    private String deviceSn;
    private Float attitudeHead;
    private Double attitudePitch;
    private Double attitudeRoll;
    private Float elevation;
    private String battery;
    private String firmwareVersion;
    private String gear;
    private Float height;
    private Float homeDistance;
    private Float horizontalSpeed;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer modeCode;
    private Integer actionType;
    private Double totalFlightDistance;
    private Float totalFlightTime;
    private Float verticalSpeed;
    private String windDirection;
    private Float windSpeed;
    private String positionState;
    private String payloads;
    private String storage;
    private String nightLightsState;
    private Integer heightLimit;
    private String distanceLimitStatus;
    private String obstacleAvoidance;
    private Long activationTime;
    private String cameras;
    private String rcLostAction;
    private Integer rthAltitude;
    private Integer totalFlightSorties;
    private String exitWaylineWhenRcLost;
    private String country;
    private Boolean ridState;
    private Boolean nearAreaLimit;
    private Boolean nearHeightLimit;
    private String maintainStatus;
    private String trackId;
    private LocalDateTime publishTime;
    private LocalDateTime publishTimeCp1;
    private String rawJson;
    private GeoPoint frameCenter;
    private List<GeoPoint> corners;
    private LocalDateTime createTime;
    private String createdBy;
    private LocalDateTime updateTime;
    private String updatedBy;
    private Integer isDeleted;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public String getDeviceSn() { return deviceSn; }
    public void setDeviceSn(String deviceSn) { this.deviceSn = deviceSn; }
    public Float getAttitudeHead() { return attitudeHead; }
    public void setAttitudeHead(Float attitudeHead) { this.attitudeHead = attitudeHead; }
    public Double getAttitudePitch() { return attitudePitch; }
    public void setAttitudePitch(Double attitudePitch) { this.attitudePitch = attitudePitch; }
    public Double getAttitudeRoll() { return attitudeRoll; }
    public void setAttitudeRoll(Double attitudeRoll) { this.attitudeRoll = attitudeRoll; }
    public Float getElevation() { return elevation; }
    public void setElevation(Float elevation) { this.elevation = elevation; }
    public String getBattery() { return battery; }
    public void setBattery(String battery) { this.battery = battery; }
    public String getFirmwareVersion() { return firmwareVersion; }
    public void setFirmwareVersion(String firmwareVersion) { this.firmwareVersion = firmwareVersion; }
    public String getGear() { return gear; }
    public void setGear(String gear) { this.gear = gear; }
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
    public Integer getModeCode() { return modeCode; }
    public void setModeCode(Integer modeCode) { this.modeCode = modeCode; }
    public Integer getActionType() { return actionType; }
    public void setActionType(Integer actionType) { this.actionType = actionType; }
    public Double getTotalFlightDistance() { return totalFlightDistance; }
    public void setTotalFlightDistance(Double totalFlightDistance) { this.totalFlightDistance = totalFlightDistance; }
    public Float getTotalFlightTime() { return totalFlightTime; }
    public void setTotalFlightTime(Float totalFlightTime) { this.totalFlightTime = totalFlightTime; }
    public Float getVerticalSpeed() { return verticalSpeed; }
    public void setVerticalSpeed(Float verticalSpeed) { this.verticalSpeed = verticalSpeed; }
    public String getWindDirection() { return windDirection; }
    public void setWindDirection(String windDirection) { this.windDirection = windDirection; }
    public Float getWindSpeed() { return windSpeed; }
    public void setWindSpeed(Float windSpeed) { this.windSpeed = windSpeed; }
    public String getPositionState() { return positionState; }
    public void setPositionState(String positionState) { this.positionState = positionState; }
    public String getPayloads() { return payloads; }
    public void setPayloads(String payloads) { this.payloads = payloads; }
    public String getStorage() { return storage; }
    public void setStorage(String storage) { this.storage = storage; }
    public String getNightLightsState() { return nightLightsState; }
    public void setNightLightsState(String nightLightsState) { this.nightLightsState = nightLightsState; }
    public Integer getHeightLimit() { return heightLimit; }
    public void setHeightLimit(Integer heightLimit) { this.heightLimit = heightLimit; }
    public String getDistanceLimitStatus() { return distanceLimitStatus; }
    public void setDistanceLimitStatus(String distanceLimitStatus) { this.distanceLimitStatus = distanceLimitStatus; }
    public String getObstacleAvoidance() { return obstacleAvoidance; }
    public void setObstacleAvoidance(String obstacleAvoidance) { this.obstacleAvoidance = obstacleAvoidance; }
    public Long getActivationTime() { return activationTime; }
    public void setActivationTime(Long activationTime) { this.activationTime = activationTime; }
    public String getCameras() { return cameras; }
    public void setCameras(String cameras) { this.cameras = cameras; }
    public String getRcLostAction() { return rcLostAction; }
    public void setRcLostAction(String rcLostAction) { this.rcLostAction = rcLostAction; }
    public Integer getRthAltitude() { return rthAltitude; }
    public void setRthAltitude(Integer rthAltitude) { this.rthAltitude = rthAltitude; }
    public Integer getTotalFlightSorties() { return totalFlightSorties; }
    public void setTotalFlightSorties(Integer totalFlightSorties) { this.totalFlightSorties = totalFlightSorties; }
    public String getExitWaylineWhenRcLost() { return exitWaylineWhenRcLost; }
    public void setExitWaylineWhenRcLost(String exitWaylineWhenRcLost) { this.exitWaylineWhenRcLost = exitWaylineWhenRcLost; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public Boolean getRidState() { return ridState; }
    public void setRidState(Boolean ridState) { this.ridState = ridState; }
    public Boolean getNearAreaLimit() { return nearAreaLimit; }
    public void setNearAreaLimit(Boolean nearAreaLimit) { this.nearAreaLimit = nearAreaLimit; }
    public Boolean getNearHeightLimit() { return nearHeightLimit; }
    public void setNearHeightLimit(Boolean nearHeightLimit) { this.nearHeightLimit = nearHeightLimit; }
    public String getMaintainStatus() { return maintainStatus; }
    public void setMaintainStatus(String maintainStatus) { this.maintainStatus = maintainStatus; }
    public String getTrackId() { return trackId; }
    public void setTrackId(String trackId) { this.trackId = trackId; }
    public LocalDateTime getPublishTime() { return publishTime; }
    public void setPublishTime(LocalDateTime publishTime) { this.publishTime = publishTime; }
    public LocalDateTime getPublishTimeCp1() { return publishTimeCp1; }
    public void setPublishTimeCp1(LocalDateTime publishTimeCp1) { this.publishTimeCp1 = publishTimeCp1; }
    public String getRawJson() { return rawJson; }
    public void setRawJson(String rawJson) { this.rawJson = rawJson; }
    public GeoPoint getFrameCenter() { return frameCenter; }
    public void setFrameCenter(GeoPoint frameCenter) { this.frameCenter = frameCenter; }
    public List<GeoPoint> getCorners() { return corners; }
    public void setCorners(List<GeoPoint> corners) { this.corners = corners; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
