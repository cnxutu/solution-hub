package com.cv.solution.formdict.common.enums;

/**
 * @author: xutu
 * @since: 2025/10/29 16:28
 */
public enum TemplateFieldTypeEnum {
    TEXT("TEXT", "单行文本"),
    TEXTAREA("TEXTAREA", "多行文本"),
    NUMBER("NUMBER", "数字"),
    SELECT("SELECT", "下拉单选"),
    MULTI_SELECT("MULTI_SELECT", "下拉多选/复选"),
    RADIO("RADIO", "单选按钮组"),
    CHECKBOX("CHECKBOX", "复选框组"),
    DATE("DATE", "日期选择器（年月日）"),
    DATETIME("DATETIME", "日期时间选择器（年月日时分秒）"),
    TIME("TIME", "时间选择器"),
    BOOLEAN("BOOLEAN", "布尔/开关"),
    FILE("FILE", "文件上传"),
    IMAGE("IMAGE", "图片上传"),
    JSON("JSON", "JSON对象");

    private final String code;
    private final String desc;

    TemplateFieldTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TemplateFieldTypeEnum getByCode(String code) {
        for (TemplateFieldTypeEnum value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
