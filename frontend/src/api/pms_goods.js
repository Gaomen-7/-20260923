import service from '@/network/request.js'

/* 1.获取商品列表【带分页+筛选】 */
export function getGoodsList(params) {
    return service({
        url: `/Goods/list`,
        method: "GET",
        params: params
    });
}

/* 2.上架/下架切换【写入DB】 */
export function updateGoodsStatus(id, publishStatus) {
    return service({
        url: `/Goods/updateStatus?id=${id}&publishStatus=${publishStatus}`,
        method: "POST"
    });
}

/* 3.获取SKU列表【带分页+筛选+价格区间】 */
export function getSkuList(params) {
    return service({
        url: `/Goods/skuList`,
        method: "GET",
        params: params
    });
}

/* 4.批量上架/下架 */
export function batchUpdateStatus(ids, publishStatus) {
    return service({
        url: "/Goods/batchUpdateStatus",
        method: "POST",
        data: { ids: ids, publishStatus: publishStatus }
    });
}

/* 5.批量删除 */
export function batchDelete(ids) {
    return service({
        url: "/Goods/batchDelete",
        method: "POST",
        data: { ids: ids }
    });
}

/* 6.切换推荐状态 */
export function toggleRecommend(id, isRecommend) {
    return service({
        url: `/Goods/toggleRecommend?id=${id}&isRecommend=${isRecommend}`,
        method: "POST"
    });
}

/* 7.批量推荐/取消推荐 */
export function batchRecommend(ids, isRecommend) {
    return service({
        url: "/Goods/batchRecommend",
        method: "POST",
        data: { ids: ids, isRecommend: isRecommend }
    });
}
