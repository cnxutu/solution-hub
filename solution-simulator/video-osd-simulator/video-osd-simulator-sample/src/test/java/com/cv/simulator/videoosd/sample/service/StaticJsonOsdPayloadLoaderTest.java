package com.cv.simulator.videoosd.sample.service;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StaticJsonOsdPayloadLoaderTest {

    @Test
    void loadsJsonArrayAsPayloadList() {
        StaticJsonOsdPayloadLoader loader = new StaticJsonOsdPayloadLoader(new DefaultResourceLoader());

        List<String> payloads = loader.load("classpath:static/test-osd-array.json");

        assertEquals(List.of(
                "{\"device_sn\":\"drone-001\",\"latitude\":30.1}",
                "{\"device_sn\":\"drone-001\",\"latitude\":30.2}"
        ), payloads);
    }

    @Test
    void loadsSingleJsonObjectAsSinglePayload() {
        StaticJsonOsdPayloadLoader loader = new StaticJsonOsdPayloadLoader(new DefaultResourceLoader());

        List<String> payloads = loader.load("classpath:static/test-osd-single.json");

        assertEquals(List.of("{\"device_sn\":\"drone-002\",\"mode_code\":5}"), payloads);
    }

    @Test
    void rejectsNonObjectJsonArrayElements() {
        StaticJsonOsdPayloadLoader loader = new StaticJsonOsdPayloadLoader(new DefaultResourceLoader());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> loader.load("classpath:static/test-osd-invalid.json"));

        assertEquals("static osd json must contain object payloads", exception.getMessage());
    }
}
