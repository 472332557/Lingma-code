package com.example.loginsystem.controller;

import com.example.loginsystem.common.Result;
import com.example.loginsystem.entity.Banner;
import com.example.loginsystem.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 轮播图控制器
 * 处理轮播图相关请求
 */
@RestController
@RequestMapping("/api/banner")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    /**
     * 获取启用的轮播图列表接口
     * @return 轮播图列表
     */
    @GetMapping("/enabled")
    public Result<List<Banner>> getEnabledBanners() {
        // 获取所有启用的轮播图
        List<Banner> banners = bannerService.getEnabledBanners();

        // 返回结果
        return Result.success(banners);
    }
}