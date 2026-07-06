@echo off
setlocal

set SCRIPT_DIR=%~dp0
set APP_JAR=%SCRIPT_DIR%target\video-osd-simulator-v1-1.0.0.jar

if not exist "%APP_JAR%" (
  echo [ERROR] Jar not found: %APP_JAR%
  echo [INFO] Build it first with: mvn clean package
  exit /b 1
)

if "%JAVA_CMD%"=="" set JAVA_CMD=java
if "%SERVER_PORT%"=="" set SERVER_PORT=18083
if "%MQTT_BROKER_URL%"=="" set MQTT_BROKER_URL=tcp://127.0.0.1:1883
if "%MQTT_CLIENT_ID%"=="" set MQTT_CLIENT_ID=video-osd-simulator-v1
if "%MQTT_TOPIC%"=="" set MQTT_TOPIC=thing/product/8UUXN4E00A05F5/drc/up
if "%MQTT_QOS%"=="" set MQTT_QOS=0
if "%MQTT_AUTO_RECONNECT%"=="" set MQTT_AUTO_RECONNECT=true
if "%MQTT_CLEAN_SESSION%"=="" set MQTT_CLEAN_SESSION=true
if "%MQTT_DIRECT_CONSUME_ENABLED%"=="" set MQTT_DIRECT_CONSUME_ENABLED=true
if "%WS_SENDER_THREADS%"=="" set WS_SENDER_THREADS=4
if "%WS_DROP_LOG_INTERVAL_MILLIS%"=="" set WS_DROP_LOG_INTERVAL_MILLIS=5000
if "%WS_SEND_SLOW_THRESHOLD_MILLIS%"=="" set WS_SEND_SLOW_THRESHOLD_MILLIS=1000

set JAVA_OPTS=%JAVA_OPTS% --server.port=%SERVER_PORT%
set JAVA_OPTS=%JAVA_OPTS% --simulator.osd.enabled=true
set JAVA_OPTS=%JAVA_OPTS% --simulator.osd.websocket-path=/ws/osd
set JAVA_OPTS=%JAVA_OPTS% --simulator.osd.ws-sender-threads=%WS_SENDER_THREADS%
set JAVA_OPTS=%JAVA_OPTS% --simulator.osd.ws-drop-log-interval-millis=%WS_DROP_LOG_INTERVAL_MILLIS%
set JAVA_OPTS=%JAVA_OPTS% --simulator.osd.ws-send-slow-threshold-millis=%WS_SEND_SLOW_THRESHOLD_MILLIS%
set JAVA_OPTS=%JAVA_OPTS% --simulator.osd.mqtt.broker-url=%MQTT_BROKER_URL%
set JAVA_OPTS=%JAVA_OPTS% --simulator.osd.mqtt.client-id=%MQTT_CLIENT_ID%
set JAVA_OPTS=%JAVA_OPTS% --simulator.osd.mqtt.topic=%MQTT_TOPIC%
set JAVA_OPTS=%JAVA_OPTS% --simulator.osd.mqtt.qos=%MQTT_QOS%
set JAVA_OPTS=%JAVA_OPTS% --simulator.osd.mqtt.auto-reconnect=%MQTT_AUTO_RECONNECT%
set JAVA_OPTS=%JAVA_OPTS% --simulator.osd.mqtt.clean-session=%MQTT_CLEAN_SESSION%
set JAVA_OPTS=%JAVA_OPTS% --simulator.osd.mqtt.direct-consume-enabled=%MQTT_DIRECT_CONSUME_ENABLED%

if not "%MQTT_USERNAME%"=="" set JAVA_OPTS=%JAVA_OPTS% --simulator.osd.mqtt.username=%MQTT_USERNAME%
if not "%MQTT_PASSWORD%"=="" set JAVA_OPTS=%JAVA_OPTS% --simulator.osd.mqtt.password=%MQTT_PASSWORD%

echo [INFO] Starting video-osd-simulator-v1
echo [INFO] Broker=%MQTT_BROKER_URL%
echo [INFO] Topic=%MQTT_TOPIC%
echo [INFO] Port=%SERVER_PORT%
echo [INFO] WsSenderThreads=%WS_SENDER_THREADS%

"%JAVA_CMD%" -jar "%APP_JAR%" %JAVA_OPTS%
