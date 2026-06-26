package com.cv.simulator.videoosd.sample.pojo.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TelemetryPageQuery extends PageBaseQuery {
    private Long taskId;
    private String deviceSn;
    private String trackId;
}
