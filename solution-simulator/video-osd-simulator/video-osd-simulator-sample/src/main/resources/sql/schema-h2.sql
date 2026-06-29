DROP TABLE IF EXISTS sim_task_run_log;
DROP TABLE IF EXISTS device_telemetry_sub;
DROP TABLE IF EXISTS sim_stream_task;

CREATE TABLE sim_stream_task (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  task_name VARCHAR(128) NOT NULL,
  video_directory VARCHAR(512) NOT NULL,
  video_file_path VARCHAR(512),
  file_pattern VARCHAR(128) DEFAULT '*.mp4,*.flv,*.mov,*.mkv',
  zlm_host VARCHAR(128) NOT NULL,
  zlm_port INT NOT NULL,
  app VARCHAR(64) NOT NULL,
  stream VARCHAR(128) NOT NULL,
  protocol VARCHAR(32) NOT NULL,
  webrtc_command_template VARCHAR(1024),
  loop_enabled BOOLEAN DEFAULT TRUE,
  ffmpeg_options VARCHAR(512),
  osd_publish_time_start TIMESTAMP,
  osd_publish_time_end TIMESTAMP,
  status VARCHAR(32) NOT NULL,
  last_message VARCHAR(1024),
  create_time TIMESTAMP,
  update_time TIMESTAMP,
  is_deleted INT DEFAULT 0
);

CREATE TABLE device_telemetry_sub (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  task_id BIGINT,
  device_sn VARCHAR(32) NOT NULL DEFAULT '',
  attitude_head FLOAT,
  attitude_pitch DOUBLE,
  attitude_roll DOUBLE,
  elevation FLOAT,
  battery CLOB,
  firmware_version VARCHAR(64),
  gear VARCHAR(32),
  height FLOAT,
  home_distance FLOAT,
  horizontal_speed FLOAT,
  latitude DECIMAL(20,13),
  longitude DECIMAL(20,13),
  mode_code INT,
  action_type INT,
  total_flight_distance DOUBLE,
  total_flight_time FLOAT,
  vertical_speed FLOAT,
  wind_direction VARCHAR(32),
  wind_speed FLOAT,
  position_state CLOB,
  payloads CLOB,
  storage CLOB,
  night_lights_state CLOB,
  height_limit INT,
  distance_limit_status CLOB,
  obstacle_avoidance CLOB,
  activation_time BIGINT,
  cameras CLOB,
  rc_lost_action VARCHAR(32),
  rth_altitude INT,
  total_flight_sorties INT,
  exit_wayline_when_rc_lost VARCHAR(32),
  country VARCHAR(64),
  rid_state BOOLEAN,
  is_near_area_limit BOOLEAN,
  is_near_height_limit BOOLEAN,
  maintain_status CLOB,
  track_id VARCHAR(128),
  publish_time TIMESTAMP,
  raw_json CLOB,
  create_time TIMESTAMP,
  update_time TIMESTAMP,
  created_by BIGINT,
  updated_by BIGINT,
  is_deleted INT DEFAULT 0
);

CREATE INDEX idx_dock_sn ON device_telemetry_sub(device_sn);
CREATE INDEX idx_track_id ON device_telemetry_sub(track_id);
CREATE INDEX idx_task_drone_time ON device_telemetry_sub(task_id, create_time, is_deleted);

CREATE TABLE sim_task_run_log (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  task_id BIGINT,
  event_type VARCHAR(64),
  status VARCHAR(32),
  message VARCHAR(1024),
  command_line CLOB,
  sent_count INT DEFAULT 0,
  create_time TIMESTAMP
);
