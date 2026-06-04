package com.cv.rtsp.sample.controller;

import com.cv.rtsp.core.manager.RtspStreamManager;
import com.cv.rtsp.core.model.RtspEndpoint;
import com.cv.rtsp.sample.RtspSampleProperties;
import com.cv.rtsp.sample.service.SampleCameraCatalog;
import com.cv.rtsp.sample.service.SampleRtspObserver;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/rtsp/demo")
public class RtspDemoController {

    private final SampleCameraCatalog cameraCatalog;
    private final SampleRtspObserver observer;
    private final RtspStreamManager manager;
    private final RtspSampleProperties properties;

    public RtspDemoController(SampleCameraCatalog cameraCatalog,
                              SampleRtspObserver observer,
                              RtspStreamManager manager,
                              RtspSampleProperties properties) {
        this.cameraCatalog = cameraCatalog;
        this.observer = observer;
        this.manager = manager;
        this.properties = properties;
    }

    @GetMapping("/overview")
    public Map<String, Object> overview() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("provider", properties.getProvider());
        result.put("embeddedServerEnabled", properties.getEmbeddedServer().isEnabled());
        result.put("rtspHost", properties.getRtspHost());
        result.put("rtspPort", properties.getRtspPort());
        result.put("registeredCameras", cameraCatalog.describeCatalog());
        result.put("activeSessions", manager.listSessions());
        result.put("latestEvents", observer.latestEvents());
        return result;
    }

    @GetMapping("/cameras")
    public Collection<Map<String, Object>> cameras() {
        return cameraCatalog.describeCatalog();
    }

    @GetMapping("/sessions")
    public Collection<?> sessions() {
        return manager.listSessions();
    }

    @GetMapping("/events")
    public Collection<String> events() {
        return observer.latestEvents();
    }

    @GetMapping("/frames")
    public Collection<Map<String, Object>> frames(@RequestParam String streamId) {
        return observer.latestFrames(streamId);
    }

    @PostMapping("/start")
    public Map<String, Object> start(@RequestParam String streamId) {
        RtspEndpoint endpoint = cameraCatalog.buildEndpoint(streamId);
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        if (endpoint == null) {
            result.put("success", false);
            result.put("message", "unknown streamId");
            return result;
        }
        result.put("success", true);
        result.put("session", manager.start(endpoint, observer));
        return result;
    }

    @PostMapping("/stop")
    public Map<String, Object> stop(@RequestParam String streamId) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("success", manager.stop(streamId));
        result.put("streamId", streamId);
        return result;
    }
}
