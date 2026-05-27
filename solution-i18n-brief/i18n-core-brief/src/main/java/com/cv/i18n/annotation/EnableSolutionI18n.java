package com.cv.i18n.annotation;

import com.cv.i18n.config.SolutionI18nAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SolutionI18nAutoConfiguration.class)
public @interface EnableSolutionI18n {
}
