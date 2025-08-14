package com.example.loginsystem.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.loginsystem.common.Result;
import com.example.loginsystem.config.AlipayConfig;
import com.example.loginsystem.config.WxPayConfig;
import com.example.loginsystem.entity.Order;
import com.example.loginsystem.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
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
    
    @Autowired
    private OrderService orderService;

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
    public void alipay(@RequestParam("orderId") Long orderId, @RequestParam("amount") BigDecimal amount, HttpServletResponse response) {
        try {
            // 获取订单信息
            Order order = orderService.getById(orderId);
            if (order == null) {
                response.getWriter().write("订单不存在");
                return;
            }
            
            // 检查订单状态
            if (order.getStatus() != 0) { // 0:待支付
                response.getWriter().write("订单状态不正确");
                return;
            }
            
            // 检查订单金额是否匹配
            if (order.getTotalAmount().compareTo(amount) != 0) {
                response.getWriter().write("订单金额不匹配");
                return;
            }

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
            String outTradeNo = order.getOrderNumber(); // 使用订单号作为商户订单号
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

            // 调用SDK生成表单 - 这是真实的支付宝支付表单
            String form = alipayClient.pageExecute(alipayRequest).getBody();

            // 直接将完整的表单html输出到页面，让浏览器自动提交到支付宝
            response.setContentType("text/html;charset=" + alipayConfig.getCharset());
            response.getWriter().write(form);
            response.getWriter().flush();
            response.getWriter().close();
        } catch (AlipayApiException e) {
            // 支付宝API异常
            e.printStackTrace();
            try {
                response.getWriter().write("支付宝支付创建失败: " + e.getMessage());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } catch (Exception e) {
            // 其他异常
            e.printStackTrace();
            try {
                response.getWriter().write("支付处理异常: " + e.getMessage());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    /**
     * 支付宝支付同步回调接口
     * @param request HTTP请求
     * @return 支付结果页面
     */
    @GetMapping("/alipay/return")
    public String alipayReturn(HttpServletRequest request) {
        try {
            // 获取支付宝GET过来反馈信息
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
                String name = iter.next();
                String[] values = requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }
                // 乱码解决，这段代码在出现乱码时使用
                // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
                params.put(name, valueStr);
            }

            // 验证签名
            boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayConfig.getAlipayPublicKey(), alipayConfig.getCharset(), alipayConfig.getSignType());
            
            if (signVerified) {
                // 商户订单号
                String outTradeNo = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
                
                // 支付宝交易号
                String tradeNo = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
                
                // 付款金额
                String totalAmount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");
                
                return "<html><body><h1>支付成功</h1><p>订单号: " + outTradeNo + "</p><p>支付宝交易号: " + tradeNo + "</p><p>付款金额: " + totalAmount + "</p></body></html>";
            } else {
                return "<html><body><h1>支付验证失败</h1><p>签名验证失败</p></body></html>";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "<html><body><h1>支付处理异常</h1><p>" + e.getMessage() + "</p></body></html>";
        }
    }

    /**
     * 支付宝支付异步回调接口
     * @param request HTTP请求
     * @return 处理结果
     */
    @PostMapping("/alipay/notify")
    public String alipayNotify(HttpServletRequest request) {
        try {
            // 获取支付宝POST过来反馈信息
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
                String name = iter.next();
                String[] values = requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }
                params.put(name, valueStr);
            }

            // 验证签名
            boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayConfig.getAlipayPublicKey(), alipayConfig.getCharset(), alipayConfig.getSignType());
            
            if (signVerified) {
                // 商户订单号
                String outTradeNo = params.get("out_trade_no");
                
                // 支付宝交易号
                String tradeNo = params.get("trade_no");
                
                // 交易状态
                String tradeStatus = params.get("trade_status");
                
                if (tradeStatus.equals("TRADE_FINISHED") || tradeStatus.equals("TRADE_SUCCESS")) {
                    // 根据订单号查询订单
                    QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("order_number", outTradeNo);
                    Order order = orderService.getOne(queryWrapper);
                    
                    if (order != null && order.getStatus() == 0) { // 待支付状态
                        // 更新订单状态为已支付
                        order.setStatus(1); // 已支付状态
                        order.setUpdateTime(java.time.LocalDateTime.now());
                        orderService.updateById(order);
                    }
                }
                
                return "success";
            } else {
                return "fail";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
}