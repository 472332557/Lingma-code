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
        { id: 1, name: '草莓奶油蛋糕', description: '新鲜草莓配奶油，口感丰富', price: 128.00, category: 1 },
        { id: 2, name: '巧克力慕斯蛋糕', description: '浓郁巧克力，丝滑慕斯', price: 158.00, category: 1 },
        { id: 3, name: '芒果千层蛋糕', description: '新鲜芒果，层层酥皮', price: 98.00, category: 2 },
        { id: 4, name: '提拉米苏', description: '经典意式甜品', price: 68.00, category: 2 },
        { id: 5, name: '蓝莓芝士蛋糕', description: '新鲜蓝莓配芝士', price: 138.00, category: 2 },
        { id: 6, name: '抹茶红豆蛋糕', description: '日式抹茶配红豆', price: 118.00, category: 1 },
        { id: 7, name: '法式长棍面包', description: '传统法式烘焙', price: 15.00, category: 3 },
        { id: 8, name: '拿铁咖啡', description: '意式浓缩咖啡配牛奶', price: 28.00, category: 4 },
        { id: 9, name: '手工曲奇饼干', description: '纯手工制作，多种口味', price: 38.00, category: 5 }
      ],
      // 当前激活的tab
      activeTab: 'order'
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
      this.$router.push(`/product/${productId}`);
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