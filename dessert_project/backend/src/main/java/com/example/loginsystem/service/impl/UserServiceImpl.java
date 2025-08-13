package com.example.loginsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.loginsystem.entity.User;
import com.example.loginsystem.mapper.UserMapper;
import com.example.loginsystem.service.UserService;
import com.example.loginsystem.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户Service实现类
 * 继承ServiceImpl并实现UserService接口
 * 实现用户相关的业务逻辑方法
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * BCrypt密码加密器
     * 用于对用户密码进行加密和验证
     * 注意：BCrypt是单向加密算法，无法解密，只能验证密码是否匹配
     */
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 用户登录方法
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户对象，如果登录失败则返回null
     */
    @Override
    public User login(String username, String password) {
        // 创建查询条件构造器
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 根据用户名查询用户信息
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);

        // 判断用户是否存在且密码正确
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            // 返回用户对象
            return user;
        }

        // 用户不存在或密码错误，返回null
        return null;
    }

    /**
     * 用户注册方法
     * @param username 用户名
     * @param password 密码
     * @return 注册成功的用户信息
     */
    @Override
    public User register(String username, String password) {
        // 创建查询条件构造器
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 根据用户名查询用户信息
        queryWrapper.eq("username", username);
        User existingUser = userMapper.selectOne(queryWrapper);

        // 判断用户名是否已存在
        if (existingUser != null) {
            // 用户名已存在，返回null
            return null;
        }

        // 创建新用户对象
        User newUser = new User();
        newUser.setUsername(username);
        // 对密码进行BCrypt加密
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setCreateTime(LocalDateTime.now());
        newUser.setUpdateTime(LocalDateTime.now());

        // 插入新用户到数据库
        userMapper.insert(newUser);

        // 重新查询用户信息（获取自动生成的ID）
        queryWrapper.clear();
        queryWrapper.eq("username", username);
        newUser = userMapper.selectOne(queryWrapper);

        // 返回新用户信息
        return newUser;
    }
    
    /**
     * 重置用户密码（需要验证原密码）
     * @param username 用户名
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return 是否重置成功
     */
    @Override
    public boolean resetPassword(String username, String oldPassword, String newPassword) {
        // 创建查询条件构造器
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 根据用户名查询用户信息
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        
        // 判断用户是否存在且原密码正确
        if (user == null || !passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        
        // 更新数据库中的用户信息
        return userMapper.updateById(user) > 0;
    }
    
    /**
     * 验证密码是否匹配（用于后台验证用户密码）
     * @param rawPassword 明文密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    /**
     * 重要说明：
     * BCryptPasswordEncoder 使用的是单向哈希算法，这是一种安全的密码存储方式。
     * 特点：
     * 1. 不可逆：无法从哈希值还原回原始密码
     * 2. 安全性高：即使数据库泄露，攻击者也无法直接获取用户密码
     * 3. 抗彩虹表：每次加密相同密码都会产生不同的哈希值（因为盐值随机生成）
     * 
     * 如果需要验证密码，应该使用 passwordEncoder.matches() 方法进行匹配验证，
     * 而不是尝试解密密码。
     * 
     * 示例用法：
     * boolean isMatch = verifyPassword("用户输入的密码", "数据库中存储的加密密码");
     */
}