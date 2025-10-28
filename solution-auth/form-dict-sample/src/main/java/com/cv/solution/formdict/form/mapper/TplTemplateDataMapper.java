/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.form.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cv.solution.formdict.form.pojo.po.TplTemplateDataPO;
import com.cv.solution.formdict.form.pojo.query.TplTemplateDataPageQuery;
import com.cv.solution.formdict.form.pojo.vo.TplTemplateDataPageVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 模板录入数据表 持久层
 *
 * @author xutu
 * @date 2025-10-28 19:28:01
 */
public interface TplTemplateDataMapper extends BaseMapper<TplTemplateDataPO> {

   /**
   * 分页列表
   */
   Page<TplTemplateDataPageVO> selectTplTemplateDataPageList(Page page, @Param("query") TplTemplateDataPageQuery query);
}
