package com.cv.i18n.context;

import org.springframework.util.StringUtils;

import java.util.Locale;

public final class I18nContextHolder {

    private static final ThreadLocal<Locale> LOCALE = new ThreadLocal<>();

    private I18nContextHolder() {
    }

    public static void setLocale(Locale locale) {
        LOCALE.set(locale);
    }

    public static Locale getLocale() {
        Locale locale = LOCALE.get();
        return locale == null ? Locale.getDefault() : locale;
    }

    public static Locale parseLocale(String language) {
        if (!StringUtils.hasText(language)) {
            return Locale.getDefault();
        }
        return Locale.forLanguageTag(language.replace('_', '-'));
    }

    public static void clear() {
        LOCALE.remove();
    }
}
