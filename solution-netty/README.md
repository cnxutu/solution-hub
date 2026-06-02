# solution-netty

`solution-netty` 是一个面向 TCP 设备接入场景的 Netty 示例模块，目标不是只演示 API，而是给你一套可以直接落地练手的最小可用方案。

当前包含：

- `netty-core`：Netty TCP 服务端、客户端、会话管理、心跳、断线重连、消息编解码。
- `netty-sample`：Spring Boot 示例工程，启动后会同时起 TCP 服务端和模拟设备客户端，便于本地联调。
- [测试文档](./TESTING_GUIDE.md)：从启动到验证链路的完整步骤。
- [Netty 知识文档](./NETTY_KNOWLEDGE.md)：帮助你理解概念、线程模型、Pipeline、常见坑。

## 场景定位

这个版本更偏向 IoT 网关接入前的“认知搭建 + 可执行样例”：

- 模拟设备通过 TCP 连接平台
- 首包注册设备身份
- 周期性发送心跳
- 上报 telemetry 数据
- 平台向设备下发 command
- 设备回传 command reply
- 断线后自动重连

## 当前协议

为了先把链路跑通，示例使用的是：

- `TCP`
- `换行符分帧`
- `JSON 消息体`

这不是最终生产协议的唯一选择，但很适合第一阶段学习和内部验证。后续如果你们设备侧是二进制报文、固定头、长度字段、校验位、TLV 或 Modbus 风格，都可以在这个工程上替换编解码层。

## 快速开始

```bash
cd solution-netty
mvn -pl netty-core test
mvn -pl netty-sample -am spring-boot:run
```

应用启动后：

- HTTP 示例入口：`http://localhost:8091/netty/demo/overview`
- TCP 服务端端口：`19090`
- 默认会自动启动 2 个模拟设备客户端

## 模块建议

如果后续要做你们公司的真实设备接入，建议按下面思路演进：

1. 保留 `NettyTcpServer` 的整体结构。
2. 将 `TransportMessageCodec` 替换成你们真实设备协议编解码器。
3. 在 `ServerMessageHandler` 中增加鉴权、设备档案校验、协议版本处理。
4. 将 `DeviceSessionManager` 对接 Redis 或数据库，补充设备在线状态同步。
5. 将消息处理解耦到业务服务层，避免所有逻辑都写在 Handler 内。
