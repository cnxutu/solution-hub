package com.cv.netty.core.server;

import com.cv.netty.core.common.NettyConstants;
import com.cv.netty.core.model.TransportMessage;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Collection;
import java.util.Map;

public class NettyTcpServer {

    private final int port;
    private final int bossThreads;
    private final int workerThreads;
    private final int readIdleSeconds;
    private final ServerMessageListener listener;
    private final DeviceSessionManager sessionManager = new DeviceSessionManager();

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;

    public NettyTcpServer(int port, int bossThreads, int workerThreads, int readIdleSeconds, ServerMessageListener listener) {
        this.port = port;
        this.bossThreads = bossThreads;
        this.workerThreads = workerThreads;
        this.readIdleSeconds = readIdleSeconds;
        this.listener = listener == null ? new LoggingServerMessageListener() : listener;
    }

    public synchronized void start() throws InterruptedException {
        if (serverChannel != null && serverChannel.isActive()) {
            return;
        }
        bossGroup = new NioEventLoopGroup(bossThreads);
        workerGroup = new NioEventLoopGroup(workerThreads);

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ServerChannelInitializer(readIdleSeconds, sessionManager, listener));

        serverChannel = bootstrap.bind(port).sync().channel();
    }

    public synchronized void stop() {
        if (serverChannel != null) {
            serverChannel.close();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        serverChannel = null;
        workerGroup = null;
        bossGroup = null;
    }

    public boolean sendCommandToDevice(String deviceId, String command, Map<String, Object> payload) {
        Map<String, Object> safePayload = payload == null
                ? new java.util.LinkedHashMap<String, Object>()
                : new java.util.LinkedHashMap<String, Object>(payload);
        safePayload.put("command", command);
        TransportMessage message = TransportMessage.of(NettyConstants.TYPE_COMMAND, deviceId, safePayload);
        return sessionManager.send(deviceId, message);
    }

    public Collection<DeviceSession> getConnectedSessions() {
        return sessionManager.listSessions();
    }

    public DeviceSessionManager getSessionManager() {
        return sessionManager;
    }
}
