import request from '@/utils/request'

/**
 * 查询担保业务列表
 * @param {Object} query 查询参数
 * @returns {Promise}
 */
export function listGuarantee(query) {
  return request({
    url: '/business/guarantee/list',
    method: 'get',
    params: query
  })
}

/**
 * 查询担保业务详情
 * @param {Number} id 担保业务ID
 * @returns {Promise}
 */
export function getGuarantee(id) {
  return request({
    url: '/business/guarantee/' + id,
    method: 'get'
  })
}

/**
 * 新增担保业务
 * @param {Object} data 担保业务数据
 * @returns {Promise}
 */
export function addGuarantee(data) {
  return request({
    url: '/business/guarantee',
    method: 'post',
    data: data
  })
}

/**
 * 修改担保业务
 * @param {Object} data 担保业务数据
 * @returns {Promise}
 */
export function updateGuarantee(data) {
  return request({
    url: '/business/guarantee',
    method: 'put',
    data: data
  })
}

/**
 * 删除担保业务
 * @param {Number} id 担保业务ID
 * @returns {Promise}
 */
export function delGuarantee(id) {
  return request({
    url: '/business/guarantee/' + id,
    method: 'delete'
  })
}

/**
 * 导出担保业务
 * @param {Object} query 查询参数
 * @returns {Promise}
 */
export function exportGuarantee(query) {
  return request({
    url: '/business/guarantee/export',
    method: 'post',
    data: query,
    responseType: 'blob'
  })
}
