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
- 交付部署时，jar 可以放在 `startup.sh/startup.bat` 同级目录，或保留在 `target/` 目录

构建命令：

```bash
mvn clean package
```

混淆构建命令：

```bash
mvn clean package -Dobfuscation.enabled=true
```

windows 下环境命令推荐：
```bash  
mvn clean package '-Dobfuscation.enabled=true'
```


说明：

- `application.yml` 不控制 jar 是否混淆。
- 混淆属于构建期行为，只能通过 Maven 构建开关控制。
- 当前采用保守混淆策略，目标是“可运行优先”，不是极致防逆向。
- 混淆只影响打包产物，不影响启动方式、MQTT 参数覆盖方式和 `/ws/osd` 路径。
- 如果在 PowerShell 中执行，建议写成 `mvn clean package '-Dobfuscation.enabled=true'`，避免 `-D` 参数被错误拆分。

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
    ws-crypto:
      enabled: false
      mode: plain
      key-base64:
      key-id:
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
- Windows 当前仍是前台启动，主要用于本地手工调试。
- Linux 才提供后台守护启动、PID 文件和日志归档能力。

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
export WS_CRYPTO_ENABLED=false
export WS_CRYPTO_MODE=plain
export WS_CRYPTO_KEY_BASE64=
export WS_CRYPTO_KEY_ID=
sh startup.sh
```

说明：

- `startup.sh` 只需要执行一次。
- 上面两段是两种启动方式，不是“先默认启动，再自定义启动”。
- `startup.sh` 会优先查找同级目录下的 `video-osd-simulator-v1-1.0.0.jar`，找不到时再查找 `target/video-osd-simulator-v1-1.0.0.jar`。
- `startup.sh` 是后台守护启动，命令返回后应用会继续运行。
- 脚本会在当前目录自动创建 `logs/`，并生成 `video-osd-simulator-v1.pid`。
- 当前活动日志文件固定为 `logs/video-osd-simulator-v1.log`。
- 每天归档日志会进入 `logs/yyyyMMdd/`，同一天单文件超过 `200MB` 后按序号滚动。
- 若 PID 文件存在且进程仍在运行，脚本会拒绝重复启动；若 PID 文件失效，则会自动清理后重启。

推荐的 Linux 交付目录结构示例：

```text
/home/data/test/260706/
  startup.sh
  video-osd-simulator-v1-1.0.0.jar
  video-osd-simulator-v1.pid
  logs/
    video-osd-simulator-v1.log
    20260706/
      video-osd-simulator-v1.0.log.gz
      video-osd-simulator-v1.1.log.gz
```

启动成功后，脚本会直接打印常用排查命令，例如：

```bash
tail -f /home/data/test/260706/logs/video-osd-simulator-v1.log
ps -fp $(cat /home/data/test/260706/video-osd-simulator-v1.pid)
kill $(cat /home/data/test/260706/video-osd-simulator-v1.pid)
ls -lh /home/data/test/260706/logs
ls -lh /home/data/test/260706/logs/20260706
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
- `WS_SENDER_THREADS`
- `WS_DROP_LOG_INTERVAL_MILLIS`
- `WS_SEND_SLOW_THRESHOLD_MILLIS`
- `WS_CRYPTO_ENABLED`
- `WS_CRYPTO_MODE`
- `WS_CRYPTO_KEY_BASE64`
- `WS_CRYPTO_KEY_ID`

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

- `timestamp`
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
- `simulator.osd.ws-crypto.enabled=false` 时，WS 输出保持当前明文 JSON。
- `simulator.osd.ws-crypto.enabled=true` 且 `mode=aes-gcm` 时，WS 输出改为密文 envelope，前端需先解密再读取原始 OSD JSON。

## WS 加密配置

当前只支持两种模式：

- `plain`
- `aes-gcm`

推荐配置示例：

```yaml
simulator:
  osd:
    ws-crypto:
      enabled: true
      mode: aes-gcm
      key-base64: MDEyMzQ1Njc4OWFiY2RlZg==
      key-id: relay-key-01
```

说明：

- 推荐使用 `AES-GCM`，不推荐 `XOR + Base64`。
- `AES-GCM` 同时提供机密性和完整性校验，更适合正式交付。
- 若 `enabled=true` 且 `key-base64` 非法、或解码后不是 `16/24/32` 字节，应用会在启动时直接失败。
- `key-id` 会透传到 WS 密文 envelope，方便前端按密钥版本解密。

开启 AES-GCM 后，WS 推送示例结构如下：

```json
{
  "encrypted": true,
  "alg": "AES-GCM",
  "kid": "relay-key-01",
  "iv": "H3rGmYxvT3M5YxXv",
  "ciphertext": "4m4p3g2sjx0oK1x4m+4r+6P8m6K3J2gJ0D3v3r4i7T8..."
}
```

前后端对接时，后端需要提供给前端的内容：

1. WebSocket 地址，例如 `ws://<host>:<port>/ws/osd`
2. 是否启用加密，即 `ws-crypto.enabled`
3. 加密模式，当前固定为 `AES-GCM`
4. `kid` 对应的密钥明文，或双方约定好的密钥分发表
5. 密钥编码方式，当前为 `Base64`
6. 解密后得到的原始 OSD JSON 结构说明

说明：

- `kid` 只是密钥标识，不是密钥本身。
- `iv` 会随每条消息一起下发，它不是保密字段，可以公开传输。
- 真正需要保密的是 `key`。
- 当前实现里 `iv` 为服务端每条消息随机生成，前端无需预置固定 `iv`。

前端处理流程：

1. 读取 `kid` 定位密钥
2. 对 `kid` 对应的 `key-base64` 做 Base64 解码，得到 AES 原始密钥字节
3. 从消息中读取 `iv` 和 `ciphertext`，两者也先做 Base64 解码
4. 使用 `key + iv + ciphertext` 做 AES-GCM 解密
5. 解密后得到原始 OSD JSON

前端要点：

- 不能只拿 `iv + ciphertext` 解密，必须同时持有正确的 `key`。
- 即使别人看到了 `kid`、`iv`、`ciphertext`，只要没有 `key`，也无法还原明文。
- 若后续切换密钥版本，只需变更 `kid -> key` 的映射关系即可。

推荐对接流程：

1. 后端先提供一组联调密钥，例如：
   - `kid=relay-key-01`
   - `key-base64=MDEyMzQ1Njc4OWFiY2RlZg==`
2. 前端按 `kid` 建立本地密钥映射表
3. 联调时先确认收到的是 AES-GCM envelope，而不是明文 JSON
4. 前端解密成功后，再按“WebSocket 输出消息”章节解析原始 OSD 字段
5. 若正式环境需要换钥，只替换服务端配置和前端密钥映射，不改 WS 地址和业务字段结构

可给前端的示例密钥说明：

```text
kid: relay-key-01
key-base64: MDEyMzQ1Njc4OWFiY2RlZg==
algorithm: AES-GCM
payload-encoding: Base64(iv) + Base64(ciphertext)
plaintext: WebSocket 输出消息章节中的原始 OSD JSON
```

明文模式下，前端处理方式保持不变。

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
- 若 `data.latitude` 或 `data.longitude` 为空，则该条消息会按心跳/无效 OSD 直接过滤，不进入 WS 推送。
- `frame_center` / `corners` 的计算依赖 `latitude + longitude + height`，并结合画面朝向角、`frame-hfov-deg`、`frame-vfov-deg`。
- 画面朝向角优先取 `attitude_head`，缺失时回退到 `gimbal_yaw`。

## WebSocket 输出消息

服务端推送给前端的消息结构如下：

```json
{
  "timestamp": 1782972590947,
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
| `timestamp` | `long` | MQTT 原始消息时间戳，毫秒，服务端透传 |
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
- `corners` 的顺序固定为相对视频画面的 `topLeft`、`topRight`、`bottomRight`、`bottomLeft`。
- 上述 `top / bottom / left / right` 是画面语义，不是地图上的西北、东北、东南、西南。
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
