package com.example.loginsystem.dto;

import com.example.loginsystem.entity.Order;
import com.example.loginsystem.entity.OrderItem;
import lombok.Data;

import java.util.List;

/**
 * 订单DTO类
 * 用于封装订单数据
 */
@Data
public class OrderDTO {

    /**
     * 订单基本信息
     */
    private Order order;

    /**
     * 订单项列表
     */
    private List<OrderItem> orderItems;
}