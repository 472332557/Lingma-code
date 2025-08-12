<template>
  <div class="profile-container">
    <!-- 顶部导航栏 -->
    <div class="header">
      <h1 class="title">我的</h1>
    </div>

    <!-- 用户信息 -->
    <div class="user-info-section" @click="loginOrRegister">
      <div class="user-avatar-placeholder"></div>
      <div class="user-details">
        <h2 class="username" v-if="isLoggedIn">用户名: {{ userInfo.username }}</h2>
        <h2 class="username" v-else>点击登录/注册</h2>
        <p class="user-id" v-if="isLoggedIn">用户ID: {{ userInfo.id }}</p>
      </div>
      <div class="arrow">></div>
    </div>

    <!-- 功能列表 -->
    <div class="function-list">
      <div class="function-item" @click="goToCoupons">
        <div class="function-icon">💰</div>
        <span class="function-text">优惠券</span>
        <div class="arrow">></div>
      </div>
      <div class="function-item" @click="goToOrders">
        <div class="function-icon">📦</div>
        <span class="function-text">我的订单</span>
        <div class="arrow">></div>
      </div>
      <div class="function-item" @click="goToAddresses">
        <div class="function-icon">📍</div>
        <span class="function-text">收货地址</span>
        <div class="arrow">></div>
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
        class="nav-item" 
        @click="setActiveTab('orders')"
      >
        <span>订单</span>
      </div>
      <div 
        class="nav-item active" 
        @click="setActiveTab('profile')"
      >
        <span>我的</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Profile',
  data() {
    return {
      // 是否已登录
      isLoggedIn: false,
      // 用户信息
      userInfo: {
        id: 1,
        username: 'testuser'
      },
      // 当前激活的tab
      activeTab: 'profile'
    };
  },
  methods: {
    /**
     * 登录或注册
     */
    loginOrRegister() {
      if (!this.isLoggedIn) {
        // 跳转到登录页面
        this.$router.push('/login');
      }
    },

    /**
     * 跳转到优惠券页面
     */
    goToCoupons() {
      // 实际项目中这里会跳转到优惠券页面
      alert('跳转到优惠券页面');
    },

    /**
     * 跳转到订单页面
     */
    goToOrders() {
      this.$router.push('/orders');
    },

    /**
     * 跳转到收货地址页面
     */
    goToAddresses() {
      // 实际项目中这里会跳转到收货地址页面
      alert('跳转到收货地址页面');
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
.profile-container {
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

.user-info-section {
  display: flex;
  align-items: center;
  padding: 20px 15px;
  background-color: #fff;
  margin-bottom: 15px;
  cursor: pointer;
}

.user-avatar-placeholder {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: #f5f5f5;
  margin-right: 15px;
}

.user-details {
  flex: 1;
}

.username {
  margin: 0 0 5px 0;
  font-size: 18px;
  color: #333;
}

.user-id {
  margin: 0;
  font-size: 14px;
  color: #999;
}

.arrow {
  font-size: 18px;
  color: #ccc;
}

.function-list {
  background-color: #fff;
}

.function-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
}

.function-item:last-child {
  border-bottom: none;
}

.function-icon {
  font-size: 24px;
  margin-right: 15px;
}

.function-text {
  flex: 1;
  font-size: 16px;
  color: #333;
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