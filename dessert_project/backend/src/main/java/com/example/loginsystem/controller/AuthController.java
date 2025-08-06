package com.example.loginsystem.controller;

import com.example.loginsystem.common.Result;
import com.example.loginsystem.dto.LoginRequest;
import com.example.loginsystem.dto.RegisterRequest;
import com.example.loginsystem.dto.ResetPasswordRequest;
import com.example.loginsystem.entity.User;
import com.example.loginsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 * 处理登录和注册请求
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录接口
     * @param loginRequest 登录请求参数
     * @return 登录结果，包含token
     */
    @PostMapping("/login")
    public Result<Map<String, String>> login(@Valid @RequestBody LoginRequest loginRequest) {
        // 调用UserService的登录方法
        String token = userService.login(loginRequest.getUsername(), loginRequest.getPassword());

        // 判断登录是否成功
        if (token != null) {
            // 登录成功，返回token
            Map<String, String> result = new HashMap<>();
            result.put("token", token);
            return Result.success(result);
        } else {
            // 登录失败，返回错误信息
            return Result.error("用户名或密码错误");
        }
    }

    /**
     * 用户注册接口
     * @param registerRequest 注册请求参数
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<Map<String, String>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        // 调用UserService的注册方法
        User user = userService.register(registerRequest.getUsername(), registerRequest.getPassword());

        // 判断注册是否成功
        if (user != null) {
            // 注册成功
            Map<String, String> result = new HashMap<>();
            result.put("username", user.getUsername());
            return Result.success(result);
        } else {
            // 注册失败，返回错误信息
            return Result.error("用户名已存在");
        }
    }
    
    /**
     * 重置密码接口
     * @param resetPasswordRequest 重置密码请求参数
     * @return 重置结果
     */
    @PostMapping("/reset-password")
    public Result<String> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        // 调用UserService的重置密码方法
        boolean success = userService.resetPassword(
            resetPasswordRequest.getUsername(), 
            resetPasswordRequest.getNewPassword()
        );

        // 判断重置是否成功
        if (success) {
            // 重置成功
            return Result.success("密码重置成功");
        } else {
            // 重置失败，返回错误信息
            return Result.error("用户不存在");
        }
    }
}