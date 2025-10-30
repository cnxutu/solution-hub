/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.dict.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cv.solution.formdict.dict.pojo.po.SysDictItemPO;
import com.cv.solution.formdict.dict.pojo.param.SysDictItemParam;
import com.cv.solution.formdict.dict.pojo.query.SysDictItemPageQuery;
import com.cv.solution.formdict.dict.pojo.vo.SysDictItemPageVO;
import com.cv.solution.formdict.dict.pojo.vo.SysDictItemVO;
import com.cv.boot.common.pojo.query.DeletedByIdListQuery;
import com.cv.boot.mybatisplus.pojo.vo.PageInfoVO;

import java.util.List;

/**
 * 系统字典项表 服务接口
 *
 * @author xutu
 * @date 2025-10-28 09:22:53
 */
public interface ISysDictItemService extends IService<SysDictItemPO> {

    /**
     * 分页
     *
     * @param query 入参
     * @return 分页结果
     * @author xutu
     * @date 2025-10-28 09:22:53
     */
    PageInfoVO<SysDictItemPageVO> pageList(SysDictItemPageQuery query);

    /**
     * 新增
     *
     * @param paramList {@link SysDictItemParam}
     * @return id
     * @author xutu
     * @date 2025-10-28 09:22:53
     */
    void add(List<SysDictItemParam> paramList);

    /**
     * 编辑
     *
     * @param param {@link SysDictItemParam}
     * @author xutu
     * @date 2025-10-28 09:22:53
     */
    void edit(SysDictItemParam param);

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
     * @param sysDictItemId 主键id
     * @return {@link }
     * @author xutu
     * @date 2025-10-28 09:22:53
     */
    SysDictItemVO detail(Long sysDictItemId);

}