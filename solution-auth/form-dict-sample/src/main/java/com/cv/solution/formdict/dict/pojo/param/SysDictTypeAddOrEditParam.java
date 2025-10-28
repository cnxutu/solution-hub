/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.dict.pojo.param;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**   
 * 系统字典类型表 新增接口入参
 *
 * @author xutu
 * @date 2025-10-28 09:22:53
 */
@Data
public class SysDictTypeAddOrEditParam {

    /**
     * 主键 id
     */
    private Long id;

    /**
     * 字典编码（唯一）
     */
    @NotBlank(message = "字典编码（唯一）不能为空")
	private String dictCode;
    /**
     * 字典名称
     */
    @NotBlank(message = "字典名称不能为空")
	private String dictName;
    /**
     * 描述
     */
    @NotBlank(message = "描述不能为空")
	private String description;
    /**
     * 状态 1=启用 0=禁用
     */
    @NotNull(message = "状态 1=启用 0=禁用不能为空")
	private Integer status;
    /**
     * 是否系统内置 1=系统 0=自定义
     */
    @NotNull(message = "是否系统内置 1=系统 0=自定义不能为空")
	private Integer isSystem;

}
