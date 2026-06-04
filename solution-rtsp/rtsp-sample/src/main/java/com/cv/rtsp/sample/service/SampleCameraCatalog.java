package com.cv.rtsp.sample.service;

import com.cv.rtsp.core.gateway.mock.MockRtspCameraProfile;
import com.cv.rtsp.core.model.RtspEndpoint;
import com.cv.rtsp.core.model.RtspMediaTrack;
import com.cv.rtsp.netty.model.RtspServerProfile;
import com.cv.rtsp.netty.server.EmbeddedRtspServer;
import com.cv.rtsp.sample.RtspProviderType;
import com.cv.rtsp.sample.RtspSampleProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class SampleCameraCatalog {

    private final RtspSampleProperties properties;
    private final Map<String, SampleCameraDefinition> definitions = new LinkedHashMap<String, SampleCameraDefinition>();

    public SampleCameraCatalog(RtspSampleProperties properties) {
        this.properties = properties;
        registerDefaults();
    }

    private void registerDefaults() {
        definitions.put("front-gate", new SampleCameraDefinition(
                "front-gate",
                "/live/front-gate/main",
                "admin",
                "123456",
                "hikvision",
                "front-gate"));
        definitions.put("warehouse", new SampleCameraDefinition(
                "warehouse",
                "/live/warehouse/sub",
                "operator",
                "123456",
                "dahua",
                "warehouse"));
    }

    private String buildSdp(String streamId) {
        return "v=0\n"
                + "o=- 0 0 IN IP4 127.0.0.1\n"
                + "s=" + streamId + "\n"
                + "t=0 0\n"
                + "m=video 0 RTP/AVP 96\n"
                + "a=rtpmap:96 H264/90000\n"
                + "a=control:trackID=1";
    }

    private List<RtspMediaTrack> buildTracks() {
        return Arrays.asList(new RtspMediaTrack("video", "H264", "trackID=1", 90000));
    }

    public Collection<SampleCameraDefinition> listDefinitions() {
        return Collections.unmodifiableCollection(new ArrayList<SampleCameraDefinition>(definitions.values()));
    }

    public Collection<MockRtspCameraProfile> buildMockProfiles() {
        List<MockRtspCameraProfile> profiles = new ArrayList<MockRtspCameraProfile>();
        for (SampleCameraDefinition definition : definitions.values()) {
            profiles.add(new MockRtspCameraProfile(
                    definition.getStreamId(),
                    "rtsp://mock-camera" + definition.getStreamPath(),
                    definition.getUsername(),
                    definition.getPassword(),
                    definition.getVendor(),
                    buildSdp(definition.getStreamId()),
                    buildTracks(),
                    definition.getPayloadPrefix()
            ));
        }
        return profiles;
    }

    public Collection<RtspServerProfile> buildServerProfiles() {
        List<RtspServerProfile> profiles = new ArrayList<RtspServerProfile>();
        for (SampleCameraDefinition definition : definitions.values()) {
            profiles.add(EmbeddedRtspServer.buildProfile(
                    definition.getStreamPath(),
                    definition.getUsername(),
                    definition.getPassword(),
                    definition.getStreamId(),
                    definition.getPayloadPrefix()
            ));
        }
        return profiles;
    }

    public RtspEndpoint buildEndpoint(String streamId) {
        SampleCameraDefinition definition = definitions.get(streamId);
        if (definition == null) {
            return null;
        }
        String url = properties.getProvider() == RtspProviderType.MOCK
                ? "rtsp://mock-camera" + definition.getStreamPath()
                : "rtsp://" + properties.getRtspHost() + ":" + properties.getRtspPort() + definition.getStreamPath();
        return new RtspEndpoint(
                definition.getStreamId(),
                url,
                definition.getUsername(),
                definition.getPassword(),
                3000,
                5000,
                properties.getKeepAliveSeconds(),
                properties.getFramePullIntervalMillis(),
                true
        );
    }

    public Collection<Map<String, Object>> describeCatalog() {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (SampleCameraDefinition definition : definitions.values()) {
            Map<String, Object> item = new LinkedHashMap<String, Object>();
            item.put("streamId", definition.getStreamId());
            item.put("streamPath", definition.getStreamPath());
            item.put("rtspUrl", buildEndpoint(definition.getStreamId()).getRtspUrl());
            item.put("vendor", definition.getVendor());
            item.put("username", definition.getUsername());
            item.put("password", definition.getPassword());
            item.put("tracks", buildTracks());
            result.add(item);
        }
        return result;
    }

    public MockRtspCameraProfile findMockProfile(String streamId) {
        for (MockRtspCameraProfile profile : buildMockProfiles()) {
            if (profile.getStreamId().equals(streamId)) {
                return profile;
            }
        }
        return null;
    }
}
