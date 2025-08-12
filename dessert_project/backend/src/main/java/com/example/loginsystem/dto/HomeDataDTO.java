package com.example.loginsystem.dto;

import com.example.loginsystem.entity.Banner;
import com.example.loginsystem.entity.Category;
import lombok.Data;

import java.util.List;

/**
 * 首页数据DTO类
 * 用于封装首页需要展示的数据
 */
@Data
public class HomeDataDTO {

    /**
     * 轮播图列表
     */
    private List<Banner> banners;

    /**
     * 甜品系列分类列表
     */
    private List<Category> dessertSeries;

    /**
     * 其他分类列表
     */
    private List<Category> otherCategories;
}