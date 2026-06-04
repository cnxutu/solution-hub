package com.cv.rtsp.sample.service;

import com.cv.rtsp.core.listener.RtspFrameListener;
import com.cv.rtsp.core.model.RtspFrame;
import com.cv.rtsp.core.model.RtspPullSession;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SampleRtspObserver implements RtspFrameListener {

    private static final int MAX_EVENTS = 50;
    private static final int MAX_FRAMES_PER_STREAM = 20;

    private final Deque<String> latestEvents = new LinkedList<String>();
    private final Map<String, Deque<Map<String, Object>>> latestFrames = new ConcurrentHashMap<String, Deque<Map<String, Object>>>();

    @Override
    public void onSessionStarted(RtspPullSession session) {
        appendEvent("START stream=" + session.getEndpoint().getStreamId() + ", session=" + session.getSessionId());
    }

    @Override
    public void onFrame(RtspPullSession session, RtspFrame frame) {
        appendEvent("FRAME stream=" + frame.getStreamId() + ", seq=" + frame.getSequence() + ", keyFrame=" + frame.isKeyFrame());
        Deque<Map<String, Object>> queue = latestFrames.computeIfAbsent(frame.getStreamId(), key -> new LinkedList<Map<String, Object>>());
        synchronized (queue) {
            if (queue.size() >= MAX_FRAMES_PER_STREAM) {
                queue.removeFirst();
            }
            Map<String, Object> snapshot = new LinkedHashMap<String, Object>();
            snapshot.put("streamId", frame.getStreamId());
            snapshot.put("sequence", frame.getSequence());
            snapshot.put("timestamp", frame.getTimestamp());
            snapshot.put("codec", frame.getCodec());
            snapshot.put("keyFrame", frame.isKeyFrame());
            snapshot.put("payloadPreview", frame.getPayloadPreview());
            queue.addLast(snapshot);
        }
    }

    @Override
    public void onSessionStopped(RtspPullSession session) {
        appendEvent("STOP stream=" + session.getEndpoint().getStreamId() + ", session=" + session.getSessionId());
    }

    @Override
    public void onSessionError(RtspPullSession session, Exception exception) {
        appendEvent("ERROR stream=" + session.getEndpoint().getStreamId() + ", error=" + exception.getMessage());
    }

    public void appendSystemEvent(String event) {
        appendEvent("SYSTEM " + event);
    }

    public Collection<String> latestEvents() {
        synchronized (latestEvents) {
            return new ArrayList<String>(latestEvents);
        }
    }

    public Collection<Map<String, Object>> latestFrames(String streamId) {
        Deque<Map<String, Object>> queue = latestFrames.get(streamId);
        if (queue == null) {
            return Collections.emptyList();
        }
        synchronized (queue) {
            return new ArrayList<Map<String, Object>>(queue);
        }
    }

    private void appendEvent(String event) {
        synchronized (latestEvents) {
            if (latestEvents.size() >= MAX_EVENTS) {
                latestEvents.removeFirst();
            }
            latestEvents.addLast(event);
        }
    }
}
