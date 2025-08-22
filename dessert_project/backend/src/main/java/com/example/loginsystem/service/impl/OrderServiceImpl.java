package com.example.loginsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.loginsystem.dto.PaymentRequestDTO;
import com.example.loginsystem.entity.*;
import com.example.loginsystem.mapper.*;
import com.example.loginsystem.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 订单Service实现类
 * 继承ServiceImpl并实现OrderService接口
 * 实现订单相关的业务逻辑方法
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    @Autowired
    private DessertMapper dessertMapper;
    
    @Autowired
    private DessertSpecMapper dessertSpecMapper;
    
    @Autowired
    private AddressMapper addressMapper;

    /**
     * 根据用户ID获取订单列表
     * @param userId 用户ID
     * @return 订单列表
     */
    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        // 创建查询条件构造器
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        // 添加用户ID条件
        queryWrapper.eq("user_id", userId);
        // 按创建时间降序排列
        queryWrapper.orderByDesc("create_time");
        // 查询并返回结果
        return orderMapper.selectList(queryWrapper);
    }
    
    /**
     * 创建订单
     * @param userId 用户ID
     * @param paymentRequest 支付请求参数
     * @return 创建的订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(Long userId, PaymentRequestDTO paymentRequest) {
        // 1. 验证商品和规格是否存在，并计算订单总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        // 创建订单项列表
        for (PaymentRequestDTO.OrderItemDTO itemDTO : paymentRequest.getOrderItems()) {
            // 验证甜品是否存在
            Dessert dessert = dessertMapper.selectById(itemDTO.getDessertId());
            if (dessert == null) {
                throw new RuntimeException("甜品不存在，ID: " + itemDTO.getDessertId());
            }
            
            // 获取甜品规格价格
            BigDecimal itemPrice = dessert.getPrice(); // 默认价格
            String specName = "标准"; // 默认规格名
            
            // 如果指定了规格，则使用规格价格
            if (itemDTO.getSpecId() != null) {
                DessertSpec spec = dessertSpecMapper.selectById(itemDTO.getSpecId());
                if (spec == null) {
                    throw new RuntimeException("甜品规格不存在，ID: " + itemDTO.getSpecId());
                }
                itemPrice = spec.getPrice();
                specName = spec.getName();
            }
            
            // 计算小计金额
            BigDecimal subtotal = itemPrice.multiply(new BigDecimal(itemDTO.getQuantity()));
            totalAmount = totalAmount.add(subtotal);
        }
        
        // 2. 获取收货地址
        Address address = addressMapper.selectById(paymentRequest.getAddressId());
        if (address == null) {
            throw new RuntimeException("收货地址不存在，ID: " + paymentRequest.getAddressId());
        }
        
        // 3. 生成订单号
        String orderNumber = "ORDER_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // 4. 创建订单
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderNumber(orderNumber);
        order.setTotalAmount(totalAmount);
        order.setStatus(0); // 待支付状态
        order.setAddress(address.getProvince() + address.getCity() + address.getDistrict() + address.getDetail());
        order.setPhone(address.getPhone());
        order.setRemark(paymentRequest.getRemark());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        // 保存订单
        orderMapper.insert(order);
        
        // 重新查询订单以获取生成的ID
        QueryWrapper<Order> orderQuery = new QueryWrapper<>();
        orderQuery.eq("order_number", orderNumber);
        order = orderMapper.selectOne(orderQuery);
        
        // 5. 创建订单项
        for (PaymentRequestDTO.OrderItemDTO itemDTO : paymentRequest.getOrderItems()) {
            // 获取甜品信息
            Dessert dessert = dessertMapper.selectById(itemDTO.getDessertId());
            
            // 获取甜品规格信息
            DessertSpec spec = null;
            String specName = "标准";
            if (itemDTO.getSpecId() != null) {
                spec = dessertSpecMapper.selectById(itemDTO.getSpecId());
                specName = spec != null ? spec.getName() : "标准";
            }
            
            // 计算单价和小计
            BigDecimal price = spec != null ? spec.getPrice() : dessert.getPrice();
            BigDecimal subtotal = price.multiply(new BigDecimal(itemDTO.getQuantity()));
            
            // 创建订单项
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setDessertId(dessert.getId());
            orderItem.setDessertName(dessert.getName());
            orderItem.setImageUrl(dessert.getImageUrl());
            orderItem.setSpecId(itemDTO.getSpecId());
            orderItem.setSpecName(specName);
            orderItem.setPrice(price);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setSubtotal(subtotal);
            orderItem.setCreateTime(LocalDateTime.now());
            orderItem.setUpdateTime(LocalDateTime.now());
            
            // 保存订单项
            orderItemMapper.insert(orderItem);
        }
        
        return order;
    }
    
    /**
     * 取消订单
     * @param orderId 订单ID
     * @return 是否取消成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(Long orderId) {
        // 查询订单
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return false;
        }
        
        // 只有待支付的订单才能取消
        if (order.getStatus() != 0) {
            return false;
        }
        
        // 更新订单状态为已取消
        order.setStatus(2);
        order.setUpdateTime(LocalDateTime.now());
        
        return orderMapper.updateById(order) > 0;
    }
    
    /**
     * 检查并取消超时未支付的订单
     * @param timeoutMinutes 超时时间（分钟）
     * @return 取消的订单数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancelTimeoutOrders(int timeoutMinutes) {
        // 计算超时时间点
        LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(timeoutMinutes);
        
        // 查询超时未支付的订单
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 0) // 待支付状态
                   .lt("create_time", timeoutTime); // 创建时间小于超时时间点
        
        List<Order> timeoutOrders = orderMapper.selectList(queryWrapper);
        
        if (timeoutOrders.isEmpty()) {
            return 0;
        }
        
        // 批量更新订单状态为已取消
        int canceledCount = 0;
        for (Order order : timeoutOrders) {
            order.setStatus(2); // 已取消
            order.setUpdateTime(LocalDateTime.now());
            if (orderMapper.updateById(order) > 0) {
                canceledCount++;
            }
        }
        
        return canceledCount;
    }
}