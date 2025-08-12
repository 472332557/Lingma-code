// 导入请求工具
import request from '@/utils/request';

/**
 * 获取启用的轮播图列表
 * @returns Promise
 */
export function getEnabledBanners() {
  return request({
    url: '/banner/enabled',
    method: 'get'
  });
}