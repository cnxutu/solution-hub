package com.cv.simulator.videoosd.v1.mqtt;

public class MqttOsdProperties {

    private String brokerUrl = "tcp://127.0.0.1:1883";
    private String clientId = "video-osd-simulator-v1";
    private String topic = "thing/product/8UUXN4E00A05F5/drc/up";
    private String username;
    private String password;
    private int qos = 0;
    private boolean autoReconnect = true;
    private boolean cleanSession = true;
    private double frameHfovDeg = 60.0D;
    private double frameVfovDeg = 40.0D;

    public String getBrokerUrl() {
        return brokerUrl;
    }

    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    public boolean isCleanSession() {
        return cleanSession;
    }

    public void setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    public double getFrameHfovDeg() {
        return frameHfovDeg;
    }

    public void setFrameHfovDeg(double frameHfovDeg) {
        this.frameHfovDeg = frameHfovDeg;
    }

    public double getFrameVfovDeg() {
        return frameVfovDeg;
    }

    public void setFrameVfovDeg(double frameVfovDeg) {
        this.frameVfovDeg = frameVfovDeg;
    }
}
