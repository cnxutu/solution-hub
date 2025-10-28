/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.dict.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cv.boot.common.enums.ErrorCodeEnum;
import com.cv.boot.common.enums.DeletedEnum;
import com.cv.boot.common.exception.BizException;
import com.cv.solution.formdict.dict.pojo.po.SysDictItemPO;
import com.cv.solution.formdict.dict.pojo.param.SysDictItemAddOrEditParam;
import com.cv.solution.formdict.dict.pojo.query.SysDictItemPageQuery;
import com.cv.solution.formdict.dict.pojo.vo.SysDictItemPageVO;
import com.cv.solution.formdict.dict.pojo.vo.SysDictItemVO;
import com.cv.solution.formdict.dict.mapper.SysDictItemMapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cv.solution.formdict.dict.service.ISysDictItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cv.boot.common.pojo.query.DeletedByIdListQuery;
import com.cv.boot.mybatisplus.pojo.vo.PageInfoVO;
import com.cv.boot.web.response.Result;
import com.cv.boot.mybatisplus.util.PageUtils;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**   
 * 系统字典项表服务实现层
 *
 * @author xutu
 * @date 2025-10-28 09:22:53
 */
@Service
@Slf4j
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItemPO> implements ISysDictItemService  {

    @Resource
    private SysDictItemMapper sysDictItemMapper;

    /**
    * 新增
    *
    * @param param {@link SysDictItemAddOrEditParam}
    * @author xutu
    * @date 2025-10-28 09:22:53
    */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(SysDictItemAddOrEditParam param) {
        // 使用 hutool BeanUtil 进行 Param -> PO 转换
        SysDictItemPO po = new SysDictItemPO();
        BeanUtil.copyProperties(param, po);
        int i = sysDictItemMapper.insert(po);
        if (i <= 0) {
            throw new BizException(ErrorCodeEnum.OPERATION_FAIL);
        }
        return po.getId();
    }

    /**
    * 编辑
    *
    * @param param {@link SysDictItemAddOrEditParam}
    * @author xutu
    * @date 2025-10-28 09:22:53
    */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(SysDictItemAddOrEditParam param) {
        // 使用 hutool BeanUtil 进行 Param -> PO 转换
        SysDictItemPO po = new SysDictItemPO();
        BeanUtil.copyProperties(param, po);
        boolean b = updateById(po);
        if (!b) {
            throw new BizException(ErrorCodeEnum.OPERATION_FAIL);
        }
    }

    /**
    * 删除
    *
    * @param query id
    * @author xutu
    * @date 2025-10-28 09:22:53
    */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(DeletedByIdListQuery query) {
        SysDictItemPO po = new SysDictItemPO();
        po.setIsDeleted(DeletedEnum.DELETED.getCode());
        boolean b = update(po, new UpdateWrapper<SysDictItemPO>()
                .in("id", query.getIdList())
                .eq("is_deleted", DeletedEnum.NORMAL.getCode()));
        if (!b) {
            throw new BizException(ErrorCodeEnum.OPERATION_FAIL);
        }
    }

    /**
     * 分页列表
     *
     * @param query {@link }
     * @return {@link PageInfoVO}
     * @author xutu
     * @date 2025-10-28 09:22:53
     */
    @Override
    public PageInfoVO<SysDictItemPageVO> pageList(SysDictItemPageQuery query) {
        Page page = new Page(query.getCurrent(), query.getSize());
        Page pageList = sysDictItemMapper.selectSysDictItemPageList(page, query);
        return PageUtils.buildPage(pageList);
    }

    /**
     * 详情
     *
     * @param sysDictItemId 主键id
     * @return {@link }
     * @author xutu
     * @date 2025-10-28 09:22:53
     */
    @Override
    public SysDictItemVO detail(Long sysDictItemId) {
        SysDictItemVO vo = new SysDictItemVO();
        SysDictItemPO po = isExistById(sysDictItemId);
        BeanUtil.copyProperties(po, vo);
        return vo;
    }

    private SysDictItemPO isExistById(Long sysDictItemId) {
        if (null == sysDictItemId) {
            throw new BizException(ErrorCodeEnum.REQUEST_PARAM_ERROR, "唯一标识不能为空");
        }
        SysDictItemPO detail = this.getById(sysDictItemId);
        if (null == detail || DeletedEnum.DELETED.getCode().equals(detail.getIsDeleted())) {
            throw new BizException(ErrorCodeEnum.DATA_NOT_FOUND);
        }
        return detail;
    }

}