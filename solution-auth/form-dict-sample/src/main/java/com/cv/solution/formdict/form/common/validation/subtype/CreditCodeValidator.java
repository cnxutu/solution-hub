package com.cv.solution.formdict.form.common.validation.subtype;

import com.cv.solution.formdict.form.common.enums.InputSubTypeEnum;
import com.cv.solution.formdict.form.common.validation.InputSubTypeValidator;
import org.springframework.stereotype.Component;

/**
 * 统一社会信用代码校验器
 * 格式：18 位大写字母或数字（不含 I、O、Z、S、V）
 * 可选：支持校验码计算（GB32100-2015）
 *
 * @author: xutu
 * @since: 2025/11/3 17:04
 */
@Component
public class CreditCodeValidator implements InputSubTypeValidator {

    private static final String CREDIT_CODE_REGEX = "^[0-9A-Z]{18}$";
    // 用于计算校验码的字符集（不包括 I、O、Z、S、V）
    private static final String BASE_CODE = "0123456789ABCDEFGHJKLMNPQRTUWXY";

    private static final int[] WEIGHT = {1, 3, 9, 27, 19, 26, 16, 17, 20,
            29, 25, 13, 8, 24, 10, 30, 28};

    @Override
    public InputSubTypeEnum getSubType() {
        return InputSubTypeEnum.VALIDATE_CREDIT_CODE;
    }

    @Override
    public void validate(String value, String fieldName) {
        if (value == null || !value.matches(CREDIT_CODE_REGEX)) {
            throw new IllegalArgumentException(fieldName + " 必须是 18 位大写字母或数字组成的统一社会信用代码");
        }

        // 可选启用严格校验
        // validateChecksum(value);
    }

    /**
     * （可选）统一社会信用代码校验码验证
     * 依据：GB32100-2015
     */
    @SuppressWarnings("unused")
    private void validateChecksum(String creditCode) {
        if (creditCode.length() != 18) return;

        int sum = 0;
        for (int i = 0; i < 17; i++) {
            char c = creditCode.charAt(i);
            int codeIndex = BASE_CODE.indexOf(c);
            if (codeIndex == -1) {
                throw new IllegalArgumentException("统一社会信用代码包含非法字符：" + c);
            }
            sum += codeIndex * WEIGHT[i];
        }

        int logicCheckCode = 31 - sum % 31;
        if (logicCheckCode == 31) logicCheckCode = 0;
        char expected = BASE_CODE.charAt(logicCheckCode);
        char actual = creditCode.charAt(17);

        if (expected != actual) {
            throw new IllegalArgumentException("统一社会信用代码校验位错误");
        }
    }
}


