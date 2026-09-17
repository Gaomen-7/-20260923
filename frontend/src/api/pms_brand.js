import service from '@/network/request.js'

/* 1.显示品牌列表.【带分页+搜索】 */
export function list(page, limit, param){
    return service({
        url:`/Brand/list/${page}/${limit}`,
        method:"POST",
        data:param
    });
}

/* 2.添加品牌.【写入数据到后台】 */
export function addBrand( brand ){
    return service({
        url:`/Brand/addBrand`,
        method:"POST",
        data:brand
    });
}

/* 3.更新品牌.【写入数据到后台】 */
export function updateBrand( brand ){
    return service({
        url:`/Brand/updateBrand`,
        method:"PUT",
        data:brand
    });
}

/* 4.删除品牌.【写入数据到后台】 */
export function deleteBrand( id ){
    return service({
        url:`/Brand/deleteBrand/${id}`,
        method:"DELETE"
    });
}

/* 5.品牌图片上传.【写入数据到后台】 */
export function uploadFile( FD ){
    return service({
        url:`/Brand/upload`,
        method:"POST",
        data:FD
    });
}

/* 6.获取品牌列表.【带类别关联标记】 */
export function listByCategory(page, limit, param){
    return service({
        url:`/Brand/listByCategory/${page}/${limit}`,
        method:"POST",
        data:param
    });
}

/* 7.根据类别ID查询关联品牌选项。 */
export function getBrandOptions( categoryId ){
    return service({
        url:`/Brand/brandOptions/${categoryId}`,
        method:"GET"
    });
}
