package com.example.loginsystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 甜品实体类
 * 用于映射数据库中的甜品表，包含甜品的基本信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dessert")
public class Dessert implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 甜品ID，主键，自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 甜品名称
     */
    private String name;

    /**
     * 甜品描述
     */
    private String description;

    /**
     * 甜品图片URL
     */
    private String imageUrl;

    /**
     * 甜品价格
     */
    private BigDecimal price;

    /**
     * 所属分类ID
     */
    private Long categoryId;

    /**
     * 甜品状态（0:停售 1:在售）
     */
    private Integer status;

    /**
     * 排序字段
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}