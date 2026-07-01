package com.cv.simulator.videoosd.sample.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("device_telemetry_sub")
public class DeviceTelemetryEntity {
    /** 主键。 */
    @TableId(type = IdType.AUTO)
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
    @TableField("is_near_area_limit")
    private Boolean nearAreaLimit;
    /** 临近高度限飞。 */
    @TableField("is_near_height_limit")
    private Boolean nearHeightLimit;
    /** OsdDroneMaintainStatus 对象。 */
    private String maintainStatus;
    /** 轨迹ID。 */
    private String trackId;
    /** OSD 发布时间。 */
    private LocalDateTime publishTime;
    /** 原始 JSON 对象，仅用于导入兼容。 */
    @TableField(exist = false)
    private String rawJson;
    /** 创建时间。 */
    private LocalDateTime createTime;
    /** 更新时间。 */
    private LocalDateTime updateTime;
    /** 创建人。 */
    private String createdBy;
    /** 更新人。 */
    private String updatedBy;
    /** 软删除标记。 */
    private Integer isDeleted;
}
