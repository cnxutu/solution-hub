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
        private String websocketEndpoint = "ws://127.0.0.1:18083/osd";
        private long fixedIntervalMillis = 1000L;
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
