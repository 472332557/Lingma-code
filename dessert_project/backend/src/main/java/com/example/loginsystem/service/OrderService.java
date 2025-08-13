package com.example.loginsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.loginsystem.dto.PaymentRequestDTO;
import com.example.loginsystem.entity.Order;

import java.util.List;

/**
 * 订单Service接口
 * 继承IService接口，获得基本的CRUD操作方法
 * 定义订单相关的业务逻辑方法
 */
public interface OrderService extends IService<Order> {

    /**
     * 根据用户ID获取订单列表
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> getOrdersByUserId(Long userId);
    
    /**
     * 创建订单
     * @param userId 用户ID
     * @param paymentRequest 支付请求参数
     * @return 创建的订单
     */
    Order createOrder(Long userId, PaymentRequestDTO paymentRequest);
}