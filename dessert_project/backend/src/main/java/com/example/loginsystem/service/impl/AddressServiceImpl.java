package com.example.loginsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.loginsystem.entity.Address;
import com.example.loginsystem.mapper.AddressMapper;
import com.example.loginsystem.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 地Service实现类
 * 继承ServiceImpl并实现AddressService接口
 * 实现地址相关的业务逻辑方法
 */
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    /**
     * 根据用户ID获取地址列表
     * @param userId 用户ID
     * @return 地址列表
     */
    @Override
    public List<Address> getAddressesByUserId(Long userId) {
        // 创建查询条件构造器
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        // 添加用户ID条件
        queryWrapper.eq("user_id", userId);
        // 按创建时间降序排列
        queryWrapper.orderByDesc("create_time");
        // 查询并返回结果
        return addressMapper.selectList(queryWrapper);
    }

    /**
     * 设置默认地址
     * @param userId 用户ID
     * @param addressId 地址ID
     * @return 是否设置成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setDefaultAddress(Long userId, Long addressId) {
        // 先将该用户的所有地址设置为非默认
        Address addressToUpdate = new Address();
        addressToUpdate.setIsDefault(0);
        
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        addressMapper.update(addressToUpdate, queryWrapper);
        
        // 再将指定地址设置为默认地址
        Address defaultAddress = new Address();
        defaultAddress.setId(addressId);
        defaultAddress.setIsDefault(1);
        
        QueryWrapper<Address> updateWrapper = new QueryWrapper<>();
        updateWrapper.eq("id", addressId);
        updateWrapper.eq("user_id", userId);
        
        return addressMapper.update(defaultAddress, updateWrapper) > 0;
    }
}