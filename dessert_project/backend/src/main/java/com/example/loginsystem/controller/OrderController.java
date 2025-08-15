package com.example.loginsystem.controller;

import com.example.loginsystem.common.Result;
import com.example.loginsystem.dto.DessertDetailDTO;
import com.example.loginsystem.dto.DessertListDTO;
import com.example.loginsystem.entity.Category;
import com.example.loginsystem.entity.Dessert;
import com.example.loginsystem.entity.Order;
import com.example.loginsystem.service.CategoryService;
import com.example.loginsystem.service.DessertService;
import com.example.loginsystem.service.DessertSpecService;
import com.example.loginsystem.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 点单控制器
 * 处理点单相关请求
 */
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DessertService dessertService;

    @Autowired
    private DessertSpecService dessertSpecService;

    @Autowired
    private OrderService orderService;

    /**
     * 获取所有分类接口
     * @return 分类列表
     */
    @GetMapping("/categories")
    public Result<List<Category>> getAllCategories(HttpServletRequest request) {
        // 从请求属性中获取用户ID（如果需要）
        Long userId = (Long) request.getAttribute("userId");

        // 获取所有分类
        List<Category> categories = categoryService.getAllCategories();

        // 返回结果
        return Result.success(categories);
    }

    /**
     * 根据分类ID获取甜品列表接口
     * @param categoryId 分类ID（可选，默认为null，表示获取所有甜品）
     * @return 甜品列表
     */
    @GetMapping("/desserts")
    public Result<List<DessertListDTO>> getDessertsByCategory(@RequestParam(required = false) Long categoryId, HttpServletRequest request) {
        // 从请求属性中获取用户ID（如果需要）
        Long userId = (Long) request.getAttribute("userId");
        
        List<Category> categories;

        // 如果categoryId为空，则获取所有分类
        if (categoryId == null) {
            categories = categoryService.getAllCategories();
        } else {
            // 否则只获取指定分类
            Category category = categoryService.getById(categoryId);
            if (category == null) {
                return Result.error("分类不存在");
            }
            // 使用JDK 1.8兼容的方式创建包含单个元素的列表
            categories = new ArrayList<>();
            categories.add(category);
        }

        // 转换为甜品列表DTO
        List<DessertListDTO> dessertLists = categories.stream().map(category -> {
            // 创建甜品列表DTO
            DessertListDTO dessertListDTO = new DessertListDTO();
            dessertListDTO.setCategoryId(category.getId());
            dessertListDTO.setCategoryName(category.getName());

            // 获取该分类下的甜品列表
            List<Dessert> desserts = dessertService.getDessertsByCategoryId(category.getId());
            dessertListDTO.setDesserts(desserts);

            return dessertListDTO;
        }).collect(Collectors.toList());

        // 返回结果
        return Result.success(dessertLists);
    }

    /**
     * 查询订单状态接口
     * @param orderId 订单ID
     * @return 订单状态信息
     */
    @GetMapping("/{orderId}/status")
    public Result<Map<String, Object>> getOrderStatus(@PathVariable Long orderId) {
        try {
            // 查询订单
            Order order = orderService.getById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("orderId", order.getId());
            result.put("orderNumber", order.getOrderNumber());
            result.put("status", order.getStatus());
            result.put("statusDesc", getStatusDescription(order.getStatus()));
            result.put("totalAmount", order.getTotalAmount());
            result.put("createTime", order.getCreateTime());

            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询订单状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取订单状态描述
     * @param status 订单状态
     * @return 状态描述
     */
    private String getStatusDescription(Integer status) {
        switch (status) {
            case 0:
                return "待支付";
            case 1:
                return "支付成功";
            case 2:
                return "支付失败";
            case 3:
                return "已取消";
            case 4:
                return "已完成";
            default:
                return "未知状态";
        }
    }

    /**
     * 获取订单详情接口
     * @param orderId 订单ID
     * @return 订单详情
     */
    @GetMapping("/{orderId}")
    public Result<Order> getOrderDetail(@PathVariable Long orderId) {
        try {
            Order order = orderService.getById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }
            return Result.success(order);
        } catch (Exception e) {
            return Result.error("获取订单详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取甜品详情接口
     * @param dessertId 甜品ID
     * @return 甜品详情
     */
    @GetMapping("/dessert-detail")
    public Result<DessertDetailDTO> getDessertDetail(@RequestParam Long dessertId, HttpServletRequest request) {
        // 从请求属性中获取用户ID（如果需要）
        Long userId = (Long) request.getAttribute("userId");
        
        // 获取甜品详情
        Dessert dessert = dessertService.getDessertDetail(dessertId);
        if (dessert == null) {
            return Result.error("甜品不存在或已下架");
        }

        // 获取甜品规格列表
        List<com.example.loginsystem.entity.DessertSpec> specs = dessertSpecService.getSpecsByDessertId(dessertId);

        // 创建甜品详情DTO
        DessertDetailDTO dessertDetailDTO = new DessertDetailDTO();
        dessertDetailDTO.setDessert(dessert);
        dessertDetailDTO.setSpecs(specs);

        // 返回结果
        return Result.success(dessertDetailDTO);
    }
}