package com.cv.netty.core.client;

import com.cv.netty.core.codec.TransportMessageCodec;
import com.cv.netty.core.common.NettyConstants;
import com.cv.netty.core.model.TransportMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public class ClientMessageHandler extends SimpleChannelInboundHandler<String> {

    private final NettyTcpClient client;
    private final ClientMessageListener listener;

    public ClientMessageHandler(NettyTcpClient client, ClientMessageListener listener) {
        this.client = client;
        this.listener = listener;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        client.onChannelActive(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        TransportMessage message = TransportMessageCodec.decode(msg);
        if (NettyConstants.TYPE_ACK.equals(message.getType())) {
            listener.onAck(client.getDeviceId(), message);
            return;
        }
        listener.onServerMessage(client.getDeviceId(), message);
        if (NettyConstants.TYPE_COMMAND.equals(message.getType())) {
            Map<String, Object> payload = new LinkedHashMap<String, Object>();
            payload.put("status", "ACCEPTED");
            payload.put("command", message.getPayload() == null ? null : message.getPayload().get("command"));
            payload.put("processedAt", System.currentTimeMillis());
            client.sendCommandReply(message.getMessageId(), payload);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        client.onChannelInactive();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (IdleState.WRITER_IDLE.equals(idleStateEvent.state())) {
                client.sendHeartbeat();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        listener.onException(client.getDeviceId(), cause);
        ctx.close();
    }
}
