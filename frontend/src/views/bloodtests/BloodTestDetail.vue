<template>
  <div class="blood-test-detail">
    <div class="page-header">
      <h2>血常规检查详情</h2>
      <div class="header-actions">
        <el-button type="success" @click="generateReport" :disabled="!bloodTest.id">
          <el-icon><Printer /></el-icon> 生成报告
        </el-button>
        <el-button type="primary" @click="handleEdit">
          <el-icon><EditPen /></el-icon> 编辑
        </el-button>
        <el-button @click="goBack">
          <el-icon><Back /></el-icon> 返回
        </el-button>
      </div>
    </div>

    <!-- 患者信息卡片 -->
    <el-card class="detail-card" v-loading="loading" shadow="hover">
      <template #header>
        <div class="card-header">
          <span><el-icon><User /></el-icon> 患者信息</span>
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
      <el-empty v-else description="暂无患者信息" :image-size="60"></el-empty>
    </el-card>
    
    <!-- 血常规检查数据卡片 -->
    <el-card class="detail-card" v-loading="loading" shadow="hover">
      <template #header>
        <div class="card-header">
          <span><el-icon><DataAnalysis /></el-icon> 血常规检查数据</span>
          <el-tag type="success" v-if="bloodTest.aiAnalysisResult">已进行AI分析</el-tag>
        </div>
      </template>
      
      <el-descriptions :column="1" border direction="vertical">
        <el-descriptions-item label="检测时间">
          {{ formatDateTime(bloodTest.testDate) }}
        </el-descriptions-item>
        
        <el-descriptions-item v-for="(item, index) in bloodTestItems" :key="index" :label="item.label">
          <template v-if="bloodTest[item.field] !== undefined && bloodTest[item.field] !== null">
            <span :class="getValueClass(bloodTest[item.field], item.field)" style="font-weight: bold;">
              {{ bloodTest[item.field] }} {{ item.unit }}
            </span>
            <div v-if="getBloodTestValueStatus(item.field, bloodTest[item.field]) === 'high'" class="value-high">
              ↑ 高于参考范围
            </div>
            <div v-if="getBloodTestValueStatus(item.field, bloodTest[item.field]) === 'low'" class="value-low">
              ↓ 低于参考范围
            </div>
            <div class="reference-range">参考范围: {{ item.reference }}</div>
          </template>
          <span v-else>--</span>
        </el-descriptions-item>
        
        <el-descriptions-item label="创建时间">
          {{ formatDateTime(bloodTest.createTime) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
    
    <!-- AI分析结果卡片 -->
    <el-card class="detail-card" v-loading="loading" shadow="hover">
      <template #header>
        <div class="card-header">
          <span><el-icon><Cpu /></el-icon> AI分析结果</span>
          <div v-if="!bloodTest.aiAnalysisResult">
            <el-button type="primary" size="small" @click="handleAnalyze" :loading="analyzing">
              <el-icon><EditPen /></el-icon> AI辅助分析
            </el-button>
          </div>
        </div>
      </template>
      
      <div class="ai-result" v-if="bloodTest.aiAnalysisResult">
        <div class="markdown-content" v-html="formatAiResult">
        </div>
      </div>
      
      <div v-else class="no-analysis">
        <el-empty description="尚未进行AI分析" :image-size="100">
          <template #extra>
            <el-button type="primary" @click="handleAnalyze" :loading="analyzing">
              立即分析
            </el-button>
          </template>
        </el-empty>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { EditPen, Printer, Back, User, DataAnalysis, Cpu } from '@element-plus/icons-vue'
import { 
  getBloodTestById, 
  analyzeBloodTest as analyzeAPI,
  getBloodTestValueStatus
} from '../../api/bloodtest'
import { getPatientById } from '../../api/patient'
import MarkdownIt from 'markdown-it'

const route = useRoute()
const router = useRouter()
const bloodTestId = route.params.id
const loading = ref(false)
const analyzing = ref(false)
const bloodTest = ref({})
const patient = ref({})

const md = new MarkdownIt()

// 血常规检查项目配置
const bloodTestItems = [
  { field: 'wbc', label: '白细胞计数 (WBC)', unit: '× 10^9/L', reference: '4.0-10.0 × 10^9/L' },
  { field: 'rbc', label: '红细胞计数 (RBC)', unit: '× 10^12/L', reference: '3.5-5.5 × 10^12/L' },
  { field: 'hgb', label: '血红蛋白 (HGB)', unit: 'g/L', reference: '110-160 g/L' },
  { field: 'plt', label: '血小板计数 (PLT)', unit: '× 10^9/L', reference: '100-300 × 10^9/L' },
  { field: 'neutp', label: '中性粒细胞百分比 (NEUT%)', unit: '%', reference: '50-70 %' },
  { field: 'lymp', label: '淋巴细胞百分比 (LYM%)', unit: '%', reference: '20-40 %' }
]

// 获取值的CSS类
const getValueClass = (value, field) => {
  const status = getBloodTestValueStatus(field, value)
  return status === 'high' ? 'value-high' : status === 'low' ? 'value-low' : ''
}

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

// 格式化AI分析结果
const formatAiResult = computed(() => {
  try {
    if (!bloodTest.value.aiAnalysisResult) return ''
    
    const data = typeof bloodTest.value.aiAnalysisResult === 'string' 
      ? JSON.parse(bloodTest.value.aiAnalysisResult) 
      : bloodTest.value.aiAnalysisResult
    
    return data.content ? md.render(data.content) : md.render(JSON.stringify(data, null, 2))
  } catch (e) {
    return `<p>无法解析AI分析结果</p><pre>${bloodTest.value.aiAnalysisResult}</pre>`
  }
})

// 获取血常规检查详情
const fetchBloodTestDetail = async () => {
  loading.value = true
  try {
    const res = await getBloodTestById(bloodTestId)
    if (res.data && res.data.code === 1) {
      bloodTest.value = res.data.data
      
      // 获取患者详情
      if (bloodTest.value.patientId) {
        await fetchPatientDetail(bloodTest.value.patientId)
      }
    } else {
      ElMessage.error(res.data?.message || '获取血常规检查详情失败')
    }
  } catch (error) {
    ElMessage.error('获取血常规检查详情出错')
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
    ElMessage.warning('获取患者详情出错')
  }
}

// AI分析
const handleAnalyze = async () => {
  analyzing.value = true
  try {
    const res = await analyzeAPI({
      bloodTestId: bloodTestId,
      applyMedicalPrompt: true
    })
    
    if (res.data && res.data.code === 1) {
      ElMessage.success('AI分析请求已提交，请稍后刷新查看结果')
      // 分析完成后刷新页面
      setTimeout(() => {
        fetchBloodTestDetail()
      }, 1000)
    } else {
      ElMessage.error(res.data?.message || 'AI分析请求失败')
    }
  } catch (error) {
    ElMessage.error('AI分析请求出错')
  } finally {
    analyzing.value = false
  }
}

// 跳转编辑页面
const handleEdit = () => {
  router.push(`/bloodtests/edit/${bloodTestId}`)
}

// 返回列表
const goBack = () => {
  if (route.query.from === 'patient' && patient.value.id) {
    router.push(`/patients/detail/${patient.value.id}`)
  } else {
    router.push('/bloodtests/list')
  }
}

// 前往患者详情页
const goToPatientDetail = () => {
  if (patient.value.id) {
    router.push(`/patients/detail/${patient.value.id}`)
  }
}

// 获取状态文本
const getStatusText = (field, value) => {
  const status = getBloodTestValueStatus(field, value)
  if (status === 'high') {
    return '高于参考值'
  } else if (status === 'low') {
    return '低于参考值'
  }
  return '正常'
}

// 生成报告
const generateReport = () => {
  if (!bloodTest.value || !bloodTest.value.id) {
    ElMessage.warning('没有检查数据可供生成报告')
    return
  }
  
  // 创建报告模板
  const reportTemplate = `
    <!DOCTYPE html>
    <html>
    <head>
      <title>血常规检查报告 - ${patient.value?.name || '患者'}</title>
      <meta charset="utf-8">
      <style>
        body {
          font-family: Arial, sans-serif;
          line-height: 1.6;
          max-width: 800px;
          margin: 0 auto;
          padding: 20px;
          color: #333;
        }
        .header {
          text-align: center;
          margin-bottom: 20px;
          padding-bottom: 10px;
          border-bottom: 2px solid #1989fa;
        }
        .header h1 {
          margin-bottom: 5px;
          color: #1989fa;
        }
        .patient-info {
          margin-bottom: 20px;
          padding: 15px;
          background-color: #f9f9f9;
          border-radius: 8px;
          border-left: 4px solid #1989fa;
        }
        table {
          width: 100%;
          border-collapse: collapse;
          margin-bottom: 20px;
          border-radius: 8px;
          overflow: hidden;
          box-shadow: 0 0 10px rgba(0,0,0,0.05);
        }
        th, td {
          padding: 12px;
          border: 1px solid #ebeef5;
          text-align: left;
        }
        th {
          background-color: #f5f7fa;
          color: #606266;
          font-weight: 600;
        }
        tr:nth-child(even) {
          background-color: #fafafa;
        }
        .high {
          color: #f56c6c;
          font-weight: bold;
        }
        .low {
          color: #e6a23c;
          font-weight: bold;
        }
        .analysis {
          margin-top: 20px;
          padding: 15px;
          background-color: #f0f9eb;
          border-radius: 8px;
          border-left: 4px solid #67c23a;
        }
        .analysis h2 {
          color: #67c23a;
          margin-top: 0;
        }
        .footer {
          margin-top: 30px;
          text-align: center;
          font-size: 12px;
          color: #909399;
          padding-top: 15px;
          border-top: 1px solid #ebeef5;
        }
        @media print {
          body {
            padding: 0;
          }
          .no-print {
            display: none;
          }
          button {
            display: none;
          }
          table {
            box-shadow: none;
          }
        }
      </style>
    </head>
    <body>
      <div class="header">
        <h1>血常规检查报告</h1>
        <p>报告时间: ${new Date().toLocaleString('zh-CN')}</p>
      </div>
      
      <div class="patient-info">
        <h2>患者信息</h2>
        <p><strong>姓名:</strong> ${patient.value?.name || '--'}</p>
        <p><strong>编号:</strong> ${patient.value?.patientNo || '--'}</p>
        <p><strong>性别:</strong> ${patient.value?.genderText || '--'} | <strong>年龄:</strong> ${patient.value?.age || '--'} 岁</p>
        <p><strong>检测日期:</strong> ${formatDateTime(bloodTest.value.testDate)}</p>
        ${patient.value?.allergies ? `<p><strong>过敏史:</strong> ${patient.value.allergies}</p>` : ''}
        ${patient.value?.medicalHistory ? `<p><strong>既往病史:</strong> ${patient.value.medicalHistory}</p>` : ''}
      </div>
      
      <h2>检查结果</h2>
      <table>
        <thead>
          <tr>
            <th>检测项目</th>
            <th>结果</th>
            <th>参考范围</th>
            <th>状态</th>
          </tr>
        </thead>
        <tbody>
          ${bloodTestItems.map(item => `
            <tr>
              <td>${item.label}</td>
              <td class="${getBloodTestValueStatus(item.field, bloodTest.value[item.field]) || ''}">
                ${bloodTest.value[item.field] !== null && bloodTest.value[item.field] !== undefined 
                  ? bloodTest.value[item.field] + ' ' + item.unit 
                  : '--'}</td>
              <td>${item.reference}</td>
              <td>${getStatusText(item.field, bloodTest.value[item.field])}</td>
            </tr>
          `).join('')}
        </tbody>
      </table>
      
      ${bloodTest.value.aiAnalysisResult ? 
        `<div class="analysis">
          <h2>AI辅助分析</h2>
          ${formatAiResult.value}
        </div>` 
        : ''}
      
      <div class="footer">
        <p>报告生成于: ${formatDateTime(new Date())} | MediWise医疗系统</p>
        <p>注: 本报告仅供医学参考，具体诊断请遵医嘱。</p>
      </div>
      
      <div class="no-print" style="margin-top: 20px; text-align: center;">
        <button onclick="window.print()" style="padding: 10px 20px; background-color: #409EFF; color: white; border: none; border-radius: 4px; cursor: pointer; box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);">
          打印报告
        </button>
        <button onclick="window.close()" style="padding: 10px 20px; margin-left: 10px; background-color: #67c23a; color: white; border: none; border-radius: 4px; cursor: pointer; box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);">
          关闭窗口
        </button>
      </div>
    </body>
    </html>
  `;
  
  // 打开新窗口并显示报告
  const reportWindow = window.open('', '_blank');
  reportWindow.document.write(reportTemplate);
  reportWindow.document.close();
}

// 页面初始化时加载数据
onMounted(() => {
  fetchBloodTestDetail()
})
</script>

<style scoped>
.blood-test-detail {
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

.header-actions .el-button {
  margin-left: 10px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  display: flex;
  align-items: center;
  font-weight: bold;
}

.card-header .el-icon {
  margin-right: 8px;
  font-size: 18px;
}

.detail-card {
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
}

.patient-info {
  padding: 10px 0;
}

.patient-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.patient-header h3 {
  margin: 0;
  margin-right: 10px;
  color: #303133;
}

.patient-details {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
}

.patient-details p {
  margin: 8px 0;
}

.ai-result {
  padding: 10px;
}

.markdown-content {
  line-height: 1.8;
  color: #303133;
}

.markdown-content h3 {
  color: #409EFF;
  font-size: 16px;
  margin-top: 16px;
  margin-bottom: 12px;
  border-bottom: 1px solid #eaeaea;
  padding-bottom: 8px;
}

.markdown-content p {
  margin: 10px 0;
}

.markdown-content ul {
  padding-left: 20px;
  margin: 10px 0;
}

.value-high {
  color: #f56c6c;
}

.value-low {
  color: #e6a23c;
}

.reference-range {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.no-analysis {
  padding: 30px 0;
}

:deep(.el-descriptions__label) {
  font-weight: bold;
  width: 180px;
}

:deep(.el-descriptions__content) {
  padding: 12px 15px;
}

@media (max-width: 768px) {
  .blood-test-detail {
    padding: 10px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .header-actions {
    margin-top: 10px;
    width: 100%;
    display: flex;
  }
  
  .header-actions .el-button {
    flex: 1;
  }
}
</style> 