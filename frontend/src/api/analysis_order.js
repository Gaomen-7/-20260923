import service from '@/network/request.js'

export function getOverview(params) {
    return service({ url: "/analysis/order/overview", method: "GET", params })
}
export function getTrend(params) {
    return service({ url: "/analysis/order/trend", method: "GET", params })
}
export function getCategory(params) {
    return service({ url: "/analysis/order/category", method: "GET", params })
}
export function getReturnBoard(params) {
    return service({ url: "/analysis/order/return", method: "GET", params })
}
export function getPriceRange(params) {
    return service({ url: "/analysis/order/priceRange", method: "GET", params })
}
export function getHourly(params) {
    return service({ url: "/analysis/order/hourly", method: "GET", params })
}
