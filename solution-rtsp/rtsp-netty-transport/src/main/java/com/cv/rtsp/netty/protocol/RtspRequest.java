package com.cv.rtsp.netty.protocol;

import java.util.LinkedHashMap;
import java.util.Map;

public class RtspRequest {

    private final String method;
    private final String uri;
    private final Map<String, String> headers = new LinkedHashMap<String, String>();
    private String body;

    public RtspRequest(String method, String uri) {
        this.method = method;
        this.uri = uri;
    }

    public RtspRequest header(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public RtspRequest body(String body) {
        this.body = body;
        return this;
    }

    public String render() {
        StringBuilder builder = new StringBuilder();
        builder.append(method).append(" ").append(uri).append(" RTSP/1.0\r\n");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }
        if (body != null && !body.isEmpty()) {
            builder.append("Content-Length: ").append(body.getBytes().length).append("\r\n");
        }
        builder.append("\r\n");
        if (body != null && !body.isEmpty()) {
            builder.append(body);
        }
        return builder.toString();
    }
}
