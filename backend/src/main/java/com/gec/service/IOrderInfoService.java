package com.gec.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gec.domain.bo.OrderInfoBO;
import com.gec.domain.entity.OrderInfo;
import com.gec.domain.search.OrderInfoSearch;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

public interface IOrderInfoService extends IService<OrderInfo> {

    /* 分页查询订单列表 */
    IPage<OrderInfoBO> pageOrder(Page page, OrderInfoSearch search);

    /* 订单详情（含明细） */
    OrderInfoBO getOrderDetail(Integer id);

    /* 发货（待发货→已发货） */
    void shipOrder(Integer id);

    /* 修改价格（仅待付款状态可改） */
    void updatePrice(Integer id, BigDecimal actualAmount);

    /* 取消订单（待付款/待发货→已取消） */
    void cancelOrder(Integer id);

    /* 删除订单（仅已取消/已完成可删） */
    void deleteOrder(Integer id);

    /* 修改备注 */
    void updateRemark(Integer id, String remark);

    /* 批量删除订单（仅已完成/已取消） */
    void batchDelete(List<Integer> ids);

    /* 导出订单CSV */
    void exportOrders(OrderInfoSearch search, HttpServletResponse response);
}
