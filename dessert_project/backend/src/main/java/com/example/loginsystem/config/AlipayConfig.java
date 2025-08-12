package com.example.loginsystem.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 支付宝支付配置类
 * 用于读取支付宝支付相关配置参数
 */
@Data
@Component
@ConfigurationProperties(prefix = "alipay")
public class AlipayConfig {

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 商户私钥
     */
    private String merchantPrivateKey;

    /**
     * 支付宝公钥
     */
    private String alipayPublicKey;

    /**
     * 签名类型
     */
    private String signType = "RSA2";

    /**
     * 字符编码格式
     */
    private String charset = "UTF-8";

    /**
     * 支付宝网关地址
     */
    private String gatewayUrl;

    /**
     * 支付成功后的回调地址
     */
    private String returnUrl;

    /**
     * 支付成功后的异步通知地址
     */
    private String notifyUrl;
}