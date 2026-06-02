package com.cv.netty.core.client;

import com.cv.netty.core.common.NettyConstants;
import com.cv.netty.core.model.TransportMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class NettyTcpClient {

    private final String host;
    private final int port;
    private final String deviceId;
    private final int heartbeatSeconds;
    private final int reconnectDelaySeconds;
    private final ClientMessageListener listener;
    private final AtomicBoolean running = new AtomicBoolean(false);

    private EventLoopGroup workerGroup;
    private Bootstrap bootstrap;
    private volatile Channel channel;

    public NettyTcpClient(String host, int port, String deviceId, int heartbeatSeconds,
                          int reconnectDelaySeconds, ClientMessageListener listener) {
        this.host = host;
        this.port = port;
        this.deviceId = deviceId;
        this.heartbeatSeconds = heartbeatSeconds;
        this.reconnectDelaySeconds = reconnectDelaySeconds;
        this.listener = listener == null ? new LoggingClientMessageListener() : listener;
    }

    public synchronized void start() {
        if (!running.compareAndSet(false, true)) {
            return;
        }
        workerGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ClientChannelInitializer(this, heartbeatSeconds, listener));
        doConnect();
    }

    public synchronized void stop() {
        running.set(false);
        Channel currentChannel = channel;
        if (currentChannel != null) {
            currentChannel.close();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        channel = null;
        workerGroup = null;
        bootstrap = null;
    }

    public void doConnect() {
        if (!running.get()) {
            return;
        }
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                channel = ((io.netty.channel.ChannelFuture) future).channel();
            } else if (running.get() && workerGroup != null) {
                workerGroup.schedule(this::doConnect, reconnectDelaySeconds, TimeUnit.SECONDS);
            }
        });
    }

    public void onChannelActive(Channel activeChannel) {
        this.channel = activeChannel;
        listener.onConnected(deviceId);
        Map<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("clientType", "mock-device");
        payload.put("connectedAt", System.currentTimeMillis());
        send(TransportMessage.of(NettyConstants.TYPE_REGISTER, deviceId, payload));
    }

    public void onChannelInactive() {
        listener.onDisconnected(deviceId);
        channel = null;
        if (running.get() && workerGroup != null) {
            workerGroup.schedule(this::doConnect, reconnectDelaySeconds, TimeUnit.SECONDS);
        }
    }

    public void sendHeartbeat() {
        Map<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("ts", System.currentTimeMillis());
        send(TransportMessage.of(NettyConstants.TYPE_HEARTBEAT, deviceId, payload));
    }

    public void sendTelemetry(Map<String, Object> payload) {
        send(TransportMessage.of(NettyConstants.TYPE_TELEMETRY, deviceId, payload));
    }

    public void sendCommandReply(String sourceMessageId, Map<String, Object> payload) {
        Map<String, Object> safePayload = payload == null ? new LinkedHashMap<String, Object>() : new LinkedHashMap<String, Object>(payload);
        safePayload.put("sourceMessageId", sourceMessageId);
        send(TransportMessage.of(NettyConstants.TYPE_COMMAND_REPLY, deviceId, safePayload));
    }

    public void send(TransportMessage message) {
        Channel currentChannel = this.channel;
        if (currentChannel != null && currentChannel.isActive()) {
            currentChannel.writeAndFlush(com.cv.netty.core.codec.TransportMessageCodec.encode(message));
        }
    }

    public String getDeviceId() {
        return deviceId;
    }
}
