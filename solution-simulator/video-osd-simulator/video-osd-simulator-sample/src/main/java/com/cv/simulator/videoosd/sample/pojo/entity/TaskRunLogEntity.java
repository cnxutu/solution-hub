package com.cv.simulator.videoosd.sample.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sim_task_run_log")
public class TaskRunLogEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long taskId;
    private String eventType;
    private String status;
    private String message;
    private String commandLine;
    private Integer sentCount;
    private LocalDateTime createTime;
}
