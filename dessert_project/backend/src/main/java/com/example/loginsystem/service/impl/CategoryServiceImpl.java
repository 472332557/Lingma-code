package com.example.loginsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.loginsystem.entity.Category;
import com.example.loginsystem.mapper.CategoryMapper;
import com.example.loginsystem.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 甜品分类Service实现类
 * 继承ServiceImpl并实现CategoryService接口
 * 实现甜品分类相关的业务逻辑方法
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 根据类型获取分类列表
     * @param type 分类类型
     * @return 分类列表
     */
    @Override
    public List<Category> getCategoryByType(Integer type) {
        // 创建查询条件构造器
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        // 添加类型条件
        queryWrapper.eq("type", type);
        // 按排序字段升序排列
        queryWrapper.orderByAsc("sort");
        // 查询并返回结果
        return categoryMapper.selectList(queryWrapper);
    }

    /**
     * 获取所有分类列表
     * @return 分类列表
     */
    @Override
    public List<Category> getAllCategories() {
        // 创建查询条件构造器
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        // 按排序字段升序排列
        queryWrapper.orderByAsc("sort");
        // 查询并返回结果
        return categoryMapper.selectList(queryWrapper);
    }
}