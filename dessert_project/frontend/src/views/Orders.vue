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
              @click="payOrder(order.id, 'wechat')"
            >
              微信支付
            </button>
            <button 
              class="action-button"
              v-if="order.status === 0"
              @click="payOrder(order.id, 'alipay')"
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
      activeTab: 'orders'
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
    async payOrder(orderId, paymentMethod) {
      try {
        if (paymentMethod === 'alipay') {
          // 以隐藏表单方式POST，避免 window.open
          const form = document.createElement('form');
          form.method = 'POST';
          form.action = '/api/payment/alipay';
          form.style.display = 'none';

          const orderIdInput = document.createElement('input');
          orderIdInput.type = 'hidden';
          orderIdInput.name = 'orderId';
          orderIdInput.value = orderId;
          form.appendChild(orderIdInput);

          const amountInput = document.createElement('input');
          amountInput.type = 'hidden';
          amountInput.name = 'amount';
          amountInput.value = (88).toFixed(2); // 示例金额，建议换为订单实际金额
          form.appendChild(amountInput);

          document.body.appendChild(form);
          form.submit();
          document.body.removeChild(form);
        } else if (paymentMethod === 'wechat') {
          // 调用后端微信支付接口
          // 实际项目中这里会跳转到微信支付页面
          alert(`使用微信支付订单 ${orderId}`);
        }
      } catch (error) {
        console.error('支付失败:', error);
        alert('支付失败，请稍后重试');
      }
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
</style>