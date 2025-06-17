// 重新导出jwt.js中的函数，以保持向后兼容
export {
  getToken,
  setToken,
  removeToken,
  parseToken,
  getUserRole,
  getUserRoles,
  getUserId,
  getUserInfo,
  isTokenExpired
} from './jwt' 