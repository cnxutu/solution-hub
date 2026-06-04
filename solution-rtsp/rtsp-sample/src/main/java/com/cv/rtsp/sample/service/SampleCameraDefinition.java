package com.cv.rtsp.sample.service;

public class SampleCameraDefinition {

    private final String streamId;
    private final String streamPath;
    private final String username;
    private final String password;
    private final String vendor;
    private final String payloadPrefix;

    public SampleCameraDefinition(String streamId,
                                  String streamPath,
                                  String username,
                                  String password,
                                  String vendor,
                                  String payloadPrefix) {
        this.streamId = streamId;
        this.streamPath = streamPath;
        this.username = username;
        this.password = password;
        this.vendor = vendor;
        this.payloadPrefix = payloadPrefix;
    }

    public String getStreamId() {
        return streamId;
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

    public String getVendor() {
        return vendor;
    }

    public String getPayloadPrefix() {
        return payloadPrefix;
    }
}
