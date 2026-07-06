# video-osd-simulator-v1 External Delivery Guide

`video-osd-simulator-v1` is delivered as an independent OSD relay module.

Its runtime path is:

`MQTT -> OSD mapping -> WebSocket broadcast`

This guide is intended for external deployment and startup use. It only covers build, startup, log location, and external interface basics.

## Package Location

Recommended module directory:

`D:\workspace\github\solution-hub\solution-simulator\video-osd-simulator\video-osd-simulator-v1`

Build output:

`target/video-osd-simulator-v1-1.0.0.jar`

## Build

Run the package command in the module directory:

```bash
mvn clean package
```

If you use PowerShell, the same command can be executed directly:

```bash
mvn clean package
```

## Linux Startup

1. Place `startup.sh` and `video-osd-simulator-v1-1.0.0.jar` in the same directory, or keep the jar under `target/`
2. Set customer environment variables if needed
3. Run once:

```bash
sh startup.sh
```

Example:

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

Notes:

- `startup.sh` runs in background
- run it once only
- if a valid PID already exists, the script will reject duplicate startup

## Windows Startup

Run in the module directory:

```bat
startup.bat
```

Example with environment overrides:

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

Notes:

- `startup.bat` is foreground mode
- run it once only

## Log Files

Linux startup writes logs under:

```text
logs/
  video-osd-simulator-v1.log
  yyyyMMdd/
    video-osd-simulator-v1.0.log.gz
    video-osd-simulator-v1.1.log.gz
```

Useful commands after startup:

```bash
tail -f logs/video-osd-simulator-v1.log
ps -fp $(cat video-osd-simulator-v1.pid)
kill $(cat video-osd-simulator-v1.pid)
```

## Supported Environment Variables

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

## External Interface

WebSocket endpoint:

`ws://<host>:<server.port>/ws/osd`

MQTT topic is configured by:

`MQTT_TOPIC`

## Deployment Check

1. Start the application
2. Confirm the process is alive
3. Confirm MQTT source data is arriving on the configured topic
4. Confirm the frontend can connect to `/ws/osd`
5. Check `logs/video-osd-simulator-v1.log` if needed
