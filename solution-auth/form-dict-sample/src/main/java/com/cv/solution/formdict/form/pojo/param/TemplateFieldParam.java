/**
 * Copyright (c) 2025 Tu Personal Research
 * All rights reserved.
 */
package com.cv.solution.formdict.form.pojo.param;

import com.cv.solution.formdict.form.common.enums.FieldTypeEnum;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 模板字段表 新增接口入参
 *
 * @author xutu
 * @date 2025-10-28 19:44:00
 */
@Data
public class TemplateFieldParam {

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
     *
     * @see com.cv.solution.formdict.form.common.enums.FieldTypeEnum
     */
    @NotBlank(message = "字段类型，如 text, number, select, date不能为空")
    private String fieldType;

    /**
     * 选项来源：1=字典(dict_code)，2=模板专属选项，3=接口调用获取参数
     */
    @NotNull(message = "选项来源：1=字典(dict_code)，2=模板专属选项，3=接口调用获取参数")
    private Integer optionSource;

    /**
     * 绑定的字典类型编码（option_source=1时使用）
     */
    private String dictCode;

    /**
     * 绑定的自定义模板项（option_source=2时使用）不能为空
     */
    private List<TemplateFieldOptionParam> templateFieldOptions;

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
    private String remark;

    // ----------------------- v2 版本新增字段内容 -----------------------

    /**
     * 当字段选择器为 TEXT 类型时必填，输入子类型，如 phone、id_card、email、credit_code 等
     */
    private Integer inputSubType;

    /**
     * 选项接口 URL（当 option_source=3 时使用）
     */
    private String optionApi;

    /**
     * 接口请求方式：GET / POST
     */
    private String optionApiMethod;

    /**
     * 这里直接用 Map 接收 JSON 对象
     * 接口参数（支持模板变量）
     * 例如：{"hospitalId": "${context.hospitalId}", "status": "ENABLED"}
     */
    private Map<String, Object> optionApiParams;

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
