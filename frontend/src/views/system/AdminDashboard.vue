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
            <div class="stat-value">24</div>
            <div class="stat-label">系统用户</div>
          </div>
        </div>
        <div class="stat-footer">
          <span class="trend-up">↑ 2</span>
          <span class="trend-text">本月新增</span>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon roles">
            <el-icon><UserFilled /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">3</div>
            <div class="stat-label">系统角色</div>
          </div>
        </div>
        <div class="stat-footer">
          <span class="trend-text">固定角色</span>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon knowledge">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">156</div>
            <div class="stat-label">知识库条目</div>
          </div>
        </div>
        <div class="stat-footer">
          <span class="trend-up">↑ 12</span>
          <span class="trend-text">本月新增</span>
        </div>
      </el-card>
      
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon logs">
            <el-icon><List /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">1,283</div>
            <div class="stat-label">系统日志</div>
          </div>
        </div>
        <div class="stat-footer">
          <span class="trend-up">↑ 128</span>
          <span class="trend-text">今日新增</span>
        </div>
      </el-card>
    </div>
    
    <!-- 快捷入口区域 -->
    <h3 class="section-title">快捷入口</h3>
    <div class="quick-access">
      <el-row :gutter="20">
        <el-col :xs="12" :sm="8" :md="6">
          <router-link to="/system/users" class="quick-link">
            <el-card shadow="hover" class="quick-card">
              <div class="quick-icon">
                <el-icon><User /></el-icon>
              </div>
              <div class="quick-title">用户管理</div>
              <div class="quick-desc">管理系统用户账号</div>
            </el-card>
          </router-link>
        </el-col>
        
        <el-col :xs="12" :sm="8" :md="6">
          <router-link to="/system/roles" class="quick-link">
            <el-card shadow="hover" class="quick-card">
              <div class="quick-icon">
                <el-icon><UserFilled /></el-icon>
              </div>
              <div class="quick-title">角色管理</div>
              <div class="quick-desc">管理系统角色权限</div>
            </el-card>
          </router-link>
        </el-col>
        
        <el-col :xs="12" :sm="8" :md="6">
          <router-link to="/knowledge/manage" class="quick-link">
            <el-card shadow="hover" class="quick-card">
              <div class="quick-icon">
                <el-icon><Document /></el-icon>
              </div>
              <div class="quick-title">知识库管理</div>
              <div class="quick-desc">管理医学知识库内容</div>
            </el-card>
          </router-link>
        </el-col>
        
        <el-col :xs="12" :sm="8" :md="6">
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
    
    <!-- 系统状态区域 -->
    <h3 class="section-title">系统状态</h3>
    <el-card class="system-status">
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="status-item">
            <div class="status-label">CPU 使用率</div>
            <el-progress :percentage="32" :stroke-width="15" />
          </div>
          <div class="status-item">
            <div class="status-label">内存使用率</div>
            <el-progress :percentage="45" :stroke-width="15" />
          </div>
          <div class="status-item">
            <div class="status-label">磁盘使用率</div>
            <el-progress :percentage="68" :stroke-width="15" />
          </div>
        </el-col>
        <el-col :span="12">
          <div class="status-info">
            <div class="info-item">
              <div class="info-label">系统版本</div>
              <div class="info-value">MediWise v1.0.5</div>
            </div>
            <div class="info-item">
              <div class="info-label">运行时间</div>
              <div class="info-value">32天16小时</div>
            </div>
            <div class="info-item">
              <div class="info-label">最近更新</div>
              <div class="info-value">2023-05-15 14:30:22</div>
            </div>
            <div class="info-item">
              <div class="info-label">系统状态</div>
              <div class="info-value">
                <el-tag type="success" size="small">运行正常</el-tag>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>
    
    <!-- 最近活动区域 -->
    <h3 class="section-title">最近活动</h3>
    <el-card class="recent-activity">
      <el-table :data="activities" style="width: 100%">
        <el-table-column label="时间" width="180">
          <template #default="scope">
            <span>{{ scope.row.time }}</span>
          </template>
        </el-table-column>
        <el-table-column label="用户" width="120">
          <template #default="scope">
            <span>{{ scope.row.user }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <span>{{ scope.row.action }}</span>
          </template>
        </el-table-column>
        <el-table-column label="IP地址" width="140" align="right">
          <template #default="scope">
            <span>{{ scope.row.ip }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { 
  User, 
  UserFilled,
  Document,
  List
} from '@element-plus/icons-vue'

// 最近活动数据
const activities = [
  {
    time: '2023-05-15 14:30:22',
    user: '管理员',
    action: '添加了新用户: 张医生',
    ip: '192.168.1.100'
  },
  {
    time: '2023-05-15 13:15:45',
    user: '管理员',
    action: '更新了系统配置',
    ip: '192.168.1.100'
  },
  {
    time: '2023-05-14 16:42:10',
    user: '管理员',
    action: '添加了新的知识库条目: 高血压诊断标准',
    ip: '192.168.1.100'
  },
  {
    time: '2023-05-14 10:23:15',
    user: '管理员',
    action: '修改了用户权限: 李医生',
    ip: '192.168.1.100'
  },
  {
    time: '2023-05-13 09:12:33',
    user: '系统',
    action: '系统自动备份完成',
    ip: '127.0.0.1'
  }
]
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

.stat-icon.knowledge {
  background: linear-gradient(135deg, #E6A23C 0%, #c78722 100%);
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

/* 系统状态样式 */
.system-status {
  margin-bottom: 30px;
}

.status-item {
  margin-bottom: 20px;
}

.status-label {
  margin-bottom: 8px;
  font-size: 14px;
  color: #606266;
}

.status-info {
  padding-left: 20px;
  border-left: 1px solid #EBEEF5;
  height: 100%;
}

.info-item {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.info-label {
  width: 100px;
  font-size: 14px;
  color: #909399;
}

.info-value {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

/* 最近活动样式 */
.recent-activity {
  margin-bottom: 30px;
}

@media (max-width: 768px) {
  .stat-cards {
    grid-template-columns: 1fr;
  }
  
  .status-info {
    padding-left: 0;
    border-left: none;
    margin-top: 20px;
  }
}
</style> 