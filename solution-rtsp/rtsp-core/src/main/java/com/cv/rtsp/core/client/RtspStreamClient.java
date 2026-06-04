package com.cv.rtsp.core.client;

import com.cv.rtsp.core.exception.RtspClientException;
import com.cv.rtsp.core.gateway.RtspCameraGateway;
import com.cv.rtsp.core.listener.RtspFrameListener;
import com.cv.rtsp.core.model.RtspDescribeResult;
import com.cv.rtsp.core.model.RtspEndpoint;
import com.cv.rtsp.core.model.RtspFrame;
import com.cv.rtsp.core.model.RtspPullSession;
import com.cv.rtsp.core.model.RtspSessionState;

import java.util.UUID;

public class RtspStreamClient {

    private final RtspCameraGateway gateway;

    public RtspStreamClient(RtspCameraGateway gateway) {
        this.gateway = gateway;
    }

    public RtspPullSession open(RtspEndpoint endpoint, RtspFrameListener listener) {
        RtspPullSession session = new RtspPullSession(buildSessionId(endpoint), endpoint);
        try {
            session.markStarted();
            gateway.options(session);
            RtspDescribeResult describeResult = gateway.describe(session);
            session.setDescribeResult(describeResult);
            gateway.setup(session);
            gateway.play(session);
            listener.onSessionStarted(session);
            return session;
        } catch (Exception exception) {
            session.getMetrics().incrementError();
            session.setState(RtspSessionState.FAILED);
            session.setLastError(exception.getMessage());
            listener.onSessionError(session, wrap(exception));
            throw wrap(exception);
        }
    }

    public RtspFrame pullFrame(RtspPullSession session, RtspFrameListener listener) {
        try {
            RtspFrame frame = gateway.readFrame(session);
            session.getMetrics().incrementFrame();
            session.markFrame();
            listener.onFrame(session, frame);
            return frame;
        } catch (Exception exception) {
            session.getMetrics().incrementError();
            session.setState(RtspSessionState.FAILED);
            session.setLastError(exception.getMessage());
            listener.onSessionError(session, wrap(exception));
            throw wrap(exception);
        }
    }

    public void keepAlive(RtspPullSession session) {
        try {
            gateway.keepAlive(session);
        } catch (Exception exception) {
            session.getMetrics().incrementError();
            session.setState(RtspSessionState.FAILED);
            session.setLastError(exception.getMessage());
            throw wrap(exception);
        }
    }

    public void close(RtspPullSession session, RtspFrameListener listener) {
        try {
            gateway.teardown(session);
            listener.onSessionStopped(session);
        } catch (Exception exception) {
            session.getMetrics().incrementError();
            session.setState(RtspSessionState.FAILED);
            session.setLastError(exception.getMessage());
            listener.onSessionError(session, wrap(exception));
            throw wrap(exception);
        }
    }

    private String buildSessionId(RtspEndpoint endpoint) {
        return endpoint.getStreamId() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private RtspClientException wrap(Exception exception) {
        if (exception instanceof RtspClientException) {
            return (RtspClientException) exception;
        }
        return new RtspClientException("RTSP client error", exception);
    }
}
