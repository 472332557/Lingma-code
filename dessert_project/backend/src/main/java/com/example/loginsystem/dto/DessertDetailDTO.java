package com.example.loginsystem.dto;

import com.example.loginsystem.entity.Dessert;
import com.example.loginsystem.entity.DessertSpec;
import lombok.Data;

import java.util.List;

/**
 * 甜品详情DTO类
 * 用于封装甜品详情数据
 */
@Data
public class DessertDetailDTO {

    /**
     * 甜品基本信息
     */
    private Dessert dessert;

    /**
     * 甜品规格列表
     */
    private List<DessertSpec> specs;
}