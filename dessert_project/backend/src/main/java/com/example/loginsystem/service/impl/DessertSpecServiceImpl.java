package com.example.loginsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.loginsystem.entity.DessertSpec;
import com.example.loginsystem.mapper.DessertSpecMapper;
import com.example.loginsystem.service.DessertSpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 甜品规格Service实现类
 * 继承ServiceImpl并实现DessertSpecService接口
 * 实现甜品规格相关的业务逻辑方法
 */
@Service
public class DessertSpecServiceImpl extends ServiceImpl<DessertSpecMapper, DessertSpec> implements DessertSpecService {

    @Autowired
    private DessertSpecMapper dessertSpecMapper;

    /**
     * 根据甜品ID获取规格列表
     * @param dessertId 甜品ID
     * @return 规格列表
     */
    @Override
    public List<DessertSpec> getSpecsByDessertId(Long dessertId) {
        // 创建查询条件构造器
        QueryWrapper<DessertSpec> queryWrapper = new QueryWrapper<>();
        // 添加甜品ID条件
        queryWrapper.eq("dessert_id", dessertId);
        // 查询并返回结果
        return dessertSpecMapper.selectList(queryWrapper);
    }
}