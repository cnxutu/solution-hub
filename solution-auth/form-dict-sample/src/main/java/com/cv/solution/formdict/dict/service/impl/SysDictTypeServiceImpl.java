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
import com.cv.solution.formdict.dict.pojo.po.SysDictTypePO;
import com.cv.solution.formdict.dict.pojo.param.SysDictTypeParam;
import com.cv.solution.formdict.dict.pojo.query.SysDictTypePageQuery;
import com.cv.solution.formdict.dict.pojo.vo.SysDictTypePageVO;
import com.cv.solution.formdict.dict.pojo.vo.SysDictTypeVO;
import com.cv.solution.formdict.dict.mapper.SysDictTypeMapper;
import com.cv.solution.formdict.dict.service.ISysDictItemService;
import com.cv.solution.formdict.dict.service.ISysDictTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cv.boot.common.pojo.query.DeletedByIdListQuery;
import com.cv.boot.mybatisplus.pojo.vo.PageInfoVO;
import com.cv.boot.mybatisplus.util.PageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统字典类型表服务实现层
 *
 * @author xutu
 * @date 2025-10-28 09:22:53
 */
@Service
@Slf4j
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictTypePO> implements ISysDictTypeService {

    @Resource
    private ISysDictItemService sysDictItemService;

    @Resource
    private SysDictTypeMapper sysDictTypeMapper;

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
     * 新增
     *
     * @param param {@link SysDictTypeParam}
     * @author xutu
     * @date 2025-10-28 09:22:53
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(SysDictTypeParam param) {
        // 1、若为新增操作，查询 DICT_CODE 是否已存在
        QueryWrapper<SysDictTypePO> eq = new QueryWrapper<SysDictTypePO>()
                .eq("dict_code", param.getDictCode())
                .eq("is_deleted", DeletedEnum.NORMAL.getCode());
        if (this.count(eq) > 0) {
            throw new BizException(ErrorCodeEnum.OPERATION_FAIL, "字典编码已存在");
        }
        // 2、执行新增操作
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
     * @param param {@link SysDictTypeParam}
     * @author xutu
     * @date 2025-10-28 09:22:53
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(SysDictTypeParam param) {
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
        // 1、查询所有的字典类型
        List<SysDictTypePO> sysDictTypePOS = this.listByIds(query.getIdList());
        if (CollUtil.isEmpty(sysDictTypePOS)) {
            return;
        }
        // 2、查询当前 dict_code 下是否有字典项
        LambdaQueryWrapper<SysDictItemPO> itemQueryWrapper = new LambdaQueryWrapper<>();
        itemQueryWrapper
                .in(SysDictItemPO::getDictCode, sysDictTypePOS.stream().map(SysDictTypePO::getDictCode).collect(Collectors.toList()))
                .eq(SysDictItemPO::getIsDeleted, DeletedEnum.NORMAL.getCode());
        long count = sysDictItemService.count(itemQueryWrapper);
        if (count > 0) {
            throw new BizException(ErrorCodeEnum.OPERATION_FAIL, "字典类型下有字典项，请先删除字典项");
        }
        // 3、执行删除操作
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
        BeanUtil.copyProperties(po, vo);
        return vo;
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