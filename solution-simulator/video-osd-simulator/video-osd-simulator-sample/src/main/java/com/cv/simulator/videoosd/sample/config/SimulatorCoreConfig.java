package com.cv.simulator.videoosd.sample.config;

import com.cv.simulator.videoosd.core.ffmpeg.FfmpegCommandBuilder;
import com.cv.simulator.videoosd.core.ffmpeg.FfmpegPathResolver;
import com.cv.simulator.videoosd.core.ffmpeg.ProcessBuilderFfmpegLauncher;
import com.cv.simulator.videoosd.core.media.VideoSourceScanner;
import com.cv.simulator.videoosd.core.osd.OsdExcelImporter;
import com.cv.simulator.videoosd.core.osd.OsdPayloadSupport;
import com.cv.simulator.videoosd.core.runtime.StreamTaskRuntime;
import com.cv.simulator.videoosd.core.webrtc.ExternalWebRtcCommandBuilder;
import com.cv.simulator.videoosd.core.websocket.WebSocketOsdSender;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SimulatorProperties.class)
public class SimulatorCoreConfig {

    @Bean
    public FfmpegCommandBuilder ffmpegCommandBuilder() {
        return new FfmpegCommandBuilder();
    }

    @Bean
    public FfmpegPathResolver ffmpegPathResolver() {
        return new FfmpegPathResolver();
    }

    @Bean
    public VideoSourceScanner videoSourceScanner() {
        return new VideoSourceScanner();
    }

    @Bean
    public StreamTaskRuntime streamTaskRuntime() {
        return new StreamTaskRuntime(new ProcessBuilderFfmpegLauncher());
    }

    @Bean
    public OsdPayloadSupport osdPayloadSupport() {
        return new OsdPayloadSupport();
    }

    @Bean
    public OsdExcelImporter osdExcelImporter() {
        return new OsdExcelImporter();
    }

    @Bean
    public WebSocketOsdSender webSocketOsdSender() {
        return new WebSocketOsdSender();
    }

    @Bean
    public ExternalWebRtcCommandBuilder externalWebRtcCommandBuilder() {
        return new ExternalWebRtcCommandBuilder();
    }
}
