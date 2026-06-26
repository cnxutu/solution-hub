package com.cv.simulator.videoosd.sample.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "simulator")
public class SimulatorProperties {

    private Ffmpeg ffmpeg = new Ffmpeg();
    private Osd osd = new Osd();

    @Data
    public static class Ffmpeg {
        private String path = "ffmpeg";
        private String inputPatterns = "*.mp4,*.flv,*.mov,*.mkv";
    }

    @Data
    public static class Osd {
        private String websocketEndpoint = "ws://127.0.0.1:18083/osd";
        private long fixedIntervalMillis = 1000L;
    }
}
