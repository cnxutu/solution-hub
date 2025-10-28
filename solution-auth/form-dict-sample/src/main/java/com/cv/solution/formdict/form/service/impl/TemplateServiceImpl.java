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
import com.cv.solution.formdict.form.pojo.po.TemplatePO;
import com.cv.solution.formdict.form.pojo.param.TemplateAddOrEditParam;
import com.cv.solution.formdict.form.pojo.query.TemplatePageQuery;
import com.cv.solution.formdict.form.pojo.vo.TemplatePageVO;
import com.cv.solution.formdict.form.pojo.vo.TemplateVO;
import com.cv.solution.formdict.form.mapper.TemplateMapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cv.solution.formdict.form.service.ITemplateService;
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
 * 模板表服务实现层
 *
 * @author xutu
 * @date 2025-10-28 19:44:00
 */
@Service
@Slf4j
public class TemplateServiceImpl extends ServiceImpl<TemplateMapper, TemplatePO> implements ITemplateService  {

    @Resource
    private TemplateMapper templateMapper;

    /**
     * 分页列表
     *
     * @param query {@link }
     * @return {@link PageInfoVO}
     * @author xutu
     * @date 2025-10-28 19:44:00
     */
    @Override
    public PageInfoVO<TemplatePageVO> pageList(TemplatePageQuery query) {
        Page page = new Page(query.getCurrent(), query.getSize());
        Page<TemplatePageVO> pageList = templateMapper.selectTemplatePageList(page, query);
        return PageUtils.buildPage(pageList);
    }

    /**
    * 新增
    *
    * @param param {@link TemplateAddOrEditParam}
    * @author xutu
    * @date 2025-10-28 19:44:00
    */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(TemplateAddOrEditParam param) {
        // 使用 hutool BeanUtil 进行 Param -> PO 转换
        TemplatePO po = new TemplatePO();
        BeanUtil.copyProperties(param, po);
        int i = templateMapper.insert(po);
        if (i <= 0) {
            throw new BizException(ErrorCodeEnum.OPERATION_FAIL);
        }
        return po.getId();
    }

    /**
    * 编辑
    *
    * @param param {@link TemplateAddOrEditParam}
    * @author xutu
    * @date 2025-10-28 19:44:00
    */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(TemplateAddOrEditParam param) {
        // 使用 hutool BeanUtil 进行 Param -> PO 转换
        TemplatePO po = new TemplatePO();
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
        TemplatePO po = new TemplatePO();
        po.setIsDeleted(DeletedEnum.DELETED.getCode());
        boolean b = update(po, new UpdateWrapper<TemplatePO>()
                .in("id", query.getIdList())
                .eq("is_deleted", DeletedEnum.NORMAL.getCode()));
        if (!b) {
            throw new BizException(ErrorCodeEnum.OPERATION_FAIL);
        }
    }

    /**
     * 详情
     *
     * @param templateId 主键id
     * @return {@link }
     * @author xutu
     * @date 2025-10-28 19:44:00
     */
    @Override
    public TemplateVO detail(Long templateId) {
        TemplateVO vo = new TemplateVO();
        TemplatePO po = isExistById(templateId);
        BeanUtil.copyProperties(po, vo);
        return vo;
    }

    private TemplatePO isExistById(Long templateId) {
        if (null == templateId) {
            throw new BizException(ErrorCodeEnum.REQUEST_PARAM_ERROR, "唯一标识不能为空");
        }
        TemplatePO detail = this.getById(templateId);
        if (null == detail || DeletedEnum.DELETED.getCode().equals(detail.getIsDeleted())) {
            throw new BizException(ErrorCodeEnum.DATA_NOT_FOUND);
        }
        return detail;
    }

}