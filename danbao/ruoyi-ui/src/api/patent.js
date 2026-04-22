import request from '@/utils/request'

// ==================== 专利数据管理CRUD接口 ====================

// 分页查询专利列表
export function listPatent(query) {
  return request({
    url: '/api/patent/list',
    method: 'get',
    params: query
  })
}

// 根据ID获取专利详情
export function getPatent(id) {
  return request({
    url: '/api/patent/' + id,
    method: 'get'
  })
}

// 添加专利
export function addPatent(data) {
  return request({
    url: '/api/patent',
    method: 'post',
    data: data
  })
}

// 更新专利
export function updatePatent(data) {
  return request({
    url: '/api/patent',
    method: 'put',
    data: data
  })
}

// 删除专利
export function delPatent(id) {
  return request({
    url: '/api/patent/' + id,
    method: 'delete'
  })
}

// 批量删除专利
export function delBatchPatent(ids) {
  return request({
    url: '/api/patent/batch',
    method: 'delete',
    data: ids
  })
}

// ==================== 数据导入导出接口 ====================

// 导出专利数据
export function exportPatent(keyword) {
  return request({
    url: '/api/patent/export',
    method: 'get',
    params: { keyword },
    responseType: 'blob'
  })
}

// 导入预览
export function importPreview(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/api/patent/import/preview',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 确认导入
export function importPatent(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/api/patent/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 下载导入模板
export function downloadTemplate() {
  return request({
    url: '/api/patent/import/template',
    method: 'get',
    responseType: 'blob'
  })
}
