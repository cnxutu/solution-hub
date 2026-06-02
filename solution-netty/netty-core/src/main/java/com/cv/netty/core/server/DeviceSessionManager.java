package com.cv.netty.core.server;

import com.cv.netty.core.codec.TransportMessageCodec;
import com.cv.netty.core.model.TransportMessage;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DeviceSessionManager {

    private final Map<String, DeviceSession> sessionsByDeviceId = new ConcurrentHashMap<String, DeviceSession>();
    private final Map<String, Channel> channelsByDeviceId = new ConcurrentHashMap<String, Channel>();
    private final Map<String, String> deviceByChannelId = new ConcurrentHashMap<String, String>();

    public DeviceSession bind(String deviceId, Channel channel) {
        long now = System.currentTimeMillis();
        DeviceSession session = new DeviceSession(deviceId, channel.id().asShortText(),
                String.valueOf(channel.remoteAddress()), now, now);
        sessionsByDeviceId.put(deviceId, session);
        channelsByDeviceId.put(deviceId, channel);
        deviceByChannelId.put(channel.id().asShortText(), deviceId);
        return session;
    }

    public DeviceSession touch(String deviceId) {
        DeviceSession session = sessionsByDeviceId.get(deviceId);
        if (session != null) {
            session.setLastSeenAt(System.currentTimeMillis());
        }
        return session;
    }

    public DeviceSession removeByChannel(Channel channel) {
        if (channel == null) {
            return null;
        }
        String channelId = channel.id().asShortText();
        String deviceId = deviceByChannelId.remove(channelId);
        if (deviceId == null) {
            return null;
        }
        channelsByDeviceId.remove(deviceId);
        return sessionsByDeviceId.remove(deviceId);
    }

    public DeviceSession getSession(String deviceId) {
        return sessionsByDeviceId.get(deviceId);
    }

    public Collection<DeviceSession> listSessions() {
        return Collections.unmodifiableCollection(new ArrayList<DeviceSession>(sessionsByDeviceId.values()));
    }

    public boolean send(String deviceId, TransportMessage message) {
        Channel channel = channelsByDeviceId.get(deviceId);
        if (channel == null || !channel.isActive()) {
            return false;
        }
        channel.writeAndFlush(TransportMessageCodec.encode(message));
        return true;
    }
}
