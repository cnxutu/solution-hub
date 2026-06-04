# rtsp-ffmpeg-bridge

这个模块提供的是偏“生产落地”的桥接实现思路：

- 通过 `ffmpeg` 进程拉 RTSP
- `core` 继续负责生命周期、会话编排和状态输出
- 进程日志会被采集，用于调试当前拉流状态

适合你后续：

- 接真实 RTSP server
- 做截图、录制、抽帧、转码前的基础接入
- 结合 Docker RTSP 容器做整链路验证
