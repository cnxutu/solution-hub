package com.cv.simulator.videoosd.core.osd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.temporal.TemporalAccessor;

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
        put(root, "elevation", record.getElevation());
        put(root, "battery", parseJsonLike(record.getBattery()));
        put(root, "firmware_version", record.getFirmwareVersion());
        put(root, "gear", record.getGear());
        put(root, "height", record.getHeight());
        put(root, "home_distance", record.getHomeDistance());
        put(root, "horizontal_speed", record.getHorizontalSpeed());
        put(root, "latitude", record.getLatitude());
        put(root, "longitude", record.getLongitude());
        put(root, "mode_code", record.getModeCode());
        put(root, "action_type", record.getActionType());
        put(root, "total_flight_distance", record.getTotalFlightDistance());
        put(root, "total_flight_time", record.getTotalFlightTime());
        put(root, "vertical_speed", record.getVerticalSpeed());
        put(root, "wind_direction", record.getWindDirection());
        put(root, "wind_speed", record.getWindSpeed());
        put(root, "position_state", parseJsonLike(record.getPositionState()));
        put(root, "payloads", parseJsonLike(record.getPayloads()));
        put(root, "storage", parseJsonLike(record.getStorage()));
        put(root, "night_lights_state", record.getNightLightsState());
        put(root, "height_limit", record.getHeightLimit());
        put(root, "distance_limit_status", parseJsonLike(record.getDistanceLimitStatus()));
        put(root, "obstacle_avoidance", parseJsonLike(record.getObstacleAvoidance()));
        put(root, "activation_time", record.getActivationTime());
        put(root, "cameras", parseJsonLike(record.getCameras()));
        put(root, "rc_lost_action", record.getRcLostAction());
        put(root, "rth_altitude", record.getRthAltitude());
        put(root, "total_flight_sorties", record.getTotalFlightSorties());
        put(root, "exit_wayline_when_rc_lost", record.getExitWaylineWhenRcLost());
        put(root, "country", record.getCountry());
        put(root, "rid_state", record.getRidState());
        put(root, "is_near_area_limit", record.getNearAreaLimit());
        put(root, "is_near_height_limit", record.getNearHeightLimit());
        put(root, "maintain_status", parseJsonLike(record.getMaintainStatus()));
        put(root, "track_id", record.getTrackId());
        put(root, "publish_time", record.getPublishTime());
        put(root, "frame_center", record.getFrameCenter());
        put(root, "corners", record.getCorners());
        put(root, "create_time", record.getCreateTime());
        put(root, "created_by", record.getCreatedBy());
        put(root, "update_time", record.getUpdateTime());
        put(root, "updated_by", record.getUpdatedBy());
        put(root, "is_deleted", record.getIsDeleted());
        try {
            return objectMapper.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("failed to serialize osd payload", e);
        }
    }

    private void put(ObjectNode node, String name, Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof TemporalAccessor temporalAccessor) {
            node.put(name, temporalAccessor.toString());
            return;
        }
        node.set(name, objectMapper.valueToTree(value));
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private JsonNode parseJsonLike(String value) {
        if (!hasText(value)) {
            return null;
        }
        try {
            return objectMapper.readTree(value);
        } catch (JsonProcessingException e) {
            return objectMapper.valueToTree(value);
        }
    }
}
