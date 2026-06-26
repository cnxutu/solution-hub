package com.cv.simulator.videoosd.core.osd;

import com.alibaba.excel.EasyExcel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OsdExcelImporter {

    public List<DeviceTelemetryRecord> importExcel(InputStream inputStream) {
        List<Map<Integer, String>> rows = EasyExcel.read(inputStream).sheet().doReadSync();
        if (rows.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Integer, String> head = rows.get(0);
        List<DeviceTelemetryRecord> records = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            records.add(toRecord(toNamedRow(head, rows.get(i)), i + 1));
        }
        return records;
    }

    public List<DeviceTelemetryRecord> importCsvLike(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return new ArrayList<>();
            }
            List<String> headers = parseCsvLine(headerLine);
            List<DeviceTelemetryRecord> records = new ArrayList<>();
            String line;
            int rowNumber = 1;
            while ((line = reader.readLine()) != null) {
                rowNumber++;
                List<String> values = parseCsvLine(line);
                Map<String, String> row = new LinkedHashMap<>();
                for (int i = 0; i < headers.size(); i++) {
                    row.put(headers.get(i), i < values.size() ? values.get(i) : null);
                }
                records.add(toRecord(row, rowNumber));
            }
            return records;
        } catch (IOException e) {
            throw new IllegalStateException("failed to import osd csv content", e);
        }
    }

    private Map<String, String> toNamedRow(Map<Integer, String> head, Map<Integer, String> row) {
        Map<String, String> result = new LinkedHashMap<>();
        for (Map.Entry<Integer, String> entry : head.entrySet()) {
            result.put(entry.getValue(), row.get(entry.getKey()));
        }
        return result;
    }

    private DeviceTelemetryRecord toRecord(Map<String, String> row, int rowNumber) {
        String deviceSn = text(row.get("device_sn"));
        if (deviceSn == null) {
            throw new IllegalArgumentException("device_sn is required at row " + rowNumber);
        }
        DeviceTelemetryRecord record = new DeviceTelemetryRecord();
        record.setDeviceSn(deviceSn);
        record.setTaskId(toLong(row.get("task_id")));
        record.setLatitude(toBigDecimal(row.get("latitude")));
        record.setLongitude(toBigDecimal(row.get("longitude")));
        record.setRawJson(text(row.get("raw_json")));
        return record;
    }

    private List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (quoted && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (ch == ',' && !quoted) {
                result.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        result.add(current.toString());
        return result;
    }

    private String text(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private Long toLong(String value) {
        String text = text(value);
        return text == null ? null : Long.valueOf(text);
    }

    private BigDecimal toBigDecimal(String value) {
        String text = text(value);
        return text == null ? null : new BigDecimal(text);
    }
}
