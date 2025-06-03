import axios from 'axios'

// 创建axios实例
const request = axios.create({
  baseURL: 'http://localhost:9191', // 后端API基础URL
  timeout: 30000 // 请求超时时间增加到30秒
})

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