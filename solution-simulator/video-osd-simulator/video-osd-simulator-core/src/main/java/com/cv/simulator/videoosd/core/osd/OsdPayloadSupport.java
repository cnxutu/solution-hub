package com.cv.simulator.videoosd.core.osd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class OsdPayloadSupport {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String toPayloadJson(DeviceTelemetryRecord record) {
        ObjectNode root = objectMapper.createObjectNode();
        if (hasText(record.getRawJson())) {
            try {
                JsonNode raw = objectMapper.readTree(record.getRawJson());
                if (!raw.isObject()) {
                    throw new IllegalArgumentException("raw_json must be a valid JSON object");
                }
                root.setAll((ObjectNode) raw);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("raw_json must be a valid JSON object", e);
            }
        }
        put(root, "task_id", record.getTaskId());
        put(root, "device_sn", record.getDeviceSn());
        put(root, "attitude_head", record.getAttitudeHead());
        put(root, "attitude_pitch", record.getAttitudePitch());
        put(root, "attitude_roll", record.getAttitudeRoll());
        put(root, "latitude", record.getLatitude());
        put(root, "longitude", record.getLongitude());
        put(root, "mode_code", record.getModeCode());
        put(root, "track_id", record.getTrackId());
        try {
            return objectMapper.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("failed to serialize osd payload", e);
        }
    }

    private void put(ObjectNode node, String name, Object value) {
        if (value != null) {
            node.set(name, objectMapper.valueToTree(value));
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
