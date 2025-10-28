/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.form.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cv.solution.formdict.form.pojo.po.TplTemplateFieldPO;
import com.cv.solution.formdict.form.pojo.query.TplTemplateFieldPageQuery;
import com.cv.solution.formdict.form.pojo.vo.TplTemplateFieldPageVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 模板字段表 持久层
 *
 * @author xutu
 * @date 2025-10-28 19:28:01
 */
public interface TplTemplateFieldMapper extends BaseMapper<TplTemplateFieldPO> {

   /**
   * 分页列表
   */
   Page<TplTemplateFieldPageVO> selectTplTemplateFieldPageList(Page page, @Param("query") TplTemplateFieldPageQuery query);
}
