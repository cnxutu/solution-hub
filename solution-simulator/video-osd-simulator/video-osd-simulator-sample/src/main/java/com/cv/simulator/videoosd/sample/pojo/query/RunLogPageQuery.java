package com.cv.simulator.videoosd.sample.pojo.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RunLogPageQuery extends PageBaseQuery {
    private Long taskId;
}
