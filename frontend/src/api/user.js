import request from './auth'

// 获取当前用户个人信息
export function getCurrentUserProfile() {
  return request({
    url: '/api/users/profile',
    method: 'get'
  })
}

// 更新当前用户个人信息
export function updateUserProfile(data) {
  return request({
    url: '/api/users/profile',
    method: 'put',
    data
  })
}

// 修改密码
export function changePassword(data) {
  return request({
    url: '/api/users/password',
    method: 'put',
    data
  })
} 