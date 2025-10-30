/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.dict.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cv.solution.formdict.dict.pojo.po.SysDictTypePO;
import com.cv.solution.formdict.dict.pojo.param.SysDictTypeParam;
import com.cv.solution.formdict.dict.pojo.query.SysDictTypePageQuery;
import com.cv.solution.formdict.dict.pojo.vo.SysDictTypePageVO;
import com.cv.solution.formdict.dict.pojo.vo.SysDictTypeVO;
import com.cv.boot.common.pojo.query.DeletedByIdListQuery;
import com.cv.boot.mybatisplus.pojo.vo.PageInfoVO;

/**
 * 系统字典类型表 服务接口
 *
 * @author xutu
 * @date 2025-10-28 09:22:53
 */
public interface ISysDictTypeService extends IService<SysDictTypePO> {

    /**
     * 分页
     *
     * @param query 入参
     * @return 分页结果
     * @author xutu
     * @date 2025-10-28 09:22:53
     */
    PageInfoVO<SysDictTypePageVO> pageList(SysDictTypePageQuery query);

    /**
     * 新增
     *
     * @param param {@link SysDictTypeParam}
     * @return id
     * @author xutu
     * @date 2025-10-28 09:22:53
     */
    Long add(SysDictTypeParam param);

    /**
     * 编辑
     *
     * @param param {@link SysDictTypeParam}
     * @author xutu
     * @date 2025-10-28 09:22:53
     */
    void edit(SysDictTypeParam param);

    /**
     * 删除
     *
     * @param query id集合
     * @author xutu
     * @date 2025-10-28 09:22:53
     */
    void delete(DeletedByIdListQuery query);

    /**
     * 详情
     *
     * @param sysDictTypeId 主键id
     * @return {@link }
     * @author xutu
     * @date 2025-10-28 09:22:53
     */
    SysDictTypeVO detail(Long sysDictTypeId);

}