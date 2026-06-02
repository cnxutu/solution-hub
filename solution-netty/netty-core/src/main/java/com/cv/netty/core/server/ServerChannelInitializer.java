package com.cv.netty.core.server;

import com.cv.netty.core.common.NettyConstants;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final int readIdleSeconds;
    private final DeviceSessionManager sessionManager;
    private final ServerMessageListener listener;

    public ServerChannelInitializer(int readIdleSeconds, DeviceSessionManager sessionManager, ServerMessageListener listener) {
        this.readIdleSeconds = readIdleSeconds;
        this.sessionManager = sessionManager;
        this.listener = listener;
    }

    @Override
    protected void initChannel(SocketChannel channel) {
        channel.pipeline()
                .addLast(new DelimiterBasedFrameDecoder(
                        NettyConstants.MAX_FRAME_LENGTH,
                        Unpooled.copiedBuffer(NettyConstants.LINE_DELIMITER, CharsetUtil.UTF_8)))
                .addLast(new StringDecoder(CharsetUtil.UTF_8))
                .addLast(new StringEncoder(CharsetUtil.UTF_8))
                .addLast(new IdleStateHandler(readIdleSeconds, 0, 0, TimeUnit.SECONDS))
                .addLast(new ServerMessageHandler(sessionManager, listener));
    }
}
