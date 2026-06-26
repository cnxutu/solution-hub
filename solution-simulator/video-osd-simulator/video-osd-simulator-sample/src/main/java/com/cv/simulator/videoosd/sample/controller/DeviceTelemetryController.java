package com.cv.simulator.videoosd.sample.controller;

import com.cv.boot.mybatisplus.pojo.vo.PageInfoVO;
import com.cv.simulator.videoosd.sample.common.ApiResult;
import com.cv.simulator.videoosd.sample.pojo.entity.DeviceTelemetryEntity;
import com.cv.simulator.videoosd.sample.pojo.query.DeleteIdsQuery;
import com.cv.simulator.videoosd.sample.pojo.query.TelemetryPageQuery;
import com.cv.simulator.videoosd.sample.service.DeviceTelemetryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/simulator/osd")
public class DeviceTelemetryController {

    private final DeviceTelemetryService telemetryService;

    public DeviceTelemetryController(DeviceTelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    @PostMapping("/pageList")
    public ApiResult<PageInfoVO<DeviceTelemetryEntity>> pageList(@RequestBody TelemetryPageQuery query) {
        return ApiResult.success(telemetryService.pageList(query));
    }

    @PostMapping("/add")
    public ApiResult<Long> add(@RequestBody DeviceTelemetryEntity entity) {
        return ApiResult.success(telemetryService.add(entity));
    }

    @PostMapping("/edit")
    public ApiResult<Long> edit(@RequestBody DeviceTelemetryEntity entity) {
        return ApiResult.success(telemetryService.edit(entity));
    }

    @PostMapping("/delete")
    public ApiResult<Void> delete(@RequestBody DeleteIdsQuery query) {
        telemetryService.delete(query);
        return ApiResult.success();
    }

    @GetMapping("/detail/{id}")
    public ApiResult<DeviceTelemetryEntity> detail(@PathVariable Long id) {
        return ApiResult.success(telemetryService.detail(id));
    }

    @PostMapping("/upload-json")
    public ApiResult<Long> uploadJson(@RequestBody DeviceTelemetryEntity entity) {
        return ApiResult.success(telemetryService.uploadJson(entity));
    }

    @PostMapping("/import-excel")
    public ApiResult<Integer> importExcel(@RequestParam("file") MultipartFile file,
                                          @RequestParam(value = "taskId", required = false) Long taskId) throws Exception {
        return ApiResult.success(telemetryService.importExcel(file.getInputStream(), taskId));
    }
}
