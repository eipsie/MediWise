import axios from 'axios'

// 创建axios实例
const request = axios.create({
  baseURL: 'http://localhost:9191', // 后端API基础URL
  timeout: 5000 // 请求超时时间
})

// 登录API
export const login = (data) => {
  return request({
    url: '/api/auth/login',
    method: 'post',
    data
  })
}

// 导出请求实例，方便扩展其他API
export default request 