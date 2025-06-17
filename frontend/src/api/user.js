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

// 获取所有用户列表（医生列表）
export function getAllUsers() {
  return request({
    url: '/api/users',
    method: 'get'
  })
}

// 获取指定用户信息
export function getUserById(id) {
  return request({
    url: `/api/users/${id}`,
    method: 'get'
  })
}

// 更新用户状态（启用/禁用）
export function updateUserStatus(id, data) {
  return request({
    url: `/api/users/${id}/status`,
    method: 'put',
    data
  })
}

// 重置用户密码
export function resetUserPassword(id) {
  return request({
    url: `/api/users/${id}/reset-password`,
    method: 'post'
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/api/users/${id}`,
    method: 'delete'
  })
}

// 注册新医生（管理员功能）
export function registerDoctor(data) {
  return request({
    url: '/api/auth/register-doctor',
    method: 'post',
    data
  })
} 