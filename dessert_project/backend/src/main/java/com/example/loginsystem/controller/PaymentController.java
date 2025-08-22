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
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
        try {
            // 获取订单信息
            Order order = orderService.getById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }
            
            // 检查订单状态
            if (order.getStatus() != 0) { // 0:待支付
                return Result.error("订单状态不正确");
            }
            
            // 检查订单金额是否匹配
            if (order.getTotalAmount().compareTo(amount) != 0) {
                return Result.error("订单金额不匹配");
            }

            // 构造微信支付请求参数
            PrepayRequest request = new PrepayRequest();
            // 设置公众号ID
            request.setAppid(wxPayConfig.getAppId());
            // 设置商户号
            request.setMchid(wxPayConfig.getMchId());
            // 设置商品描述
            request.setDescription("甜品订单-" + order.getOrderNumber());
            // 设置商户订单号
            request.setOutTradeNo(order.getOrderNumber());
            // 设置交易结束时间
            request.setTimeExpire(LocalDateTime.now().plusMinutes(30)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")));
            
            // 设置附加数据
            request.setAttach("甜品订单");
            
            // 设置通知地址
            request.setNotifyUrl(wxPayConfig.getNotifyUrl());
            
            // 设置金额信息
            Amount amountInfo = new Amount();
            amountInfo.setTotal(amount.multiply(new BigDecimal(100)).intValue()); // 转换为分
            amountInfo.setCurrency("CNY");
            request.setAmount(amountInfo);
            
            // 设置支付者信息（示例openid）
            Payer payer = new Payer();
            payer.setOpenid("oUpF8uMuAJO_M2pxb1Q9zNjWeS6o"); // 示例openid，实际应从用户会话中获取
            request.setPayer(payer);

            // 初始化配置
            Config config = new RSAAutoCertificateConfig.Builder()
                    .merchantId(wxPayConfig.getMchId())
                    .privateKeyFromPath(wxPayConfig.getPrivateKeyPath()) // 从配置文件中读取私钥路径
                    .merchantSerialNumber(wxPayConfig.getMerchantSerialNumber()) // 从配置文件中读取证书序列号
                    .apiV3Key(wxPayConfig.getApiV3Key()) // 从配置文件中读取APIv3密钥
                    .build();

            // 初始化服务
            JsapiService service = new JsapiService.Builder().config(config).build();

            // 发起微信支付请求
            PrepayResponse response = service.prepay(request);
            
            // 生成前端调用支付所需的参数
            Map<String, String> payParams = new HashMap<>();
            
            // 公众号ID
            payParams.put("appId", wxPayConfig.getAppId());
            
            // 时间戳
            payParams.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
            
            // 随机字符串
            payParams.put("nonceStr", generateNonceStr());
            
            // 订单详情扩展字符串
            payParams.put("package", "prepay_id=" + response.getPrepayId());
            
            // 签名方式
            payParams.put("signType", "RSA");
            
            // 签名
            payParams.put("paySign", generatePaySign(payParams));

            return Result.success(payParams);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("微信支付创建失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成随机字符串
     * @return 随机字符串
     */
    private String generateNonceStr() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 32);
    }
    
    /**
     * 生成预支付交易会话标识
     * @param order 订单信息
     * @param amount 支付金额
     * @return 预支付交易会话标识
     */
    private String generatePrepayId(Order order, BigDecimal amount) {
        // 实际项目中需要调用微信统一下单接口
        // 这里生成一个更标准的模拟prepay_id
        
        // 使用订单ID和时间戳作为基础
        String base = "prepay_id=" + order.getId() + "_" + System.currentTimeMillis();
        
        // 添加随机字符串确保唯一性
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8);
        
        // 生成MD5签名作为标识
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest((base + randomSuffix).getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            
            // 返回标准格式的prepay_id
            return "prepay_id=" + sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            // 出错时返回一个简单的模拟prepay_id
            return "prepay_id=simulated_" + System.currentTimeMillis();
        }
    }
    
    /**
     * 生成签名
     * @param payParams 支付参数
     * @return 签名
     */
    private String generatePaySign(Map<String, String> payParams) {
        // 按照ASCII码字典序排序参数
        List<String> keys = new ArrayList<>(payParams.keySet());
        Collections.sort(keys);
        
        // 拼接签名字符串
        StringBuilder signStr = new StringBuilder();
        for (String key : keys) {
            if ("paySign".equals(key)) continue; // 排除paySign本身
            String value = payParams.get(key);
            if (value != null && !value.isEmpty()) {
                signStr.append(key).append("=").append(value).append("&");
            }
        }
        
        // 添加商户密钥
        signStr.append("key=").append(wxPayConfig.getMchKey());
        
        // MD5签名
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(signStr.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString().toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 微信支付回调通知接口
     * @param request HTTP请求
     * @return 处理结果
     */
    @PostMapping("/wechat/notify")
    public String wechatPayNotify(HttpServletRequest request) {
        try {
            // 读取微信支付回调通知数据
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String xmlData = sb.toString();
            
            // 实际项目中需要解析XML格式的回调数据
            // 解析商户订单号和支付结果
            String outTradeNo = parseOutTradeNoFromXml(xmlData); // 商户订单号
            String resultCode = parseResultCodeFromXml(xmlData); // 业务结果
            
            if ("SUCCESS".equals(resultCode)) {
                // 根据订单号查询订单
                QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("order_number", outTradeNo);
                Order order = orderService.getOne(queryWrapper);
                
                if (order != null && order.getStatus() == 0) { // 待支付状态
                    // 更新订单状态为已支付
                    order.setStatus(1); // 已支付状态
                    order.setUpdateTime(java.time.LocalDateTime.now());
                    orderService.updateById(order);
                    
                    // 记录支付日志或发送通知等操作
                    System.out.println("订单 " + outTradeNo + " 支付成功，状态已更新");
                }
            }
            
            // 返回成功响应给微信
            return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
        } catch (Exception e) {
            e.printStackTrace();
            return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[处理失败]]></return_msg></xml>";
        }
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
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write("<html><body><h1>订单不存在</h1></body></html>");
                return;
            }
            
            // 检查订单状态
            if (order.getStatus() != 0) { // 0:待支付
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write("<html><body><h1>订单状态不正确</h1></body></html>");
                return;
            }
            
            // 检查订单金额是否匹配
            if (order.getTotalAmount().compareTo(amount) != 0) {
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write("<html><body><h1>订单金额不匹配</h1></body></html>");
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
            String subject = "甜品订单支付 - " + order.getOrderNumber(); // 订单标题
            String body = "甜品订单支付详情"; // 订单描述

            // 设置业务参数
            alipayRequest.setBizContent(
                    "{\"out_trade_no\":\"" + outTradeNo + "\"," 
                            + "\"total_amount\":\"" + totalAmount + "\"," 
                            + "\"subject\":\"" + subject + "\"," 
                            + "\"body\":\"" + body + "\"," 
                            + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"," 
                            + "\"timeout_express\":\"1800s\"}");

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
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write("<html><body><h1>支付宝支付创建失败: " + e.getMessage() + "</h1></body></html>");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } catch (Exception e) {
            // 其他异常
            e.printStackTrace();
            try {
                response.setContentType("text/html;charset=UTF-8");
                response.getWriter().write("<html><body><h1>支付处理异常: " + e.getMessage() + "</h1></body></html>");
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
    
    /**
     * 从XML数据中解析商户订单号
     * @param xmlData XML数据
     * @return 商户订单号
     */
    private String parseOutTradeNoFromXml(String xmlData) {
        return parseXmlValue(xmlData, "out_trade_no");
    }
    
    /**
     * 从XML数据中解析业务结果
     * @param xmlData XML数据
     * @return 业务结果
     */
    private String parseResultCodeFromXml(String xmlData) {
        return parseXmlValue(xmlData, "result_code");
    }
    
    /**
     * 从XML数据中解析指定字段的值
     * @param xmlData XML数据
     * @param tagName 标签名
     * @return 字段值
     */
    private String parseXmlValue(String xmlData, String tagName) {
        try {
            String startTag = "<" + tagName + ">";
            String endTag = "</" + tagName + ">";
            int startIndex = xmlData.indexOf(startTag);
            int endIndex = xmlData.indexOf(endTag);
            
            if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                return xmlData.substring(startIndex + startTag.length(), endIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ""; // 返回空字符串表示解析失败
    }
}