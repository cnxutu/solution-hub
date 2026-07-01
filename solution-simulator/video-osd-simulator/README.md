# video-osd-simulator

`video-osd-simulator` 用于联调 ZLMediaKit、视频推流与 OSD 数据回放。当前优先推荐的验证链路是：

1. Spring Boot 调起 `ffmpeg`，把本地视频文件推到 ZLMediaKit 的 `RTMP` 地址。
2. 同时从 `video-osd-simulator-sample/src/main/resources/static` 下的 JSON 文件读取 OSD 数据，按固定间隔回放到 WebSocket。
3. 前端页面通过 ZLMediaKit 的 `WebRTC play` 方式拉流，并叠加接收到的 OSD。

这样就不依赖本机额外安装 WebRTC 推流工具，也能先把“视频 + OSD + 前端展示”整条链路跑通。

## 当前模块职责

`video-osd-simulator-core`

- `FfmpegCommandBuilder`：生成 `RTMP`、`RTSP` 推流命令。
- `ExternalWebRtcCommandBuilder`：为后续外部 WebRTC 推流工具预留命令模板能力。
- `StreamTaskRuntime`：维护外部推流进程状态。
- `VideoSourceScanner`：扫描本地视频文件。
- `OsdPayloadSupport`：把结构化字段和 `raw_json` 合并成最终 OSD JSON。

`video-osd-simulator-sample`

- 任务表 `sim_stream_task` 的 CRUD 与启动、停止、状态查询。
- OSD 表 `device_telemetry_sub` 的 CRUD、JSON 上传、Excel 导入。
- `application.yml` 直接提供 MySQL 运行配置，以及 OSD 数据源切换配置。

## 当前推荐配置

当前 sample 已经收敛为单一 MySQL 方案保存任务与运行日志，不再保留 H2/profile 分流逻辑。

当前本地联调环境按你现在的真实端口写法如下：

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/xm_test?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: Win@2026!

simulator:
  ffmpeg:
    path: ffmpeg
  osd:
    source-type: STATIC_JSON
    websocket-endpoint: ws://127.0.0.1:18083/ws/osd
    static-json-location: classpath:/static/long_text_1813A2F3-3BCF-47A1-BE25-B6A7C4F3E1D8.json
    fixed-interval-millis: 1000
    require-task-id-match: false
    publish-time-start: 2026-06-29T10:00:00
    publish-time-end: 2026-06-29T10:30:00
  video:
    source-file: C:/Users/44527/Downloads/trailer_video_test.mp4
  zlm:
    host: 127.0.0.1
    rtmp-port: 7935
    rtsp-port: 8554
    rtc-port: 10000
    app: live
    stream: drone001
```

说明：

- `source-type=STATIC_JSON` 时，OSD 从 classpath JSON 文件读取，第一条立即发送，后续按 `fixed-interval-millis` 固定间隔发送。
- `static-json-location` 当前默认指向 sample 自带的 `long_text_1813A2F3-3BCF-47A1-BE25-B6A7C4F3E1D8.json`。
- 如果后续要切回数据库时间轴模式，把 `source-type` 改成 `MYSQL` 即可。
- `publish-time-start` 和 `publish-time-end` 用于限定 OSD 回放时间窗。
- `require-task-id-match=false` 时，OSD 按时间窗查询，不强依赖 `device_telemetry_sub.task_id`。
- `require-task-id-match=true` 时，OSD 会额外要求 `device_telemetry_sub.task_id = 当前任务id`。
- 当前 `RTMP` 推流默认会转码为更适合浏览器 WebRTC 拉流验证的 `H.264 + AAC`，不再直接 `copy` 原视频编码。
- `MYSQL` 模式下，OSD 仍然会以时间窗内最小 `publish_time` 为起点，后续每条消息都按它和上一条记录的时间差发送。
- `publish_time` 为空的记录不会参与本轮回放。
- `MYSQL` 模式下如果启用了四角估算，会基于 `height + frame-hfov-deg + frame-vfov-deg + attitude_head` 计算 `frame_center/corners`。
- 当前版本默认新建任务协议为 `RTMP`，这样更适合先完成整体流程验证。

## 启动方式

当前默认直接启动就是 MySQL：

```bash
mvn -pl solution-simulator/video-osd-simulator/video-osd-simulator-sample -am spring-boot:run
```

数据库信息：

- URL：`jdbc:mysql://localhost:3306/xm_test?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai`
- 用户名：`root`
- 密码：`Win@2026!`
- 表：`sim_stream_task`、`sim_task_run_log`，如果切回 `MYSQL` OSD 模式还会读取 `device_telemetry_sub`

## 需要准备哪些表

如果你现在按默认 `STATIC_JSON` 模式跑链路，至少需要这些表：

- `sim_stream_task`
- `sim_task_run_log`

如果后续切回 `MYSQL` 模式，再额外准备：

- `device_telemetry_sub`

`device_telemetry_sub` 是 `MYSQL` 模式下的 OSD 数据源表，应用会按任务里的 `osd_publish_time_start`、`osd_publish_time_end` 过滤，并按 `publish_time asc, id asc` 顺序发送。

默认情况下不要求 `task_id` 必须等于任务 id；如果你希望一条任务只回放自己绑定的数据，可以把 `simulator.osd.require-task-id-match` 改成 `true`。

`sim_task_run_log` 在 MySQL 中不要用 `CLOB`，改成 `LONGTEXT`：

```sql
CREATE TABLE sim_task_run_log (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  task_id BIGINT,
  event_type VARCHAR(64),
  status VARCHAR(32),
  message VARCHAR(1024),
  command_line LONGTEXT,
  sent_count INT DEFAULT 0,
  create_time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP
);
```

如果你之前参考过旧版 H2 示例，请忽略其中的 `DATEADD(...)` 语法；当前 sample 已经不再保留 H2 初始化逻辑。

## 推荐任务写法

当前建议任务协议直接使用 `RTMP`，由服务端推到 ZLM，再让前端通过 WebRTC 拉：

```bash
curl -X POST http://localhost:18083/simulator/stream-task/add ^
  -H "Content-Type: application/json" ^
  -d "{\"taskName\":\"demo-rtmp-webrtc-play\",\"videoFilePath\":\"C:/Users/44527/Downloads/trailer_video_test.mp4\",\"zlmHost\":\"127.0.0.1\",\"zlmPort\":7935,\"app\":\"live\",\"stream\":\"drone001\",\"protocol\":\"RTMP\",\"loopEnabled\":true,\"ffmpegOptions\":\"-re\",\"osdPublishTimeStart\":\"2026-06-29T10:00:00\",\"osdPublishTimeEnd\":\"2026-06-29T10:30:00\"}"
```

启动任务：

```bash
curl -X POST http://localhost:18083/simulator/stream-task/start/1
```

停止任务：

```bash
curl -X POST http://localhost:18083/simulator/stream-task/stop/1
```

查询状态：

```bash
curl http://localhost:18083/simulator/stream-task/status/1
```

## 一次性联调案例

### 1. 后端侧

1. 确认 MySQL 容器 `kh-mysql` 正常运行，且 `xm_test.sim_stream_task`、`xm_test.sim_task_run_log` 可正常写入。
2. 确认 ZLMediaKit 容器 `zlm` 正常运行，关键端口如下：
   - HTTP：`8086`
   - RTMP：`7935`
   - RTSP：`8554`
   - RTC：`10000`
3. 确认本机命令行能执行：

```bash
ffmpeg -version
```

4. 确认 `application.yml` 中的 MySQL 地址、账号、密码正确，然后启动 `video-osd-simulator-sample`。
5. 新增一条 `RTMP` 任务记录。
6. 调用 `/simulator/stream-task/start/{id}`。

### 2. ZLM 侧检查

任务启动后，视频会先推到：

```text
rtmp://127.0.0.1:7935/live/drone001
```

浏览器拉流可直接打开 ZLM 自带页面：

```text
http://localhost:8086/webrtc/index.html?app=live&stream=drone001&type=play
```

这个页面是我刚根据你本机 `http://localhost:8086/webrtc/index.html` 实际内容确认过的，页面内部默认就是走：

```text
/index/api/webrtc?app=live&stream=drone001&type=play
```

如果画面能出来，说明 `RTMP -> ZLM -> WebRTC play` 这条链路已经通了。

注意这里的 `stream` 要和任务里的 `stream` 字段一致。当前默认示例是 `drone001`，不要再用 `stream=test` 去测默认任务。

### 3. OSD 侧检查

OSD 通过应用内置的 WebSocket 服务端对外广播：

```text
ws://127.0.0.1:18083/ws/osd
```

你可以先用一个最小 HTML 页面验证时间节奏：

```html
<!doctype html>
<html lang="zh-CN">
<body>
  <h3>OSD Monitor</h3>
  <pre id="osd"></pre>
  <script>
    const osdBox = document.getElementById('osd');
    const socket = new WebSocket('ws://127.0.0.1:18083/ws/osd');
    let previous = null;
    socket.onmessage = (event) => {
      const now = Date.now();
      const diff = previous === null ? 0 : now - previous;
      previous = now;
      osdBox.textContent =
        new Date(now).toISOString() +
        '  gap=' + diff + 'ms' +
        '\n' + event.data + '\n\n' + osdBox.textContent;
    };
  </script>
</body>
</html>
```

观察重点：

- 第一条 OSD 会立即发送。
- 当前默认 `STATIC_JSON` 模式下，第二条开始会按 `fixed-interval-millis` 固定间隔发送，默认是 `1000ms`。
- 如果切回 `MYSQL` 模式，第二条开始则会按数据库里相邻两条记录的 `publish_time` 差值发送。

## OSD 数据源切换

默认配置：

```yaml
simulator:
  osd:
    source-type: STATIC_JSON
    static-json-location: classpath:/static/long_text_1813A2F3-3BCF-47A1-BE25-B6A7C4F3E1D8.json
    fixed-interval-millis: 1000
    frame-hfov-deg: 60.0
    frame-vfov-deg: 40.0
```

如果你想切回 MySQL：

```yaml
simulator:
  osd:
    source-type: MYSQL
    publish-time-start: 2026-06-29T10:00:00
    publish-time-end: 2026-06-29T10:30:00
    require-task-id-match: false
    frame-hfov-deg: 60.0
    frame-vfov-deg: 40.0
```

当前静态 JSON 文件直接放在：

```text
video-osd-simulator-sample/src/main/resources/static/long_text_1813A2F3-3BCF-47A1-BE25-B6A7C4F3E1D8.json
```

前端如果只是要模拟 OSD 叠加，当前推荐就先用这条 `STATIC_JSON` 路线。

补充说明：

- `frame-hfov-deg`、`frame-vfov-deg` 仅用于 `MYSQL` 模式下的首版四角估算。
- 当前模型按 90 度俯视处理，因此 `frame_center` 直接取无人机当前经纬度。

## 前端接入建议

建议前端先分成两路接：

1. 视频：直接接 ZLM 的 WebRTC 播放地址。
2. OSD：连接 `ws://127.0.0.1:18083/ws/osd`，把 JSON 渲染为叠加层。

最简单的首轮验证方式是：

1. 浏览器先打开 ZLM 自带的 WebRTC 播放页，确认有画面。
2. 同时打开 OSD WebSocket 调试页，确认 OSD 正在按 `publish_time` 节奏到达。
3. 前端 demo 再把这两块合并到一个页面里。

## 关于 WebRTC 推流工具

当前这版不要求你本机先安装 `webrtc-pusher`。因为我们现在优先走的是：

```text
ffmpeg -> RTMP push to ZLM -> browser WebRTC play
```

后续如果你还想继续验证“服务端直接 WebRTC 推流”，再补 `GStreamer` 或其他外部 WebRTC/WHIP 推流工具即可。当前代码里 `WEBRTC` 协议和 `webrtcCommandTemplate` 仍然保留，方便下一轮继续扩展。

## 本地验证

模块测试：

```bash
mvn -pl solution-simulator/video-osd-simulator/video-osd-simulator-core,solution-simulator/video-osd-simulator/video-osd-simulator-sample -am test
```

Docker 运行前先打包：

```bash
mvn -pl solution-simulator/video-osd-simulator/video-osd-simulator-sample -am package
```
