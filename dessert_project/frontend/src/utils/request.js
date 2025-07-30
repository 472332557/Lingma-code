// 导入axios
import axios from 'axios';

// 创建axios实例
const service = axios.create({
  // 基础URL，所有请求都会加上这个URL前缀
  baseURL: '/api',
  // 请求超时时间
  timeout: 5000
});

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从localStorage中获取token
    const token = localStorage.getItem('token');
    // 如果token存在，则在请求头中添加Authorization字段
    if (token) {
      config.headers['Authorization'] = token;
    }
    return config;
  },
  error => {
    // 请求错误处理
    console.log(error);
    return Promise.reject(error);
  }
);

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 直接返回响应数据
    return response.data;
  },
  error => {
    // 响应错误处理
    console.log('err' + error);
    return Promise.reject(error);
  }
);

export default service;