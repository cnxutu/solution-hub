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
    @TableId(type = IdType.AUTO)
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
    @TableField("is_near_area_limit")
    private Boolean nearAreaLimit;
    @TableField("is_near_height_limit")
    private Boolean nearHeightLimit;
    private String maintainStatus;
    private String trackId;
    private LocalDateTime publishTime;
    private String rawJson;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createdBy;
    private Long updatedBy;
    private Integer isDeleted;
}
