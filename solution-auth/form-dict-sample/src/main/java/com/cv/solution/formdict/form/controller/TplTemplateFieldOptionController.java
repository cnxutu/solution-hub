/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.form.controller;

import com.cv.boot.common.enums.ErrorCodeEnum;
import com.cv.boot.common.enums.DeletedEnum;
import com.cv.boot.common.exception.BizException;
import com.cv.solution.formdict.form.pojo.po.TplTemplateFieldOptionPO;
import com.cv.solution.formdict.form.pojo.param.TplTemplateFieldOptionAddOrEditParam;
import com.cv.solution.formdict.form.pojo.query.TplTemplateFieldOptionPageQuery;
import com.cv.solution.formdict.form.pojo.vo.TplTemplateFieldOptionPageVO;
import com.cv.solution.formdict.form.pojo.vo.TplTemplateFieldOptionVO;
import com.cv.solution.formdict.form.service.ITplTemplateFieldOptionService;
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
 * 模板字段专属可选项表API接口层
 *
 * @author xutu
 * @date 2025-10-28 19:28:01
 */
@RestController
@RequestMapping("/tplTemplateFieldOption")
@Slf4j
public class TplTemplateFieldOptionController {

    @Resource
    private ITplTemplateFieldOptionService tplTemplateFieldOptionService;

    /**
     * @param query {@link }
     * @return {@link Result<PageInfoVO>}
     * @author xutu
     * @date 2025-10-28 19:28:01
     * @description 分页列表
     * @menu 模板字段专属可选项表管理
     **/
    @RequestMapping(value = "/pageList", method = RequestMethod.POST)
    public Result<PageInfoVO<TplTemplateFieldOptionPageVO>> pageList(@RequestBody TplTemplateFieldOptionPageQuery query) {
        PageInfoVO vo = tplTemplateFieldOptionService.pageList(query);
        return Result.success(vo);
    }

    /**
     * @param param {@link TplTemplateFieldOptionAddOrEditParam}
     * @return {@link Result<Long>}
     * @author xutu
     * @date 2025-10-28 19:28:01
     * @description 新增
     * @menu 模板字段专属可选项表管理
     **/
    @PostMapping("/add")
    public Result<Long> add(@RequestBody @Validated TplTemplateFieldOptionAddOrEditParam param) {
        Long id = tplTemplateFieldOptionService.add(param);
        return Result.success(id);
    }

    /**
     * @param param {@link TplTemplateFieldOptionAddOrEditParam}
     * @return {@link Result<Long>}
     * @author xutu
     * @date 2025-10-28 19:28:01
     * @description 编辑
     * @menu 模板字段专属可选项表管理
     **/
    @PostMapping("/edit")
    public Result<Long> edit(@RequestBody @Validated TplTemplateFieldOptionAddOrEditParam param) {
        tplTemplateFieldOptionService.edit(param);
        return Result.success(param.getId());
    }

    /**
     * @param query id
     * @return {@link Result}
     * @author xutu
     * @date 2025-10-28 19:28:01
     * @description 删除
     * @menu 模板字段专属可选项表管理
     **/
    @PostMapping("/delete")
    public Result delete(@RequestBody DeletedByIdListQuery query) {
        if (CollectionUtils.isEmpty(query.getIdList())) {
            return Result.fail(ErrorCodeEnum.REQUEST_PARAM_ERROR, "请选择要删除的数据");
        }
        tplTemplateFieldOptionService.delete(query);
        return Result.success();
    }

    /**
     * @param tplTemplateFieldOptionId 主键id
     * @return {@link Result}
     * @author xutu
     * @date 2025-10-28 19:28:01
     * @description 详情
     * @menu 模板字段专属可选项表管理
     **/
    @GetMapping("/detail/{tplTemplateFieldOptionId}")
    public Result<TplTemplateFieldOptionVO> detail(@PathVariable("tplTemplateFieldOptionId") Long tplTemplateFieldOptionId) {
        return Result.success(tplTemplateFieldOptionService.detail(tplTemplateFieldOptionId));
    }

}