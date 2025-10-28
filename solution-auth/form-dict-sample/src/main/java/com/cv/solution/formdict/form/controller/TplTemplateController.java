/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.form.controller;

import com.cv.boot.common.enums.ErrorCodeEnum;
import com.cv.boot.common.enums.DeletedEnum;
import com.cv.boot.common.exception.BizException;
import com.cv.solution.formdict.form.pojo.po.TplTemplatePO;
import com.cv.solution.formdict.form.pojo.param.TplTemplateAddOrEditParam;
import com.cv.solution.formdict.form.pojo.query.TplTemplatePageQuery;
import com.cv.solution.formdict.form.pojo.vo.TplTemplatePageVO;
import com.cv.solution.formdict.form.pojo.vo.TplTemplateVO;
import com.cv.solution.formdict.form.service.ITplTemplateService;
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
 * 模板表API接口层
 *
 * @author xutu
 * @date 2025-10-28 19:28:01
 */
@RestController
@RequestMapping("/tplTemplate")
@Slf4j
public class TplTemplateController {

    @Resource
    private ITplTemplateService tplTemplateService;

    /**
     * @param query {@link }
     * @return {@link Result<PageInfoVO>}
     * @author xutu
     * @date 2025-10-28 19:28:01
     * @description 分页列表
     * @menu 模板表管理
     **/
    @RequestMapping(value = "/pageList", method = RequestMethod.POST)
    public Result<PageInfoVO<TplTemplatePageVO>> pageList(@RequestBody TplTemplatePageQuery query) {
        PageInfoVO vo = tplTemplateService.pageList(query);
        return Result.success(vo);
    }

    /**
     * @param param {@link TplTemplateAddOrEditParam}
     * @return {@link Result<Long>}
     * @author xutu
     * @date 2025-10-28 19:28:01
     * @description 新增
     * @menu 模板表管理
     **/
    @PostMapping("/add")
    public Result<Long> add(@RequestBody @Validated TplTemplateAddOrEditParam param) {
        Long id = tplTemplateService.add(param);
        return Result.success(id);
    }

    /**
     * @param param {@link TplTemplateAddOrEditParam}
     * @return {@link Result<Long>}
     * @author xutu
     * @date 2025-10-28 19:28:01
     * @description 编辑
     * @menu 模板表管理
     **/
    @PostMapping("/edit")
    public Result<Long> edit(@RequestBody @Validated TplTemplateAddOrEditParam param) {
        tplTemplateService.edit(param);
        return Result.success(param.getId());
    }

    /**
     * @param query id
     * @return {@link Result}
     * @author xutu
     * @date 2025-10-28 19:28:01
     * @description 删除
     * @menu 模板表管理
     **/
    @PostMapping("/delete")
    public Result delete(@RequestBody DeletedByIdListQuery query) {
        if (CollectionUtils.isEmpty(query.getIdList())) {
            return Result.fail(ErrorCodeEnum.REQUEST_PARAM_ERROR, "请选择要删除的数据");
        }
        tplTemplateService.delete(query);
        return Result.success();
    }

    /**
     * @param tplTemplateId 主键id
     * @return {@link Result}
     * @author xutu
     * @date 2025-10-28 19:28:01
     * @description 详情
     * @menu 模板表管理
     **/
    @GetMapping("/detail/{tplTemplateId}")
    public Result<TplTemplateVO> detail(@PathVariable("tplTemplateId") Long tplTemplateId) {
        return Result.success(tplTemplateService.detail(tplTemplateId));
    }

}