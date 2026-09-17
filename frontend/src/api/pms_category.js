import service from '@/network/request.js'

/* 1.获取类别的树形菜单。 */
export function getCategoryList(){
    return service({
        url:`/Category/list`,
        method:"GET"
    });
}

/* 2.添加类别【写入DB】。 */
export function addCategory(category){
    return service({
        url:`/Category/addCategory`,
        method:"POST",
        data:category
    });
}

/* 3.更新类别【写入DB】。 */
export function updateCategory(category){
    return service({
        url:`/Category/updateCategory`,
        method:"PUT",
        data:category
    });
}

/* 4.删除类别【删除DB数据】。 */
export function deleteCategory(id){
    return service({
        url:`/Category/deleteCategory/${id}`,
        method:"DELETE"
    });
}

/* 5.关联品牌【写入DB数据】。 */
export function associateBrand( cbVO ){
    return service({
        url:`/Category/associateBrand`,
        method:"POST",
        data:cbVO
    });
}

/* 6.获取类别的父ID序列【读取DB数据】。 */
export function getPids( id ){
    return service({
        url:`/Category/getPids/${id}`,
        method:"GET"
    });
}
