/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.form.pojo.vo;

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
 * @date 2025-10-28 19:28:01
 */
@Data
public class TplTemplateFieldOptionPageVO {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 所属模板ID
     */
	private Long templateId;
    /**
     * 所属字段ID
     */
	private Long fieldId;
    /**
     * 选项编码
     */
	private String optionCode;
    /**
     * 选项名称
     */
	private String optionName;
    /**
     * 选项值（实际存储）
     */
	private String optionValue;
    /**
     * 版本号，可用于不同版本选项区分
     */
	private String version;
    /**
     * 排序
     */
	private Integer sortOrder;
    /**
     * 状态 1=启用 0=停用
     */
	private Integer status;

}
