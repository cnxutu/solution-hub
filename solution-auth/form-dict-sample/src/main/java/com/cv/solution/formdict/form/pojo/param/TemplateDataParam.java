package com.cv.solution.formdict.form.pojo.param;

import java.util.List;

/**
 * @author: xutu
 * @since: 2025/10/29 13:24
 */
public class TemplateDataParam {

    /** 模板ID */
    private Long templateId;

    /** 唯一记录ID，用于一次完整录入（可由前端生成 UUID） */
    private Long recordId;

    /** 字段值集合 */
    private List<TemplateFieldValueParam> fields;

}
