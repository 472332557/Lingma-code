package com.example.loginsystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.loginsystem.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项Mapper接口
 * 继承BaseMapper接口，获得基本的CRUD操作方法
 * 通过@Mapper注解标记为MyBatis Mapper接口
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {

}