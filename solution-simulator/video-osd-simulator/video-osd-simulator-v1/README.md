# video-osd-simulator-v1

`video-osd-simulator-v1` 是一个独立交付版 OSD 实时转发工具，职责只保留一条链路：

`MQTT -> OSD 映射 -> WebSocket 广播`

它不依赖 `video-osd-simulator-core`、`video-osd-simulator-sample`，也不依赖任何公司私有 Maven 坐标。客户拿到当前目录后，直接构建并通过启动脚本覆盖 MQTT 参数即可部署。

## 功能范围

- 消费 MQTT 实时 OSD 消息
- 按现有链路规则计算中心点与四角
- 输出精简 WebSocket 结构
- 通过 WebSocket 广播到 `/ws/osd`
- 慢客户端场景下只保留最新消息，避免反压 MQTT 消费

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
    ws-sender-threads: 4
    ws-drop-log-interval-millis: 5000
    ws-send-slow-threshold-millis: 1000
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

默认参数启动：

```bat
startup.bat
```

如果要覆盖客户环境参数，先设置环境变量，再启动一次：

```bat
set MQTT_BROKER_URL=tcp://192.168.1.112:1883
set MQTT_CLIENT_ID=customer-osd-relay
set MQTT_TOPIC=thing/product/8UUXN4E00A05F5/drc/up
set MQTT_USERNAME=
set MQTT_PASSWORD=
set MQTT_QOS=0
set SERVER_PORT=18083
set WS_SENDER_THREADS=4
set WS_DROP_LOG_INTERVAL_MILLIS=5000
set WS_SEND_SLOW_THRESHOLD_MILLIS=1000
startup.bat
```

说明：

- `startup.bat` 只需要执行一次。
- 上面两段是两种启动方式，不是“先默认启动，再自定义启动”。

### Linux

先构建：

```bash
mvn clean package
```

默认参数启动：

```bash
sh startup.sh
```

如果要覆盖客户环境参数，先设置环境变量，再启动一次：

```bash
export MQTT_BROKER_URL=tcp://192.168.1.112:1883
export MQTT_CLIENT_ID=customer-osd-relay
export MQTT_TOPIC=thing/product/8UUXN4E00A05F5/drc/up
export MQTT_USERNAME=
export MQTT_PASSWORD=
export MQTT_QOS=0
export SERVER_PORT=18083
export WS_SENDER_THREADS=4
export WS_DROP_LOG_INTERVAL_MILLIS=5000
export WS_SEND_SLOW_THRESHOLD_MILLIS=1000
sh startup.sh
```

说明：

- `startup.sh` 只需要执行一次。
- 上面两段是两种启动方式，不是“先默认启动，再自定义启动”。

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
- `WS_SENDER_THREADS`
- `WS_DROP_LOG_INTERVAL_MILLIS`
- `WS_SEND_SLOW_THRESHOLD_MILLIS`

## 对外接口

- WebSocket 地址：`ws://<host>:<server.port>/ws/osd`

前端连接示例：

```javascript
const socket = new WebSocket("ws://127.0.0.1:18083/ws/osd");
socket.onmessage = (event) => {
  console.log("OSD:", event.data);
};
```

当前 WebSocket 输出字段固定为：

- `attitude_head`
- `latitude`
- `longitude`
- `height`
- `speed_x`
- `speed_y`
- `speed_z`
- `gimbal_pitch`
- `gimbal_roll`
- `gimbal_yaw`
- `frame_center`
- `corners`

说明：

- 当前策略是实时优先，不保证前端收到每一条消息。
- 某个 WebSocket 客户端发送过慢时，旧待发送消息会被新消息覆盖。
- 服务端会输出 `WS_DROP_SUMMARY` 聚合日志，用来感知慢连接导致的丢弃。
- 服务端会每 `5s` 输出一次 `MQTT_TRACE [MQTT_TO_WS_COST_SUMMARY]`，用于观察这段时间内 MQTT 到 WS 的平均耗时与最大耗时。

## MQTT 输入消息

v1 当前只消费 MQTT 报文中的少量关键字段，推荐输入结构如下：

```json
{
  "method": "osd_info_push",
  "seq": 4392,
  "timestamp": 1782972590947,
  "data": {
    "attitude_head": 87.0,
    "latitude": 30.18566738053386,
    "longitude": 120.19797559348879,
    "height": 100.24636383056641,
    "speed_x": 0.0,
    "speed_y": 0.0,
    "speed_z": 1.0,
    "gimbal_pitch": -0.1,
    "gimbal_roll": 1.5,
    "gimbal_yaw": 87.3602828699535
  }
}
```

当前 v1 实际消费字段说明：

| 字段 | 类型 | 说明 | 是否必需 / 缺失行为 |
| --- | --- | --- | --- |
| `timestamp` | `long` | MQTT 消息时间戳，毫秒 | 非必需；缺失时仅影响服务端耗时日志中的时间信息 |
| `data.attitude_head` | `number` | 无人机机头朝向 | 非必需；缺失时按 `0` 参与四角旋转计算 |
| `data.latitude` | `number` | 无人机当前纬度 | 建议必需；缺失时无法计算 `frame_center` / `corners` |
| `data.longitude` | `number` | 无人机当前经度 | 建议必需；缺失时无法计算 `frame_center` / `corners` |
| `data.height` | `number` | 无人机当前高度 | 建议必需；缺失或小于等于 `0` 时无法计算 `frame_center` / `corners` |
| `data.speed_x` | `number` | X 轴速度分量 | 非必需；缺失时 WebSocket 对应字段为空 |
| `data.speed_y` | `number` | Y 轴速度分量 | 非必需；缺失时 WebSocket 对应字段为空 |
| `data.speed_z` | `number` | Z 轴速度分量 | 非必需；缺失时 WebSocket 对应字段为空 |
| `data.gimbal_pitch` | `number` | 云台俯仰角 | 非必需；缺失时 WebSocket 对应字段为空 |
| `data.gimbal_roll` | `number` | 云台横滚角 | 非必需；缺失时 WebSocket 对应字段为空 |
| `data.gimbal_yaw` | `number` | 云台偏航角 | 非必需；缺失时 WebSocket 对应字段为空 |

说明：

- 除上表字段外，其他 MQTT 字段即使存在，v1 当前也不会参与输出。
- `frame_center` / `corners` 的计算依赖 `latitude + longitude + height`，并结合 `attitude_head`、`frame-hfov-deg`、`frame-vfov-deg`。

## WebSocket 输出消息

服务端推送给前端的消息结构如下：

```json
{
  "latitude": 30.18566738053386,
  "longitude": 120.19797559348879,
  "height": 100.24636,
  "corners": [
    {
      "lat": 30.186090117590297,
      "lon": 120.19816482019777
    },
    {
      "lat": 30.186613173200535,
      "lon": 120.19872774328237
    },
    {
      "lat": 30.18524464347742,
      "lon": 120.19778636677981
    },
    {
      "lat": 30.184721587867183,
      "lon": 120.19722344369521
    }
  ],
  "attitude_head": 87.0,
  "speed_x": 0.0,
  "speed_y": 0.0,
  "speed_z": 1.0,
  "gimbal_pitch": -0.1,
  "gimbal_roll": 1.5,
  "gimbal_yaw": 87.3602828699535,
  "frame_center": {
    "lat": 30.18566738053386,
    "lon": 120.19797559348879
  }
}
```

字段说明：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `attitude_head` | `number` | 无人机机头朝向 |
| `latitude` | `number` | 无人机当前纬度 |
| `longitude` | `number` | 无人机当前经度 |
| `height` | `number` | 无人机当前高度 |
| `speed_x` | `number` | X 轴速度分量 |
| `speed_y` | `number` | Y 轴速度分量 |
| `speed_z` | `number` | Z 轴速度分量 |
| `gimbal_pitch` | `number` | 云台俯仰角 |
| `gimbal_roll` | `number` | 云台横滚角 |
| `gimbal_yaw` | `number` | 云台偏航角 |
| `frame_center.lat` | `number` | 画面中心点纬度 |
| `frame_center.lon` | `number` | 画面中心点经度 |
| `corners[].lat` | `number` | 画面角点纬度 |
| `corners[].lon` | `number` | 画面角点经度 |

说明：

- `frame_center` 与 `corners` 由后端实时计算生成，不要求设备直接上送。
- 若 `latitude`、`longitude` 或 `height` 缺失，`frame_center` / `corners` 可能为空。
- 当前策略是实时优先，慢客户端可能跳过中间帧，只保证尽快收到最新值。

## 验证建议

1. 启动应用后确认 MQTT 已建立消费连接。
2. 用 MQTT 客户端向目标 topic 推送现有格式的 OSD JSON。
3. 用浏览器或调试页连接 `ws://127.0.0.1:18083/ws/osd`。
4. 确认 WebSocket 能收到广播消息。
5. 若前端故意降速，可观察 `OSD_TRACE [WS_DROP_SUMMARY]` 是否输出聚合丢弃信息。

## 测试

模块测试：

```bash
mvn test
```

独立打包：

```bash
mvn clean package
```
