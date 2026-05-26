package com.cv.i18n.core;

import java.util.Locale;

public interface I18nMessageResolver {

    String resolve(String code, Locale locale, Object[] args, String defaultMessage);
}
