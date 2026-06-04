package com.cv.rtsp.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableConfigurationProperties(RtspSampleProperties.class)
@ComponentScan(basePackages = "com.cv.rtsp")
public class RtspSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(RtspSampleApplication.class, args);
    }
}
