# rtsp-netty-transport

这个模块提供的是偏“自研 RTSP 协议层”的实现思路：

- 真实发送 `OPTIONS / DESCRIBE / SETUP / PLAY / GET_PARAMETER / TEARDOWN`
- 自带一个 `EmbeddedRtspServer`，方便本地联调控制面
- 当前重点是握手、鉴权、保活和状态机，不是完整 RTP 解码

适合你用来感受：

- 不借重媒体库时，RTSP 控制面大概需要自己做哪些事
- `core` 如何挂接一种更偏底层的实现
