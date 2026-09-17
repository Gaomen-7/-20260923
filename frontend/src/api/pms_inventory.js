import service from '@/network/request.js'

/* 1.库存统计 */
export function statistics() {
    return service({
        url: "/Inventory/statistics",
        method: "GET"
    });
}

/* 2.库存列表（分页） */
export function list(page, limit, params) {
    return service({
        url: `/Inventory/list/${page}/${limit}`,
        method: "POST",
        data: params
    });
}

/* 3.某商品的SKU库存明细 */
export function skuDetail(goodsId) {
    return service({
        url: `/Inventory/skuDetail/${goodsId}`,
        method: "GET"
    });
}

/* 4.调整商品库存 */
export function adjustStock(goodsId, stock) {
    return service({
        url: "/Inventory/adjust",
        method: "POST",
        data: { goodsId: goodsId, stock: stock }
    });
}

/* 5.调整SKU库存 */
export function adjustSkuStock(skuId, stock) {
    return service({
        url: "/Inventory/adjustSku",
        method: "POST",
        data: { skuId: skuId, stock: stock }
    });
}

/* 6.库存流水列表 */
export function inventoryLog(page, limit, params) {
    var qs = []
    if (params.goodsId) qs.push('goodsId=' + params.goodsId)
    if (params.skuId) qs.push('skuId=' + params.skuId)
    if (params.changeType) qs.push('changeType=' + params.changeType)
    var query = qs.length > 0 ? '?' + qs.join('&') : ''
    return service({
        url: `/Inventory/log/${page}/${limit}${query}`,
        method: "GET"
    })
}

/* 7.库存盘点 */
export function stocktake(goodsId, skuId, actualStock, remark) {
    return service({
        url: "/Inventory/stocktake",
        method: "POST",
        data: { goodsId: goodsId, skuId: skuId, actualStock: actualStock, remark: remark }
    })
}
