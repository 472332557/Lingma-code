<template>
  <div class="orders-container">
    <!-- 顶部导航栏 -->
    <div class="header">
      <h1 class="title">我的订单</h1>
    </div>

    <!-- 订单列表 -->
    <div class="orders-list">
      <div 
        class="order-item" 
        v-for="order in orders" 
        :key="order.id"
      >
        <div class="order-header">
          <span class="order-number">订单号: {{ order.orderNumber }}</span>
          <span class="order-status" :class="'status-' + order.status">
            {{ getOrderStatusText(order.status) }}
          </span>
        </div>
        <div class="order-products">
          <div 
            class="product-item" 
            v-for="item in order.items" 
            :key="item.id"
          >
            <div class="product-image-placeholder"></div>
            <div class="product-info">
              <h3 class="product-name">{{ item.name }}</h3>
              <p class="product-spec">{{ item.spec }}</p>
              <div class="product-price-quantity">
                <span class="price">¥{{ item.price }}</span>
                <span class="quantity">x{{ item.quantity }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="order-footer">
          <span class="total-amount">共{{ order.totalItems }}件商品，合计: ¥{{ order.totalAmount }}</span>
          <div class="order-actions">
            <button 
              class="action-button"
              v-if="order.status === 0"
              @click="payOrder(order.id, order.totalAmount, 'wechat')"
            >
              微信支付
            </button>
            <button 
              class="action-button"
              v-if="order.status === 0"
              @click="payOrder(order.id, order.totalAmount, 'alipay')"
            >
              支付宝支付
            </button>
            <button 
              class="action-button"
              v-if="order.status === 3"
              @click="buyAgain(order.id)"
            >
              再次购买
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部导航栏 -->
    <div class="footer-nav">
      <div 
        class="nav-item" 
        @click="setActiveTab('home')"
      >
        <span>首页</span>
      </div>
      <div 
        class="nav-item" 
        @click="setActiveTab('order')"
      >
        <span>点单</span>
      </div>
      <div 
        class="nav-item active" 
        @click="setActiveTab('orders')"
      >
        <span>订单</span>
      </div>
      <div 
        class="nav-item" 
        @click="setActiveTab('profile')"
      >
        <span>我的</span>
      </div>
    </div>
    
    <!-- 支付加载提示 -->
    <div class="loading-overlay" v-if="isPaying">
      <div class="loading-content">
        <div class="spinner"></div>
        <p>正在跳转到支付页面...</p>
      </div>
    </div>
    
    <!-- 支付弹窗 -->
    <div class="modal" v-if="showPaymentModal" @click="closePaymentModal">
      <div class="payment-modal-content" @click.stop>
        <div class="modal-header">
          <h3>支付宝支付</h3>
          <button class="close-button" @click="closePaymentModal">&times;</button>
        </div>
        <div class="modal-body">
          <div class="payment-info">
            <p>订单号: {{ paymentOrderNumber }}</p>
            <p>支付金额: ¥{{ paymentAmount }}</p>
          </div>
          <div class="payment-iframe-container">
            <iframe 
              :src="paymentUrl" 
              class="payment-iframe"
              v-if="paymentUrl"
            ></iframe>
            <div v-else class="loading">支付页面加载中...</div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="cancel-button" @click="closePaymentModal">取消支付</button>
          <button class="check-payment-button" @click="checkPaymentStatus(paymentOrderId)">检查支付状态</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
// 导入请求工具
import request from '@/utils/request';

export default {
  name: 'Orders',
  data() {
    return {
      // 订单列表
      orders: [
        {
          id: 1,
          orderNumber: '202306010001',
          status: 0, // 0:待支付 1:已支付 2:已取消 3:已完成
          totalItems: 2,
          totalAmount: 196.00,
          items: [
            {
              id: 1,
              name: '草莓奶油蛋糕',
              spec: '8寸',
              price: 128.00,
              quantity: 1
            },
            {
              id: 2,
              name: '芒果千层蛋糕',
              spec: '6寸',
              price: 68.00,
              quantity: 1
            }
          ]
        },
        {
          id: 2,
          orderNumber: '202305280005',
          status: 3, // 已完成
          totalItems: 1,
          totalAmount: 158.00,
          items: [
            {
              id: 3,
              name: '巧克力慕斯蛋糕',
              spec: '8寸',
              price: 158.00,
              quantity: 1
            }
          ]
        }
      ],
      // 当前激活的tab
      activeTab: 'orders',
      // 支付状态
      isPaying: false,
      // 支付弹窗
      showPaymentModal: false,
      paymentUrl: '',
      paymentAmount: 0,
      paymentOrderNumber: '',
      paymentOrderId: 0
    };
  },
  mounted() {
    // 页面加载时获取订单列表
    this.fetchOrders();
  },
  methods: {
    /**
     * 获取订单列表
     */
    async fetchOrders() {
      try {
        // 实际项目中这里会调用后端API获取订单列表
        // const response = await api.get('/api/user/order/list');
        // if (response.data.code === 200) {
        //   this.orders = response.data.data;
        // }
      } catch (error) {
        console.error('获取订单列表失败:', error);
      }
    },
    
    /**
     * 获取订单状态文本
     */
    getOrderStatusText(status) {
      const statusMap = {
        0: '待支付',
        1: '已支付',
        2: '已取消',
        3: '已完成'
      };
      return statusMap[status] || '未知状态';
    },

    /**
     * 支付订单
     */
    async payOrder(orderId, amount, paymentMethod) {
      this.isPaying = true;
      try {
        if (paymentMethod === 'alipay') {
          // 使用FormData提交，匹配后端的@RequestParam
          const formData = new FormData();
          formData.append('orderId', orderId);
          formData.append('amount', Number(amount).toFixed(2));

          const response = await fetch('/api/payment/alipay', {
            method: 'POST',
            body: formData
          });

          if (response.ok) {
            this.isPaying = false;
            
            // 获取HTML响应并显示在弹窗中
            const html = await response.text();
            
            // 创建支付页面
            const blob = new Blob([html], { type: 'text/html' });
            const url = window.URL.createObjectURL(blob);
            
            // 在弹窗中显示支付页面
            this.paymentUrl = url;
            this.paymentAmount = amount;
            this.paymentOrderId = orderId;
            this.paymentOrderNumber = 'ORDER_' + orderId; // 示例订单号
            this.showPaymentModal = true;
          } else if (response.status === 400) {
            this.isPaying = false;
            const errorText = await response.text();
            alert('支付创建失败: ' + errorText);
          } else {
            this.isPaying = false;
            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
          }
        } else if (paymentMethod === 'wechat') {
          // 调用后端微信支付接口
          // 实际项目中这里会跳转到微信支付页面
          alert(`使用微信支付订单 ${orderId}`);
        }
      } catch (error) {
        console.error('支付失败:', error);
        alert('支付失败，请稍后重试');
        this.isPaying = false;
      }
    },
    
    /**
     * 关闭支付弹窗
     */
    closePaymentModal() {
      this.showPaymentModal = false;
      this.paymentUrl = '';
      this.paymentAmount = 0;
      this.paymentOrderId = 0;
      this.paymentOrderNumber = '';
      
      // 检查支付状态
      setTimeout(() => {
        this.checkPaymentStatus(this.paymentOrderId);
      }, 2000);
    },
    
    /**
     * 检查支付状态
     */
    checkPaymentStatus(orderId) {
      // 实际项目中这里会调用后端API检查支付状态
      // 示例代码：
      // try {
      //   const response = await api.get(`/api/order/${orderId}/status`);
      //   if (response.data.code === 200) {
      //     if (response.data.data.status === 1) {
      //       alert('支付成功');
      //     } else {
      //       alert('支付未完成，请继续支付或取消订单');
      //     }
      //   }
      // } catch (error) {
      //   console.error('检查支付状态失败:', error);
      //   alert('检查支付状态失败');
      // }
      
      alert('支付状态检查完成');
    },

    /**
     * 再次购买
     */
    buyAgain(orderId) {
      // 实际项目中这里会将订单中的商品添加到购物车
      alert(`再次购买订单 ${orderId}`);
    },

    /**
     * 设置激活的tab
     */
    setActiveTab(tab) {
      this.activeTab = tab;
      switch (tab) {
        case 'home':
          this.$router.push('/');
          break;
        case 'order':
          this.$router.push('/order');
          break;
        case 'orders':
          this.$router.push('/orders');
          break;
        case 'profile':
          this.$router.push('/profile');
          break;
      }
    }
  }
};
</script>

<style scoped>
.orders-container {
  padding-bottom: 60px;
}

.header {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 15px;
  background-color: #fff;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.title {
  margin: 0;
  font-size: 20px;
  color: #333;
}

.orders-list {
  padding: 15px;
}

.order-item {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
  margin-bottom: 15px;
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
}

.order-number {
  font-size: 14px;
  color: #666;
}

.order-status {
  font-size: 14px;
  font-weight: bold;
}

.order-status.status-0 {
  color: #ff9900;
}

.order-status.status-1 {
  color: #409eff;
}

.order-status.status-2 {
  color: #999;
}

.order-status.status-3 {
  color: #67c23a;
}

.order-products {
  padding: 15px;
  border-bottom: 1px solid #eee;
}

.product-item {
  display: flex;
  margin-bottom: 15px;
}

.product-item:last-child {
  margin-bottom: 0;
}

.product-image-placeholder {
  width: 80px;
  height: 80px;
  background-color: #f5f5f5;
  border-radius: 8px;
  margin-right: 15px;
}

.product-info {
  flex: 1;
}

.product-name {
  margin: 0 0 5px 0;
  font-size: 16px;
  color: #333;
}

.product-spec {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #999;
}

.product-price-quantity {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  font-size: 16px;
  color: #ff4444;
  font-weight: bold;
}

.quantity {
  font-size: 14px;
  color: #666;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
}

.total-amount {
  font-size: 14px;
  color: #666;
}

.order-actions {
  display: flex;
}

.action-button {
  padding: 8px 15px;
  margin-left: 10px;
  font-size: 14px;
  color: #409eff;
  background-color: #fff;
  border: 1px solid #409eff;
  border-radius: 4px;
  cursor: pointer;
}

.action-button:hover {
  background-color: #ecf5ff;
}

.footer-nav {
  display: flex;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 60px;
  background-color: #fff;
  box-shadow: 0 -2px 5px rgba(0,0,0,0.1);
  z-index: 100;
}

.nav-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  font-size: 14px;
  color: #999;
  cursor: pointer;
}

.nav-item.active {
  color: #409eff;
}

.nav-item span {
  margin-top: 5px;
}

/* 加载提示样式 */
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.loading-content {
  background-color: #fff;
  padding: 30px;
  border-radius: 8px;
  text-align: center;
}

.spinner {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #409eff;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 弹窗样式 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.close-button {
  font-size: 24px;
  color: #999;
  background: none;
  border: none;
  cursor: pointer;
}

.modal-body {
  padding: 15px;
}

.payment-info {
  padding: 15px;
  background-color: #f5f5f5;
  border-radius: 4px;
  margin-bottom: 15px;
}

.payment-info p {
  margin: 5px 0;
  font-size: 16px;
}

.payment-iframe-container {
  flex: 1;
  min-height: 400px;
}

.payment-iframe {
  width: 100%;
  height: 100%;
  min-height: 400px;
  border: none;
}

.loading {
  text-align: center;
  padding: 50px;
  color: #999;
}

.modal-footer {
  padding: 15px;
  border-top: 1px solid #eee;
  text-align: right;
}

.cancel-button {
  padding: 10px 20px;
  font-size: 16px;
  color: #ff4444;
  background-color: #fff;
  border: 1px solid #ff4444;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 10px;
}

.check-payment-button {
  padding: 10px 20px;
  font-size: 16px;
  color: #409eff;
  background-color: #fff;
  border: 1px solid #409eff;
  border-radius: 4px;
  cursor: pointer;
}

/* 支付弹窗 */
.payment-modal-content {
  background-color: #fff;
  border-radius: 8px;
  width: 90%;
  max-width: 800px;
  max-height: 90%;
  display: flex;
  flex-direction: column;
}
</style>