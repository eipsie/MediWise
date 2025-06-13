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
            <div class="stat-value">2,547</div>
            <div class="stat-label">患者总数</div>
          </div>
        </div>
        <div class="stat-footer">
          <span class="trend-up">↑ 12.5%</span>
          <span class="trend-text">较上月</span>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon diagnoses">
            <el-icon><Stethoscope /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">1,283</div>
            <div class="stat-label">诊断记录</div>
          </div>
        </div>
        <div class="stat-footer">
          <span class="trend-up">↑ 8.3%</span>
          <span class="trend-text">较上月</span>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon tests">
            <el-icon><FirstAidKit /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">3,842</div>
            <div class="stat-label">检查记录</div>
          </div>
        </div>
        <div class="stat-footer">
          <span class="trend-up">↑ 15.2%</span>
          <span class="trend-text">较上月</span>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon ai">
            <el-icon><ChatDotRound /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">967</div>
            <div class="stat-label">AI问诊次数</div>
          </div>
        </div>
        <div class="stat-footer">
          <span class="trend-up">↑ 23.7%</span>
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
    
    <!-- 图表区域 -->
    <div class="chart-container">
      <el-row :gutter="20">
        <el-col :span="16">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>患者就诊趋势</span>
                <el-radio-group v-model="timeRange" size="small">
                  <el-radio-button label="week">本周</el-radio-button>
                  <el-radio-button label="month">本月</el-radio-button>
                  <el-radio-button label="year">全年</el-radio-button>
                </el-radio-group>
              </div>
            </template>
            <div class="chart-placeholder">
              <div class="chart-mock">
                <div class="chart-bar" v-for="(height, index) in chartData" :key="index" :style="{ height: height + '%' }"></div>
              </div>
              <div class="chart-labels">
                <span v-for="(label, index) in chartLabels" :key="index">{{ label }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="8">
          <el-card class="chart-card">
            <template #header>
              <div class="card-header">
                <span>诊断分布</span>
              </div>
            </template>
            <div class="pie-chart-placeholder">
              <div class="pie-chart">
                <div class="pie-segment" style="--percentage: 45%; --color: #3f8cda;"></div>
                <div class="pie-segment" style="--percentage: 30%; --color: #67C23A;"></div>
                <div class="pie-segment" style="--percentage: 15%; --color: #E6A23C;"></div>
                <div class="pie-segment" style="--percentage: 10%; --color: #F56C6C;"></div>
              </div>
              <div class="pie-legend">
                <div class="legend-item">
                  <span class="legend-color" style="background-color: #3f8cda;"></span>
                  <span class="legend-text">呼吸系统 (45%)</span>
                </div>
                <div class="legend-item">
                  <span class="legend-color" style="background-color: #67C23A;"></span>
                  <span class="legend-text">消化系统 (30%)</span>
                </div>
                <div class="legend-item">
                  <span class="legend-color" style="background-color: #E6A23C;"></span>
                  <span class="legend-text">心血管系统 (15%)</span>
                </div>
                <div class="legend-item">
                  <span class="legend-color" style="background-color: #F56C6C;"></span>
                  <span class="legend-text">其他 (10%)</span>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    
    <!-- 最近活动区域 -->
    <el-card class="recent-activity">
      <template #header>
        <div class="card-header">
          <span>最近活动</span>
          <el-button type="text">查看全部</el-button>
        </div>
      </template>
      <el-table :data="activities" style="width: 100%" :show-header="false">
        <el-table-column width="60">
          <template #default="scope">
            <el-avatar :size="40" :icon="getActivityIcon(scope.row.type)"></el-avatar>
          </template>
        </el-table-column>
        <el-table-column>
          <template #default="scope">
            <div class="activity-content">
              <div class="activity-title">{{ scope.row.title }}</div>
              <div class="activity-desc">{{ scope.row.description }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column width="150" align="right">
          <template #default="scope">
            <span class="activity-time">{{ scope.row.time }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, defineComponent, h } from 'vue'
import { 
  User, 
  ChatDotRound,
  Document,
  EditPen,
  View,
  Plus
} from '@element-plus/icons-vue'

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

// 时间范围选择
const timeRange = ref('month')

// 模拟图表数据
const chartData = [30, 45, 25, 60, 75, 40, 50]
const chartLabels = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']

// 最近活动数据
const activities = [
  {
    type: 'patient',
    title: '新增患者',
    description: '张医生添加了新患者：李明，男，45岁',
    time: '10分钟前'
  },
  {
    type: 'diagnosis',
    title: '诊断记录更新',
    description: '王医生更新了患者陈静的诊断记录',
    time: '30分钟前'
  },
  {
    type: 'test',
    title: '新增检查报告',
    description: '系统导入了15份新的血常规检查报告',
    time: '1小时前'
  },
  {
    type: 'view',
    title: '查看病历',
    description: '赵医生查看了患者王强的历史病历',
    time: '2小时前'
  },
  {
    type: 'ai',
    title: 'AI问诊',
    description: '完成了一次AI辅助诊断，诊断结果：感冒',
    time: '3小时前'
  }
]

// 根据活动类型获取图标
const getActivityIcon = (type) => {
  switch (type) {
    case 'patient':
      return User
    case 'diagnosis':
      return Stethoscope
    case 'test':
      return Document
    case 'view':
      return View
    case 'ai':
      return ChatDotRound
    default:
      return Plus
  }
}
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

/* 统计卡片样式 */
.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
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

.stat-icon.tests {
  background: linear-gradient(135deg, #e2725b, #E6A23C);
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

/* 图表卡片样式 */
.chart-container {
  margin-bottom: 24px;
  flex-shrink: 0;
}

.chart-card {
  border-radius: 12px;
  height: 360px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
  border: 1px solid rgba(106, 152, 233, 0.05);
}

.chart-card:hover {
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-placeholder {
  height: 280px;
  display: flex;
  flex-direction: column;
}

.chart-mock {
  flex: 1;
  display: flex;
  align-items: flex-end;
  justify-content: space-around;
  padding: 0 10px;
  margin-bottom: 10px;
}

.chart-bar {
  width: 30px;
  background: linear-gradient(to top, #3f8cda, #77cdf3);
  border-radius: 6px 6px 0 0;
  transition: height 0.5s;
}

.chart-labels {
  display: flex;
  justify-content: space-around;
  color: #909399;
  font-size: 12px;
}

/* 饼图样式 */
.pie-chart-placeholder {
  height: 280px;
  display: flex;
  align-items: center;
}

.pie-chart {
  position: relative;
  width: 150px;
  height: 150px;
  border-radius: 50%;
  background-color: #f5f7fa;
  overflow: hidden;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
}

.pie-segment {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  transform-origin: 50% 50%;
  background-color: var(--color);
}

.pie-segment:nth-child(1) {
  clip-path: polygon(50% 50%, 50% 0%, 100% 0%, 100% 100%, 50% 100%);
  transform: rotate(0deg);
}

.pie-segment:nth-child(2) {
  clip-path: polygon(50% 50%, 50% 0%, 100% 0%, 100% 50%);
  transform: rotate(162deg);
}

.pie-segment:nth-child(3) {
  clip-path: polygon(50% 50%, 50% 0%, 75% 0%);
  transform: rotate(270deg);
}

.pie-segment:nth-child(4) {
  clip-path: polygon(50% 50%, 50% 0%, 60% 0%);
  transform: rotate(324deg);
}

.pie-legend {
  flex: 1;
  padding-left: 24px;
}

.legend-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
  margin-right: 8px;
}

.legend-text {
  font-size: 14px;
  color: #606266;
}

/* 最近活动样式 */
.recent-activity {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
  transition: all 0.3s ease;
  border: 1px solid rgba(106, 152, 233, 0.05);
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 200px;
}

.recent-activity:hover {
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.08);
}

.activity-content {
  padding: 8px 0;
}

.activity-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.activity-desc {
  font-size: 13px;
  color: #909399;
}

.activity-time {
  font-size: 12px;
  color: #c0c4cc;
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

/* 快捷入口样式 */
.section-title {
  margin: 30px 0 20px;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

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
</style> 