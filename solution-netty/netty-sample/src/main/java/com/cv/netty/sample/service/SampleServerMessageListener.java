package com.cv.netty.sample.service;

import com.cv.netty.core.model.TransportMessage;
import com.cv.netty.core.server.DeviceSession;
import com.cv.netty.core.server.LoggingServerMessageListener;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@Service
public class SampleServerMessageListener extends LoggingServerMessageListener {

    private final Deque<String> latestEvents = new ArrayDeque<String>();

    @Override
    public synchronized void onSessionRegistered(DeviceSession session, TransportMessage message) {
        super.onSessionRegistered(session, message);
        record("REGISTER " + session.getDeviceId() + " " + session.getRemoteAddress());
    }

    @Override
    public synchronized void onHeartbeat(DeviceSession session, TransportMessage message) {
        super.onHeartbeat(session, message);
        if (session != null) {
            record("HEARTBEAT " + session.getDeviceId());
        }
    }

    @Override
    public synchronized void onTelemetry(DeviceSession session, TransportMessage message) {
        super.onTelemetry(session, message);
        if (session != null) {
            record("TELEMETRY " + session.getDeviceId() + " " + message.getPayload());
        }
    }

    @Override
    public synchronized void onCommandReply(DeviceSession session, TransportMessage message) {
        super.onCommandReply(session, message);
        if (session != null) {
            record("COMMAND_REPLY " + session.getDeviceId() + " " + message.getPayload());
        }
    }

    @Override
    public synchronized void onSessionClosed(DeviceSession session) {
        super.onSessionClosed(session);
        if (session != null) {
            record("CLOSED " + session.getDeviceId());
        }
    }

    public synchronized List<String> getLatestEvents() {
        return new ArrayList<String>(latestEvents);
    }

    private void record(String event) {
        if (latestEvents.size() >= 20) {
            latestEvents.removeFirst();
        }
        latestEvents.addLast(event);
    }
}
