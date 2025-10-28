/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.form.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cv.solution.formdict.form.pojo.po.TemplateFieldPO;
import com.cv.solution.formdict.form.pojo.param.TemplateFieldAddOrEditParam;
import com.cv.solution.formdict.form.pojo.query.TemplateFieldPageQuery;
import com.cv.solution.formdict.form.pojo.vo.TemplateFieldPageVO;
import com.cv.solution.formdict.form.pojo.vo.TemplateFieldVO;
import com.cv.boot.common.pojo.query.DeletedByIdListQuery;
import com.cv.boot.mybatisplus.pojo.vo.PageInfoVO;

import java.util.List;
/**
 * 模板字段表 服务接口
 *
 * @author xutu
 * @date 2025-10-28 19:44:00
 */
public interface ITemplateFieldService extends IService<TemplateFieldPO> {

    /**
     * 分页
     *
     * @param query 入参
     * @return 分页结果
     * @author xutu
     * @date 2025-10-28 19:44:00
     */
    PageInfoVO<TemplateFieldPageVO> pageList(TemplateFieldPageQuery query);

    /**
     * 新增
     *
     * @param param {@link TemplateFieldAddOrEditParam}
     * @return id
     * @author xutu
     * @date 2025-10-28 19:44:00
     */
    Long add(TemplateFieldAddOrEditParam param);

    /**
     * 编辑
     *
     * @param param {@link TemplateFieldAddOrEditParam}
     * @author xutu
     * @date 2025-10-28 19:44:00
     */
    void edit(TemplateFieldAddOrEditParam param);

    /**
     * 删除
     *
     * @param query id集合
     * @author xutu
     * @date 2025-10-28 19:44:00
     */
    void delete(DeletedByIdListQuery query);

    /**
     * 详情
     *
     * @param templateFieldId 主键id
     * @return {@link }
     * @author xutu
     * @date 2025-10-28 19:44:00
     */
    TemplateFieldVO detail(Long templateFieldId);

}