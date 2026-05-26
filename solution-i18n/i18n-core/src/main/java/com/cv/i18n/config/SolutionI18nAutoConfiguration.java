package com.cv.i18n.config;

import com.cv.i18n.core.I18nMessageResolver;
import com.cv.i18n.core.SpringMessageI18nResolver;
import com.cv.i18n.filter.I18nLocaleFilter;
import com.cv.i18n.processor.I18nResponseBodyAdvice;
import com.cv.i18n.processor.I18nValueProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
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
    @ConditionalOnMissingBean(name = "i18nLocaleFilterRegistration")
    public FilterRegistrationBean<I18nLocaleFilter> i18nLocaleFilterRegistration() {
        FilterRegistrationBean<I18nLocaleFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new I18nLocaleFilter());
        registrationBean.setOrder(Integer.MIN_VALUE + 100);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("i18nLocaleFilter");
        return registrationBean;
    }
}
