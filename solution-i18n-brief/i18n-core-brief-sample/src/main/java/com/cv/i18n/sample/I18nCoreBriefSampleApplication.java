package com.cv.i18n.sample;

import com.cv.i18n.annotation.EnableSolutionI18n;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@EnableSolutionI18n
@SpringBootApplication
@ComponentScan(basePackages = "com.cv.i18n")
public class I18nCoreBriefSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(I18nCoreBriefSampleApplication.class, args);
    }
}
