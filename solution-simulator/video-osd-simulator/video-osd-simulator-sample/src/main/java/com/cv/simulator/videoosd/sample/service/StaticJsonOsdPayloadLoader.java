package com.cv.simulator.videoosd.sample.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class StaticJsonOsdPayloadLoader {

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public StaticJsonOsdPayloadLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public List<String> load(String location) {
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("static osd json location must not be blank");
        }
        Resource resource = resourceLoader.getResource(location.trim());
        if (!resource.exists()) {
            throw new IllegalArgumentException("static osd json resource does not exist: " + location.trim());
        }
        try (InputStream inputStream = resource.getInputStream()) {
            JsonNode root = objectMapper.readTree(inputStream);
            if (root == null || root.isNull()) {
                return List.of();
            }
            if (root.isObject()) {
                return List.of(objectMapper.writeValueAsString(root));
            }
            if (!root.isArray()) {
                throw new IllegalArgumentException("static osd json must be an object or array");
            }
            List<String> payloads = new ArrayList<>();
            for (JsonNode item : root) {
                if (!item.isObject()) {
                    throw new IllegalArgumentException("static osd json must contain object payloads");
                }
                payloads.add(objectMapper.writeValueAsString(item));
            }
            return payloads;
        } catch (IOException e) {
            throw new IllegalStateException("failed to load static osd json: " + location.trim(), e);
        }
    }
}
