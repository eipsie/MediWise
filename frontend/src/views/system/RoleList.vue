<template>
  <div class="role-list">
    <PageHeader title="患者管理" />

    <el-card class="patient-table-card">
      <template #header>
        <div class="card-header">
          <div class="left">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索患者姓名/电话/编号"
              class="search-input"
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
            >
              <template #append>
                <el-button @click="handleSearch">
                  <el-icon><Search /></el-icon>
                </el-button>
              </template>
            </el-input>
          </div>
          <div class="right">
            <el-button type="primary" @click="addPatient">
              <el-icon><Plus /></el-icon> 添加患者
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="patientList" border v-loading="loading" style="width: 100%">
        <el-table-column prop="patientNo" label="患者编号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="genderText" label="性别" width="60" />
        <el-table-column prop="age" label="年龄" width="60">
          <template #default="scope">
            {{ scope.row.age }}岁
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="idCard" label="身份证号" min-width="180" />
        <el-table-column prop="creatorName" label="创建医生" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="160">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="viewPatientDetail(scope.row)">详情</el-button>
            <el-button link type="primary" size="small" @click="editPatient(scope.row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDeletePatient(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container" v-if="pagination.total > 0">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 没有数据时的提示 -->
    <el-empty v-if="patientList.length === 0 && !loading" description="暂无患者数据" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { getPatientList, deletePatient } from '../../api/patient'

const router = useRouter()
const loading = ref(false)
const searchKeyword = ref('')
const patientList = ref([])

// 分页参数
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 加载患者列表
const loadPatientList = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      keyword: searchKeyword.value
    }
    
    const response = await getPatientList(params)
    if (response.data && response.data.code === 1) {
      const data = response.data.data
      patientList.value = data.records || []
      pagination.total = data.total || 0
    } else {
      ElMessage.error(response.data?.message || '获取患者列表失败')
    }
  } catch (error) {
    console.error('获取患者列表出错:', error)
    ElMessage.error('获取患者列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索处理
const handleSearch = () => {
  pagination.current = 1
  loadPatientList()
}

// 分页大小变化
const handleSizeChange = (val) => {
  pagination.size = val
  loadPatientList()
}

// 页码变化
const handleCurrentChange = (val) => {
  pagination.current = val
  loadPatientList()
}

// 添加患者
const addPatient = () => {
  router.push('/patients/add')
}

// 查看患者详情
const viewPatientDetail = (row) => {
  router.push(`/patients/detail/${row.id}`)
}

// 编辑患者
const editPatient = (row) => {
  router.push(`/patients/edit/${row.id}`)
}

// 删除患者
const handleDeletePatient = (row) => {
  ElMessageBox.confirm(
    `确定要删除患者 ${row.name}(编号: ${row.patientNo}) 吗？删除后将无法恢复！`,
    '删除患者',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'danger'
    }
  ).then(async () => {
    try {
      const response = await deletePatient(row.id)
      if (response.data && response.data.code === 1) {
        ElMessage.success('删除患者成功')
        loadPatientList() // 重新加载列表
      } else {
        ElMessage.error(response.data?.message || '删除患者失败')
      }
    } catch (error) {
      console.error('删除患者出错:', error)
      ElMessage.error('删除患者失败')
    }
  }).catch(() => {})
}

// 格式化日期时间
const formatDateTime = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 初始化
onMounted(() => {
  loadPatientList()
})
</script>

<style scoped>
.role-list {
  padding: 20px;
}

.patient-table-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.left {
  flex: 1;
  display: flex;
  align-items: center;
}

.right {
  margin-left: 10px;
}

.search-input {
  max-width: 300px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 