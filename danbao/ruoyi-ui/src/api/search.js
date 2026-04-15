import request from '@/utils/request'

// 查询专利搜索列表（基础检索）
export function listSearch(query) {
  return request({
    url: '/api/search/list',
    method: 'get',
    params: query
  })
}

// 高级检索：多维度组合查询
export function advancedSearch(query) {
  return request({
    url: '/api/search/advanced',
    method: 'get',
    params: query
  })
}

// 获取专利分类列表
export function listClassifications() {
  return request({
    url: '/api/search/classifications',
    method: 'get'
  })
}

// ==================== Facet 聚合统计接口 ====================

// 按省份统计专利数量（用于柱状图）
export function statByProvince(query) {
  return request({
    url: '/api/search/stat/province',
    method: 'get',
    params: query
  })
}

// 按类型统计专利数量（用于饼图）
export function statByType(query) {
  return request({
    url: '/api/search/stat/type',
    method: 'get',
    params: query
  })
}

// 按申请年份统计专利数量（用于时间趋势图）
export function statByYear(query) {
  return request({
    url: '/api/search/stat/year',
    method: 'get',
    params: query
  })
}
