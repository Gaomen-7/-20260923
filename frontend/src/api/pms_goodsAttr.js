import service from '@/network/request.js'

/* 1.分页查询属性列表 */
export function list(page, limit, params) {
    return service({
        url: `/GoodsAttr/list/${page}/${limit}`,
        method: "POST",
        data: params
    });
}

/* 2.新增属性 */
export function addAttr(data) {
    return service({
        url: "/GoodsAttr/add",
        method: "POST",
        data: data
    });
}

/* 3.更新属性 */
export function updateAttr(data) {
    return service({
        url: "/GoodsAttr/update",
        method: "PUT",
        data: data
    });
}

/* 4.删除属性 */
export function deleteAttr(id) {
    return service({
        url: `/GoodsAttr/delete/${id}`,
        method: "DELETE"
    });
}

/* 5.获取规格参数/销售属性（根据类别ID，发布商品用） */
export function listByCategory(categoryId, attrType) {
    return service({
        url: `/GoodsAttr/listByCategory/${categoryId}/${attrType}`,
        method: "GET"
    });
}

/* 6.查询某分类下的属性分组列表（下拉用） */
export function groupOptions(categoryId) {
    return service({
        url: `/GoodsAttr/groupOptions/${categoryId}`,
        method: "GET"
    });
}
