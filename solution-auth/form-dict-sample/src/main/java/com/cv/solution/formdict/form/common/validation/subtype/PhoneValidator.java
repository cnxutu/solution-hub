package com.cv.solution.formdict.form.common.validation.subtype;

import com.cv.solution.formdict.form.common.enums.InputSubTypeEnum;
import com.cv.solution.formdict.form.common.validation.InputSubTypeValidator;
import org.springframework.stereotype.Component;

/**
 * @author: xutu
 * @since: 2025/11/3 17:03
 */
@Component
public class PhoneValidator implements InputSubTypeValidator {
    @Override
    public InputSubTypeEnum getSubType() {
        return InputSubTypeEnum.VALIDATE_PHONE;
    }
    @Override
    public void validate(String value, String fieldName) {
        if (!value.matches("^1[3-9]\\d{9}$")) {
            throw new IllegalArgumentException(fieldName + " 必须是有效手机号");
        }
    }
}


