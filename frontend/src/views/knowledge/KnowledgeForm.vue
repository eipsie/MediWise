<template>
  <div class="knowledge-form">
    <div class="page-header">
      <h2>{{ isEdit ? '编辑知识' : '添加知识' }}</h2>
      <div class="header-actions">
        <el-button @click="goBack">返回列表</el-button>
      </div>
    </div>
    
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      class="knowledge-form-content"
      v-loading="loading"
    >
      <el-card class="form-card">
        <template #header>
          <div class="card-header">
            <span>基本信息</span>
          </div>
        </template>
        
        <el-form-item label="条目类型" prop="entryType">
          <el-radio-group v-model="form.entryType">
            <el-radio label="DISEASE">疾病</el-radio>
            <el-radio label="DRUG">药品</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        
        <el-form-item label="分类" prop="category">
          <el-input v-model="form.category" placeholder="请输入分类" />
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item v-if="form.entryType === 'DISEASE'" label="疾病名称" prop="diseaseName">
          <el-input v-model="form.diseaseName" placeholder="请输入疾病名称" />
        </el-form-item>
        
        <el-form-item v-if="form.entryType === 'DISEASE'" label="疾病编码" prop="diseaseCode">
          <el-input v-model="form.diseaseCode" placeholder="请输入疾病编码，例如 ICD-10 编码" />
        </el-form-item>
        
        <el-form-item v-if="form.entryType === 'DRUG'" label="药品名称" prop="drugName">
          <el-input v-model="form.drugName" placeholder="请输入药品名称" />
        </el-form-item>
      </el-card>
      
      <el-card class="form-card">
        <template #header>
          <div class="card-header">
            <span>详细描述</span>
          </div>
        </template>
        
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="6"
            placeholder="请输入详细描述"
          />
        </el-form-item>
        
        <el-form-item v-if="form.entryType === 'DISEASE'" label="相关症状" prop="symptoms">
          <el-input
            v-model="form.symptoms"
            type="textarea"
            :rows="4"
            placeholder="请输入相关症状，使用JSON数组格式，例如: ['头痛', '发热']"
          />
        </el-form-item>
        
        <el-form-item label="治疗指南" prop="treatmentGuide">
          <el-input
            v-model="form.treatmentGuide"
            type="textarea"
            :rows="6"
            placeholder="请输入治疗指南或用药建议"
          />
        </el-form-item>
      </el-card>
      
      <div class="form-actions">
        <el-button @click="goBack">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          {{ isEdit ? '更新' : '提交' }}
        </el-button>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getKnowledgeDetail, createKnowledge, updateKnowledge } from '../../api/knowledge'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const submitLoading = ref(false)

// 判断是编辑还是添加模式
const isEdit = computed(() => {
  return route.name === 'KnowledgeEdit'
})

// 获取ID参数
const knowledgeId = computed(() => {
  return route.params.id
})

// 表单数据
const form = reactive({
  entryType: 'DISEASE', // 默认选择疾病类型
  title: '',
  category: '',
  description: '',
  diseaseName: '',
  diseaseCode: '',
  drugName: '',
  symptoms: '',
  treatmentGuide: '',
  status: 1 // 默认启用
})

// 表单验证规则
const rules = {
  entryType: [
    { required: true, message: '请选择条目类型', trigger: 'change' }
  ],
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在2到100个字符', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请输入分类', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入描述', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 获取知识详情
const fetchKnowledgeDetail = async (id) => {
  loading.value = true
  try {
    const res = await getKnowledgeDetail(id)
    if (res.data && res.data.code === 1) {
      const detail = res.data.data
      
      // 填充表单数据
      Object.keys(form).forEach(key => {
        if (detail[key] !== undefined) {
          form[key] = detail[key]
        }
      })
    } else {
      ElMessage.error(res.data?.msg || '获取知识详情失败')
    }
  } catch (error) {
    console.error('获取知识详情出错:', error)
    ElMessage.error('获取知识详情失败')
  } finally {
    loading.value = false
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    try {
      // 根据编辑或添加模式选择不同API
      const api = isEdit.value
        ? () => updateKnowledge(knowledgeId.value, form)
        : () => createKnowledge(form)
      
      const res = await api()
      
      if (res.data && res.data.code === 1) {
        ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
        goBack()
      } else {
        ElMessage.error(res.data?.msg || (isEdit.value ? '更新失败' : '添加失败'))
      }
    } catch (error) {
      console.error(isEdit.value ? '更新知识出错:' : '添加知识出错:', error)
      ElMessage.error(isEdit.value ? '更新失败' : '添加失败')
    } finally {
      submitLoading.value = false
    }
  })
}

// 返回列表页
const goBack = () => {
  router.push('/knowledge/manage')
}

// 组件挂载时，如果是编辑模式则获取详情
onMounted(() => {
  if (isEdit.value && knowledgeId.value) {
    fetchKnowledgeDetail(knowledgeId.value)
  }
})
</script>

<style scoped>
.knowledge-form {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
}

.knowledge-form-content {
  margin-bottom: 30px;
}

.form-card {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  align-items: center;
  font-weight: bold;
  font-size: 16px;
}

.form-actions {
  margin-top: 30px;
  display: flex;
  justify-content: center;
  gap: 20px;
}
</style> 