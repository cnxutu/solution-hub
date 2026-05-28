package com.cv.i18n.sample.controller;

import com.alibaba.fastjson2.JSON;
import com.cv.i18n.sample.dto.OrderDetailDTO;
import com.cv.i18n.sample.dto.OrderItemDTO;
import com.cv.i18n.sample.enums.OrderStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/i18n/demo")
@Slf4j
public class I18nDemoController {

    @GetMapping("/order")
    public OrderDetailDTO order() {
        Map<String, String> tagMap = new LinkedHashMap<>();
        tagMap.put("priority", "紧急");
        tagMap.put("channel", "线上");
        OrderDetailDTO orderDetailDTO = new OrderDetailDTO(
                "SO20260526001",
                OrderStatusEnum.PAID,
                "已支付",
                "Tom",
                new BigDecimal("128.50"),
                Arrays.asList("已支付", "已发货"),
                tagMap,
                Arrays.asList(
                        new OrderItemDTO("iPhone Case", 2, "普通"),
                        new OrderItemDTO("USB-C Cable", 1, "赠品")
                )
        );
        log.info("orderDetailDTO: {}", JSON.toJSONString(orderDetailDTO));
        return orderDetailDTO;
    }
}