<template>
  <div class="dashboard-container">
    <h2 class="page-title">医生工作台</h2>
    
    <!-- 统计卡片区域 -->
    <div class="stat-cards">
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon patients">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.patientCount || 0 }}</div>
            <div class="stat-label">患者总数</div>
          </div>
        </div>
        <div class="stat-footer">
          <span class="trend-up" v-if="parseFloat(statistics.patientGrowthRate) > 0">↑ {{ statistics.patientGrowthRate }}%</span>
          <span class="trend-down" v-else-if="parseFloat(statistics.patientGrowthRate) < 0">↓ {{ Math.abs(parseFloat(statistics.patientGrowthRate)) }}%</span>
          <span class="trend-text">较上月</span>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon diagnoses">
            <el-icon><Stethoscope /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.diagnosisCount || 0 }}</div>
            <div class="stat-label">诊断记录</div>
          </div>
        </div>
        <div class="stat-footer">
          <span class="trend-up" v-if="parseFloat(statistics.diagnosisGrowthRate) > 0">↑ {{ statistics.diagnosisGrowthRate }}%</span>
          <span class="trend-down" v-else-if="parseFloat(statistics.diagnosisGrowthRate) < 0">↓ {{ Math.abs(parseFloat(statistics.diagnosisGrowthRate)) }}%</span>
          <span class="trend-text">较上月</span>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon ai">
            <el-icon><ChatDotRound /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.aiCallCount || 0 }}</div>
            <div class="stat-label">AI问诊次数</div>
          </div>
        </div>
        <div class="stat-footer">
          <span class="trend-up" v-if="parseFloat(statistics.aiCallGrowthRate) > 0">↑ {{ statistics.aiCallGrowthRate }}%</span>
          <span class="trend-down" v-else-if="parseFloat(statistics.aiCallGrowthRate) < 0">↓ {{ Math.abs(parseFloat(statistics.aiCallGrowthRate)) }}%</span>
          <span class="trend-text">较上月</span>
        </div>
      </el-card>
    </div>
    
    <!-- 快捷入口区域 -->
    <h3 class="section-title">快捷入口</h3>
    <div class="quick-access">
      <el-row :gutter="20">
        <el-col :xs="12" :sm="8" :md="6">
          <router-link to="/ai-chat" class="quick-link">
            <el-card shadow="hover" class="quick-card">
              <div class="quick-icon">
                <el-icon><ChatDotRound /></el-icon>
              </div>
              <div class="quick-title">AI智能问诊</div>
              <div class="quick-desc">使用AI辅助医疗诊断</div>
            </el-card>
          </router-link>
        </el-col>
        
        <el-col :xs="12" :sm="8" :md="6">
          <router-link to="/patients/list" class="quick-link">
            <el-card shadow="hover" class="quick-card">
              <div class="quick-icon">
                <el-icon><User /></el-icon>
              </div>
              <div class="quick-title">患者管理</div>
              <div class="quick-desc">管理患者信息</div>
            </el-card>
          </router-link>
        </el-col>
        
        <el-col :xs="12" :sm="8" :md="6">
          <router-link to="/diagnoses/list" class="quick-link">
            <el-card shadow="hover" class="quick-card">
              <div class="quick-icon">
                <el-icon><Stethoscope /></el-icon>
              </div>
              <div class="quick-title">诊断管理</div>
              <div class="quick-desc">查看和创建诊断记录</div>
            </el-card>
          </router-link>
        </el-col>
        
        <el-col :xs="12" :sm="8" :md="6">
          <router-link to="/bloodtests/list" class="quick-link">
            <el-card shadow="hover" class="quick-card">
              <div class="quick-icon">
                <el-icon><FirstAidKit /></el-icon>
              </div>
              <div class="quick-title">血常规检查</div>
              <div class="quick-desc">管理血常规检查记录</div>
            </el-card>
          </router-link>
        </el-col>
      </el-row>
    </div>
    
    <!-- 最近活动区域 -->
    <h3 class="section-title">最近活动</h3>
    <el-card class="activity-card">
      <el-timeline v-loading="activitiesLoading">
        <el-timeline-item
          v-for="(activity, index) in activities"
          :key="index"
          :timestamp="formatDateTime(activity.logTime)"
          :icon="getActivityIcon(activity.actionType)"
          :type="getActivityType(activity.logType)"
        >
          <div class="timeline-content">
            <div class="timeline-title">{{ getActivityTitle(activity) }}</div>
            <div class="timeline-desc">{{ activity.actionDesc }}</div>
          </div>
        </el-timeline-item>
        <el-timeline-item v-if="activities.length === 0 && !activitiesLoading" timestamp="">
          <div class="timeline-content">
            <div class="timeline-title">暂无活动记录</div>
          </div>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import { ref, defineComponent, h, onMounted } from 'vue'
import { 
  User, 
  ChatDotRound,
  Document,
  EditPen,
  View,
  Plus
} from '@element-plus/icons-vue'
import { getUserLogs } from '../../api/log'
import { getDoctorDashboardStatistics } from '../../api/statistics'
import { getUserInfo } from '../../utils/auth'
import { formatDateTime } from '../../utils/format'

// 图标定义
const Stethoscope = defineComponent({
  name: 'Stethoscope',
  setup() {
    return () => h('svg', {
      viewBox: '0 0 24 24',
      width: '1em',
      height: '1em'
    }, [
      h('path', {
        fill: 'currentColor',
        d: 'M19 8V6h-2v2h2M3 3h18v18H3V3m11 3c-1.66 0-3 1.34-3 3c0 1.66 1.34 3 3 3s3-1.34 3-3c0-1.66-1.34-3-3-3m-7 3c0-1.66-1.34-3-3-3V8c1.66 0 3-1.34 3-3h-2c0 1.66-1.34 3-3 3v2c1.66 0 3 1.34 3 3h2c0-1.66 1.34-3 3-3v-2c-1.66 0-3 1.34-3 3H7Z'
      })
    ])
  }
})

const FirstAidKit = defineComponent({
  name: 'FirstAidKit',
  setup() {
    return () => h('svg', {
      viewBox: '0 0 24 24',
      width: '1em',
      height: '1em'
    }, [
      h('path', {
        fill: 'currentColor',
        d: 'M20 6h-3V4c0-1.1-.9-2-2-2h-6c-1.1 0-2 .9-2 2v2H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2zM9 4h6v2H9V4zm11 16H4V8h16v12z'
      }),
      h('path', {
        fill: 'currentColor',
        d: 'M13 10h-2v3H8v2h3v3h2v-3h3v-2h-3z'
      })
    ])
  }
})

// 最近活动数据
const activities = ref([])
const activitiesLoading = ref(false)

// 控制台统计数据
const statistics = ref({
  patientCount: 0,
  patientGrowthRate: '0.0',
  diagnosisCount: 0,
  diagnosisGrowthRate: '0.0',
  aiCallCount: 0,
  aiCallGrowthRate: '0.0'
})

// 获取当前用户的最近活动
const fetchUserActivities = () => {
  const userInfo = getUserInfo()
  if (!userInfo || !userInfo.id) return
  
  activitiesLoading.value = true
  getUserLogs(userInfo.id, 5)
    .then(response => {
      if (response.data && response.data.code === 1) {
        activities.value = response.data.data || []
      } else {
        console.error('获取用户活动失败:', response.data?.msg)
        activities.value = []
      }
    })
    .catch(error => {
      console.error('获取用户活动出错:', error)
      activities.value = []
    })
    .finally(() => {
      activitiesLoading.value = false
    })
}

// 获取控制台统计数据
const fetchStatistics = () => {
  getDoctorDashboardStatistics()
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

// 根据活动类型获取图标
const getActivityIcon = (type) => {
  switch (type) {
    case 'PATIENT_CREATE':
    case 'PATIENT_UPDATE':
    case 'PATIENT_QUERY':
    case 'PATIENT_DELETE':
      return User
    case 'DIAGNOSIS_CREATE':
    case 'DIAGNOSIS_UPDATE':
    case 'DIAGNOSIS_QUERY':
      return Stethoscope
    case 'AI_DIAGNOSIS_CALL':
      return ChatDotRound
    default:
      return Document
  }
}

// 获取活动类型（颜色）
const getActivityType = (logType) => {
  switch (logType) {
    case 'ERROR':
      return 'danger'
    case 'AI_CALL':
      return 'info'
    case 'OPERATION':
    default:
      return 'primary'
  }
}

// 获取活动标题
const getActivityTitle = (activity) => {
  const actionTypes = {
    'PATIENT_CREATE': '创建了新患者',
    'PATIENT_UPDATE': '更新了患者信息',
    'PATIENT_QUERY': '查询了患者信息',
    'PATIENT_DELETE': '删除了患者信息',
    'DIAGNOSIS_CREATE': '创建了诊断记录',
    'DIAGNOSIS_UPDATE': '更新了诊断记录',
    'DIAGNOSIS_QUERY': '查询了诊断记录',
    'AI_DIAGNOSIS_CALL': '使用了AI诊断'
  }
  
  return actionTypes[activity.actionType] || activity.actionType
}

// 组件挂载时获取数据
onMounted(() => {
  fetchStatistics()
  fetchUserActivities()
})
</script>

<style scoped>
.dashboard-container {
  padding: 0;
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
}

.page-title {
  margin-bottom: 24px;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  flex-shrink: 0;
}

.section-title {
  margin: 30px 0 20px;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

/* 快捷入口样式 */
.quick-access {
  margin-bottom: 24px;
}

.quick-link {
  text-decoration: none;
  display: block;
  margin-bottom: 20px;
}

.quick-card {
  height: 100%;
  border-radius: 12px;
  text-align: center;
  padding: 20px;
  transition: all 0.3s ease;
  border: 1px solid rgba(106, 152, 233, 0.05);
}

.quick-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.08);
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
.activity-card {
  margin-bottom: 20px;
}

.timeline-content {
  padding: 8px 0;
}

.timeline-title {
  font-weight: bold;
  margin-bottom: 5px;
}

.timeline-desc {
  color: #606266;
  font-size: 14px;
}

/* 统计卡片样式 */
.stat-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;
  flex-shrink: 0;
}

.stat-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
  border: 1px solid rgba(106, 152, 233, 0.05);
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.08);
}

.stat-content {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  border-radius: 12px;
  margin-right: 16px;
  font-size: 24px;
  color: #fff;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.stat-icon.patients {
  background: linear-gradient(135deg, #3f8cda, #77cdf3);
}

.stat-icon.diagnoses {
  background: linear-gradient(135deg, #42b983, #67C23A);
}

.stat-icon.ai {
  background: linear-gradient(135deg, #764ba2, #667eea);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.stat-footer {
  display: flex;
  align-items: center;
  font-size: 12px;
}

.trend-up {
  color: #67C23A;
  margin-right: 4px;
}

.trend-down {
  color: #F56C6C;
  margin-right: 4px;
}

.trend-text {
  color: #909399;
}

/* 响应式布局 */
@media (max-width: 1200px) {
  .stat-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .stat-cards {
    grid-template-columns: 1fr;
  }
}
</style> 