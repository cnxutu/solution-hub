package com.cv.rtsp.sample;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "rtsp.sample")
public class RtspSampleProperties {

    private RtspProviderType provider = RtspProviderType.MOCK;
    private boolean autoStart = true;
    private int keepAliveSeconds = 15;
    private long framePullIntervalMillis = 800L;
    private String rtspHost = "127.0.0.1";
    private int rtspPort = 8554;
    private final EmbeddedServer embeddedServer = new EmbeddedServer();
    private final Ffmpeg ffmpeg = new Ffmpeg();

    public RtspProviderType getProvider() {
        return provider;
    }

    public void setProvider(RtspProviderType provider) {
        this.provider = provider;
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    public int getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    public void setKeepAliveSeconds(int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }

    public long getFramePullIntervalMillis() {
        return framePullIntervalMillis;
    }

    public void setFramePullIntervalMillis(long framePullIntervalMillis) {
        this.framePullIntervalMillis = framePullIntervalMillis;
    }

    public String getRtspHost() {
        return rtspHost;
    }

    public void setRtspHost(String rtspHost) {
        this.rtspHost = rtspHost;
    }

    public int getRtspPort() {
        return rtspPort;
    }

    public void setRtspPort(int rtspPort) {
        this.rtspPort = rtspPort;
    }

    public EmbeddedServer getEmbeddedServer() {
        return embeddedServer;
    }

    public Ffmpeg getFfmpeg() {
        return ffmpeg;
    }

    public static class EmbeddedServer {

        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class Ffmpeg {

        private String binary = "ffmpeg";
        private String transport = "tcp";
        private int logTailSize = 50;
        private List<String> extraArgs = new ArrayList<String>();

        public String getBinary() {
            return binary;
        }

        public void setBinary(String binary) {
            this.binary = binary;
        }

        public String getTransport() {
            return transport;
        }

        public void setTransport(String transport) {
            this.transport = transport;
        }

        public int getLogTailSize() {
            return logTailSize;
        }

        public void setLogTailSize(int logTailSize) {
            this.logTailSize = logTailSize;
        }

        public List<String> getExtraArgs() {
            return extraArgs;
        }

        public void setExtraArgs(List<String> extraArgs) {
            this.extraArgs = extraArgs;
        }
    }
}
