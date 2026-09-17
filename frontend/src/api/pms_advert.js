import service from '@/network/request.js'

/* 1.分页查询广告列表 */
export function list(page, limit, params) {
    return service({
        url: `/Advert/list/${page}/${limit}`,
        method: "POST",
        data: params
    });
}

/* 2.新增广告 */
export function addAdvert(data) {
    return service({
        url: "/Advert/add",
        method: "POST",
        data: data
    });
}

/* 3.更新广告 */
export function updateAdvert(data) {
    return service({
        url: "/Advert/update",
        method: "PUT",
        data: data
    });
}

/* 4.删除单个广告 */
export function deleteAdvert(id) {
    return service({
        url: `/Advert/delete/${id}`,
        method: "DELETE"
    });
}

/* 5.批量删除 */
export function batchDelete(ids) {
    return service({
        url: "/Advert/batchDelete",
        method: "POST",
        data: { ids: ids }
    });
}

/* 6.上下线切换 */
export function toggleStatus(id, status) {
    return service({
        url: "/Advert/toggleStatus",
        method: "POST",
        data: { id: id, status: status }
    });
}

/* 7.刷新过期广告状态 */
export function refreshExpired() {
    return service({
        url: "/Advert/refreshExpired",
        method: "POST"
    });
}

/* 8.曝光量+1 */
export function incrementView(id) {
    return service({
        url: `/Advert/incrementView/${id}`,
        method: "POST"
    });
}

/* 9.点击量+1 */
export function incrementClick(id) {
    return service({
        url: `/Advert/incrementClick/${id}`,
        method: "POST"
    });
}
