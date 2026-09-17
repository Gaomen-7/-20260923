import service from '@/network/request.js'

/* 1.品牌图片上传.【写入数据到后台】 */
export function uploadImage( FD ){
    return service({
        url:`/PublishGoods/upload`,
        method:"POST",
        data:FD
    });
}

/* 2.保存基本信息.【写入数据到后台】 */
export function savePublishBase( baseInfo ){
    return service({
        url:`/PublishGoods/saveGoodsBaseInfo`,
        method:"POST",
        data:baseInfo
    });
}

/* 3.保存规格参数.【写入数据到后台】 */
export function saveGoodsAttrValues( form ){
    return service({
        url:`/PublishGoods/saveGoodsAttrValues`,
        method:"POST",
        data:form
    });
}

/* 4.保存销售属性.【写入数据到后台】 */
export function saveSaleAttrValues( form ){
    return service({
        url:`/PublishGoods/saveSaleAttrValues`,
        method:"POST",
        data:form
    });
}

/* 5.读取销售属性选中值.【工单03】 */
export function getSaleAttr( pubKey ){
    return service({
        url:`/PublishGoods/getSaleAttr?pubKey=${pubKey}`,
        method:"GET",
    });
}

/* 6.读取图集列表.【工单03】 */
export function getAlbumList( pubKey ){
    return service({
        url:`/PublishGoods/getAlbumList?pubKey=${pubKey}`,
        method:"GET",
    });
}

/* 7.生成SKU组合(笛卡尔积).【工单03】 */
export function generateSku( data ){
    return service({
        url:`/PublishGoods/generateSku`,
        method:"POST",
        data:data
    });
}

/* 8.保存SKU信息到缓存.【工单03】 */
export function saveSkuCache( data ){
    return service({
        url:`/PublishGoods/saveSkuCache`,
        method:"POST",
        data:data
    });
}

/* 9.保存完成：Redis数据持久化到数据库.【工单04】 */
export function saveComplete( pubKey ){
    return service({
        url:`/PublishGoods/saveComplete?pubKey=${pubKey}`,
        method:"POST"
    });
}