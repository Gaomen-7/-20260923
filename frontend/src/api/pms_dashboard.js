import service from '@/network/request.js'

/* 1.顶部统计概览 */
export function statistics() {
    return service({
        url: "/Dashboard/statistics",
        method: "GET"
    });
}

/* 2.订单状态分布 */
export function orderStatus() {
    return service({
        url: "/Dashboard/orderStatus",
        method: "GET"
    });
}

/* 3.最近订单 */
export function recentOrders() {
    return service({
        url: "/Dashboard/recentOrders",
        method: "GET"
    });
}

/* 4.商品上下架分布 */
export function goodsStatus() {
    return service({
        url: "/Dashboard/goodsStatus",
        method: "GET"
    });
}

/* 5.会员类型分布 */
export function memberType() {
    return service({
        url: "/Dashboard/memberType",
        method: "GET"
    });
}
