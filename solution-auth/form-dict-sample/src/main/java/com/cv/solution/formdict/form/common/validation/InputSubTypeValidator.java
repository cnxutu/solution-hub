package com.cv.solution.formdict.form.common.validation;

import com.cv.solution.formdict.form.common.enums.InputSubTypeEnum;

/**
 * @author: xutu
 * @since: 2025/11/3 17:03
 */
public interface InputSubTypeValidator {
    InputSubTypeEnum getSubType();
    void validate(String value, String fieldName);
}


