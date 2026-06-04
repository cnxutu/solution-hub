package com.cv.rtsp.ffmpeg;

import com.cv.rtsp.core.exception.RtspClientException;
import com.cv.rtsp.core.gateway.RtspCameraGateway;
import com.cv.rtsp.core.model.RtspDescribeResult;
import com.cv.rtsp.core.model.RtspFrame;
import com.cv.rtsp.core.model.RtspMediaTrack;
import com.cv.rtsp.core.model.RtspPullSession;
import com.cv.rtsp.core.model.RtspSessionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class FfmpegRtspCameraGateway implements RtspCameraGateway {

    private static final Logger log = LoggerFactory.getLogger(FfmpegRtspCameraGateway.class);

    private final FfmpegRtspGatewayConfig config;
    private final Map<String, ProcessRuntime> runtimes = new ConcurrentHashMap<String, ProcessRuntime>();

    public FfmpegRtspCameraGateway(FfmpegRtspGatewayConfig config) {
        this.config = config;
    }

    @Override
    public void options(RtspPullSession session) {
        validateConfig();
        session.getMetrics().incrementOptions();
        session.setState(RtspSessionState.OPTIONS_NEGOTIATED);
    }

    @Override
    public RtspDescribeResult describe(RtspPullSession session) {
        RtspDescribeResult result = new RtspDescribeResult(
                "generated-by-ffmpeg-bridge",
                Collections.singletonList(new RtspMediaTrack("video", "H264", "ffmpeg", 90000))
        );
        session.getMetrics().incrementDescribe();
        session.setDescribeResult(result);
        session.setState(RtspSessionState.DESCRIBED);
        return result;
    }

    @Override
    public String setup(RtspPullSession session) {
        String remoteSessionId = "ffmpeg-" + UUID.randomUUID().toString().substring(0, 8);
        session.setRemoteSessionId(remoteSessionId);
        session.getMetrics().incrementSetup();
        session.setState(RtspSessionState.SETUP);
        return remoteSessionId;
    }

    @Override
    public void play(RtspPullSession session) {
        ProcessRuntime runtime = startProcess(session);
        runtimes.put(session.getSessionId(), runtime);
        session.getMetrics().incrementPlay();
        session.setState(RtspSessionState.PLAYING);
    }

    @Override
    public void keepAlive(RtspPullSession session) {
        ProcessRuntime runtime = requireRuntime(session);
        if (!runtime.process.isAlive()) {
            throw new RtspClientException("ffmpeg process exited with code " + runtime.process.exitValue());
        }
        session.getMetrics().incrementKeepAlive();
        session.markKeepAlive();
    }

    @Override
    public RtspFrame readFrame(RtspPullSession session) {
        ProcessRuntime runtime = requireRuntime(session);
        long sequence = runtime.sequence.incrementAndGet();
        String payloadPreview = runtime.latestLogLine();
        if (payloadPreview == null || payloadPreview.trim().isEmpty()) {
            payloadPreview = "ffmpeg-waiting-for-output";
        }
        return new RtspFrame(session.getEndpoint().getStreamId(), sequence, System.currentTimeMillis(),
                "H264", sequence % 25 == 1, payloadPreview);
    }

    @Override
    public void teardown(RtspPullSession session) {
        ProcessRuntime runtime = runtimes.remove(session.getSessionId());
        if (runtime != null) {
            runtime.process.destroy();
        }
        session.getMetrics().incrementTeardown();
        session.setState(RtspSessionState.STOPPED);
    }

    private ProcessRuntime startProcess(RtspPullSession session) {
        List<String> command = new ArrayList<String>();
        command.add(config.getFfmpegBinary());
        command.add("-nostdin");
        command.add("-hide_banner");
        command.add("-loglevel");
        command.add("info");
        command.add("-rtsp_transport");
        command.add(config.getRtspTransport());
        command.add("-i");
        command.add(session.getEndpoint().getRtspUrl());
        command.addAll(config.getExtraArgs());
        command.add("-f");
        command.add("null");
        command.add("-");
        try {
            Process process = new ProcessBuilder(command).start();
            ProcessRuntime runtime = new ProcessRuntime(process, config.getLogTailSize());
            runtime.startLogCollector();
            log.info("Started ffmpeg bridge process for stream {}", session.getEndpoint().getStreamId());
            return runtime;
        } catch (IOException exception) {
            throw new RtspClientException("Unable to start ffmpeg process. command=" + command, exception);
        }
    }

    private void validateConfig() {
        if (config.getFfmpegBinary() == null || config.getFfmpegBinary().trim().isEmpty()) {
            throw new RtspClientException("ffmpeg binary is not configured");
        }
    }

    private ProcessRuntime requireRuntime(RtspPullSession session) {
        ProcessRuntime runtime = runtimes.get(session.getSessionId());
        if (runtime == null) {
            throw new RtspClientException("ffmpeg process not started for session: " + session.getSessionId());
        }
        return runtime;
    }

    private static class ProcessRuntime {

        private final Process process;
        private final int logTailSize;
        private final AtomicLong sequence = new AtomicLong(0L);
        private final Deque<String> logLines = new LinkedList<String>();

        private ProcessRuntime(Process process, int logTailSize) {
            this.process = process;
            this.logTailSize = logTailSize;
        }

        private void startLogCollector() {
            Thread stderrThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    readLines(process.getErrorStream());
                }
            }, "ffmpeg-stderr");
            stderrThread.setDaemon(true);
            stderrThread.start();
            Thread stdoutThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    readLines(process.getInputStream());
                }
            }, "ffmpeg-stdout");
            stdoutThread.setDaemon(true);
            stdoutThread.start();
        }

        private void readLines(java.io.InputStream inputStream) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    synchronized (logLines) {
                        if (logLines.size() >= logTailSize) {
                            logLines.removeFirst();
                        }
                        logLines.addLast(line);
                    }
                }
            } catch (IOException ignore) {
            }
        }

        private String latestLogLine() {
            synchronized (logLines) {
                return logLines.peekLast();
            }
        }
    }
}
