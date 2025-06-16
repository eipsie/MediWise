<template>
  <div class="blood-test-form">
    <div class="page-header">
      <h2>{{ isEdit ? '编辑血常规检查' : '新增血常规检查' }}</h2>
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
          <el-empty description="请先选择患者" :image-size="60" />
        </div>
      </el-card>
      
      <!-- 血常规检查内容卡片 -->
      <el-card class="blood-test-card">
        <template #header>
          <div class="card-header">
            <span>血常规检查数据</span>
            <el-tooltip 
              content="请填写完整的血常规检查数据，可以使用AI辅助分析功能" 
              placement="top"
            >
              <el-icon><QuestionFilled /></el-icon>
            </el-tooltip>
          </div>
        </template>

        <el-form-item label="检测日期" prop="testDate">
          <el-date-picker
            v-model="form.testDate"
            type="datetime"
            placeholder="选择检测日期时间"
            format="YYYY-MM-DD HH:mm"
            value-format="YYYY-MM-DDTHH:mm:ss"
            :shortcuts="dateShortcuts"
          />
        </el-form-item>
        
        <el-row :gutter="20">
          <!-- 白细胞计数 -->
          <el-col :span="8">
            <el-form-item label="白细胞计数 (WBC)" prop="wbc">
              <el-input v-model="form.wbc" type="number" step="0.1">
                <template #append>× 10^9/L</template>
              </el-input>
              <div class="reference-range">参考范围: 4.0-10.0 × 10^9/L</div>
            </el-form-item>
          </el-col>
          
          <!-- 红细胞计数 -->
          <el-col :span="8">
            <el-form-item label="红细胞计数 (RBC)" prop="rbc">
              <el-input v-model="form.rbc" type="number" step="0.1">
                <template #append>× 10^12/L</template>
              </el-input>
              <div class="reference-range">参考范围: 3.5-5.5 × 10^12/L</div>
            </el-form-item>
          </el-col>
          
          <!-- 血红蛋白 -->
          <el-col :span="8">
            <el-form-item label="血红蛋白 (HGB)" prop="hgb">
              <el-input v-model="form.hgb" type="number" step="1">
                <template #append>g/L</template>
              </el-input>
              <div class="reference-range">参考范围: 110-160 g/L</div>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <!-- 血小板计数 -->
          <el-col :span="8">
            <el-form-item label="血小板计数 (PLT)" prop="plt">
              <el-input v-model="form.plt" type="number" step="1">
                <template #append>× 10^9/L</template>
              </el-input>
              <div class="reference-range">参考范围: 100-300 × 10^9/L</div>
            </el-form-item>
          </el-col>
          
          <!-- 中性粒细胞百分比 -->
          <el-col :span="8">
            <el-form-item label="中性粒细胞百分比 (NEUT%)" prop="neutp">
              <el-input v-model="form.neutp" type="number" step="0.1">
                <template #append>%</template>
              </el-input>
              <div class="reference-range">参考范围: 50-70 %</div>
            </el-form-item>
          </el-col>
          
          <!-- 淋巴细胞百分比 -->
          <el-col :span="8">
            <el-form-item label="淋巴细胞百分比 (LYM%)" prop="lymp">
              <el-input v-model="form.lymp" type="number" step="0.1">
                <template #append>%</template>
              </el-input>
              <div class="reference-range">参考范围: 20-40 %</div>
            </el-form-item>
          </el-col>
        </el-row>
        
        <div class="form-actions">
          <el-button-group>
            <el-button type="success" @click="submitForm" :loading="submitting">
              <el-icon><Document /></el-icon> 保存检查记录
            </el-button>
            <el-button type="primary" @click="handleAIAnalyze" :disabled="!canAnalyze" :loading="analyzing">
              <el-icon><Edit /></el-icon> AI辅助分析
            </el-button>
          </el-button-group>
        </div>
      </el-card>
      
      <!-- AI分析结果卡片 -->
      <el-card class="analysis-result-card" v-if="isAnalyzed">
        <template #header>
          <div class="card-header">
            <span>AI分析结果</span>
          </div>
        </template>
        
        <div class="ai-result">
          <div class="ai-badge">
            <el-tag type="success">AI辅助分析结果</el-tag>
          </div>
          <div class="ai-content markdown-content" v-html="formatAiResult">
          </div>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { QuestionFilled, Edit, Document, Plus } from '@element-plus/icons-vue'
import { getPatientList, getPatientById } from '../../api/patient'
import { 
  createBloodTest,
  getBloodTestById,
  analyzeBloodTest
} from '../../api/bloodtest'
import MarkdownIt from 'markdown-it'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const submitting = ref(false)
const analyzing = ref(false)

// 路由参数
const bloodTestId = computed(() => route.params.id)
const patientId = computed(() => route.query.patientId)
const isEdit = computed(() => !!bloodTestId.value)

// 表单数据
const form = reactive({
  patientId: '',
  testDate: new Date().toISOString().slice(0, 19), // 当前日期时间，ISO格式
  wbc: '',
  rbc: '',
  hgb: '',
  plt: '',
  neutp: '',
  lymp: '',
  aiAnalysisResult: ''
})

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

// 日期快捷选项
const dateShortcuts = [
  {
    text: '今天',
    value: new Date()
  },
  {
    text: '昨天',
    value: () => {
      const date = new Date()
      date.setTime(date.getTime() - 3600 * 1000 * 24)
      return date
    }
  },
  {
    text: '一周前',
    value: () => {
      const date = new Date()
      date.setTime(date.getTime() - 3600 * 1000 * 24 * 7)
      return date
    }
  }
]

// markdown解析器
const md = new MarkdownIt()

// 表单验证规则
const rules = {
  patientId: [
    { required: true, message: '请选择患者', trigger: 'change' }
  ],
  testDate: [
    { required: true, message: '请选择检测日期', trigger: 'change' }
  ],
  wbc: [
    { pattern: /^\d+(\.\d+)?$/, message: '请输入有效的数值', trigger: 'blur' }
  ],
  rbc: [
    { pattern: /^\d+(\.\d+)?$/, message: '请输入有效的数值', trigger: 'blur' }
  ],
  hgb: [
    { pattern: /^\d+(\.\d+)?$/, message: '请输入有效的数值', trigger: 'blur' }
  ],
  plt: [
    { pattern: /^\d+(\.\d+)?$/, message: '请输入有效的数值', trigger: 'blur' }
  ],
  neutp: [
    { pattern: /^\d+(\.\d+)?$/, message: '请输入有效的数值', trigger: 'blur' }
  ],
  lymp: [
    { pattern: /^\d+(\.\d+)?$/, message: '请输入有效的数值', trigger: 'blur' }
  ]
}

// 是否可以进行AI分析
const canAnalyze = computed(() => {
  return form.patientId && 
    (form.wbc || form.rbc || form.hgb || form.plt || form.neutp || form.lymp);
})

// 格式化AI分析结果
const formatAiResult = computed(() => {
  try {
    if (!form.aiAnalysisResult) return '';
    
    const data = typeof form.aiAnalysisResult === 'string' 
      ? JSON.parse(form.aiAnalysisResult) 
      : form.aiAnalysisResult;
    
    if (data.content) {
      return md.render(data.content);
    }
    
    return md.render(JSON.stringify(data, null, 2));
  } catch (e) {
    console.error('格式化AI结果错误:', e);
    return `<p>无法解析AI分析结果</p><pre>${form.aiAnalysisResult}</pre>`;
  }
});

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

// 获取血常规检查记录详情
const fetchBloodTestDetail = async () => {
  if (!isEdit.value) return
  
  loading.value = true
  try {
    const res = await getBloodTestById(bloodTestId.value)
    if (res.data && res.data.code === 1) {
      const bloodTestData = res.data.data
      Object.keys(form).forEach(key => {
        if (bloodTestData[key] !== undefined) {
          form[key] = bloodTestData[key]
        }
      })
      
      // 获取患者信息
      await initPatientInfo(form.patientId)
      
      // 如果有AI分析结果，标记为已分析
      isAnalyzed.value = !!form.aiAnalysisResult
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

// 提交表单
const submitForm = async () => {
  if (!form.patientId) {
    ElMessage.warning('请先选择患者')
    return
  }
  
  try {
    submitting.value = true
    
    // 表单验证
    await formRef.value.validate()
    
    // 准备提交的数据，确保类型正确
    const submitData = {
      patientId: Number(form.patientId),
      testDate: form.testDate,
      wbc: form.wbc !== '' ? Number(form.wbc) : null,
      rbc: form.rbc !== '' ? Number(form.rbc) : null,
      hgb: form.hgb !== '' ? Number(form.hgb) : null,
      plt: form.plt !== '' ? Number(form.plt) : null,
      neutp: form.neutp !== '' ? Number(form.neutp) : null,
      lymp: form.lymp !== '' ? Number(form.lymp) : null
    }
    
    // 处理日期格式，从 "YYYY-MM-DD HH:mm:ss" 转换为 "YYYY-MM-DDThh:mm:ss" ISO-8601格式
    if (submitData.testDate && typeof submitData.testDate === 'string') {
      submitData.testDate = submitData.testDate.replace(' ', 'T');
    }
    
    console.log('提交的数据:', submitData)
    
    // 创建血常规检查记录
    const res = await createBloodTest(submitData)
    
    if (res.data && res.data.code === 1) {
      ElMessage.success('保存血常规检查记录成功')
      
      // 如果是从患者详情页过来，返回患者详情页
      if (patientId.value) {
        router.push(`/patients/detail/${patientId.value}`)
      } else {
        // 否则返回血常规检查列表
        router.push('/bloodtests/list')
      }
    } else {
      ElMessage.error(res.data?.message || '创建血常规检查记录失败')
    }
  } catch (error) {
    console.error('提交表单出错:', error)
    let errorMessage = '操作失败'
    
    if (error.response) {
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

// 返回上一页
const goBack = () => {
  if (patientId.value) {
    router.push(`/patients/detail/${patientId.value}`)
  } else {
    router.push('/bloodtests/list')
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

// AI辅助分析
const handleAIAnalyze = async () => {
  if (!canAnalyze.value) {
    ElMessage.warning('请先填写血常规检查数据并选择患者')
    return
  }

  // 如果是新建记录，先保存再分析
  if (!isEdit.value && !bloodTestId.value) {
    ElMessageBox.confirm(
      '需要先保存血常规检查记录才能进行AI分析，是否继续？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(async () => {
      try {
        submitting.value = true
        
        // 表单验证
        await formRef.value.validate()
        
        // 准备提交的数据，确保类型正确
        const submitData = {
          patientId: Number(form.patientId),
          testDate: form.testDate,
          wbc: form.wbc !== '' ? Number(form.wbc) : null,
          rbc: form.rbc !== '' ? Number(form.rbc) : null,
          hgb: form.hgb !== '' ? Number(form.hgb) : null,
          plt: form.plt !== '' ? Number(form.plt) : null,
          neutp: form.neutp !== '' ? Number(form.neutp) : null,
          lymp: form.lymp !== '' ? Number(form.lymp) : null
        }
        
        // 处理日期格式，从 "YYYY-MM-DD HH:mm:ss" 转换为 "YYYY-MM-DDThh:mm:ss" ISO-8601格式
        if (submitData.testDate && typeof submitData.testDate === 'string') {
          submitData.testDate = submitData.testDate.replace(' ', 'T');
        }
        
        // 创建血常规检查记录
        const res = await createBloodTest(submitData)
        
        if (res.data && res.data.code === 1) {
          ElMessage.success('保存血常规检查记录成功')
          // 获取新创建的记录ID
          const newId = res.data.data.id
          // 更新URL
          router.replace(`/bloodtests/edit/${newId}`)
          // 分析
          await performAnalysis(newId)
        } else {
          ElMessage.error(res.data?.message || '创建血常规检查记录失败')
        }
      } catch (error) {
        console.error('提交表单出错:', error)
        ElMessage.error('保存记录失败，无法进行AI分析')
      } finally {
        submitting.value = false
      }
    }).catch(() => {
      // 用户取消操作
    })
    return
  }
  
  // 直接分析
  await performAnalysis(bloodTestId.value)
}

// 执行AI分析
const performAnalysis = async (id) => {
  loading.value = true
  analyzing.value = true
  try {
    // 请求AI分析
    const res = await analyzeBloodTest({
      bloodTestId: id,
      applyMedicalPrompt: true
    })
    
    if (res.data && res.data.code === 1) {
      ElMessage.success('AI分析完成')
      // 重新获取记录详情，以获取分析结果
      await fetchBloodTestDetail()
      isAnalyzed.value = true
    } else {
      ElMessage.error(res.data?.message || 'AI分析请求失败')
    }
  } catch (error) {
    console.error('AI分析出错:', error)
    let errorMessage = '操作失败'
    
    if (error.response) {
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
  } finally {
    loading.value = false
    analyzing.value = false
  }
}

// 初始化获取数据
onMounted(async () => {
  // 如果是编辑模式则获取血常规检查详情
  if (isEdit.value) {
    await fetchBloodTestDetail()
  } 
  // 如果有患者ID参数，则获取患者信息
  else if (patientId.value) {
    await initPatientInfo(patientId.value)
  }
})
</script>

<style scoped>
.blood-test-form {
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
.blood-test-card,
.analysis-result-card {
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

.reference-range {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
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