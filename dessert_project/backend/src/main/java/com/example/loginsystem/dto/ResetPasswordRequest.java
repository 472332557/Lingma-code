package com.example.loginsystem.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 密码重置请求参数类
 * 用于接收密码重置接口的请求参数
 */
@Data
public class ResetPasswordRequest {

    /**
     * 用户名
     * 使用@NotBlank注解标记不能为空
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 原密码
     * 使用@NotBlank注解标记不能为空
     */
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    /**
     * 新密码
     * 使用@NotBlank注解标记不能为空
     */
    @NotBlank(message = "新密码不能为空")
    private String newPassword;

}