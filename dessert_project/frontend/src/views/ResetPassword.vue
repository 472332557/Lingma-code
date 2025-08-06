<template>
  <div class="form-container">
    <h1 class="form-title">重置密码</h1>
    
    <!-- 成功消息提示 -->
    <div v-if="successMessage" class="message message-success">
      {{ successMessage }}
    </div>
    
    <!-- 错误消息提示 -->
    <div v-if="errorMessage" class="message message-error">
      {{ errorMessage }}
    </div>
    
    <!-- 重置密码表单 -->
    <form @submit.prevent="handleResetPassword" v-if="!resetSuccess">
      <div class="form-item">
        <label class="form-label">用户名</label>
        <input 
          v-model="resetForm.username" 
          class="form-input" 
          type="text" 
          placeholder="请输入用户名"
          required
        />
      </div>
      
      <div class="form-item">
        <label class="form-label">新密码</label>
        <input 
          v-model="resetForm.newPassword" 
          class="form-input" 
          type="password" 
          placeholder="请输入新密码"
          required
        />
      </div>
      
      <div class="form-item">
        <label class="form-label">确认新密码</label>
        <input 
          v-model="resetForm.confirmPassword" 
          class="form-input" 
          type="password" 
          placeholder="请再次输入新密码"
          required
        />
      </div>
      
      <button 
        class="form-button" 
        type="submit" 
        :disabled="loading"
      >
        {{ loading ? '重置中...' : '重置密码' }}
      </button>
    </form>
    
    <div class="form-footer" v-if="resetSuccess">
      <router-link class="form-link" to="/">
        返回登录
      </router-link>
    </div>
    
    <div class="form-footer" v-if="!resetSuccess">
      <router-link class="form-link" to="/">
        返回登录
      </router-link>
    </div>
  </div>
</template>

<script>
// 导入重置密码API接口
import { resetPassword } from '@/api/auth';

export default {
  name: 'ResetPassword',
  data() {
    return {
      // 重置密码表单数据
      resetForm: {
        username: '',
        newPassword: '',
        confirmPassword: ''
      },
      // 加载状态
      loading: false,
      // 成功消息
      successMessage: '',
      // 错误消息
      errorMessage: '',
      // 重置是否成功
      resetSuccess: false
    };
  },
  methods: {
    /**
     * 处理重置密码逻辑
     */
    async handleResetPassword() {
      // 重置消息
      this.successMessage = '';
      this.errorMessage = '';
      
      // 验证两次输入的密码是否一致
      if (this.resetForm.newPassword !== this.resetForm.confirmPassword) {
        this.errorMessage = '两次输入的密码不一致';
        return;
      }
      
      // 设置加载状态
      this.loading = true;
      
      try {
        // 构造请求参数
        const requestData = {
          username: this.resetForm.username,
          newPassword: this.resetForm.newPassword
        };
        
        // 调用重置密码接口
        const response = await resetPassword(requestData);
        
        // 判断重置是否成功
        if (response.code === 200) {
          // 重置成功
          this.successMessage = response.data || response.message;
          this.resetSuccess = true;
        } else {
          // 重置失败，显示错误消息
          this.errorMessage = response.message;
        }
      } catch (error) {
        // 网络错误或其他异常
        this.errorMessage = '重置密码失败，请稍后重试';
        console.error('重置密码失败:', error);
      } finally {
        // 取消加载状态
        this.loading = false;
      }
    }
  }
};
</script>