package com.cv.netty.core.common;

import io.netty.util.AttributeKey;

public final class NettyConstants {

    public static final String TYPE_REGISTER = "register";
    public static final String TYPE_HEARTBEAT = "heartbeat";
    public static final String TYPE_TELEMETRY = "telemetry";
    public static final String TYPE_COMMAND = "command";
    public static final String TYPE_COMMAND_REPLY = "command_reply";
    public static final String TYPE_ACK = "ack";

    public static final String STATUS_OK = "OK";
    public static final String STATUS_FAIL = "FAIL";

    public static final String LINE_DELIMITER = "\n";
    public static final int MAX_FRAME_LENGTH = 8192;

    public static final AttributeKey<String> ATTR_DEVICE_ID = AttributeKey.valueOf("deviceId");

    private NettyConstants() {
    }
}
