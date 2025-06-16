import request from './auth'

/**
 * 获取患者列表（分页查询）
 * @param params 查询参数 {current, size, name, idCard, phone}
 * @returns {Promise}
 */
export const getPatientList = (params) => {
  return request({
    url: '/api/patients/page',
    method: 'get',
    params
  })
}

/**
 * 获取我的患者列表（当前医生创建的）
 * @returns {Promise}
 */
export const getMyPatients = () => {
  return request({
    url: '/api/patients/my-patients',
    method: 'get'
  })
}

/**
 * 根据ID获取患者详情
 * @param id 患者ID
 * @returns {Promise}
 */
export const getPatientById = (id) => {
  return request({
    url: `/api/patients/${id}`,
    method: 'get'
  })
}

/**
 * 根据患者编号获取患者详情
 * @param patientNo 患者编号
 * @returns {Promise}
 */
export const getPatientByPatientNo = (patientNo) => {
  return request({
    url: `/api/patients/no/${patientNo}`,
    method: 'get'
  })
}

/**
 * 创建患者
 * @param data 患者信息
 * @returns {Promise}
 */
export const createPatient = (data) => {
  return request({
    url: '/api/patients',
    method: 'post',
    data
  })
}

/**
 * 更新患者信息
 * @param id 患者ID
 * @param data 更新数据
 * @returns {Promise}
 */
export const updatePatient = (id, data) => {
  return request({
    url: `/api/patients/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除患者
 * @param id 患者ID
 * @returns {Promise}
 */
export const deletePatient = (id) => {
  return request({
    url: `/api/patients/${id}`,
    method: 'delete'
  })
} 