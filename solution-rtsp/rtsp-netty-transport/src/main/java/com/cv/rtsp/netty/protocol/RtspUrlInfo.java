package com.cv.rtsp.netty.protocol;

import com.cv.rtsp.core.exception.RtspClientException;

import java.net.URI;

public class RtspUrlInfo {

    private final String host;
    private final int port;
    private final String path;

    public RtspUrlInfo(String host, int port, String path) {
        this.host = host;
        this.port = port;
        this.path = path;
    }

    public static RtspUrlInfo parse(String rtspUrl) {
        try {
            URI uri = URI.create(rtspUrl);
            int port = uri.getPort() > 0 ? uri.getPort() : 554;
            String path = uri.getRawPath();
            if (path == null || path.isEmpty()) {
                path = "/";
            }
            return new RtspUrlInfo(uri.getHost(), port, path);
        } catch (Exception exception) {
            throw new RtspClientException("Invalid RTSP url: " + rtspUrl, exception);
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getPath() {
        return path;
    }
}
