/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.dict.controller;

import com.cv.boot.common.enums.ErrorCodeEnum;
import com.cv.solution.formdict.dict.pojo.param.SysDictTypeParam;
import com.cv.solution.formdict.dict.pojo.query.SysDictTypePageQuery;
import com.cv.solution.formdict.dict.pojo.vo.SysDictTypePageVO;
import com.cv.solution.formdict.dict.pojo.vo.SysDictTypeVO;
import com.cv.solution.formdict.dict.service.ISysDictTypeService;
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

import javax.annotation.Resource;

/**
 * 系统字典类型表API接口层
 *
 * @author xutu
 * @date 2025-10-28 09:22:53
 */
@RestController
@RequestMapping("/sysDict/type")
@Slf4j
public class SysDictTypeController {

    @Resource
    private ISysDictTypeService sysDictTypeService;

    /**
     * @param query {@link }
     * @return {@link Result<PageInfoVO>}
     * @author xutu
     * @date 2025-10-28 09:22:53
     * @description 分页列表
     * @menu 系统字典类型表管理
     **/
    @RequestMapping(value = "/pageList", method = RequestMethod.POST)
    public Result<PageInfoVO<SysDictTypePageVO>> pageList(@RequestBody SysDictTypePageQuery query) {
        PageInfoVO vo = sysDictTypeService.pageList(query);
        return Result.success(vo);
    }

    /**
     * @param param {@link SysDictTypeParam}
     * @return {@link Result<Long>}
     * @author xutu
     * @date 2025-10-28 09:22:53
     * @description 新增
     * @menu 系统字典类型表管理
     **/
    @PostMapping("/add")
    public Result<Long> add(@RequestBody @Validated SysDictTypeParam param) {
        Long id = sysDictTypeService.add(param);
        return Result.success(id);
    }

    /**
     * @param param {@link SysDictTypeParam}
     * @return {@link Result<Long>}
     * @author xutu
     * @date 2025-10-28 09:22:53
     * @description 编辑
     * @menu 系统字典类型表管理
     **/
    @PostMapping("/edit")
    public Result<Long> edit(@RequestBody @Validated SysDictTypeParam param) {
        sysDictTypeService.edit(param);
        return Result.success(param.getId());
    }

    /**
     * @param query id
     * @return {@link Result}
     * @author xutu
     * @date 2025-10-28 09:22:53
     * @description 删除
     * @menu 系统字典类型表管理
     **/
    @PostMapping("/delete")
    public Result delete(@RequestBody DeletedByIdListQuery query) {
        if (CollectionUtils.isEmpty(query.getIdList())) {
            return Result.fail(ErrorCodeEnum.REQUEST_PARAM_ERROR, "请选择要删除的数据");
        }
        sysDictTypeService.delete(query);
        return Result.success();
    }

    /**
     * @param sysDictTypeId 主键id
     * @return {@link Result}
     * @author xutu
     * @date 2025-10-28 09:22:53
     * @description 详情
     * @menu 系统字典类型表管理
     **/
    @GetMapping("/detail/{sysDictTypeId}")
    public Result<SysDictTypeVO> detail(@PathVariable("sysDictTypeId") Long sysDictTypeId) {
        return Result.success(sysDictTypeService.detail(sysDictTypeId));
    }

}