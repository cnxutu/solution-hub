/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.form.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cv.solution.formdict.form.pojo.po.TplTemplatePO;
import com.cv.solution.formdict.form.pojo.param.TplTemplateAddOrEditParam;
import com.cv.solution.formdict.form.pojo.query.TplTemplatePageQuery;
import com.cv.solution.formdict.form.pojo.vo.TplTemplatePageVO;
import com.cv.solution.formdict.form.pojo.vo.TplTemplateVO;
import com.cv.boot.common.pojo.query.DeletedByIdListQuery;
import com.cv.boot.mybatisplus.pojo.vo.PageInfoVO;

import java.util.List;
/**
 * 模板表 服务接口
 *
 * @author xutu
 * @date 2025-10-28 19:28:01
 */
public interface ITplTemplateService extends IService<TplTemplatePO> {

    /**
     * 分页
     *
     * @param query 入参
     * @return 分页结果
     * @author xutu
     * @date 2025-10-28 19:28:01
     */
    PageInfoVO<TplTemplatePageVO> pageList(TplTemplatePageQuery query);

    /**
     * 新增
     *
     * @param param {@link TplTemplateAddOrEditParam}
     * @return id
     * @author xutu
     * @date 2025-10-28 19:28:01
     */
    Long add(TplTemplateAddOrEditParam param);

    /**
     * 编辑
     *
     * @param param {@link TplTemplateAddOrEditParam}
     * @author xutu
     * @date 2025-10-28 19:28:01
     */
    void edit(TplTemplateAddOrEditParam param);

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
     * @param tplTemplateId 主键id
     * @return {@link }
     * @author xutu
     * @date 2025-10-28 19:28:01
     */
    TplTemplateVO detail(Long tplTemplateId);

}