package com.example.loginsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.loginsystem.entity.User;

/**
 * 用户Service接口
 * 继承IService接口，获得基本的CRUD操作方法
 * 定义用户相关的业务逻辑方法
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录方法
     * @param username 用户名
     * @param password 密码
     * @return 生成的JWT token
     */
    String login(String username, String password);

    /**
     * 用户注册方法
     * @param username 用户名
     * @param password 密码
     * @return 注册成功的用户信息
     */
    User register(String username, String password);
    
    /**
     * 重置用户密码
     * @param username 用户名
     * @param newPassword 新密码
     * @return 是否重置成功
     */
    boolean resetPassword(String username, String newPassword);

}