package com.cv.solution.formdict.form.common.validation.subtype;

import com.cv.solution.formdict.form.common.enums.InputSubTypeEnum;
import com.cv.solution.formdict.form.common.validation.InputSubTypeValidator;
import org.springframework.stereotype.Component;

/**
 * @author: xutu
 * @since: 2025/11/3 17:04
 */
@Component
public class EmailValidator implements InputSubTypeValidator {
    @Override
    public InputSubTypeEnum getSubType() {
        return InputSubTypeEnum.VALIDATE_EMAIL;
    }

    @Override
    public void validate(String value, String fieldName) {
        if (!value.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException(fieldName + " 必须是有效邮箱地址");
        }
    }
}

