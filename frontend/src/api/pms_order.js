import service from '@/network/request.js'

/* 1.分页查询订单列表 */
export function list(page, limit, params) {
    return service({
        url: `/Order/list/${page}/${limit}`,
        method: "POST",
        data: params
    });
}

/* 2.订单详情 */
export function detail(id) {
    return service({
        url: `/Order/detail/${id}`,
        method: "GET"
    });
}

/* 3.发货 */
export function ship(id) {
    return service({
        url: `/Order/ship/${id}`,
        method: "PUT"
    });
}

/* 4.修改价格 */
export function updatePrice(id, actualAmount) {
    return service({
        url: "/Order/updatePrice",
        method: "PUT",
        data: { id: id, actualAmount: actualAmount }
    });
}

/* 5.取消订单 */
export function cancel(id) {
    return service({
        url: `/Order/cancel/${id}`,
        method: "PUT"
    });
}

/* 6.删除订单 */
export function deleteOrder(id) {
    return service({
        url: `/Order/delete/${id}`,
        method: "DELETE"
    });
}

/* 7.修改备注 */
export function updateRemark(id, remark) {
    return service({
        url: "/Order/updateRemark",
        method: "PUT",
        data: { id: id, remark: remark }
    });
}

/* 8.批量删除 */
export function batchDelete(ids) {
    return service({
        url: "/Order/batchDelete",
        method: "DELETE",
        data: { ids: ids }
    });
}

/* 9.导出订单CSV */
export function exportOrders(params) {
    return service({
        url: "/Order/export",
        method: "POST",
        data: params,
        responseType: 'blob'
    });
}

/* 7.查询可用优惠券列表（订单详情用） */
export function availableCoupons() {
    return service({
        url: `/Coupon/list/1/100`,
        method: "POST",
        data: { status: 1 }
    });
}
