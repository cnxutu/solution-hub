package com.cv.netty.core.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class TransportMessage {

    private String messageId;
    private String type;
    private String deviceId;
    private Long timestamp;
    private Map<String, Object> payload;

    public static TransportMessage of(String type, String deviceId, Map<String, Object> payload) {
        TransportMessage message = new TransportMessage();
        message.setMessageId(UUID.randomUUID().toString().replace("-", ""));
        message.setType(type);
        message.setDeviceId(deviceId);
        message.setTimestamp(System.currentTimeMillis());
        message.setPayload(payload == null ? new LinkedHashMap<String, Object>() : new LinkedHashMap<String, Object>(payload));
        return message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
}
