package com.cv.solution.formdict.form.common.enums;

/**
 * @author: xutu
 * @since: 2025/10/30 10:27
 */
public enum TemplateTypeEnum {
    ACTIVITY(1, "活动类"),
    REGISTRATION(2, "报名类");

    private final int code;
    private final String description;

    TemplateTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static TemplateTypeEnum fromCode(int code) {
        for (TemplateTypeEnum type : TemplateTypeEnum.values()) {
            if (type.getCode() == code) return type;
        }
        throw new IllegalArgumentException("Unknown template type code: " + code);
    }
}

