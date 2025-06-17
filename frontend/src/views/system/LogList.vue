<template>
  <div class="log-list">
    <div class="page-header">
      <h2>系统日志</h2>
      <div class="header-actions">
        <el-button type="primary" @click="refreshLogs">刷新</el-button>
      </div>
    </div>

    <!-- 搜索筛选栏 -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="日志类型">
          <el-select v-model="queryParams.logType" placeholder="全部" clearable>
            <el-option label="操作日志" value="OPERATION" />
            <el-option label="错误日志" value="ERROR" />
            <el-option label="AI调用日志" value="AI_CALL" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select v-model="queryParams.actionType" placeholder="全部" clearable>
            <el-option v-for="(label, key) in actionTypes" :key="key" :label="label" :value="key" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 日志表格 -->
    <el-card class="log-table-card">
      <el-table
        v-loading="loading"
        :data="logList"
        stripe
        border
        style="width: 100%"
      >
        <el-table-column prop="logTime" label="操作时间" width="180" sortable>
          <template #default="scope">
            {{ formatDateTime(scope.row.logTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="username" label="操作用户" width="120" />
        <el-table-column prop="logType" label="日志类型" width="100">
          <template #default="scope">
            <el-tag :type="getLogTypeTag(scope.row.logType)">
              {{ getLogTypeLabel(scope.row.logType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="actionType" label="操作类型" width="150" />
        <el-table-column prop="actionDesc" label="操作描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="ipAddress" label="IP地址" width="140" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusTag(scope.row.status)">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="showLogDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          v-model:page-size="queryParams.size"
          v-model:current-page="queryParams.page"
        />
      </div>
    </el-card>

    <!-- 日志详情对话框 -->
    <el-dialog title="日志详情" v-model="dialogVisible" width="700px">
      <div v-if="currentLog" class="log-detail">
        <div class="detail-item">
          <span class="detail-label">日志ID:</span>
          <span class="detail-value">{{ currentLog.id }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">日志类型:</span>
          <span class="detail-value">{{ getLogTypeLabel(currentLog.logType) }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">操作用户:</span>
          <span class="detail-value">{{ currentLog.username }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">操作类型:</span>
          <span class="detail-value">{{ currentLog.actionType }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">操作描述:</span>
          <span class="detail-value">{{ currentLog.actionDesc }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">操作时间:</span>
          <span class="detail-value">{{ formatDateTime(currentLog.logTime) }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">IP地址:</span>
          <span class="detail-value">{{ currentLog.ipAddress }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">请求URL:</span>
          <span class="detail-value">{{ currentLog.requestUrl }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">请求方法:</span>
          <span class="detail-value">{{ currentLog.requestMethod }}</span>
        </div>
        <div class="detail-item" v-if="currentLog.executionTime">
          <span class="detail-label">执行时间:</span>
          <span class="detail-value">{{ currentLog.executionTime }}ms</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">状态:</span>
          <span class="detail-value">{{ getStatusLabel(currentLog.status) }}</span>
        </div>
        <div v-if="currentLog.errorMessage" class="detail-item">
          <span class="detail-label">错误信息:</span>
          <span class="detail-value error-message">{{ currentLog.errorMessage }}</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { getLogsByPage } from '../../api/log'
import { formatDateTime } from '../../utils/format'
import { ElMessage } from 'element-plus'

// 系统日志页面

// 操作类型常量
const actionTypes = {
  'USER_LOGIN': '用户登录',
  'USER_LOGOUT': '用户退出',
  'USER_QUERY': '用户查询',
  'USER_CREATE': '用户创建',
  'USER_UPDATE': '用户更新',
  'USER_DELETE': '用户删除',
  'USER_STATUS_CHANGE': '用户状态变更',
  'PASSWORD_RESET': '密码重置',
  'PATIENT_QUERY': '患者查询',
  'PATIENT_CREATE': '患者创建',
  'PATIENT_UPDATE': '患者更新',
  'PATIENT_DELETE': '患者删除',
  'DIAGNOSIS_CREATE': '诊断创建',
  'DIAGNOSIS_UPDATE': '诊断更新',
  'DIAGNOSIS_QUERY': '诊断查询',
  'SYSTEM_CONFIG_UPDATE': '系统设置更新',
  'LOG_QUERY': '日志查询',
  'AI_DIAGNOSIS_CALL': 'AI诊断调用'
}

// 状态
const loading = ref(false)
const logList = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const currentLog = ref(null)
const dateRange = ref([])

// 查询参数
const queryParams = reactive({
  page: 1,
  size: 10,
  logType: '',
  actionType: '',
  username: '',
  startDate: '',
  endDate: ''
})

// 监听日期范围变化
watch(dateRange, (newVal) => {
  if (newVal && newVal.length === 2) {
    queryParams.startDate = newVal[0]
    queryParams.endDate = newVal[1]
  } else {
    queryParams.startDate = ''
    queryParams.endDate = ''
  }
})

// 监听分页参数变化
watch(
  () => [queryParams.page, queryParams.size],
  () => {
    getLogList()
  }
)

// 日志类型标签
const getLogTypeLabel = (type) => {
  const types = {
    'OPERATION': '操作日志',
    'ERROR': '错误日志',
    'AI_CALL': 'AI调用'
  }
  return types[type] || type
}

// 日志类型标签样式
const getLogTypeTag = (type) => {
  const tags = {
    'OPERATION': 'primary',
    'ERROR': 'danger',
    'AI_CALL': 'info'
  }
  return tags[type] || 'info'
}

// 状态标签
const getStatusLabel = (status) => {
  const statuses = {
    'SUCCESS': '成功',
    'FAILURE': '失败',
    'ERROR': '错误'
  }
  return statuses[status] || status
}

// 状态标签样式
const getStatusTag = (status) => {
  const tags = {
    'SUCCESS': 'success',
    'FAILURE': 'warning',
    'ERROR': 'danger'
  }
  return tags[status] || ''
}

// 查询日志列表
const getLogList = () => {
  loading.value = true
  getLogsByPage(queryParams)
    .then(response => {
      if (response.data && response.data.code === 1) {
        logList.value = response.data.data.records || []
        total.value = response.data.data.total || 0
      } else {
        ElMessage.error(response.data?.msg || '获取日志失败')
        logList.value = []
        total.value = 0
      }
    })
    .catch(error => {
      console.error('获取日志列表出错:', error)
      logList.value = []
      total.value = 0
    })
    .finally(() => {
      loading.value = false
    })
}

// 查询按钮点击
const handleQuery = () => {
  queryParams.page = 1
  getLogList()
}

// 重置查询条件
const resetQuery = () => {
  queryParams.logType = ''
  queryParams.actionType = ''
  queryParams.username = ''
  dateRange.value = []
  queryParams.startDate = ''
  queryParams.endDate = ''
  handleQuery()
}

// 刷新日志
const refreshLogs = () => {
  getLogList()
}

// 显示日志详情
const showLogDetail = (log) => {
  currentLog.value = log
  dialogVisible.value = true
}

// 组件挂载时加载日志列表
onMounted(() => {
  getLogList()
})
</script>

<style scoped>
.log-list {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.filter-card {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
}

.log-table-card {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.log-detail {
  padding: 10px;
}

.detail-item {
  margin-bottom: 12px;
  display: flex;
}

.detail-label {
  min-width: 100px;
  font-weight: bold;
  color: #606266;
}

.detail-value {
  flex: 1;
  word-break: break-all;
}

.error-message {
  color: #F56C6C;
}
</style> 