/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.form.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cv.boot.common.enums.ErrorCodeEnum;
import com.cv.boot.common.enums.DeletedEnum;
import com.cv.boot.common.exception.BizException;
import com.cv.solution.formdict.form.pojo.po.TemplateDataPO;
import com.cv.solution.formdict.form.pojo.param.TemplateDataAddOrEditParam;
import com.cv.solution.formdict.form.pojo.query.TemplateDataPageQuery;
import com.cv.solution.formdict.form.pojo.vo.TemplateDataPageVO;
import com.cv.solution.formdict.form.pojo.vo.TemplateDataVO;
import com.cv.solution.formdict.form.mapper.TemplateDataMapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cv.solution.formdict.form.service.ITemplateDataService;
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
 * 模板录入数据表服务实现层
 *
 * @author xutu
 * @date 2025-10-28 19:44:00
 */
@Service
@Slf4j
public class TemplateDataServiceImpl extends ServiceImpl<TemplateDataMapper, TemplateDataPO> implements ITemplateDataService  {

    @Resource
    private TemplateDataMapper templateDataMapper;

    /**
     * 分页列表
     *
     * @param query {@link }
     * @return {@link PageInfoVO}
     * @author xutu
     * @date 2025-10-28 19:44:00
     */
    @Override
    public PageInfoVO<TemplateDataPageVO> pageList(TemplateDataPageQuery query) {
        Page page = new Page(query.getCurrent(), query.getSize());
        Page<TemplateDataPageVO> pageList = templateDataMapper.selectTemplateDataPageList(page, query);
        return PageUtils.buildPage(pageList);
    }

    /**
    * 新增
    *
    * @param param {@link TemplateDataAddOrEditParam}
    * @author xutu
    * @date 2025-10-28 19:44:00
    */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(TemplateDataAddOrEditParam param) {
        // 使用 hutool BeanUtil 进行 Param -> PO 转换
        TemplateDataPO po = new TemplateDataPO();
        BeanUtil.copyProperties(param, po);
        int i = templateDataMapper.insert(po);
        if (i <= 0) {
            throw new BizException(ErrorCodeEnum.OPERATION_FAIL);
        }
        return po.getId();
    }

    /**
    * 编辑
    *
    * @param param {@link TemplateDataAddOrEditParam}
    * @author xutu
    * @date 2025-10-28 19:44:00
    */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(TemplateDataAddOrEditParam param) {
        // 使用 hutool BeanUtil 进行 Param -> PO 转换
        TemplateDataPO po = new TemplateDataPO();
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
    * @date 2025-10-28 19:44:00
    */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(DeletedByIdListQuery query) {
        TemplateDataPO po = new TemplateDataPO();
        po.setIsDeleted(DeletedEnum.DELETED.getCode());
        boolean b = update(po, new UpdateWrapper<TemplateDataPO>()
                .in("id", query.getIdList())
                .eq("is_deleted", DeletedEnum.NORMAL.getCode()));
        if (!b) {
            throw new BizException(ErrorCodeEnum.OPERATION_FAIL);
        }
    }

    /**
     * 详情
     *
     * @param templateDataId 主键id
     * @return {@link }
     * @author xutu
     * @date 2025-10-28 19:44:00
     */
    @Override
    public TemplateDataVO detail(Long templateDataId) {
        TemplateDataVO vo = new TemplateDataVO();
        TemplateDataPO po = isExistById(templateDataId);
        BeanUtil.copyProperties(po, vo);
        return vo;
    }

    private TemplateDataPO isExistById(Long templateDataId) {
        if (null == templateDataId) {
            throw new BizException(ErrorCodeEnum.REQUEST_PARAM_ERROR, "唯一标识不能为空");
        }
        TemplateDataPO detail = this.getById(templateDataId);
        if (null == detail || DeletedEnum.DELETED.getCode().equals(detail.getIsDeleted())) {
            throw new BizException(ErrorCodeEnum.DATA_NOT_FOUND);
        }
        return detail;
    }

}