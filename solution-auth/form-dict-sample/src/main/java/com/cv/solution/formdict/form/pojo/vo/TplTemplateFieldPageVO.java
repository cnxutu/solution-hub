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
 * 模板字段表 新增接口入参
 *
 * @author xutu
 * @date 2025-10-28 19:28:01
 */
@Data
public class TplTemplateFieldPageVO {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 所属模板ID
     */
	private Long templateId;
    /**
     * 字段编码
     */
	private String fieldCode;
    /**
     * 字段名称
     */
	private String fieldName;
    /**
     * 字段类型，如 text, number, select, date
     */
	private String fieldType;
    /**
     * 选项来源：1=字典(dict_code)，2=模板专属选项，3=自由输入
     */
	private Integer optionSource;
    /**
     * 绑定的字典类型编码（option_source=1时使用）
     */
	private String dictCode;
    /**
     * 是否必填 0=否 1=是
     */
	private Integer required;
    /**
     * 默认值
     */
	private String defaultValue;
    /**
     * 字段排序
     */
	private Integer fieldSort;
    /**
     * 字段状态 1=启用 0=停用
     */
	private Integer status;
    /**
     * 备注
     */
	private String remark;

}
