package com.example.loginsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.loginsystem.entity.Banner;
import com.example.loginsystem.mapper.BannerMapper;
import com.example.loginsystem.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 轮播图Service实现类
 * 继承ServiceImpl并实现BannerService接口
 * 实现轮播图相关的业务逻辑方法
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

    @Autowired
    private BannerMapper bannerMapper;

    /**
     * 获取所有启用的轮播图列表
     * @return 轮播图列表
     */
    @Override
    public List<Banner> getEnabledBanners() {
        // 创建查询条件构造器
        QueryWrapper<Banner> queryWrapper = new QueryWrapper<>();
        // 只查询启用的轮播图
        queryWrapper.eq("status", 1);
        // 按排序字段升序排列
        queryWrapper.orderByAsc("sort");
        // 查询并返回结果
        return bannerMapper.selectList(queryWrapper);
    }
}