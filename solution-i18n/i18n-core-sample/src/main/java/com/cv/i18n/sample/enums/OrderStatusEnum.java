package com.cv.i18n.sample.enums;

import com.cv.i18n.core.I18nEnum;

public enum OrderStatusEnum implements I18nEnum {

    CREATED("order.status.created", "Created"),
    PAID("order.status.paid", "Paid"),
    SHIPPED("order.status.shipped", "Shipped");

    private final String i18nCode;
    private final String defaultMessage;

    OrderStatusEnum(String i18nCode, String defaultMessage) {
        this.i18nCode = i18nCode;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public String getI18nCode() {
        return i18nCode;
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }
}
