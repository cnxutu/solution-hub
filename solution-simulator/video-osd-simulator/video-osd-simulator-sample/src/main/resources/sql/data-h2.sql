INSERT INTO sim_stream_task(task_name, video_directory, video_file_path, file_pattern, zlm_host, zlm_port, app, stream, protocol, webrtc_command_template, loop_enabled, ffmpeg_options, osd_publish_time_start, osd_publish_time_end, status, create_time, update_time, is_deleted)
VALUES ('demo-webrtc-task', './sample-videos', './sample-videos/demo.mp4', '*.mp4,*.flv', '127.0.0.1', 1935, 'live', 'drone001', 'WEBRTC', 'webrtc-pusher --input "{videoFile}" --host {zlmHost} --app {app} --stream {stream}', TRUE, '-re', DATEADD('MINUTE', -5, CURRENT_TIMESTAMP), DATEADD('MINUTE', 5, CURRENT_TIMESTAMP), 'CREATED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

INSERT INTO device_telemetry_sub(task_id, device_sn, latitude, longitude, mode_code, raw_json, publish_time, create_time, update_time, is_deleted)
VALUES (1, 'dock-001', 30.1234567890123, 120.1234567890123, 5, '{"battery":{"percent":90},"custom":"demo"}', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);
