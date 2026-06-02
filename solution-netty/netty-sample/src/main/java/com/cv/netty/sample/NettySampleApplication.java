package com.cv.netty.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.cv.netty")
public class NettySampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettySampleApplication.class, args);
    }
}
