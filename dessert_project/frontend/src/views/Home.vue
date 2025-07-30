<template>
  <div class="home-container">
    <h1 class="home-title">欢迎来到登录系统</h1>
    
    <div class="home-content">
      <p>您已成功登录系统！</p>
      <p>这是受保护的页面，只有登录用户才能访问。</p>
    </div>
    
    <button class="home-button" @click="handleLogout">
      退出登录
    </button>
  </div>
</template>

<script>
export default {
  name: 'Home',
  methods: {
    /**
     * 处理退出登录逻辑
     */
    handleLogout() {
      // 清除localStorage中的token
      localStorage.removeItem('token');
      
      // 跳转到登录页
      this.$router.push('/');
    }
  }
};
</script>