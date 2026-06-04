package com.cv.rtsp.core.gateway.mock;

import com.cv.rtsp.core.model.RtspMediaTrack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MockRtspCameraProfile {

    private final String streamId;
    private final String rtspUrl;
    private final String username;
    private final String password;
    private final String vendor;
    private final String sdp;
    private final List<RtspMediaTrack> tracks;
    private final String payloadPrefix;

    public MockRtspCameraProfile(String streamId,
                                 String rtspUrl,
                                 String username,
                                 String password,
                                 String vendor,
                                 String sdp,
                                 List<RtspMediaTrack> tracks,
                                 String payloadPrefix) {
        this.streamId = streamId;
        this.rtspUrl = rtspUrl;
        this.username = username;
        this.password = password;
        this.vendor = vendor;
        this.sdp = sdp;
        this.tracks = new ArrayList<RtspMediaTrack>(tracks);
        this.payloadPrefix = payloadPrefix;
    }

    public String getStreamId() {
        return streamId;
    }

    public String getRtspUrl() {
        return rtspUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getVendor() {
        return vendor;
    }

    public String getSdp() {
        return sdp;
    }

    public List<RtspMediaTrack> getTracks() {
        return Collections.unmodifiableList(tracks);
    }

    public String getPayloadPrefix() {
        return payloadPrefix;
    }
}
