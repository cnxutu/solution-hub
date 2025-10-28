/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.form.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cv.solution.formdict.form.pojo.po.TemplateDataPO;
import com.cv.solution.formdict.form.pojo.query.TemplateDataPageQuery;
import com.cv.solution.formdict.form.pojo.vo.TemplateDataPageVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 模板录入数据表 持久层
 *
 * @author xutu
 * @date 2025-10-28 19:44:00
 */
public interface TemplateDataMapper extends BaseMapper<TemplateDataPO> {

   /**
   * 分页列表
   */
   Page<TemplateDataPageVO> selectTemplateDataPageList(Page page, @Param("query") TemplateDataPageQuery query);
}
