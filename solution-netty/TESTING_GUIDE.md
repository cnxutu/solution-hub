# Netty TCP 联调测试文档

## 1. 测试目标

验证以下能力是否工作正常：

- TCP 服务端正常启动
- 模拟设备客户端可以接入
- 注册报文可以建立设备会话
- 心跳可以维持连接
- telemetry 可以正常上报
- 服务端可以向设备下发指令
- 设备可以回传指令应答
- 断开后客户端可以自动重连

## 2. 环境准备

- JDK 8 及以上
- Maven 3.6+
- 本地可用端口：
  - HTTP：`8091`
  - TCP：`19090`

## 3. 先跑自动化测试

在仓库根目录执行：

```bash
mvn -pl solution-netty/netty-core test
```

预期结果：

- `NettyTcpIntegrationTest` 通过
- 说明最基础的“服务端启动 -> 客户端注册 -> 平台下发指令 -> 客户端应答”链路是通的

## 4. 启动 sample

```bash
cd solution-netty
mvn -pl netty-sample -am spring-boot:run
```

预期结果：

- 启动一个 Spring Boot HTTP 服务，端口 `8091`
- 启动一个 Netty TCP 服务端，端口 `19090`
- 自动启动 2 个模拟设备：
  - `mock-device-01`
  - `mock-device-02`

## 5. 查看设备是否在线

```bash
curl http://localhost:8091/netty/demo/overview
```

预期关注点：

- `connectedSessions` 中能看到 2 个设备
- `latestEvents` 中能看到 `REGISTER`

## 6. 手工触发 telemetry 上报

```bash
curl -X POST "http://localhost:8091/netty/demo/client/telemetry?deviceId=mock-device-01&temperature=26.3&humidity=67.2"
```

预期结果：

- 返回 `success=true`
- 日志中出现 `Telemetry received`
- `GET /netty/demo/events` 可以看到 `TELEMETRY mock-device-01`

## 7. 批量触发 telemetry

```bash
curl -X POST http://localhost:8091/netty/demo/client/telemetry/batch
```

预期结果：

- 返回 `successCount=2`
- 两个模拟设备都能上报 telemetry

## 8. 服务端向设备下发指令

```bash
curl -X POST "http://localhost:8091/netty/demo/server/command?deviceId=mock-device-01&command=capture-photo&priority=HIGH"
```

预期结果：

- HTTP 返回 `success=true`
- 设备客户端收到 `command`
- 客户端自动回传 `command_reply`
- 服务端日志出现 `Command reply received`
- `GET /netty/demo/events` 可以看到 `COMMAND_REPLY mock-device-01`

## 9. 验证心跳

默认每 `5` 秒发送一次心跳。

验证方式：

- 观察日志中是否持续出现 heartbeat 相关记录
- 或者等待一段时间后查看连接是否仍存在

```bash
curl http://localhost:8091/netty/demo/sessions
```

如果连接仍在，说明心跳和空闲控制工作正常。

## 10. 验证断线重连

### 方法一：停掉 sample，再重启

1. 停止应用
2. 重新执行启动命令
3. 观察设备重新注册

### 方法二：代码层思路

当前客户端在连接断开后会按照 `reconnect-seconds` 自动重连。真实项目中你可以继续补：

- 最大重试次数
- 指数退避
- 注册失败重试策略
- 连接鉴权失败熔断

## 11. 真实设备接入时建议增加的测试项

- 粘包/拆包测试
- 非法报文测试
- 超长报文测试
- 半包测试
- 设备重复登录测试
- 设备身份伪造测试
- 大量并发连接测试
- 网络抖动测试
- 心跳丢失测试
- 命令超时重试测试

## 12. 联调落地建议

如果你后面要对接真实设备，建议按这个顺序推进：

1. 先和设备侧确认协议文档。
2. 确认帧格式：定长、分隔符、长度字段还是二进制头。
3. 明确注册、心跳、上报、应答报文样例。
4. 先用模拟客户端把平台服务端跑通。
5. 再替换成真实设备报文编解码。
6. 最后补压测、异常流和在线运维能力。
