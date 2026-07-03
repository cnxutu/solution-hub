# video-osd-simulator-v1

`video-osd-simulator-v1` 是一个独立交付版 OSD 实时转发工具，职责只保留一条链路：

`MQTT -> OSD 映射 -> WebSocket 广播`

它不依赖 `video-osd-simulator-core`、`video-osd-simulator-sample`，也不依赖任何公司私有 Maven 坐标。客户拿到当前目录后，直接构建并通过启动脚本覆盖 MQTT 参数即可部署。

## 功能范围

- 消费 MQTT 实时 OSD 消息
- 按现有链路规则映射成 OSD 结构
- 复用当前 payload 组装规则输出 JSON
- 通过 WebSocket 广播到 `/ws/osd`

当前版本不包含：

- MySQL / MyBatis-Plus / CRUD
- 视频推流 / ZLMediaKit / 任务管理
- SQLite / 静态 JSON 回放

## 构建要求

- Spring Boot `2.7.18`
- 编译目标 `Java 8`
- 打包产物：`target/video-osd-simulator-v1-1.0.0.jar`

构建命令：

```bash
mvn clean package
```

## 默认配置

默认配置位于 [application.yml](/D:/workspace/github/solution-hub/solution-simulator/video-osd-simulator/video-osd-simulator-v1/src/main/resources/application.yml:1)。

默认关键项：

```yaml
server:
  port: 18083

simulator:
  osd:
    websocket-path: /ws/osd
    mqtt:
      broker-url: tcp://127.0.0.1:1883
      client-id: video-osd-simulator-v1
      topic: thing/product/8UUXN4E00A05F5/drc/up
      qos: 0
      direct-consume-enabled: true
```

## 启动方式

### Windows

先构建：

```bash
mvn clean package
```

再启动：

```bat
startup.bat
```

按客户环境覆盖参数：

```bat
set MQTT_BROKER_URL=tcp://192.168.1.112:1883
set MQTT_CLIENT_ID=customer-osd-relay
set MQTT_TOPIC=thing/product/8UUXN4E00A05F5/drc/up
set MQTT_USERNAME=
set MQTT_PASSWORD=
set MQTT_QOS=0
set SERVER_PORT=18083
startup.bat
```

### Linux

先构建：

```bash
mvn clean package
```

再启动：

```bash
sh startup.sh
```

按客户环境覆盖参数：

```bash
export MQTT_BROKER_URL=tcp://192.168.1.112:1883
export MQTT_CLIENT_ID=customer-osd-relay
export MQTT_TOPIC=thing/product/8UUXN4E00A05F5/drc/up
export MQTT_USERNAME=
export MQTT_PASSWORD=
export MQTT_QOS=0
export SERVER_PORT=18083
sh startup.sh
```

## 启动脚本支持的环境变量

- `JAVA_CMD`
- `JAVA_OPTS`
- `SERVER_PORT`
- `MQTT_BROKER_URL`
- `MQTT_CLIENT_ID`
- `MQTT_TOPIC`
- `MQTT_USERNAME`
- `MQTT_PASSWORD`
- `MQTT_QOS`
- `MQTT_AUTO_RECONNECT`
- `MQTT_CLEAN_SESSION`
- `MQTT_DIRECT_CONSUME_ENABLED`

## 对外接口

- WebSocket 地址：`ws://<host>:<server.port>/ws/osd`

前端连接示例：

```javascript
const socket = new WebSocket("ws://127.0.0.1:18083/ws/osd");
socket.onmessage = (event) => {
  console.log("OSD:", event.data);
};
```

## 验证建议

1. 启动应用后确认日志中出现 `MQTT_TRACE [SUBSCRIBED]`。
2. 用 MQTT 客户端向目标 topic 推送现有格式的 OSD JSON。
3. 用浏览器或调试页连接 `ws://127.0.0.1:18083/ws/osd`。
4. 确认 WebSocket 能收到广播消息。

## 测试

模块测试：

```bash
mvn test
```

独立打包：

```bash
mvn clean package
```
