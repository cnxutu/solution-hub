INSERT INTO sim_stream_task(task_name, video_directory, file_pattern, zlm_host, zlm_port, app, stream, protocol, loop_enabled, ffmpeg_options, status, create_time, update_time, is_deleted)
VALUES ('demo-rtmp-task', './sample-videos', '*.mp4,*.flv', '127.0.0.1', 1935, 'live', 'drone001', 'RTMP', TRUE, '-re', 'CREATED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO device_telemetry_sub(task_id, device_sn, latitude, longitude, mode_code, raw_json, publish_time, create_time, update_time, is_deleted)
VALUES (1, 'dock-001', 30.1234567890123, 120.1234567890123, 5, '{"battery":{"percent":90},"custom":"demo"}', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);
