package com.example.loginsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.loginsystem.entity.Dessert;

import java.util.List;

/**
 * 甜品Service接口
 * 继承IService接口，获得基本的CRUD操作方法
 * 定义甜品相关的业务逻辑方法
 */
public interface DessertService extends IService<Dessert> {

    /**
     * 根据分类ID获取甜品列表
     * @param categoryId 分类ID
     * @return 甜品列表
     */
    List<Dessert> getDessertsByCategoryId(Long categoryId);

    /**
     * 获取所有在售甜品列表
     * @return 甜品列表
     */
    List<Dessert> getAllAvailableDesserts();

    /**
     * 根据甜品ID获取甜品详情
     * @param id 甜品ID
     * @return 甜品详情
     */
    Dessert getDessertDetail(Long id);
}