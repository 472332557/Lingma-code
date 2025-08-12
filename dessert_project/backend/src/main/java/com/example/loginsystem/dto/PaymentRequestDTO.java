package com.example.loginsystem.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 支付请求DTO类
 * 用于封装支付请求数据
 */
@Data
public class PaymentRequestDTO {

    /**
     * 订单项列表
     */
    private List<OrderItemDTO> orderItems;

    /**
     * 收货地址ID
     */
    private Long addressId;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 订单项DTO类
     */
    @Data
    public static class OrderItemDTO {
        /**
         * 甜品ID
         */
        private Long dessertId;

        /**
         * 甜品规格ID
         */
        private Long specId;

        /**
         * 数量
         */
        private Integer quantity;
    }
}