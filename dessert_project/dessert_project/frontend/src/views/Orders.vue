methods: {
    payOrder(orderId, amount, type) {
        // 显示支付弹窗
        this.showPaymentModal = true;
        this.paymentOrderId = orderId;
        this.paymentAmount = amount;
        this.paymentType = type;
        
        // 根据支付类型发起请求
        if (type === 'alipay') {
            this.$http.post(`/api/payment/alipay`, {
                orderId: orderId,
                amount: amount
            }).then(response => {
                // 获取支付宝支付页面HTML
                const paymentHtml = response.data;
                
                // 设置iframe的src属性
                this.paymentIframeSrc = 'data:text/html;charset=utf-8,' + encodeURIComponent(paymentHtml);
            }).catch(error => {
                console.error('支付失败:', error);
                this.$message.error('支付失败，请稍后重试');
                this.showPaymentModal = false;
            });
        } else if (type === 'wechat') {
            // 微信支付逻辑
            this.$http.post(`/api/payment/wechat`, {
                orderId: orderId,
                amount: amount
            }).then(response => {
                // 处理微信支付
                this.wechatPayInfo = response.data;
                this.showWechatPayModal = true;
            }).catch(error => {
                console.error('支付失败:', error);
                this.$message.error('支付失败，请稍后重试');
            });
        }
    },
    // ... other methods ...
}