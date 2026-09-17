import service from '@/network/request.js'

/* 1.分页查询会员列表 */
export function list(page, limit, params) {
    return service({
        url: `/Member/list/${page}/${limit}`,
        method: "POST",
        data: params
    });
}

/* 2.会员统计 */
export function statistics() {
    return service({
        url: "/Member/statistics",
        method: "GET"
    });
}

/* 3.会员详情 */
export function detail(id) {
    return service({
        url: `/Member/detail/${id}`,
        method: "GET"
    });
}

/* 4.更新会员 */
export function updateMember(data) {
    return service({
        url: "/Member/update",
        method: "PUT",
        data: data
    });
}

/* 5.批量禁用（设为黑名单） */
export function batchDisable(ids) {
    return service({
        url: "/Member/batchDisable",
        method: "POST",
        data: { ids: ids }
    });
}

/* 6.会员消费记录 */
export function consumeRecord(memberId) {
    return service({
        url: `/Member/consumeRecord/${memberId}`,
        method: "GET"
    });
}

/* 7.导出会员CSV */
export function exportMembers(params) {
    return service({
        url: "/Member/export",
        method: "POST",
        data: params,
        responseType: 'blob'
    });
}

/* 8.等级规则列表 */
export function levelRules() {
    return service({
        url: "/Member/levelRules",
        method: "GET"
    });
}

/* 9.保存等级规则 */
export function saveLevelRule(rule) {
    return service({
        url: "/Member/levelRule/save",
        method: "POST",
        data: rule
    });
}

/* 10.删除等级规则 */
export function deleteLevelRule(id) {
    return service({
        url: `/Member/levelRule/${id}`,
        method: "DELETE"
    });
}
