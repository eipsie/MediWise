<template>
  <div class="container">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="logo-container">
        <router-link :to="homeRoute" class="logo">
          <span class="logo-icon">🏥</span>
          <span class="logo-text" v-show="!isCollapsed">Medi<span class="logo-accent">Wise</span></span>
        </router-link>
        <el-icon class="collapse-btn" @click="toggleCollapse">
          <component :is="isCollapsed ? 'Expand' : 'Fold'" />
        </el-icon>
      </div>
      
      <!-- 使用SideMenu组件 -->
      <side-menu :is-collapsed="isCollapsed" />
    </aside>
    
    <!-- 主内容区 -->
    <main class="main-content">
      <!-- 顶部导航栏 -->
      <header class="header">
        <!-- 面包屑导航 -->
        <div class="breadcrumb-container">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="homeRoute">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-for="(item, index) in breadcrumbItems" :key="index" :to="item.path">
              {{ item.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <!-- 用户信息 -->
        <div class="user-info">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-profile">
              <el-avatar :size="32" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
              <span class="user-name">{{ userInfo.realName || userInfo.username || '用户' }}</span>
              <el-icon><arrow-down /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="settings">系统设置</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>
      
      <!-- 内容区域 -->
      <div class="page-view">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, defineComponent, h } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  ArrowDown,
  Fold, 
  Expand
} from '@element-plus/icons-vue'
import { getUserInfo, getUserRole, removeToken } from '../../utils/jwt'
import SideMenu from './SideMenu.vue'

// 路由相关
const route = useRoute()
const router = useRouter()

// 侧边栏折叠状态
const isCollapsed = ref(false)
const toggleCollapse = () => {
  isCollapsed.value = !isCollapsed.value
}

// 获取用户信息
const userInfo = ref(getUserInfo() || {})
const hasAdminRole = computed(() => {
  const role = getUserRole()
  return role === 'ADMIN'
})

// 根据角色确定首页路径
const homeRoute = computed(() => {
  const role = getUserRole()
  return role === 'ADMIN' ? '/system/users' : '/dashboard'
})

// 当前活动菜单
const activeMenu = computed(() => {
  return route.path
})

// 面包屑导航
const breadcrumbItems = computed(() => {
  const { path, matched, meta } = route
  
  // 如果是首页，不显示面包屑
  const role = getUserRole()
  if ((role === 'DOCTOR' && path === '/dashboard') || 
      (role === 'ADMIN' && path === '/system/users')) {
    return []
  }
  
  // 根据路由匹配情况生成面包屑
  const result = []
  
  // 处理多级路由
  matched.forEach((item, index) => {
    // 跳过首页和主布局
    if (index === 0) return
    
    // 添加面包屑项
    result.push({
      path: item.path,
      title: item.meta.title || '未命名'
    })
  })
  
  return result
})

// 处理用户下拉菜单命令
const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      ElMessage.info('个人信息功能开发中...')
      break
    case 'settings':
      ElMessage.info('系统设置功能开发中...')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm(
          '确定要退出登录吗？',
          '退出确认',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
        )
        
        // 清除token并跳转到登录页
        removeToken()
        ElMessage.success('已安全退出')
        router.push('/login')
      } catch {
        // 用户取消退出
      }
      break
  }
}

// 组件挂载时检查用户信息
onMounted(() => {
  if (!userInfo.value || !userInfo.value.username) {
    // 获取最新的用户信息
    userInfo.value = getUserInfo() || {}
  }
})
</script>

<style scoped>
.container {
  display: flex;
  height: 100vh;
  font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
  font-size: 14px;
  color: #333;
  background-color: #f9fafa;
  overflow: hidden;
}

/* 侧边栏样式 */
.sidebar {
  width: 240px;
  height: 100vh;
  background: #ffffff;
  padding: 0;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  transition: all 0.3s ease;
  overflow-y: auto;
  position: relative;
  z-index: 100;
  border-radius: 0 16px 16px 0;
  border-left: 4px solid #3f8cda;
  flex-shrink: 0;
  /* 隐藏滚动条但保留滚动功能 */
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE and Edge */
}

.sidebar::-webkit-scrollbar {
  display: none; /* Chrome, Safari, Opera */
}

.sidebar.collapsed {
  width: 64px;
}

.logo-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 28px 16px;
  margin-bottom: 10px;
  position: relative;
}

.logo-container::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 15%;
  width: 70%;
  height: 1px;
  background: rgba(63, 140, 218, 0.2);
}

.logo {
  display: flex;
  align-items: center;
  text-decoration: none;
}

.logo-icon {
  font-size: 24px;
  margin-right: 8px;
}

.logo-text {
  font-size: 24px;
  font-weight: 700;
  color: #3f8cda;
  white-space: nowrap;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  letter-spacing: 0.5px;
}

.logo-accent {
  color: #e2725b;
  font-weight: 800;
}

.collapse-btn {
  cursor: pointer;
  font-size: 20px;
  color: #3f8cda;
}

.sidebar-menu {
  border-right: none;
  background-color: transparent;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 240px;
}

/* 主内容区样式 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  margin-left: 10px;
  height: 100vh;
}

.header {
  height: 64px;
  min-height: 64px;
  max-height: 64px;
  background: linear-gradient(90deg, #3f8cda, #77cdf3);
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
  border-radius: 12px;
  margin: 12px 12px 0 12px;
  flex-shrink: 0;
}

.breadcrumb-container {
  color: #fff;
}

.breadcrumb-container :deep(.el-breadcrumb__inner) {
  color: rgba(255, 255, 255, 0.8);
  font-weight: 500;
}

.breadcrumb-container :deep(.el-breadcrumb__inner.is-link:hover) {
  color: #ffffff;
}

.breadcrumb-container :deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: #ffffff;
  font-weight: 600;
}

.breadcrumb-container :deep(.el-breadcrumb__separator) {
  color: rgba(255, 255, 255, 0.8);
}

.user-info {
  display: flex;
  align-items: center;
}

.user-profile {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 6px;
  transition: background-color 0.3s;
  color: #fff;
}

.user-profile:hover {
  background-color: rgba(255, 255, 255, 0.15);
}

.user-name {
  margin: 0 8px;
  font-size: 14px;
  font-weight: 500;
}

.page-view {
  flex: 1;
  padding: 28px;
  background-color: white;
  margin: 16px;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
  border: 1px solid rgba(106, 152, 233, 0.05);
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  /* 自定义滚动条样式 */
  scrollbar-width: thin; /* Firefox */
  scrollbar-color: rgba(63, 140, 218, 0.2) transparent; /* Firefox */
}

/* Chrome, Safari, Edge 滚动条样式 */
.page-view::-webkit-scrollbar {
  width: 6px;
}

.page-view::-webkit-scrollbar-track {
  background: transparent;
}

.page-view::-webkit-scrollbar-thumb {
  background-color: rgba(63, 140, 218, 0.2);
  border-radius: 10px;
  border: 2px solid transparent;
}

.page-view::-webkit-scrollbar-thumb:hover {
  background-color: rgba(63, 140, 218, 0.4);
}

.page-view:hover {
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.06);
}

/* 菜单项样式 */
:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  height: 54px;
  line-height: 54px;
  border-radius: 12px;
  margin: 8px 10px;
}

:deep(.el-menu-item .el-icon),
:deep(.el-sub-menu__title .el-icon) {
  margin-right: 14px;
  font-size: 18px;
  transition: transform 0.2s ease;
  color: #3f8cda;
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background-color: rgba(63, 140, 218, 0.08);
  color: #3f8cda;
}

:deep(.el-menu-item:hover .el-icon),
:deep(.el-sub-menu__title:hover .el-icon) {
  transform: scale(1.1);
}

:deep(.el-menu-item.is-active) {
  background-color: rgba(63, 140, 218, 0.15);
  color: #3f8cda;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(63, 140, 218, 0.1);
}

:deep(.el-sub-menu .el-menu-item) {
  min-width: auto;
  margin-left: 4px;
  margin-right: 4px;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style> 