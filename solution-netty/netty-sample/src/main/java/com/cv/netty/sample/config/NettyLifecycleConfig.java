package com.cv.netty.sample.config;

import com.cv.netty.core.server.NettyTcpServer;
import com.cv.netty.sample.service.MockDeviceFleetService;
import com.cv.netty.sample.service.SampleServerMessageListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NettyLifecycleConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public NettyTcpServer nettyTcpServer(@Value("${netty.sample.tcp.port:19090}") int tcpPort,
                                         @Value("${netty.sample.server.boss-threads:1}") int bossThreads,
                                         @Value("${netty.sample.server.worker-threads:2}") int workerThreads,
                                         @Value("${netty.sample.server.read-idle-seconds:30}") int readIdleSeconds,
                                         SampleServerMessageListener listener) {
        return new NettyTcpServer(tcpPort, bossThreads, workerThreads, readIdleSeconds, listener);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public MockDeviceFleetService mockDeviceFleetService(@Value("${netty.sample.mock.enabled:true}") boolean enabled,
                                                         @Value("${netty.sample.mock.count:2}") int count,
                                                         @Value("${netty.sample.mock.host:127.0.0.1}") String host,
                                                         @Value("${netty.sample.tcp.port:19090}") int tcpPort,
                                                         @Value("${netty.sample.mock.heartbeat-seconds:5}") int heartbeatSeconds,
                                                         @Value("${netty.sample.mock.reconnect-seconds:3}") int reconnectSeconds) {
        return new MockDeviceFleetService(enabled, count, host, tcpPort, heartbeatSeconds, reconnectSeconds);
    }
}
