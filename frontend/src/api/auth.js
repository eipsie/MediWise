import axios from 'axios'
import { getToken, removeToken } from '../utils/jwt'
import { ElMessage } from 'element-plus'
import router from '../router'

// 创建axios实例
const request = axios.create({
  baseURL: 'http://localhost:9191', // 后端API基础URL
  timeout: 30000 // 请求超时时间增加到30秒
})

// 添加请求拦截器
request.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      // 确保Authorization头格式正确
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 添加响应拦截器
request.interceptors.response.use(
  response => response,
  error => {
    if (error.response) {
      if (error.response.status === 401) {
        ElMessage.error('登录已过期，请重新登录')
        removeToken()
        router.push('/login')
      }
    }
    return Promise.reject(error)
  }
)

// 登录API
export const login = (data) => {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data
  })
}

// 注册API
export const register = (data) => {
  return request({
    url: '/api/auth/register',
    method: 'post',
    data
  })
}

// 导出请求实例，方便扩展其他API
export default request 