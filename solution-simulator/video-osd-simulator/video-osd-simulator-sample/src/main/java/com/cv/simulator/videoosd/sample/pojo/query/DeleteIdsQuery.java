package com.cv.simulator.videoosd.sample.pojo.query;

import lombok.Data;

import java.util.List;

@Data
public class DeleteIdsQuery {
    private List<Long> idList;
}
