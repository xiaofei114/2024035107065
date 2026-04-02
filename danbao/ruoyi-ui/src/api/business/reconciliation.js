import request from '@/utils/request'

/**
 * 对账API
 */

/**
 * 查询对账列表
 * @param {Object} query - 查询参数
 * @returns {Promise}
 */
export function listReconciliation(query) {
  return request({
    url: '/business/reconciliation/list',
    method: 'get',
    params: query
  })
}

/**
 * 查询对账详情
 * @param {Number} id - 对账ID
 * @returns {Promise}
 */
export function getReconciliation(id) {
  return request({
    url: `/business/reconciliation/${id}`,
    method: 'get'
  })
}

/**
 * 新增对账
 * @param {Object} data - 对账数据
 * @returns {Promise}
 */
export function addReconciliation(data) {
  return request({
    url: '/business/reconciliation',
    method: 'post',
    data: data
  })
}

/**
 * 修改对账
 * @param {Object} data - 对账数据
 * @returns {Promise}
 */
export function updateReconciliation(data) {
  return request({
    url: '/business/reconciliation',
    method: 'put',
    data: data
  })
}

/**
 * 删除对账
 * @param {Number} id - 对账ID
 * @returns {Promise}
 */
export function delReconciliation(id) {
  return request({
    url: `/business/reconciliation/${id}`,
    method: 'delete'
  })
}

/**
 * 批量自动对账
 * @returns {Promise}
 */
export function batchAutoReconciliation() {
  return request({
    url: '/business/reconciliation/auto-reconciliation',
    method: 'post'
  })
}

/**
 * 手动对账
 * @param {Object} data - 对账数据
 * @returns {Promise}
 */
export function manualReconciliation(data) {
  return request({
    url: '/business/reconciliation/manual-reconciliation',
    method: 'post',
    data: data
  })
}

/**
 * 解绑对账
 * @param {Number} id - 对账ID
 * @param {Object} data - 操作人信息
 * @returns {Promise}
 */
export function unbindReconciliation(id, data) {
  return request({
    url: `/business/reconciliation/unbind/${id}`,
    method: 'post',
    data: data
  })
}

/**
 * 统计对账状态
 * @returns {Promise}
 */
export function countReconciliationStatus() {
  return request({
    url: '/business/reconciliation/status-count',
    method: 'get'
  })
}

/**
 * 导出对账列表
 * @param {Object} query - 查询参数
 * @returns {Promise}
 */
export function exportReconciliation(query) {
  return request({
    url: '/business/reconciliation/export',
    method: 'post',
    data: query,
    responseType: 'blob'
  })
}
