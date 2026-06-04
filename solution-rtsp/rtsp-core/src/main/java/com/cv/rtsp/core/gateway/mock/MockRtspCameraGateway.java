package com.cv.rtsp.core.gateway.mock;

import com.cv.rtsp.core.exception.RtspClientException;
import com.cv.rtsp.core.gateway.RtspCameraGateway;
import com.cv.rtsp.core.model.RtspDescribeResult;
import com.cv.rtsp.core.model.RtspFrame;
import com.cv.rtsp.core.model.RtspPullSession;
import com.cv.rtsp.core.model.RtspSessionState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MockRtspCameraGateway implements RtspCameraGateway {

    private final Map<String, MockRtspCameraProfile> profileByUrl = new ConcurrentHashMap<String, MockRtspCameraProfile>();
    private final Map<String, AtomicLong> frameSequenceBySession = new ConcurrentHashMap<String, AtomicLong>();

    public void registerCamera(MockRtspCameraProfile profile) {
        profileByUrl.put(profile.getRtspUrl(), profile);
    }

    public Collection<MockRtspCameraProfile> listProfiles() {
        return Collections.unmodifiableCollection(new ArrayList<MockRtspCameraProfile>(profileByUrl.values()));
    }

    @Override
    public void options(RtspPullSession session) {
        MockRtspCameraProfile profile = requireProfile(session);
        validateCredential(profile, session);
        session.getMetrics().incrementOptions();
        session.setState(RtspSessionState.OPTIONS_NEGOTIATED);
    }

    @Override
    public RtspDescribeResult describe(RtspPullSession session) {
        MockRtspCameraProfile profile = requireProfile(session);
        validateCredential(profile, session);
        session.getMetrics().incrementDescribe();
        session.setState(RtspSessionState.DESCRIBED);
        RtspDescribeResult result = new RtspDescribeResult(profile.getSdp(), profile.getTracks());
        session.setDescribeResult(result);
        return result;
    }

    @Override
    public String setup(RtspPullSession session) {
        MockRtspCameraProfile profile = requireProfile(session);
        validateCredential(profile, session);
        String remoteSessionId = profile.getVendor() + "-" + UUID.randomUUID().toString().substring(0, 8);
        frameSequenceBySession.put(session.getSessionId(), new AtomicLong(0L));
        session.getMetrics().incrementSetup();
        session.setRemoteSessionId(remoteSessionId);
        session.setState(RtspSessionState.SETUP);
        return remoteSessionId;
    }

    @Override
    public void play(RtspPullSession session) {
        requireOpenSession(session);
        session.getMetrics().incrementPlay();
        session.setState(RtspSessionState.PLAYING);
    }

    @Override
    public void keepAlive(RtspPullSession session) {
        requireOpenSession(session);
        session.getMetrics().incrementKeepAlive();
        session.markKeepAlive();
    }

    @Override
    public RtspFrame readFrame(RtspPullSession session) {
        requireOpenSession(session);
        AtomicLong sequence = frameSequenceBySession.get(session.getSessionId());
        if (sequence == null) {
            throw new RtspClientException("RTSP session frame cursor not found: " + session.getSessionId());
        }
        MockRtspCameraProfile profile = requireProfile(session);
        long current = sequence.incrementAndGet();
        boolean keyFrame = current % 25 == 1;
        String payload = profile.getPayloadPrefix() + "-frame-" + String.format("%06d", current);
        return new RtspFrame(session.getEndpoint().getStreamId(), current, System.currentTimeMillis(),
                profile.getTracks().get(0).getCodec(), keyFrame, payload);
    }

    @Override
    public void teardown(RtspPullSession session) {
        session.getMetrics().incrementTeardown();
        frameSequenceBySession.remove(session.getSessionId());
        session.setState(RtspSessionState.STOPPED);
    }

    private void requireOpenSession(RtspPullSession session) {
        if (session.getState() == RtspSessionState.FAILED || session.getState() == RtspSessionState.STOPPED) {
            throw new RtspClientException("RTSP session already closed: " + session.getSessionId());
        }
        requireProfile(session);
    }

    private MockRtspCameraProfile requireProfile(RtspPullSession session) {
        MockRtspCameraProfile profile = profileByUrl.get(session.getEndpoint().getRtspUrl());
        if (profile == null) {
            throw new RtspClientException("Unknown RTSP camera: " + session.getEndpoint().getRtspUrl());
        }
        return profile;
    }

    private void validateCredential(MockRtspCameraProfile profile, RtspPullSession session) {
        String username = session.getEndpoint().getUsername();
        String password = session.getEndpoint().getPassword();
        if (!safeEquals(profile.getUsername(), username) || !safeEquals(profile.getPassword(), password)) {
            throw new RtspClientException("RTSP authentication failed for stream: " + session.getEndpoint().getStreamId());
        }
    }

    private boolean safeEquals(String expected, String actual) {
        return expected == null ? actual == null : expected.equals(actual);
    }
}
