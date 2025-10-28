/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.form.controller;

import com.cv.boot.common.enums.ErrorCodeEnum;
import com.cv.boot.common.enums.DeletedEnum;
import com.cv.boot.common.exception.BizException;
import com.cv.solution.formdict.form.pojo.po.TplTemplateDataPO;
import com.cv.solution.formdict.form.pojo.param.TplTemplateDataAddOrEditParam;
import com.cv.solution.formdict.form.pojo.query.TplTemplateDataPageQuery;
import com.cv.solution.formdict.form.pojo.vo.TplTemplateDataPageVO;
import com.cv.solution.formdict.form.pojo.vo.TplTemplateDataVO;
import com.cv.solution.formdict.form.service.ITplTemplateDataService;
import com.cv.boot.common.pojo.query.DeletedByIdListQuery;
import com.cv.boot.mybatisplus.pojo.vo.PageInfoVO;
import com.cv.boot.web.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import javax.annotation.Resource;

import java.util.List;

/**
 * 模板录入数据表API接口层
 *
 * @author xutu
 * @date 2025-10-28 19:28:01
 */
@RestController
@RequestMapping("/tplTemplateData")
@Slf4j
public class TplTemplateDataController {

    @Resource
    private ITplTemplateDataService tplTemplateDataService;

    /**
     * @param query {@link }
     * @return {@link Result<PageInfoVO>}
     * @author xutu
     * @date 2025-10-28 19:28:01
     * @description 分页列表
     * @menu 模板录入数据表管理
     **/
    @RequestMapping(value = "/pageList", method = RequestMethod.POST)
    public Result<PageInfoVO<TplTemplateDataPageVO>> pageList(@RequestBody TplTemplateDataPageQuery query) {
        PageInfoVO vo = tplTemplateDataService.pageList(query);
        return Result.success(vo);
    }

    /**
     * @param param {@link TplTemplateDataAddOrEditParam}
     * @return {@link Result<Long>}
     * @author xutu
     * @date 2025-10-28 19:28:01
     * @description 新增
     * @menu 模板录入数据表管理
     **/
    @PostMapping("/add")
    public Result<Long> add(@RequestBody @Validated TplTemplateDataAddOrEditParam param) {
        Long id = tplTemplateDataService.add(param);
        return Result.success(id);
    }

    /**
     * @param param {@link TplTemplateDataAddOrEditParam}
     * @return {@link Result<Long>}
     * @author xutu
     * @date 2025-10-28 19:28:01
     * @description 编辑
     * @menu 模板录入数据表管理
     **/
    @PostMapping("/edit")
    public Result<Long> edit(@RequestBody @Validated TplTemplateDataAddOrEditParam param) {
        tplTemplateDataService.edit(param);
        return Result.success(param.getId());
    }

    /**
     * @param query id
     * @return {@link Result}
     * @author xutu
     * @date 2025-10-28 19:28:01
     * @description 删除
     * @menu 模板录入数据表管理
     **/
    @PostMapping("/delete")
    public Result delete(@RequestBody DeletedByIdListQuery query) {
        if (CollectionUtils.isEmpty(query.getIdList())) {
            return Result.fail(ErrorCodeEnum.REQUEST_PARAM_ERROR, "请选择要删除的数据");
        }
        tplTemplateDataService.delete(query);
        return Result.success();
    }

    /**
     * @param tplTemplateDataId 主键id
     * @return {@link Result}
     * @author xutu
     * @date 2025-10-28 19:28:01
     * @description 详情
     * @menu 模板录入数据表管理
     **/
    @GetMapping("/detail/{tplTemplateDataId}")
    public Result<TplTemplateDataVO> detail(@PathVariable("tplTemplateDataId") Long tplTemplateDataId) {
        return Result.success(tplTemplateDataService.detail(tplTemplateDataId));
    }

}