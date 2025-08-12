package com.example.loginsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.loginsystem.entity.Banner;

import java.util.List;

/**
 * 轮播图Service接口
 * 继承IService接口，获得基本的CRUD操作方法
 * 定义轮播图相关的业务逻辑方法
 */
public interface BannerService extends IService<Banner> {

    /**
     * 获取所有启用的轮播图列表
     * @return 轮播图列表
     */
    List<Banner> getEnabledBanners();
}