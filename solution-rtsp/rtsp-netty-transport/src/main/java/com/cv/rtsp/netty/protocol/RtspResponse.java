package com.cv.rtsp.netty.protocol;

import java.util.LinkedHashMap;
import java.util.Map;

public class RtspResponse {

    private final int statusCode;
    private final String statusText;
    private final Map<String, String> headers;
    private final String body;

    public RtspResponse(int statusCode, String statusText, Map<String, String> headers, String body) {
        this.statusCode = statusCode;
        this.statusText = statusText;
        this.headers = new LinkedHashMap<String, String>(headers);
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusText() {
        return statusText;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String name) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(name)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public String getBody() {
        return body;
    }
}
