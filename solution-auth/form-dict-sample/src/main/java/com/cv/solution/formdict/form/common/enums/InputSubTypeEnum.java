package com.cv.solution.formdict.form.common.enums;

/**
 * @author: xutu
 * @since: 2025/10/30 10:16
 */
public enum InputSubTypeEnum {
    VALIDATE_PHONE(1, "输入框-子类校验器-手机号校验"),
    VALIDATE_ID_CARD(2, "输入框-子类校验器-身份证号校验"),
    VALIDATE_EMAIL(3, "输入框-子类校验器-邮箱校验"),
    VALIDATE_CREDIT_CODE(4, "输入框-子类校验器-统一社会信用代码校验");

    private final int code;
    private final String description;

    InputSubTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    // 可以根据 code 获取枚举值
    public static InputSubTypeEnum fromCode(int code) {
        for (InputSubTypeEnum type : InputSubTypeEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}

