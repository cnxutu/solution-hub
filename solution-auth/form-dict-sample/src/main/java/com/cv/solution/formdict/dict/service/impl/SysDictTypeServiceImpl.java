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
import com.cv.solution.formdict.dict.pojo.po.SysDictTypePO;
import com.cv.solution.formdict.dict.pojo.param.SysDictTypeAddOrEditParam;
import com.cv.solution.formdict.dict.pojo.query.SysDictTypePageQuery;
import com.cv.solution.formdict.dict.pojo.vo.SysDictTypePageVO;
import com.cv.solution.formdict.dict.pojo.vo.SysDictTypeVO;
import com.cv.solution.formdict.dict.mapper.SysDictTypeMapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cv.solution.formdict.dict.service.ISysDictTypeService;
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
 * 系统字典类型表服务实现层
 *
 * @author xutu
 * @date 2025-10-28 09:22:53
 */
@Service
@Slf4j
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictTypePO> implements ISysDictTypeService  {

    @Resource
    private SysDictTypeMapper sysDictTypeMapper;

    /**
    * 新增
    *
    * @param param {@link SysDictTypeAddOrEditParam}
    * @author xutu
    * @date 2025-10-28 09:22:53
    */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(SysDictTypeAddOrEditParam param) {
        // 使用 hutool BeanUtil 进行 Param -> PO 转换
        SysDictTypePO po = new SysDictTypePO();
        BeanUtil.copyProperties(param, po);
        int i = sysDictTypeMapper.insert(po);
        if (i <= 0) {
            throw new BizException(ErrorCodeEnum.OPERATION_FAIL);
        }
        return po.getId();
    }

    /**
    * 编辑
    *
    * @param param {@link SysDictTypeAddOrEditParam}
    * @author xutu
    * @date 2025-10-28 09:22:53
    */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(SysDictTypeAddOrEditParam param) {
        // 使用 hutool BeanUtil 进行 Param -> PO 转换
        SysDictTypePO po = new SysDictTypePO();
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
        SysDictTypePO po = new SysDictTypePO();
        po.setIsDeleted(DeletedEnum.DELETED.getCode());
        boolean b = update(po, new UpdateWrapper<SysDictTypePO>()
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
    public PageInfoVO<SysDictTypePageVO> pageList(SysDictTypePageQuery query) {
        Page page = new Page(query.getCurrent(), query.getSize());
        Page pageList = sysDictTypeMapper.selectSysDictTypePageList(page, query);
        return PageUtils.buildPage(pageList);
    }

    /**
     * 详情
     *
     * @param sysDictTypeId 主键id
     * @return {@link }
     * @author xutu
     * @date 2025-10-28 09:22:53
     */
    @Override
    public SysDictTypeVO detail(Long sysDictTypeId) {
        SysDictTypeVO vo = new SysDictTypeVO();
        SysDictTypePO po = isExistById(sysDictTypeId);
        return BeanUtil.copyProperties(po, vo);
    }

    private SysDictTypePO isExistById(Long sysDictTypeId) {
        if (null == sysDictTypeId) {
            throw new BizException(ErrorCodeEnum.REQUEST_PARAM_ERROR, "唯一标识不能为空");
        }
        SysDictTypePO detail = this.getById(sysDictTypeId);
        if (null == detail || DeletedEnum.DELETED.getCode().equals(detail.getIsDeleted())) {
            throw new BizException(ErrorCodeEnum.DATA_NOT_FOUND);
        }
        return detail;
    }

}