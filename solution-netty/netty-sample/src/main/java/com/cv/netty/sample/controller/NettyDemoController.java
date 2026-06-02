package com.cv.netty.sample.controller;

import com.cv.netty.core.server.NettyTcpServer;
import com.cv.netty.sample.service.MockDeviceFleetService;
import com.cv.netty.sample.service.SampleServerMessageListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/netty/demo")
public class NettyDemoController {

    private final NettyTcpServer nettyTcpServer;
    private final MockDeviceFleetService mockDeviceFleetService;
    private final SampleServerMessageListener sampleServerMessageListener;

    public NettyDemoController(NettyTcpServer nettyTcpServer,
                               MockDeviceFleetService mockDeviceFleetService,
                               SampleServerMessageListener sampleServerMessageListener) {
        this.nettyTcpServer = nettyTcpServer;
        this.mockDeviceFleetService = mockDeviceFleetService;
        this.sampleServerMessageListener = sampleServerMessageListener;
    }

    @GetMapping("/overview")
    public Map<String, Object> overview() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        Collection<?> sessions = nettyTcpServer.getConnectedSessions();
        result.put("connectedSessions", sessions);
        result.put("deviceIds", mockDeviceFleetService.listDeviceIds());
        result.put("latestEvents", sampleServerMessageListener.getLatestEvents());
        return result;
    }

    @GetMapping("/sessions")
    public Collection<?> sessions() {
        return nettyTcpServer.getConnectedSessions();
    }

    @PostMapping("/server/command")
    public Map<String, Object> sendCommand(@RequestParam String deviceId,
                                           @RequestParam String command,
                                           @RequestParam(defaultValue = "NORMAL") String priority) {
        Map<String, Object> payload = new LinkedHashMap<String, Object>();
        payload.put("priority", priority);
        boolean success = nettyTcpServer.sendCommandToDevice(deviceId, command, payload);
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("success", success);
        result.put("deviceId", deviceId);
        result.put("command", command);
        return result;
    }

    @PostMapping("/client/telemetry")
    public Map<String, Object> sendTelemetry(@RequestParam String deviceId,
                                             @RequestParam double temperature,
                                             @RequestParam double humidity) {
        boolean success = mockDeviceFleetService.sendTelemetry(deviceId, temperature, humidity);
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("success", success);
        result.put("deviceId", deviceId);
        return result;
    }

    @PostMapping("/client/telemetry/batch")
    public Map<String, Object> batchTelemetry() {
        int success = mockDeviceFleetService.broadcastTelemetry();
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("successCount", success);
        return result;
    }

    @GetMapping("/events")
    public Collection<String> events() {
        return sampleServerMessageListener.getLatestEvents();
    }
}
