package com.cv.i18n.sample.controller;

import com.cv.i18n.sample.I18nCoreSampleApplication;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = I18nCoreSampleApplication.class)
@AutoConfigureMockMvc
class I18nDemoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("测试中文语言头 - X-Language: zh-CN")
    void testChineseLanguageHeader() throws Exception {
        MvcResult result = mockMvc.perform(get("/i18n/demo/order")
                        .header("X-Language", "zh-CN"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        assertEquals("SO20260526001", jsonNode.get("orderNo").asText());
        assertEquals("已支付", jsonNode.get("statusCodeText").asText());
        assertEquals("已支付", jsonNode.get("status").asText());
        assertEquals("Tom 已支付 128.50 元", jsonNode.get("summary").asText());

        JsonNode statusNameList = jsonNode.get("statusNameList");
        assertEquals("已支付", statusNameList.get(0).asText());
        assertEquals("已发货", statusNameList.get(1).asText());

        JsonNode tagNameMap = jsonNode.get("tagNameMap");
        assertEquals("紧急", tagNameMap.get("priority").asText());
        assertEquals("线上", tagNameMap.get("channel").asText());

        JsonNode items = jsonNode.get("items");
        assertEquals("普通", items.get(0).get("tagText").asText());
        assertEquals("赠品", items.get(1).get("tagText").asText());
    }

    @Test
    @DisplayName("测试英文语言头 - X-Language: en-US")
    void testEnglishLanguageHeader() throws Exception {
        MvcResult result = mockMvc.perform(get("/i18n/demo/order")
                        .header("X-Language", "en-US"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        assertEquals("SO20260526001", jsonNode.get("orderNo").asText());
        assertEquals("Paid", jsonNode.get("statusCodeText").asText());
        assertEquals("Paid", jsonNode.get("status").asText());
        assertEquals("Tom paid 128.50", jsonNode.get("summary").asText());

        JsonNode statusNameList = jsonNode.get("statusNameList");
        assertEquals("Paid", statusNameList.get(0).asText());
        assertEquals("Shipped", statusNameList.get(1).asText());

        JsonNode tagNameMap = jsonNode.get("tagNameMap");
        assertEquals("Urgent", tagNameMap.get("priority").asText());
        assertEquals("Online", tagNameMap.get("channel").asText());

        JsonNode items = jsonNode.get("items");
        assertEquals("Normal", items.get(0).get("tagText").asText());
        assertEquals("Gift", items.get(1).get("tagText").asText());
    }

    @Test
    @DisplayName("测试Accept-Language头 - 备选语言来源")
    void testAcceptLanguageHeader() throws Exception {
        MvcResult result = mockMvc.perform(get("/i18n/demo/order")
                        .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        assertEquals("已支付", jsonNode.get("statusCodeText").asText());
    }

    @Test
    @DisplayName("测试无语言头 - 使用系统默认语言")
    void testNoLanguageHeader() throws Exception {
        MvcResult result = mockMvc.perform(get("/i18n/demo/order"))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        assertNotNull(jsonNode.get("statusCodeText"));
        assertNotNull(jsonNode.get("status"));
        assertNotNull(jsonNode.get("summary"));
    }
}