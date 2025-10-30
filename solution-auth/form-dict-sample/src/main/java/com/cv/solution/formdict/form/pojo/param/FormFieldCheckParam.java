package com.cv.solution.formdict.form.pojo.param;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: xutu
 * @since: 2025/10/29 13:24
 *
 * y
 */
@Data
public class FormFieldCheckParam extends FormFieldDataParam {

    /**
     * 字段类型
     *
     * @see com.cv.solution.formdict.form.common.enums.FieldTypeEnum
     */
    private String fieldType;  // 字段类型 (TEXT, SELECT 等)

    /**
     * 校验类型
     *
     * @see com.cv.solution.formdict.form.common.enums.InputSubTypeEnum
     */
    private Integer inputSubType; // 校验类型 (手机号、身份证等)

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

}
