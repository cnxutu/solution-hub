package com.cv.rtsp.core;

import com.cv.rtsp.core.client.RtspStreamClient;
import com.cv.rtsp.core.gateway.mock.MockRtspCameraGateway;
import com.cv.rtsp.core.gateway.mock.MockRtspCameraProfile;
import com.cv.rtsp.core.listener.RtspFrameListener;
import com.cv.rtsp.core.model.RtspEndpoint;
import com.cv.rtsp.core.model.RtspFrame;
import com.cv.rtsp.core.model.RtspMediaTrack;
import com.cv.rtsp.core.model.RtspPullSession;
import com.cv.rtsp.core.model.RtspSessionState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class RtspStreamClientTest {

    @Test
    public void shouldOpenPullKeepAliveAndCloseSession() {
        MockRtspCameraGateway gateway = new MockRtspCameraGateway();
        gateway.registerCamera(new MockRtspCameraProfile(
                "front-gate",
                "rtsp://mock-camera/front-gate/main",
                "admin",
                "123456",
                "hikvision",
                "v=0",
                Arrays.asList(new RtspMediaTrack("video", "H264", "trackID=1", 90000)),
                "front-gate"));

        RtspStreamClient client = new RtspStreamClient(gateway);
        RtspEndpoint endpoint = new RtspEndpoint("front-gate", "rtsp://mock-camera/front-gate/main",
                "admin", "123456", 3000, 5000, 10, 1000L, false);

        RtspPullSession session = client.open(endpoint, new RtspFrameListener() {
        });
        Assertions.assertEquals(RtspSessionState.PLAYING, session.getState());

        RtspFrame frame = client.pullFrame(session, new RtspFrameListener() {
        });
        Assertions.assertEquals("front-gate", frame.getStreamId());
        Assertions.assertEquals(1L, frame.getSequence());

        client.keepAlive(session);
        Assertions.assertTrue(session.getLastKeepAliveAt() > 0L);

        client.close(session, new RtspFrameListener() {
        });
        Assertions.assertEquals(RtspSessionState.STOPPED, session.getState());
    }
}
