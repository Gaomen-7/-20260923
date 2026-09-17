import service from '@/network/request.js'

export function getOverview(params) {
    return service({ url: "/analysis/review/overview", method: "GET", params })
}
export function getLevelDist(params) {
    return service({ url: "/analysis/review/levelDist", method: "GET", params })
}
export function getKeywords(params) {
    return service({ url: "/analysis/review/keywords", method: "GET", params })
}
export function getCategoryCompare(params) {
    return service({ url: "/analysis/review/categoryCompare", method: "GET", params })
}
export function getTrend(params) {
    return service({ url: "/analysis/review/trend", method: "GET", params })
}
export function getTopProducts(params) {
    return service({ url: "/analysis/review/topProducts", method: "GET", params })
}
