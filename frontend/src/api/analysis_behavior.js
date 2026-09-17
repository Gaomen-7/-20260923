import service from '@/network/request.js'

export function getSource(params) {
    return service({ url: "/analysis/behavior/source", method: "GET", params })
}
export function getTrend(params) {
    return service({ url: "/analysis/behavior/trend", method: "GET", params })
}
export function getKeywords(params) {
    return service({ url: "/analysis/behavior/keywords", method: "GET", params })
}
export function getFunnel(params) {
    return service({ url: "/analysis/behavior/funnel", method: "GET", params })
}
export function getHeatmap(params) {
    return service({ url: "/analysis/behavior/heatmap", method: "GET", params })
}
export function getTopProducts(params) {
    return service({ url: "/analysis/behavior/topProducts", method: "GET", params })
}
