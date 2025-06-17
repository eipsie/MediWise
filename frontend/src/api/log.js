import request from './auth'

/**
 * 分页查询系统日志
 * @param {Object} params - 查询参数
 * @returns {Promise}
 */
export const getLogsByPage = (params) => {
  return request({
    url: '/api/system/logs',
    method: 'get',
    params
  })
}

/**
 * 获取最近的日志活动
 * @param {number} limit - 限制数量
 * @returns {Promise}
 */
export const getRecentLogs = (limit = 5) => {
  return request({
    url: '/api/system/logs/recent',
    method: 'get',
    params: { limit }
  })
}

/**
 * 获取指定用户的日志
 * @param {number} userId - 用户ID
 * @param {number} limit - 限制数量
 * @returns {Promise}
 */
export const getUserLogs = (userId, limit = 5) => {
  return request({
    url: '/api/system/logs/user',
    method: 'get',
    params: { userId, limit }
  })
} 