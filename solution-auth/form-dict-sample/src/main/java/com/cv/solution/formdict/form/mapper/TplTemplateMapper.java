/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.form.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cv.solution.formdict.form.pojo.po.TplTemplatePO;
import com.cv.solution.formdict.form.pojo.query.TplTemplatePageQuery;
import com.cv.solution.formdict.form.pojo.vo.TplTemplatePageVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 模板表 持久层
 *
 * @author xutu
 * @date 2025-10-28 19:28:01
 */
public interface TplTemplateMapper extends BaseMapper<TplTemplatePO> {

   /**
   * 分页列表
   */
   Page<TplTemplatePageVO> selectTplTemplatePageList(Page page, @Param("query") TplTemplatePageQuery query);
}
