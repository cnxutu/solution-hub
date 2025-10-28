/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.form.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cv.solution.formdict.form.pojo.po.TemplateFieldPO;
import com.cv.solution.formdict.form.pojo.query.TemplateFieldPageQuery;
import com.cv.solution.formdict.form.pojo.vo.TemplateFieldPageVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 模板字段表 持久层
 *
 * @author xutu
 * @date 2025-10-28 19:44:00
 */
public interface TemplateFieldMapper extends BaseMapper<TemplateFieldPO> {

   /**
   * 分页列表
   */
   Page<TemplateFieldPageVO> selectTemplateFieldPageList(Page page, @Param("query") TemplateFieldPageQuery query);
}
