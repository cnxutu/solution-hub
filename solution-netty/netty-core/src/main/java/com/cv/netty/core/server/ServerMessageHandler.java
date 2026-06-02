package com.cv.netty.core.server;

import com.cv.netty.core.codec.TransportMessageCodec;
import com.cv.netty.core.common.NettyConstants;
import com.cv.netty.core.model.TransportMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public class ServerMessageHandler extends SimpleChannelInboundHandler<String> {

    private final DeviceSessionManager sessionManager;
    private final ServerMessageListener listener;

    public ServerMessageHandler(DeviceSessionManager sessionManager, ServerMessageListener listener) {
        this.sessionManager = sessionManager;
        this.listener = listener;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        TransportMessage message = TransportMessageCodec.decode(msg);
        String type = message.getType();
        if (NettyConstants.TYPE_REGISTER.equals(type)) {
            handleRegister(ctx.channel(), message);
            return;
        }

        DeviceSession session = sessionManager.touch(message.getDeviceId());
        if (NettyConstants.TYPE_HEARTBEAT.equals(type)) {
            listener.onHeartbeat(session, message);
            writeAck(ctx, message, NettyConstants.STATUS_OK, "heartbeat accepted");
            return;
        }
        if (NettyConstants.TYPE_TELEMETRY.equals(type)) {
            listener.onTelemetry(session, message);
            writeAck(ctx, message, NettyConstants.STATUS_OK, "telemetry accepted");
            return;
        }
        if (NettyConstants.TYPE_COMMAND_REPLY.equals(type)) {
            listener.onCommandReply(session, message);
            writeAck(ctx, message, NettyConstants.STATUS_OK, "reply accepted");
            return;
        }
        writeAck(ctx, message, NettyConstants.STATUS_FAIL, "unsupported message type");
    }

    private void handleRegister(Channel channel, TransportMessage message) {
        channel.attr(NettyConstants.ATTR_DEVICE_ID).set(message.getDeviceId());
        DeviceSession session = sessionManager.bind(message.getDeviceId(), channel);
        listener.onSessionRegistered(session, message);
        channel.writeAndFlush(TransportMessageCodec.encode(buildAck(message, NettyConstants.STATUS_OK, "register accepted")));
    }

    private void writeAck(ChannelHandlerContext ctx, TransportMessage request, String status, String remark) {
        ctx.writeAndFlush(TransportMessageCodec.encode(buildAck(request, status, remark)));
    }

    private TransportMessage buildAck(TransportMessage request, String status, String remark) {
        Map<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("status", status);
        payload.put("requestType", request.getType());
        payload.put("requestMessageId", request.getMessageId());
        payload.put("remark", remark);
        return TransportMessage.of(NettyConstants.TYPE_ACK, request.getDeviceId(), payload);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (IdleState.READER_IDLE.equals(idleStateEvent.state())) {
                ctx.close();
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        DeviceSession session = sessionManager.removeByChannel(ctx.channel());
        listener.onSessionClosed(session);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        listener.onException(cause);
        ctx.close();
    }
}
