package com.cv.netty.core.codec;

import com.cv.netty.core.common.NettyConstants;
import com.cv.netty.core.model.TransportMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public final class TransportMessageCodec {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private TransportMessageCodec() {
    }

    public static TransportMessage decode(String text) {
        try {
            return OBJECT_MAPPER.readValue(text, TransportMessage.class);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to decode transport message: " + text, ex);
        }
    }

    public static String encode(TransportMessage message) {
        try {
            return OBJECT_MAPPER.writeValueAsString(message) + NettyConstants.LINE_DELIMITER;
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Failed to encode transport message", ex);
        }
    }
}
