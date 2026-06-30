package com.cv.simulator.videoosd.sample.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "simulator")
public class SimulatorProperties {

    private Ffmpeg ffmpeg = new Ffmpeg();
    private Osd osd = new Osd();
    private Video video = new Video();
    private Zlm zlm = new Zlm();
    private Webrtc webrtc = new Webrtc();

    @Data
    public static class Ffmpeg {
        private String path = "ffmpeg";
        private String inputPatterns = "*.mp4,*.flv,*.mov,*.mkv";
    }

    @Data
    public static class Osd {
        private OsdSourceType sourceType = OsdSourceType.STATIC_JSON;
        private String websocketEndpoint = "ws://127.0.0.1:18083/ws/osd";
        private String staticJsonLocation = "classpath:/static/long_text_1813A2F3-3BCF-47A1-BE25-B6A7C4F3E1D8.json";
        private long fixedIntervalMillis = 1000L;
        private boolean requireTaskIdMatch = false;
        private String publishTimeStart;
        private String publishTimeEnd;
    }

    @Data
    public static class Video {
        private String sourceDirectory = "./sample-videos";
        private String sourceFile;
    }

    @Data
    public static class Zlm {
        private String host = "127.0.0.1";
        private Integer httpPort = 8086;
        private Integer rtmpPort = 7935;
        private Integer rtspPort = 8554;
        private Integer rtcPort = 10000;
        private String app = "live";
        private String stream = "drone001";
    }

    @Data
    public static class Webrtc {
        private String pushCommandTemplate = "webrtc-pusher --input \"{videoFile}\" --host {zlmHost} --app {app} --stream {stream}";
    }
}
