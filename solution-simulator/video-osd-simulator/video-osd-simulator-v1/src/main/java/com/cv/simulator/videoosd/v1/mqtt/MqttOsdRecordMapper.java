package com.cv.simulator.videoosd.v1.mqtt;

import com.cv.simulator.videoosd.v1.model.MappedOsdMessage;
import com.cv.simulator.videoosd.v1.model.OsdWebSocketPayload;
import com.cv.simulator.videoosd.v1.osd.OsdFrameGeometryCalculator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
public class MqttOsdRecordMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OsdFrameGeometryCalculator frameGeometryCalculator;

    public MqttOsdRecordMapper(OsdFrameGeometryCalculator frameGeometryCalculator) {
        this.frameGeometryCalculator = frameGeometryCalculator;
    }

    public MappedOsdMessage map(String payloadJson, double frameHfovDeg, double frameVfovDeg) {
        MqttOsdMessage message = readMessage(payloadJson);
        if (message.getData() == null) {
            throw new IllegalArgumentException("mqtt osd payload must contain data object");
        }
        MqttOsdDataPayload data = message.getData();
        if (data.getLatitude() == null || data.getLongitude() == null) {
            log.error("mqtt osd payload must contain non-null latitude and longitude");
        }
        OsdWebSocketPayload payload = new OsdWebSocketPayload();
        payload.setTimestamp(message.getTimestamp());
        payload.setAttitudeHead(data.getAttitudeHead());
        payload.setLatitude(data.getLatitude());
        payload.setLongitude(data.getLongitude());
        payload.setHeight(data.getHeight());
        payload.setSpeedX(data.getSpeedX());
        payload.setSpeedY(data.getSpeedY());
        payload.setSpeedZ(data.getSpeedZ());
        payload.setGimbalPitch(data.getGimbalPitch());
        payload.setGimbalRoll(data.getGimbalRoll());
        payload.setGimbalYaw(data.getGimbalYaw());
        frameGeometryCalculator.populateFrameGeometry(payload, frameHfovDeg, frameVfovDeg);

        LocalDateTime publishTime = null;
        if (message.getTimestamp() != null) {
            publishTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(message.getTimestamp()),
                    ZoneId.systemDefault());
        }
        return new MappedOsdMessage(payload, message.getTimestamp(), publishTime);
    }

    private MqttOsdMessage readMessage(String payloadJson) {
        try {
            return objectMapper.readValue(payloadJson, MqttOsdMessage.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("mqtt osd payload must be valid json object", e);
        }
    }
}
