package com.cv.i18n.sample.enums;

import com.cv.i18n.core.I18nEnum;

public enum OrderStatusEnum implements I18nEnum {

    CREATED("已创建"),
    PAID("已支付"),
    SHIPPED("已发货");

    private final String defaultMessage;

    OrderStatusEnum(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String getI18nCode() {
        return defaultMessage;
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }
}