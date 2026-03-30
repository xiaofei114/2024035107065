import request from '@/utils/request'

/**
 * 业务大屏API
 */

/**
 * 获取大屏总体统计数据
 * @returns {Promise}
 */
export function getScreenStats() {
  return request({
    url: '/business/screen/stats',
    method: 'get'
  })
}

/**
 * 获取按银行分组的担保业务数量（饼图数据）
 * @returns {Promise}
 */
export function getGuaranteeByBank() {
  return request({
    url: '/business/screen/guarantee/bybank',
    method: 'get'
  })
}

/**
 * 获取月度担保费趋势（折线图数据）
 * @returns {Promise}
 */
export function getGuaranteeMonthly() {
  return request({
    url: '/business/screen/guarantee/monthly',
    method: 'get'
  })
}

/**
 * 获取月度收支数据（柱状图数据）
 * @returns {Promise}
 */
export function getBankFlowMonthly() {
  return request({
    url: '/business/screen/bankflow/monthly',
    method: 'get'
  })
}
