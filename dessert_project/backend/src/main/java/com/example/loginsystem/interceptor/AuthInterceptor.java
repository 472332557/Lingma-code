package com.example.loginsystem.interceptor;

import com.example.loginsystem.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证拦截器
 * 用于拦截需要认证的请求，验证JWT token
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 在请求处理之前进行调用
     * @param request 请求对象
     * @param response 响应对象
     * @param handler 处理器
     * @return 是否继续执行
     * @throws Exception 异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取token
        String token = request.getHeader("Authorization");

        // 判断token是否存在
        if (token == null || token.isEmpty()) {
            // token不存在，返回未认证错误
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 验证token是否有效
        if (!jwtUtil.validateToken(token)) {
            // token无效，返回未认证错误
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // token有效，继续执行
        return true;
    }
}