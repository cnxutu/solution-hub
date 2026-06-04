# solution-rtsp

`solution-rtsp` 是一个按仓库既有风格组织的 RTSP 标准化接入模块，包含：

- `rtsp-core`：会话协商、鉴权、SDP 解析结果承载、保活、拉流调度、自动重连等基础能力。
- `rtsp-netty-transport`：偏自研协议层的 RTSP 控制面实现，带一个本地可启动的 embedded RTSP mock server，适合调试握手链路。
- `rtsp-ffmpeg-bridge`：基于 `ffmpeg` 进程桥接的生产落地实现，适合后续接真实 RTSP 服务、截图、转码、录制。
- `rtsp-sample`：Spring Boot 示例工程，支持 `mock / netty / ffmpeg` 三种 provider 配置切换。
- [操作手册](./OPERATION_MANUAL.md)：从启动、验证到常见扩展点的完整说明。
- [方案选型](./RTSP_ARCHITECTURE_OPTIONS.md)：两种实现路线的对比、架构图和推荐演进方式。

## 场景定位

这个模块适合你在以下阶段直接复用或二次改造：

- 新项目做摄像头、NVR、边缘网关的 RTSP 拉流接入基线。
- 需要先把生产级架构跑通，再替换成真实 socket / Netty / FFmpeg / GStreamer 实现。
- 团队要统一 RTSP 的标准接入流程、监控指标和异常处理约定。

## 当前实现重点

当前版本刻意将“真实媒体解封装”与“生产级接入编排”分开，并做成可拔插形式：

- 在 `core` 中完整模拟 `OPTIONS -> DESCRIBE -> SETUP -> PLAY -> KEEPALIVE -> TEARDOWN`。
- 保留生产里最关键的对象边界：`Endpoint`、`Gateway`、`Session`、`Manager`、`Listener`。
- `mock` 适合快速演练业务侧生命周期。
- `netty` 适合验证 RTSP 控制面握手与 embedded server 联调。
- `ffmpeg` 适合后续接 Docker RTSP 容器或真实摄像头做生产调试。

## 快速开始

```bash
cd solution-rtsp
mvn -pl rtsp-core test
mvn -pl rtsp-sample -am spring-boot:run
```

启动后可直接访问：

- 概览接口：`http://localhost:8092/rtsp/demo/overview`
- 摄像头目录：`http://localhost:8092/rtsp/demo/cameras`
- 会话状态：`http://localhost:8092/rtsp/demo/sessions`
- 事件流：`http://localhost:8092/rtsp/demo/events`

默认 provider 为 `mock`。你也可以在 `application.yml` 中切到：

- `MOCK`
- `NETTY`
- `FFMPEG`

## 推荐落地方式

1. 保留 `RtspStreamManager` 作为统一拉流编排入口。
2. 按需选择 `MockRtspCameraGateway`、`NettyRtspCameraGateway`、`FfmpegRtspCameraGateway`。
3. 将 `RtspFrameListener` 对接到你们的转码、AI 分析、截图、录像或消息总线链路。
4. 将 `RtspSessionSnapshot` 和 `RtspSessionMetrics` 接 Prometheus、Redis 或数据库。
