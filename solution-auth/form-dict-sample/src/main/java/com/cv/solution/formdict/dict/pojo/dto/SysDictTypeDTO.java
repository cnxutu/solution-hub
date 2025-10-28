/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.dict.pojo.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**   
 * 系统字典类型表 service转换 DTO 类
 *
 * 主要用于 dubbo rpc 调用序列化对象数据传输
 *
 * @author xutu
 * @date 2025-10-28 09:22:53
 */
@Data
public class SysDictTypeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @NotNull(message = "唯一标识不能为空")
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
