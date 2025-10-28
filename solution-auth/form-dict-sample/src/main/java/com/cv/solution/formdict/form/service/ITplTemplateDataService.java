/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.form.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cv.solution.formdict.form.pojo.po.TplTemplateDataPO;
import com.cv.solution.formdict.form.pojo.param.TplTemplateDataAddOrEditParam;
import com.cv.solution.formdict.form.pojo.query.TplTemplateDataPageQuery;
import com.cv.solution.formdict.form.pojo.vo.TplTemplateDataPageVO;
import com.cv.solution.formdict.form.pojo.vo.TplTemplateDataVO;
import com.cv.boot.common.pojo.query.DeletedByIdListQuery;
import com.cv.boot.mybatisplus.pojo.vo.PageInfoVO;

import java.util.List;
/**
 * 模板录入数据表 服务接口
 *
 * @author xutu
 * @date 2025-10-28 19:28:01
 */
public interface ITplTemplateDataService extends IService<TplTemplateDataPO> {

    /**
     * 分页
     *
     * @param query 入参
     * @return 分页结果
     * @author xutu
     * @date 2025-10-28 19:28:01
     */
    PageInfoVO<TplTemplateDataPageVO> pageList(TplTemplateDataPageQuery query);

    /**
     * 新增
     *
     * @param param {@link TplTemplateDataAddOrEditParam}
     * @return id
     * @author xutu
     * @date 2025-10-28 19:28:01
     */
    Long add(TplTemplateDataAddOrEditParam param);

    /**
     * 编辑
     *
     * @param param {@link TplTemplateDataAddOrEditParam}
     * @author xutu
     * @date 2025-10-28 19:28:01
     */
    void edit(TplTemplateDataAddOrEditParam param);

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
     * @param tplTemplateDataId 主键id
     * @return {@link }
     * @author xutu
     * @date 2025-10-28 19:28:01
     */
    TplTemplateDataVO detail(Long tplTemplateDataId);

}