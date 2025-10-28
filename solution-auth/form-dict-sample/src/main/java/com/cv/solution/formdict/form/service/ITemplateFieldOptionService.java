/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.form.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cv.solution.formdict.form.pojo.po.TemplateFieldOptionPO;
import com.cv.solution.formdict.form.pojo.param.TemplateFieldOptionAddOrEditParam;
import com.cv.solution.formdict.form.pojo.query.TemplateFieldOptionPageQuery;
import com.cv.solution.formdict.form.pojo.vo.TemplateFieldOptionPageVO;
import com.cv.solution.formdict.form.pojo.vo.TemplateFieldOptionVO;
import com.cv.boot.common.pojo.query.DeletedByIdListQuery;
import com.cv.boot.mybatisplus.pojo.vo.PageInfoVO;

import java.util.List;
/**
 * 模板字段专属可选项表 服务接口
 *
 * @author xutu
 * @date 2025-10-28 19:44:00
 */
public interface ITemplateFieldOptionService extends IService<TemplateFieldOptionPO> {

    /**
     * 分页
     *
     * @param query 入参
     * @return 分页结果
     * @author xutu
     * @date 2025-10-28 19:44:00
     */
    PageInfoVO<TemplateFieldOptionPageVO> pageList(TemplateFieldOptionPageQuery query);

    /**
     * 新增
     *
     * @param param {@link TemplateFieldOptionAddOrEditParam}
     * @return id
     * @author xutu
     * @date 2025-10-28 19:44:00
     */
    Long add(TemplateFieldOptionAddOrEditParam param);

    /**
     * 编辑
     *
     * @param param {@link TemplateFieldOptionAddOrEditParam}
     * @author xutu
     * @date 2025-10-28 19:44:00
     */
    void edit(TemplateFieldOptionAddOrEditParam param);

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
     * @param templateFieldOptionId 主键id
     * @return {@link }
     * @author xutu
     * @date 2025-10-28 19:44:00
     */
    TemplateFieldOptionVO detail(Long templateFieldOptionId);

}