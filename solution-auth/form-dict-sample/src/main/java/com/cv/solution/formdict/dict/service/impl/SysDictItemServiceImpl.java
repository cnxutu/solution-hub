/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.dict.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cv.boot.common.enums.ErrorCodeEnum;
import com.cv.boot.common.enums.DeletedEnum;
import com.cv.boot.common.exception.BizException;
import com.cv.solution.formdict.dict.pojo.po.SysDictItemPO;
import com.cv.solution.formdict.dict.pojo.param.SysDictItemParam;
import com.cv.solution.formdict.dict.pojo.po.SysDictTypePO;
import com.cv.solution.formdict.dict.pojo.query.SysDictItemPageQuery;
import com.cv.solution.formdict.dict.pojo.vo.SysDictItemPageVO;
import com.cv.solution.formdict.dict.pojo.vo.SysDictItemVO;
import com.cv.solution.formdict.dict.mapper.SysDictItemMapper;
import com.cv.solution.formdict.dict.service.ISysDictItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cv.boot.common.pojo.query.DeletedByIdListQuery;
import com.cv.boot.mybatisplus.pojo.vo.PageInfoVO;
import com.cv.boot.mybatisplus.util.PageUtils;
import com.cv.solution.formdict.dict.service.ISysDictTypeService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 系统字典项表服务实现层
 *
 * @author xutu
 * @date 2025-10-28 09:22:53
 */
@Service
@Slf4j
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItemPO> implements ISysDictItemService {

    @Resource
    private SysDictItemMapper sysDictItemMapper;

    @Resource
    @Lazy
    private ISysDictTypeService sysDictTypeService;

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
     * 新增
     *
     * @param paramList {@link SysDictItemParam}
     * @author xutu
     * @date 2025-10-28 09:22:53
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(List<SysDictItemParam> paramList) {
        // 1、统一判断 字典编码 是否存在
        Set<String> dictCodeSet = paramList.stream().map(SysDictItemParam::getDictCode).collect(Collectors.toSet());
        LambdaQueryWrapper<SysDictTypePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysDictTypePO::getDictCode, dictCodeSet);
        List<SysDictTypePO> dictTypeList = sysDictTypeService.list(queryWrapper);
        Set<String> foundDictCodes = dictTypeList.stream().map(SysDictTypePO::getDictCode).collect(Collectors.toSet());
        // 判断是否有缺失的 dictCode
        if (!foundDictCodes.containsAll(dictCodeSet)) {
            throw new BizException(ErrorCodeEnum.DATA_NOT_FOUND, "字典编码不存在");
        }
        // 2、批量插入执行
        this.saveBatch(BeanUtil.copyToList(paramList, SysDictItemPO.class));
    }

    /**
     * 编辑
     *
     * @param param {@link SysDictItemParam}
     * @author xutu
     * @date 2025-10-28 09:22:53
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(SysDictItemParam param) {
        // 1、判断当前 id 是否存在
        SysDictItemPO po = isExistById(param.getId());
        if (ObjectUtil.isNull(po)) {
            throw new BizException(ErrorCodeEnum.DATA_NOT_FOUND, "字典项不存在");
        }
        // 2、判断 字典编码 是否存在
        LambdaQueryWrapper<SysDictTypePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDictTypePO::getDictCode, param.getDictCode());
        SysDictTypePO one = sysDictTypeService.getOne(queryWrapper);
        if (ObjectUtil.isNull(one)) {
            throw new BizException(ErrorCodeEnum.DATA_NOT_FOUND, "字典编码不存在");
        }
        // 3、执行编辑操作
        SysDictItemPO updatePO = new SysDictItemPO();
        BeanUtil.copyProperties(param, updatePO);
        boolean b = updateById(updatePO);
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