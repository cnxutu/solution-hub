package com.cv.simulator.videoosd.sample.service;

import com.cv.simulator.videoosd.sample.config.SimulatorProperties;
import com.cv.simulator.videoosd.sample.pojo.sqlite.SqliteOsdSampleRow;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class SqliteOsdSampleLoader {

    private final ResourceLoader resourceLoader;
    private final SimulatorProperties properties;

    public SqliteOsdSampleLoader(ResourceLoader resourceLoader, SimulatorProperties properties) {
        this.resourceLoader = resourceLoader;
        this.properties = properties;
    }

    public List<SqliteOsdSampleRow> loadOrderedRows() {
        SimulatorProperties.Sqlite sqlite = properties.getOsd().getSqlite();
        String location = sqlite.getLocation();
        String tableName = sqlite.getTableName();
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("sqlite osd location must not be blank");
        }
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("sqlite osd tableName must not be blank");
        }
        Resource resource = resourceLoader.getResource(location.trim());
        if (!resource.exists()) {
            throw new IllegalArgumentException("sqlite osd resource does not exist: " + location.trim());
        }
        Path tempFile = null;
        try {
            tempFile = createTempCopy(resource);
            return queryRows(tempFile, tableName.trim());
        } catch (IOException | SQLException e) {
            throw new IllegalStateException("failed to load sqlite osd rows from: " + location.trim(), e);
        } finally {
            deleteQuietly(tempFile);
        }
    }

    private List<SqliteOsdSampleRow> queryRows(Path sqliteFile, String tableName) throws SQLException {
        String sql = "select id, received_at_ms, message_timestamp_ms, dock_sn, drone_sn, attitude_head, latitude, longitude, "
                + "height, speed_x, speed_y, speed_z, gimbal_pitch, gimbal_roll, gimbal_yaw, raw_json "
                + "from " + tableName + " where message_timestamp_ms is not null "
                + "order by message_timestamp_ms asc, id asc";
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + sqliteFile.toAbsolutePath());
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<SqliteOsdSampleRow> rows = new ArrayList<>();
            while (resultSet.next()) {
                SqliteOsdSampleRow row = new SqliteOsdSampleRow();
                row.setId(resultSet.getLong("id"));
                row.setReceivedAtMs(resultSet.getLong("received_at_ms"));
                row.setMessageTimestampMs(resultSet.getLong("message_timestamp_ms"));
                row.setDockSn(resultSet.getString("dock_sn"));
                row.setDroneSn(resultSet.getString("drone_sn"));
                row.setAttitudeHead(getNullableDouble(resultSet, "attitude_head"));
                row.setLatitude(getNullableDouble(resultSet, "latitude"));
                row.setLongitude(getNullableDouble(resultSet, "longitude"));
                row.setHeight(getNullableDouble(resultSet, "height"));
                row.setSpeedX(getNullableDouble(resultSet, "speed_x"));
                row.setSpeedY(getNullableDouble(resultSet, "speed_y"));
                row.setSpeedZ(getNullableDouble(resultSet, "speed_z"));
                row.setGimbalPitch(getNullableDouble(resultSet, "gimbal_pitch"));
                row.setGimbalRoll(getNullableDouble(resultSet, "gimbal_roll"));
                row.setGimbalYaw(getNullableDouble(resultSet, "gimbal_yaw"));
                row.setRawJson(resultSet.getString("raw_json"));
                rows.add(row);
            }
            return rows;
        }
    }

    private Path createTempCopy(Resource resource) throws IOException {
        Path tempFile = Files.createTempFile("video-osd-simulator-", ".sqlite3");
        try (InputStream inputStream = resource.getInputStream()) {
            Files.copy(inputStream, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
        return tempFile;
    }

    private Double getNullableDouble(ResultSet resultSet, String column) throws SQLException {
        double value = resultSet.getDouble(column);
        return resultSet.wasNull() ? null : value;
    }

    private void deleteQuietly(Path path) {
        if (path == null) {
            return;
        }
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }
}
