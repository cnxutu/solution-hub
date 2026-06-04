package com.cv.rtsp.core.model;

public class RtspMediaTrack {

    private final String mediaType;
    private final String codec;
    private final String control;
    private final int clockRate;

    public RtspMediaTrack(String mediaType, String codec, String control, int clockRate) {
        this.mediaType = mediaType;
        this.codec = codec;
        this.control = control;
        this.clockRate = clockRate;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getCodec() {
        return codec;
    }

    public String getControl() {
        return control;
    }

    public int getClockRate() {
        return clockRate;
    }
}
