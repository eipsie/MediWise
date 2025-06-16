<template>
  <div class="diagnosis-detail">
    <div class="page-header">
      <h2>诊断详情</h2>
      <div class="header-actions">
        <el-button type="primary" @click="handleEdit" v-if="canEdit">编辑</el-button>
        <el-button @click="goBack">返回</el-button>
      </div>
    </div>

    <!-- 患者信息卡片 -->
    <el-card class="detail-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>患者信息</span>
          <el-button type="text" @click="goToPatientDetail">查看患者档案</el-button>
        </div>
      </template>
      
      <div class="patient-info" v-if="patient.id">
        <div class="patient-header">
          <h3>{{ patient.name }}</h3>
          <el-tag size="small" type="info">{{ patient.patientNo }}</el-tag>
        </div>
        <div class="patient-details">
          <span>{{ patient.genderText }} | {{ patient.age }}岁 | {{ patient.phone }}</span>
          <p v-if="patient.allergies"><strong>过敏史：</strong>{{ patient.allergies }}</p>
          <p v-if="patient.medicalHistory"><strong>既往病史：</strong>{{ patient.medicalHistory }}</p>
        </div>
      </div>
    </el-card>
    
    <!-- 诊断信息卡片 -->
    <el-card class="detail-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>诊断信息</span>
          <div class="status-tag">
            <el-tag :type="getDiagnosisStatusType(diagnosis.status)">
              {{ getDiagnosisStatusText(diagnosis.status) }}
            </el-tag>
          </div>
        </div>
      </template>
      
      <el-descriptions :column="1" border>
        <el-descriptions-item label="诊断时间">
          {{ formatDateTime(diagnosis.diagnosisTime) }}
        </el-descriptions-item>
        
        <el-descriptions-item label="主诊医生">
          {{ diagnosis.doctorName }}
        </el-descriptions-item>
        
        <el-descriptions-item label="症状描述">
          <div class="multiline-text">{{ diagnosis.symptomsText }}</div>
        </el-descriptions-item>
        
        <el-descriptions-item label="生命体征" v-if="diagnosis.vitalSigns">
          <div class="vital-signs">
            <template v-for="(value, key) in parsedVitalSigns" :key="key">
              <span class="vital-sign-item">
                <strong>{{ getVitalSignLabel(key) }}:</strong> {{ value }}
                {{ getVitalSignUnit(key) }}
              </span>
            </template>
          </div>
        </el-descriptions-item>
        
        <!-- 仅在非COMPLETED状态才显示AI辅助分析 -->
        <el-descriptions-item label="AI辅助分析" v-if="diagnosis.llmResponseData && diagnosis.status !== 'COMPLETED'">
          <div class="ai-result">{{ formatAiResult(diagnosis.llmResponseData) }}</div>
        </el-descriptions-item>
        
        <el-descriptions-item label="最终诊断" class="highlight-item">
          {{ diagnosis.finalDiagnosis }}
        </el-descriptions-item>
        
        <el-descriptions-item label="治疗方案">
          <div class="multiline-text">{{ diagnosis.treatmentPlan }}</div>
        </el-descriptions-item>
        
        <el-descriptions-item label="创建时间">
          {{ formatDateTime(diagnosis.createTime) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getDiagnosisById, getDiagnosisStatusText, getDiagnosisStatusType } from '../../api/diagnosis'
import { getPatientById } from '../../api/patient'

const route = useRoute()
const router = useRouter()
const diagnosisId = route.params.id
const loading = ref(false)
const diagnosis = ref({})
const patient = ref({})

// 是否可以编辑（非"已完成"状态可编辑）
const canEdit = computed(() => {
  return diagnosis.value.status !== 'COMPLETED'
})

// 解析后的生命体征
const parsedVitalSigns = computed(() => {
  try {
    return diagnosis.value.vitalSigns ? JSON.parse(diagnosis.value.vitalSigns) : {}
  } catch (e) {
    console.error('解析生命体征数据错误:', e)
    return {}
  }
})

// 获取诊断详情
const fetchDiagnosisDetail = async () => {
  loading.value = true
  try {
    const res = await getDiagnosisById(diagnosisId)
    if (res.data && res.data.code === 1) {
      diagnosis.value = res.data.data
      
      // 获取患者详情
      if (diagnosis.value.patientId) {
        await fetchPatientDetail(diagnosis.value.patientId)
      }
    } else {
      ElMessage.error(res.data?.message || '获取诊断详情失败')
    }
  } catch (error) {
    console.error('获取诊断详情出错:', error)
    ElMessage.error('获取诊断详情出错')
  } finally {
    loading.value = false
  }
}

// 获取患者详情
const fetchPatientDetail = async (patientId) => {
  try {
    const res = await getPatientById(patientId)
    if (res.data && res.data.code === 1) {
      patient.value = res.data.data
    } else {
      ElMessage.warning(res.data?.message || '获取患者信息失败')
    }
  } catch (error) {
    console.error('获取患者详情出错:', error)
    ElMessage.warning('获取患者详情出错')
  }
}

// 跳转编辑页面
const handleEdit = () => {
  router.push(`/diagnoses/edit/${diagnosisId}`)
}

// 返回列表
const goBack = () => {
  // 如果是从患者详情页跳转来的，则返回患者详情页
  if (route.query.from === 'patient' && patient.value.id) {
    router.push(`/patients/detail/${patient.value.id}`)
  } else {
    // 否则返回诊断列表
    router.push('/diagnoses/list')
  }
}

// 前往患者详情页
const goToPatientDetail = () => {
  if (patient.value.id) {
    router.push(`/patients/detail/${patient.value.id}`)
  }
}

// 获取生命体征的标签
const getVitalSignLabel = (key) => {
  const labels = {
    temperature: '体温',
    bloodPressure: '血压',
    heartRate: '心率'
  }
  return labels[key] || key
}

// 获取生命体征的单位
const getVitalSignUnit = (key) => {
  const units = {
    temperature: '°C',
    bloodPressure: 'mmHg',
    heartRate: 'bpm'
  }
  return units[key] || ''
}

// 格式化AI分析结果
const formatAiResult = (aiDataStr) => {
  if (!aiDataStr) return '';
  
  try {
    const parsedData = JSON.parse(aiDataStr);
    
    // 支持多种JSON格式
    if (parsedData.result) {
      return parsedData.result;
    } else if (parsedData.content) {
      return parsedData.content;
    } else if (typeof parsedData === 'string') {
      return parsedData;
    } else {
      // 如果是其他格式，尝试展示整个JSON
      return JSON.stringify(parsedData);
    }
  } catch (e) {
    console.error('解析AI返回数据出错:', e);
    // 如果解析失败，直接返回原字符串
    return aiDataStr;
  }
}

// 格式化日期时间 (yyyy-MM-dd HH:mm:ss)
const formatDateTime = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`
}

// 初始化
onMounted(() => {
  if (diagnosisId) {
    fetchDiagnosisDetail()
  } else {
    ElMessage.error('诊断ID不能为空')
    router.push('/diagnoses/list')
  }
})
</script>

<style scoped>
.diagnosis-detail {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.detail-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.patient-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.patient-header h3 {
  margin: 0;
}

.patient-details {
  color: #606266;
  font-size: 14px;
}

.patient-details p {
  margin: 5px 0;
}

.multiline-text {
  white-space: pre-wrap;
}

.vital-signs {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.vital-sign-item {
  padding: 4px 8px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.ai-result {
  padding: 10px;
  background-color: #f0f9eb;
  border-left: 3px solid #67c23a;
  border-radius: 4px;
  white-space: pre-wrap;
}

.highlight-item :deep(.el-descriptions-item__content) {
  font-weight: bold;
  color: #409eff;
}
</style> 