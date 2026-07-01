package com.cv.simulator.videoosd.sample.config;

import com.cv.simulator.videoosd.core.ffmpeg.FfmpegCommandBuilder;
import com.cv.simulator.videoosd.core.ffmpeg.FfmpegPathResolver;
import com.cv.simulator.videoosd.core.ffmpeg.ProcessBuilderFfmpegLauncher;
import com.cv.simulator.videoosd.core.media.VideoSourceScanner;
import com.cv.simulator.videoosd.core.mqtt.MqttOsdRecordMapper;
import com.cv.simulator.videoosd.core.mqtt.MqttOsdSubscriber;
import com.cv.simulator.videoosd.core.osd.OsdExcelImporter;
import com.cv.simulator.videoosd.core.osd.OsdFrameGeometryCalculator;
import com.cv.simulator.videoosd.core.osd.OsdPayloadSupport;
import com.cv.simulator.videoosd.core.mqtt.PahoMqttOsdSubscriber;
import com.cv.simulator.videoosd.core.runtime.StreamTaskRuntime;
import com.cv.simulator.videoosd.core.webrtc.ExternalWebRtcCommandBuilder;
import com.cv.simulator.videoosd.sample.service.PublishTimeReplayExecutor;
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
    public OsdFrameGeometryCalculator osdFrameGeometryCalculator() {
        return new OsdFrameGeometryCalculator();
    }

    @Bean
    public MqttOsdRecordMapper mqttOsdRecordMapper(OsdFrameGeometryCalculator osdFrameGeometryCalculator) {
        return new MqttOsdRecordMapper(osdFrameGeometryCalculator);
    }

    @Bean
    public MqttOsdSubscriber mqttOsdSubscriber(MqttOsdRecordMapper mqttOsdRecordMapper) {
        return new PahoMqttOsdSubscriber(mqttOsdRecordMapper);
    }

    @Bean
    public ExternalWebRtcCommandBuilder externalWebRtcCommandBuilder() {
        return new ExternalWebRtcCommandBuilder();
    }

    @Bean
    public PublishTimeReplayExecutor publishTimeReplayExecutor() {
        return new PublishTimeReplayExecutor();
    }

    @Bean
    public OsdBroadcastWebSocketHandler osdBroadcastWebSocketHandler() {
        return new OsdBroadcastWebSocketHandler();
    }
}
