<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

// 获取路由实例
const router = useRouter()

// 用户信息
const userInfo = ref({})
const loading = ref(true)

// 计算当前时间问候语
const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '夜深了'
  if (hour < 12) return '早上好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

// 格式化最后登录时间
const formatLastLoginTime = computed(() => {
  if (!userInfo.value.lastLoginTime) return '首次登录'
  return new Date(userInfo.value.lastLoginTime).toLocaleString('zh-CN')
})

// 组件挂载时从本地存储获取用户信息
onMounted(() => {
  try {
    const storedUserInfo = localStorage.getItem('userInfo')
    if (storedUserInfo) {
      userInfo.value = JSON.parse(storedUserInfo)
      setTimeout(() => {
        loading.value = false
      }, 500)
    } else {
      ElMessage.warning('未检测到登录信息，请重新登录')
      router.push('/login')
    }
  } catch (error) {
    console.error('解析用户信息失败:', error)
    ElMessage.error('用户信息解析失败，请重新登录')
    router.push('/login')
  }
})

// 退出登录
const logout = async () => {
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

    localStorage.removeItem('userInfo')
    ElMessage.success('已安全退出')
    router.push('/login')
  } catch {
    // 用户取消退出
  }
}

// 处理下拉菜单命令
const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      ElMessage.info('个人信息功能开发中...')
      break
    case 'settings':
      ElMessage.info('系统设置功能开发中...')
      break
    case 'logout':
      logout()
      break
  }
}
</script>

<template>
  <div class="home-page">
    <!-- 顶部导航栏 -->
    <header class="navbar">
      <div class="nav-brand">
        <span class="brand-icon">🏥</span>
        <span class="brand-text">MediWise 智慧医疗系统</span>
      </div>
      <div class="nav-user">
        <el-dropdown @command="handleCommand" trigger="click">
          <div class="user-info">
            <el-avatar :size="32" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
            <span class="user-name">{{ userInfo.realName || userInfo.username || '系统管理员' }}</span>
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

    <!-- 主要内容区域 -->
    <main class="main-content">
      <!-- 欢迎横幅 -->
      <section class="welcome-banner" v-loading="loading">
        <div class="welcome-text">
          <h2>{{ greeting }}，{{ userInfo.realName || userInfo.username || '系统管理员' }}！</h2>
          <p>欢迎回到MediWise智慧医疗诊断系统</p>
        </div>
        <div class="status-badge">
          <el-tag type="success" size="large">在线</el-tag>
        </div>
      </section>

      <!-- 内容网格 -->
      <section class="content-grid">
        <!-- 个人信息卡片 -->
        <el-card class="info-card">
          <template #header>
            <div class="card-header">
              <span>👤 个人信息</span>
              <el-button type="primary" size="small">编辑</el-button>
            </div>
          </template>
          <div class="info-list">
            <div class="info-row">
              <span class="label">用户名：</span>
              <span class="value">{{ userInfo.username || 'admin' }}</span>
            </div>
            <div class="info-row">
              <span class="label">真实姓名：</span>
              <span class="value">{{ userInfo.realName || '系统管理员' }}</span>
            </div>
            <div class="info-row">
              <span class="label">邮箱地址：</span>
              <span class="value">{{ userInfo.email || '未设置' }}</span>
            </div>
            <div class="info-row">
              <span class="label">联系电话：</span>
              <span class="value">{{ userInfo.phone || '未设置' }}</span>
            </div>
            <div class="info-row">
              <span class="label">部门科室：</span>
              <span class="value">{{ userInfo.department || '未设置' }}</span>
            </div>
            <div class="info-row">
              <span class="label">最后登录：</span>
              <span class="value">{{ formatLastLoginTime }}</span>
            </div>
          </div>
        </el-card>

        <!-- 快速功能卡片 -->
        <el-card class="actions-card">
          <template #header>
            <span>⚡ 快速功能</span>
          </template>
          <div class="action-grid">
            <div class="action-item" @click="ElMessage.info('功能开发中...')">
              <div class="action-icon">📋</div>
              <span>患者管理</span>
            </div>
            <div class="action-item" @click="ElMessage.info('功能开发中...')">
              <div class="action-icon">🩺</div>
              <span>诊断记录</span>
            </div>
            <div class="action-item" @click="router.push('/ai-chat')">
              <div class="action-icon">🤖</div>
              <span>AI智能问诊</span>
            </div>
            <div class="action-item" @click="ElMessage.info('功能开发中...')">
              <div class="action-icon">💊</div>
              <span>药品管理</span>
            </div>
            <div class="action-item" @click="ElMessage.info('功能开发中...')">
              <div class="action-icon">📊</div>
              <span>数据统计</span>
            </div>
            <div class="action-item" @click="logout">
              <div class="action-icon">🚪</div>
              <span>退出系统</span>
            </div>
          </div>
        </el-card>

        <!-- 系统通知卡片 -->
        <el-card class="notice-card">
          <template #header>
            <span>📢 系统通知</span>
          </template>
          <div class="notice-list">
            <div class="notice-item">
              <el-tag type="info" size="small">系统</el-tag>
              <span class="notice-text">欢迎使用MediWise智慧医疗系统！</span>
              <small>刚刚</small>
            </div>
            <div class="notice-item">
              <el-tag type="success" size="small">更新</el-tag>
              <span class="notice-text">系统已更新至最新版本</span>
              <small>1小时前</small>
            </div>
            <div class="notice-item">
              <el-tag type="warning" size="small">提醒</el-tag>
              <span class="notice-text">请及时备份重要数据</span>
              <small>昨天</small>
            </div>
          </div>
        </el-card>
      </section>
    </main>
  </div>
</template>

<style scoped>
/* 全局重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.home-page {
  width: 100vw;
  height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  display: flex;
  flex-direction: column;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
}

/* 顶部导航栏 */
.navbar {
  height: 60px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  flex-shrink: 0;
}

.nav-brand {
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.brand-icon {
  margin-right: 8px;
  font-size: 22px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f5f5;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
}

/* 主要内容区域 */
.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 欢迎横幅 */
.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px;
  border-radius: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.welcome-text h2 {
  font-size: 24px;
  margin-bottom: 8px;
  font-weight: 600;
}

.welcome-text p {
  font-size: 16px;
  opacity: 0.9;
}

/* 内容网格 */
.content-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  grid-template-areas:
    "info actions"
    "info notice";
  flex: 1;
  min-height: 0;
}

.info-card {
  grid-area: info;
}

.actions-card {
  grid-area: actions;
}

.notice-card {
  grid-area: notice;
}

/* 卡片样式 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

/* 个人信息 */
.info-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-row {
  display: flex;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-row:last-child {
  border-bottom: none;
}

.info-row .label {
  width: 80px;
  font-weight: 500;
  color: #606266;
  flex-shrink: 0;
}

.info-row .value {
  flex: 1;
  color: #303133;
}

/* 快速功能 */
.action-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-item:hover {
  background: #e9ecef;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.action-icon {
  font-size: 20px;
  margin-bottom: 6px;
}

.action-item span {
  font-size: 13px;
  font-weight: 500;
}

/* 系统通知 */
.notice-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.notice-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-text {
  flex: 1;
  font-size: 14px;
}

.notice-item small {
  color: #909399;
  font-size: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .navbar {
    padding: 0 16px;
  }

  .nav-brand {
    font-size: 16px;
  }

  .main-content {
    padding: 16px;
  }

  .welcome-banner {
    flex-direction: column;
    gap: 16px;
    text-align: center;
    padding: 20px;
  }

  .welcome-text h2 {
    font-size: 20px;
  }

  .content-grid {
    grid-template-columns: 1fr;
    grid-template-areas:
      "info"
      "actions"
      "notice";
  }

  .action-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 8px;
  }

  .action-item {
    padding: 12px 8px;
  }

  .action-icon {
    font-size: 18px;
  }

  .info-row .label {
    width: 70px;
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .action-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>