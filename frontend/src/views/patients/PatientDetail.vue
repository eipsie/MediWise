<template>
  <div class="patient-detail">
    <div class="page-header">
      <h2>患者详情</h2>
      <div class="header-actions">
        <el-button @click="handleEdit" type="primary">编辑</el-button>
        <el-button @click="goBack">返回列表</el-button>
      </div>
    </div>

    <el-card v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>基本信息</span>
          <div class="patient-no">患者编号: {{ patient.patientNo }}</div>
        </div>
      </template>
      
      <el-descriptions :column="2" border>
        <el-descriptions-item label="姓名">{{ patient.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ patient.genderText }}</el-descriptions-item>
        <el-descriptions-item label="出生日期">{{ formatDate(patient.birthDate) }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ patient.age }}岁</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ patient.idCard }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ patient.phone }}</el-descriptions-item>
        <el-descriptions-item label="地址" :span="2">{{ patient.address || '无' }}</el-descriptions-item>
        <el-descriptions-item label="过敏史" :span="2">
          {{ patient.allergies || '无' }}
        </el-descriptions-item>
        <el-descriptions-item label="既往病史" :span="2">
          {{ patient.medicalHistory || '无' }}
        </el-descriptions-item>
        <el-descriptions-item label="创建者">{{ patient.creatorName }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDateTime(patient.createTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 相关诊断记录等其他信息可以在此处扩展 -->
    <el-card class="diagnoses-card">
      <template #header>
        <div class="card-header">
          <span>诊断记录</span>
          <el-button type="primary" size="small" @click="handleAddDiagnosis">
            <el-icon><Plus /></el-icon>新增诊断
          </el-button>
        </div>
      </template>
      
      <div class="empty-tip" v-if="!hasDiagnoses">
        <el-empty description="暂无诊断记录" />
      </div>
      
      <!-- 诊断记录列表 -->
      <div v-else>
        <el-table :data="diagnosisList" style="width: 100%" border stripe>
          <el-table-column prop="diagnosisTime" label="诊断时间" width="180">
            <template #default="scope">
              {{ formatDateTime(scope.row.diagnosisTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="symptoms" label="主要症状" min-width="200" show-overflow-tooltip>
            <template #default="scope">
              {{ scope.row.symptomsText.substring(0, 50) + (scope.row.symptomsText.length > 50 ? '...' : '') }}
            </template>
          </el-table-column>
          <el-table-column prop="finalDiagnosis" label="诊断结果" min-width="150" show-overflow-tooltip />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getDiagnosisStatusType(scope.row.status)" size="small">
                {{ getDiagnosisStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="doctorName" label="医生" width="120" />
          <el-table-column fixed="right" label="操作" width="150">
            <template #default="scope">
              <el-button link type="primary" size="small" @click="viewDiagnosis(scope.row.id)">查看</el-button>
              <el-button 
                link 
                type="primary" 
                size="small" 
                @click="editDiagnosis(scope.row.id)" 
                v-if="scope.row.status !== 'COMPLETED'"
              >
                编辑
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 分页 -->
        <div class="pagination-container" v-if="diagnosisPagination.total > diagnosisPagination.size">
          <el-pagination
            v-model:current-page="diagnosisPagination.current"
            v-model:page-size="diagnosisPagination.size"
            :page-sizes="[5, 10, 20]"
            layout="total, sizes, prev, pager, next"
            :total="diagnosisPagination.total"
            @size-change="fetchPatientDiagnoses"
            @current-change="fetchPatientDiagnoses"
          />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getPatientById } from '../../api/patient'
import { getPatientDiagnoses, getDiagnosisStatusText, getDiagnosisStatusType } from '../../api/diagnosis'

const route = useRoute()
const router = useRouter()
const patientId = route.params.id
const loading = ref(false)
const patient = ref({})
const hasDiagnoses = ref(false) // 后续可以根据实际情况调整

const diagnosisList = ref([])
const diagnosisPagination = reactive({
  current: 1,
  size: 5,
  total: 0
})

// 获取患者详情
const fetchPatientDetail = async () => {
  if (!patientId) {
    ElMessage.error('患者ID不能为空')
    return
  }
  
  loading.value = true
  try {
    const res = await getPatientById(patientId)
    if (res.data && res.data.code === 1) {
      patient.value = res.data.data || {}
    } else {
      ElMessage.error(res.data?.message || '获取患者详情失败')
    }
  } catch (error) {
    console.error('获取患者详情出错:', error)
    ElMessage.error('获取患者详情出错')
  } finally {
    loading.value = false
  }
}

// 编辑患者信息
const handleEdit = () => {
  router.push(`/patients/edit/${patientId}`)
}

// 返回患者列表
const goBack = () => {
  router.push('/patients/list')
}

// 新增诊断
const handleAddDiagnosis = () => {
  router.push({
    path: '/diagnoses/add',
    query: { patientId }
  })
}

// 获取患者的诊断记录
const fetchPatientDiagnoses = async () => {
  if (!patientId) return
  
  try {
    const res = await getPatientDiagnoses(
      patientId, 
      diagnosisPagination.current,
      diagnosisPagination.size
    )
    
    if (res.data && res.data.code === 1) {
      const pageData = res.data.data
      diagnosisList.value = pageData.records || []
      diagnosisPagination.total = pageData.total || 0
      hasDiagnoses.value = diagnosisList.value.length > 0
    } else {
      ElMessage.error(res.data?.message || '获取诊断记录失败')
    }
  } catch (error) {
    console.error('获取诊断记录出错:', error)
    ElMessage.error('获取诊断记录出错')
  }
}

// 查看诊断详情
const viewDiagnosis = (id) => {
  router.push(`/diagnoses/detail/${id}`)
}

// 编辑诊断
const editDiagnosis = (id) => {
  router.push(`/diagnoses/edit/${id}`)
}

// 格式化日期 (yyyy-MM-dd)
const formatDate = (dateString) => {
  if (!dateString) return '-'
  // 如果已经是日期格式字符串，直接返回
  if (typeof dateString === 'string' && dateString.length <= 10) {
    return dateString
  }
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 格式化日期时间 (yyyy-MM-dd HH:mm:ss)
const formatDateTime = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`
}

// 初始化
onMounted(() => {
  fetchPatientDetail()
  fetchPatientDiagnoses()
})
</script>

<style scoped>
.patient-detail {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.patient-no {
  font-size: 14px;
  color: #909399;
}

.diagnoses-card {
  margin-top: 20px;
}

.empty-tip {
  padding: 30px 0;
}
</style> 