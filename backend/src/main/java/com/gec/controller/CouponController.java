package com.gec.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.components.FileTemplate;
import com.gec.domain.entity.Coupon;
import com.gec.domain.search.CouponSearch;
import com.gec.service.ICouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Coupon")
public class CouponController extends BaseController {

    @Autowired
    private ICouponService couponService;

    /* 1. 分页查询优惠券列表 */
    @PostMapping("/list/{page}/{limit}")
    public R list(
            @PathVariable("page") Integer page,
            @PathVariable("limit") Integer limit,
            @RequestBody CouponSearch param) {
        Page frmPage = newPage(page, limit);
        IPage<Coupon> retPage = couponService.pageCoupon(frmPage, param);
        return R.convertPage(retPage);
    }

    /* 2. 新增优惠券 */
    @PostMapping("/add")
    public R add(@RequestBody Coupon coupon) {
        couponService.addCoupon(coupon);
        return R.ok();
    }

    /* 3. 更新优惠券 */
    @PutMapping("/update")
    public R update(@RequestBody Coupon coupon) {
        couponService.updateCoupon(coupon);
        return R.ok();
    }

    /* 4. 删除单个优惠券 */
    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable("id") Integer id) {
        boolean ret = couponService.removeById(id);
        if (!ret) {
            throw new RuntimeException("删除失败");
        }
        return R.ok();
    }

    /* 5. 批量删除 */
    @PostMapping("/batchDelete")
    public R batchDelete(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) body.get("ids");
        couponService.batchDelete(ids);
        return R.ok();
    }

    /* 6. 批量绑定商品 */
    @PostMapping("/bindGoods")
    public R bindGoods(@RequestBody Map<String, Object> body) {
        Integer couponId = Integer.valueOf(body.get("couponId").toString());
        @SuppressWarnings("unchecked")
        List<Integer> goodsIds = (List<Integer>) body.get("goodsIds");
        @SuppressWarnings("unchecked")
        List<Integer> skuIds = (List<Integer>) body.get("skuIds");
        couponService.bindGoods(couponId, goodsIds, skuIds);
        return R.ok();
    }

    /* 7. 解绑商品 */
    @PostMapping("/unbindGoods")
    public R unbindGoods(@RequestBody Map<String, Object> body) {
        Integer couponId = Integer.valueOf(body.get("couponId").toString());
        Integer goodsId = Integer.valueOf(body.get("goodsId").toString());
        Integer skuId = body.get("skuId") != null ? Integer.valueOf(body.get("skuId").toString()) : null;
        couponService.unbindGoods(couponId, goodsId, skuId);
        return R.ok();
    }

    /* 8. 查询已绑定商品列表 */
    @GetMapping("/boundGoods/{couponId}")
    public R boundGoods(@PathVariable("couponId") Integer couponId) {
        return R.ok().put("data", couponService.getBoundGoods(couponId));
    }

    /* 9. 计算优惠 */
    @PostMapping("/calculate")
    public R calculate(@RequestBody Map<String, Object> body) {
        Integer couponId = Integer.valueOf(body.get("couponId").toString());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> goodsList = (List<Map<String, Object>>) body.get("goodsList");
        return R.ok().put("data", couponService.calculateDiscount(couponId, goodsList));
    }

    /* 10. 应用优惠券到订单 */
    @PostMapping("/applyToOrder")
    public R applyToOrder(@RequestBody Map<String, Object> body) {
        Integer orderId = Integer.valueOf(body.get("orderId").toString());
        Integer couponId = Integer.valueOf(body.get("couponId").toString());
        couponService.applyToOrder(orderId, couponId);
        return R.ok();
    }

    @ExceptionHandler(Exception.class)
    public void exceptionFallback(Exception E, HttpServletResponse resp) {
        E.printStackTrace();
        R.err(E).write(resp);
    }

    @Override
    protected FileTemplate getFileTemplate() {
        return null;
    }
}
