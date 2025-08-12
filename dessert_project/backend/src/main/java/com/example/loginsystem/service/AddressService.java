package com.example.loginsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.loginsystem.entity.Address;

import java.util.List;

/**
 * 地址Service接口
 * 继承IService接口，获得基本的CRUD操作方法
 * 定义地址相关的业务逻辑方法
 */
public interface AddressService extends IService<Address> {

    /**
     * 根据用户ID获取地址列表
     * @param userId 用户ID
     * @return 地址列表
     */
    List<Address> getAddressesByUserId(Long userId);

    /**
     * 设置默认地址
     * @param userId 用户ID
     * @param addressId 地址ID
     * @return 是否设置成功
     */
    boolean setDefaultAddress(Long userId, Long addressId);
}