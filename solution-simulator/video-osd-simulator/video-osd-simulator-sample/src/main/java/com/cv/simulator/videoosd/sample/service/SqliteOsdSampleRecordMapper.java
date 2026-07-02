package com.cv.simulator.videoosd.sample.service;

import com.cv.simulator.videoosd.core.osd.DeviceTelemetryRecord;
import com.cv.simulator.videoosd.core.osd.OsdFrameGeometryCalculator;
import com.cv.simulator.videoosd.sample.pojo.sqlite.SqliteOsdSampleRow;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class SqliteOsdSampleRecordMapper {

    private final OsdFrameGeometryCalculator frameGeometryCalculator;

    public SqliteOsdSampleRecordMapper(OsdFrameGeometryCalculator frameGeometryCalculator) {
        this.frameGeometryCalculator = frameGeometryCalculator;
    }

    public DeviceTelemetryRecord map(SqliteOsdSampleRow row, double frameHfovDeg, double frameVfovDeg) {
        DeviceTelemetryRecord record = new DeviceTelemetryRecord();
        record.setId(row.getId());
        record.setDeviceSn(row.getDroneSn());
        record.setAttitudeHead(toFloat(row.getAttitudeHead()));
        record.setAttitudePitch(row.getGimbalPitch());
        record.setAttitudeRoll(row.getGimbalRoll());
        record.setHeight(toFloat(row.getHeight()));
        record.setVerticalSpeed(toFloat(row.getSpeedZ()));
        record.setLatitude(toBigDecimal(row.getLatitude()));
        record.setLongitude(toBigDecimal(row.getLongitude()));
        record.setPublishTime(toLocalDateTime(row.getMessageTimestampMs()));
        record.setRawJson(row.getRawJson());
        frameGeometryCalculator.populateFrameGeometry(record, frameHfovDeg, frameVfovDeg);
        return record;
    }

    private LocalDateTime toLocalDateTime(Long epochMillis) {
        if (epochMillis == null) {
            return null;
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault());
    }

    private BigDecimal toBigDecimal(Double value) {
        return value == null ? null : BigDecimal.valueOf(value);
    }

    private Float toFloat(Double value) {
        return value == null ? null : value.floatValue();
    }
}
