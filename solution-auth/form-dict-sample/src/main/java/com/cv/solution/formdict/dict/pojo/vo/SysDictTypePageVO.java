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
 * 系统字典类型表 新增接口入参
 *
 * @author xutu
 * @date 2025-10-28 09:22:53
 */
@Data
public class SysDictTypePageVO {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 字典编码（唯一）
     */
	private String dictCode;
    /**
     * 字典名称
     */
	private String dictName;
    /**
     * 描述
     */
	private String description;
    /**
     * 状态 1=启用 0=禁用
     */
	private Integer status;
    /**
     * 是否系统内置 1=系统 0=自定义
     */
	private Integer isSystem;

}
