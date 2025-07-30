package com.example.loginsystem.exception;

import com.example.loginsystem.common.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * 统一处理参数验证等异常
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数验证异常（使用@Valid验证请求体参数时）
     * @param ex 方法参数无效异常
     * @return 错误结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        // 遍历所有验证错误
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        Result<Map<String, String>> result = Result.error("参数验证失败");
        result.setData(errors);
        return result;
    }

    /**
     * 处理参数验证异常（使用@Valid验证请求参数时）
     * @param ex 绑定异常
     * @return 错误结果
     */
    @ExceptionHandler(BindException.class)
    public Result<Map<String, String>> handleBindException(BindException ex) {
        Map<String, String> errors = new HashMap<>();
        // 遍历所有验证错误
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        Result<Map<String, String>> result = Result.error("参数验证失败");
        result.setData(errors);
        return result;
    }
}