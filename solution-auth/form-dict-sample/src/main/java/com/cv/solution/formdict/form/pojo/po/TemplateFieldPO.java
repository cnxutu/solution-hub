/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.form.pojo.po;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.cv.boot.mybatisplus.pojo.model.BasePO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**   
 * 模板字段表 实体类
 *
 * @author xutu
 * @date 2025-10-28 19:44:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("tpl_template_field")
public class TemplateFieldPO extends BasePO  implements Serializable {

	private static final long serialVersionUID = 1761651841271L;

    /**
     * 所属模板ID
     */
    @TableField("template_id")
	private Long templateId;
    /**
     * 字段编码
     */
    @TableField("field_code")
	private String fieldCode;
    /**
     * 字段名称
     */
    @TableField("field_name")
	private String fieldName;
    /**
     * 字段类型，如 text, number, select, date
     */
    @TableField("field_type")
	private String fieldType;
    /**
     * 选项来源：1=字典(dict_code)，2=模板专属选项，3=自由输入
     */
    @TableField("option_source")
	private Integer optionSource;
    /**
     * 绑定的字典类型编码（option_source=1时使用）
     */
    @TableField("dict_code")
	private String dictCode;
    /**
     * 是否必填 0=否 1=是
     */
	private Integer required;
    /**
     * 默认值
     */
    @TableField("default_value")
	private String defaultValue;
    /**
     * 字段排序
     */
    @TableField("field_sort")
	private Integer fieldSort;
    /**
     * 字段状态 1=启用 0=停用
     */
	private Integer status;
    /**
     * 备注
     */
	private String remark;

    // ----------------------- v2 版本新增字段内容 -----------------------

    /**
     * 输入子类型，如 phone、id_card、email、credit_code 等
     */
    @TableField("input_sub_type")
    private Integer inputSubType;

    /**
     * 选项接口 URL（当 option_source=3 时使用）
     */
    @TableField("option_api")
    private String optionApi;

    /**
     * 接口请求方式：GET / POST
     */
    @TableField("option_api_method")
    private String optionApiMethod;

    /**
     * 接口参数（支持模板变量）
     * 例如：{"hospitalId": "${context.hospitalId}", "status": "ENABLED"}
     */
    @TableField(value = "option_api_params", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> optionApiParams;

    /**
     * 最小长度
     */
    @TableField("min_length")
    private Integer minLength;

    /**
     * 最大长度
     */
    @TableField("max_length")
    private Integer maxLength;

    /**
     * 最小值（针对数值型字段）
     */
    @TableField("min_value")
    private BigDecimal minValue;

    /**
     * 最大值（针对数值型字段）
     */
    @TableField("max_value")
    private BigDecimal maxValue;

    /**
     * 正则表达式（用于自定义校验）
     */
    @TableField("pattern")
    private String pattern;

    /**
     * 小数位数（针对数值型字段）
     */
    @TableField("decimal_scale")
    private Integer decimalScale;

}
