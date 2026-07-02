package com.cv.simulator.videoosd.core.mqtt;

import com.cv.simulator.videoosd.core.osd.DeviceTelemetryRecord;
import com.cv.simulator.videoosd.core.osd.OsdFrameGeometryCalculator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class MqttOsdRecordMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OsdFrameGeometryCalculator frameGeometryCalculator;

    public MqttOsdRecordMapper(OsdFrameGeometryCalculator frameGeometryCalculator) {
        this.frameGeometryCalculator = frameGeometryCalculator;
    }

    public DeviceTelemetryRecord map(String payloadJson, double frameHfovDeg, double frameVfovDeg) {
        MqttOsdMessage message = readMessage(payloadJson);
        if (message.getData() == null) {
            throw new IllegalArgumentException("mqtt osd payload must contain data object");
        }
        DeviceTelemetryRecord record = new DeviceTelemetryRecord();
        MqttOsdDataPayload data = message.getData();
        record.setAttitudeHead(data.getAttitudeHead());
        record.setElevation(data.getElevation());
        record.setHeight(data.getHeight());
        record.setHomeDistance(data.getHomeDistance());
        record.setHorizontalSpeed(data.getHorizontalSpeed());
        record.setLatitude(data.getLatitude());
        record.setLongitude(data.getLongitude());
        record.setVerticalSpeed(data.getVerticalSpeed());
        record.setWindDirection(data.getWindDirection() == null ? null : String.valueOf(data.getWindDirection()));
        record.setWindSpeed(data.getWindSpeed());
        if (message.getTimestamp() != null) {
            record.setPublishTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(message.getTimestamp()), ZoneId.systemDefault()));
        }
        record.setPublishTimeCp1(LocalDateTime.now());
        record.setRawJson(payloadJson);
        frameGeometryCalculator.populateFrameGeometry(record, frameHfovDeg, frameVfovDeg);
        return record;
    }

    private MqttOsdMessage readMessage(String payloadJson) {
        try {
            return objectMapper.readValue(payloadJson, MqttOsdMessage.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("mqtt osd payload must be valid json object", e);
        }
    }
}
