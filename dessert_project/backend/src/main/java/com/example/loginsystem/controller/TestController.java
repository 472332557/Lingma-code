package com.example.loginsystem.controller;

import com.example.loginsystem.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 * 用于测试认证拦截器是否正常工作
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    /**
     * 需要认证的测试接口
     * @return 测试结果
     */
    @GetMapping("/auth")
    public Result<Map<String, String>> authTest() {
        Map<String, String> result = new HashMap<>();
        result.put("message", "认证成功，可以访问需要认证的接口");
        return Result.success(result);
    }
}