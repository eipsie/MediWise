<template>
  <div class="blood-test-list">
    <div class="page-header">
      <h2>血常规检查记录</h2>
      <div class="header-actions">
        <el-button type="primary" @click="handleAddBloodTest">
          <el-icon><Plus /></el-icon>新增检查
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选工具条 -->
    <el-card class="filter-card" shadow="hover">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="患者姓名/编号"
            clearable
            @keyup.enter="handleSearch"
            style="width: 220px;"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 血常规检查记录表格 -->
    <el-card class="table-card" shadow="hover">
      <el-table
        v-loading="loading"
        :data="bloodTestList"
        stripe
        style="width: 100%"
        border
        row-key="id"
        @row-click="handleRowClick"
        :row-class-name="tableRowClassName"
      >
        <el-table-column prop="testDate" label="检测时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.testDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="patientName" label="患者姓名" width="120">
          <template #default="scope">
            <span class="patient-name">{{ scope.row.patientName }}</span>
          </template>
        </el-table-column>
        <el-table-column label="血常规结果" min-width="400">
          <template #default="scope">
            <div class="blood-test-values">
              <span v-for="(item, index) in bloodTestItems" :key="index"
                class="blood-value" 
                :class="getValueClass(scope.row[item.field], item.field)"
                v-tooltip="getTooltip(scope.row[item.field], item.field)"
              >
                {{ item.shortName }}: {{ scope.row[item.field] !== undefined && scope.row[item.field] !== null ? scope.row[item.field] : '--' }}
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="AI分析" width="120">
          <template #default="scope">
            <el-tag type="success" v-if="scope.row.aiAnalysisResult" size="small" effect="dark">已分析</el-tag>
            <el-tag type="info" v-else size="small">未分析</el-tag>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="180">
          <template #default="scope">
            <el-button link type="primary" size="small" @click.stop="viewBloodTest(scope.row)">
              <el-icon><View /></el-icon>查看
            </el-button>
            <el-popconfirm
              title="确定要删除此记录吗?"
              @confirm="handleDeleteBloodTest(scope.row.id)"
            >
              <template #reference>
                <el-button link type="danger" size="small" @click.stop>
                  <el-icon><Delete /></el-icon>删除
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 空数据提示 -->
      <el-empty 
        v-if="bloodTestList.length === 0 && !loading" 
        :description="isSearching ? '没有找到匹配的检查记录' : '暂无血常规检查记录'"
      >
        <template #extra>
          <el-button type="primary" @click="handleAddBloodTest">新增检查</el-button>
          <el-button v-if="isSearching" @click="resetSearch">清除筛选</el-button>
        </template>
      </el-empty>
      
      <!-- 分页控件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          background
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Search, View, Delete } from '@element-plus/icons-vue'
import { 
  getBloodTestList, 
  deleteBloodTest,
  getBloodTestValueStatus
} from '../../api/bloodtest'

const router = useRouter()
const loading = ref(false)
const bloodTestList = ref([])

// 血常规检查项目配置
const bloodTestItems = [
  { field: 'wbc', shortName: 'WBC', label: '白细胞计数', unit: '× 10^9/L', reference: '4.0-10.0' },
  { field: 'rbc', shortName: 'RBC', label: '红细胞计数', unit: '× 10^12/L', reference: '3.5-5.5' },
  { field: 'hgb', shortName: 'HGB', label: '血红蛋白', unit: 'g/L', reference: '110-160' },
  { field: 'plt', shortName: 'PLT', label: '血小板计数', unit: '× 10^9/L', reference: '100-300' }
]

// 搜索表单
const searchForm = reactive({
  keyword: ''
})

// 判断是否在搜索状态
const isSearching = computed(() => {
  return searchForm.keyword
})

// 分页参数
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  try {
    const date = new Date(dateTime)
    return date.toLocaleString('zh-CN', { 
      year: 'numeric', month: '2-digit', day: '2-digit',
      hour: '2-digit', minute: '2-digit'
    })
  } catch (e) {
    return dateTime
  }
}

// 获取值的CSS类
const getValueClass = (value, field) => {
  const status = getBloodTestValueStatus(field, value)
  return status === 'high' ? 'value-high' : status === 'low' ? 'value-low' : ''
}

// 获取表格行的类名
const tableRowClassName = ({ row }) => {
  return row.aiAnalysisResult ? 'has-analysis-row' : ''
}

// 获取提示信息
const getTooltip = (value, field) => {
  if (value === undefined || value === null) return ''
  
  const item = bloodTestItems.find(item => item.field === field)
  if (!item) return ''
  
  const status = getBloodTestValueStatus(field, value)
  let statusText = '正常'
  if (status === 'high') {
    statusText = '高于参考值'
  } else if (status === 'low') {
    statusText = '低于参考值'
  }
  
  return `${item.label} (${item.shortName}): ${value} ${item.unit}\n参考范围: ${item.reference} ${item.unit}\n状态: ${statusText}`
}

// 处理行点击
const handleRowClick = (row) => {
  viewBloodTest(row)
}

// 获取血常规检查记录列表
const fetchBloodTestList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      keyword: searchForm.keyword || undefined
    }
    
    const res = await getBloodTestList(params)
    if (res.data && res.data.code === 1) {
      const pageData = res.data.data
      bloodTestList.value = pageData.records || []
      pagination.total = pageData.total || 0
    } else {
      ElMessage.error(res.data?.message || '获取血常规检查记录失败')
    }
  } catch (error) {
    ElMessage.error('获取血常规检查记录出错')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  fetchBloodTestList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  pagination.page = 1
  fetchBloodTestList()
}

// 处理页码变化
const handleCurrentChange = (newPage) => {
  pagination.page = newPage
  fetchBloodTestList()
}

// 处理每页条数变化
const handleSizeChange = (newSize) => {
  pagination.size = newSize
  pagination.page = 1
  fetchBloodTestList()
}

// 新增血常规检查
const handleAddBloodTest = () => {
  router.push('/bloodtests/add')
}

// 查看血常规检查详情
const viewBloodTest = (row) => {
  router.push(`/bloodtests/detail/${row.id}`)
}

// 删除血常规检查记录
const handleDeleteBloodTest = async (id) => {
  try {
    const res = await deleteBloodTest(id)
    
    if (res.data && res.data.code === 1) {
      ElMessage.success('删除成功')
      // 刷新列表
      fetchBloodTestList()
    } else {
      ElMessage.error(res.data?.message || '删除失败')
    }
  } catch (error) {
    ElMessage.error('删除出错')
  }
}

// 页面初始化时加载数据
onMounted(() => {
  fetchBloodTestList()
})
</script>

<style scoped>
.blood-test-list {
  padding: 20px;
  height: 100%;
  overflow-y: auto;
  background-color: #f5f7fa;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  background-color: white;
  padding: 15px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.filter-card {
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
}

.table-card {
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.blood-test-values {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.blood-value {
  padding: 3px 8px;
  border-radius: 4px;
  background-color: #f5f7fa;
  font-family: monospace;
  transition: all 0.3s;
  cursor: default;
}

.blood-value:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.value-high {
  color: #f56c6c;
  font-weight: bold;
  background-color: #fef0f0;
}

.value-low {
  color: #e6a23c;
  font-weight: bold;
  background-color: #fdf6ec;
}

.patient-name {
  font-weight: 500;
}

:deep(.has-analysis-row) {
  background-color: #f0f9eb;
}

:deep(.el-table__row) {
  cursor: pointer;
  transition: all 0.2s;
}

:deep(.el-table__row:hover) {
  background-color: #ecf5ff !important;
}

@media (max-width: 768px) {
  .blood-test-list {
    padding: 10px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .header-actions {
    margin-top: 10px;
    width: 100%;
  }
  
  .search-form {
    flex-wrap: wrap;
  }
}
</style> 