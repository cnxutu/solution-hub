package com.cv.simulator.videoosd.sample.controller;

import com.cv.boot.mybatisplus.pojo.vo.PageInfoVO;
import com.cv.simulator.videoosd.sample.common.ApiResult;
import com.cv.simulator.videoosd.sample.pojo.entity.TaskRunLogEntity;
import com.cv.simulator.videoosd.sample.pojo.query.RunLogPageQuery;
import com.cv.simulator.videoosd.sample.service.TaskRunLogService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/simulator/run-log")
public class RunLogController {

    private final TaskRunLogService runLogService;

    public RunLogController(TaskRunLogService runLogService) {
        this.runLogService = runLogService;
    }

    @PostMapping("/pageList")
    public ApiResult<PageInfoVO<TaskRunLogEntity>> pageList(@RequestBody RunLogPageQuery query) {
        return ApiResult.success(runLogService.pageList(query));
    }
}
