# netty-core 模块设计说明

## 1. 模块目标

`netty-core` 解决的是一个典型 TCP 设备接入链路中的基础能力：

- 平台作为 TCP 服务端接收设备连接
- 平台也可以作为 TCP 客户端主动连接其他服务
- 维护设备会话
- 处理注册、心跳、数据上报、指令应答
- 提供断线重连和空闲检测

## 2. 核心类说明

### 服务端

- `NettyTcpServer`
  负责启动、关闭服务端，并对外暴露发指令和查询会话能力。
- `ServerChannelInitializer`
  负责组装 ChannelPipeline。
- `ServerMessageHandler`
  负责处理注册、心跳、telemetry、command reply。
- `DeviceSessionManager`
  负责维护 `deviceId -> Channel`、`deviceId -> Session` 映射。

### 客户端

- `NettyTcpClient`
  负责主动建立连接、断线重连、发送心跳、发送 telemetry。
- `ClientChannelInitializer`
  负责组装客户端 Pipeline。
- `ClientMessageHandler`
  负责接收平台消息，并在收到 `command` 时自动回传 `command_reply`。

### 编解码

- `TransportMessage`
  统一消息模型。
- `TransportMessageCodec`
  基于 Jackson 做 JSON 编解码。

## 3. 默认消息类型

- `register`
- `heartbeat`
- `telemetry`
- `command`
- `command_reply`
- `ack`

## 4. 默认连接流程

```text
device connect
  -> register
  -> server ack
  -> heartbeat / telemetry
  -> server command
  -> device command_reply
```

## 5. 为什么这里先用文本协议

因为你当前最需要的是把下面这几件事彻底吃透：

- 服务端怎么收连接
- 客户端怎么主动连
- Handler 怎么处理消息
- 心跳和断线重连怎么做
- 会话如何维护
- 指令如何下发

这些问题先独立于复杂二进制协议来学习，效率会高很多。等这些能力熟了，再切二进制协议会更稳。
