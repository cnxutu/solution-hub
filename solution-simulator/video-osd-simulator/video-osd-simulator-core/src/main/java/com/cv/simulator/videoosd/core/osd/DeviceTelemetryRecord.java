package com.cv.simulator.videoosd.core.osd;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class DeviceTelemetryRecord {

    /** 主键。 */
    private Long id;
    /** 任务id。 */
    private Long taskId;
    /** 设备序列号（机场/无人机/遥控器）。 */
    private String deviceSn;
    /** 航向角。 */
    private Float attitudeHead;
    /** 俯仰角。 */
    private Double attitudePitch;
    /** 横滚角。 */
    private Double attitudeRoll;
    /** 相对高度。 */
    private Float elevation;
    /** DroneBattery 对象。 */
    private String battery;
    /** 固件版本。 */
    private String firmwareVersion;
    /** GearEnum。 */
    private String gear;
    /** 海拔。 */
    private Float height;
    /** 返航点距离。 */
    private Float homeDistance;
    /** 水平速度。 */
    private Float horizontalSpeed;
    /** 纬度。 */
    private BigDecimal latitude;
    /** 经度。 */
    private BigDecimal longitude;
    /** DroneModeCodeEnum。 */
    private Integer modeCode;
    /** 动作类型。 */
    private Integer actionType;
    /** 累计飞行距离。 */
    private Double totalFlightDistance;
    /** 累计飞行时间(分钟/秒视协议)。 */
    private Float totalFlightTime;
    /** 垂直速度。 */
    private Float verticalSpeed;
    /** WindDirectionEnum。 */
    private String windDirection;
    /** 风速。 */
    private Float windSpeed;
    /** DronePositionState 对象。 */
    private String positionState;
    /** List<DockDronePayload>。 */
    private String payloads;
    /** Storage 对象。 */
    private String storage;
    /** SwitchActionEnum。 */
    private String nightLightsState;
    /** 高度限高。 */
    private Integer heightLimit;
    /** DockDistanceLimitStatus 对象。 */
    private String distanceLimitStatus;
    /** ObstacleAvoidance 对象。 */
    private String obstacleAvoidance;
    /** 激活时间(ms)。 */
    private Long activationTime;
    /** List<OsdCamera>。 */
    private String cameras;
    /** RcLostActionEnum。 */
    private String rcLostAction;
    /** RTH 高度。 */
    private Integer rthAltitude;
    /** 总飞行架次。 */
    private Integer totalFlightSorties;
    /** ExitWaylineWhenRcLostEnum(废弃标记按需使用)。 */
    private String exitWaylineWhenRcLost;
    /** 国家/区域。 */
    private String country;
    /** 远程ID状态。 */
    private Boolean ridState;
    /** 临近区限飞。 */
    private Boolean nearAreaLimit;
    /** 临近高度限飞。 */
    private Boolean nearHeightLimit;
    /** OsdDroneMaintainStatus 对象。 */
    private String maintainStatus;
    /** 轨迹ID。 */
    private String trackId;
    /** OSD 发布时间。 */
    private LocalDateTime publishTime;

    private LocalDateTime publishTimeCp1;
    /** 原始 JSON 对象，仅用于兼容额外动态字段。 */
    private String rawJson;
    /** 计算后的画面中心点。 */
    private GeoPoint frameCenter;
    /** 计算后的画面四角点。 */
    private List<GeoPoint> corners;
    /** 创建时间。 */
    private LocalDateTime createTime;
    /** 创建人。 */
    private String createdBy;
    /** 更新时间。 */
    private LocalDateTime updateTime;
    /** 更新人。 */
    private String updatedBy;
    /** 软删除标记。 */
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

    public LocalDateTime getPublishTimeCp1() {
        return publishTimeCp1;
    }

    public void setPublishTimeCp1(LocalDateTime publishTimeCp1) {
        this.publishTimeCp1 = publishTimeCp1;
    }
}
