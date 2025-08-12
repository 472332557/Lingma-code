package com.example.loginsystem.controller;

import com.example.loginsystem.common.Result;
import com.example.loginsystem.entity.Address;
import com.example.loginsystem.entity.User;
import com.example.loginsystem.service.AddressService;
import com.example.loginsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 个人中心控制器
 * 处理个人中心相关请求
 */
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private AddressService addressService;

    /**
     * 获取用户信息接口
     * @return 用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo() {
        // 这里应该从token中获取用户ID，示例中写死为1
        Long userId = 1L;

        // 获取用户信息
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 清除密码信息，不返回给前端
        user.setPassword(null);

        // 返回结果
        return Result.success(user);
    }

    /**
     * 获取用户地址列表接口
     * @return 地址列表
     */
    @GetMapping("/addresses")
    public Result<List<Address>> getUserAddresses() {
        // 这里应该从token中获取用户ID，示例中写死为1
        Long userId = 1L;

        // 获取用户地址列表
        List<Address> addresses = addressService.getAddressesByUserId(userId);

        // 返回结果
        return Result.success(addresses);
    }

    /**
     * 添加地址接口
     * @param address 地址信息
     * @return 添加结果
     */
    @PostMapping("/address")
    public Result<String> addAddress(@RequestBody Address address) {
        // 这里应该从token中获取用户ID，示例中写死为1
        Long userId = 1L;
        address.setUserId(userId);

        // 设置默认值
        if (address.getIsDefault() == null) {
            address.setIsDefault(0);
        }

        // 插入地址
        boolean saved = addressService.save(address);
        if (saved) {
            return Result.success("地址添加成功");
        } else {
            return Result.error("地址添加失败");
        }
    }

    /**
     * 更新地址接口
     * @param address 地址信息
     * @return 更新结果
     */
    @PutMapping("/address")
    public Result<String> updateAddress(@RequestBody Address address) {
        // 这里应该从token中获取用户ID，示例中写死为1
        Long userId = 1L;

        // 检查地址是否存在且属于当前用户
        Address existingAddress = addressService.getById(address.getId());
        if (existingAddress == null || !existingAddress.getUserId().equals(userId)) {
            return Result.error("地址不存在或无权限修改");
        }

        // 更新地址
        boolean updated = addressService.updateById(address);
        if (updated) {
            return Result.success("地址更新成功");
        } else {
            return Result.error("地址更新失败");
        }
    }

    /**
     * 删除地址接口
     * @param id 地址ID
     * @return 删除结果
     */
    @DeleteMapping("/address/{id}")
    public Result<String> deleteAddress(@PathVariable Long id) {
        // 这里应该从token中获取用户ID，示例中写死为1
        Long userId = 1L;

        // 检查地址是否存在且属于当前用户
        Address existingAddress = addressService.getById(id);
        if (existingAddress == null || !existingAddress.getUserId().equals(userId)) {
            return Result.error("地址不存在或无权限删除");
        }

        // 删除地址
        boolean removed = addressService.removeById(id);
        if (removed) {
            return Result.success("地址删除成功");
        } else {
            return Result.error("地址删除失败");
        }
    }

    /**
     * 设置默认地址接口
     * @param id 地址ID
     * @return 设置结果
     */
    @PutMapping("/address/default/{id}")
    public Result<String> setDefaultAddress(@PathVariable Long id) {
        // 这里应该从token中获取用户ID，示例中写死为1
        Long userId = 1L;

        // 设置默认地址
        boolean success = addressService.setDefaultAddress(userId, id);
        if (success) {
            return Result.success("默认地址设置成功");
        } else {
            return Result.error("默认地址设置失败");
        }
    }
}