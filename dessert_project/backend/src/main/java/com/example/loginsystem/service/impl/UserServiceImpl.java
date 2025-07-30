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
     */
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 用户登录方法
     * @param username 用户名
     * @param password 密码
     * @return 生成的JWT token
     */
    @Override
    public String login(String username, String password) {
        // 创建查询条件构造器
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 根据用户名查询用户信息
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);

        // 判断用户是否存在且密码正确
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            // 生成并返回JWT token
            return jwtUtil.generateToken(username);
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
        // 判断用户名是否已存在
        if (userMapper.selectCount(queryWrapper) > 0) {
            // 用户名已存在，返回null
            return null;
        }

        // 创建新用户对象
        User user = new User();
        user.setUsername(username);
        // 对密码进行加密存储
        user.setPassword(passwordEncoder.encode(password));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 插入用户信息到数据库
        userMapper.insert(user);

        // 返回注册成功的用户信息
        return user;
    }
}