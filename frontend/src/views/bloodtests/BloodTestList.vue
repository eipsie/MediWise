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
    <el-card class="filter-card">
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
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="bloodTestList"
        stripe
        style="width: 100%"
        border
        row-key="id"
      >
        <el-table-column prop="testDate" label="检测时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.testDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="patientName" label="患者姓名" width="120" />
        <el-table-column label="血常规结果" min-width="400">
          <template #default="scope">
            <div class="blood-test-values">
              <span class="blood-value" :class="getValueClass(scope.row.wbc, 'wbc')">
                WBC: {{ scope.row.wbc }}
              </span>
              <span class="blood-value" :class="getValueClass(scope.row.rbc, 'rbc')">
                RBC: {{ scope.row.rbc }}
              </span>
              <span class="blood-value" :class="getValueClass(scope.row.hgb, 'hgb')">
                HGB: {{ scope.row.hgb }}
              </span>
              <span class="blood-value" :class="getValueClass(scope.row.plt, 'plt')">
                PLT: {{ scope.row.plt }}
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="AI分析" width="120">
          <template #default="scope">
            <el-tag type="success" v-if="scope.row.aiAnalysisResult" size="small">已分析</el-tag>
            <el-tag type="info" v-else size="small">未分析</el-tag>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="180">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="viewBloodTest(scope.row)">查看</el-button>
            <el-popconfirm
              title="确定要删除此记录吗?"
              @confirm="handleDeleteBloodTest(scope.row.id)"
            >
              <template #reference>
                <el-button link type="danger" size="small">删除</el-button>
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
  getBloodTestList, 
  deleteBloodTest,
  analyzeBloodTest as analyzeAPI,
  getBloodTestValueStatus
} from '../../api/bloodtest'

const router = useRouter()
const loading = ref(false)
const analyzing = ref(false)
const bloodTestList = ref([])

// 搜索表单
const searchForm = reactive({
  keyword: ''
})

// 判断是否在搜索状态
const isSearching = computed(() => {
  return searchForm.keyword;
})

// 分页参数
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '';
  
  try {
    const date = new Date(dateTime);
    return date.toLocaleString('zh-CN', { 
      year: 'numeric', 
      month: '2-digit', 
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch (e) {
    console.error('格式化日期错误:', e);
    return dateTime;
  }
}

// 获取值的CSS类
const getValueClass = (value, field) => {
  const status = getBloodTestValueStatus(field, value);
  if (status === 'high') {
    return 'value-high';
  } else if (status === 'low') {
    return 'value-low';
  }
  return '';
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
    
    console.log('血常规检查搜索参数:', params)
    
    const res = await getBloodTestList(params)
    if (res.data && res.data.code === 1) {
      const pageData = res.data.data
      bloodTestList.value = pageData.records || []
      pagination.total = pageData.total || 0
      
      console.log(`搜索到 ${pagination.total} 条记录`)
    } else {
      ElMessage.error(res.data?.message || '获取血常规检查记录失败')
    }
  } catch (error) {
    console.error('获取血常规检查记录出错:', error)
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
    console.error('删除血常规检查记录出错:', error)
    ElMessage.error('删除出错')
  }
}

// AI分析
const analyzeBloodTest = async (row) => {
  if (analyzing.value) {
    ElMessage.warning('正在处理另一个分析请求，请稍后再试')
    return
  }
  
  analyzing.value = true
  try {
    const res = await analyzeAPI({
      bloodTestId: row.id,
      applyMedicalPrompt: true
    })
    
    if (res.data && res.data.code === 1) {
      ElMessage.success('AI分析请求已提交')
      // 刷新列表
      setTimeout(() => {
        fetchBloodTestList()
      }, 1000)
    } else {
      ElMessage.error(res.data?.message || 'AI分析请求失败')
    }
  } catch (error) {
    console.error('AI分析出错:', error)
    ElMessage.error('AI分析请求出错')
  } finally {
    analyzing.value = false
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

.table-card {
  margin-bottom: 20px;
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
  padding: 2px 8px;
  border-radius: 4px;
  background-color: #f5f7fa;
  font-family: monospace;
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
</style> 