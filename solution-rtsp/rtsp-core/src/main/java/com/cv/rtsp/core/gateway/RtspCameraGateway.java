package com.cv.rtsp.core.gateway;

import com.cv.rtsp.core.model.RtspDescribeResult;
import com.cv.rtsp.core.model.RtspFrame;
import com.cv.rtsp.core.model.RtspPullSession;

public interface RtspCameraGateway {

    void options(RtspPullSession session);

    RtspDescribeResult describe(RtspPullSession session);

    String setup(RtspPullSession session);

    void play(RtspPullSession session);

    void keepAlive(RtspPullSession session);

    RtspFrame readFrame(RtspPullSession session);

    void teardown(RtspPullSession session);
}
