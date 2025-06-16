import request from './auth'

// 获取知识库列表（分页+搜索+类型筛选）
export const getKnowledgeList = (params) => {
  return request({
    url: '/api/medical-knowledge',
    method: 'get',
    params
  })
}

// 获取知识库详情
export const getKnowledgeDetail = (id) => {
  return request({
    url: `/api/medical-knowledge/${id}`,
    method: 'get'
  })
}

// 创建知识条目
export const createKnowledge = (data) => {
  return request({
    url: '/api/medical-knowledge',
    method: 'post',
    data
  })
}

// 更新知识条目
export const updateKnowledge = (id, data) => {
  return request({
    url: `/api/medical-knowledge/${id}`,
    method: 'put',
    data
  })
}

// 删除知识条目
export const deleteKnowledge = (id) => {
  return request({
    url: `/api/medical-knowledge/${id}`,
    method: 'delete'
  })
} 