package com.cv.rtsp.core.listener;

import com.cv.rtsp.core.model.RtspFrame;
import com.cv.rtsp.core.model.RtspPullSession;

public interface RtspFrameListener {

    default void onSessionStarted(RtspPullSession session) {
    }

    default void onFrame(RtspPullSession session, RtspFrame frame) {
    }

    default void onSessionStopped(RtspPullSession session) {
    }

    default void onSessionError(RtspPullSession session, Exception exception) {
    }
}
