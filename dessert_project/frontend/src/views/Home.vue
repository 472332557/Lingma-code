<template>
  <div class="home-container">
    <!-- 顶部导航栏 -->
    <div class="header">
      <h1 class="title">甜品店</h1>
      <div class="user-info" @click="goToProfile">
        <span>我的</span>
      </div>
    </div>

    <!-- 轮播图 -->
    <div class="banner">
      <div class="banner-item" v-for="banner in banners" :key="banner.id">
        <img :src="banner.imageUrl" :alt="banner.title" class="banner-image">
      </div>
    </div>

    <!-- 甜品系列 -->
    <div class="section">
      <h2 class="section-title">甜品系列</h2>
      <div class="category-list">
        <div 
          class="category-item" 
          v-for="category in dessertSeries" 
          :key="category.id"
          @click="goToCategory(category.id)"
        >
          <div class="category-image-placeholder"></div>
          <span class="category-name">{{ category.name }}</span>
        </div>
      </div>
    </div>

    <!-- 其他分类 -->
    <div class="section">
      <h2 class="section-title">其他分类</h2>
      <div class="category-list">
        <div 
          class="category-item" 
          v-for="category in otherCategories" 
          :key="category.id"
          @click="goToCategory(category.id)"
        >
          <div class="category-image-placeholder"></div>
          <span class="category-name">{{ category.name }}</span>
        </div>
      </div>
    </div>

    <!-- 底部导航栏 -->
    <div class="footer-nav">
      <div 
        class="nav-item active" 
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
// 导入轮播图API接口
import { getEnabledBanners } from '@/api/banner';

export default {
  name: 'Home',
  data() {
    return {
      // 轮播图数据
      banners: [],
      // 甜品系列分类
      dessertSeries: [
        { id: 1, name: '女神系列' },
        { id: 2, name: '水果系列' },
        { id: 3, name: '男士系列' },
        { id: 4, name: '鲜花系列' },
        { id: 5, name: '儿童系列' },
        { id: 6, name: '订婚系列' },
        { id: 7, name: '祝寿系列' },
        { id: 8, name: 'INS简约系列' }
      ],
      // 其他分类
      otherCategories: [
        { id: 9, name: '饮品' },
        { id: 10, name: '简餐' },
        { id: 11, name: '手工零食' },
        { id: 12, name: '甜品台' }
      ],
      // 当前激活的tab
      activeTab: 'home'
    };
  },
  mounted() {
    // 页面加载时获取轮播图数据
    this.fetchBanners();
  },
  methods: {
    /**
     * 获取轮播图数据
     */
    async fetchBanners() {
      try {
        const response = await getEnabledBanners();
        if (response.code === 200) {
          this.banners = response.data;
        } else {
          console.error('获取轮播图失败:', response.message);
        }
      } catch (error) {
        console.error('获取轮播图失败:', error);
        // 如果获取失败，使用默认的占位图
        this.banners = [
          { id: 1, imageUrl: 'https://via.placeholder.com/400x200?text=Banner+1' },
          { id: 2, imageUrl: 'https://via.placeholder.com/400x200?text=Banner+2' },
          { id: 3, imageUrl: 'https://via.placeholder.com/400x200?text=Banner+3' }
        ];
      }
    },
    
    /**
     * 跳转到个人中心
     */
    goToProfile() {
      this.$router.push('/profile');
    },

    /**
     * 跳转到分类详情
     */
    goToCategory(categoryId) {
      this.$router.push(`/category/${categoryId}`);
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
.home-container {
  padding-bottom: 60px;
}

.header {
  display: flex;
  justify-content: space-between;
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

.user-info {
  font-size: 16px;
  color: #666;
  cursor: pointer;
}

.banner {
  height: 200px;
  overflow: hidden;
  position: relative;
}

.banner-item {
  width: 100%;
  height: 100%;
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.section {
  margin: 20px 0;
  padding: 0 15px;
}

.section-title {
  font-size: 18px;
  color: #333;
  margin-bottom: 15px;
}

.category-list {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.category-item {
  width: calc(50% - 15px);
  text-align: center;
  cursor: pointer;
}

.category-image-placeholder {
  width: 100%;
  height: 100px;
  background-color: #f5f5f5;
  border-radius: 8px;
  margin-bottom: 10px;
}

.category-name {
  font-size: 14px;
  color: #666;
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