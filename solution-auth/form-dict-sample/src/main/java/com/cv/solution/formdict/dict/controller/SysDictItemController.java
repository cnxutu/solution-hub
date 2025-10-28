/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.dict.controller;

import com.cv.boot.common.enums.ErrorCodeEnum;
import com.cv.boot.common.enums.DeletedEnum;
import com.cv.boot.common.exception.BizException;
import com.cv.solution.formdict.dict.pojo.po.SysDictItemPO;
import com.cv.solution.formdict.dict.pojo.param.SysDictItemAddOrEditParam;
import com.cv.solution.formdict.dict.pojo.query.SysDictItemPageQuery;
import com.cv.solution.formdict.dict.pojo.vo.SysDictItemPageVO;
import com.cv.solution.formdict.dict.pojo.vo.SysDictItemVO;
import com.cv.solution.formdict.dict.service.ISysDictItemService;
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
 * 系统字典项表API接口层
 *
 * @author xutu
 * @date 2025-10-28 09:22:53
 */
@RestController
@RequestMapping("/sysDict/item")
@Slf4j
public class SysDictItemController {

    @Resource
    private ISysDictItemService sysDictItemService;

    /**
     * @param param {@link SysDictItemAddOrEditParam}
     * @return {@link Result<Long>}
     * @author xutu
     * @date 2025-10-28 09:22:53
     * @description 新增
     * @menu 系统字典项表管理
     **/
    @PostMapping("/add")
    public Result<Long> add(@RequestBody @Validated SysDictItemAddOrEditParam param) {
        Long id = sysDictItemService.add(param);
        return Result.success(id);
    }

    /**
     * @param param {@link SysDictItemAddOrEditParam}
     * @return {@link Result<Long>}
     * @author xutu
     * @date 2025-10-28 09:22:53
     * @description 编辑
     * @menu 系统字典项表管理
     **/
    @PostMapping("/edit")
    public Result<Long> edit(@RequestBody @Validated SysDictItemAddOrEditParam param) {
        sysDictItemService.edit(param);
        return Result.success(param.getId());
    }

    /**
     * @param query id
     * @return {@link Result}
     * @author xutu
     * @date 2025-10-28 09:22:53
     * @description 删除
     * @menu 系统字典项表管理
     **/
    @PostMapping("/delete")
    public Result delete(@RequestBody DeletedByIdListQuery query) {
        if (CollectionUtils.isEmpty(query.getIdList())) {
            return Result.fail(ErrorCodeEnum.REQUEST_PARAM_ERROR, "请选择要删除的数据");
        }
        sysDictItemService.delete(query);
        return Result.success();
    }

    /**
     * @param sysDictItemId 主键id
     * @return {@link Result}
     * @author xutu
     * @date 2025-10-28 09:22:53
     * @description 详情
     * @menu 系统字典项表管理
     **/
    @GetMapping("/detail/{sysDictItemId}")
    public Result<SysDictItemVO> detail(@PathVariable("sysDictItemId") Long sysDictItemId) {
        return Result.success(sysDictItemService.detail(sysDictItemId));
    }

    /**
     * @param query {@link }
     * @return {@link Result<PageInfoVO>}
     * @author xutu
     * @date 2025-10-28 09:22:53
     * @description 分页列表
     * @menu 系统字典项表管理
     **/
    @RequestMapping(value = "/pageList", method = RequestMethod.POST)
    public Result<PageInfoVO<SysDictItemPageVO>> pageList(@RequestBody SysDictItemPageQuery query) {
        PageInfoVO vo = sysDictItemService.pageList(query);
        return Result.success(vo);
    }

}