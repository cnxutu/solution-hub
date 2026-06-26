package com.cv.simulator.videoosd.sample;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.cv.simulator.videoosd.sample.mapper")
@EnableAsync
public class VideoOsdSimulatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoOsdSimulatorApplication.class, args);
    }
}
