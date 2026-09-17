package com.gec.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.dao.InventoryLogMapper;
import com.gec.dao.OrderItemMapper;
import com.gec.dao.SkuInfoMapper;
import com.gec.domain.entity.GoodsInfo;
import com.gec.domain.entity.InventoryLog;
import com.gec.domain.entity.OrderInfo;
import com.gec.domain.entity.OrderItem;
import com.gec.domain.entity.SkuInfo;
import com.gec.service.IGoodsService;
import com.gec.service.IInventoryService;
import com.gec.service.IOrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements IInventoryService {

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private InventoryLogMapper inventoryLogMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    @Lazy
    private IOrderInfoService orderInfoService;

    private static final DateTimeFormatter LOG_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /* 1. 库存统计：商品总数、库存总量、低库存数、库存总价值 */
    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> result = new HashMap<>();
        result.put("totalGoods", goodsService.count());
        result.put("onSaleGoods", goodsService.count(
                new QueryWrapper<GoodsInfo>().eq("publish_status", 1)));

        /* 只查需要的列，避免全表加载大对象 */
        List<GoodsInfo> all = goodsService.list(
                new QueryWrapper<GoodsInfo>().select("stock", "price"));
        int totalStock = 0;
        int lowStockCount = 0;
        BigDecimal totalValue = BigDecimal.ZERO;
        for (GoodsInfo g : all) {
            int stock = g.getStock() != null ? g.getStock() : 0;
            totalStock += stock;
            if (stock < 20) {
                lowStockCount++;
            }
            BigDecimal price = g.getPrice() != null ? g.getPrice() : BigDecimal.ZERO;
            totalValue = totalValue.add(price.multiply(new BigDecimal(stock)));
        }
        result.put("totalStock", totalStock);
        result.put("lowStockCount", lowStockCount);
        result.put("totalValue", totalValue);
        return result;
    }

    /* 2. 库存列表（分页，基于商品，聚合SKU数量，无N+1） */
    @Override
    public Map<String, Object> pageInventory(int page, int limit, Map<String, Object> param) {
        QueryWrapper<GoodsInfo> qw = new QueryWrapper<>();
        String keyword = param.get("keyword") != null ? param.get("keyword").toString() : null;
        Integer categoryId = param.get("categoryId") != null ? Integer.valueOf(param.get("categoryId").toString()) : null;
        Integer brandId = param.get("brandId") != null ? Integer.valueOf(param.get("brandId").toString()) : null;
        String stockStatus = param.get("stockStatus") != null ? param.get("stockStatus").toString() : null;

        if (keyword != null && !keyword.trim().isEmpty()) {
            qw.like("goods_name", keyword.trim());
        }
        if (categoryId != null) {
            qw.eq("category_id", categoryId);
        }
        if (brandId != null) {
            qw.eq("brand_id", brandId);
        }
        if ("low".equals(stockStatus)) {
            qw.lt("stock", 20);
        } else if ("out".equals(stockStatus)) {
            qw.eq("stock", 0);
        } else if ("normal".equals(stockStatus)) {
            qw.ge("stock", 20);
        }
        qw.orderByAsc("stock");

        IPage<GoodsInfo> pageResult = goodsService.page(new Page<>(page, limit), qw);
        List<GoodsInfo> records = pageResult.getRecords();

        /* 消除 N+1：一次查出当前页所有商品的 SKU，内存分组计数 */
        Map<Integer, Integer> skuCountMap = new HashMap<>();
        if (!records.isEmpty()) {
            List<Integer> goodsIds = records.stream().map(GoodsInfo::getId).collect(Collectors.toList());
            List<SkuInfo> allSkus = skuInfoMapper.selectList(
                    new QueryWrapper<SkuInfo>().in("goods_id", goodsIds).select("goods_id"));
            Map<Integer, Long> countMap = allSkus.stream()
                    .collect(Collectors.groupingBy(SkuInfo::getGoodsId, Collectors.counting()));
            for (Map.Entry<Integer, Long> e : countMap.entrySet()) {
                skuCountMap.put(e.getKey(), e.getValue().intValue());
            }
        }

        List<Map<String, Object>> rows = new ArrayList<>();
        for (GoodsInfo g : records) {
            Map<String, Object> row = new HashMap<>();
            row.put("goodsId", g.getId());
            row.put("goodsName", g.getGoodsName());
            row.put("goodsSn", g.getGoodsSn());
            row.put("categoryId", g.getCategoryId());
            row.put("brandId", g.getBrandId());
            row.put("price", g.getPrice());
            row.put("stock", g.getStock());
            row.put("saleCount", g.getSaleCount());
            row.put("skuCount", skuCountMap.getOrDefault(g.getId(), 0));
            rows.add(row);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("data", rows);
        result.put("total", pageResult.getTotal());
        return result;
    }

    /* 3. 某商品的SKU库存明细 */
    @Override
    public List<SkuInfo> getSkuDetail(Integer goodsId) {
        QueryWrapper<SkuInfo> qw = new QueryWrapper<>();
        qw.eq("goods_id", goodsId);
        qw.orderByDesc("sku_id");
        return skuInfoMapper.selectList(qw);
    }

    /* 4. 调整商品库存 */
    @Override
    @Transactional
    public void adjustStock(Integer goodsId, Integer stock) {
        if (goodsId == null || stock == null) {
            throw new RuntimeException("参数不完整");
        }
        if (stock < 0) {
            throw new RuntimeException("库存不能为负数");
        }
        GoodsInfo g = goodsService.getById(goodsId);
        if (g == null) {
            throw new RuntimeException("商品不存在");
        }
        int before = g.getStock() != null ? g.getStock() : 0;
        g.setStock(stock);
        boolean ret = goodsService.updateById(g);
        if (!ret) {
            throw new RuntimeException("调整库存失败");
        }
        writeLog(goodsId, null, 5, stock - before, before, stock, null, "admin", "手动调整商品库存");
    }

    /* 5. 调整SKU库存 */
    @Override
    @Transactional
    public void adjustSkuStock(Integer skuId, Integer stock) {
        if (skuId == null || stock == null) {
            throw new RuntimeException("参数不完整");
        }
        if (stock < 0) {
            throw new RuntimeException("库存不能为负数");
        }
        SkuInfo sku = skuInfoMapper.selectById(skuId);
        if (sku == null) {
            throw new RuntimeException("SKU不存在");
        }
        int before = sku.getStock() != null ? sku.getStock() : 0;
        sku.setStock(stock);
        int ret = skuInfoMapper.updateById(sku);
        if (ret <= 0) {
            throw new RuntimeException("调整SKU库存失败");
        }
        writeLog(sku.getGoodsId(), skuId, 5, stock - before, before, stock, null, "admin", "手动调整SKU库存");
        syncGoodsStock(sku.getGoodsId());
    }

    @Override
    @Transactional
    public void deductByOrder(Integer orderId, Integer changeType, String operator) {
        OrderInfo order = orderInfoService.getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        List<OrderItem> items = orderItemMapper.selectList(
                new QueryWrapper<OrderItem>().eq("order_id", orderId));
        if (items.isEmpty()) {
            throw new RuntimeException("订单无明细，无法扣减库存");
        }
        Set<Integer> goodsIds = new HashSet<>();
        for (OrderItem item : items) {
            int qty = item.getQuantity() != null ? item.getQuantity() : 1;
            if (item.getSkuId() != null) {
                deductSku(item.getSkuId(), qty, changeType, order.getOrderNo(), operator, "订单发货扣减");
                goodsIds.add(item.getGoodsId());
            }
        }
        /* 同步涉及商品的总库存 */
        for (Integer gid : goodsIds) {
            syncGoodsStock(gid);
        }
    }

    @Override
    @Transactional
    public void rollbackByOrder(Integer orderId, Integer changeType, String operator) {
        OrderInfo order = orderInfoService.getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        List<OrderItem> items = orderItemMapper.selectList(
                new QueryWrapper<OrderItem>().eq("order_id", orderId));
        Set<Integer> goodsIds = new HashSet<>();
        for (OrderItem item : items) {
            int qty = item.getQuantity() != null ? item.getQuantity() : 1;
            if (item.getSkuId() != null) {
                rollbackSku(item.getSkuId(), qty, changeType, order.getOrderNo(), operator, "订单取消回滚");
                goodsIds.add(item.getGoodsId());
            }
        }
        for (Integer gid : goodsIds) {
            syncGoodsStock(gid);
        }
    }

    @Override
    public IPage<InventoryLog> pageInventoryLog(int page, int limit, Integer goodsId, Integer skuId, Integer changeType) {
        QueryWrapper<InventoryLog> qw = new QueryWrapper<>();
        if (goodsId != null) qw.eq("goods_id", goodsId);
        if (skuId != null) qw.eq("sku_id", skuId);
        if (changeType != null) qw.eq("change_type", changeType);
        qw.orderByDesc("id");
        return inventoryLogMapper.selectPage(new Page<>(page, limit), qw);
    }

    @Override
    @Transactional
    public void stocktake(Integer goodsId, Integer skuId, Integer actualStock, String operator, String remark) {
        if (actualStock == null || actualStock < 0) {
            throw new RuntimeException("盘点数量不能为负数");
        }
        if (skuId != null) {
            /* SKU级盘点 */
            SkuInfo sku = skuInfoMapper.selectById(skuId);
            if (sku == null) throw new RuntimeException("SKU不存在");
            int before = sku.getStock() != null ? sku.getStock() : 0;
            int diff = actualStock - before;
            sku.setStock(actualStock);
            skuInfoMapper.updateById(sku);
            writeLog(goodsId, skuId, 6, diff, before, actualStock, null, operator,
                    (remark != null ? remark : "库存盘点") + "，差异" + (diff >= 0 ? "+" : "") + diff);
            syncGoodsStock(goodsId);
        } else {
            /* 商品级盘点：直接改商品总库存（不影响SKU） */
            GoodsInfo g = goodsService.getById(goodsId);
            if (g == null) throw new RuntimeException("商品不存在");
            int before = g.getStock() != null ? g.getStock() : 0;
            int diff = actualStock - before;
            g.setStock(actualStock);
            goodsService.updateById(g);
            writeLog(goodsId, null, 6, diff, before, actualStock, null, operator,
                    (remark != null ? remark : "库存盘点") + "，差异" + (diff >= 0 ? "+" : "") + diff);
        }
    }

    /* 记录一条库存流水 */
    private void writeLog(Integer goodsId, Integer skuId, Integer changeType,
                          Integer changeQty, Integer beforeStock, Integer afterStock,
                          String orderNo, String operator, String remark) {
        InventoryLog log = new InventoryLog();
        log.setGoodsId(goodsId);
        log.setSkuId(skuId);
        log.setChangeType(changeType);
        log.setChangeQty(changeQty);
        log.setBeforeStock(beforeStock);
        log.setAfterStock(afterStock);
        log.setOrderNo(orderNo);
        log.setOperator(operator != null ? operator : "admin");
        log.setRemark(remark);
        log.setCreateTime(LocalDateTime.now().format(LOG_FMT));
        inventoryLogMapper.insert(log);
    }

    /* 扣减SKU库存，返回变动后的库存 */
    private int deductSku(Integer skuId, int qty, Integer changeType, String orderNo, String operator, String remark) {
        SkuInfo sku = skuInfoMapper.selectById(skuId);
        if (sku == null) {
            throw new RuntimeException("SKU不存在: " + skuId);
        }
        int before = sku.getStock() != null ? sku.getStock() : 0;
        int after = before - qty;
        if (after < 0) {
            throw new RuntimeException("SKU[" + sku.getSkuName() + "]库存不足，当前" + before + "，需扣减" + qty);
        }
        sku.setStock(after);
        skuInfoMapper.updateById(sku);
        writeLog(sku.getGoodsId(), skuId, changeType, -qty, before, after, orderNo, operator, remark);
        return after;
    }

    /* 回滚SKU库存（增加） */
    private int rollbackSku(Integer skuId, int qty, Integer changeType, String orderNo, String operator, String remark) {
        SkuInfo sku = skuInfoMapper.selectById(skuId);
        if (sku == null) {
            throw new RuntimeException("SKU不存在: " + skuId);
        }
        int before = sku.getStock() != null ? sku.getStock() : 0;
        int after = before + qty;
        sku.setStock(after);
        skuInfoMapper.updateById(sku);
        writeLog(sku.getGoodsId(), skuId, changeType, qty, before, after, orderNo, operator, remark);
        return after;
    }

    /* 同步商品总库存 = 其下所有SKU库存之和 */
    private void syncGoodsStock(Integer goodsId) {
        List<SkuInfo> skus = skuInfoMapper.selectList(
                new QueryWrapper<SkuInfo>().eq("goods_id", goodsId));
        int total = 0;
        for (SkuInfo s : skus) {
            total += s.getStock() != null ? s.getStock() : 0;
        }
        GoodsInfo g = goodsService.getById(goodsId);
        if (g != null) {
            int before = g.getStock() != null ? g.getStock() : 0;
            g.setStock(total);
            goodsService.updateById(g);
            writeLog(goodsId, null, 5, total - before, before, total, null, "system", "SKU库存变动后同步商品总库存");
        }
    }
}
