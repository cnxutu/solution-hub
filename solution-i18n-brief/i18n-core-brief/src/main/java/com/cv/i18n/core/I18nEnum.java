package com.cv.i18n.core;

public interface I18nEnum {

    String getI18nCode();

    default String getDefaultMessage() {
        return null;
    }
}
