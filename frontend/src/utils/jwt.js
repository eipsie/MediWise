import { jwtDecode } from "jwt-decode";

// jwt 令牌存储键名
const TokenKey = 'MediWise-Token'

// 获取jwt令牌
export function getToken(){
  return localStorage.getItem(TokenKey)
}

// 设置jwt令牌
export function setToken(token){
  return localStorage.setItem(TokenKey, token)
}

// 移除jwt令牌
export function removeToken(){
  return localStorage.removeItem(TokenKey)
}

// 解码jwt令牌
export function parseToken(){
  const token = getToken()
  if(!token) return null

  try{
    return jwtDecode(token)
  } catch(error){
    console.error("jwt解码失败", error)
    return null
  }
}

// 获取角色
export function getUserRole(){
  const decoded = parseToken()
  return decoded ? decoded.role : null
}

// 保持向后兼容
export function getUserRoles(){
  return getUserRole()
}

// 获取用户ID - 使用id字段
export function getUserId() {
  const decoded = parseToken()
  return decoded ? decoded.id : null
}

// 获取用户基本信息 - 直接使用后端字段结构
export function getUserInfo(){
  const decoded = parseToken()
  if(!decoded) return {}

  return {
    id: decoded.id,
    username: decoded.username,
    realName: decoded.realName,
    role: decoded.role
  }
}

// 检查令牌是否过期
export function isTokenExpired(){
  const decoded = parseToken()
  if(!decoded || !decoded.exp) return true

  // exp是以秒为单位的时间戳
  const currentTime = Date.now() / 1000
  return decoded.exp < currentTime
}
