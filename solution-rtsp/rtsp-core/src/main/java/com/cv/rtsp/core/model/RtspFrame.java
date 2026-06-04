package com.cv.rtsp.core.model;

public class RtspFrame {

    private final String streamId;
    private final long sequence;
    private final long timestamp;
    private final String codec;
    private final boolean keyFrame;
    private final String payloadPreview;

    public RtspFrame(String streamId, long sequence, long timestamp, String codec, boolean keyFrame, String payloadPreview) {
        this.streamId = streamId;
        this.sequence = sequence;
        this.timestamp = timestamp;
        this.codec = codec;
        this.keyFrame = keyFrame;
        this.payloadPreview = payloadPreview;
    }

    public String getStreamId() {
        return streamId;
    }

    public long getSequence() {
        return sequence;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getCodec() {
        return codec;
    }

    public boolean isKeyFrame() {
        return keyFrame;
    }

    public String getPayloadPreview() {
        return payloadPreview;
    }
}
