<template>
  <div class="knowledge-list-container">
    <div class="knowledge-list">
      <div class="header-section">
        <h2 class="page-title">医学知识库</h2>
        <div class="search-bar">
          <el-input 
            v-model="searchKeyword" 
            placeholder="请输入医学知识关键字" 
            class="search-input"
            prefix-icon="Search"
            clearable
            @keyup.enter="handleSearch" 
          />
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-radio-group v-model="entryType" size="small" class="filter-group" @change="handleSearch">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button label="DRUG">药品</el-radio-button>
            <el-radio-button label="DISEASE">疾病</el-radio-button>
          </el-radio-group>
        </div>
      </div>

      <div class="table-container">
        <el-table 
          :data="list" 
          v-loading="loading" 
          class="knowledge-table"
          :header-cell-style="headerStyle" 
          :cell-style="cellStyle"
          :row-style="rowStyle"
          border
        >
          <el-table-column prop="title" label="标题" min-width="280" />
          <el-table-column label="类型" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.entryType === 'DISEASE' ? 'danger' : 'success'" size="small">
                {{ scope.row.entryType === 'DISEASE' ? '疾病' : '药品' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="category" label="分类" width="180" />
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="scope">
              <el-button type="primary" link @click="goDetail(scope.row.id)">查看详情</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-bar">
          <el-pagination
            background
            layout="total, prev, pager, next, jumper"
            :total="total"
            :page-size="pageSize"
            :current-page="page"
            @current-change="handlePageChange"
          />
        </div>

        <el-empty v-if="!loading && list.length === 0" description="暂无数据" style="margin-top: 40px;" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getKnowledgeList } from '../../api/knowledge'
import { Search } from '@element-plus/icons-vue'

const router = useRouter()
const list = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')
const entryType = ref('') // 默认全部
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
    if (res.data && res.data.data) {
      list.value = res.data.data.records || []
      total.value = res.data.data.total || 0
    }
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

const goDetail = (id) => {
  router.push(`/knowledge/detail/${id}`)
}

const headerStyle = () => ({
  background: '#f5f7fa',
  color: '#333',
  fontWeight: 600,
  fontSize: '15px',
  height: '50px',
  padding: '12px'
})

const cellStyle = () => ({
  fontSize: '14px',
  padding: '10px 12px'
})

const rowStyle = () => ({
  height: '55px'
})

onMounted(fetchList)
</script>

<style scoped>
.knowledge-list-container {
  width: 100%;
  box-sizing: border-box;
  padding: 0;
  margin: 0;
  background: #fff;
  min-height: calc(100vh - 120px);
}

.knowledge-list {
  width: 95%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 10px 0 30px 0;
}

.header-section {
  margin-bottom: 16px;
}

.page-title {
  font-size: 22px;
  margin: 0 0 16px 0;
  color: #333;
  font-weight: 600;
}

.search-bar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.search-input {
  width: 320px;
}

.filter-group {
  margin-left: 8px;
}

.table-container {
  background: white;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.03);
}

.knowledge-table {
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
}

.pagination-bar {
  margin: 25px 0 20px;
  display: flex;
  justify-content: center;
}
</style> 