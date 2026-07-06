#!/usr/bin/env sh
set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname "$0")" && pwd)
APP_NAME=video-osd-simulator-v1
APP_JAR="$SCRIPT_DIR/video-osd-simulator-v1-1.0.0.jar"
LOG_DIR="$SCRIPT_DIR/logs"
LOG_FILE="$LOG_DIR/$APP_NAME.log"
ARCHIVE_DIR_PATTERN="$LOG_DIR/$(date +%Y%m%d)"
PID_FILE="$SCRIPT_DIR/$APP_NAME.pid"

if [ ! -f "$APP_JAR" ]; then
  APP_JAR="$SCRIPT_DIR/target/video-osd-simulator-v1-1.0.0.jar"
fi

if [ ! -f "$APP_JAR" ]; then
  echo "[ERROR] Jar not found. Checked:"
  echo "[ERROR]   $SCRIPT_DIR/video-osd-simulator-v1-1.0.0.jar"
  echo "[ERROR]   $SCRIPT_DIR/target/video-osd-simulator-v1-1.0.0.jar"
  echo "[INFO] Build it first with: mvn clean package, or place the jar next to startup.sh"
  exit 1
fi

JAVA_CMD=${JAVA_CMD:-java}
SERVER_PORT=${SERVER_PORT:-18083}
MQTT_BROKER_URL=${MQTT_BROKER_URL:-tcp://192.168.1.112:1883}
MQTT_CLIENT_ID=${MQTT_CLIENT_ID:-video-osd-simulator-v1}
MQTT_TOPIC=${MQTT_TOPIC:-thing/product/8UUXN4E00A05F5/drc/up}
MQTT_QOS=${MQTT_QOS:-0}
MQTT_AUTO_RECONNECT=${MQTT_AUTO_RECONNECT:-true}
MQTT_CLEAN_SESSION=${MQTT_CLEAN_SESSION:-true}
MQTT_DIRECT_CONSUME_ENABLED=${MQTT_DIRECT_CONSUME_ENABLED:-true}
WS_SENDER_THREADS=${WS_SENDER_THREADS:-4}
WS_DROP_LOG_INTERVAL_MILLIS=${WS_DROP_LOG_INTERVAL_MILLIS:-5000}
WS_SEND_SLOW_THRESHOLD_MILLIS=${WS_SEND_SLOW_THRESHOLD_MILLIS:-1000}
MQTT_USERNAME=${MQTT_USERNAME:-}
MQTT_PASSWORD=${MQTT_PASSWORD:-}

mkdir -p "$LOG_DIR"

is_running() {
  pid="$1"
  if [ -z "$pid" ]; then
    return 1
  fi
  kill -0 "$pid" >/dev/null 2>&1
}

if [ -f "$PID_FILE" ]; then
  EXISTING_PID=$(cat "$PID_FILE" 2>/dev/null || true)
  if is_running "$EXISTING_PID"; then
    echo "[ERROR] $APP_NAME is already running. pid=$EXISTING_PID"
    echo "[INFO] PID file: $PID_FILE"
    echo "[INFO] Log file: $LOG_FILE"
    exit 1
  fi
  echo "[WARN] Removing stale PID file: $PID_FILE"
  rm -f "$PID_FILE"
fi

JAVA_ARGS="--server.port=$SERVER_PORT"
JAVA_ARGS="$JAVA_ARGS --simulator.osd.enabled=true"
JAVA_ARGS="$JAVA_ARGS --simulator.osd.websocket-path=/ws/osd"
JAVA_ARGS="$JAVA_ARGS --simulator.osd.ws-sender-threads=$WS_SENDER_THREADS"
JAVA_ARGS="$JAVA_ARGS --simulator.osd.ws-drop-log-interval-millis=$WS_DROP_LOG_INTERVAL_MILLIS"
JAVA_ARGS="$JAVA_ARGS --simulator.osd.ws-send-slow-threshold-millis=$WS_SEND_SLOW_THRESHOLD_MILLIS"
JAVA_ARGS="$JAVA_ARGS --simulator.osd.mqtt.broker-url=$MQTT_BROKER_URL"
JAVA_ARGS="$JAVA_ARGS --simulator.osd.mqtt.client-id=$MQTT_CLIENT_ID"
JAVA_ARGS="$JAVA_ARGS --simulator.osd.mqtt.topic=$MQTT_TOPIC"
JAVA_ARGS="$JAVA_ARGS --simulator.osd.mqtt.qos=$MQTT_QOS"
JAVA_ARGS="$JAVA_ARGS --simulator.osd.mqtt.auto-reconnect=$MQTT_AUTO_RECONNECT"
JAVA_ARGS="$JAVA_ARGS --simulator.osd.mqtt.clean-session=$MQTT_CLEAN_SESSION"
JAVA_ARGS="$JAVA_ARGS --simulator.osd.mqtt.direct-consume-enabled=$MQTT_DIRECT_CONSUME_ENABLED"

if [ -n "$MQTT_USERNAME" ]; then
  JAVA_ARGS="$JAVA_ARGS --simulator.osd.mqtt.username=$MQTT_USERNAME"
fi

if [ -n "$MQTT_PASSWORD" ]; then
  JAVA_ARGS="$JAVA_ARGS --simulator.osd.mqtt.password=$MQTT_PASSWORD"
fi

echo "[INFO] Starting video-osd-simulator-v1"
echo "[INFO] Broker=$MQTT_BROKER_URL"
echo "[INFO] Topic=$MQTT_TOPIC"
echo "[INFO] Port=$SERVER_PORT"
echo "[INFO] WsSenderThreads=$WS_SENDER_THREADS"
echo "[INFO] LogDir=$LOG_DIR"
echo "[INFO] LogFile=$LOG_FILE"

nohup "$JAVA_CMD" ${JAVA_OPTS:-} -Dapp.name=$APP_NAME -Dapp.log.dir=$LOG_DIR -jar "$APP_JAR" $JAVA_ARGS >/dev/null 2>&1 &
APP_PID=$!
echo "$APP_PID" > "$PID_FILE"

sleep 1
if ! is_running "$APP_PID"; then
  echo "[ERROR] Failed to start $APP_NAME. Check log: $LOG_FILE"
  rm -f "$PID_FILE"
  exit 1
fi

echo "[INFO] $APP_NAME started in background."
echo "[INFO] PID=$APP_PID"
echo "[INFO] PID file: $PID_FILE"
echo "[INFO] Active log: $LOG_FILE"
echo "[INFO] Archive logs: $ARCHIVE_DIR_PATTERN"
echo "[INFO] Tail logs: tail -f \"$LOG_FILE\""
echo "[INFO] Show process: ps -fp \$(cat \"$PID_FILE\")"
echo "[INFO] Stop process: kill \$(cat \"$PID_FILE\")"
