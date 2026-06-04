package com.cv.rtsp.netty.client;

import com.cv.rtsp.core.exception.RtspClientException;
import com.cv.rtsp.netty.protocol.RtspRequest;
import com.cv.rtsp.netty.protocol.RtspResponse;
import com.cv.rtsp.netty.protocol.RtspResponseReader;
import com.cv.rtsp.netty.protocol.RtspUrlInfo;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class RtspSocketSession {

    private final Socket socket;
    private final BufferedInputStream inputStream;
    private final OutputStream outputStream;

    public RtspSocketSession(String rtspUrl, int connectTimeoutMillis, int readTimeoutMillis) {
        try {
            RtspUrlInfo urlInfo = RtspUrlInfo.parse(rtspUrl);
            this.socket = new Socket();
            this.socket.connect(new InetSocketAddress(urlInfo.getHost(), urlInfo.getPort()), connectTimeoutMillis);
            this.socket.setSoTimeout(readTimeoutMillis);
            this.inputStream = new BufferedInputStream(socket.getInputStream());
            this.outputStream = socket.getOutputStream();
        } catch (IOException exception) {
            throw new RtspClientException("Unable to connect RTSP server: " + rtspUrl, exception);
        }
    }

    public synchronized RtspResponse exchange(RtspRequest request) {
        try {
            outputStream.write(request.render().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            return RtspResponseReader.read(inputStream);
        } catch (IOException exception) {
            throw new RtspClientException("RTSP request exchange failed", exception);
        }
    }

    public synchronized void closeQuietly() {
        try {
            socket.close();
        } catch (IOException ignore) {
        }
    }
}
