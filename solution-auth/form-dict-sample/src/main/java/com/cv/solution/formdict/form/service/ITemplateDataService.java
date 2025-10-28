/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.form.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cv.solution.formdict.form.pojo.po.TemplateDataPO;
import com.cv.solution.formdict.form.pojo.param.TemplateDataAddOrEditParam;
import com.cv.solution.formdict.form.pojo.query.TemplateDataPageQuery;
import com.cv.solution.formdict.form.pojo.vo.TemplateDataPageVO;
import com.cv.solution.formdict.form.pojo.vo.TemplateDataVO;
import com.cv.boot.common.pojo.query.DeletedByIdListQuery;
import com.cv.boot.mybatisplus.pojo.vo.PageInfoVO;

import java.util.List;
/**
 * 模板录入数据表 服务接口
 *
 * @author xutu
 * @date 2025-10-28 19:44:00
 */
public interface ITemplateDataService extends IService<TemplateDataPO> {

    /**
     * 分页
     *
     * @param query 入参
     * @return 分页结果
     * @author xutu
     * @date 2025-10-28 19:44:00
     */
    PageInfoVO<TemplateDataPageVO> pageList(TemplateDataPageQuery query);

    /**
     * 新增
     *
     * @param param {@link TemplateDataAddOrEditParam}
     * @return id
     * @author xutu
     * @date 2025-10-28 19:44:00
     */
    Long add(TemplateDataAddOrEditParam param);

    /**
     * 编辑
     *
     * @param param {@link TemplateDataAddOrEditParam}
     * @author xutu
     * @date 2025-10-28 19:44:00
     */
    void edit(TemplateDataAddOrEditParam param);

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
     * @param templateDataId 主键id
     * @return {@link }
     * @author xutu
     * @date 2025-10-28 19:44:00
     */
    TemplateDataVO detail(Long templateDataId);

}