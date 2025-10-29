package com.cv.solution.formdict.form.pojo.param;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author: xutu
 * @since: 2025/10/29 13:24
 */
@Data
public class FormTemplateDataParam {

    /** 模板ID */
    private Long templateId;

    /** 唯一记录ID，用于一次完整录入（可由前端生成 UUID） */
    private Long recordId;

    @TableField(value = "field_values", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> fieldValues;

}
