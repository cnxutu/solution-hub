package com.cv.simulator.videoosd.sample.service;

import com.cv.simulator.videoosd.sample.pojo.entity.DeviceTelemetryEntity;

import java.util.List;

public final class OsdReplayLogSupport {

    public static final String PREFIX = "OSD_TRACE";

    private OsdReplayLogSupport() {
    }

    public static String summarizeRows(List<DeviceTelemetryEntity> rows) {
        if (rows == null || rows.isEmpty()) {
            return "rowCount=0";
        }
        DeviceTelemetryEntity first = rows.get(0);
        DeviceTelemetryEntity last = rows.get(rows.size() - 1);
        long publishTimeReadyCount = rows.stream().filter(row -> row.getPublishTime() != null).count();
        return "rowCount=" + rows.size()
                + ", publishTimeReadyCount=" + publishTimeReadyCount
                + ", firstId=" + first.getId()
                + ", firstPublishTime=" + first.getPublishTime()
                + ", lastId=" + last.getId()
                + ", lastPublishTime=" + last.getPublishTime();
    }

    public static String payloadPreview(String payload, int maxLength) {
        if (payload == null) {
            return "null";
        }
        if (payload.length() <= maxLength) {
            return payload;
        }
        return payload.substring(0, maxLength) + "...(" + payload.length() + " chars)";
    }

    public static String marker(String stage) {
        return PREFIX + " [" + stage + "]";
    }
}
