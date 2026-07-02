package com.cv.simulator.videoosd.core.mqtt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MqttOsdMessage {

    private MqttOsdDataPayload data;
    private String method;
    private Long seq;
    private Long timestamp;

    public MqttOsdDataPayload getData() {
        return data;
    }

    public void setData(MqttOsdDataPayload data) {
        this.data = data;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
