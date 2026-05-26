package com.cv.i18n.sample.dto;

import com.cv.i18n.annotation.I18nField;

public class OrderItemDTO {

    private String skuName;

    private Integer quantity;

    @I18nField(prefix = "order.tag.")
    private String tag;

    public OrderItemDTO(String skuName, Integer quantity, String tag) {
        this.skuName = skuName;
        this.quantity = quantity;
        this.tag = tag;
    }

    public String getSkuName() {
        return skuName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getTag() {
        return tag;
    }
}
