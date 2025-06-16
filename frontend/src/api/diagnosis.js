import request from './auth'

/**
 * 创建诊断记录
 * @param data 诊断数据
 * @returns {Promise}
 */
export const createDiagnosis = (data) => {
  return request({
    url: '/api/diagnoses',
    method: 'post',
    data
  })
}

/**
 * 获取诊断记录详情
 * @param id 诊断记录ID
 * @returns {Promise}
 */
export const getDiagnosisById = (id) => {
  return request({
    url: `/api/diagnoses/${id}`,
    method: 'get'
  })
}

/**
 * 更新诊断记录
 * @param id 诊断记录ID
 * @param data 更新数据
 * @returns {Promise}
 */
export const updateDiagnosis = (id, data) => {
  return request({
    url: `/api/diagnoses/${id}`,
    method: 'put',
    data
  })
}

/**
 * AI分析诊断
 * @param data 分析请求数据
 * @returns {Promise}
 */
export const analyzeDiagnosis = (data) => {
  return request({
    url: '/api/diagnoses/analyze',
    method: 'post',
    data,
    timeout: 30000 // 增加超时时间到30秒，以适应长时间运行的AI分析请求
  })
}

/**
 * 获取患者诊断历史
 * @param patientId 患者ID
 * @param page 页码
 * @param size 每页大小
 * @returns {Promise}
 */
export const getPatientDiagnoses = (patientId, page = 1, size = 10) => {
  return request({
    url: `/api/diagnoses/patient/${patientId}`,
    method: 'get',
    params: { page, size }
  })
}

/**
 * 分页查询诊断记录列表
 * @param params 查询参数
 * @returns {Promise}
 */
export const getDiagnosisList = (params) => {
  // 过滤掉空字符串参数
  const filteredParams = {};
  Object.keys(params).forEach(key => {
    if (params[key] !== undefined && params[key] !== '') {
      filteredParams[key] = params[key];
    }
  });
  
  return request({
    url: '/api/diagnoses',
    method: 'get',
    params: filteredParams
  })
}

/**
 * 获取诊断状态列表
 * @returns {Array} 诊断状态列表
 */
export const getDiagnosisStatusList = () => {
  return [
    { value: 'DRAFT', label: '草稿' },
    { value: 'ANALYZING', label: '分析中' },
    { value: 'PENDING', label: '待确认' },
    { value: 'COMPLETED', label: '已完成' }
  ]
}

/**
 * 获取诊断状态显示文本
 * @param status 状态值
 * @returns {string} 状态文本
 */
export const getDiagnosisStatusText = (status) => {
  const statusMap = {
    'DRAFT': '草稿',
    'ANALYZING': '分析中',
    'PENDING': '待确认',
    'COMPLETED': '已完成'
  }
  return statusMap[status] || '未知状态'
}

/**
 * 获取诊断状态显示类型
 * @param status 状态值
 * @returns {string} Element-Plus Tag类型
 */
export const getDiagnosisStatusType = (status) => {
  const typeMap = {
    'DRAFT': 'info',
    'ANALYZING': 'warning',
    'PENDING': 'success',
    'COMPLETED': 'primary'
  }
  return typeMap[status] || 'info'
} 