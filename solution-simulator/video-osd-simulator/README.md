# video-osd-simulator

`video-osd-simulator` 是一个用于联调 ZLMediaKit 与 OSD 数据链路的模拟器。

## 能力

- 扫描指定目录下的视频文件。
- 使用 Spring Boot 调度 ffmpeg 进程向 ZLM 推流。
- 第一版支持 `RTMP`、`RTSP`，`WEBRTC` 仅保留枚举并返回明确不支持。
- 使用 H2 作为默认样例库，提供 `sim_stream_task`、`device_telemetry_sub`、`sim_task_run_log`。
- 支持 OSD CRUD、JSON 上传、Excel 导入。
- 按任务异步通过 WebSocket 发送 OSD JSON。
- Docker 镜像内安装 ffmpeg，部署时不依赖宿主机安装。

## 本地运行

```bash
mvn -pl solution-simulator/video-osd-simulator/video-osd-simulator-sample -am spring-boot:run
```

默认端口：`18083`

H2 控制台：`http://localhost:18083/h2-console`

默认 JDBC：`jdbc:h2:mem:video-osd-simulator;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1`

## ffmpeg 配置

```yaml
simulator:
  ffmpeg:
    path: ffmpeg
    input-patterns: "*.mp4,*.flv,*.mov,*.mkv"
```

开发态可以把 `path` 改成本机 ffmpeg 绝对路径；Docker 运行时镜像内已安装 ffmpeg，保持默认即可。

## Docker 运行

先打包：

```bash
mvn -pl solution-simulator/video-osd-simulator/video-osd-simulator-sample -am package
```

再进入 sample 目录：

```bash
cd solution-simulator/video-osd-simulator/video-osd-simulator-sample
docker compose up --build
```

视频目录默认挂载到容器内 `/app/sample-videos`。

## 常用接口

新增推流任务：

```bash
curl -X POST http://localhost:18083/simulator/stream-task/add \
  -H "Content-Type: application/json" \
  -d "{\"taskName\":\"demo\",\"videoDirectory\":\"./sample-videos\",\"zlmHost\":\"127.0.0.1\",\"zlmPort\":1935,\"app\":\"live\",\"stream\":\"drone001\",\"protocol\":\"RTMP\"}"
```

启动任务：

```bash
curl -X POST http://localhost:18083/simulator/stream-task/start/1
```

停止任务：

```bash
curl -X POST http://localhost:18083/simulator/stream-task/stop/1
```

上传 OSD JSON：

```bash
curl -X POST http://localhost:18083/simulator/osd/upload-json \
  -H "Content-Type: application/json" \
  -d "{\"taskId\":1,\"deviceSn\":\"dock-001\",\"latitude\":30.1234567890123,\"longitude\":120.1234567890123,\"rawJson\":\"{\\\"battery\\\":{\\\"percent\\\":90}}\"}"
```

导入 Excel：

```bash
curl -F "file=@osd.xlsx" -F "taskId=1" http://localhost:18083/simulator/osd/import-excel
```

Excel 首行建议使用字段名，例如：`device_sn`、`task_id`、`latitude`、`longitude`、`raw_json`。

## MySQL 切换

H2 schema 已按 MySQL 命名习惯组织。切换 MySQL 时，将 `application.yml` 中 datasource 改为 MySQL，并把 `device_telemetry_sub` 替换为真实 MySQL DDL 即可；JSON 字段可用 MySQL `json` 类型。
