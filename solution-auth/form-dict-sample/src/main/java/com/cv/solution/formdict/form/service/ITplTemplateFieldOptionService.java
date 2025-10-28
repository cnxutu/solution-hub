/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.form.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cv.solution.formdict.form.pojo.po.TplTemplateFieldOptionPO;
import com.cv.solution.formdict.form.pojo.param.TplTemplateFieldOptionAddOrEditParam;
import com.cv.solution.formdict.form.pojo.query.TplTemplateFieldOptionPageQuery;
import com.cv.solution.formdict.form.pojo.vo.TplTemplateFieldOptionPageVO;
import com.cv.solution.formdict.form.pojo.vo.TplTemplateFieldOptionVO;
import com.cv.boot.common.pojo.query.DeletedByIdListQuery;
import com.cv.boot.mybatisplus.pojo.vo.PageInfoVO;

import java.util.List;
/**
 * 模板字段专属可选项表 服务接口
 *
 * @author xutu
 * @date 2025-10-28 19:28:01
 */
public interface ITplTemplateFieldOptionService extends IService<TplTemplateFieldOptionPO> {

    /**
     * 分页
     *
     * @param query 入参
     * @return 分页结果
     * @author xutu
     * @date 2025-10-28 19:28:01
     */
    PageInfoVO<TplTemplateFieldOptionPageVO> pageList(TplTemplateFieldOptionPageQuery query);

    /**
     * 新增
     *
     * @param param {@link TplTemplateFieldOptionAddOrEditParam}
     * @return id
     * @author xutu
     * @date 2025-10-28 19:28:01
     */
    Long add(TplTemplateFieldOptionAddOrEditParam param);

    /**
     * 编辑
     *
     * @param param {@link TplTemplateFieldOptionAddOrEditParam}
     * @author xutu
     * @date 2025-10-28 19:28:01
     */
    void edit(TplTemplateFieldOptionAddOrEditParam param);

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
     * @param tplTemplateFieldOptionId 主键id
     * @return {@link }
     * @author xutu
     * @date 2025-10-28 19:28:01
     */
    TplTemplateFieldOptionVO detail(Long tplTemplateFieldOptionId);

}