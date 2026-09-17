import service from '@/network/request.js'

/* 1.分页查询属性分组列表 */
export function list(page, limit, categoryId) {
    return service({
        url: `/AttrGroup/list/${page}/${limit}/${categoryId}`,
        method: "POST"
    });
}

/* 2.新增属性分组 */
export function addAttrGroup(data) {
    return service({
        url: "/AttrGroup/add",
        method: "POST",
        data: data
    });
}

/* 3.修改属性分组 */
export function updateAttrGroup(data) {
    return service({
        url: "/AttrGroup/update",
        method: "PUT",
        data: data
    });
}

/* 4.删除属性分组 */
export function deleteAttrGroup(id) {
    return service({
        url: `/AttrGroup/delete/${id}`,
        method: "DELETE"
    });
}
