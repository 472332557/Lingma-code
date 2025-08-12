package com.example.loginsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.loginsystem.entity.DessertSpec;

import java.util.List;

/**
 * 甜品规格Service接口
 * 继承IService接口，获得基本的CRUD操作方法
 * 定义甜品规格相关的业务逻辑方法
 */
public interface DessertSpecService extends IService<DessertSpec> {

    /**
     * 根据甜品ID获取规格列表
     * @param dessertId 甜品ID
     * @return 规格列表
     */
    List<DessertSpec> getSpecsByDessertId(Long dessertId);
}