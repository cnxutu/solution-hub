/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.form.pojo.vo;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**   
 * 模板字段表 新增接口入参
 *
 * @author xutu
 * @date 2025-10-28 19:44:00
 */
@Data
public class TemplateFieldVO {

    /** 字段ID */
    private Long id;

    /** 所属模板ID */
    private Long templateId;

    /** 字段编码 */
    private String fieldCode;

    /** 字段名称 */
    private String fieldName;

    /** 字段类型，如 text, number, select, date */
    private String fieldType;

    /** 选项来源：1=字典 2=模板专属选项 3=接口调用 4=自由输入 */
    private Integer optionSource;

    /** 字典类型编码（当 optionSource=1 时使用） */
    private String dictCode;

    /** 是否必填：0=否，1=是 */
    private Integer required;

    /** 默认值 */
    private String defaultValue;

    /** 字段排序 */
    private Integer fieldSort;

    /** 状态：1=启用，0=停用 */
    private Integer status;

    /** 备注 */
    private String remark;

    /** 字段选项列表（当 optionSource=1 或 2 时使用） */
    private List<TemplateFieldOptionVO> options;


    // ----------------------- v2 版本新增字段内容 -----------------------

    /**
     * 输入子类型，如 phone、id_card、email、credit_code 等
     */
    private Integer inputSubType;

    /**
     * 绑定的外部选项 URL（当 optionSource = 3 时使用）
     */
    private String optionUrl;

    /**
     * 最小长度
     */
    private Integer minLength;

    /**
     * 最大长度
     */
    private Integer maxLength;

    /**
     * 最小值（针对数值型字段）
     */
    private BigDecimal minValue;

    /**
     * 最大值（针对数值型字段）
     */
    private BigDecimal maxValue;

    /**
     * 正则表达式（用于自定义校验）
     */
    private String pattern;

    /**
     * 小数位数（针对数值型字段）
     */
    private Integer decimalScale;

}
