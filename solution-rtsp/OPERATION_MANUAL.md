# RTSP 模块操作手册

## 1. 手册目标

这份手册不是只告诉你“怎么启动”，而是让你可以在本地完整模拟一次生产里的 RTSP 接入操作：

1. 注册摄像头目录
2. 拉起 RTSP 会话
3. 观察握手、保活、持续拉帧
4. 停止某一路流
5. 按相同代码入口替换为真实 RTSP 实现

## 2. 模块结构

```text
solution-rtsp
├─ rtsp-core
│  ├─ model        会话、帧、轨道、指标对象
│  ├─ gateway      RTSP 传输抽象
│  ├─ client       单路流握手和拉帧编排
│  └─ manager      多路流调度、保活、重连
├─ rtsp-netty-transport
│  ├─ gateway      RTSP 控制面握手实现
│  ├─ client       socket transport
│  └─ server       embedded mock RTSP server
├─ rtsp-ffmpeg-bridge
│  └─ ffmpeg       ffmpeg 进程桥接
└─ rtsp-sample
   ├─ controller   HTTP 调试入口
   ├─ service      mock 摄像头目录、事件观察器、启动引导
   └─ application.yml
```

## 3. 启动方式

### 3.1 仅验证 core

```bash
cd solution-rtsp
mvn -pl rtsp-core test
```

你会验证到：

- 会话可以完成 `OPTIONS -> DESCRIBE -> SETUP -> PLAY`
- 能够拉到模拟帧
- 能发送 keepalive
- 能正常 teardown

### 3.2 启动 sample

```bash
cd solution-rtsp
mvn -pl rtsp-sample -am install -DskipTests
mvn -f rtsp-sample/pom.xml spring-boot:run
```

说明：

- 不要直接在 `solution-rtsp` 聚合模块上执行 `mvn -pl rtsp-sample -am spring-boot:run`
- 这个命令会先命中聚合 `pom`，从而报 `Unable to find a suitable main class`
- 推荐先 `install` 子模块依赖，再从 `rtsp-sample/pom.xml` 启动

默认端口：

- HTTP：`8092`

默认行为：

- 自动注册 2 路 mock 摄像头
- 自动启动 2 路拉流任务
- 每 `800ms` 模拟拉取 1 帧
- 每 `15s` 发送一次 keepalive

## 3.3 provider 切换

通过 [application.yml](D:/workspace/github/solution-hub/solution-rtsp/rtsp-sample/src/main/resources/application.yml) 配置：

```yaml
rtsp:
  sample:
    provider: mock
```

可选值：

- `MOCK`
- `NETTY`
- `FFMPEG`

### `MOCK`

- 不依赖真实 RTSP 服务
- 使用 `rtsp-core` 中的 mock gateway
- 最适合先看业务生命周期

### `NETTY`

- 使用 `rtsp-netty-transport`
- 默认会起一个本地 embedded RTSP mock server
- 适合调试 `OPTIONS / DESCRIBE / SETUP / PLAY / GET_PARAMETER / TEARDOWN`

### `FFMPEG`

- 使用 `rtsp-ffmpeg-bridge`
- 需要本机已安装 `ffmpeg`，或能在 PATH 中找到
- 适合连真实 RTSP server / docker 容器

## 4. 默认模拟摄像头

### 4.1 front-gate

- `streamId`: `front-gate`
- `rtspUrl`: `rtsp://mock-camera/front-gate/main`
- `username`: `admin`
- `password`: `123456`
- `vendor`: `hikvision`

### 4.2 warehouse

- `streamId`: `warehouse`
- `streamPath`: `/live/warehouse/sub`
- `username`: `operator`
- `password`: `123456`
- `vendor`: `dahua`

不同 provider 下，`rtspUrl` 会自动组装：

- `MOCK`：`rtsp://mock-camera/...`
- `NETTY / FFMPEG`：`rtsp://{rtspHost}:{rtspPort}/...`

## 5. 调用示例

### 5.1 查看总览

```bash
curl http://localhost:8092/rtsp/demo/overview
```

你可以看到：

- 已注册摄像头目录
- 当前活动会话
- 最近事件
- 当前 provider
- embedded server 是否开启

### 5.2 查看摄像头目录

```bash
curl http://localhost:8092/rtsp/demo/cameras
```

适合用于：

- 核对 sample 内置流地址
- 查看当前 mock 账号密码
- 后续替换成数据库摄像头配置中心时对照结构

### 5.3 查看会话状态

```bash
curl http://localhost:8092/rtsp/demo/sessions
```

重点关注字段：

- `state`：是否为 `PLAYING`
- `remoteSessionId`：模拟摄像头分配的 RTSP session
- `metrics.frameCount`：帧计数是否持续增长
- `lastKeepAliveAt`：保活时间是否刷新
- `lastError`：是否有异常

### 5.4 查看事件流

```bash
curl http://localhost:8092/rtsp/demo/events
```

你会看到类似事件：

```text
START stream=front-gate, session=front-gate-xxxx
FRAME stream=front-gate, seq=1, keyFrame=true
FRAME stream=warehouse, seq=1, keyFrame=true
```

### 5.5 查看某一路的最近帧

```bash
curl "http://localhost:8092/rtsp/demo/frames?streamId=front-gate"
```

可观察字段：

- `sequence`
- `timestamp`
- `codec`
- `keyFrame`
- `payloadPreview`

### 5.6 手动启动一路流

如果你把 `rtsp.sample.auto-start` 改成 `false`，可以手工启动：

```bash
curl -X POST "http://localhost:8092/rtsp/demo/start?streamId=front-gate"
```

### 5.7 停止一路流

```bash
curl -X POST "http://localhost:8092/rtsp/demo/stop?streamId=warehouse"
```

之后再看：

```bash
curl http://localhost:8092/rtsp/demo/sessions
```

你会发现该流已经被移出活动任务。

## 6. sample 中如何调用 core

sample 的核心调用入口就是 `RtspStreamManager.start(endpoint, observer)`。

典型流程：

1. `SampleCameraCatalog` 根据 `streamId` 构建 `RtspEndpoint`
2. `RtspStreamManager` 接收 endpoint 后创建调度任务
3. `RtspStreamClient` 完成 RTSP 握手
4. `SampleRtspObserver` 接收 `onSessionStarted / onFrame / onSessionError / onSessionStopped`
5. `RtspDemoController` 通过 HTTP 把会话和事件暴露出来

provider 的选择发生在：

- [RtspSampleConfig.java](D:/workspace/github/solution-hub/solution-rtsp/rtsp-sample/src/main/java/com/cv/rtsp/sample/config/RtspSampleConfig.java)

这里会根据配置选择：

- `MockRtspCameraGateway`
- `NettyRtspCameraGateway`
- `FfmpegRtspCameraGateway`

## 7. 替换为真实 RTSP 的标准做法

你真正上线时，一般不需要动 `controller` 和 `manager`，重点替换这层：

- 新建 `RealRtspCameraGateway implements RtspCameraGateway`

建议逐步实现：

1. `options(session)`：建立 socket，发送 OPTIONS
2. `describe(session)`：请求 SDP，解析音视频轨道
3. `setup(session)`：协商 transport，拿到 server session id
4. `play(session)`：开始收 RTP
5. `readFrame(session)`：将 RTP 包重组为帧或 NALU 片段
6. `keepAlive(session)`：定时发送 `GET_PARAMETER` 或 `OPTIONS`
7. `teardown(session)`：释放连接

## 8. Docker 联调建议

如果你后续要自己起本地 RTSP/FFmpeg 容器做整链路测试，推荐这样用：

### 8.1 RTSP 服务容器

你可以准备一个本地 RTSP server，例如把摄像头视频文件循环推成 RTSP。

典型验证目标：

- provider 切到 `FFMPEG`
- `rtspHost` 指向本地 docker 暴露端口
- `rtspPort` 改成容器端口
- `ffmpeg.binary` 指向宿主机 `ffmpeg`

### 8.2 FFmpeg 本机调试

确认本机命令可用：

```bash
ffmpeg -version
```

然后在 sample 中改成：

```yaml
rtsp:
  sample:
    provider: ffmpeg
    rtsp-host: 127.0.0.1
    rtsp-port: 8554
    ffmpeg:
      binary: ffmpeg
      transport: tcp
```

### 8.3 本地自带调试模式

如果你暂时不想起 docker，也可以先：

```yaml
rtsp:
  sample:
    provider: netty
    embedded-server:
      enabled: true
```

这样 sample 会直接起一个本地 embedded RTSP mock server，先把握手链路跑通。

## 9. 生产落地建议

### 8.1 必补能力

- digest/basic 鉴权
- TCP interleaved 和 UDP transport 切换
- 超时控制与断流检测
- 重连退避策略
- 帧队列背压
- 指标埋点和告警

### 8.2 推荐扩展点

- 在 `RtspFrameListener` 中挂接截图服务
- 在 `RtspFrameListener` 中投递 AI 识别任务
- 在 `RtspSessionSnapshot` 基础上做在线状态看板
- 将 `SampleCameraCatalog` 替换成数据库或配置中心驱动

## 10. 常见排查思路

### 10.1 会话没进入 PLAYING

优先排查：

- `rtspUrl` 是否匹配摄像头目录
- 用户名密码是否正确
- `MockRtspCameraGateway` 或真实网关是否抛异常

### 10.2 没有帧增长

优先排查：

- `framePullIntervalMillis` 是否被配得过大
- `readFrame` 是否抛错
- `onSessionError` 里是否已经出现异常

### 10.3 保活不刷新

优先排查：

- `keepAliveSeconds` 配置
- 调度线程是否被阻塞
- 真实网关是否未实现 `GET_PARAMETER` 或 `OPTIONS` keepalive

## 11. 最小二次开发范例

如果你要在 sample 中新增“按业务编码启动拉流”，只需要：

1. 从数据库查询摄像头配置
2. 组装为 `RtspEndpoint`
3. 调用 `RtspStreamManager.start(endpoint, yourListener)`

这也是当前模块刻意保持 `core` 与 `sample` 解耦的原因。
