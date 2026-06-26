package com.cv.rtsp.sample.config;

import com.cv.rtsp.core.client.RtspStreamClient;
import com.cv.rtsp.core.gateway.RtspCameraGateway;
import com.cv.rtsp.core.gateway.mock.MockRtspCameraGateway;
import com.cv.rtsp.core.manager.RtspStreamManager;
import com.cv.rtsp.ffmpeg.FfmpegRtspCameraGateway;
import com.cv.rtsp.ffmpeg.FfmpegRtspGatewayConfig;
import com.cv.rtsp.netty.gateway.NettyRtspCameraGateway;
import com.cv.rtsp.netty.server.EmbeddedRtspServer;
import com.cv.rtsp.sample.RtspProviderType;
import com.cv.rtsp.sample.RtspSampleProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RtspSampleConfig {

    @Bean
    public MockRtspCameraGateway mockRtspCameraGateway() {
        return new MockRtspCameraGateway();
    }

    @Bean
    public NettyRtspCameraGateway nettyRtspCameraGateway() {
        return new NettyRtspCameraGateway();
    }

    @Bean
    public FfmpegRtspCameraGateway ffmpegRtspCameraGateway(RtspSampleProperties properties) {
        return new FfmpegRtspCameraGateway(new FfmpegRtspGatewayConfig(
                properties.getFfmpeg().getBinary(),
                properties.getFfmpeg().getTransport(),
                properties.getFfmpeg().getLogTailSize(),
                properties.getFfmpeg().getExtraArgs()
        ));
    }

    @Bean
    public EmbeddedRtspServer embeddedRtspServer(RtspSampleProperties properties) {
        return new EmbeddedRtspServer(properties.getRtspPort());
    }

    @Bean
    @Primary
    public RtspCameraGateway rtspCameraGateway(RtspSampleProperties properties,
                                               MockRtspCameraGateway mockGateway,
                                               NettyRtspCameraGateway nettyGateway,
                                               FfmpegRtspCameraGateway ffmpegGateway) {
        RtspProviderType provider = properties.getProvider();
        if (provider == RtspProviderType.NETTY) {
            return nettyGateway;
        }
        if (provider == RtspProviderType.FFMPEG) {
            return ffmpegGateway;
        }
        return mockGateway;
    }

    @Bean
    public RtspStreamClient rtspStreamClient(@Qualifier("rtspCameraGateway") RtspCameraGateway gateway) {
        return new RtspStreamClient(gateway);
    }

    @Bean
    public RtspStreamManager rtspStreamManager(RtspStreamClient client) {
        return new RtspStreamManager(client);
    }
}
