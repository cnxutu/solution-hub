/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.dict.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cv.solution.formdict.dict.pojo.po.SysDictTypePO;
import com.cv.solution.formdict.dict.pojo.query.SysDictTypePageQuery;
import com.cv.solution.formdict.dict.pojo.vo.SysDictTypePageVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统字典类型表 持久层
 *
 * @author xutu
 * @date 2025-10-28 09:22:53
 */
public interface SysDictTypeMapper extends BaseMapper<SysDictTypePO> {

   /**
   * 分页列表
   */
   Page<SysDictTypePageVO> selectSysDictTypePageList(Page page, @Param("query") SysDictTypePageQuery query);
}
