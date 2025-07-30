// 导入请求工具
import request from '@/utils/request';

/**
 * 用户登录接口
 * @param {Object} data 登录参数 {username, password}
 * @returns Promise
 */
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  });
}

/**
 * 用户注册接口
 * @param {Object} data 注册参数 {username, password}
 * @returns Promise
 */
export function register(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  });
}

/**
 * 测试认证接口
 * @returns Promise
 */
export function authTest() {
  return request({
    url: '/test/auth',
    method: 'get'
  });
}