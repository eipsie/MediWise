import { reactive, readonly } from 'vue'

// 创建一个响应式状态对象
const state = reactive({
  // 用户信息
  userInfo: null,
  // 系统设置
  settings: {
    theme: 'light',
    sidebarCollapsed: false
  }
})

// 状态操作方法
const actions = {
  // 设置用户信息
  setUserInfo(userInfo) {
    state.userInfo = userInfo
  },
  
  // 清除用户信息
  clearUserInfo() {
    state.userInfo = null
  },
  
  // 更新系统设置
  updateSettings(settings) {
    state.settings = { ...state.settings, ...settings }
  },
  
  // 切换侧边栏折叠状态
  toggleSidebar() {
    state.settings.sidebarCollapsed = !state.settings.sidebarCollapsed
  }
}

// 导出只读状态和操作方法
export const useStore = () => ({
  state: readonly(state),
  ...actions
})

// 获取用户角色
export const getUserRole = () => {
  return state.userInfo ? state.userInfo.role : null
}

// 获取用户信息
export const getUserInfo = () => {
  return state.userInfo
} 