<template>
  <div class="diagnosis-form">
    <div class="page-header">
      <h2>{{ isEdit ? '编辑诊断' : '新增诊断' }}</h2>
      <div class="header-actions">
        <el-button @click="goBack">返回</el-button>
      </div>
    </div>

    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-position="top"
      v-loading="loading"
    >
      <!-- 患者信息卡片 -->
      <el-card class="patient-card">
        <template #header>
          <div class="card-header">
            <span>患者信息</span>
            <el-button 
              v-if="!patientId && !form.patientId" 
              type="primary" 
              size="small" 
              @click="selectPatient"
            >
              <el-icon><Plus /></el-icon> 选择患者
            </el-button>
          </div>
        </template>

        <div v-if="patient.id" class="patient-info">
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
        
        <div v-else class="patient-empty">
          <el-empty description="请先选择患者" :image-size="100" />
        </div>
      </el-card>
      
      <!-- 诊断内容卡片 -->
      <el-card class="diagnosis-card">
        <template #header>
          <div class="card-header">
            <span>诊断内容</span>
            <el-tooltip 
              content="先填写诊断内容，可以直接保存草稿，或使用AI辅助分析功能" 
              placement="top"
            >
              <el-icon><QuestionFilled /></el-icon>
            </el-tooltip>
          </div>
        </template>
        
        <el-form-item label="症状描述" prop="symptomsText">
          <el-input
            v-model="form.symptomsText"
            type="textarea"
            :rows="4"
            placeholder="请描述患者症状..."
          />
        </el-form-item>
        
        <el-form-item label="生命体征">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-input v-model="vitalSigns.temperature" placeholder="体温(°C)">
                <template #append>°C</template>
              </el-input>
            </el-col>
            <el-col :span="8">
              <el-input v-model="vitalSigns.bloodPressure" placeholder="血压(mmHg)">
                <template #append>mmHg</template>
              </el-input>
            </el-col>
            <el-col :span="8">
              <el-input v-model="vitalSigns.heartRate" placeholder="心率(bpm)">
                <template #append>bpm</template>
              </el-input>
            </el-col>
          </el-row>
        </el-form-item>
        
        <div class="form-actions">
          <el-button-group>
            <el-button type="primary" @click="handleAIAnalyze" :disabled="!form.symptomsText" :loading="analyzing">
              <el-icon><Edit /></el-icon> AI辅助分析
            </el-button>
            <el-button type="success" @click="saveDraft" :disabled="!form.symptomsText" :loading="saving">
              <el-icon><Document /></el-icon> 保存草稿
            </el-button>
          </el-button-group>
        </div>
      </el-card>
      
      <!-- 诊断结果卡片 -->
      <el-card class="diagnosis-result-card" v-if="isAnalyzed || isEdit || showResultCard">
        <template #header>
          <div class="card-header">
            <span>诊断结果</span>
          </div>
        </template>
        
        <div v-if="isAnalyzed && form.llmResponseData" class="ai-result">
          <div class="ai-badge">
            <el-tag type="success">AI辅助分析结果</el-tag>
          </div>
          <div class="ai-content markdown-content" v-html="formatAiResult(form.llmResponseData)">
          </div>
        </div>
        
        <el-form-item label="最终诊断" prop="finalDiagnosis">
          <el-input
            v-model="form.finalDiagnosis"
            placeholder="请输入最终诊断结果"
          />
        </el-form-item>
        
        <el-form-item label="治疗方案" prop="treatmentPlan">
          <el-input
            v-model="form.treatmentPlan"
            type="textarea"
            :rows="4"
            placeholder="请输入治疗方案..."
          />
        </el-form-item>
        
        <el-form-item label="状态">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        
        <div class="form-actions">
          <el-button type="primary" @click="submitForm" :loading="submitting">保存</el-button>
          <el-button @click="resetForm">重置</el-button>
        </div>
      </el-card>
    </el-form>
    
    <!-- 患者选择对话框 -->
    <el-dialog v-model="patientDialogVisible" title="选择患者" width="70%">
      <el-form :inline="true" :model="patientSearch">
        <el-form-item>
          <el-input v-model="patientSearch.keyword" placeholder="姓名/编号/手机" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchPatients">搜索</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="patientList" stripe border style="width: 100%">
        <el-table-column prop="patientNo" label="患者编号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="genderText" label="性别" width="60" />
        <el-table-column prop="age" label="年龄" width="60" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
        <el-table-column fixed="right" label="操作" width="120">
          <template #default="scope">
            <el-button link type="primary" @click="selectThisPatient(scope.row)">选择</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="patientDialogVisible = false">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  createDiagnosis, 
  getDiagnosisById, 
  updateDiagnosis, 
  analyzeDiagnosis,
  getDiagnosisStatusList 
} from '../../api/diagnosis'
import { getPatientList, getPatientById } from '../../api/patient'
import { QuestionFilled, Edit, Document, Plus } from '@element-plus/icons-vue'
import MarkdownIt from 'markdown-it'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const submitting = ref(false)

// 路由参数
const diagnosisId = computed(() => route.params.id)
const patientId = computed(() => route.query.patientId)
const isEdit = computed(() => !!diagnosisId.value)

// 表单数据
const form = reactive({
  patientId: '',
  symptomsText: '',
  symptomsStructured: '',
  vitalSigns: '',
  llmResponseData: '',
  finalDiagnosis: '',
  treatmentPlan: '',
  status: 'DRAFT'
})

// 生命体征数据
const vitalSigns = reactive({
  temperature: '',
  bloodPressure: '',
  heartRate: ''
})

// 诊断状态选项
const statusOptions = getDiagnosisStatusList()

// AI分析状态
const isAnalyzed = ref(false)

// 患者信息
const patient = ref({})

// 患者选择对话框
const patientDialogVisible = ref(false)
const patientSearch = reactive({
  keyword: '',
  page: 1,
  size: 10
})
const patientList = ref([])

// 表单验证规则
const rules = {
  symptomsText: [
    { required: true, message: '请输入症状描述', trigger: 'blur' }
  ],
  finalDiagnosis: [
    { required: true, message: '请输入最终诊断', trigger: 'blur' }
  ]
}

// 在script部分添加showResultCard状态变量
const showResultCard = ref(false)

// 添加状态变量
const analyzing = ref(false)
const saving = ref(false)

const md = new MarkdownIt()

// 监听生命体征的变化，更新表单中的vitalSigns字段
watch(vitalSigns, (newVal) => {
  // 过滤掉空值
  const filteredSigns = Object.fromEntries(
    Object.entries(newVal).filter(([_, v]) => v !== null && v !== '')
  )
  
  // 如果有值则转为JSON字符串
  if (Object.keys(filteredSigns).length > 0) {
    form.vitalSigns = JSON.stringify(filteredSigns)
  } else {
    form.vitalSigns = ''
  }
}, { deep: true })

// 初始化患者信息
const initPatientInfo = async (id) => {
  try {
    const res = await getPatientById(id)
    if (res.data && res.data.code === 1) {
      patient.value = res.data.data
      form.patientId = patient.value.id
    } else {
      ElMessage.error(res.data?.message || '获取患者信息失败')
    }
  } catch (error) {
    console.error('获取患者信息出错:', error)
    ElMessage.error('获取患者信息出错')
  }
}

// 获取诊断记录详情
const fetchDiagnosisDetail = async () => {
  if (!isEdit.value) return
  
  loading.value = true
  try {
    const res = await getDiagnosisById(diagnosisId.value)
    if (res.data && res.data.code === 1) {
      const diagnosisData = res.data.data
      Object.keys(form).forEach(key => {
        if (diagnosisData[key] !== undefined) {
          form[key] = diagnosisData[key]
        }
      })
      
      // 如果有生命体征数据，解析到vitalSigns对象
      if (form.vitalSigns) {
        try {
          const parsedVitalSigns = JSON.parse(form.vitalSigns)
          Object.keys(vitalSigns).forEach(key => {
            if (parsedVitalSigns[key] !== undefined) {
              vitalSigns[key] = parsedVitalSigns[key]
            }
          })
        } catch (e) {
          console.error('解析生命体征数据错误:', e)
        }
      }
      
      // 获取患者信息
      await initPatientInfo(form.patientId)
      
      // 如果有AI分析结果，标记为已分析
      isAnalyzed.value = !!form.llmResponseData
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

// 提交表单，改进错误处理
const submitForm = async () => {
  if (!form.patientId) {
    ElMessage.warning('请先选择患者')
    return
  }
  
  try {
    submitting.value = true
    
    // 表单验证
    await formRef.value.validate()
    
    let res
    
    if (isEdit.value) {
      // 更新诊断 - 仅发送updateDTO支持的字段
      console.log('更新诊断:', diagnosisId.value)
      res = await updateDiagnosis(diagnosisId.value, {
        finalDiagnosis: form.finalDiagnosis,
        treatmentPlan: form.treatmentPlan,
        status: form.status
      })
    } else {
      // 创建新诊断
      console.log('创建诊断')
      res = await createNewDiagnosis()
    }
    
    if (res.data && res.data.code === 1) {
      ElMessage.success(isEdit.value ? '更新诊断成功' : '创建诊断成功')
      
      // 如果是从患者详情页过来，返回患者详情页
      if (patientId.value) {
        router.push(`/patients/detail/${patientId.value}`)
      } else {
        // 否则返回诊断列表
        router.push('/diagnoses/list')
      }
    } else {
      ElMessage.error(res.data?.message || (isEdit.value ? '更新诊断失败' : '创建诊断失败'))
    }
  } catch (error) {
    console.error('提交表单出错:', error)
    // 增强错误处理，尝试从响应中获取详细错误信息
    let errorMessage = '操作失败'
    
    if (error.response) {
      // 如果有响应对象，尝试解析具体错误
      const responseData = error.response.data
      if (responseData) {
        errorMessage = responseData.message || `${error.response.status}: ${responseData.error || '未知错误'}`
      } else {
        errorMessage = `HTTP错误: ${error.response.status}`
      }
    } else if (error.message) {
      errorMessage = error.message
    }
    
    ElMessage.error(`提交表单出错: ${errorMessage}`)
  } finally {
    submitting.value = false
  }
}

// 重置表单
const resetForm = () => {
  if (isEdit.value) {
    fetchDiagnosisDetail()
  } else {
    formRef.value.resetFields()
    // 清空生命体征数据
    Object.keys(vitalSigns).forEach(key => {
      vitalSigns[key] = ''
    })
  }
}

// 返回上一页
const goBack = () => {
  if (patientId.value) {
    router.push(`/patients/detail/${patientId.value}`)
  } else {
    router.push('/diagnoses/list')
  }
}

// 打开患者选择对话框
const selectPatient = () => {
  patientDialogVisible.value = true
  searchPatients()
}

// 搜索患者
const searchPatients = async () => {
  try {
    const params = {
      ...patientSearch,
      name: patientSearch.keyword
    }
    const res = await getPatientList(params)
    if (res.data && res.data.code === 1) {
      patientList.value = res.data.data.records || []
    } else {
      ElMessage.error(res.data?.message || '获取患者列表失败')
    }
  } catch (error) {
    console.error('搜索患者出错:', error)
    ElMessage.error('搜索患者出错')
  }
}

// 选择患者
const selectThisPatient = (selectedPatient) => {
  patient.value = selectedPatient
  form.patientId = selectedPatient.id
  patientDialogVisible.value = false
}

// 创建诊断
const createNewDiagnosis = async () => {
  // 表单验证
  const validationResult = await formRef.value.validate().catch(err => {
    console.error('表单验证失败:', err);
    return false;
  });
  
  if (!validationResult) {
    return Promise.reject(new Error('表单验证失败'));
  }
  
  // 创建诊断时只发送后端DTO支持的字段
  const createPayload = {
    patientId: form.patientId,
    symptomsText: form.symptomsText,
    symptomsStructured: form.symptomsStructured || null,
    vitalSigns: form.vitalSigns || null
  };
  
  console.log('发送创建诊断请求:', createPayload);
  return await createDiagnosis(createPayload);
};

// AI辅助分析
const handleAIAnalyze = async () => {
  if (!form.symptomsText) {
    ElMessage.warning('请先填写症状描述')
    return
  }
  
  if (!form.patientId) {
    ElMessage.warning('请先选择患者')
    return
  }
  
  loading.value = true
  analyzing.value = true
  try {
    let res
    let diagId
    
    if (isEdit.value) {
      diagId = diagnosisId.value
      // 对已有诊断进行分析
      res = await analyzeDiagnosis({
        diagnosisId: diagId,
        applyMedicalPrompt: true
      })
    } else {
      // 创建一个草稿诊断记录然后分析
      const createRes = await createNewDiagnosis()
      
      if (createRes.data && createRes.data.code === 1) {
        diagId = createRes.data.data.id
        res = await analyzeDiagnosis({
          diagnosisId: diagId,
          applyMedicalPrompt: true
        })
        
        // 如果不是编辑模式，更新URL
        router.replace({
          path: `/diagnoses/edit/${diagId}`,
          query: route.query
        })
      } else {
        throw new Error(createRes.data?.message || '创建诊断失败')
      }
    }
    
    if (res.data && res.data.code === 1) {
      ElMessage.success('AI分析请求已提交，正在处理中...')
      // 开始轮询检查分析结果
      pollAnalysisResult(diagId)
    } else {
      ElMessage.error(res.data?.message || 'AI分析请求失败')
      loading.value = false
      analyzing.value = false
    }
  } catch (error) {
    console.error('AI分析出错:', error)
    // 增强错误处理，尝试从响应中获取详细错误信息
    let errorMessage = '操作失败'
    
    if (error.response) {
      // 如果有响应对象，尝试解析具体错误
      const responseData = error.response.data
      if (responseData) {
        errorMessage = responseData.message || `${error.response.status}: ${responseData.error || '未知错误'}`
      } else {
        errorMessage = `HTTP错误: ${error.response.status}`
      }
    } else if (error.message) {
      errorMessage = error.message
    }
    
    ElMessage.error(`AI分析出错: ${errorMessage}`)
    loading.value = false
    analyzing.value = false
  }
}

// 轮询检查分析结果
const pollAnalysisResult = async (id) => {
  const maxAttempts = 20 // 最多尝试20次，每次3秒，总共约1分钟
  let attempts = 0
  let pollInterval

  pollInterval = setInterval(async () => {
    try {
      attempts++
      console.log(`正在检查分析结果，第${attempts}次尝试`)
      
      const res = await getDiagnosisById(id)
      if (res.data && res.data.code === 1) {
        const diagData = res.data.data
        
        // 如果分析结果已返回
        if (diagData.llmResponseData) {
          clearInterval(pollInterval)
          
          // 更新表单数据
          Object.keys(form).forEach(key => {
            if (diagData[key] !== undefined) {
              form[key] = diagData[key]
            }
          })
          
          isAnalyzed.value = true
          ElMessage.success('AI分析完成！')
          showResultCard.value = true // 显示结果卡片
          
          // 显示建议的诊断结果
          if (!form.finalDiagnosis && diagData.llmResponseData) {
            try {
              const analysis = JSON.parse(diagData.llmResponseData)
              if (analysis.diagnosisSuggestion) {
                form.finalDiagnosis = analysis.diagnosisSuggestion
              }
              
              if (analysis.treatmentSuggestion) {
                form.treatmentPlan = analysis.treatmentSuggestion
              }
            } catch (e) {
              console.error('解析AI结果失败:', e)
            }
          }
        } else if (attempts >= maxAttempts) {
          clearInterval(pollInterval)
          ElMessage.warning('分析请求已提交，但处理时间较长，请稍后刷新页面查看结果')
        }
      } else {
        console.error('获取分析结果失败:', res.data?.message)
        if (attempts >= maxAttempts) {
          clearInterval(pollInterval)
          ElMessage.error('获取分析结果失败，请稍后刷新页面重试')
        }
      }
    } catch (error) {
      console.error('轮询分析结果错误:', error)
      if (attempts >= maxAttempts) {
        clearInterval(pollInterval)
        ElMessage.error('获取分析结果出错，请稍后刷新页面重试')
      }
    } finally {
      if (!pollInterval._destroyed && attempts >= maxAttempts) {
        clearInterval(pollInterval)
      }
      
      if (!pollInterval._destroyed && isAnalyzed.value) {
        loading.value = false
        analyzing.value = false
      }
    }
  }, 3000) // 每3秒检查一次
}

// 格式化AI分析结果
const formatAiResult = (dataStr) => {
  try {
    if (!dataStr) return ''
    
    const data = typeof dataStr === 'string' ? JSON.parse(dataStr) : dataStr
    
    let result = ''
    
    if (data.possibleDiagnoses) {
      result += `### 可能的诊断\n${data.possibleDiagnoses}\n\n`
    }
    
    if (data.reasoningProcess) {
      result += `### 分析推理\n${data.reasoningProcess}\n\n`
    }
    
    if (data.diagnosisSuggestion) {
      result += `### 建议诊断\n${data.diagnosisSuggestion}\n\n`
    }
    
    if (data.treatmentSuggestion) {
      result += `### 建议治疗\n${data.treatmentSuggestion}\n\n`
    }
    
    return md.render(result)
  } catch (e) {
    console.error('格式化AI结果错误:', e)
    return `<p>无法解析AI分析结果</p><pre>${dataStr}</pre>`
  }
}

// 保存草稿
const saveDraft = async () => {
  if (!form.symptomsText) {
    ElMessage.warning('请先填写症状描述')
    return
  }
  
  if (!form.patientId) {
    ElMessage.warning('请先选择患者')
    return
  }
  
  saving.value = true
  try {
    let res
    
    if (isEdit.value) {
      // 更新草稿
      res = await updateDiagnosis(diagnosisId.value, {
        symptomsText: form.symptomsText,
        vitalSigns: form.vitalSigns,
        status: 'DRAFT'
      })
    } else {
      // 创建草稿
      res = await createNewDiagnosis()
      
      // 如果创建成功，跳转到编辑页面
      if (res.data && res.data.code === 1) {
        const newDiagId = res.data.data.id
        router.replace({
          path: `/diagnoses/edit/${newDiagId}`,
          query: route.query
        })
      }
    }
    
    if (res.data && res.data.code === 1) {
      ElMessage.success('保存草稿成功')
    } else {
      ElMessage.error(res.data?.message || '保存草稿失败')
    }
  } catch (error) {
    console.error('保存草稿出错:', error)
    ElMessage.error('保存草稿出错: ' + (error.message || '未知错误'))
  } finally {
    saving.value = false
  }
}

// 初始化获取数据
onMounted(async () => {
  // 如果是编辑模式则获取诊断详情
  if (isEdit.value) {
    await fetchDiagnosisDetail()
  } 
  // 如果有患者ID参数，则获取患者信息
  else if (patientId.value) {
    await initPatientInfo(patientId.value)
  }
})
</script>

<style scoped>
.diagnosis-form {
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

.patient-card,
.diagnosis-card,
.diagnosis-result-card {
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

.form-actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-start;
}

.ai-result {
  margin-bottom: 20px;
  background-color: #f8f8f8;
  border-radius: 4px;
  padding: 15px;
}

.ai-badge {
  margin-bottom: 10px;
}

.ai-content {
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
</style>