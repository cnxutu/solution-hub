package com.cv.i18n.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface I18nField {

    String value() default "";

    String target() default "";

    String prefix() default "";

    String defaultMessage() default "";

    String[] args() default {};

    boolean replace() default false;
}
