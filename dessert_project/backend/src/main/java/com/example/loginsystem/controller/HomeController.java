package com.example.loginsystem.controller;

import com.example.loginsystem.common.Result;
import com.example.loginsystem.dto.DessertListDTO;
import com.example.loginsystem.dto.HomeDataDTO;
import com.example.loginsystem.entity.Category;
import com.example.loginsystem.entity.Dessert;
import com.example.loginsystem.service.BannerService;
import com.example.loginsystem.service.CategoryService;
import com.example.loginsystem.service.DessertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 首页控制器
 * 处理首页相关请求
 */
@RestController
@RequestMapping("/api/home")
public class HomeController {

    @Autowired
    private BannerService bannerService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DessertService dessertService;

    /**
     * 获取首页数据接口
     * @return 首页数据
     */
    @GetMapping("/data")
    public Result<HomeDataDTO> getHomeData(HttpServletRequest request) {
        // 从请求属性中获取用户ID（如果需要）
        Long userId = (Long) request.getAttribute("userId");

        // 创建首页数据对象
        HomeDataDTO homeData = new HomeDataDTO();

        // 获取轮播图数据
        homeData.setBanners(bannerService.getEnabledBanners());

        // 获取甜品系列分类（type=1）
        homeData.setDessertSeries(categoryService.getCategoryByType(1));

        // 获取其他分类（type=2）
        homeData.setOtherCategories(categoryService.getCategoryByType(2));

        // 返回结果
        return Result.success(homeData);
    }

    /**
     * 根据分类ID获取甜品列表接口
     * @param categoryId 分类ID
     * @return 甜品列表
     */
    @GetMapping("/desserts")
    public Result<DessertListDTO> getDessertsByCategory(@RequestParam Long categoryId, HttpServletRequest request) {
        // 从请求属性中获取用户ID（如果需要）
        Long userId = (Long) request.getAttribute("userId");
        
        // 获取分类信息
        Category category = categoryService.getById(categoryId);
        if (category == null) {
            return Result.error("分类不存在");
        }

        // 获取甜品列表
        List<Dessert> desserts = dessertService.getDessertsByCategoryId(categoryId);

        // 创建甜品列表DTO
        DessertListDTO dessertListDTO = new DessertListDTO();
        dessertListDTO.setCategoryId(categoryId);
        dessertListDTO.setCategoryName(category.getName());
        dessertListDTO.setDesserts(desserts);

        // 返回结果
        return Result.success(dessertListDTO);
    }

    /**
     * 获取所有甜品系列列表接口
     * @return 甜品系列列表
     */
    @GetMapping("/all-series")
    public Result<List<DessertListDTO>> getAllSeries(HttpServletRequest request) {
        // 从请求属性中获取用户ID（如果需要）
        Long userId = (Long) request.getAttribute("userId");
        
        // 获取所有甜品系列分类（type=1）
        List<Category> categories = categoryService.getCategoryByType(1);

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
}