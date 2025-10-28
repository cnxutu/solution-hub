/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.form.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cv.solution.formdict.form.pojo.po.TemplateFieldOptionPO;
import com.cv.solution.formdict.form.pojo.query.TemplateFieldOptionPageQuery;
import com.cv.solution.formdict.form.pojo.vo.TemplateFieldOptionPageVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 模板字段专属可选项表 持久层
 *
 * @author xutu
 * @date 2025-10-28 19:44:00
 */
public interface TemplateFieldOptionMapper extends BaseMapper<TemplateFieldOptionPO> {

   /**
   * 分页列表
   */
   Page<TemplateFieldOptionPageVO> selectTemplateFieldOptionPageList(Page page, @Param("query") TemplateFieldOptionPageQuery query);
}
