package com.cv.simulator.videoosd.sample.pojo.sqlite;

import lombok.Data;

@Data
public class SqliteOsdSampleRow {

    private Long id;
    private Long receivedAtMs;
    private Long messageTimestampMs;
    private String dockSn;
    private String droneSn;
    private Double attitudeHead;
    private Double latitude;
    private Double longitude;
    private Double height;
    private Double speedX;
    private Double speedY;
    private Double speedZ;
    private Double gimbalPitch;
    private Double gimbalRoll;
    private Double gimbalYaw;
    private String rawJson;
}
