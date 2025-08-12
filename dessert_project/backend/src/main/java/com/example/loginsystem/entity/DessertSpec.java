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
 * 甜品规格实体类
 * 用于映射数据库中的甜品规格表，包含甜品的不同规格信息（如尺寸、口味等）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dessert_spec")
public class DessertSpec implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 规格ID，主键，自增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 甜品ID
     */
    private Long dessertId;

    /**
     * 规格名称（如4寸、6寸、草莓味等）
     */
    private String name;

    /**
     * 规格价格
     */
    private BigDecimal price;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}