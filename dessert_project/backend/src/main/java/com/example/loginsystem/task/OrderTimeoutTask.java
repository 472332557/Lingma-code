package com.example.loginsystem.task;

import com.example.loginsystem.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 订单超时处理定时任务
 * 定期检查并取消超时未支付的订单
 */
@Slf4j
@Component
public class OrderTimeoutTask {

    @Autowired
    private OrderService orderService;

    /**
     * 订单超时时间（分钟）
     */
    @Value("${order.timeout-minutes:30}")
    private int timeoutMinutes;

    /**
     * 定时检查超时订单任务
     * 每隔5分钟执行一次（根据配置文件中的check-interval-minutes配置）
     * 也可以使用 @Value("${order.check-interval-minutes:5}") 来读取配置
     */
    @Scheduled(fixedRate = 5 * 60 * 1000) // 5分钟 = 5 * 60 * 1000毫秒
    public void checkTimeoutOrders() {
        try {
            log.info("开始检查超时订单，超时时间: {} 分钟", timeoutMinutes);
            
            // 取消超时订单
            int canceledCount = orderService.cancelTimeoutOrders(timeoutMinutes);
            
            if (canceledCount > 0) {
                log.info("成功取消 {} 个超时订单", canceledCount);
            } else {
                log.debug("没有发现超时订单");
            }
            
        } catch (Exception e) {
            log.error("检查超时订单时发生异常", e);
        }
    }

    /**
     * 应用启动后延迟1分钟执行第一次检查，然后每5分钟执行一次
     * 这个方法提供了另一种定时任务配置方式
     */
    // @Scheduled(initialDelay = 60 * 1000, fixedRate = 5 * 60 * 1000)
    // public void checkTimeoutOrdersWithDelay() {
    //     checkTimeoutOrders();
    // }

    /**
     * 使用cron表达式的定时任务（注释掉，可选择使用）
     * 每5分钟执行一次：0 0/5 * * * ?
     * 每小时执行一次：0 0 * * * ?
     * 每天凌晨2点执行：0 0 2 * * ?
     */
    // @Scheduled(cron = "0 0/5 * * * ?")
    // public void checkTimeoutOrdersByCron() {
    //     checkTimeoutOrders();
    // }
}