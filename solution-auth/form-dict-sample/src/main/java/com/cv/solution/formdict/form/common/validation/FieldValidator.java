package com.cv.solution.formdict.form.common.validation;

import com.cv.solution.formdict.form.common.enums.FieldTypeEnum;
import com.cv.solution.formdict.form.pojo.po.TemplateFieldPO;

/**
 * @author: xutu
 * @since: 2025/11/3 16:57
 */
public interface FieldValidator {
    FieldTypeEnum getFieldType();
    void validate(TemplateFieldPO field, Object value);
}


