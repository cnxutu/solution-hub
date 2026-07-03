package com.cv.simulator.videoosd.v1.model;

import java.math.BigDecimal;

public class GeoPoint {

    private BigDecimal lat;
    private BigDecimal lon;

    public GeoPoint() {
    }

    public GeoPoint(BigDecimal lat, BigDecimal lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLon() {
        return lon;
    }

    public void setLon(BigDecimal lon) {
        this.lon = lon;
    }
}
