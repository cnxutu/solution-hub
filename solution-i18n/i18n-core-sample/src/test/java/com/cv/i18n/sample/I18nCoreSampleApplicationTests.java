package com.cv.i18n.sample;

import com.cv.i18n.context.I18nContextHolder;
import com.cv.i18n.processor.I18nValueProcessor;
import com.cv.i18n.sample.dto.OrderDetailDTO;
import com.cv.i18n.sample.dto.OrderItemDTO;
import com.cv.i18n.sample.enums.OrderStatusEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class I18nCoreSampleApplicationTests {

    @Autowired
    private I18nValueProcessor valueProcessor;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        I18nContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
    }

    @AfterEach
    void tearDown() {
        I18nContextHolder.clear();
    }

    @Test
    @DisplayName("测试枚举自动翻译 - 中文")
    void testEnumTranslationChinese() {
        I18nContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
        
        OrderStatusEnum status = OrderStatusEnum.PAID;
        Object result = valueProcessor.process(status);
        
        assertEquals("已支付", result);
    }

    @Test
    @DisplayName("测试枚举自动翻译 - 英文")
    void testEnumTranslationEnglish() {
        I18nContextHolder.setLocale(Locale.ENGLISH);
        
        OrderStatusEnum status = OrderStatusEnum.PAID;
        Object result = valueProcessor.process(status);
        
        assertEquals("Paid", result);
    }

    @Test
    @DisplayName("测试@I18nField注解翻译 - 带prefix")
    void testI18nFieldWithPrefix() {
        I18nContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
        
        Map<String, Object> testBean = new LinkedHashMap<>();
        testBean.put("statusCode", "paid");
        
        // 模拟DTO字段
        class TestDTO {
            @com.cv.i18n.annotation.I18nField(prefix = "order.status.")
            private String statusCode;

            public TestDTO(String statusCode) {
                this.statusCode = statusCode;
            }

            public String getStatusCode() {
                return statusCode;
            }
        }
        
        TestDTO dto = new TestDTO("paid");
        Object result = valueProcessor.process(dto);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> resultMap = (Map<String, Object>) result;
        
        assertEquals("paid", resultMap.get("statusCode"));
        assertEquals("已支付", resultMap.get("statusCodeText"));
    }

    @Test
    @DisplayName("测试@I18nField注解翻译 - 动态参数")
    void testI18nFieldWithArgs() {
        I18nContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
        
        class TestDTO {
            @com.cv.i18n.annotation.I18nField(value = "order.summary", target = "summary", args = {"customerName", "amount"})
            private String customerName;
            
            private BigDecimal amount;

            public TestDTO(String customerName, BigDecimal amount) {
                this.customerName = customerName;
                this.amount = amount;
            }

            public String getCustomerName() {
                return customerName;
            }

            public BigDecimal getAmount() {
                return amount;
            }
        }
        
        TestDTO dto = new TestDTO("Tom", new BigDecimal("128.50"));
        Object result = valueProcessor.process(dto);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> resultMap = (Map<String, Object>) result;
        
        assertEquals("Tom", resultMap.get("customerName"));
        assertEquals("Tom 已支付 128.50 元", resultMap.get("summary"));
    }

    @Test
    @DisplayName("测试List字段翻译")
    void testListTranslation() {
        I18nContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
        
        class TestDTO {
            @com.cv.i18n.annotation.I18nField(prefix = "order.status.", target = "statusNameList")
            private List<String> statusList;

            public TestDTO(List<String> statusList) {
                this.statusList = statusList;
            }

            public List<String> getStatusList() {
                return statusList;
            }
        }
        
        TestDTO dto = new TestDTO(Arrays.asList("paid", "shipped"));
        Object result = valueProcessor.process(dto);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> resultMap = (Map<String, Object>) result;
        
        @SuppressWarnings("unchecked")
        List<String> translatedList = (List<String>) resultMap.get("statusNameList");
        
        assertEquals(2, translatedList.size());
        assertEquals("已支付", translatedList.get(0));
        assertEquals("已发货", translatedList.get(1));
    }

    @Test
    @DisplayName("测试Map字段翻译")
    void testMapTranslation() {
        I18nContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
        
        class TestDTO {
            @com.cv.i18n.annotation.I18nField(prefix = "order.tag.", target = "tagNameMap")
            private Map<String, String> tags;

            public TestDTO(Map<String, String> tags) {
                this.tags = tags;
            }

            public Map<String, String> getTags() {
                return tags;
            }
        }
        
        Map<String, String> tags = new LinkedHashMap<>();
        tags.put("priority", "urgent");
        tags.put("channel", "online");
        
        TestDTO dto = new TestDTO(tags);
        Object result = valueProcessor.process(dto);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> resultMap = (Map<String, Object>) result;
        
        @SuppressWarnings("unchecked")
        Map<String, Object> translatedMap = (Map<String, Object>) resultMap.get("tagNameMap");
        
        assertEquals("紧急", translatedMap.get("priority"));
        assertEquals("线上", translatedMap.get("channel"));
    }

    @Test
    @DisplayName("测试嵌套对象翻译")
    void testNestedObjectTranslation() {
        I18nContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
        
        OrderItemDTO item = new OrderItemDTO("iPhone Case", 2, "normal");
        Object result = valueProcessor.process(item);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> resultMap = (Map<String, Object>) result;
        
        assertEquals("iPhone Case", resultMap.get("skuName"));
        assertEquals(2, resultMap.get("quantity"));
        assertEquals("normal", resultMap.get("tag"));
        assertEquals("普通", resultMap.get("tagText"));
    }

    @Test
    @DisplayName("测试完整订单DTO翻译")
    void testOrderDetailTranslation() {
        I18nContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
        
        Map<String, String> tagMap = new LinkedHashMap<>();
        tagMap.put("priority", "urgent");
        tagMap.put("channel", "online");
        
        OrderDetailDTO orderDetailDTO = new OrderDetailDTO(
                "SO20260526001",
                OrderStatusEnum.PAID,
                "paid",
                "Tom",
                new BigDecimal("128.50"),
                Arrays.asList("paid", "shipped"),
                tagMap,
                Arrays.asList(
                        new OrderItemDTO("iPhone Case", 2, "normal"),
                        new OrderItemDTO("USB-C Cable", 1, "gift")
                )
        );
        
        Object result = valueProcessor.process(orderDetailDTO);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> resultMap = (Map<String, Object>) result;
        
        assertEquals("SO20260526001", resultMap.get("orderNo"));
        assertEquals("已支付", resultMap.get("status"));
        assertEquals("paid", resultMap.get("statusCode"));
        assertEquals("已支付", resultMap.get("statusCodeText"));
        assertEquals("Tom", resultMap.get("customerName"));
        assertEquals(new BigDecimal("128.50"), resultMap.get("amount"));
        
        @SuppressWarnings("unchecked")
        List<String> statusNameList = (List<String>) resultMap.get("statusNameList");
        assertEquals("已支付", statusNameList.get(0));
        assertEquals("已发货", statusNameList.get(1));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> tagNameMap = (Map<String, Object>) resultMap.get("tagNameMap");
        assertEquals("紧急", tagNameMap.get("priority"));
        assertEquals("线上", tagNameMap.get("channel"));
        
        assertEquals("Tom 已支付 128.50 元", resultMap.get("summary"));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) resultMap.get("items");
        assertEquals(2, items.size());
        assertEquals("普通", items.get(0).get("tagText"));
        assertEquals("赠品", items.get(1).get("tagText"));
    }

    @Test
    @DisplayName("测试英文环境下的完整翻译")
    void testOrderDetailTranslationEnglish() {
        I18nContextHolder.setLocale(Locale.ENGLISH);
        
        Map<String, String> tagMap = new LinkedHashMap<>();
        tagMap.put("priority", "urgent");
        tagMap.put("channel", "online");
        
        OrderDetailDTO orderDetailDTO = new OrderDetailDTO(
                "SO20260526001",
                OrderStatusEnum.PAID,
                "paid",
                "Tom",
                new BigDecimal("128.50"),
                Arrays.asList("paid", "shipped"),
                tagMap,
                Arrays.asList(
                        new OrderItemDTO("iPhone Case", 2, "normal"),
                        new OrderItemDTO("USB-C Cable", 1, "gift")
                )
        );
        
        Object result = valueProcessor.process(orderDetailDTO);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> resultMap = (Map<String, Object>) result;
        
        assertEquals("Paid", resultMap.get("status"));
        assertEquals("Paid", resultMap.get("statusCodeText"));
        assertEquals("Tom paid 128.50", resultMap.get("summary"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> tagNameMap = (Map<String, Object>) resultMap.get("tagNameMap");
        assertEquals("Urgent", tagNameMap.get("priority"));
        assertEquals("Online", tagNameMap.get("channel"));
    }

    @Test
    @DisplayName("测试replace属性 - 直接替换原字段")
    void testReplaceProperty() {
        I18nContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
        
        class TestDTO {
            @com.cv.i18n.annotation.I18nField(prefix = "order.status.", replace = true)
            private String statusCode;

            public TestDTO(String statusCode) {
                this.statusCode = statusCode;
            }

            public String getStatusCode() {
                return statusCode;
            }
        }
        
        TestDTO dto = new TestDTO("paid");
        Object result = valueProcessor.process(dto);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> resultMap = (Map<String, Object>) result;
        
        assertEquals("已支付", resultMap.get("statusCode"));
        assertNull(resultMap.get("statusCodeText"));
    }
}