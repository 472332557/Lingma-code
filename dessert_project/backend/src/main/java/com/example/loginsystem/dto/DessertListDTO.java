package com.example.loginsystem.dto;

import com.example.loginsystem.entity.Dessert;
import lombok.Data;

import java.util.List;

/**
 * 甜品列表DTO类
 * 用于封装甜品列表数据
 */
@Data
public class DessertListDTO {

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 甜品列表
     */
    private List<Dessert> desserts;
}