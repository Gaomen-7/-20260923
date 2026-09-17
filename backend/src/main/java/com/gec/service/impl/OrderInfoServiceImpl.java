package com.gec.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gec.dao.OrderInfoMapper;
import com.gec.dao.OrderItemMapper;
import com.gec.domain.bo.OrderInfoBO;
import com.gec.domain.entity.OrderInfo;
import com.gec.domain.entity.OrderItem;
import com.gec.domain.search.OrderInfoSearch;
import com.gec.service.IOrderInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

@Service
public class OrderInfoServiceImpl
    extends ServiceImpl<OrderInfoMapper, OrderInfo>
    implements IOrderInfoService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    @Lazy
    private com.gec.service.IInventoryService inventoryService;

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public IPage<OrderInfoBO> pageOrder(Page page, OrderInfoSearch search) {
        return orderInfoMapper.getOrderList(page, search);
    }

    @Override
    public OrderInfoBO getOrderDetail(Integer id) {
        OrderInfo order = this.getById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        OrderInfoBO bo = new OrderInfoBO();
        BeanUtils.copyProperties(order, bo);
        /* 查询订单明细 */
        QueryWrapper<OrderItem> QW = new QueryWrapper<>();
        QW.eq("order_id", id).orderByAsc("id");
        List<OrderItem> items = orderItemMapper.selectList(QW);
        bo.setItemList(items);
        return bo;
    }

    @Override
    @Transactional
    public void shipOrder(Integer id) {
        OrderInfo order = this.getById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getOrderStatus() != 1) {
            throw new RuntimeException("仅待发货状态的订单可以发货");
        }
        OrderInfo update = new OrderInfo();
        update.setId(id);
        update.setOrderStatus(2);  // 已发货
        update.setUpdateTime(now());
        boolean ret = this.updateById(update);
        if (!ret) {
            throw new RuntimeException("发货失败");
        }
        /* 发货成功后扣减库存 */
        inventoryService.deductByOrder(id, 2, "admin");
    }

    @Override
    @Transactional
    public void updatePrice(Integer id, BigDecimal actualAmount) {
        OrderInfo order = this.getById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getOrderStatus() != 0) {
            throw new RuntimeException("仅待付款状态的订单可以修改价格");
        }
        if (actualAmount == null || actualAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("价格不合法");
        }
        OrderInfo update = new OrderInfo();
        update.setId(id);
        update.setActualAmount(actualAmount);
        update.setUpdateTime(now());
        boolean ret = this.updateById(update);
        if (!ret) {
            throw new RuntimeException("修改价格失败");
        }
    }

    @Override
    @Transactional
    public void cancelOrder(Integer id) {
        OrderInfo order = this.getById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getOrderStatus() != 0 && order.getOrderStatus() != 1 && order.getOrderStatus() != 2) {
            throw new RuntimeException("仅待付款、待发货或已发货状态的订单可以取消");
        }
        boolean needRollback = (order.getOrderStatus() == 2);  // 已发货取消需回滚库存
        OrderInfo update = new OrderInfo();
        update.setId(id);
        update.setOrderStatus(4);
        update.setUpdateTime(now());
        boolean ret = this.updateById(update);
        if (!ret) {
            throw new RuntimeException("取消订单失败");
        }
        if (needRollback) {
            inventoryService.rollbackByOrder(id, 3, "admin");
        }
    }

    @Override
    @Transactional
    public void deleteOrder(Integer id) {
        OrderInfo order = this.getById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (order.getOrderStatus() != 3 && order.getOrderStatus() != 4) {
            throw new RuntimeException("仅已完成或已取消的订单可以删除");
        }
        /* 删除明细 */
        QueryWrapper<OrderItem> QW = new QueryWrapper<>();
        QW.eq("order_id", id);
        orderItemMapper.delete(QW);
        /* 删除主表 */
        boolean ret = this.removeById(id);
        if (!ret) {
            throw new RuntimeException("删除订单失败");
        }
    }

    @Override
    @Transactional
    public void updateRemark(Integer id, String remark) {
        OrderInfo order = this.getById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        OrderInfo update = new OrderInfo();
        update.setId(id);
        update.setRemark(remark);
        update.setUpdateTime(now());
        boolean ret = this.updateById(update);
        if (!ret) {
            throw new RuntimeException("修改备注失败");
        }
    }

    @Override
    @Transactional
    public void batchDelete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("请选择要删除的订单");
        }
        for (Integer id : ids) {
            OrderInfo order = this.getById(id);
            if (order == null) {
                throw new RuntimeException("订单ID=" + id + " 不存在");
            }
            if (order.getOrderStatus() != 3 && order.getOrderStatus() != 4) {
                throw new RuntimeException("订单【" + order.getOrderNo() + "】不是已完成或已取消状态，不能删除");
            }
            /* 删除明细 */
            QueryWrapper<OrderItem> QW = new QueryWrapper<>();
            QW.eq("order_id", id);
            orderItemMapper.delete(QW);
            /* 删除主表 */
            this.removeById(id);
        }
    }

    @Override
    public void exportOrders(OrderInfoSearch search, HttpServletResponse response) {
        /* 复用分页查询的 SQL，不分页，查全部符合条件的数据 */
        List<OrderInfoBO> list = orderInfoMapper.getOrderList(null, search).getRecords();

        response.setContentType("text/csv;charset=UTF-8");
        try {
            String fileName = URLEncoder.encode("订单列表_" + now().replaceAll("[-: ]", "") + ".csv", "UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            OutputStream out = response.getOutputStream();
            /* BOM 头，防止 Excel 打开中文乱码 */
            out.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
            StringBuilder sb = new StringBuilder();
            /* 表头 */
            sb.append("订单编号,会员昵称,会员手机号,收货人,收货电话,收货地址,商品总额,折扣金额,运费,实收款,订单状态,付款状态,备注,下单时间\n");
            /* 数据行 */
            for (OrderInfoBO o : list) {
                sb.append(escapeCsv(o.getOrderNo())).append(",");
                sb.append(escapeCsv(o.getUserName())).append(",");
                sb.append(escapeCsv(o.getUserPhone())).append(",");
                sb.append(escapeCsv(o.getReceiverName())).append(",");
                sb.append(escapeCsv(o.getReceiverPhone())).append(",");
                sb.append(escapeCsv(o.getReceiverAddress())).append(",");
                sb.append(o.getTotalAmount() != null ? o.getTotalAmount() : "").append(",");
                sb.append(o.getDiscountAmount() != null ? o.getDiscountAmount() : "").append(",");
                sb.append(o.getFreight() != null ? o.getFreight() : "").append(",");
                sb.append(o.getActualAmount() != null ? o.getActualAmount() : "").append(",");
                sb.append(o.getOrderStatusName()).append(",");
                sb.append(o.getPayStatusName()).append(",");
                sb.append(escapeCsv(o.getRemark())).append(",");
                sb.append(escapeCsv(o.getCreateTime())).append("\n");
            }
            out.write(sb.toString().getBytes("UTF-8"));
            out.flush();
            out.close();
        } catch (Exception e) {
            throw new RuntimeException("导出失败：" + e.getMessage());
        }
    }

    /* CSV 字段转义：含逗号/引号/换行时用双引号包裹，内部双引号转义为两个 */
    private String escapeCsv(String val) {
        if (val == null) return "";
        if (val.contains(",") || val.contains("\"") || val.contains("\n")) {
            return "\"" + val.replace("\"", "\"\"") + "\"";
        }
        return val;
    }

    private String now() {
        return LocalDateTime.now().format(FMT);
    }
}
