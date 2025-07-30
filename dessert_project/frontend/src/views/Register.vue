<template>
  <div class="form-container">
    <h1 class="form-title">用户注册</h1>
    
    <!-- 成功消息提示 -->
    <div v-if="successMessage" class="message message-success">
      {{ successMessage }}
    </div>
    
    <!-- 错误消息提示 -->
    <div v-if="errorMessage" class="message message-error">
      {{ errorMessage }}
    </div>
    
    <!-- 注册表单 -->
    <form @submit.prevent="handleRegister">
      <div class="form-item">
        <label class="form-label">用户名</label>
        <input 
          v-model="registerForm.username" 
          class="form-input" 
          type="text" 
          placeholder="请输入用户名"
          required
        />
      </div>
      
      <div class="form-item">
        <label class="form-label">密码</label>
        <input 
          v-model="registerForm.password" 
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
        {{ loading ? '注册中...' : '注册' }}
      </button>
    </form>
    
    <div class="form-footer">
      <router-link class="form-link" to="/">
        已有账号？立即登录
      </router-link>
    </div>
  </div>
</template>

<script>
// 导入注册API接口
import { register } from '@/api/auth';

export default {
  name: 'Register',
  data() {
    return {
      // 注册表单数据
      registerForm: {
        username: '',
        password: ''
      },
      // 加载状态
      loading: false,
      // 成功消息
      successMessage: '',
      // 错误消息
      errorMessage: ''
    };
  },
  methods: {
    /**
     * 处理注册逻辑
     */
    async handleRegister() {
      // 重置消息
      this.successMessage = '';
      this.errorMessage = '';
      
      // 设置加载状态
      this.loading = true;
      
      try {
        // 调用注册接口
        const response = await register(this.registerForm);
        
        // 判断注册是否成功
        if (response.code === 200) {
          // 注册成功，显示成功消息
          this.successMessage = `注册成功，用户名：${response.data.username}`;
          
          // 清空表单
          this.registerForm.username = '';
          this.registerForm.password = '';
        } else {
          // 注册失败，显示错误消息
          this.errorMessage = response.message;
        }
      } catch (error) {
        // 网络错误或其他异常
        this.errorMessage = '注册失败，请稍后重试';
        console.error('注册失败:', error);
      } finally {
        // 取消加载状态
        this.loading = false;
      }
    }
  }
};
</script>