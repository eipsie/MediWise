import request from './auth'
import { BLOOD_TEST_CONFIG } from '../config/constants'

/**
 * 创建血常规检查记录
 * @param data 检查数据
 * @returns {Promise}
 */
export const createBloodTest = (data) => {
  return request({
    url: '/api/blood-tests',
    method: 'post',
    data,
    timeout: 15000
  })
}

/**
 * 获取血常规检查详情
 * @param id 血常规检查ID
 * @returns {Promise}
 */
export const getBloodTestById = (id) => {
  return request({
    url: `/api/blood-tests/${id}`,
    method: 'get'
  })
}

/**
 * 删除血常规检查记录
 * @param id 血常规检查ID
 * @returns {Promise}
 */
export const deleteBloodTest = (id) => {
  return request({
    url: `/api/blood-tests/${id}`,
    method: 'delete'
  })
}

/**
 * AI分析血常规检查
 * @param data 分析请求数据 { bloodTestId, applyMedicalPrompt }
 * @returns {Promise}
 */
export const analyzeBloodTest = (data) => {
  return request({
    url: '/api/blood-tests/analyze',
    method: 'post',
    data,
    timeout: 30000
  })
}

/**
 * 获取患者血常规检查历史
 * @param patientId 患者ID
 * @param page 页码
 * @param size 每页大小
 * @returns {Promise}
 */
export const getPatientBloodTests = (patientId, page = 1, size = 10) => {
  return request({
    url: `/api/blood-tests/patient/${patientId}`,
    method: 'get',
    params: { page, size }
  })
}

/**
 * 分页查询血常规检查记录列表
 * @param params 查询参数
 * @returns {Promise}
 */
export const getBloodTestList = (params) => {
  // 过滤掉空字符串参数
  const filteredParams = {}
  Object.keys(params).forEach(key => {
    if (params[key] !== undefined && params[key] !== '') {
      filteredParams[key] = params[key]
    }
  })
  
  return request({
    url: '/api/blood-tests',
    method: 'get',
    params: filteredParams
  })
}

/**
 * 获取所有血常规检查项目配置
 * @returns {Array} 配置数组
 */
export const getBloodTestFields = () => {
  return BLOOD_TEST_CONFIG.fields
}

/**
 * 获取单个血常规检查项配置
 * @param {string} field 字段名称
 * @returns {Object|null} 配置对象
 */
export const getBloodTestField = (field) => {
  return BLOOD_TEST_CONFIG.fields.find(item => item.field === field) || null
}

/**
 * 格式化血常规字段名称
 * @param key 字段名
 * @returns {string} 显示名称
 */
export const getBloodTestFieldLabel = (key) => {
  const field = getBloodTestField(key)
  return field ? field.label : key
}

/**
 * 获取血常规字段单位
 * @param key 字段名
 * @returns {string} 字段单位
 */
export const getBloodTestFieldUnit = (key) => {
  const field = getBloodTestField(key)
  return field ? field.unit : ''
}

/**
 * 获取血常规字段参考范围
 * @param key 字段名
 * @returns {Object} 包含最小值、最大值的对象
 */
export const getBloodTestReferenceRange = (key) => {
  const field = getBloodTestField(key)
  return field ? { min: field.min, max: field.max } : { min: null, max: null }
}

/**
 * 判断血常规检查值是否异常
 * @param key 字段名
 * @param value 字段值
 * @returns {string|null} 异常类型：'high'高于参考范围, 'low'低于参考范围, null正常
 */
export const getBloodTestValueStatus = (key, value) => {
  if (value === null || value === undefined) {
    return null
  }
  
  const range = getBloodTestReferenceRange(key)
  if (range.min === null || range.max === null) {
    return null
  }
  
  const numValue = Number(value)
  if (isNaN(numValue)) {
    return null
  }
  
  if (numValue > range.max) {
    return 'high'
  } else if (numValue < range.min) {
    return 'low'
  }
  return null
} 