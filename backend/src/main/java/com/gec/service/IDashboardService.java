package com.gec.service;

import com.gec.domain.entity.OrderInfo;

import java.util.List;
import java.util.Map;

public interface IDashboardService {

    /* 1. 顶部统计概览：商品/订单/销售总额/会员 */
    Map<String, Object> getStatistics();

    /* 2. 订单状态分布（饼图数据） */
    List<Map<String, Object>> getOrderStatus();

    /* 3. 最近订单（5条） */
    List<OrderInfo> getRecentOrders();

    /* 4. 商品上下架分布（饼图） */
    List<Map<String, Object>> getGoodsStatus();

    /* 5. 会员类型分布（饼图） */
    List<Map<String, Object>> getMemberType();
}
