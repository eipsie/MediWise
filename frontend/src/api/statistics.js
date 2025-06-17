import request from './auth'

/**
 * 获取管理员控制台统计数据
 * @returns {Promise} 返回管理员控制台统计数据
 */
export const getAdminDashboardStatistics = () => {
  return request({
    url: '/api/statistics/admin/dashboard',
    method: 'get'
  })
}

/**
 * 获取医生控制台统计数据
 * @returns {Promise} 返回医生控制台统计数据
 */
export const getDoctorDashboardStatistics = () => {
  return request({
    url: '/api/statistics/doctor/dashboard',
    method: 'get'
  })
} 