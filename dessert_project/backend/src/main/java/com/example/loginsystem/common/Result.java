package com.example.loginsystem.common;

import lombok.Data;

/**
 * 统一返回结果类
 * 用于统一接口返回格式，包含状态码、消息和数据
 * @param <T> 数据类型
 */
@Data
public class Result<T> {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 默认构造函数
     */
    public Result() {}

    /**
     * 构造函数
     * @param code 状态码
     * @param message 消息
     * @param data 数据
     */
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回结果
     * @param data 数据
     * @param <T> 数据类型
     * @return Result对象
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 成功返回结果（无数据）
     * @param <T> 数据类型
     * @return Result对象
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    /**
     * 失败返回结果
     * @param message 错误消息
     * @param <T> 数据类型
     * @return Result对象
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    /**
     * 自定义状态码和消息的返回结果
     * @param code 状态码
     * @param message 消息
     * @param <T> 数据类型
     * @return Result对象
     */
    public static <T> Result<T> custom(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}