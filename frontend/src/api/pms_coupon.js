import service from '@/network/request.js'

/* 1.分页查询优惠券列表 */
export function list(page, limit, params) {
    return service({
        url: `/Coupon/list/${page}/${limit}`,
        method: "POST",
        data: params
    });
}

/* 2.新增优惠券 */
export function addCoupon(data) {
    return service({
        url: "/Coupon/add",
        method: "POST",
        data: data
    });
}

/* 3.更新优惠券 */
export function updateCoupon(data) {
    return service({
        url: "/Coupon/update",
        method: "PUT",
        data: data
    });
}

/* 4.删除单个优惠券 */
export function deleteCoupon(id) {
    return service({
        url: `/Coupon/delete/${id}`,
        method: "DELETE"
    });
}

/* 5.批量删除 */
export function batchDelete(ids) {
    return service({
        url: "/Coupon/batchDelete",
        method: "POST",
        data: { ids: ids }
    });
}

/* 6.批量绑定商品 */
export function bindGoods(couponId, goodsIds, skuIds) {
    return service({
        url: "/Coupon/bindGoods",
        method: "POST",
        data: { couponId: couponId, goodsIds: goodsIds, skuIds: skuIds }
    });
}

/* 7.解绑商品 */
export function unbindGoods(couponId, goodsId, skuId) {
    return service({
        url: "/Coupon/unbindGoods",
        method: "POST",
        data: { couponId: couponId, goodsId: goodsId, skuId: skuId }
    });
}

/* 8.查询已绑定商品 */
export function boundGoods(couponId) {
    return service({
        url: `/Coupon/boundGoods/${couponId}`,
        method: "GET"
    });
}

/* 9.计算优惠 */
export function calculate(couponId, goodsList) {
    return service({
        url: "/Coupon/calculate",
        method: "POST",
        data: { couponId: couponId, goodsList: goodsList }
    });
}

/* 10.应用优惠券到订单 */
export function applyToOrder(orderId, couponId) {
    return service({
        url: "/Coupon/applyToOrder",
        method: "POST",
        data: { orderId: orderId, couponId: couponId }
    });
}
