/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.form.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cv.boot.common.enums.ErrorCodeEnum;
import com.cv.boot.common.enums.DeletedEnum;
import com.cv.boot.common.exception.BizException;
import com.cv.solution.formdict.form.pojo.po.TemplateFieldPO;
import com.cv.solution.formdict.form.pojo.param.TemplateFieldParam;
import com.cv.solution.formdict.form.pojo.query.TemplateFieldPageQuery;
import com.cv.solution.formdict.form.pojo.vo.TemplateFieldPageVO;
import com.cv.solution.formdict.form.pojo.vo.TemplateFieldVO;
import com.cv.solution.formdict.form.mapper.TemplateFieldMapper;
import com.cv.solution.formdict.form.service.ITemplateFieldService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cv.boot.common.pojo.query.DeletedByIdListQuery;
import com.cv.boot.mybatisplus.pojo.vo.PageInfoVO;
import com.cv.boot.mybatisplus.util.PageUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import javax.annotation.Resource;

/**   
 * 模板字段表服务实现层
 *
 * @author xutu
 * @date 2025-10-28 19:44:00
 */
@Service
@Slf4j
public class TemplateFieldServiceImpl extends ServiceImpl<TemplateFieldMapper, TemplateFieldPO> implements ITemplateFieldService  {

    @Resource
    private TemplateFieldMapper templateFieldMapper;

    /**
     * 分页列表
     *
     * @param query {@link }
     * @return {@link PageInfoVO}
     * @author xutu
     * @date 2025-10-28 19:44:00
     */
    @Override
    public PageInfoVO<TemplateFieldPageVO> pageList(TemplateFieldPageQuery query) {
        Page page = new Page(query.getCurrent(), query.getSize());
        Page<TemplateFieldPageVO> pageList = templateFieldMapper.selectTemplateFieldPageList(page, query);
        return PageUtils.buildPage(pageList);
    }

    /**
    * 新增
    *
    * @param param {@link TemplateFieldParam}
    * @author xutu
    * @date 2025-10-28 19:44:00
    */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(TemplateFieldParam param) {
        // 使用 hutool BeanUtil 进行 Param -> PO 转换
        TemplateFieldPO po = new TemplateFieldPO();
        BeanUtil.copyProperties(param, po);
        int i = templateFieldMapper.insert(po);
        if (i <= 0) {
            throw new BizException(ErrorCodeEnum.OPERATION_FAIL);
        }
        return po.getId();
    }

    /**
    * 编辑
    *
    * @param param {@link TemplateFieldParam}
    * @author xutu
    * @date 2025-10-28 19:44:00
    */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(TemplateFieldParam param) {
        // 使用 hutool BeanUtil 进行 Param -> PO 转换
        TemplateFieldPO po = new TemplateFieldPO();
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
        TemplateFieldPO po = new TemplateFieldPO();
        po.setIsDeleted(DeletedEnum.DELETED.getCode());
        boolean b = update(po, new UpdateWrapper<TemplateFieldPO>()
                .in("id", query.getIdList())
                .eq("is_deleted", DeletedEnum.NORMAL.getCode()));
        if (!b) {
            throw new BizException(ErrorCodeEnum.OPERATION_FAIL);
        }
    }

    /**
     * 详情
     *
     * @param templateFieldId 主键id
     * @return {@link }
     * @author xutu
     * @date 2025-10-28 19:44:00
     */
    @Override
    public TemplateFieldVO detail(Long templateFieldId) {
        TemplateFieldVO vo = new TemplateFieldVO();
        TemplateFieldPO po = isExistById(templateFieldId);
        BeanUtil.copyProperties(po, vo);
        return vo;
    }

    private TemplateFieldPO isExistById(Long templateFieldId) {
        if (null == templateFieldId) {
            throw new BizException(ErrorCodeEnum.REQUEST_PARAM_ERROR, "唯一标识不能为空");
        }
        TemplateFieldPO detail = this.getById(templateFieldId);
        if (null == detail || DeletedEnum.DELETED.getCode().equals(detail.getIsDeleted())) {
            throw new BizException(ErrorCodeEnum.DATA_NOT_FOUND);
        }
        return detail;
    }

}