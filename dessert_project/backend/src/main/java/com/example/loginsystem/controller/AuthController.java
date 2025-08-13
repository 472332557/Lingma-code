package com.example.loginsystem.controller;

import com.example.loginsystem.common.Result;
import com.example.loginsystem.dto.LoginRequest;
import com.example.loginsystem.dto.RegisterRequest;
import com.example.loginsystem.dto.ResetPasswordRequest;
import com.example.loginsystem.entity.User;
import com.example.loginsystem.service.UserService;
import com.example.loginsystem.util.JwtUtil;
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

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户登录接口
     * @param loginRequest 登录请求参数
     * @return 登录结果，包含token
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest loginRequest) {
        // 调用用户服务进行登录验证
        User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());

        // 判断用户是否存在
        if (user == null) {
            // 用户不存在或密码错误，返回错误信息
            return Result.error("用户名或密码错误");
        }

        // 用户存在，生成JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 创建返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("username", user.getUsername());

        // 返回登录成功结果
        return Result.success(data);
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
            resetPasswordRequest.getOldPassword(),
            resetPasswordRequest.getNewPassword()
        );

        // 判断重置是否成功
        if (success) {
            // 密码重置成功
            return Result.success("密码重置成功");
        } else {
            // 密码重置失败，返回错误信息
            return Result.error("用户名或原密码错误");
        }
    }
    
    /**
     * 测试认证接口
     * @return 认证测试结果
     */
    @GetMapping("/test")
    public Result<Map<String, String>> testAuth() {
        // 创建返回数据
        Map<String, String> data = new HashMap<>();
        data.put("message", "认证成功，可以访问需要认证的接口");

        // 返回认证成功结果
        return Result.success(data);
    }
}