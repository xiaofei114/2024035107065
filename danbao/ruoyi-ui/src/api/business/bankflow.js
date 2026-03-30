import request from '@/utils/request'

/**
 * 查询账单台账列表
 * @param {Object} query 查询参数
 * @returns {Promise}
 */
export function listBankFlow(query) {
  return request({
    url: '/business/bankflow/list',
    method: 'get',
    params: query
  })
}

/**
 * 查询账单台账详情
 * @param {Number} id 账单台账ID
 * @returns {Promise}
 */
export function getBankFlow(id) {
  return request({
    url: '/business/bankflow/' + id,
    method: 'get'
  })
}

/**
 * 新增账单台账
 * @param {Object} data 账单台账数据
 * @returns {Promise}
 */
export function addBankFlow(data) {
  return request({
    url: '/business/bankflow',
    method: 'post',
    data: data
  })
}

/**
 * 修改账单台账
 * @param {Object} data 账单台账数据
 * @returns {Promise}
 */
export function updateBankFlow(data) {
  return request({
    url: '/business/bankflow',
    method: 'put',
    data: data
  })
}

/**
 * 删除账单台账
 * @param {Number} id 账单台账ID
 * @returns {Promise}
 */
export function delBankFlow(id) {
  return request({
    url: '/business/bankflow/' + id,
    method: 'delete'
  })
}

/**
 * 导出账单台账
 * @param {Object} query 查询参数
 * @returns {Promise}
 */
export function exportBankFlow(query) {
  return request({
    url: '/business/bankflow/export',
    method: 'post',
    data: query,
    responseType: 'blob'
  })
}
