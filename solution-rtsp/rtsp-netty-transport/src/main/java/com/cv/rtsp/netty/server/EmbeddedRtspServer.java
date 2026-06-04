package com.cv.rtsp.netty.server;

import com.cv.rtsp.core.model.RtspMediaTrack;
import com.cv.rtsp.netty.model.RtspServerProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EmbeddedRtspServer {

    private static final Logger log = LoggerFactory.getLogger(EmbeddedRtspServer.class);

    private final int port;
    private final Map<String, RtspServerProfile> profiles = new ConcurrentHashMap<String, RtspServerProfile>();
    private volatile boolean running;
    private volatile ServerSocket serverSocket;
    private volatile Thread acceptThread;

    public EmbeddedRtspServer(int port) {
        this.port = port;
    }

    public void registerProfile(RtspServerProfile profile) {
        profiles.put(normalizePath(profile.getStreamPath()), profile);
    }

    public synchronized void start() {
        if (running) {
            return;
        }
        try {
            running = true;
            serverSocket = new ServerSocket(port);
            acceptThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    acceptLoop();
                }
            }, "embedded-rtsp-server");
            acceptThread.setDaemon(true);
            acceptThread.start();
            log.info("Embedded RTSP server started on port {}", port);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to start embedded RTSP server", exception);
        }
    }

    public synchronized void stop() {
        running = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ignore) {
        }
    }

    private void acceptLoop() {
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                Thread thread = new Thread(new ConnectionHandler(socket), "embedded-rtsp-conn");
                thread.setDaemon(true);
                thread.start();
            } catch (IOException exception) {
                if (running) {
                    log.warn("Embedded RTSP accept failed", exception);
                }
            }
        }
    }

    private class ConnectionHandler implements Runnable {

        private final Socket socket;
        private final Map<String, String> sessionIdByPath = new LinkedHashMap<String, String>();

        private ConnectionHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedInputStream inputStream = new BufferedInputStream(socket.getInputStream());
                OutputStream outputStream = socket.getOutputStream();
                while (running && !socket.isClosed()) {
                    Request request = Request.read(inputStream);
                    if (request == null) {
                        break;
                    }
                    Response response = handle(request);
                    outputStream.write(response.render().getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                }
            } catch (IOException exception) {
                log.debug("Embedded RTSP connection closed: {}", exception.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException ignore) {
                }
            }
        }

        private Response handle(Request request) {
            String normalizedPath = normalizePath(request.getPath());
            RtspServerProfile profile = profiles.get(normalizedPath);
            if (profile == null) {
                if (normalizedPath.endsWith("/trackID=1")) {
                    profile = profiles.get(normalizedPath.substring(0, normalizedPath.length() - "/trackID=1".length()));
                }
            }
            if (profile == null) {
                return Response.error(request.getCSeq(), 404, "Not Found");
            }
            if (!authorizationOk(request.getAuthorization(), profile)) {
                return Response.error(request.getCSeq(), 401, "Unauthorized");
            }
            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                return Response.ok(request.getCSeq()).header("Public", "OPTIONS, DESCRIBE, SETUP, PLAY, GET_PARAMETER, TEARDOWN");
            }
            if ("DESCRIBE".equalsIgnoreCase(request.getMethod())) {
                return Response.ok(request.getCSeq())
                        .header("Content-Type", "application/sdp")
                        .body(profile.getSdp());
            }
            if ("SETUP".equalsIgnoreCase(request.getMethod())) {
                String sessionId = UUID.randomUUID().toString().substring(0, 8);
                sessionIdByPath.put(normalizedPath, sessionId);
                return Response.ok(request.getCSeq())
                        .header("Session", sessionId)
                        .header("Transport", "RTP/AVP/TCP;unicast;interleaved=0-1");
            }
            if ("PLAY".equalsIgnoreCase(request.getMethod())) {
                return Response.ok(request.getCSeq()).header("Session", request.getSession());
            }
            if ("GET_PARAMETER".equalsIgnoreCase(request.getMethod())) {
                return Response.ok(request.getCSeq()).header("Session", request.getSession());
            }
            if ("TEARDOWN".equalsIgnoreCase(request.getMethod())) {
                return Response.ok(request.getCSeq()).header("Session", request.getSession());
            }
            return Response.error(request.getCSeq(), 405, "Method Not Allowed");
        }

        private boolean authorizationOk(String authorization, RtspServerProfile profile) {
            String expected = "Basic " + profile.getUsername() + ":" + profile.getPassword();
            return expected.equals(authorization);
        }
    }

    private static String normalizePath(String path) {
        return path.startsWith("/") ? path : "/" + path;
    }

    private static class Request {

        private final String method;
        private final String path;
        private final String cSeq;
        private final String authorization;
        private final String session;

        private Request(String method, String path, String cSeq, String authorization, String session) {
            this.method = method;
            this.path = path;
            this.cSeq = cSeq;
            this.authorization = authorization;
            this.session = session;
        }

        private static Request read(BufferedInputStream inputStream) throws IOException {
            String requestLine = readLine(inputStream);
            if (requestLine == null || requestLine.trim().isEmpty()) {
                return null;
            }
            String[] firstLine = requestLine.split(" ");
            if (firstLine.length < 2) {
                return null;
            }
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
            String target = firstLine[1];
            String path = target;
            int schemaIndex = target.indexOf("://");
            if (schemaIndex > 0) {
                int pathIndex = target.indexOf('/', schemaIndex + 3);
                path = pathIndex > 0 ? target.substring(pathIndex) : "/";
            }
            return new Request(firstLine[0], path, headers.get("CSeq"), headers.get("Authorization"), headers.get("Session"));
        }

        private String getMethod() {
            return method;
        }

        private String getPath() {
            return path;
        }

        private String getCSeq() {
            return cSeq == null ? "1" : cSeq;
        }

        private String getAuthorization() {
            return authorization;
        }

        private String getSession() {
            return session;
        }
    }

    private static class Response {

        private final int statusCode;
        private final String statusText;
        private final Map<String, String> headers = new LinkedHashMap<String, String>();
        private String body;

        private Response(int statusCode, String statusText) {
            this.statusCode = statusCode;
            this.statusText = statusText;
        }

        private static Response ok(String cSeq) {
            return new Response(200, "OK").header("CSeq", cSeq);
        }

        private static Response error(String cSeq, int statusCode, String statusText) {
            return new Response(statusCode, statusText).header("CSeq", cSeq);
        }

        private Response header(String key, String value) {
            headers.put(key, value);
            return this;
        }

        private Response body(String body) {
            this.body = body;
            return this;
        }

        private String render() {
            StringBuilder builder = new StringBuilder();
            builder.append("RTSP/1.0 ").append(statusCode).append(" ").append(statusText).append("\r\n");
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

    public static RtspServerProfile buildProfile(String streamPath,
                                                 String username,
                                                 String password,
                                                 String streamName,
                                                 String payloadPrefix) {
        String sdp = "v=0\n"
                + "o=- 0 0 IN IP4 127.0.0.1\n"
                + "s=" + streamName + "\n"
                + "t=0 0\n"
                + "m=video 0 RTP/AVP 96\n"
                + "a=rtpmap:96 H264/90000\n"
                + "a=control:trackID=1";
        return new RtspServerProfile(streamPath, username, password, sdp,
                java.util.Collections.singletonList(new RtspMediaTrack("video", "H264", "trackID=1", 90000)),
                payloadPrefix);
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
