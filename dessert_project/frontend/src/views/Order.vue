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
      modalQuantity: 1
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
      // 实际项目中这里会跳转到结算页面
      alert(`总计: ¥${this.totalAmount.toFixed(2)}，共${this.cartItems.length}件商品`);
      
      // 模拟创建订单并支付流程
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
        
        // 实际项目中这里会调用后端API创建订单
        // const response = await api.post('/api/user/order/create', orderData);
        // if (response.data.code === 200) {
        //   const orderId = response.data.data.orderId;
        //   // 跳转到支付页面
        //   this.payOrder(orderId);
        // } else {
        //   alert('创建订单失败: ' + response.data.message);
        // }
        
        // 模拟创建订单成功
        alert('订单创建成功，准备跳转到支付页面');
        // 模拟订单ID
        const orderId = 1001;
        // 模拟跳转到支付页面
        this.payOrder(orderId);
      } catch (error) {
        console.error('创建订单失败:', error);
        alert('创建订单失败，请稍后重试');
      }
    },
    
    /**
     * 支付订单
     */
    payOrder(orderId) {
      // 实际项目中这里会跳转到支付页面
      // 可以选择微信支付或支付宝支付
      if (confirm('选择支付方式：确定使用支付宝支付，取消使用微信支付')) {
        // 使用支付宝支付
        window.open(`/api/payment/alipay?orderId=${orderId}&amount=${this.totalAmount}`, '_blank');
      } else {
        // 使用微信支付
        alert(`使用微信支付订单 ${orderId}，金额: ¥${this.totalAmount.toFixed(2)}`);
      }
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
</style>