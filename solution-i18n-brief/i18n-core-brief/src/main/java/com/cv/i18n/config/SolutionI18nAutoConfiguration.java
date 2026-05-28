package com.cv.i18n.config;

import com.cv.i18n.core.I18nMessageResolver;
import com.cv.i18n.core.SpringMessageI18nResolver;
import com.cv.i18n.interceptor.I18nLocaleInterceptor;
import com.cv.i18n.processor.I18nResponseBodyAdvice;
import com.cv.i18n.processor.I18nValueProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(I18nValueProcessor.class)
public class SolutionI18nAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public I18nMessageResolver i18nMessageResolver(MessageSource messageSource) {
        return new SpringMessageI18nResolver(messageSource);
    }

    @Bean
    @ConditionalOnMissingBean
    public I18nValueProcessor i18nValueProcessor(I18nMessageResolver messageResolver, ObjectMapper objectMapper) {
        return new I18nValueProcessor(messageResolver, objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public I18nResponseBodyAdvice i18nResponseBodyAdvice(I18nValueProcessor valueProcessor) {
        return new I18nResponseBodyAdvice(valueProcessor);
    }

    @Bean
    @ConditionalOnMissingBean
    public I18nLocaleInterceptor i18nLocaleInterceptor() {
        return new I18nLocaleInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean(name = "i18nWebMvcConfigurer")
    public WebMvcConfigurer i18nWebMvcConfigurer(I18nLocaleInterceptor interceptor) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(interceptor).addPathPatterns("/**");
            }
        };
    }
}
