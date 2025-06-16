<template>
  <div class="patient-list">
    <div class="page-header">
      <h2>患者管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="handleAddPatient">
          <el-icon><Plus /></el-icon>新增患者
        </el-button>
      </div>
    </div>

    <!-- 搜索表单 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="姓名">
          <el-input v-model="searchForm.name" placeholder="请输入患者姓名" clearable />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="searchForm.idCard" placeholder="请输入身份证号" clearable />
        </el-form-item>
        <el-form-item label="电话号码">
          <el-input v-model="searchForm.phone" placeholder="请输入电话号码" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格内容 -->
    <el-card class="table-card">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="所有患者" name="all"></el-tab-pane>
        <el-tab-pane label="我的患者" name="my"></el-tab-pane>
      </el-tabs>

      <el-table
        v-loading="loading"
        :data="patientList"
        stripe
        style="width: 100%"
        border
        row-key="id"
      >
        <el-table-column prop="patientNo" label="患者编号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="genderText" label="性别" width="60" />
        <el-table-column prop="age" label="年龄" width="60" />
        <el-table-column prop="phone" label="联系电话" width="120" />
        <el-table-column prop="idCard" label="身份证号" width="180" />
        <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
        <el-table-column label="创建时间" width="160">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="180">
          <template #default="scope">
            <el-button link type="primary" @click="handleViewPatient(scope.row)">查看</el-button>
            <el-button link type="primary" @click="handleEditPatient(scope.row)">编辑</el-button>
            <el-popconfirm
              title="确定要删除这个患者吗？"
              @confirm="handleDeletePatient(scope.row.id)"
            >
              <template #reference>
                <el-button link type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页器 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 30, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handleCurrentChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getPatientList, getMyPatients, deletePatient } from '../../api/patient'

const router = useRouter()

// 搜索表单
const searchForm = reactive({
  name: '',
  idCard: '',
  phone: ''
})

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 患者列表数据
const patientList = ref([])
const loading = ref(false)
const activeTab = ref('all')

// 获取患者列表数据
const fetchPatientList = async () => {
  loading.value = true
  try {
    if (activeTab.value === 'all') {
      const params = {
        current: pagination.current,
        size: pagination.size,
        name: searchForm.name || undefined,
        idCard: searchForm.idCard || undefined,
        phone: searchForm.phone || undefined
      }

      const res = await getPatientList(params)
      if (res.data && res.data.code === 1) {
        patientList.value = res.data.data.records || []
        pagination.total = res.data.data.total || 0
      } else {
        ElMessage.error(res.data?.message || '获取患者列表失败')
      }
    } else {
      // 获取我的患者
      console.log('正在获取我的患者列表...')
      const res = await getMyPatients()
      console.log('获取我的患者返回结果:', res)
      
      if (res.data && res.data.code === 1) {
        // 确保数据是数组格式
        if (Array.isArray(res.data.data)) {
          patientList.value = res.data.data
        } else if (res.data.data && res.data.data.records) {
          patientList.value = res.data.data.records
        } else {
          patientList.value = []
          console.warn('我的患者列表数据格式不符合预期:', res.data.data)
        }
        
        pagination.total = patientList.value.length
        
        // 手动过滤
        if (searchForm.name || searchForm.idCard || searchForm.phone) {
          patientList.value = patientList.value.filter(patient => {
            return (!searchForm.name || patient.name.includes(searchForm.name)) &&
                   (!searchForm.idCard || (patient.idCard && patient.idCard.includes(searchForm.idCard))) &&
                   (!searchForm.phone || (patient.phone && patient.phone.includes(searchForm.phone)))
          })
        }
      } else {
        ElMessage.error(res.data?.message || '获取我的患者列表失败')
        console.error('获取我的患者列表失败:', res.data)
      }
    }
  } catch (error) {
    console.error('获取患者列表出错:', error)
    ElMessage.error('获取患者列表出错: ' + (error.response?.data?.message || error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchPatientList()
}

// 重置搜索条件
const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  handleSearch()
}

// 分页相关操作
const handleCurrentChange = (val) => {
  pagination.current = val
  fetchPatientList()
}

const handleSizeChange = (val) => {
  pagination.size = val
  pagination.current = 1
  fetchPatientList()
}

// 切换标签页
const handleTabChange = () => {
  pagination.current = 1
  fetchPatientList()
}

// 新增患者
const handleAddPatient = () => {
  router.push('/patients/add')
}

// 查看患者详情
const handleViewPatient = (row) => {
  router.push(`/patients/detail/${row.id}`)
}

// 编辑患者
const handleEditPatient = (row) => {
  router.push(`/patients/edit/${row.id}`)
}

// 删除患者
const handleDeletePatient = async (id) => {
  try {
    const res = await deletePatient(id)
    if (res.data && res.data.code === 1) {
      ElMessage.success('删除成功')
      fetchPatientList()
    } else {
      ElMessage.error(res.data?.message || '删除失败')
    }
  } catch (error) {
    console.error('删除患者出错:', error)
    ElMessage.error('删除患者出错')
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 初始化
onMounted(() => {
  fetchPatientList()
})
</script>

<style scoped>
.patient-list {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-card {
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
</style> 