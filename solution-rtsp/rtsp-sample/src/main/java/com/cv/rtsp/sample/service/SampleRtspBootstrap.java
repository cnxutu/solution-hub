package com.cv.rtsp.sample.service;

import com.cv.rtsp.core.gateway.RtspCameraGateway;
import com.cv.rtsp.core.gateway.mock.MockRtspCameraGateway;
import com.cv.rtsp.core.gateway.mock.MockRtspCameraProfile;
import com.cv.rtsp.core.manager.RtspStreamManager;
import com.cv.rtsp.core.model.RtspEndpoint;
import com.cv.rtsp.netty.model.RtspServerProfile;
import com.cv.rtsp.netty.server.EmbeddedRtspServer;
import com.cv.rtsp.sample.RtspProviderType;
import com.cv.rtsp.sample.RtspSampleProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class SampleRtspBootstrap {

    private final RtspCameraGateway gateway;
    private final MockRtspCameraGateway mockGateway;
    private final SampleCameraCatalog cameraCatalog;
    private final SampleRtspObserver observer;
    private final RtspStreamManager manager;
    private final RtspSampleProperties properties;
    private final EmbeddedRtspServer embeddedRtspServer;

    public SampleRtspBootstrap(RtspCameraGateway gateway,
                               MockRtspCameraGateway mockGateway,
                               SampleCameraCatalog cameraCatalog,
                               SampleRtspObserver observer,
                               RtspStreamManager manager,
                               RtspSampleProperties properties,
                               EmbeddedRtspServer embeddedRtspServer) {
        this.gateway = gateway;
        this.mockGateway = mockGateway;
        this.cameraCatalog = cameraCatalog;
        this.observer = observer;
        this.manager = manager;
        this.properties = properties;
        this.embeddedRtspServer = embeddedRtspServer;
    }

    @PostConstruct
    public void init() {
        observer.appendSystemEvent("Provider selected: " + properties.getProvider() + ", gateway=" + gateway.getClass().getSimpleName());
        if (properties.getProvider() == RtspProviderType.MOCK) {
            for (MockRtspCameraProfile profile : cameraCatalog.buildMockProfiles()) {
                mockGateway.registerCamera(profile);
            }
        } else if (properties.getEmbeddedServer().isEnabled()) {
            for (RtspServerProfile profile : cameraCatalog.buildServerProfiles()) {
                embeddedRtspServer.registerProfile(profile);
            }
            embeddedRtspServer.start();
        }
        if (properties.isAutoStart()) {
            for (SampleCameraDefinition definition : cameraCatalog.listDefinitions()) {
                RtspEndpoint endpoint = cameraCatalog.buildEndpoint(definition.getStreamId());
                manager.start(endpoint, observer);
            }
        }
    }

    @PreDestroy
    public void destroy() {
        manager.shutdown();
        embeddedRtspServer.stop();
    }
}
