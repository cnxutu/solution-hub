package com.cv.i18n.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
public class MessageSourceConfig {

    @Bean
    @ConditionalOnMissingBean(name = "messageSource")
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("i18n/messages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setCacheSeconds(0);
        messageSource.setFallbackToSystemLocale(false);
        
        log.info("MessageSource configured: basename=i18n/messages, encoding=UTF-8");
        
        return messageSource;
    }
}