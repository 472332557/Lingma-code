package com.example.loginsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.loginsystem.entity.Category;

import java.util.List;

/**
 * 甜品分类Service接口
 * 继承IService接口，获得基本的CRUD操作方法
 * 定义甜品分类相关的业务逻辑方法
 */
public interface CategoryService extends IService<Category> {

    /**
     * 根据类型获取分类列表
     * @param type 分类类型
     * @return 分类列表
     */
    List<Category> getCategoryByType(Integer type);

    /**
     * 获取所有分类列表
     * @return 分类列表
     */
    List<Category> getAllCategories();
}