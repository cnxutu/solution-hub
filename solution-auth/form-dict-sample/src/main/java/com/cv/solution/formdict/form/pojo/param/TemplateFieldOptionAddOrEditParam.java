/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.form.pojo.param;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**   
 * 模板字段专属可选项表 新增接口入参
 *
 * @author xutu
 * @date 2025-10-28 19:44:00
 */
@Data
public class TemplateFieldOptionAddOrEditParam {

    /**
     * 主键 id
     */
    private Long id;

    /**
     * 所属模板ID
     */
    @NotNull(message = "所属模板ID不能为空")
	private Long templateId;
    /**
     * 所属字段ID
     */
    @NotNull(message = "所属字段ID不能为空")
	private Long fieldId;
    /**
     * 选项编码
     */
    @NotBlank(message = "选项编码不能为空")
	private String optionCode;
    /**
     * 选项名称
     */
    @NotBlank(message = "选项名称不能为空")
	private String optionName;
    /**
     * 选项值（实际存储）
     */
    @NotBlank(message = "选项值（实际存储）不能为空")
	private String optionValue;
    /**
     * 版本号，可用于不同版本选项区分
     */
    @NotBlank(message = "版本号，可用于不同版本选项区分不能为空")
	private String version;
    /**
     * 排序
     */
    @NotNull(message = "排序不能为空")
	private Integer sortOrder;
    /**
     * 状态 1=启用 0=停用
     */
    @NotNull(message = "状态 1=启用 0=停用不能为空")
	private Integer status;

}
