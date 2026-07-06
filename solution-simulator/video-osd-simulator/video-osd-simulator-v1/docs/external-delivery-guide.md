# video-osd-simulator-v1 外部交付启动说明

`video-osd-simulator-v1` 是一个独立交付的 OSD 实时转发模块。

模块运行链路如下：

`MQTT -> OSD 映射 -> WebSocket 广播`

本文档仅面向外部交付使用，主要说明构建、启动、日志位置、环境变量和对外接口，不展开内部实现细节。

## 模块位置

推荐在以下目录执行构建和启动：

`D:\workspace\github\solution-hub\solution-simulator\video-osd-simulator\video-osd-simulator-v1`

打包产物位置：

`target/video-osd-simulator-v1-1.0.0.jar`

## 构建

请在当前模块目录下执行：

```bash
mvn clean package
```

如果使用 PowerShell，也可直接执行同样的命令：

```bash
mvn clean package
```

## Linux 启动

1. 将 `startup.sh` 与 `video-osd-simulator-v1-1.0.0.jar` 放在同一目录，或保留 jar 在 `target/` 目录下
2. 如需覆盖客户环境参数，先设置环境变量
3. 执行一次启动脚本：

```bash
sh startup.sh
```

示例：

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

- `startup.sh` 为后台守护启动
- 启动脚本只需执行一次
- 若当前目录下已有有效 PID 进程，脚本会拒绝重复启动

## Windows 启动

在当前模块目录下执行：

```bat
startup.bat
```

如需覆盖客户环境参数，可先设置环境变量后再执行：

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

- `startup.bat` 为前台启动
- 启动脚本只需执行一次

## 日志文件

Linux 启动后，日志默认输出到：

```text
logs/
  video-osd-simulator-v1.log
  yyyyMMdd/
    video-osd-simulator-v1.0.log.gz
    video-osd-simulator-v1.1.log.gz
```

常用排查命令：

```bash
tail -f logs/video-osd-simulator-v1.log
ps -fp $(cat video-osd-simulator-v1.pid)
kill $(cat video-osd-simulator-v1.pid)
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

## 对外接口

WebSocket 地址：

`ws://<host>:<server.port>/ws/osd`

MQTT 消费主题由以下环境变量控制：

`MQTT_TOPIC`

## 部署检查建议

1. 启动应用
2. 确认进程正常存在
3. 确认 MQTT 主题上已有设备消息输入
4. 确认前端可以连接 `/ws/osd`
5. 如需排查，查看 `logs/video-osd-simulator-v1.log`
