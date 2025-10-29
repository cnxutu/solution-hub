/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.form.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cv.solution.formdict.form.pojo.po.TemplatePO;
import com.cv.solution.formdict.form.pojo.param.TemplateParam;
import com.cv.solution.formdict.form.pojo.query.TemplatePageQuery;
import com.cv.solution.formdict.form.pojo.vo.TemplatePageVO;
import com.cv.solution.formdict.form.pojo.vo.TemplateVO;
import com.cv.boot.common.pojo.query.DeletedByIdListQuery;
import com.cv.boot.mybatisplus.pojo.vo.PageInfoVO;

/**
 * 模板表 服务接口
 *
 * @author xutu
 * @date 2025-10-28 19:44:00
 */
public interface ITemplateService extends IService<TemplatePO> {

    /**
     * 分页
     *
     * @param query 入参
     * @return 分页结果
     * @author xutu
     * @date 2025-10-28 19:44:00
     */
    PageInfoVO<TemplatePageVO> pageList(TemplatePageQuery query);

    /**
     * 新增
     *
     * @param param {@link TemplateParam}
     * @return id
     * @author xutu
     * @date 2025-10-28 19:44:00
     */
    Long add(TemplateParam param);

    /**
     * 编辑
     *
     * @param param {@link TemplateParam}
     * @author xutu
     * @date 2025-10-28 19:44:00
     */
    void edit(TemplateParam param);

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
     * @param templateId 主键id
     * @return {@link }
     * @author xutu
     * @date 2025-10-28 19:44:00
     */
    TemplateVO detail(Long templateId);

}