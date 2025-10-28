/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.dict.pojo.vo;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**   
 * 系统字典项表 新增接口入参
 *
 * @author xutu
 * @date 2025-10-28 09:22:53
 */
@Data
public class SysDictItemVO {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 所属字典编码
     */
	private String dictCode;
    /**
     * 字典项值
     */
	private String itemValue;
    /**
     * 字典项名称
     */
	private String itemName;
    /**
     * 排序值
     */
	private Integer itemSort;
    /**
     * 父级值（支持层级结构）
     */
	private String parentValue;
    /**
     * 标签颜色
     */
	private String tagColor;
    /**
     * 状态 1=启用 0=禁用
     */
	private Integer status;
    /**
     * 备注
     */
	private String remark;

}
