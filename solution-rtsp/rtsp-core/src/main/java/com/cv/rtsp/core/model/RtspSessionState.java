package com.cv.rtsp.core.model;

public enum RtspSessionState {
    CREATED,
    OPTIONS_NEGOTIATED,
    DESCRIBED,
    SETUP,
    PLAYING,
    STOPPED,
    FAILED
}
