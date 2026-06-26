package com.cv.simulator.videoosd.sample.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sim_stream_task")
public class StreamTaskEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String taskName;
    private String videoDirectory;
    private String filePattern;
    private String zlmHost;
    private Integer zlmPort;
    private String app;
    private String stream;
    private String protocol;
    private Boolean loopEnabled;
    private String ffmpegOptions;
    private String status;
    private String lastMessage;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDeleted;
}
