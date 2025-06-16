<template>
  <div class="blood-test-detail">
    <div class="page-header">
      <h2>血常规检查详情</h2>
      <div class="header-actions">
        <el-button type="success" @click="generateReport" :disabled="!bloodTest.id">
          <el-icon><Printer /></el-icon> 生成报告
        </el-button>
        <el-button type="primary" @click="handleEdit">编辑</el-button>
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
    
    <!-- 血常规检查数据卡片 -->
    <el-card class="detail-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>血常规检查数据</span>
          <el-tag type="success" v-if="bloodTest.aiAnalysisResult">已进行AI分析</el-tag>
        </div>
      </template>
      
      <el-descriptions :column="1" border>
        <el-descriptions-item label="检测时间">
          {{ formatDateTime(bloodTest.testDate) }}
        </el-descriptions-item>
        
        <el-descriptions-item label="白细胞计数 (WBC)">
          <template v-if="bloodTest.wbc !== undefined && bloodTest.wbc !== null">
            <span :class="getValueClass(bloodTest.wbc, 'wbc')" style="font-weight: bold;">
              {{ bloodTest.wbc }} × 10^9/L
            </span>
            <div v-if="getBloodTestValueStatus('wbc', bloodTest.wbc) === 'high'" class="value-high">
              ↑ 高于参考范围
            </div>
            <div v-if="getBloodTestValueStatus('wbc', bloodTest.wbc) === 'low'" class="value-low">
              ↓ 低于参考范围
            </div>
            <div class="reference-range">参考范围: 4.0-10.0 × 10^9/L</div>
          </template>
          <span v-else>--</span>
        </el-descriptions-item>
        
        <el-descriptions-item label="红细胞计数 (RBC)">
          <template v-if="bloodTest.rbc !== undefined && bloodTest.rbc !== null">
            <span :class="getValueClass(bloodTest.rbc, 'rbc')" style="font-weight: bold;">
              {{ bloodTest.rbc }} × 10^12/L
            </span>
            <div v-if="getBloodTestValueStatus('rbc', bloodTest.rbc) === 'high'" class="value-high">
              ↑ 高于参考范围
            </div>
            <div v-if="getBloodTestValueStatus('rbc', bloodTest.rbc) === 'low'" class="value-low">
              ↓ 低于参考范围
            </div>
            <div class="reference-range">参考范围: 3.5-5.5 × 10^12/L</div>
          </template>
          <span v-else>--</span>
        </el-descriptions-item>
        
        <el-descriptions-item label="血红蛋白 (HGB)">
          <template v-if="bloodTest.hgb !== undefined && bloodTest.hgb !== null">
            <span :class="getValueClass(bloodTest.hgb, 'hgb')" style="font-weight: bold;">
              {{ bloodTest.hgb }} g/L
            </span>
            <div v-if="getBloodTestValueStatus('hgb', bloodTest.hgb) === 'high'" class="value-high">
              ↑ 高于参考范围
            </div>
            <div v-if="getBloodTestValueStatus('hgb', bloodTest.hgb) === 'low'" class="value-low">
              ↓ 低于参考范围
            </div>
            <div class="reference-range">参考范围: 110-160 g/L</div>
          </template>
          <span v-else>--</span>
        </el-descriptions-item>
        
        <el-descriptions-item label="血小板计数 (PLT)">
          <template v-if="bloodTest.plt !== undefined && bloodTest.plt !== null">
            <span :class="getValueClass(bloodTest.plt, 'plt')" style="font-weight: bold;">
              {{ bloodTest.plt }} × 10^9/L
            </span>
            <div v-if="getBloodTestValueStatus('plt', bloodTest.plt) === 'high'" class="value-high">
              ↑ 高于参考范围
            </div>
            <div v-if="getBloodTestValueStatus('plt', bloodTest.plt) === 'low'" class="value-low">
              ↓ 低于参考范围
            </div>
            <div class="reference-range">参考范围: 100-300 × 10^9/L</div>
          </template>
          <span v-else>--</span>
        </el-descriptions-item>
        
        <el-descriptions-item label="中性粒细胞百分比 (NEUT%)">
          <template v-if="bloodTest.neutp !== undefined && bloodTest.neutp !== null">
            <span :class="getValueClass(bloodTest.neutp, 'neutp')" style="font-weight: bold;">
              {{ bloodTest.neutp }} %
            </span>
            <div v-if="getBloodTestValueStatus('neutp', bloodTest.neutp) === 'high'" class="value-high">
              ↑ 高于参考范围
            </div>
            <div v-if="getBloodTestValueStatus('neutp', bloodTest.neutp) === 'low'" class="value-low">
              ↓ 低于参考范围
            </div>
            <div class="reference-range">参考范围: 50-70 %</div>
          </template>
          <span v-else>--</span>
        </el-descriptions-item>
        
        <el-descriptions-item label="淋巴细胞百分比 (LYM%)">
          <template v-if="bloodTest.lymp !== undefined && bloodTest.lymp !== null">
            <span :class="getValueClass(bloodTest.lymp, 'lymp')" style="font-weight: bold;">
              {{ bloodTest.lymp }} %
            </span>
            <div v-if="getBloodTestValueStatus('lymp', bloodTest.lymp) === 'high'" class="value-high">
              ↑ 高于参考范围
            </div>
            <div v-if="getBloodTestValueStatus('lymp', bloodTest.lymp) === 'low'" class="value-low">
              ↓ 低于参考范围
            </div>
            <div class="reference-range">参考范围: 20-40 %</div>
          </template>
          <span v-else>--</span>
        </el-descriptions-item>
        
        <el-descriptions-item label="创建时间">
          {{ formatDateTime(bloodTest.createTime) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
    
    <!-- AI分析结果卡片 -->
    <el-card class="detail-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>AI分析结果</span>
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
import { EditPen, Printer } from '@element-plus/icons-vue'
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

// 格式化AI分析结果
const formatAiResult = computed(() => {
  try {
    if (!bloodTest.value.aiAnalysisResult) return '';
    
    const data = typeof bloodTest.value.aiAnalysisResult === 'string' 
      ? JSON.parse(bloodTest.value.aiAnalysisResult) 
      : bloodTest.value.aiAnalysisResult;
    
    if (data.content) {
      return md.render(data.content);
    }
    
    return md.render(JSON.stringify(data, null, 2));
  } catch (e) {
    console.error('格式化AI结果错误:', e);
    return `<p>无法解析AI分析结果</p><pre>${bloodTest.value.aiAnalysisResult}</pre>`;
  }
});

// 获取血常规检查详情
const fetchBloodTestDetail = async () => {
  loading.value = true
  try {
    const res = await getBloodTestById(bloodTestId)
    if (res.data && res.data.code === 1) {
      bloodTest.value = res.data.data
      console.log('获取到的血常规检查详情:', bloodTest.value)
      
      // 获取患者详情
      if (bloodTest.value.patientId) {
        await fetchPatientDetail(bloodTest.value.patientId)
      }
    } else {
      ElMessage.error(res.data?.message || '获取血常规检查详情失败')
    }
  } catch (error) {
    console.error('获取血常规检查详情出错:', error)
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
    console.error('获取患者详情出错:', error)
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
      // 1秒后刷新页面
      setTimeout(() => {
        fetchBloodTestDetail()
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
        }
        .header {
          text-align: center;
          margin-bottom: 20px;
          padding-bottom: 10px;
          border-bottom: 1px solid #ccc;
        }
        .header h1 {
          margin-bottom: 5px;
          color: #333;
        }
        .patient-info {
          margin-bottom: 20px;
          padding: 15px;
          background-color: #f9f9f9;
          border-radius: 5px;
        }
        table {
          width: 100%;
          border-collapse: collapse;
          margin-bottom: 20px;
        }
        th, td {
          padding: 10px;
          border: 1px solid #ddd;
          text-align: left;
        }
        th {
          background-color: #f2f2f2;
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
          border-radius: 5px;
        }
        .footer {
          margin-top: 30px;
          text-align: center;
          font-size: 12px;
          color: #999;
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
          <tr>
            <td>白细胞计数 (WBC)</td>
            <td class="${getBloodTestValueStatus('wbc', bloodTest.value.wbc) || ''}">${bloodTest.value.wbc !== null && bloodTest.value.wbc !== undefined ? bloodTest.value.wbc + ' × 10^9/L' : '--'}</td>
            <td>4.0-10.0 × 10^9/L</td>
            <td>${getStatusText('wbc', bloodTest.value.wbc)}</td>
          </tr>
          <tr>
            <td>红细胞计数 (RBC)</td>
            <td class="${getBloodTestValueStatus('rbc', bloodTest.value.rbc) || ''}">${bloodTest.value.rbc !== null && bloodTest.value.rbc !== undefined ? bloodTest.value.rbc + ' × 10^12/L' : '--'}</td>
            <td>3.5-5.5 × 10^12/L</td>
            <td>${getStatusText('rbc', bloodTest.value.rbc)}</td>
          </tr>
          <tr>
            <td>血红蛋白 (HGB)</td>
            <td class="${getBloodTestValueStatus('hgb', bloodTest.value.hgb) || ''}">${bloodTest.value.hgb !== null && bloodTest.value.hgb !== undefined ? bloodTest.value.hgb + ' g/L' : '--'}</td>
            <td>110-160 g/L</td>
            <td>${getStatusText('hgb', bloodTest.value.hgb)}</td>
          </tr>
          <tr>
            <td>血小板计数 (PLT)</td>
            <td class="${getBloodTestValueStatus('plt', bloodTest.value.plt) || ''}">${bloodTest.value.plt !== null && bloodTest.value.plt !== undefined ? bloodTest.value.plt + ' × 10^9/L' : '--'}</td>
            <td>100-300 × 10^9/L</td>
            <td>${getStatusText('plt', bloodTest.value.plt)}</td>
          </tr>
          <tr>
            <td>中性粒细胞百分比 (NEUT%)</td>
            <td class="${getBloodTestValueStatus('neutp', bloodTest.value.neutp) || ''}">${bloodTest.value.neutp !== null && bloodTest.value.neutp !== undefined ? bloodTest.value.neutp + ' %' : '--'}</td>
            <td>50-70 %</td>
            <td>${getStatusText('neutp', bloodTest.value.neutp)}</td>
          </tr>
          <tr>
            <td>淋巴细胞百分比 (LYM%)</td>
            <td class="${getBloodTestValueStatus('lymp', bloodTest.value.lymp) || ''}">${bloodTest.value.lymp !== null && bloodTest.value.lymp !== undefined ? bloodTest.value.lymp + ' %' : '--'}</td>
            <td>20-40 %</td>
            <td>${getStatusText('lymp', bloodTest.value.lymp)}</td>
          </tr>
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
        <button onclick="window.print()" style="padding: 10px 20px; background-color: #409EFF; color: white; border: none; border-radius: 4px; cursor: pointer;">
          打印报告
        </button>
        <button onclick="window.close()" style="padding: 10px 20px; margin-left: 10px; background-color: #67c23a; color: white; border: none; border-radius: 4px; cursor: pointer;">
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

// 获取状态文本
const getStatusText = (field, value) => {
  const status = getBloodTestValueStatus(field, value);
  if (status === 'high') {
    return '高于参考值';
  } else if (status === 'low') {
    return '低于参考值';
  }
  return '正常';
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

.detail-card {
  margin-bottom: 20px;
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
}

.patient-details {
  color: #606266;
  font-size: 14px;
}

.patient-details p {
  margin: 5px 0;
}

.ai-result {
  margin-bottom: 20px;
}

.markdown-content {
  line-height: 1.6;
}

.markdown-content h3 {
  color: #409EFF;
  font-size: 16px;
  margin-top: 15px;
  margin-bottom: 10px;
  border-bottom: 1px solid #eaeaea;
  padding-bottom: 5px;
}

.markdown-content p {
  margin: 8px 0;
}

.markdown-content ul {
  padding-left: 20px;
}

.blood-test-value-display {
  display: flex;
  flex-direction: column;
}

.blood-test-value-display .value {
  font-weight: bold;
  font-size: 16px;
}

.blood-test-value-display .status {
  font-size: 12px;
  margin: 4px 0;
}

.blood-test-value-display .reference {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.value-high {
  color: #f56c6c;
}

.value-low {
  color: #e6a23c;
}

.no-analysis {
  padding: 20px 0;
}

.reference-range {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</style> 