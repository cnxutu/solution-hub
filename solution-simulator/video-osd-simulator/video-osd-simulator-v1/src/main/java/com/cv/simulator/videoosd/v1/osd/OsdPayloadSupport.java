package com.cv.simulator.videoosd.v1.osd;

import com.cv.simulator.videoosd.v1.model.OsdWebSocketPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OsdPayloadSupport {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String toPayloadJson(OsdWebSocketPayload payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("failed to serialize osd payload", e);
        }
    }
}
