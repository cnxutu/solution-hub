package com.cv.solution.formdict.form.export;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: xutu
 * @since: 2025/12/18 17:26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateFieldModel {

    private Long templateId;

    /**
     * 字段 code（数据 key）
     */
    private String fieldCode;

    /**
     * Excel 显示名称
     */
    private String fieldName;

    /**
     * 排序
     */
    private Integer fieldOrder;
}
