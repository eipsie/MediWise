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
        <el-descriptions-item label="AI辅助分析" v-if="diagnosis.llmResponseData">
          <div class="ai-analysis-header">
            <el-switch
              v-model="showRawData"
              active-text="查看原始数据"
              inactive-text="查看格式化结果"
              @change="toggleDataView"
              size="small"
            />
          </div>
          <div v-if="!showRawData" class="ai-result markdown-content" v-html="formatAiResult(diagnosis.llmResponseData)"></div>
          <pre v-else class="raw-data">{{ diagnosis.llmResponseData }}</pre>
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
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'

// 初始化markdown渲染器
const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  breaks: true,
  highlight: function (str, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return '<pre class="hljs"><code>' +
               hljs.highlight(str, { language: lang, ignoreIllegals: true }).value +
               '</code></pre>';
      } catch (__) {}
    }
    return '<pre class="hljs"><code>' + md.utils.escapeHtml(str) + '</code></pre>';
  }
})

const route = useRoute()
const router = useRouter()
const diagnosisId = route.params.id
const loading = ref(false)
const diagnosis = ref({})
const patient = ref({})
const showRawData = ref(false)

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
    
    // 尝试从不同格式的JSON中提取内容
    let result = '';
    
    // 解析不同格式的JSON结构
    if (parsedData.possibleDiagnoses) {
      result += `### 可能的诊断\n${parsedData.possibleDiagnoses}\n\n`;
    }
    
    if (parsedData.reasoningProcess) {
      result += `### 分析推理\n${parsedData.reasoningProcess}\n\n`;
    }
    
    if (parsedData.diagnosisSuggestion) {
      result += `### 建议诊断\n${parsedData.diagnosisSuggestion}\n\n`;
    }
    
    if (parsedData.treatmentSuggestion) {
      result += `### 建议治疗\n${parsedData.treatmentSuggestion}\n\n`;
    }
    
    // 处理简单的结构
    if (!result) {
      if (parsedData.result) {
        result = parsedData.result;
      } else if (parsedData.content) {
        result = parsedData.content;
      } else if (typeof parsedData === 'string') {
        result = parsedData;
      } else {
        // 如果是其他格式，尝试展示关键字段
        result = '### AI分析结果\n\n';
        
        Object.entries(parsedData).forEach(([key, value]) => {
          if (typeof value === 'string' && value.trim()) {
            result += `**${key}**: ${value}\n\n`;
          }
        });
      }
    }
    
    // 处理文本以改善Markdown渲染
    let processedText = result
      .replace(/\n([-*+]|\d+\.)\s/g, '\n\n$1 ')  // 确保列表项前有空行
      .replace(/\n\|\s*([^|]+)\s*\|/g, '\n| $1 |');  // 确保表格行正确格式化
    
    return md.render(processedText);
  } catch (e) {
    console.error('解析AI返回数据出错:', e);
    // 如果解析失败，直接返回原字符串的HTML安全版本
    try {
      return `<div class="raw-result">${aiDataStr.replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/\n/g, '<br>')}</div>`;
    } catch (err) {
      return '<div class="error-message">无法显示AI分析结果</div>';
    }
  }
}

// 格式化日期时间 (yyyy-MM-dd HH:mm:ss)
const formatDateTime = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`
}

// 切换数据视图模式
const toggleDataView = (value) => {
  showRawData.value = value
  console.log('切换数据视图模式:', value ? '原始数据' : '格式化结果')
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
  padding: 12px 15px;
  background-color: #f0f9eb;
  border-left: 3px solid #67c23a;
  border-radius: 4px;
  margin-right: -10px; /* 向右偏移一点 */
  max-width: 95%; /* 控制宽度，更靠右 */
}

.highlight-item :deep(.el-descriptions-item__content) {
  font-weight: bold;
  color: #409eff;
}

/* Markdown样式 */
.markdown-content :deep(h3) {
  color: #409EFF;
  font-size: 16px;
  margin-top: 15px;
  margin-bottom: 10px;
  border-bottom: 1px solid #eaeaea;
  padding-bottom: 5px;
}

.markdown-content :deep(h4) {
  color: #67C23A;
  font-size: 15px;
  margin-top: 12px;
  margin-bottom: 8px;
}

.markdown-content :deep(p) {
  margin: 8px 0;
  line-height: 1.6;
}

.markdown-content :deep(ul), .markdown-content :deep(ol) {
  padding-left: 20px;
  margin: 8px 0;
}

.markdown-content :deep(li) {
  margin-bottom: 6px;
  line-height: 1.5;
}

.markdown-content :deep(code) {
  background-color: rgba(0, 0, 0, 0.05);
  padding: 2px 4px;
  border-radius: 3px;
  font-family: monospace;
}

.markdown-content :deep(pre) {
  background-color: #f6f8fa;
  padding: 12px;
  border-radius: 4px;
  overflow-x: auto;
  margin: 12px 0;
}

.markdown-content :deep(blockquote) {
  border-left: 4px solid #dfe2e5;
  padding-left: 16px;
  margin: 16px 0;
  color: #666;
}

.markdown-content :deep(table) {
  border-collapse: collapse;
  margin: 12px 0;
  width: 100%;
}

.markdown-content :deep(th), .markdown-content :deep(td) {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

.markdown-content :deep(th) {
  background-color: #f2f2f2;
}

.markdown-content :deep(tr:nth-child(even)) {
  background-color: #f9f9f9;
}

/* 增加一个特殊处理可能的诊断列表的样式 */
.markdown-content :deep(ol) {
  counter-reset: diagnosis-counter;
}

.markdown-content :deep(ol li) {
  counter-increment: diagnosis-counter;
  position: relative;
  padding-left: 5px;
}

.raw-result {
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  font-family: monospace;
  white-space: pre-wrap;
}

.error-message {
  color: #f56c6c;
  padding: 10px;
  background-color: #fef0f0;
  border-radius: 4px;
}

/* 修改描述列表项的样式，让内容更合理地显示 */
:deep(.el-descriptions__body) {
  width: 100%;
}

:deep(.el-descriptions__label) {
  width: 120px;
  min-width: 120px;
  text-align: right;
  padding-right: 15px;
}

:deep(.el-descriptions__content) {
  padding: 12px 15px;
  text-align: left;
}

.raw-data {
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  font-family: monospace;
  white-space: pre-wrap;
  max-height: 400px;
  overflow-y: auto;
  font-size: 12px;
  line-height: 1.4;
  border: 1px solid #dcdfe6;
}

.ai-analysis-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 10px;
}
</style> 