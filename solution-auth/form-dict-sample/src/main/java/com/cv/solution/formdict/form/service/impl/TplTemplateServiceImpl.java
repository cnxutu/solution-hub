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
import com.cv.solution.formdict.form.pojo.po.TplTemplatePO;
import com.cv.solution.formdict.form.pojo.param.TplTemplateAddOrEditParam;
import com.cv.solution.formdict.form.pojo.query.TplTemplatePageQuery;
import com.cv.solution.formdict.form.pojo.vo.TplTemplatePageVO;
import com.cv.solution.formdict.form.pojo.vo.TplTemplateVO;
import com.cv.solution.formdict.form.mapper.TplTemplateMapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cv.solution.formdict.form.service.ITplTemplateService;
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
 * @date 2025-10-28 19:28:01
 */
@Service
@Slf4j
public class TplTemplateServiceImpl extends ServiceImpl<TplTemplateMapper, TplTemplatePO> implements ITplTemplateService  {

    @Resource
    private TplTemplateMapper tplTemplateMapper;

    /**
     * 分页列表
     *
     * @param query {@link }
     * @return {@link PageInfoVO}
     * @author xutu
     * @date 2025-10-28 19:28:01
     */
    @Override
    public PageInfoVO<TplTemplatePageVO> pageList(TplTemplatePageQuery query) {
        Page page = new Page(query.getCurrent(), query.getSize());
        Page<TplTemplatePageVO> pageList = tplTemplateMapper.selectTplTemplatePageList(page, query);
        return PageUtils.buildPage(pageList);
    }

    /**
    * 新增
    *
    * @param param {@link TplTemplateAddOrEditParam}
    * @author xutu
    * @date 2025-10-28 19:28:01
    */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(TplTemplateAddOrEditParam param) {
        // 使用 hutool BeanUtil 进行 Param -> PO 转换
        TplTemplatePO po = new TplTemplatePO();
        BeanUtil.copyProperties(param, po);
        int i = tplTemplateMapper.insert(po);
        if (i <= 0) {
            throw new BizException(ErrorCodeEnum.OPERATION_FAIL);
        }
        return po.getId();
    }

    /**
    * 编辑
    *
    * @param param {@link TplTemplateAddOrEditParam}
    * @author xutu
    * @date 2025-10-28 19:28:01
    */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(TplTemplateAddOrEditParam param) {
        // 使用 hutool BeanUtil 进行 Param -> PO 转换
        TplTemplatePO po = new TplTemplatePO();
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
    * @date 2025-10-28 19:28:01
    */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(DeletedByIdListQuery query) {
        TplTemplatePO po = new TplTemplatePO();
        po.setIsDeleted(DeletedEnum.DELETED.getCode());
        boolean b = update(po, new UpdateWrapper<TplTemplatePO>()
                .in("id", query.getIdList())
                .eq("is_deleted", DeletedEnum.NORMAL.getCode()));
        if (!b) {
            throw new BizException(ErrorCodeEnum.OPERATION_FAIL);
        }
    }

    /**
     * 详情
     *
     * @param tplTemplateId 主键id
     * @return {@link }
     * @author xutu
     * @date 2025-10-28 19:28:01
     */
    @Override
    public TplTemplateVO detail(Long tplTemplateId) {
        TplTemplateVO vo = new TplTemplateVO();
        TplTemplatePO po = isExistById(tplTemplateId);
        BeanUtil.copyProperties(po, vo);
        return vo;
    }

    private TplTemplatePO isExistById(Long tplTemplateId) {
        if (null == tplTemplateId) {
            throw new BizException(ErrorCodeEnum.REQUEST_PARAM_ERROR, "唯一标识不能为空");
        }
        TplTemplatePO detail = this.getById(tplTemplateId);
        if (null == detail || DeletedEnum.DELETED.getCode().equals(detail.getIsDeleted())) {
            throw new BizException(ErrorCodeEnum.DATA_NOT_FOUND);
        }
        return detail;
    }

}