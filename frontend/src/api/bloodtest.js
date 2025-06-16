import request from './auth'

/**
 * 创建血常规检查记录
 * @param data 检查数据
 * @returns {Promise}
 */
export const createBloodTest = (data) => {
  console.log('Creating blood test with data:', JSON.stringify(data, null, 2));
  return request({
    url: '/api/blood-tests',
    method: 'post',
    data,
    timeout: 15000 // 增加超时时间为15秒
  }).catch(error => {
    console.error('Blood test creation error:', error);
    if (error.response) {
      console.error('Response data:', error.response.data);
      console.error('Response status:', error.response.status);
    }
    throw error;
  });
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
    timeout: 30000 // 增加超时时间到30秒，以适应长时间运行的AI分析请求
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
  const filteredParams = {};
  Object.keys(params).forEach(key => {
    if (params[key] !== undefined && params[key] !== '') {
      filteredParams[key] = params[key];
    }
  });
  
  return request({
    url: '/api/blood-tests',
    method: 'get',
    params: filteredParams
  })
}

/**
 * 格式化血常规字段名称
 * @param key 字段名
 * @returns {string} 显示名称
 */
export const getBloodTestFieldLabel = (key) => {
  const labels = {
    wbc: '白细胞计数',
    rbc: '红细胞计数',
    hgb: '血红蛋白',
    plt: '血小板计数',
    neutp: '中性粒细胞百分比',
    lymp: '淋巴细胞百分比'
  }
  return labels[key] || key
}

/**
 * 获取血常规字段单位
 * @param key 字段名
 * @returns {string} 字段单位
 */
export const getBloodTestFieldUnit = (key) => {
  const units = {
    wbc: '× 10^9/L',
    rbc: '× 10^12/L',
    hgb: 'g/L',
    plt: '× 10^9/L',
    neutp: '%',
    lymp: '%'
  }
  return units[key] || ''
}

/**
 * 获取血常规字段参考范围
 * @param key 字段名
 * @returns {Object} 包含最小值、最大值的对象
 */
export const getBloodTestReferenceRange = (key) => {
  const ranges = {
    wbc: { min: 4.0, max: 10.0 },
    rbc: { min: 3.5, max: 5.5 },
    hgb: { min: 110, max: 160 },
    plt: { min: 100, max: 300 },
    neutp: { min: 50, max: 70 },
    lymp: { min: 20, max: 40 }
  }
  return ranges[key] || { min: null, max: null }
}

/**
 * 判断血常规检查值是否异常
 * @param key 字段名
 * @param value 字段值
 * @returns {string|null} 异常类型：'high'高于参考范围, 'low'低于参考范围, null正常
 */
export const getBloodTestValueStatus = (key, value) => {
  if (value === null || value === undefined) {
    return null;
  }
  
  const range = getBloodTestReferenceRange(key);
  if (range.min === null || range.max === null) {
    return null;
  }
  
  const numValue = Number(value);
  if (isNaN(numValue)) {
    return null;
  }
  
  if (numValue > range.max) {
    return 'high';
  } else if (numValue < range.min) {
    return 'low';
  }
  return null;
} 