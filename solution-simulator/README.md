# solution-simulator

`solution-simulator` 用于沉淀测试模拟类能力。当前聚焦模块：

- `video-osd-simulator`：本地视频推流 + OSD 数据模拟发送 + ZLMediaKit 联调闭环。

## 模块结构

```text
solution-simulator
└─ video-osd-simulator
   ├─ video-osd-simulator-core
   └─ video-osd-simulator-sample
```

`video-osd-simulator-core` 负责视频扫描、ffmpeg 推流命令、外部 WebRTC 推流命令、OSD 数据转换、Excel 导入和进程管理。

`video-osd-simulator-sample` 负责 Spring Boot 运行样例、任务 CRUD、MySQL/H2 配置、SQL 初始化、接口暴露与联调示例。

当前推荐的联调链路是：

1. `video-osd-simulator-sample` 通过 `ffmpeg` 把本地视频文件推到 ZLMediaKit 的 `RTMP` 地址。
2. 同时按 `device_telemetry_sub.publish_time` 的时间差，把 OSD 数据回放到 WebSocket。
3. 前端页面通过 ZLMediaKit 的 `WebRTC play` 方式拉流展示视频，并通过 WebSocket 叠加 OSD。
