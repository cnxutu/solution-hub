/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.form.pojo.query;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.cv.boot.common.pojo.query.PageQuery;

/**   
 * 模板字段表 新增接口入参
 *
 * @author xutu
 * @date 2025-10-28 19:28:01
 */
@Data
public class TplTemplateFieldPageQuery extends PageQuery {


    /**
     * 所属模板ID
     */
    @NotNull(message = "所属模板ID不能为空")
	private Long templateId;
    /**
     * 字段编码
     */
    @NotBlank(message = "字段编码不能为空")
	private String fieldCode;
    /**
     * 字段名称
     */
    @NotBlank(message = "字段名称不能为空")
	private String fieldName;
    /**
     * 字段类型，如 text, number, select, date
     */
    @NotBlank(message = "字段类型，如 text, number, select, date不能为空")
	private String fieldType;
    /**
     * 选项来源：1=字典(dict_code)，2=模板专属选项，3=自由输入
     */
    @NotNull(message = "选项来源：1=字典(dict_code)，2=模板专属选项，3=自由输入不能为空")
	private Integer optionSource;
    /**
     * 绑定的字典类型编码（option_source=1时使用）
     */
    @NotBlank(message = "绑定的字典类型编码（option_source=1时使用）不能为空")
	private String dictCode;
    /**
     * 是否必填 0=否 1=是
     */
    @NotNull(message = "是否必填 0=否 1=是不能为空")
	private Integer required;
    /**
     * 默认值
     */
    @NotBlank(message = "默认值不能为空")
	private String defaultValue;
    /**
     * 字段排序
     */
    @NotNull(message = "字段排序不能为空")
	private Integer fieldSort;
    /**
     * 字段状态 1=启用 0=停用
     */
    @NotNull(message = "字段状态 1=启用 0=停用不能为空")
	private Integer status;
    /**
     * 备注
     */
    @NotBlank(message = "备注不能为空")
	private String remark;

}
