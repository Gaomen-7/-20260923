import service from '@/network/request.js'

export function getOverview(params) {
    return service({ url: "/analysis/chat/overview", method: "GET", params })
}
export function getTypeDist(params) {
    return service({ url: "/analysis/chat/typeDist", method: "GET", params })
}
export function getHeatmap(params) {
    return service({ url: "/analysis/chat/heatmap", method: "GET", params })
}
export function getConversion(params) {
    return service({ url: "/analysis/chat/conversion", method: "GET", params })
}
export function getTopProducts(params) {
    return service({ url: "/analysis/chat/topProducts", method: "GET", params })
}
