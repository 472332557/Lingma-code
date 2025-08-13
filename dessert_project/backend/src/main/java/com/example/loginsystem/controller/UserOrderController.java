package com.example.loginsystem.controller;

import com.example.loginsystem.common.Result;
import com.example.loginsystem.dto.PaymentRequestDTO;
import com.example.loginsystem.entity.Order;
import com.example.loginsystem.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户订单控制器
 * 处理用户订单相关请求
 */
@RestController
@RequestMapping("/api/user/order")
public class UserOrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 获取用户订单列表接口
     * @return 订单列表
     */
    @GetMapping("/list")
    public Result<List<Order>> getUserOrders(HttpServletRequest request) {
        // 从请求属性中获取用户ID
        Long userId = (Long) request.getAttribute("userId");

        // 获取用户订单列表
        List<Order> orders = orderService.getOrdersByUserId(userId);

        // 返回结果
        return Result.success(orders);
    }

    /**
     * 创建订单接口
     * @param paymentRequest 支付请求参数
     * @return 订单创建结果
     */
    @PostMapping("/create")
    public Result<Map<String, Object>> createOrder(@RequestBody PaymentRequestDTO paymentRequest, HttpServletRequest request) {
        try {
            // 从请求属性中获取用户ID
            Long userId = (Long) request.getAttribute("userId");

            // 创建订单
            Order order = orderService.createOrder(userId, paymentRequest);

            // 创建返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("orderId", order.getId());
            data.put("orderNumber", order.getOrderNumber());
            data.put("totalAmount", order.getTotalAmount());
            data.put("message", "订单创建成功");

            // 返回结果
            return Result.success(data);
        } catch (Exception e) {
            // 返回错误信息
            return Result.error(e.getMessage());
        }
    }

    /**
     * 模拟支付接口
     * @param orderId 订单ID
     * @return 支付结果
     */
    @PostMapping("/pay/{orderId}")
    public Result<Map<String, Object>> payOrder(@PathVariable Long orderId, HttpServletRequest request) {
        try {
            // 从请求属性中获取用户ID
            Long userId = (Long) request.getAttribute("userId");

            // 获取订单信息
            Order order = orderService.getById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }

            // 检查订单是否属于当前用户
            if (!order.getUserId().equals(userId)) {
                return Result.error("无权限操作该订单");
            }

            // 更新订单状态为已支付
            order.setStatus(1); // 已支付状态
            order.setUpdateTime(java.time.LocalDateTime.now());
            orderService.updateById(order);

            // 创建返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("orderId", orderId);
            data.put("message", "支付成功");

            // 返回结果
            return Result.success(data);
        } catch (Exception e) {
            // 返回错误信息
            return Result.error("支付失败: " + e.getMessage());
        }
    }
}