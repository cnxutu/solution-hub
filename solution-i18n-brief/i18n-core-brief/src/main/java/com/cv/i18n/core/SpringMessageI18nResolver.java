package com.cv.i18n.core;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.util.StringUtils;

import java.util.Locale;

public class SpringMessageI18nResolver implements I18nMessageResolver {

    private final MessageSource messageSource;

    public SpringMessageI18nResolver(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String resolve(String code, Locale locale, Object[] args, String defaultMessage) {
        if (!StringUtils.hasText(code)) {
            return defaultMessage;
        }
        try {
            return messageSource.getMessage(code, args, defaultMessage, locale);
        } catch (NoSuchMessageException ignored) {
            return StringUtils.hasText(defaultMessage) ? defaultMessage : code;
        }
    }
}
