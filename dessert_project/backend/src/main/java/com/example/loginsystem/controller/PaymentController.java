package com.example.loginsystem.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.example.loginsystem.common.Result;
import com.example.loginsystem.config.AlipayConfig;
import com.example.loginsystem.config.WxPayConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付控制器
 * 处理支付相关请求
 */
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private WxPayConfig wxPayConfig;

    @Autowired
    private AlipayConfig alipayConfig;

    /**
     * 发起微信支付接口
     * @param orderId 订单ID
     * @param amount 支付金额
     * @return 支付信息
     */
    @PostMapping("/wechat")
    public Result<Map<String, String>> wechatPay(@RequestParam Long orderId, @RequestParam BigDecimal amount) {
        // TODO: 实际项目中需要调用微信统一下单接口
        // 这里仅作为示例返回模拟数据

        Map<String, String> payInfo = new HashMap<>();
        payInfo.put("appId", wxPayConfig.getAppId());
        payInfo.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
        payInfo.put("nonceStr", "random_string");
        payInfo.put("package", "prepay_id=wx20141027221359oIgO58k47h8O");
        payInfo.put("signType", "MD5");
        payInfo.put("paySign", "sign_content");

        return Result.success(payInfo);
    }

    /**
     * 微信支付回调通知接口
     * @param request HTTP请求
     * @return 处理结果
     */
    @PostMapping("/wechat/notify")
    public String wechatPayNotify(HttpServletRequest request) {
        // TODO: 实际项目中需要处理微信支付回调通知
        // 1. 验证回调签名
        // 2. 更新订单状态
        // 3. 返回处理结果给微信

        // 示例中直接返回成功响应
        return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    }

    /**
     * 发起支付宝支付接口
     * @param orderId 订单ID
     * @param amount 支付金额
     * @param response HTTP响应
     */
    @PostMapping("/alipay")
    public void alipay(@RequestParam Long orderId, @RequestParam BigDecimal amount, HttpServletResponse response) {
        try {
            // 创建支付宝客户端
            AlipayClient alipayClient = new DefaultAlipayClient(
                    alipayConfig.getGatewayUrl(),
                    alipayConfig.getAppId(),
                    alipayConfig.getMerchantPrivateKey(),
                    "json",
                    alipayConfig.getCharset(),
                    alipayConfig.getAlipayPublicKey(),
                    alipayConfig.getSignType());

            // 创建API对应的request
            AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
            // 设置回调地址
            alipayRequest.setReturnUrl(alipayConfig.getReturnUrl());
            alipayRequest.setNotifyUrl(alipayConfig.getNotifyUrl());

            // 填充业务参数
            String outTradeNo = "ORDER_" + orderId; // 商户订单号
            String totalAmount = amount.toString(); // 订单总金额
            String subject = "甜品订单支付"; // 订单标题
            String body = "甜品订单支付详情"; // 订单描述

            // 设置业务参数
            alipayRequest.setBizContent(
                    "{\"out_trade_no\":\"" + outTradeNo + "\","
                            + "\"total_amount\":\"" + totalAmount + "\","
                            + "\"subject\":\"" + subject + "\","
                            + "\"body\":\"" + body + "\","
                            + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

            // 调用SDK生成表单
            String form = alipayClient.pageExecute(alipayRequest).getBody();

            // 直接将完整的表单html输出到页面
            response.setContentType("text/html;charset=" + alipayConfig.getCharset());
            response.getWriter().write(form);
            response.getWriter().flush();
            response.getWriter().close();
        } catch (AlipayApiException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 支付宝支付同步回调接口
     * @param request HTTP请求
     * @return 支付结果页面
     */
    @GetMapping("/alipay/return")
    public String alipayReturn(HttpServletRequest request) {
        // TODO: 实际项目中需要处理支付宝支付同步回调
        // 1. 验证回调签名
        // 2. 展示支付结果给用户

        // 示例中直接返回成功页面
        return "<html><body><h1>支付成功</h1><p>订单支付已完成</p></body></html>";
    }

    /**
     * 支付宝支付异步回调接口
     * @param request HTTP请求
     * @return 处理结果
     */
    @PostMapping("/alipay/notify")
    public String alipayNotify(HttpServletRequest request) {
        // TODO: 实际项目中需要处理支付宝支付异步回调通知
        // 1. 验证回调签名
        // 2. 更新订单状态
        // 3. 返回处理结果给支付宝

        // 示例中直接返回成功响应
        return "success";
    }
}