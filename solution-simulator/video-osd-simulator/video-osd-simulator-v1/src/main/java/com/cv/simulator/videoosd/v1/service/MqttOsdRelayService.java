package com.cv.simulator.videoosd.v1.service;

import com.cv.simulator.videoosd.v1.config.SimulatorOsdProperties;
import com.cv.simulator.videoosd.v1.model.MappedOsdMessage;
import com.cv.simulator.videoosd.v1.mqtt.MqttOsdRecordMapper;
import com.cv.simulator.videoosd.v1.osd.OsdPayloadSupport;
import com.cv.simulator.videoosd.v1.websocket.OsdBroadcastWebSocketHandler;
import com.cv.simulator.videoosd.v1.websocket.WsPayloadEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MqttOsdRelayService {

    private static final Logger log = LoggerFactory.getLogger(MqttOsdRelayService.class);
    private static final long MQTT_WS_WARN_THRESHOLD_MILLIS = 1_000L;
    private static final long MQTT_WS_COST_SUMMARY_WINDOW_MILLIS = 5_000L;

    private final SimulatorOsdProperties properties;
    private final MqttOsdRecordMapper mqttOsdRecordMapper;
    private final OsdPayloadSupport payloadSupport;
    private final WsPayloadEncoder wsPayloadEncoder;
    private final OsdBroadcastWebSocketHandler osdBroadcastWebSocketHandler;
    private final MqttCostSummaryTracker mqttCostSummaryTracker;

    public MqttOsdRelayService(SimulatorOsdProperties properties,
                               MqttOsdRecordMapper mqttOsdRecordMapper,
                               OsdPayloadSupport payloadSupport,
                               WsPayloadEncoder wsPayloadEncoder,
                               OsdBroadcastWebSocketHandler osdBroadcastWebSocketHandler) {
        this.properties = properties;
        this.mqttOsdRecordMapper = mqttOsdRecordMapper;
        this.payloadSupport = payloadSupport;
        this.wsPayloadEncoder = wsPayloadEncoder;
        this.osdBroadcastWebSocketHandler = osdBroadcastWebSocketHandler;
        this.mqttCostSummaryTracker = new MqttCostSummaryTracker(
                MQTT_WS_COST_SUMMARY_WINDOW_MILLIS,
                System::currentTimeMillis);
    }

    public void handleInboundMessage(String topic, String payload) {
        long startedAtNanos = System.nanoTime();
        try {
            MappedOsdMessage mappedMessage = mqttOsdRecordMapper.map(
                    payload,
                    properties.getFrameHfovDeg(),
                    properties.getFrameVfovDeg());
            String payloadJson = wsPayloadEncoder.encode(payloadSupport.toPayloadJson(mappedMessage.getPayload()));
            osdBroadcastWebSocketHandler.broadcast(payloadJson);
            logCost(mappedMessage, startedAtNanos);
        } catch (IllegalArgumentException e) {
            log.warn("MQTT_TRACE [{}] topic={}, reason={}, payloadPreview={}",
                    droppedEventName(e), topic, e.getMessage(), payloadPreview(payload, 300));
        } catch (RuntimeException e) {
            log.error("MQTT_TRACE [MESSAGE_DROPPED] topic={}, reason={}, payloadPreview={}",
                    topic, e.getMessage(), payloadPreview(payload, 300), e);
        }
    }

    private void logCost(MappedOsdMessage mappedMessage, long startedAtNanos) {
        long durationMillis = (System.nanoTime() - startedAtNanos) / 1_000_000L;
        mqttCostSummaryTracker.record(durationMillis, durationMillis > MQTT_WS_WARN_THRESHOLD_MILLIS);
        Optional<MqttCostSummaryTracker.CostSummary> summaryOptional = mqttCostSummaryTracker.drainReadySummary();
        if (!summaryOptional.isPresent()) {
            return;
        }
        MqttCostSummaryTracker.CostSummary summary = summaryOptional.get();
        log.info("MQTT_TRACE [MQTT_TO_WS_COST_SUMMARY] windowStartMillis={}, windowEndMillis={}, count={}, avgDurationMillis={}, maxDurationMillis={}, overThresholdCount={}, thresholdMillis={}",
                summary.getWindowStartMillis(),
                summary.getWindowEndMillis(),
                summary.getCount(),
                summary.getAvgDurationMillis(),
                summary.getMaxDurationMillis(),
                summary.getOverThresholdCount(),
                MQTT_WS_WARN_THRESHOLD_MILLIS);
    }

    private String payloadPreview(String payload, int maxLength) {
        if (payload == null) {
            return "null";
        }
        if (payload.length() <= maxLength) {
            return payload;
        }
        return payload.substring(0, maxLength) + "...(" + payload.length() + " chars)";
    }

    private String droppedEventName(IllegalArgumentException e) {
        String message = e.getMessage();
        if (message == null) {
            return "MESSAGE_DROPPED";
        }
        if (message.contains("valid json object")) {
            return "MESSAGE_SKIPPED_BAD_JSON";
        }
        if (message.contains("non-null latitude and longitude")) {
            return "MESSAGE_SKIPPED_HEARTBEAT";
        }
        return "MESSAGE_DROPPED";
    }
}
