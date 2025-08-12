package com.example.loginsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.loginsystem.entity.Order;
import com.example.loginsystem.mapper.OrderMapper;
import com.example.loginsystem.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单Service实现类
 * 继承ServiceImpl并实现OrderService接口
 * 实现订单相关的业务逻辑方法
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

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
}