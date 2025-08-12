package com.example.loginsystem.controller;

import com.example.loginsystem.common.Result;
import com.example.loginsystem.dto.DessertDetailDTO;
import com.example.loginsystem.dto.DessertListDTO;
import com.example.loginsystem.entity.Category;
import com.example.loginsystem.entity.Dessert;
import com.example.loginsystem.service.CategoryService;
import com.example.loginsystem.service.DessertService;
import com.example.loginsystem.service.DessertSpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
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

    /**
     * 获取所有分类接口
     * @return 分类列表
     */
    @GetMapping("/categories")
    public Result<List<Category>> getAllCategories() {
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
    public Result<List<DessertListDTO>> getDessertsByCategory(@RequestParam(required = false) Long categoryId) {
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
//            categories = List.of(category);
            categories = Collections.singletonList(category);
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
     * 获取甜品详情接口
     * @param dessertId 甜品ID
     * @return 甜品详情
     */
    @GetMapping("/dessert-detail")
    public Result<DessertDetailDTO> getDessertDetail(@RequestParam Long dessertId) {
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