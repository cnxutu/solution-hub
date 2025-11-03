package com.cv.solution.formdict.form.common.validation.subtype;

import com.cv.solution.formdict.form.common.enums.InputSubTypeEnum;
import com.cv.solution.formdict.form.common.validation.InputSubTypeValidator;
import org.springframework.stereotype.Component;

/**
 * * 身份证号校验器
 * * 支持：
 * *  - 15 位纯数字（旧身份证）
 * *  - 18 位身份证，最后一位可为数字或 X
 *
 * @author: xutu
 * @since: 2025/11/3 17:04
 */
@Component
public class IdCardValidator implements InputSubTypeValidator {

    private static final String ID_CARD_REGEX = "^(\\d{15}|\\d{17}[0-9Xx])$";

    @Override
    public InputSubTypeEnum getSubType() {
        return InputSubTypeEnum.VALIDATE_ID_CARD;
    }

    @Override
    public void validate(String value, String fieldName) {
        if (value == null || !value.matches(ID_CARD_REGEX)) {
            throw new IllegalArgumentException(fieldName + " 必须是有效的身份证号");
        }

        // 校验通过正则后，还可以做进一步逻辑校验（可选）
        // 例如：校验生日是否有效、最后一位校验码是否正确。
        // 若你有严格要求，可以扩展以下逻辑：
         validateChecksum(value);
    }

    /**
     * （可选）身份证号最后一位校验码算法校验
     */
    @SuppressWarnings("unused")
    private void validateChecksum(String idCard) {
        if (idCard.length() != 18) return;

        int[] weight = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char[] checkCode = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (idCard.charAt(i) - '0') * weight[i];
        }
        char expected = checkCode[sum % 11];
        char actual = Character.toUpperCase(idCard.charAt(17));
        if (expected != actual) {
            throw new IllegalArgumentException("身份证号校验码错误");
        }
    }
}


