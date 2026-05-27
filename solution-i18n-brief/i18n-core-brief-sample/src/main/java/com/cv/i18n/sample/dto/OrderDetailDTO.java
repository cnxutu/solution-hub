package com.cv.i18n.sample.dto;

import com.cv.i18n.annotation.I18nField;
import com.cv.i18n.sample.enums.OrderStatusEnum;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class OrderDetailDTO {

    private String orderNo;

    private OrderStatusEnum status;

    @I18nField(prefix = "order.status.")
    private String statusCode;

    @I18nField(value = "order.summary", target = "summary", args = {"customerName", "amount"})
    private String customerName;

    private BigDecimal amount;

    @I18nField(prefix = "order.status.", target = "statusNameList")
    private List<String> statusList;

    @I18nField(prefix = "order.tag.", target = "tagNameMap")
    private Map<String, String> tags;

    private List<OrderItemDTO> items;

    public OrderDetailDTO(String orderNo, OrderStatusEnum status, String statusCode, String customerName,
                          BigDecimal amount, List<String> statusList, Map<String, String> tags,
                          List<OrderItemDTO> items) {
        this.orderNo = orderNo;
        this.status = status;
        this.statusCode = statusCode;
        this.customerName = customerName;
        this.amount = amount;
        this.statusList = statusList;
        this.tags = tags;
        this.items = items;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public OrderStatusEnum getStatus() {
        return status;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public List<String> getStatusList() {
        return statusList;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }
}
