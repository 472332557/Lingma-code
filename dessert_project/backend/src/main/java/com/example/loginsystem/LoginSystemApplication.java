package com.example.loginsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Boot主应用程序类
 * 该类是整个应用程序的入口点，负责启动Spring Boot应用程序
 */
@SpringBootApplication
@EnableScheduling  // 启用定时任务支持
public class LoginSystemApplication {

    /**
     * 应用程序入口方法
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(LoginSystemApplication.class, args);
    }

}