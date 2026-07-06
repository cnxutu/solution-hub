package com.cv.simulator.videoosd.v1.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "simulator.osd")
public class SimulatorOsdProperties {

    private boolean enabled = true;
    private String websocketPath = "/ws/osd";
    private double frameHfovDeg = 60.0D;
    private double frameVfovDeg = 40.0D;
    private int wsSenderThreads = 4;
    private long wsDropLogIntervalMillis = 5000L;
    private long wsSendSlowThresholdMillis = 1000L;
    private final WsCrypto wsCrypto = new WsCrypto();
    private final Mqtt mqtt = new Mqtt();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getWebsocketPath() {
        return websocketPath;
    }

    public void setWebsocketPath(String websocketPath) {
        this.websocketPath = websocketPath;
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

    public int getWsSenderThreads() {
        return wsSenderThreads;
    }

    public void setWsSenderThreads(int wsSenderThreads) {
        this.wsSenderThreads = wsSenderThreads;
    }

    public long getWsDropLogIntervalMillis() {
        return wsDropLogIntervalMillis;
    }

    public void setWsDropLogIntervalMillis(long wsDropLogIntervalMillis) {
        this.wsDropLogIntervalMillis = wsDropLogIntervalMillis;
    }

    public long getWsSendSlowThresholdMillis() {
        return wsSendSlowThresholdMillis;
    }

    public void setWsSendSlowThresholdMillis(long wsSendSlowThresholdMillis) {
        this.wsSendSlowThresholdMillis = wsSendSlowThresholdMillis;
    }

    public WsCrypto getWsCrypto() {
        return wsCrypto;
    }

    public Mqtt getMqtt() {
        return mqtt;
    }

    public static class WsCrypto {
        private boolean enabled = false;
        private String mode = "plain";
        private String keyBase64;
        private String keyId;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getKeyBase64() {
            return keyBase64;
        }

        public void setKeyBase64(String keyBase64) {
            this.keyBase64 = keyBase64;
        }

        public String getKeyId() {
            return keyId;
        }

        public void setKeyId(String keyId) {
            this.keyId = keyId;
        }
    }

    public static class Mqtt {
        private String brokerUrl = "tcp://127.0.0.1:1883";
        private String clientId = "video-osd-simulator-v1";
        private String topic = "thing/product/8UUXN4E00A05F5/drc/up";
        private String username;
        private String password;
        private int qos = 0;
        private boolean autoReconnect = true;
        private boolean cleanSession = true;
        private boolean directConsumeEnabled = true;

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

        public boolean isDirectConsumeEnabled() {
            return directConsumeEnabled;
        }

        public void setDirectConsumeEnabled(boolean directConsumeEnabled) {
            this.directConsumeEnabled = directConsumeEnabled;
        }
    }
}
