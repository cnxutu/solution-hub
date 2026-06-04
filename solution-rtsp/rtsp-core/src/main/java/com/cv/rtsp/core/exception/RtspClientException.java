package com.cv.rtsp.core.exception;

public class RtspClientException extends RuntimeException {

    public RtspClientException(String message) {
        super(message);
    }

    public RtspClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
