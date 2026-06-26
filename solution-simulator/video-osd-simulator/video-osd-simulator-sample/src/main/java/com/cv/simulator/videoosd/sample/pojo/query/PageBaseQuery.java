package com.cv.simulator.videoosd.sample.pojo.query;

import lombok.Data;

@Data
public class PageBaseQuery {
    private Long current = 1L;
    private Long size = 10L;
}
