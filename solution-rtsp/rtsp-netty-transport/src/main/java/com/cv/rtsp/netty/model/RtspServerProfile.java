package com.cv.rtsp.netty.model;

import com.cv.rtsp.core.model.RtspMediaTrack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RtspServerProfile {

    private final String streamPath;
    private final String username;
    private final String password;
    private final String sdp;
    private final List<RtspMediaTrack> tracks;
    private final String payloadPrefix;

    public RtspServerProfile(String streamPath,
                             String username,
                             String password,
                             String sdp,
                             List<RtspMediaTrack> tracks,
                             String payloadPrefix) {
        this.streamPath = streamPath;
        this.username = username;
        this.password = password;
        this.sdp = sdp;
        this.tracks = new ArrayList<RtspMediaTrack>(tracks);
        this.payloadPrefix = payloadPrefix;
    }

    public String getStreamPath() {
        return streamPath;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
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
