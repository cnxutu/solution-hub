package com.cv.rtsp.netty.gateway;

import com.cv.rtsp.core.exception.RtspClientException;
import com.cv.rtsp.core.gateway.RtspCameraGateway;
import com.cv.rtsp.core.model.RtspDescribeResult;
import com.cv.rtsp.core.model.RtspFrame;
import com.cv.rtsp.core.model.RtspMediaTrack;
import com.cv.rtsp.core.model.RtspPullSession;
import com.cv.rtsp.core.model.RtspSessionState;
import com.cv.rtsp.netty.client.RtspSocketSession;
import com.cv.rtsp.netty.protocol.RtspRequest;
import com.cv.rtsp.netty.protocol.RtspResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class NettyRtspCameraGateway implements RtspCameraGateway {

    private static final Logger log = LoggerFactory.getLogger(NettyRtspCameraGateway.class);

    private final Map<String, RtspSocketSession> transportSessions = new ConcurrentHashMap<String, RtspSocketSession>();
    private final Map<String, AtomicLong> frameSequences = new ConcurrentHashMap<String, AtomicLong>();
    private final Map<String, String> payloadPrefixes = new ConcurrentHashMap<String, String>();

    @Override
    public void options(RtspPullSession session) {
        RtspSocketSession transport = transportSessions.computeIfAbsent(session.getSessionId(), key ->
                new RtspSocketSession(session.getEndpoint().getRtspUrl(),
                        session.getEndpoint().getConnectTimeoutMillis(),
                        session.getEndpoint().getReadTimeoutMillis()));
        RtspRequest request = baseRequest("OPTIONS", session).header("User-Agent", "solution-rtsp-netty-demo");
        RtspResponse response = transport.exchange(request);
        assertSuccessful(response, "OPTIONS");
        session.getMetrics().incrementOptions();
        session.setState(RtspSessionState.OPTIONS_NEGOTIATED);
    }

    @Override
    public RtspDescribeResult describe(RtspPullSession session) {
        RtspRequest request = baseRequest("DESCRIBE", session)
                .header("Accept", "application/sdp");
        RtspResponse response = requireTransport(session).exchange(request);
        assertSuccessful(response, "DESCRIBE");
        RtspDescribeResult result = new RtspDescribeResult(response.getBody(), parseTracks(response.getBody()));
        session.getMetrics().incrementDescribe();
        session.setDescribeResult(result);
        session.setState(RtspSessionState.DESCRIBED);
        return result;
    }

    @Override
    public String setup(RtspPullSession session) {
        RtspRequest request = baseRequest("SETUP", session)
                .header("Transport", "RTP/AVP/TCP;unicast;interleaved=0-1");
        RtspDescribeResult describeResult = session.getDescribeResult();
        if (describeResult != null && !describeResult.getTracks().isEmpty()) {
            RtspMediaTrack firstTrack = describeResult.getTracks().get(0);
            request = new RtspRequest("SETUP", session.getEndpoint().getRtspUrl() + "/" + firstTrack.getControl())
                    .header("CSeq", String.valueOf(session.nextCSeq()))
                    .header("Authorization", authorizationHeader(session))
                    .header("Transport", "RTP/AVP/TCP;unicast;interleaved=0-1");
        }
        RtspResponse response = requireTransport(session).exchange(request);
        assertSuccessful(response, "SETUP");
        String remoteSessionId = response.getHeader("Session");
        if (remoteSessionId == null || remoteSessionId.trim().isEmpty()) {
            remoteSessionId = "rtsp-session-" + UUID.randomUUID().toString().substring(0, 8);
        }
        session.setRemoteSessionId(remoteSessionId);
        session.getMetrics().incrementSetup();
        session.setState(RtspSessionState.SETUP);
        frameSequences.put(session.getSessionId(), new AtomicLong(0L));
        payloadPrefixes.put(session.getSessionId(), session.getEndpoint().getStreamId());
        return remoteSessionId;
    }

    @Override
    public void play(RtspPullSession session) {
        RtspRequest request = baseRequest("PLAY", session)
                .header("Session", session.getRemoteSessionId());
        RtspResponse response = requireTransport(session).exchange(request);
        assertSuccessful(response, "PLAY");
        session.getMetrics().incrementPlay();
        session.setState(RtspSessionState.PLAYING);
    }

    @Override
    public void keepAlive(RtspPullSession session) {
        RtspRequest request = baseRequest("GET_PARAMETER", session)
                .header("Session", session.getRemoteSessionId());
        RtspResponse response = requireTransport(session).exchange(request);
        assertSuccessful(response, "GET_PARAMETER");
        session.getMetrics().incrementKeepAlive();
        session.markKeepAlive();
    }

    @Override
    public RtspFrame readFrame(RtspPullSession session) {
        AtomicLong sequence = frameSequences.get(session.getSessionId());
        if (sequence == null) {
            throw new RtspClientException("RTSP frame sequence not initialized for session: " + session.getSessionId());
        }
        long current = sequence.incrementAndGet();
        String payloadPrefix = payloadPrefixes.get(session.getSessionId());
        boolean keyFrame = current % 30 == 1;
        return new RtspFrame(session.getEndpoint().getStreamId(), current, System.currentTimeMillis(),
                codecFromSession(session), keyFrame, payloadPrefix + "-rtsp-control-frame-" + current);
    }

    @Override
    public void teardown(RtspPullSession session) {
        RtspSocketSession transport = transportSessions.remove(session.getSessionId());
        try {
            if (transport != null) {
                RtspRequest request = baseRequest("TEARDOWN", session)
                        .header("Session", session.getRemoteSessionId());
                RtspResponse response = transport.exchange(request);
                assertSuccessful(response, "TEARDOWN");
            }
        } finally {
            if (transport != null) {
                transport.closeQuietly();
            }
            frameSequences.remove(session.getSessionId());
            payloadPrefixes.remove(session.getSessionId());
        }
        session.getMetrics().incrementTeardown();
        session.setState(RtspSessionState.STOPPED);
    }

    private RtspSocketSession requireTransport(RtspPullSession session) {
        RtspSocketSession transport = transportSessions.get(session.getSessionId());
        if (transport == null) {
            throw new RtspClientException("RTSP transport not initialized for session: " + session.getSessionId());
        }
        return transport;
    }

    private RtspRequest baseRequest(String method, RtspPullSession session) {
        return new RtspRequest(method, session.getEndpoint().getRtspUrl())
                .header("CSeq", String.valueOf(session.nextCSeq()))
                .header("Authorization", authorizationHeader(session));
    }

    private String authorizationHeader(RtspPullSession session) {
        return "Basic " + session.getEndpoint().getUsername() + ":" + session.getEndpoint().getPassword();
    }

    private void assertSuccessful(RtspResponse response, String method) {
        if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
            return;
        }
        log.warn("RTSP {} failed: {} {}", method, response.getStatusCode(), response.getStatusText());
        throw new RtspClientException("RTSP " + method + " failed: " + response.getStatusCode() + " " + response.getStatusText());
    }

    private List<RtspMediaTrack> parseTracks(String sdp) {
        if (sdp == null || sdp.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<RtspMediaTrack> tracks = new ArrayList<RtspMediaTrack>();
        String[] lines = sdp.split("\\r?\\n");
        String mediaType = null;
        String codec = "H264";
        String control = "trackID=1";
        int clockRate = 90000;
        for (String line : lines) {
            if (line.startsWith("m=")) {
                if (mediaType != null) {
                    tracks.add(new RtspMediaTrack(mediaType, codec, control, clockRate));
                }
                String[] parts = line.substring(2).split(" ");
                mediaType = parts[0];
                codec = "H264";
                control = "trackID=1";
                clockRate = 90000;
            } else if (line.startsWith("a=rtpmap:")) {
                String[] parts = line.substring("a=rtpmap:".length()).split(" ");
                if (parts.length > 1) {
                    String[] codecParts = parts[1].split("/");
                    codec = codecParts[0];
                    if (codecParts.length > 1) {
                        clockRate = Integer.parseInt(codecParts[1]);
                    }
                }
            } else if (line.startsWith("a=control:")) {
                control = line.substring("a=control:".length());
            }
        }
        if (mediaType != null) {
            tracks.add(new RtspMediaTrack(mediaType, codec, control, clockRate));
        }
        return tracks;
    }

    private String codecFromSession(RtspPullSession session) {
        RtspDescribeResult describeResult = session.getDescribeResult();
        if (describeResult == null || describeResult.getTracks().isEmpty()) {
            return "H264";
        }
        return describeResult.getTracks().get(0).getCodec();
    }
}
