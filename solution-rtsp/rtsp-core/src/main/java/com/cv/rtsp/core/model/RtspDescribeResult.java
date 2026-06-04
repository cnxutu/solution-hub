package com.cv.rtsp.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RtspDescribeResult {

    private final String sessionDescription;
    private final List<RtspMediaTrack> tracks;

    public RtspDescribeResult(String sessionDescription, List<RtspMediaTrack> tracks) {
        this.sessionDescription = sessionDescription;
        this.tracks = new ArrayList<RtspMediaTrack>(tracks);
    }

    public String getSessionDescription() {
        return sessionDescription;
    }

    public List<RtspMediaTrack> getTracks() {
        return Collections.unmodifiableList(tracks);
    }
}
