/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.form.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cv.solution.formdict.form.pojo.po.TplTemplateFieldPO;
import com.cv.solution.formdict.form.pojo.param.TplTemplateFieldAddOrEditParam;
import com.cv.solution.formdict.form.pojo.query.TplTemplateFieldPageQuery;
import com.cv.solution.formdict.form.pojo.vo.TplTemplateFieldPageVO;
import com.cv.solution.formdict.form.pojo.vo.TplTemplateFieldVO;
import com.cv.boot.common.pojo.query.DeletedByIdListQuery;
import com.cv.boot.mybatisplus.pojo.vo.PageInfoVO;

import java.util.List;
/**
 * 模板字段表 服务接口
 *
 * @author xutu
 * @date 2025-10-28 19:28:01
 */
public interface ITplTemplateFieldService extends IService<TplTemplateFieldPO> {

    /**
     * 分页
     *
     * @param query 入参
     * @return 分页结果
     * @author xutu
     * @date 2025-10-28 19:28:01
     */
    PageInfoVO<TplTemplateFieldPageVO> pageList(TplTemplateFieldPageQuery query);

    /**
     * 新增
     *
     * @param param {@link TplTemplateFieldAddOrEditParam}
     * @return id
     * @author xutu
     * @date 2025-10-28 19:28:01
     */
    Long add(TplTemplateFieldAddOrEditParam param);

    /**
     * 编辑
     *
     * @param param {@link TplTemplateFieldAddOrEditParam}
     * @author xutu
     * @date 2025-10-28 19:28:01
     */
    void edit(TplTemplateFieldAddOrEditParam param);

    /**
     * 删除
     *
     * @param query id集合
     * @author xutu
     * @date 2025-10-28 19:28:01
     */
    void delete(DeletedByIdListQuery query);

    /**
     * 详情
     *
     * @param tplTemplateFieldId 主键id
     * @return {@link }
     * @author xutu
     * @date 2025-10-28 19:28:01
     */
    TplTemplateFieldVO detail(Long tplTemplateFieldId);

}