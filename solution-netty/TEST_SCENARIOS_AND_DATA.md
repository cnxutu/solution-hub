# Netty 测试场景与测试数据

这份文档是给你“真正动手跑流程”时用的。

你可以把它理解成：

- `TESTING_GUIDE.md` 更偏测试说明书
- 本文更偏测试执行单和测试数据册

建议你按本文给出的顺序执行，不要一上来就直接测 command，这样对整体链路的感知会更完整。

## 1. 推荐测试顺序总览

```mermaid
flowchart LR
    A["场景 1<br/>启动服务"] --> B["场景 2<br/>确认设备注册上线"]
    B --> C["场景 3<br/>查看在线会话和事件"]
    C --> D["场景 4<br/>单设备 telemetry 上报"]
    D --> E["场景 5<br/>批量 telemetry 上报"]
    E --> F["场景 6<br/>服务端下发 command"]
    F --> G["场景 7<br/>验证 command_reply 闭环"]
    G --> H["场景 8<br/>观察心跳保活"]
    H --> I["场景 9<br/>验证异常/离线场景"]
```

## 2. 测试基础信息

### 2.1 默认端口

- HTTP 管理接口：`8091`
- TCP 服务端端口：`19090`

### 2.2 默认模拟设备

sample 启动后，默认会自动拉起两个模拟设备：

- `mock-device-01`
- `mock-device-02`

### 2.3 主要观察接口

- 总览：`GET /netty/demo/overview`
- 会话：`GET /netty/demo/sessions`
- 事件：`GET /netty/demo/events`
- 单设备上报：`POST /netty/demo/client/telemetry`
- 批量上报：`POST /netty/demo/client/telemetry/batch`
- 服务端下发命令：`POST /netty/demo/server/command`

## 3. 场景 1：启动服务

### 目标

确认 HTTP 服务、Netty TCP 服务端、模拟设备客户端都已启动。

### 执行命令

```bash
cd solution-netty
mvn -pl netty-sample -am spring-boot:run
```

### 预期结果

- HTTP 服务监听 `8091`
- TCP 服务监听 `19090`
- 控制台日志可以看到客户端连接和注册相关日志

### 建议观察点

- 是否出现 `Device registered`
- 是否出现 `Client connected`

## 4. 场景 2：确认设备注册上线

### 目标

确认 TCP 连接已经转换成业务会话，平台已经知道“哪台设备在线”。

### 请求

```bash
curl http://localhost:8091/netty/demo/overview
```

### 预期关注点

- `deviceIds` 包含：
  - `mock-device-01`
  - `mock-device-02`
- `connectedSessions` 至少有 2 条
- `latestEvents` 中出现：
  - `REGISTER mock-device-01`
  - `REGISTER mock-device-02`

### 典型返回示例

```json
{
  "connectedSessions": [
    {
      "deviceId": "mock-device-01",
      "channelId": "a1b2c3d4",
      "remoteAddress": "/127.0.0.1:xxxxx",
      "connectedAt": 1717320000000,
      "lastSeenAt": 1717320005000
    },
    {
      "deviceId": "mock-device-02",
      "channelId": "e5f6g7h8",
      "remoteAddress": "/127.0.0.1:yyyyy",
      "connectedAt": 1717320001000,
      "lastSeenAt": 1717320006000
    }
  ],
  "deviceIds": [
    "mock-device-01",
    "mock-device-02"
  ],
  "latestEvents": [
    "REGISTER mock-device-01 /127.0.0.1:xxxxx",
    "REGISTER mock-device-02 /127.0.0.1:yyyyy"
  ]
}
```

## 5. 场景 3：查看在线会话和事件

### 目标

在做业务动作前，先学会通过会话和事件接口判断系统当前状态。

### 请求 1：查看在线会话

```bash
curl http://localhost:8091/netty/demo/sessions
```

### 请求 2：查看事件流

```bash
curl http://localhost:8091/netty/demo/events
```

### 预期结果

- `sessions` 返回在线设备会话列表
- `events` 返回最近的事件轨迹

### 你要重点感知什么

- `sessions` 更偏“当前状态”
- `events` 更偏“发生过什么”

这两个接口后面会贯穿整个测试过程。

## 6. 场景 4：单设备 telemetry 上报

### 目标

验证单台设备数据上报链路。

### 建议先测设备

- `mock-device-01`

### 测试数据 A

- `deviceId=mock-device-01`
- `temperature=26.3`
- `humidity=67.2`

### 请求

```bash
curl -X POST "http://localhost:8091/netty/demo/client/telemetry?deviceId=mock-device-01&temperature=26.3&humidity=67.2"
```

### 预期结果

- HTTP 返回：

```json
{
  "success": true,
  "deviceId": "mock-device-01"
}
```

- `GET /netty/demo/events` 中出现类似事件：

```text
TELEMETRY mock-device-01 {temperature=26.3, humidity=67.2, reportedAt=...}
```

### 补充测试数据 B

你可以再换一组数据手工感受一下：

- `deviceId=mock-device-01`
- `temperature=31.8`
- `humidity=48.5`

请求：

```bash
curl -X POST "http://localhost:8091/netty/demo/client/telemetry?deviceId=mock-device-01&temperature=31.8&humidity=48.5"
```

## 7. 场景 5：批量 telemetry 上报

### 目标

验证多设备一起上报时，服务端能否稳定接收。

### 请求

```bash
curl -X POST http://localhost:8091/netty/demo/client/telemetry/batch
```

### 预期结果

- 返回：

```json
{
  "successCount": 2
}
```

- `events` 中新增两条 `TELEMETRY`
- 一般会对应：
  - `mock-device-01`
  - `mock-device-02`

### 这一步的重点感知

这里你会更直观感受到：

- 不是只有一条连接
- 服务端可以同时维护多个设备会话
- 相同的处理逻辑可以在多设备上复用

## 8. 场景 6：服务端下发 command

### 目标

验证平台主动控制设备的能力。

### 推荐先测设备

- `mock-device-01`

### 测试数据 A：拍照命令

- `deviceId=mock-device-01`
- `command=capture-photo`
- `priority=HIGH`

### 请求

```bash
curl -X POST "http://localhost:8091/netty/demo/server/command?deviceId=mock-device-01&command=capture-photo&priority=HIGH"
```

### 预期结果

- 返回：

```json
{
  "success": true,
  "deviceId": "mock-device-01",
  "command": "capture-photo"
}
```

### 补充测试数据 B：重启命令

- `deviceId=mock-device-02`
- `command=reboot`
- `priority=NORMAL`

请求：

```bash
curl -X POST "http://localhost:8091/netty/demo/server/command?deviceId=mock-device-02&command=reboot&priority=NORMAL"
```

## 9. 场景 7：验证 command_reply 闭环

### 目标

确认“服务端下发 command -> 客户端收到 -> 客户端自动回 reply -> 服务端接收 reply”已经闭环。

### 验证方式

执行完场景 6 后调用：

```bash
curl http://localhost:8091/netty/demo/events
```

### 预期结果

事件流中能看到类似内容：

```text
COMMAND_REPLY mock-device-01 {status=ACCEPTED, command=capture-photo, processedAt=..., sourceMessageId=...}
```

或者：

```text
COMMAND_REPLY mock-device-02 {status=ACCEPTED, command=reboot, processedAt=..., sourceMessageId=...}
```

### 这一步的重点感知

这里你要把 command 链路分成两段理解：

1. 服务端通过 `deviceId -> Channel` 找到目标设备并发送 `command`
2. 客户端在 `ClientMessageHandler` 中自动构造 `command_reply` 回给服务端

## 10. 场景 8：观察心跳保活

### 目标

确认长连接不是“连上就结束”，而是在持续保活。

### 背景参数

- 当前 mock 客户端默认每 `5` 秒发一次心跳
- 服务端默认读空闲超时为 `30` 秒

### 验证方法

方法一：等待一段时间后再次看会话

```bash
curl http://localhost:8091/netty/demo/sessions
```

方法二：查看事件

```bash
curl http://localhost:8091/netty/demo/events
```

### 预期结果

- 会话没有消失
- `events` 中会逐步出现 `HEARTBEAT mock-device-xx`

### 重点感知

这一步主要是帮你建立“TCP 长连接治理”的感觉：

- 平台不仅要收数据
- 还要判断连接是不是活着

## 11. 场景 9：验证异常/离线场景

### 目标

验证不是所有请求都会成功，离线设备下发命令应该失败。

### 测试数据

- `deviceId=mock-device-99`
- `command=reboot`
- `priority=LOW`

### 请求

```bash
curl -X POST "http://localhost:8091/netty/demo/server/command?deviceId=mock-device-99&command=reboot&priority=LOW"
```

### 预期结果

```json
{
  "success": false,
  "deviceId": "mock-device-99",
  "command": "reboot"
}
```

### 这一步的意义

这一步很重要，因为真实项目里你不光要测成功流，还要测：

- 设备不在线
- 设备 ID 错误
- 连接刚断开
- 设备回复超时

## 12. 建议你第一次联调时就按这个顺序跑

如果你是第一次完整体验这套工程，推荐严格按下面顺序来：

1. 启动 sample
2. 看 `overview`
3. 看 `sessions`
4. 看 `events`
5. 触发单设备 telemetry
6. 再看 `events`
7. 触发批量 telemetry
8. 再看 `events`
9. 下发单设备 command
10. 再看 `events`
11. 等 10 到 20 秒后看心跳和会话
12. 最后测一次离线设备 command 失败

这样你对这套代码的感知会从：

- 连接建立
- 会话注册
- 数据上报
- 平台控制
- 设备应答
- 心跳保活
- 异常处理

一路完整串起来。

## 13. 对应自动化测试映射

当前 `netty-core` 里已经补了几个集成测试场景：

- `should_register_and_reply_command`
  验证注册和命令应答链路。
- `should_receive_telemetry_and_heartbeat`
  验证 telemetry 和心跳链路。
- `should_return_false_when_sending_command_to_offline_device`
  验证离线设备下发失败。

对应文件：

- [NettyTcpIntegrationTest.java](/D:/workspace/github/solution-hub/solution-netty/netty-core/src/test/java/com/cv/netty/core/NettyTcpIntegrationTest.java)

虽然你这套仓库当前 Maven 父配置会默认跳过 surefire 测试，但测试代码本身已经准备好了，后面你如果调整父配置，就可以直接拿来跑。
