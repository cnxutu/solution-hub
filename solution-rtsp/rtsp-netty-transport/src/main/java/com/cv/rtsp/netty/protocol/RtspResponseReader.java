package com.cv.rtsp.netty.protocol;

import com.cv.rtsp.core.exception.RtspClientException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public final class RtspResponseReader {

    private RtspResponseReader() {
    }

    public static RtspResponse read(BufferedInputStream inputStream) throws IOException {
        String statusLine = readLine(inputStream);
        if (statusLine == null || statusLine.isEmpty()) {
            throw new RtspClientException("RTSP empty response");
        }
        String[] parts = statusLine.split(" ", 3);
        if (parts.length < 3) {
            throw new RtspClientException("Invalid RTSP status line: " + statusLine);
        }
        int statusCode = Integer.parseInt(parts[1]);
        String statusText = parts[2];
        Map<String, String> headers = new LinkedHashMap<String, String>();
        String line;
        while ((line = readLine(inputStream)) != null) {
            if (line.isEmpty()) {
                break;
            }
            int delimiter = line.indexOf(':');
            if (delimiter > 0) {
                headers.put(line.substring(0, delimiter).trim(), line.substring(delimiter + 1).trim());
            }
        }
        int contentLength = parseContentLength(headers.get("Content-Length"));
        String body = "";
        if (contentLength > 0) {
            byte[] bodyBytes = new byte[contentLength];
            int offset = 0;
            while (offset < contentLength) {
                int read = inputStream.read(bodyBytes, offset, contentLength - offset);
                if (read < 0) {
                    break;
                }
                offset += read;
            }
            body = new String(bodyBytes, 0, offset, StandardCharsets.UTF_8);
        }
        return new RtspResponse(statusCode, statusText, headers, body);
    }

    private static int parseContentLength(String contentLength) {
        if (contentLength == null || contentLength.trim().isEmpty()) {
            return 0;
        }
        return Integer.parseInt(contentLength.trim());
    }

    private static String readLine(BufferedInputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        int previous = -1;
        int current;
        while ((current = inputStream.read()) != -1) {
            if (previous == '\r' && current == '\n') {
                builder.setLength(builder.length() - 1);
                return builder.toString();
            }
            builder.append((char) current);
            previous = current;
        }
        return builder.length() == 0 ? null : builder.toString();
    }
}
