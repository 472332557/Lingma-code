// 导入Vue Router
import { createRouter, createWebHistory } from 'vue-router';

// 导入页面组件
import Login from '@/views/Login.vue';
import Register from '@/views/Register.vue';
import Home from '@/views/Home.vue';
import ResetPassword from '@/views/ResetPassword.vue';
import Order from '@/views/Order.vue';
import Orders from '@/views/Orders.vue';
import Profile from '@/views/Profile.vue';

// 定义路由
const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/reset-password',
    name: 'ResetPassword',
    component: ResetPassword
  },
  {
    path: '/order',
    name: 'Order',
    component: Order
  },
  {
    path: '/orders',
    name: 'Orders',
    component: Orders
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile
  },
  {
    path: '/home',
    name: 'HomeRedirect',
    redirect: '/'
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
  if (to.name !== 'Login' && to.name !== 'Register' && to.name !== 'ResetPassword' && !token) {
    next({ name: 'Login' });
  } else {
    // 否则正常跳转
    next();
  }
});

export default router;