package com.cv.simulator.videoosd.sample.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cv.simulator.videoosd.sample.pojo.entity.DeviceTelemetryEntity;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DeviceTelemetryMapper extends BaseMapper<DeviceTelemetryEntity> {

    List<DeviceTelemetryEntity> selectReplayRows(@Param("taskId") Long taskId,
                                                 @Param("publishTimeStart") LocalDateTime publishTimeStart,
                                                 @Param("publishTimeEnd") LocalDateTime publishTimeEnd,
                                                 @Param("requireTaskIdMatch") boolean requireTaskIdMatch);
}
