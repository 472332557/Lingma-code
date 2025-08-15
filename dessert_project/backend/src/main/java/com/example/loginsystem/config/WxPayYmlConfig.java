package com.example.loginsystem.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信支付配置类
 * 用于读取微信支付相关配置参数
 */
@Data
@Component
//@ConfigurationProperties(prefix = "wx.pay")
public class WxPayYmlConfig {

    /**
     * 微信支付商户号
     */
    private String mchId;

    /**
     * 微信支付商户密钥
     */
    private String mchKey;

    /**
     * 微信支付appId
     */
    private String appId;

    /**
     * 微信支付API地址
     */
    private String apiUrl;

    /**
     * 支付通知地址
     */
    private String notifyUrl;
    
    /**
     * 商户证书序列号
     */
    private String merchantSerialNumber;
    
    /**
     * APIv3密钥
     */
    private String apiV3Key;
    
    /**
     * 商户私钥路径
     */
    private String privateKeyPath;
    
    /**
     * 商户证书路径
     */
    private String merchantCertPath;
    
    /**
     * 微信支付平台证书路径
     */
    private String wechatpayCertPath;
}