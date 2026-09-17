package com.gec.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gec.domain.entity.GoodsInfo;
import com.gec.domain.entity.Member;
import com.gec.domain.entity.OrderInfo;
import com.gec.service.IDashboardService;
import com.gec.service.IGoodsService;
import com.gec.service.IMemberService;
import com.gec.service.IOrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class DashboardServiceImpl implements IDashboardService {

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private IOrderInfoService orderInfoService;

    @Autowired
    private IMemberService memberService;

    /* 1. 顶部统计概览 */
    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> result = new HashMap<>();

        /* 商品统计 */
        int totalGoods = goodsService.count();
        int onSaleGoods = goodsService.count(new QueryWrapper<GoodsInfo>().eq("publish_status", 1));
        result.put("totalGoods", totalGoods);
        result.put("onSaleGoods", onSaleGoods);

        /* 订单统计 */
        int totalOrders = orderInfoService.count();
        int pendingPay = orderInfoService.count(new QueryWrapper<OrderInfo>().eq("order_status", 0));
        int pendingShip = orderInfoService.count(new QueryWrapper<OrderInfo>().eq("order_status", 1));
        int shipped = orderInfoService.count(new QueryWrapper<OrderInfo>().eq("order_status", 2));
        int completed = orderInfoService.count(new QueryWrapper<OrderInfo>().eq("order_status", 3));
        int cancelled = orderInfoService.count(new QueryWrapper<OrderInfo>().eq("order_status", 4));
        result.put("totalOrders", totalOrders);
        result.put("pendingPay", pendingPay);
        result.put("pendingShip", pendingShip);
        result.put("shipped", shipped);
        result.put("completed", completed);
        result.put("cancelled", cancelled);

        /* 销售总额：SQL 聚合，不加载全表 */
        List<Object> sumResult = orderInfoService.listObjs(
            new QueryWrapper<OrderInfo>()
                .eq("order_status", 3)
                .select("COALESCE(SUM(actual_amount), 0)"));
        BigDecimal totalSales = BigDecimal.ZERO;
        if (sumResult != null && !sumResult.isEmpty() && sumResult.get(0) != null) {
            totalSales = new BigDecimal(sumResult.get(0).toString());
        }
        result.put("totalSales", totalSales);

        /* 会员统计 */
        int totalMembers = memberService.count();
        int blacklist = memberService.count(new QueryWrapper<Member>().eq("status", 0));
        result.put("totalMembers", totalMembers);
        result.put("blacklistMembers", blacklist);

        return result;
    }

    /* 2. 订单状态分布（饼图数据） */
    @Override
    public List<Map<String, Object>> getOrderStatus() {
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(buildPieItem("待付款", orderInfoService.count(new QueryWrapper<OrderInfo>().eq("order_status", 0))));
        data.add(buildPieItem("待发货", orderInfoService.count(new QueryWrapper<OrderInfo>().eq("order_status", 1))));
        data.add(buildPieItem("已发货", orderInfoService.count(new QueryWrapper<OrderInfo>().eq("order_status", 2))));
        data.add(buildPieItem("已完成", orderInfoService.count(new QueryWrapper<OrderInfo>().eq("order_status", 3))));
        data.add(buildPieItem("已取消", orderInfoService.count(new QueryWrapper<OrderInfo>().eq("order_status", 4))));
        return data;
    }

    /* 3. 最近订单（5条） */
    @Override
    public List<OrderInfo> getRecentOrders() {
        QueryWrapper<OrderInfo> qw = new QueryWrapper<>();
        qw.orderByDesc("id");
        qw.last("LIMIT 5");
        return orderInfoService.list(qw);
    }

    /* 4. 商品上下架分布（饼图） */
    @Override
    public List<Map<String, Object>> getGoodsStatus() {
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(buildPieItem("已上架", goodsService.count(new QueryWrapper<GoodsInfo>().eq("publish_status", 1))));
        data.add(buildPieItem("已下架", goodsService.count(new QueryWrapper<GoodsInfo>().eq("publish_status", 0))));
        return data;
    }

    /* 5. 会员类型分布（饼图） */
    @Override
    public List<Map<String, Object>> getMemberType() {
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(buildPieItem("普通会员", memberService.count(new QueryWrapper<Member>().eq("member_type", 1))));
        data.add(buildPieItem("VIP会员", memberService.count(new QueryWrapper<Member>().eq("member_type", 2))));
        data.add(buildPieItem("黄金会员", memberService.count(new QueryWrapper<Member>().eq("member_type", 3))));
        return data;
    }

    /* 私有：构建饼图数据项 */
    private Map<String, Object> buildPieItem(String name, int value) {
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("value", value);
        return item;
    }
}
