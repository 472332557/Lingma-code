// 导入Vue Router
import { createRouter, createWebHistory } from 'vue-router';

// 导入页面组件
import Login from '@/views/Login.vue';
import Register from '@/views/Register.vue';
import Home from '@/views/Home.vue';

// 定义路由
const routes = [
  {
    path: '/',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/home',
    name: 'Home',
    component: Home,
    // 路由守卫，判断是否已登录
    beforeEnter: (to, from, next) => {
      // 从localStorage中获取token
      const token = localStorage.getItem('token');
      // 如果token存在，则允许访问
      if (token) {
        next();
      } else {
        // 否则跳转到登录页
        next('/');
      }
    }
  }
];

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
});

// 全局前置守卫，检查路由访问权限
router.beforeEach((to, from, next) => {
  // 获取token
  const token = localStorage.getItem('token');
  
  // 如果访问的不是登录页和注册页，且没有token，则跳转到登录页
  if (to.name !== 'Login' && to.name !== 'Register' && !token) {
    next({ name: 'Login' });
  } else {
    // 否则正常跳转
    next();
  }
});

export default router;