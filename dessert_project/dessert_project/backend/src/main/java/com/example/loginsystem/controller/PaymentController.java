/**
 * 发起支付宝支付接口
 * @param orderId 订单ID
 * @param amount 支付金额
 * @param response HTTP响应
 */
@PostMapping("/alipay")
public void alipayPay(@RequestParam Long orderId, @RequestParam BigDecimal amount, HttpServletResponse response) throws IOException {
    // 获取订单信息
    Order order = orderService.getById(orderId);
    if (order == null) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("订单不存在");
        return;
    }
    
    // 验证订单状态是否为待支付
    if (order.getStatus() != 0) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("订单状态不正确");
        return;
    }
    
    // 验证金额是否大于0
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("支付金额必须大于0");
        return;
    }
    
    try {
        // 创建支付宝客户端
        AlipayClient alipayClient = new DefaultAlipayClient(
            alipayConfig.getGatewayUrl(),
            alipayConfig.getAppId(),
            alipayConfig.getMerchantPrivateKey(),
            "JSON",
            "UTF-8",
            alipayConfig.getAlipayPublicKey(),
            alipayConfig.getSignType()
        );
        
        // 创建请求
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setReturnUrl(alipayConfig.getReturnUrl());
        request.setNotifyUrl(alipayConfig.getNotifyUrl());
        
        // 设置请求参数
        Map<String, String> params = new HashMap<>();
        params.put("out_trade_no", order.getOrderNumber()); // 订单号
        params.put("total_amount", amount.toString()); // 支付金额
        params.put("subject", "甜品店支付"); // 商品标题
        params.put("product_code", "FAST_INSTANT_TRADE"); // 产品码
        
        request.setBizContent(new JSONObject(params).toJSONString());
        
        // 执行请求并获取结果
        String result = alipayClient.pageExecute(request).getBody();
        
        // 设置响应头和内容类型
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(result);
        
    } catch (Exception e) {
        // 记录错误日志
        log.error("支付宝支付失败", e);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write("支付失败，请稍后重试");
    }
}