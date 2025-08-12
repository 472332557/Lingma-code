package com.example.loginsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.loginsystem.entity.Dessert;
import com.example.loginsystem.mapper.DessertMapper;
import com.example.loginsystem.service.DessertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 甜品Service实现类
 * 继承ServiceImpl并实现DessertService接口
 * 实现甜品相关的业务逻辑方法
 */
@Service
public class DessertServiceImpl extends ServiceImpl<DessertMapper, Dessert> implements DessertService {

    @Autowired
    private DessertMapper dessertMapper;

    /**
     * 根据分类ID获取甜品列表
     * @param categoryId 分类ID
     * @return 甜品列表
     */
    @Override
    public List<Dessert> getDessertsByCategoryId(Long categoryId) {
        // 创建查询条件构造器
        QueryWrapper<Dessert> queryWrapper = new QueryWrapper<>();
        // 添加分类ID条件
        queryWrapper.eq("category_id", categoryId);
        // 只查询在售甜品
        queryWrapper.eq("status", 1);
        // 按排序字段升序排列
        queryWrapper.orderByAsc("sort");
        // 查询并返回结果
        return dessertMapper.selectList(queryWrapper);
    }

    /**
     * 获取所有在售甜品列表
     * @return 甜品列表
     */
    @Override
    public List<Dessert> getAllAvailableDesserts() {
        // 创建查询条件构造器
        QueryWrapper<Dessert> queryWrapper = new QueryWrapper<>();
        // 只查询在售甜品
        queryWrapper.eq("status", 1);
        // 按排序字段升序排列
        queryWrapper.orderByAsc("sort");
        // 查询并返回结果
        return dessertMapper.selectList(queryWrapper);
    }

    /**
     * 根据甜品ID获取甜品详情
     * @param id 甜品ID
     * @return 甜品详情
     */
    @Override
    public Dessert getDessertDetail(Long id) {
        // 创建查询条件构造器
        QueryWrapper<Dessert> queryWrapper = new QueryWrapper<>();
        // 添加甜品ID条件
        queryWrapper.eq("id", id);
        // 只查询在售甜品
        queryWrapper.eq("status", 1);
        // 查询并返回结果
        return dessertMapper.selectOne(queryWrapper);
    }
}