<template>
  <div class="diagnosis-list">
    <div class="page-header">
      <h2>诊断记录</h2>
      <div class="header-actions">
        <el-button type="primary" @click="handleAddDiagnosis">
          <el-icon><Plus /></el-icon>新增诊断
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选工具条 -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="患者姓名/症状/诊断结果"
            clearable
            @keyup.enter="handleSearch"
            style="width: 220px;"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select 
            v-model="searchForm.status" 
            placeholder="全部状态" 
            clearable 
            style="width: 160px;"
            popper-class="diagnosis-status-dropdown"
          >
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 诊断记录表格 -->
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="diagnosisList"
        stripe
        style="width: 100%"
        border
        row-key="id"
      >
        <el-table-column prop="diagnosisTime" label="诊断时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.diagnosisTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="patientName" label="患者姓名" width="120" />
        <el-table-column prop="symptomsText" label="主要症状" min-width="200" show-overflow-tooltip>
          <template #default="scope">
            {{ scope.row.symptomsText ? (scope.row.symptomsText.substring(0, 50) + (scope.row.symptomsText.length > 50 ? '...' : '')) : '' }}
          </template>
        </el-table-column>
        <el-table-column prop="finalDiagnosis" label="诊断结果" min-width="150" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getDiagnosisStatusType(scope.row.status)" size="small">
              {{ getDiagnosisStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="doctorName" label="医生" width="100" />
        <el-table-column fixed="right" label="操作" width="150">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="viewDiagnosis(scope.row)">查看</el-button>
            <el-button 
              link 
              type="primary" 
              size="small" 
              @click="editDiagnosis(scope.row)" 
              v-if="scope.row.status !== 'COMPLETED'"
            >
              编辑
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 空数据提示 -->
      <el-empty 
        v-if="diagnosisList.length === 0 && !loading" 
        :description="isSearching ? '没有找到匹配的诊断记录' : '暂无诊断记录'"
      >
        <template #extra>
          <el-button type="primary" @click="handleAddDiagnosis">创建诊断</el-button>
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
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { 
  getDiagnosisList, 
  getDiagnosisStatusText, 
  getDiagnosisStatusType,
  getDiagnosisStatusList 
} from '../../api/diagnosis'

const router = useRouter()
const loading = ref(false)
const diagnosisList = ref([])

// 状态选项
const statusOptions = getDiagnosisStatusList()

// 搜索表单
const searchForm = reactive({
  keyword: '',
  status: ''
})

// 判断是否在搜索状态
const isSearching = computed(() => {
  return searchForm.keyword || searchForm.status;
})

// 分页参数
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 获取诊断记录列表
const fetchDiagnosisList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      keyword: searchForm.keyword || undefined,
      status: searchForm.status || undefined
    }
    
    console.log('诊断搜索参数:', params)
    
    const res = await getDiagnosisList(params)
    if (res.data && res.data.code === 1) {
      const pageData = res.data.data
      diagnosisList.value = pageData.records || []
      pagination.total = pageData.total || 0
      
      // 检查搜索结果
      console.log(`搜索到 ${pagination.total} 条记录`)
    } else {
      ElMessage.error(res.data?.message || '获取诊断记录失败')
    }
  } catch (error) {
    console.error('获取诊断记录出错:', error)
    ElMessage.error('获取诊断记录出错')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  fetchDiagnosisList()
}

// 重置搜索条件
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.status = ''
  handleSearch()
}

// 分页事件处理
const handleSizeChange = (val) => {
  pagination.size = val
  pagination.page = 1
  fetchDiagnosisList()
}

const handleCurrentChange = (val) => {
  pagination.page = val
  fetchDiagnosisList()
}

// 新增诊断
const handleAddDiagnosis = () => {
  router.push('/diagnoses/add')
}

// 查看诊断详情
const viewDiagnosis = (row) => {
  router.push(`/diagnoses/detail/${row.id}`)
}

// 编辑诊断
const editDiagnosis = (row) => {
  router.push(`/diagnoses/edit/${row.id}`)
}

// 格式化日期时间
const formatDateTime = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 初始化加载
onMounted(() => {
  fetchDiagnosisList()
})
</script>

<style scoped>
.diagnosis-list {
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
  gap: 10px;
}

.search-form .el-form-item {
  margin-bottom: 10px;
}

.table-card {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 