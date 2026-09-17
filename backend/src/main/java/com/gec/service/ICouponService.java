package com.gec.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gec.domain.entity.Coupon;
import com.gec.domain.search.CouponSearch;

import java.util.List;
import java.util.Map;

public interface ICouponService extends IService<Coupon> {

    /* 分页查询优惠券列表 */
    IPage<Coupon> pageCoupon(Page page, CouponSearch search);

    /* 新增优惠券 */
    void addCoupon(Coupon coupon);

    /* 更新优惠券 */
    void updateCoupon(Coupon coupon);

    /* 批量删除 */
    void batchDelete(List<Integer> ids);

    /* 批量绑定商品（SPU或SKU） */
    void bindGoods(Integer couponId, List<Integer> goodsIds, List<Integer> skuIds);

    /* 解绑商品 */
    void unbindGoods(Integer couponId, Integer goodsId, Integer skuId);

    /* 查询优惠券已绑定的商品列表 */
    List<Map<String, Object>> getBoundGoods(Integer couponId);

    /* 计算优惠：传入商品列表和优惠券ID，返回优惠金额 */
    Map<String, Object> calculateDiscount(Integer couponId, List<Map<String, Object>> goodsList);

    /* 应用优惠券到订单 */
    void applyToOrder(Integer orderId, Integer couponId);
}
