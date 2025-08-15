<template>
  <div class="order-container">
    <!-- 顶部导航栏 -->
    <div class="header">
      <h1 class="title">点单</h1>
    </div>

    <!-- 分类导航 -->
    <div class="category-nav">
      <div 
        class="category-item"
        :class="{ active: activeCategory === null }"
        @click="selectCategory(null)"
      >
        全部
      </div>
      <div 
        class="category-item"
        :class="{ active: activeCategory === 1 }"
        @click="selectCategory(1)"
      >
        生日蛋糕定制
      </div>
      <div 
        class="category-item"
        :class="{ active: activeCategory === 2 }"
        @click="selectCategory(2)"
      >
        甜品
      </div>
      <div 
        class="category-item"
        :class="{ active: activeCategory === 3 }"
        @click="selectCategory(3)"
      >
        现烤面包
      </div>
      <div 
        class="category-item"
        :class="{ active: activeCategory === 4 }"
        @click="selectCategory(4)"
      >
        饮品
      </div>
      <div 
        class="category-item"
        :class="{ active: activeCategory === 5 }"
        @click="selectCategory(5)"
      >
        休闲办公小零食
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="product-list">
      <div 
        class="product-item" 
        v-for="product in filteredProducts" 
        :key="product.id"
        @click="goToProductDetail(product.id)"
      >
        <div class="product-image-placeholder"></div>
        <div class="product-info">
          <h3 class="product-name">{{ product.name }}</h3>
          <p class="product-description">{{ product.description }}</p>
          <div class="product-price">¥{{ product.price }}</div>
        </div>
      </div>
    </div>
    
    <!-- 购物车 -->
    <div class="cart" v-if="cartItems.length > 0">
      <div class="cart-header">
        <span>购物车 ({{ cartItems.length }})</span>
        <button class="clear-cart" @click="clearCart">清空</button>
      </div>
      <div class="cart-items">
        <div 
          class="cart-item" 
          v-for="item in cartItems" 
          :key="item.id"
        >
          <span class="item-name">{{ item.name }}</span>
          <div class="item-spec">{{ item.specName }}</div>
          <div class="item-controls">
            <button @click="decreaseQuantity(item)">-</button>
            <span class="item-quantity">{{ item.quantity }}</span>
            <button @click="increaseQuantity(item)">+</button>
          </div>
          <span class="item-subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
        </div>
      </div>
      <div class="cart-footer">
        <div class="total">总计: ¥{{ totalAmount.toFixed(2) }}</div>
        <button class="checkout-button" @click="checkout">去结算</button>
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
        class="nav-item active" 
        @click="setActiveTab('order')"
      >
        <span>点单</span>
      </div>
      <div 
        class="nav-item" 
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
    
    <!-- 规格选择弹窗 -->
    <div class="modal" v-if="showSpecModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ selectedProduct.name }}</h3>
          <button class="close-button" @click="closeSpecModal">&times;</button>
        </div>
        <div class="modal-body">
          <div class="product-image-placeholder-large"></div>
          <div class="specs">
            <div 
              class="spec-item" 
              v-for="spec in productSpecs" 
              :key="spec.id"
              :class="{ active: selectedSpec && selectedSpec.id === spec.id }"
              @click="selectSpec(spec)"
            >
              {{ spec.name }} - ¥{{ spec.price }}
            </div>
          </div>
          <div class="quantity-control">
            <label>数量:</label>
            <div class="quantity-controls">
              <button @click="decreaseModalQuantity">-</button>
              <span class="quantity">{{ modalQuantity }}</span>
              <button @click="increaseModalQuantity">+</button>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="add-to-cart-button" @click="addToCart">加入购物车</button>
        </div>
      </div>
    </div>


    <!-- 通用消息弹窗 -->
    <div class="modal" v-if="showMessageModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>提示</h3>
          <button class="close-button" @click="closeMessageModal">&times;</button>
        </div>
        <div class="modal-body">
          <p>{{ messageText }}</p>
        </div>
        <div class="modal-footer">
          <button class="add-to-cart-button" @click="closeMessageModal">我知道了</button>
        </div>
      </div>
    </div>
    
    <!-- 支付方式选择弹窗 -->
    <div class="modal" v-if="showPaymentMethodModal" @click="closePaymentMethodModal">
      <div class="payment-method-modal-content" @click.stop>
        <div class="modal-header">
          <h3>选择支付方式</h3>
          <button class="close-button" @click="closePaymentMethodModal">&times;</button>
        </div>
        <div class="modal-body">
          <div class="payment-method-item" @click="chooseAlipay">
            <div class="payment-method-icon">💰</div>
            <div class="payment-method-name">支付宝</div>
          </div>
          <div class="payment-method-item" @click="chooseWechat">
            <div class="payment-method-icon">💬</div>
            <div class="payment-method-name">微信支付</div>
          </div>
        </div>
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
export default {
  name: 'Order',
  data() {
    return {
      // 当前激活的分类
      activeCategory: null,
      // 所有商品列表
      allProducts: [
        // 测试蛋糕商品 - 添加于2025-05-13
        { 
          id: 99, 
          name: '测试蛋糕', 
          description: '这是一个用于测试下单流程的蛋糕商品', 
          price: 99.00, 
          category: 1,
          specs: [
            { id: 991, name: '小号', price: 89.00 },
            { id: 992, name: '中号', price: 99.00 },
            { id: 993, name: '大号', price: 109.00 }
          ]
        },
        { 
          id: 1, 
          name: '草莓奶油蛋糕', 
          description: '新鲜草莓配奶油，口感丰富', 
          price: 128.00, 
          category: 1,
          specs: [
            { id: 1, name: '4寸', price: 88.00 },
            { id: 2, name: '6寸', price: 128.00 },
            { id: 3, name: '8寸', price: 168.00 }
          ]
        },
        { 
          id: 2, 
          name: '巧克力慕斯蛋糕', 
          description: '浓郁巧克力，丝滑慕斯', 
          price: 158.00, 
          category: 1,
          specs: [
            { id: 4, name: '4寸', price: 98.00 },
            { id: 5, name: '6寸', price: 158.00 },
            { id: 6, name: '8寸', price: 198.00 }
          ]
        },
        { 
          id: 3, 
          name: '芒果千层蛋糕', 
          description: '新鲜芒果，层层酥皮', 
          price: 98.00, 
          category: 2,
          specs: [
            { id: 7, name: '4寸', price: 68.00 },
            { id: 8, name: '6寸', price: 98.00 },
            { id: 9, name: '8寸', price: 128.00 }
          ]
        },
        { 
          id: 4, 
          name: '提拉米苏', 
          description: '经典意式甜品', 
          price: 68.00, 
          category: 2,
          specs: [
            { id: 10, name: '单份', price: 68.00 }
          ]
        },
        { 
          id: 5, 
          name: '蓝莓芝士蛋糕', 
          description: '新鲜蓝莓配芝士', 
          price: 138.00, 
          category: 2,
          specs: [
            { id: 11, name: '4寸', price: 98.00 },
            { id: 12, name: '6寸', price: 138.00 },
            { id: 13, name: '8寸', price: 178.00 }
          ]
        },
        { 
          id: 6, 
          name: '抹茶红豆蛋糕', 
          description: '日式抹茶配红豆', 
          price: 118.00, 
          category: 1,
          specs: [
            { id: 14, name: '4寸', price: 78.00 },
            { id: 15, name: '6寸', price: 118.00 },
            { id: 16, name: '8寸', price: 158.00 }
          ]
        },
        { 
          id: 7, 
          name: '法式长棍面包', 
          description: '传统法式烘焙', 
          price: 15.00, 
          category: 3,
          specs: [
            { id: 17, name: '一根', price: 15.00 }
          ]
        },
        { 
          id: 8, 
          name: '拿铁咖啡', 
          description: '意式浓缩咖啡配牛奶', 
          price: 28.00, 
          category: 4,
          specs: [
            { id: 18, name: '中杯', price: 28.00 },
            { id: 19, name: '大杯', price: 32.00 }
          ]
        },
        { 
          id: 9, 
          name: '手工曲奇饼干', 
          description: '纯手工制作，多种口味', 
          price: 38.00, 
          category: 5,
          specs: [
            { id: 20, name: '一盒', price: 38.00 }
          ]
        }
      ],
      // 购物车项目
      cartItems: [],
      // 当前激活的tab
      activeTab: 'order',
      // 规格选择弹窗
      showSpecModal: false,
      // 选中的商品
      selectedProduct: null,
      // 商品规格
      productSpecs: [],
      // 选中的规格
      selectedSpec: null,
      // 弹窗中的数量
      modalQuantity: 1,
      // 消息弹窗
      showMessageModal: false,
      messageText: '',
      // 支付弹窗
      showPaymentModal: false,
      paymentUrl: '',
      paymentAmount: 0,
      paymentOrderNumber: '',
      paymentOrderId: 0,
      // 支付方式选择弹窗
      showPaymentMethodModal: false,
      // 待支付的订单信息
      pendingPaymentOrderId: 0,
      pendingPaymentAmount: 0,
      pendingPaymentOrderNumber: ''
    };
  },
  computed: {
    /**
     * 根据当前选择的分类过滤商品
     */
    filteredProducts() {
      if (this.activeCategory === null) {
        // 如果选择了"全部"，返回所有商品
        return this.allProducts;
      } else {
        // 否则只返回对应分类的商品
        return this.allProducts.filter(product => product.category === this.activeCategory);
      }
    },
    
    /**
     * 计算购物车总金额
     */
    totalAmount() {
      return this.cartItems.reduce((total, item) => {
        return total + (item.price * item.quantity);
      }, 0);
    }
  },
  methods: {
    /**
     * 选择分类
     */
    selectCategory(categoryId) {
      this.activeCategory = categoryId;
      // 实际项目中这里会调用API获取对应分类的商品
    },

    /**
     * 跳转到商品详情
     */
    goToProductDetail(productId) {
      // 找到选中的商品
      const product = this.allProducts.find(p => p.id === productId);
      if (product) {
        this.selectedProduct = product;
        this.productSpecs = product.specs || [];
        this.selectedSpec = this.productSpecs.length > 0 ? this.productSpecs[0] : null;
        this.modalQuantity = 1;
        this.showSpecModal = true;
      }
    },
    
    /**
     * 选择规格
     */
    selectSpec(spec) {
      this.selectedSpec = spec;
    },
    
    /**
     * 增加弹窗中的数量
     */
    increaseModalQuantity() {
      this.modalQuantity++;
    },
    
    /**
     * 减少弹窗中的数量
     */
    decreaseModalQuantity() {
      if (this.modalQuantity > 1) {
        this.modalQuantity--;
      }
    },
    
    /**
     * 关闭规格选择弹窗
     */
    closeSpecModal() {
      this.showSpecModal = false;
      this.selectedProduct = null;
      this.selectedSpec = null;
      this.modalQuantity = 1;
    },
    
    /**
     * 添加到购物车
     */
    addToCart() {
      if (!this.selectedProduct) return;
      
      // 创建购物车项目
      const cartItem = {
        id: this.selectedProduct.id + (this.selectedSpec ? '_' + this.selectedSpec.id : ''),
        productId: this.selectedProduct.id,
        specId: this.selectedSpec ? this.selectedSpec.id : null,
        name: this.selectedProduct.name,
        specName: this.selectedSpec ? this.selectedSpec.name : '标准',
        price: this.selectedSpec ? this.selectedSpec.price : this.selectedProduct.price,
        quantity: this.modalQuantity
      };
      
      // 检查购物车中是否已存在相同商品和规格
      const existingItem = this.cartItems.find(item => 
        item.productId === cartItem.productId && item.specId === cartItem.specId);
      
      if (existingItem) {
        // 如果已存在，增加数量
        existingItem.quantity += cartItem.quantity;
      } else {
        // 如果不存在，添加新项目
        this.cartItems.push(cartItem);
      }
      
      // 关闭弹窗
      this.closeSpecModal();
    },
    
    /**
     * 增加购物车中商品数量
     */
    increaseQuantity(item) {
      item.quantity++;
    },
    
    /**
     * 减少购物车中商品数量
     */
    decreaseQuantity(item) {
      if (item.quantity > 1) {
        item.quantity--;
      } else {
        // 如果数量为1，移除商品
        this.removeItem(item);
      }
    },
    
    /**
     * 移除购物车中的商品
     */
    removeItem(item) {
      const index = this.cartItems.indexOf(item);
      if (index > -1) {
        this.cartItems.splice(index, 1);
      }
    },
    
    /**
     * 清空购物车
     */
    clearCart() {
      this.cartItems = [];
    },
    
    /**
     * 去结算
     */
    checkout() {
      // 检查用户是否已登录
      const token = localStorage.getItem('token');
      if (!token) {
        this.showMessage('请先登录后再进行结算');
        this.$router.push('/login');
        return;
      }

      // 检查购物车是否为空
      if (this.cartItems.length === 0) {
        this.showMessage('购物车为空，无法结算');
        return;
      }

      // 实际项目中这里会跳转到结算页面
      this.showMessage(`总计: ¥${this.totalAmount.toFixed(2)}，共${this.cartItems.length}件商品`);
      
      // 创建订单并支付流程
      // 1. 创建订单
      // 2. 跳转到支付页面
      this.createOrder();
    },
    
    /**
     * 创建订单
     */
    async createOrder() {
      try {
        // 构造订单数据
        const orderData = {
          orderItems: this.cartItems.map(item => ({
            dessertId: item.productId,
            specId: item.specId,
            quantity: item.quantity
          })),
          addressId: 1, // 示例地址ID
          remark: "用户备注信息"
        };
        
        console.log('准备创建订单，数据:', orderData);
        console.log('用户token:', localStorage.getItem('token'));
        
        // 调用后端API创建订单
        const response = await fetch('/api/user/order/create', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': localStorage.getItem('token') || ''
          },
          body: JSON.stringify(orderData)
        });
        
        console.log('订单创建响应状态:', response.status);
        console.log('订单创建响应头:', response.headers);

        if (response.ok) {
          const result = await response.json();
          console.log('订单创建响应数据:', result);
          if (result.code === 200) {
            const orderId = result.data.orderId;
            const totalAmount = result.data.totalAmount;
            const orderNumber = result.data.orderNumber;
            this.showMessage('订单创建成功，准备跳转到支付页面');
            // 跳转到支付页面
            this.openPaymentModal(orderId, totalAmount, orderNumber);
          } else {
            this.showMessage('创建订单失败: ' + (result.message || '未知错误'));
          }
        } else if (response.status === 401) {
          this.showMessage('登录已过期，请重新登录');
          this.$router.push('/login');
        } else {
          const errorText = await response.text();
          console.error('订单创建失败，响应内容:', errorText);
          this.showMessage('创建订单失败: ' + errorText);
        }
      } catch (error) {
        console.error('创建订单失败:', error);
        this.showMessage('创建订单失败，请稍后重试');
      }
    },
    
    /**
     * 打开/关闭支付方式弹窗
     */
    openPaymentModal(orderId, amount, orderNumber) {
      this.pendingPaymentOrderId = orderId;
      this.pendingPaymentAmount = Number(amount) || 0;
      this.pendingPaymentOrderNumber = orderNumber || '';
      this.showPaymentMethodModal = true;
    },
    closePaymentMethodModal() {
      this.showPaymentMethodModal = false;
    },
    closePaymentModal() {
      this.showPaymentModal = false;
      this.paymentUrl = '';
      
      // 检查支付状态
      setTimeout(() => {
        this.checkPaymentStatus(this.paymentOrderId);
      }, 2000);
    },

    // 选择支付方式
    chooseAlipay() {
      this.closePaymentMethodModal();
      this.alipay(this.pendingPaymentOrderId, this.pendingPaymentAmount);
    },
    chooseWechat() {
      this.closePaymentMethodModal();
      this.wechatPay(this.pendingPaymentOrderId, this.pendingPaymentAmount);
    },
    
    // 微信支付
    async wechatPay(orderId, amount) {
      try {
        this.showMessage('正在创建微信支付订单，请稍候...');
        
        const response = await fetch('/api/payment/wechat', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
          },
          body: new URLSearchParams({
            orderId: orderId,
            amount: Number(amount).toFixed(2)
          })
        });
        
        const result = await response.json();
        
        if (result.code === 200) {
          // 微信支付参数
          const payParams = result.data;
          
          // 调用微信支付JSAPI
          if (typeof WeixinJSBridge !== "undefined") {
            WeixinJSBridge.invoke('getBrandWCPayRequest', {
              "appId": payParams.appId,
              "timeStamp": payParams.timeStamp,
              "nonceStr": payParams.nonceStr,
              "package": payParams.package,
              "signType": payParams.signType,
              "paySign": payParams.paySign
            }, (res) => {
              if (res.err_msg == "get_brand_wcpay_request:ok") {
                // 支付成功
                this.showMessage('支付成功');
                // 可以跳转到支付成功页面或刷新订单状态
                setTimeout(() => {
                  this.$router.push('/orders');
                }, 2000);
              } else {
                // 支付失败或取消
                this.showMessage('支付失败或已取消');
              }
            });
          } else {
            this.showMessage('请在微信客户端中打开');
          }
        } else {
          this.showMessage('微信支付创建失败: ' + (result.message || '未知错误'));
        }
      } catch (error) {
        console.error('微信支付失败:', error);
        this.showMessage('微信支付失败，请稍后重试');
      }
    },

    /**
     * 支付宝支付（使用FormData提交）
     */
    async alipay(orderId, amount) {
      try {
        this.showMessage('正在创建支付订单，请稍候...');
        
        // 使用FormData提交，匹配后端的@RequestParam
        const formData = new FormData();
        formData.append('orderId', orderId);
        formData.append('amount', Number(amount).toFixed(2));

        const response = await fetch('/api/payment/alipay', {
          method: 'POST',
          body: formData
        });

        if (response.ok) {
          // 获取HTML响应并显示
          const html = await response.text();
          
          // 创建支付页面
          const blob = new Blob([html], { type: 'text/html' });
          const url = window.URL.createObjectURL(blob);
          
          // 在弹窗中显示支付页面
          this.paymentUrl = url;
          this.paymentAmount = amount;
          this.paymentOrderId = orderId;
          this.paymentOrderNumber = this.pendingPaymentOrderNumber; // 使用之前保存的订单号
          this.showPaymentModal = true;
          
          this.showMessage('支付页面已打开，请在支付宝页面完成支付');
        } else if (response.status === 400) {
          const errorText = await response.text();
          this.showMessage('支付创建失败: ' + errorText);
        } else {
          throw new Error(`HTTP ${response.status}: ${response.statusText}`);
        }
      } catch (error) {
        console.error('支付宝支付失败:', error);
        this.showMessage('支付宝支付失败，请稍后重试');
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
      //       this.showMessage('支付成功');
      //     } else {
      //       this.showMessage('支付未完成，请继续支付或取消订单');
      //     }
      //   }
      // } catch (error) {
      //   console.error('检查支付状态失败:', error);
      //   this.showMessage('检查支付状态失败');
      // }
      
      this.showMessage('支付状态检查完成');
    },

    // 通用消息弹窗
    showMessage(text) {
      this.messageText = text || '';
      this.showMessageModal = true;
    },
    
    // 关闭消息弹窗
    closeMessageModal() {
      this.showMessageModal = false;
      this.messageText = '';
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
.order-container {
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

.category-nav {
  display: flex;
  overflow-x: auto;
  padding: 15px;
  background-color: #fff;
  border-bottom: 1px solid #eee;
}

.category-item {
  flex-shrink: 0;
  padding: 8px 15px;
  margin-right: 10px;
  font-size: 14px;
  color: #666;
  background-color: #f5f5f5;
  border-radius: 20px;
  cursor: pointer;
  white-space: nowrap;
}

.category-item.active {
  color: #fff;
  background-color: #409eff;
}

.product-list {
  padding: 15px;
}

.product-item {
  display: flex;
  padding: 15px;
  margin-bottom: 15px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
  cursor: pointer;
}

.product-image-placeholder {
  width: 100px;
  height: 100px;
  background-color: #f5f5f5;
  border-radius: 8px;
  margin-right: 15px;
}

.product-info {
  flex: 1;
}

.product-name {
  margin: 0 0 10px 0;
  font-size: 16px;
  color: #333;
}

.product-description {
  margin: 0 0 15px 0;
  font-size: 14px;
  color: #999;
  line-height: 1.4;
}

.product-price {
  font-size: 18px;
  color: #ff4444;
  font-weight: bold;
}

/* 购物车样式 */
.cart {
  position: fixed;
  bottom: 60px;
  left: 0;
  right: 0;
  background-color: #fff;
  box-shadow: 0 -2px 10px rgba(0,0,0,0.1);
  z-index: 99;
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  border-bottom: 1px solid #eee;
}

.clear-cart {
  padding: 5px 10px;
  font-size: 12px;
  color: #ff4444;
  background-color: #fff;
  border: 1px solid #ff4444;
  border-radius: 4px;
  cursor: pointer;
}

.cart-items {
  max-height: 200px;
  overflow-y: auto;
}

.cart-item {
  display: flex;
  align-items: center;
  padding: 10px 15px;
  border-bottom: 1px solid #eee;
}

.item-name {
  flex: 2;
  font-size: 14px;
  color: #333;
}

.item-spec {
  flex: 1;
  font-size: 12px;
  color: #999;
}

.item-controls {
  display: flex;
  align-items: center;
  margin: 0 10px;
}

.item-controls button {
  width: 24px;
  height: 24px;
  font-size: 14px;
  color: #409eff;
  background-color: #fff;
  border: 1px solid #409eff;
  border-radius: 50%;
  cursor: pointer;
}

.item-quantity {
  margin: 0 8px;
  font-size: 14px;
  color: #333;
}

.item-subtotal {
  flex: 1;
  text-align: right;
  font-size: 14px;
  color: #ff4444;
  font-weight: bold;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background-color: #f5f5f5;
}

.total {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.checkout-button {
  padding: 10px 20px;
  font-size: 16px;
  color: #fff;
  background-color: #409eff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.checkout-button:hover {
  background-color: #337ecc;
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
    
.payment-methods {
    margin-top: 20px;
}

.payment-method {
    display: flex;
    align-items: center;
    padding: 15px;
    margin-bottom: 10px;
    background-color: #f5f5f5;
    border-radius: 8px;
    cursor: pointer;
}

.payment-method:hover {
    background-color: #ecf5ff;
}

.method-icon {
    width: 40px;
    height: 40px;
    background-color: #ddd;
    border-radius: 50%;
    margin-right: 15px;
}

.method-name {
    font-size: 16px;
    color: #333;
}

.payment-status-container {
    width: 100%;
    height: 200px;
    margin-top: 20px;
    background-color: #f5f5f5;
    border-radius: 8px;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .payment-status {
    text-align: center;
    padding: 20px;
  }

  .payment-status p {
    margin: 10px 0;
    color: #333;
    font-size: 16px;
  }
    
.loading {
  text-align: center;
  padding: 50px;
  color: #999;
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
    
/* 消息弹窗 */
.message-modal-content {
  background-color: #fff;
  border-radius: 8px;
  width: 90%;
  max-width: 300px;
}
    
.message-modal-content .modal-body {
  padding: 30px 15px;
  text-align: center;
}
    
.message-modal-content .modal-body p {
  margin: 0;
  font-size: 16px;
  color: #333;
}
    
.message-modal-content .modal-footer {
  padding: 15px;
  border-top: 1px solid #eee;
  text-align: center;
}
    
.message-modal-content .modal-footer button {
  padding: 8px 20px;
  font-size: 16px;
  color: #409eff;
  background-color: #fff;
  border: 1px solid #409eff;
  border-radius: 4px;
  cursor: pointer;
}

/* 规格选择弹窗 */
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

.modal-content {
  background-color: #fff;
  border-radius: 8px;
  width: 90%;
  max-width: 400px;
  max-height: 80%;
  overflow-y: auto;
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

.product-image-placeholder-large {
  width: 100%;
  height: 200px;
  background-color: #f5f5f5;
  border-radius: 8px;
  margin-bottom: 15px;
}

.specs {
  margin-bottom: 20px;
}

.spec-item {
  padding: 10px;
  margin-bottom: 10px;
  border: 1px solid #eee;
  border-radius: 4px;
  cursor: pointer;
}

.spec-item.active {
  border-color: #409eff;
  background-color: #ecf5ff;
  color: #409eff;
}

.quantity-control {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.quantity-controls {
  display: flex;
  align-items: center;
}

.quantity-controls button {
  width: 30px;
  height: 30px;
  font-size: 16px;
  color: #409eff;
  background-color: #fff;
  border: 1px solid #409eff;
  border-radius: 50%;
  cursor: pointer;
}

.quantity-controls .quantity {
  margin: 0 15px;
  font-size: 16px;
  color: #333;
}

.modal-footer {
  padding: 15px;
  border-top: 1px solid #eee;
  text-align: center;
}

.add-to-cart-button {
  width: 100%;
  padding: 12px;
  font-size: 16px;
  color: #fff;
  background-color: #409eff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.add-to-cart-button:hover {
  background-color: #337ecc;
}

/* 支付方式选择弹窗 */
.payment-method-modal-content {
  background-color: #fff;
  border-radius: 8px;
  width: 90%;
  max-width: 300px;
}

.payment-method-item {
  display: flex;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
}

.payment-method-item:last-child {
  border-bottom: none;
}

.payment-method-icon {
  font-size: 24px;
  margin-right: 15px;
}

.payment-method-name {
  font-size: 16px;
  color: #333;
}
