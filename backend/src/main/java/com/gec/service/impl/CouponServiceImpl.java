package com.gec.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gec.dao.CouponGoodsRelationMapper;
import com.gec.dao.CouponMapper;
import com.gec.dao.OrderItemMapper;
import com.gec.dao.SkuInfoMapper;
import com.gec.domain.entity.Coupon;
import com.gec.domain.entity.CouponGoodsRelation;
import com.gec.domain.entity.GoodsInfo;
import com.gec.domain.entity.OrderInfo;
import com.gec.domain.entity.OrderItem;
import com.gec.domain.entity.SkuInfo;
import com.gec.domain.search.CouponSearch;
import com.gec.service.IGoodsService;
import com.gec.service.ICouponService;
import com.gec.service.IOrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

@Service
public class CouponServiceImpl
    extends ServiceImpl<CouponMapper, Coupon>
    implements ICouponService {

    @Autowired
    private CouponGoodsRelationMapper relationMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    @Lazy
    private IOrderInfoService orderInfoService;

    private static final DateTimeFormatter FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public IPage<Coupon> pageCoupon(Page page, CouponSearch search) {
        QueryWrapper<Coupon> qw = new QueryWrapper<>();
        if (search.getCouponName() != null && !search.getCouponName().trim().isEmpty()) {
            qw.like("coupon_name", search.getCouponName().trim());
        }
        if (search.getStatus() != null) {
            qw.eq("status", search.getStatus());
        }
        if (search.getStartTime() != null && !search.getStartTime().isEmpty()) {
            qw.ge("create_time", search.getStartTime());
        }
        if (search.getEndTime() != null && !search.getEndTime().isEmpty()) {
            qw.le("create_time", search.getEndTime() + " 23:59:59");
        }
        qw.orderByDesc("id");
        return baseMapper.selectPage(page, qw);
    }

    @Override
    @Transactional
    public void addCoupon(Coupon coupon) {
        if (coupon.getCouponName() == null || coupon.getCouponName().trim().isEmpty()) {
            throw new RuntimeException("优惠券名称不能为空");
        }
        if (coupon.getTotalCount() == null) {
            coupon.setTotalCount(0);
        }
        if (coupon.getReceivedCount() == null) {
            coupon.setReceivedCount(0);
        }
        if (coupon.getStatus() == null) {
            coupon.setStatus(1);
        }
        coupon.setCreateTime(now());
        boolean ret = this.save(coupon);
        if (!ret) {
            throw new RuntimeException("添加优惠券失败");
        }
    }

    @Override
    @Transactional
    public void updateCoupon(Coupon coupon) {
        if (coupon.getId() == null) {
            throw new RuntimeException("优惠券ID不能为空");
        }
        Coupon exist = this.getById(coupon.getId());
        if (exist == null) {
            throw new RuntimeException("优惠券不存在");
        }
        coupon.setUpdateTime(now());
        boolean ret = this.updateById(coupon);
        if (!ret) {
            throw new RuntimeException("更新优惠券失败");
        }
    }

    @Override
    @Transactional
    public void batchDelete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("请选择要删除的优惠券");
        }
        boolean ret = this.removeByIds(ids);
        if (!ret) {
            throw new RuntimeException("批量删除失败");
        }
    }

    @Override
    @Transactional
    public void bindGoods(Integer couponId, List<Integer> goodsIds, List<Integer> skuIds) {
        Coupon coupon = this.getById(couponId);
        if (coupon == null) throw new RuntimeException("优惠券不存在");
        String now = now();
        /* 绑定SPU */
        if (goodsIds != null) {
            for (Integer gid : goodsIds) {
                CouponGoodsRelation r = new CouponGoodsRelation();
                r.setCouponId(couponId);
                r.setGoodsId(gid);
                r.setSkuId(null);
                r.setCreateTime(now);
                try { relationMapper.insert(r); } catch (Exception e) { /* 唯一键冲突跳过 */ }
            }
        }
        /* 绑定SKU */
        if (skuIds != null) {
            for (Integer sid : skuIds) {
                SkuInfo sku = skuInfoMapper.selectById(sid);
                if (sku == null) continue;
                CouponGoodsRelation r = new CouponGoodsRelation();
                r.setCouponId(couponId);
                r.setGoodsId(sku.getGoodsId());
                r.setSkuId(sid);
                r.setCreateTime(now);
                try { relationMapper.insert(r); } catch (Exception e) { /* 唯一键冲突跳过 */ }
            }
        }
    }

    @Override
    @Transactional
    public void unbindGoods(Integer couponId, Integer goodsId, Integer skuId) {
        QueryWrapper<CouponGoodsRelation> qw = new QueryWrapper<>();
        qw.eq("coupon_id", couponId);
        qw.eq("goods_id", goodsId);
        if (skuId != null) {
            qw.eq("sku_id", skuId);
        } else {
            qw.isNull("sku_id");
        }
        relationMapper.delete(qw);
    }

    @Override
    public List<Map<String, Object>> getBoundGoods(Integer couponId) {
        QueryWrapper<CouponGoodsRelation> qw = new QueryWrapper<>();
        qw.eq("coupon_id", couponId);
        List<CouponGoodsRelation> relations = relationMapper.selectList(qw);
        List<Map<String, Object>> result = new ArrayList<>();
        for (CouponGoodsRelation r : relations) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", r.getId());
            item.put("goodsId", r.getGoodsId());
            item.put("skuId", r.getSkuId());
            GoodsInfo g = goodsService.getById(r.getGoodsId());
            item.put("goodsName", g != null ? g.getGoodsName() : "未知商品");
            if (r.getSkuId() != null) {
                SkuInfo sku = skuInfoMapper.selectById(r.getSkuId());
                item.put("skuName", sku != null ? sku.getSkuName() : "未知SKU");
            } else {
                item.put("skuName", "全部SKU");
            }
            result.add(item);
        }
        return result;
    }

    @Override
    public Map<String, Object> calculateDiscount(Integer couponId, List<Map<String, Object>> goodsList) {
        Coupon coupon = this.getById(couponId);
        if (coupon == null) throw new RuntimeException("优惠券不存在");
        if (coupon.getStatus() == null || coupon.getStatus() != 1) {
            throw new RuntimeException("优惠券已失效");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("couponId", couponId);
        result.put("couponName", coupon.getCouponName());
        result.put("couponType", coupon.getCouponType());

        /* 查询优惠券绑定的商品范围（无记录=全场通用） */
        QueryWrapper<CouponGoodsRelation> qw = new QueryWrapper<>();
        qw.eq("coupon_id", couponId);
        List<CouponGoodsRelation> relations = relationMapper.selectList(qw);
        Set<Integer> boundGoodsIds = new HashSet<>();
        Set<Integer> boundSkuIds = new HashSet<>();
        boolean hasRelation = !relations.isEmpty();
        for (CouponGoodsRelation r : relations) {
            boundGoodsIds.add(r.getGoodsId());
            if (r.getSkuId() != null) boundSkuIds.add(r.getSkuId());
        }

        /* 筛选符合条件的商品，计算总金额 */
        BigDecimal eligibleAmount = BigDecimal.ZERO;
        List<Map<String, Object>> eligibleItems = new ArrayList<>();
        for (Map<String, Object> item : goodsList) {
            Integer gid = item.get("goodsId") != null ? Integer.valueOf(item.get("goodsId").toString()) : null;
            Integer sid = item.get("skuId") != null ? Integer.valueOf(item.get("skuId").toString()) : null;
            BigDecimal price = item.get("price") != null ? new BigDecimal(item.get("price").toString()) : BigDecimal.ZERO;
            Integer qty = item.get("quantity") != null ? Integer.valueOf(item.get("quantity").toString()) : 1;
            BigDecimal subtotal = price.multiply(new BigDecimal(qty));

            boolean eligible = true;
            if (hasRelation) {
                if (sid != null && boundSkuIds.contains(sid)) {
                    eligible = true;  // SKU精确匹配
                } else if (gid != null && boundGoodsIds.contains(gid) && !boundSkuIds.contains(sid)) {
                    eligible = true;  // SPU级绑定，且该SKU未被单独排除
                } else {
                    eligible = false;
                }
            }
            if (eligible) {
                eligibleAmount = eligibleAmount.add(subtotal);
                eligibleItems.add(item);
            }
        }

        result.put("eligibleAmount", eligibleAmount);
        result.put("eligibleCount", eligibleItems.size());

        /* 检查使用门槛 */
        BigDecimal minAmount = coupon.getMinAmount() != null ? coupon.getMinAmount() : BigDecimal.ZERO;
        if (eligibleAmount.compareTo(minAmount) < 0) {
            result.put("canUse", false);
            result.put("reason", "未达到使用门槛（需满" + minAmount + "元）");
            result.put("discountAmount", BigDecimal.ZERO);
            result.put("finalAmount", eligibleAmount);
            return result;
        }

        /* 计算优惠金额 */
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (coupon.getCouponType() == 1) {
            /* 满减 */
            discountAmount = coupon.getDiscountAmount() != null ? coupon.getDiscountAmount() : BigDecimal.ZERO;
            if (discountAmount.compareTo(eligibleAmount) > 0) {
                discountAmount = eligibleAmount;  // 优惠不超过商品金额
            }
        } else if (coupon.getCouponType() == 2) {
            /* 折扣 */
            BigDecimal rate = coupon.getDiscountRate() != null ? coupon.getDiscountRate() : BigDecimal.ONE;
            discountAmount = eligibleAmount.multiply(BigDecimal.ONE.subtract(rate))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        result.put("canUse", true);
        result.put("discountAmount", discountAmount);
        result.put("finalAmount", eligibleAmount.subtract(discountAmount));
        return result;
    }

    @Override
    @Transactional
    public void applyToOrder(Integer orderId, Integer couponId) {
        OrderInfo order = orderInfoService.getById(orderId);
        if (order == null) throw new RuntimeException("订单不存在");
        if (order.getOrderStatus() != 0) {
            throw new RuntimeException("仅待付款订单可使用优惠券");
        }

        /* 组装订单商品列表 */
        List<OrderItem> items = orderItemMapper.selectList(
                new QueryWrapper<OrderItem>().eq("order_id", orderId));
        List<Map<String, Object>> goodsList = new ArrayList<>();
        for (OrderItem item : items) {
            Map<String, Object> g = new HashMap<>();
            g.put("goodsId", item.getGoodsId());
            g.put("skuId", item.getSkuId());
            g.put("price", item.getPrice());
            g.put("quantity", item.getQuantity());
            goodsList.add(g);
        }

        /* 计算优惠 */
        Map<String, Object> calc = calculateDiscount(couponId, goodsList);
        if (!(Boolean) calc.get("canUse")) {
            throw new RuntimeException(calc.get("reason").toString());
        }
        BigDecimal discountAmount = (BigDecimal) calc.get("discountAmount");

        /* 更新订单 */
        OrderInfo update = new OrderInfo();
        update.setId(orderId);
        update.setDiscountAmount(discountAmount);
        BigDecimal actual = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
        actual = actual.subtract(discountAmount);
        if (actual.compareTo(BigDecimal.ZERO) < 0) actual = BigDecimal.ZERO;
        update.setActualAmount(actual);
        update.setCouponId(couponId);
        update.setUpdateTime(now());
        boolean ret = orderInfoService.updateById(update);
        if (!ret) throw new RuntimeException("应用优惠券失败");
    }

    private String now() {
        return LocalDateTime.now().format(FMT);
    }
}
