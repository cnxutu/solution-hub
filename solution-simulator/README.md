# solution-simulator

`solution-simulator` 用于沉淀测试模拟类能力。当前包含：

- `video-osd-simulator`：视频推流 + OSD 数据模拟发送方案。

## 模块结构

```text
solution-simulator
└─ video-osd-simulator
   ├─ video-osd-simulator-core
   └─ video-osd-simulator-sample
```

`core` 负责 ffmpeg 命令、视频目录扫描、OSD 数据转换、Excel 导入和 WebSocket 发送；`sample` 负责 Spring Boot 可运行示例、CRUD API、H2 初始化和 Docker 运行样例。
