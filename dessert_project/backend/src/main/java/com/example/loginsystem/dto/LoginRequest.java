package com.example.loginsystem.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求参数类
 * 用于接收登录接口的请求参数
 */
@Data
public class LoginRequest {

    /**
     * 用户名
     * 使用@NotBlank注解标记不能为空
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     * 使用@NotBlank注解标记不能为空
     */
    @NotBlank(message = "密码不能为空")
    private String password;

}