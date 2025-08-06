<template>
  <div class="form-container">
    <h1 class="form-title">用户登录</h1>
    
    <!-- 错误消息提示 -->
    <div v-if="errorMessage" class="message message-error">
      {{ errorMessage }}
    </div>
    
    <!-- 登录表单 -->
    <form @submit.prevent="handleLogin">
      <div class="form-item">
        <label class="form-label">用户名</label>
        <input 
          v-model="loginForm.username" 
          class="form-input" 
          type="text" 
          placeholder="请输入用户名"
          required
        />
      </div>
      
      <div class="form-item">
        <label class="form-label">密码</label>
        <input 
          v-model="loginForm.password" 
          class="form-input" 
          type="password" 
          placeholder="请输入密码"
          required
        />
      </div>
      
      <button 
        class="form-button" 
        type="submit" 
        :disabled="loading"
      >
        {{ loading ? '登录中...' : '登录' }}
      </button>
    </form>
    
    <div class="form-footer">
      <router-link class="form-link" to="/register">
        没有账号？立即注册
      </router-link>
      <br>
      <router-link class="form-link" to="/reset-password">
        忘记密码？
      </router-link>
    </div>
  </div>
</template>

<script>
// 导入登录API接口
import { login } from '@/api/auth';

export default {
  name: 'Login',
  data() {
    return {
      // 登录表单数据
      loginForm: {
        username: '',
        password: ''
      },
      // 加载状态
      loading: false,
      // 错误消息
      errorMessage: ''
    };
  },
  methods: {
    /**
     * 处理登录逻辑
     */
    async handleLogin() {
      // 重置错误消息
      this.errorMessage = '';
      
      // 设置加载状态
      this.loading = true;
      
      try {
        // 调用登录接口
        const response = await login(this.loginForm);
        
        // 判断登录是否成功
        if (response.code === 200) {
          // 登录成功，保存token到localStorage
          localStorage.setItem('token', response.data.token);
          
          // 跳转到首页
          this.$router.push('/home');
        } else {
          // 登录失败，显示错误消息
          this.errorMessage = response.message;
        }
      } catch (error) {
        // 网络错误或其他异常
        this.errorMessage = '登录失败，请稍后重试';
        console.error('登录失败:', error);
      } finally {
        // 取消加载状态
        this.loading = false;
      }
    }
  }
};
</script>