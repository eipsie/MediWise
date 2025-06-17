<template>
  <div class="admin-dashboard">
    <h2 class="page-title">管理控制台</h2>
    
    <!-- 统计卡片区域 -->
    <div class="stat-cards">
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon users">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.userCount || 0 }}</div>
            <div class="stat-label">系统用户</div>
          </div>
        </div>
        <div class="stat-footer">
          <span class="trend-up" v-if="statistics.newUsersThisMonth > 0">↑ {{ statistics.newUsersThisMonth }}</span>
          <span class="trend-text">本月新增</span>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon roles">
            <el-icon><UserFilled /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.roleCount || 0 }}</div>
            <div class="stat-label">系统角色</div>
          </div>
        </div>
        <div class="stat-footer">
          <span class="trend-text">固定角色</span>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon logs">
            <el-icon><List /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.logCount || 0 }}</div>
            <div class="stat-label">系统日志</div>
          </div>
        </div>
        <div class="stat-footer">
          <span class="trend-up" v-if="statistics.newLogsToday > 0">↑ {{ statistics.newLogsToday }}</span>
          <span class="trend-text">今日新增</span>
        </div>
      </el-card>
    </div>
    
    <!-- 快捷入口区域 -->
    <h3 class="section-title">快捷入口</h3>
    <div class="quick-access">
      <el-row :gutter="20">
        <el-col :xs="12" :sm="6" :md="6">
          <router-link to="/system/users" class="quick-link">
            <el-card shadow="hover" class="quick-card">
              <div class="quick-icon">
                <el-icon><User /></el-icon>
              </div>
              <div class="quick-title">医生管理</div>
              <div class="quick-desc">管理系统医生账号</div>
            </el-card>
          </router-link>
        </el-col>
        
        <el-col :xs="12" :sm="6" :md="6">
          <router-link to="/patients/list" class="quick-link">
            <el-card shadow="hover" class="quick-card">
              <div class="quick-icon">
                <el-icon><Avatar /></el-icon>
              </div>
              <div class="quick-title">患者管理</div>
              <div class="quick-desc">管理患者信息</div>
            </el-card>
          </router-link>
        </el-col>
        
        <el-col :xs="12" :sm="6" :md="6">
          <router-link to="/knowledge/list" class="quick-link">
            <el-card shadow="hover" class="quick-card">
              <div class="quick-icon">
                <el-icon><Document /></el-icon>
              </div>
              <div class="quick-title">知识列表</div>
              <div class="quick-desc">管理医疗知识库</div>
            </el-card>
          </router-link>
        </el-col>
        
        <el-col :xs="12" :sm="6" :md="6">
          <router-link to="/system/logs" class="quick-link">
            <el-card shadow="hover" class="quick-card">
              <div class="quick-icon">
                <el-icon><List /></el-icon>
              </div>
              <div class="quick-title">系统日志</div>
              <div class="quick-desc">查看系统操作记录</div>
            </el-card>
          </router-link>
        </el-col>
      </el-row>
    </div>
    
    <!-- 最近活动区域 -->
    <h3 class="section-title">最近活动</h3>
    <el-card class="recent-activity">
      <el-table :data="activities" style="width: 100%" v-loading="activitiesLoading">
        <el-table-column label="时间" width="180">
          <template #default="scope">
            <span>{{ formatDateTime(scope.row.logTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="用户" width="120">
          <template #default="scope">
            <span>{{ scope.row.username }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <span>{{ scope.row.actionDesc }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="IP地址" width="140" align="right">
          <template #default="scope">
            <span>{{ scope.row.ipAddress }}</span>
          </template>
        </el-table-column>
      </el-table>
      <div class="view-more">
        <router-link to="/system/logs">查看更多</router-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { 
  User, 
  UserFilled,
  List,
  Document,
  Avatar
} from '@element-plus/icons-vue'
import { getRecentLogs } from '../../api/log'
import { getAdminDashboardStatistics } from '../../api/statistics'
import { formatDateTime } from '../../utils/format'

// 最近活动数据
const activities = ref([])
const activitiesLoading = ref(false)

// 控制台统计数据
const statistics = ref({
  userCount: 0,
  newUsersThisMonth: 0,
  roleCount: 0,
  logCount: 0,
  newLogsToday: 0
})

// 获取控制台统计数据
const fetchStatistics = () => {
  getAdminDashboardStatistics()
    .then(response => {
      if (response.data && response.data.code === 1) {
        statistics.value = response.data.data || {}
      } else {
        console.error('获取控制台统计数据失败:', response.data?.msg)
      }
    })
    .catch(error => {
      console.error('获取控制台统计数据出错:', error)
    })
}

// 获取最近活动
const fetchRecentActivities = () => {
  activitiesLoading.value = true
  getRecentLogs(10)
    .then(response => {
      if (response.data && response.data.code === 1) {
        activities.value = response.data.data || []
      } else {
        console.error('获取最近活动失败:', response.data?.msg)
        activities.value = []
      }
    })
    .catch(error => {
      console.error('获取最近活动出错:', error)
      activities.value = []
    })
    .finally(() => {
      activitiesLoading.value = false
    })
}

// 获取状态标签类型
const getStatusType = (status) => {
  const types = {
    'SUCCESS': 'success',
    'FAILURE': 'warning',
    'ERROR': 'danger'
  }
  return types[status] || 'info'
}

// 获取状态标签文本
const getStatusLabel = (status) => {
  const labels = {
    'SUCCESS': '成功',
    'FAILURE': '失败',
    'ERROR': '错误'
  }
  return labels[status] || status
}

// 组件挂载时获取数据
onMounted(() => {
  fetchStatistics()
  fetchRecentActivities()
})
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
}

.page-title {
  margin-bottom: 24px;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.section-title {
  margin: 30px 0 20px;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

/* 统计卡片样式 */
.stat-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  border-radius: 8px;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 24px;
  color: white;
}

.stat-icon.users {
  background: linear-gradient(135deg, #3f8cda 0%, #2a6db8 100%);
}

.stat-icon.roles {
  background: linear-gradient(135deg, #67C23A 0%, #4a9e20 100%);
}

.stat-icon.logs {
  background: linear-gradient(135deg, #909399 0%, #606266 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.stat-footer {
  display: flex;
  align-items: center;
  font-size: 13px;
  color: #909399;
}

.trend-up {
  color: #67C23A;
  margin-right: 5px;
}

/* 快捷入口样式 */
.quick-access {
  margin-bottom: 30px;
}

.quick-link {
  text-decoration: none;
  display: block;
  margin-bottom: 20px;
}

.quick-card {
  height: 100%;
  border-radius: 8px;
  text-align: center;
  padding: 20px;
  transition: all 0.3s;
}

.quick-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.quick-icon {
  font-size: 36px;
  margin-bottom: 15px;
  color: #3f8cda;
}

.quick-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.quick-desc {
  font-size: 13px;
  color: #909399;
}

/* 最近活动样式 */
.recent-activity {
  margin-bottom: 30px;
}

.view-more {
  margin-top: 15px;
  text-align: right;
}

.view-more a {
  color: #409EFF;
  text-decoration: none;
  font-size: 14px;
}

.view-more a:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  .stat-cards {
    grid-template-columns: 1fr;
  }
}
</style> 