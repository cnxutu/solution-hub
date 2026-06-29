package com.cv.simulator.videoosd.sample.controller;

import com.cv.boot.mybatisplus.pojo.vo.PageInfoVO;
import com.cv.simulator.videoosd.core.runtime.StreamTaskSnapshot;
import com.cv.simulator.videoosd.sample.common.ApiResult;
import com.cv.simulator.videoosd.sample.pojo.entity.StreamTaskEntity;
import com.cv.simulator.videoosd.sample.pojo.query.DeleteIdsQuery;
import com.cv.simulator.videoosd.sample.pojo.query.StreamTaskPageQuery;
import com.cv.simulator.videoosd.sample.service.DeviceTelemetryService;
import com.cv.simulator.videoosd.sample.service.StreamTaskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/simulator/stream-task")
public class StreamTaskController {

    private final StreamTaskService streamTaskService;
    private final DeviceTelemetryService telemetryService;

    public StreamTaskController(StreamTaskService streamTaskService, DeviceTelemetryService telemetryService) {
        this.streamTaskService = streamTaskService;
        this.telemetryService = telemetryService;
    }

    @PostMapping("/pageList")
    public ApiResult<PageInfoVO<StreamTaskEntity>> pageList(@RequestBody StreamTaskPageQuery query) {
        return ApiResult.success(streamTaskService.pageList(query));
    }

    @PostMapping("/add")
    public ApiResult<Long> add(@RequestBody StreamTaskEntity entity) {
        return ApiResult.success(streamTaskService.add(entity));
    }

    @PostMapping("/edit")
    public ApiResult<Long> edit(@RequestBody StreamTaskEntity entity) {
        return ApiResult.success(streamTaskService.edit(entity));
    }

    @PostMapping("/delete")
    public ApiResult<Void> delete(@RequestBody DeleteIdsQuery query) {
        streamTaskService.delete(query);
        return ApiResult.success();
    }

    @GetMapping("/detail/{id}")
    public ApiResult<StreamTaskEntity> detail(@PathVariable Long id) {
        return ApiResult.success(streamTaskService.detail(id));
    }

    @PostMapping("/start/{id}")
    public ApiResult<StreamTaskSnapshot> start(@PathVariable Long id) {
        StreamTaskSnapshot snapshot = streamTaskService.start(id);
        telemetryService.replayByTask(streamTaskService.detail(id));
        return ApiResult.success(snapshot);
    }

    @PostMapping("/stop/{id}")
    public ApiResult<StreamTaskSnapshot> stop(@PathVariable Long id) {
        return ApiResult.success(streamTaskService.stop(id));
    }

    @GetMapping("/status/{id}")
    public ApiResult<StreamTaskSnapshot> status(@PathVariable Long id) {
        return ApiResult.success(streamTaskService.status(id));
    }
}
