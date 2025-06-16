<template>
  <div class="knowledge-manage">
    <div class="page-header">
      <h2>知识库管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="goToAdd">
          <el-icon><Plus /></el-icon>添加知识
        </el-button>
      </div>
    </div>

    <!-- 搜索和过滤工具栏 -->
    <div class="toolbar">
      <el-input 
        v-model="searchKeyword" 
        placeholder="请输入关键字搜索" 
        clearable 
        class="search-input"
        @keyup.enter="handleSearch"
        prefix-icon="Search"
      />
      <el-button type="primary" @click="handleSearch">搜索</el-button>
      <el-radio-group v-model="entryType" size="default" @change="handleSearch" class="type-filter">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button label="DISEASE">疾病</el-radio-button>
        <el-radio-button label="DRUG">药品</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 表格数据 -->
    <el-table 
      :data="list" 
      v-loading="loading"
      border
      style="width: 100%"
      row-key="id"
      :header-cell-style="headerStyle" 
    >
      <el-table-column prop="title" label="标题" min-width="250" show-overflow-tooltip/>
      <el-table-column label="类型" width="100" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.entryType === 'DISEASE' ? 'danger' : 'success'" size="small">
            {{ scope.row.entryType === 'DISEASE' ? '疾病' : '药品' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="category" label="分类" width="150" show-overflow-tooltip/>
      <el-table-column label="状态" width="100" align="center">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">
            {{ scope.row.status === 1 ? '已启用' : '已禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间" width="180" />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="scope">
          <el-button type="primary" link size="small" @click="goToDetail(scope.row.id)">查看</el-button>
          <el-button type="primary" link size="small" @click="goToEdit(scope.row.id)">编辑</el-button>
          <el-button 
            :type="scope.row.status === 1 ? 'warning' : 'success'" 
            link 
            size="small" 
            @click="handleToggleStatus(scope.row)"
          >
            {{ scope.row.status === 1 ? '禁用' : '启用' }}
          </el-button>
          <el-popconfirm
            title="确定要删除该知识条目吗？"
            width="220"
            confirm-button-text="确定"
            cancel-button-text="取消"
            @confirm="handleDelete(scope.row.id)"
          >
            <template #reference>
              <el-button type="danger" link size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        background
        layout="total, prev, pager, next, jumper"
        :total="total"
        :current-page="page"
        :page-size="pageSize"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getKnowledgeList, deleteKnowledge, updateKnowledge } from '../../api/knowledge'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'

const router = useRouter()
const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const entryType = ref('')
const loading = ref(false)

const fetchList = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      size: pageSize.value,
      keyword: searchKeyword.value || undefined,
      entryType: entryType.value || undefined
    }
    const res = await getKnowledgeList(params)
    if (res.data && res.data.code === 1) {
      list.value = res.data.data.records || []
      total.value = res.data.data.total || 0
    } else {
      ElMessage.error(res.data?.msg || '获取知识列表失败')
    }
  } catch (error) {
    console.error('获取知识列表出错:', error)
    ElMessage.error('获取知识列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  fetchList()
}

const handlePageChange = (val) => {
  page.value = val
  fetchList()
}

const handleDelete = async (id) => {
  try {
    const res = await deleteKnowledge(id)
    if (res.data && res.data.code === 1) {
      ElMessage.success('删除成功')
      fetchList() // 重新加载列表
    } else {
      ElMessage.error(res.data?.msg || '删除失败')
    }
  } catch (error) {
    console.error('删除知识条目出错:', error)
    ElMessage.error('删除失败')
  }
}

const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1;
  const statusText = newStatus === 1 ? '启用' : '禁用';
  
  try {
    const updateData = {
      title: row.title,
      status: newStatus
    };
    
    const res = await updateKnowledge(row.id, updateData);
    
    if (res.data && res.data.code === 1) {
      ElMessage.success(`${statusText}成功`);
      // 更新本地数据，避免重新加载整个列表
      row.status = newStatus;
    } else {
      ElMessage.error(res.data?.msg || `${statusText}失败`);
    }
  } catch (error) {
    console.error(`${statusText}知识条目失败:`, error);
    ElMessage.error(`${statusText}失败`);
  }
}

const goToDetail = (id) => {
  router.push(`/knowledge/detail/${id}`)
}

const goToEdit = (id) => {
  router.push(`/knowledge/edit/${id}`)
}

const goToAdd = () => {
  router.push('/knowledge/add')
}

const headerStyle = () => ({
  background: '#f5f7fa',
  color: '#333',
  fontWeight: 600,
  fontSize: '14px',
  padding: '12px'
})

onMounted(fetchList)
</script>

<style scoped>
.knowledge-manage {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
}

.toolbar {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  gap: 15px;
  flex-wrap: wrap;
}

.search-input {
  width: 300px;
}

.type-filter {
  margin-left: auto;
}

.pagination-container {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}
</style> 