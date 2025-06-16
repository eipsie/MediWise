<template>
  <div class="patient-form">
    <div class="page-header">
      <h2>{{ isEdit ? '编辑患者' : '新增患者' }}</h2>
      <div class="header-actions">
        <el-button @click="goBack">返回列表</el-button>
      </div>
    </div>

    <el-card>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        v-loading="loading"
      >
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入患者姓名" maxlength="50" />
        </el-form-item>

        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="0">女</el-radio>
            <el-radio :label="2">未知</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="出生日期" prop="birthDate">
          <el-date-picker 
            v-model="form.birthDate" 
            type="date" 
            placeholder="选择出生日期"
            value-format="YYYY-MM-DD"
            :disabled-date="disableFutureDates"
          />
        </el-form-item>

        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="form.idCard" placeholder="请输入身份证号" maxlength="18" />
        </el-form-item>

        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" maxlength="20" />
        </el-form-item>

        <el-form-item label="地址" prop="address">
          <el-input 
            v-model="form.address" 
            placeholder="请输入患者地址" 
            maxlength="200" 
            type="textarea" 
            :rows="2"
          />
        </el-form-item>

        <el-form-item label="过敏史" prop="allergies">
          <el-input 
            v-model="form.allergies" 
            placeholder="请输入患者过敏史，如药物过敏等" 
            maxlength="500"
            type="textarea" 
            :rows="2"
          />
        </el-form-item>

        <el-form-item label="既往病史" prop="medicalHistory">
          <el-input 
            v-model="form.medicalHistory" 
            placeholder="请输入患者既往病史" 
            maxlength="1000"
            type="textarea" 
            :rows="3"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="submitting">提交</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPatientById, createPatient, updatePatient } from '../../api/patient'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)

// 患者表单数据
const form = reactive({
  name: '',
  gender: 1,
  birthDate: '',
  idCard: '',
  phone: '',
  address: '',
  allergies: '',
  medicalHistory: ''
})

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '请输入患者姓名', trigger: 'blur' },
    { min: 2, max: 50, message: '姓名长度应为2-50个字符', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  birthDate: [
    { required: true, message: '请选择出生日期', trigger: 'change' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const loading = ref(false)
const submitting = ref(false)
const patientId = computed(() => route.params.id)
const isEdit = computed(() => !!patientId.value)

// 获取患者详情
const fetchPatientDetail = async () => {
  if (!isEdit.value) return
  
  loading.value = true
  try {
    const res = await getPatientById(patientId.value)
    if (res.data && res.data.code === 1) {
      const patientData = res.data.data
      Object.keys(form).forEach(key => {
        if (patientData[key] !== undefined) {
          form[key] = patientData[key]
        }
      })
    } else {
      ElMessage.error(res.data?.message || '获取患者信息失败')
    }
  } catch (error) {
    console.error('获取患者详情出错:', error)
    ElMessage.error('获取患者详情出错')
  } finally {
    loading.value = false
  }
}

// 提交表单
const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      if (isEdit.value) {
        // 编辑患者
        const res = await updatePatient(patientId.value, form)
        if (res.data && res.data.code === 1) {
          ElMessage.success('更新患者信息成功')
          goBack()
        } else {
          ElMessage.error(res.data?.message || '更新患者信息失败')
        }
      } else {
        // 新增患者
        const res = await createPatient(form)
        if (res.data && res.data.code === 1) {
          ElMessage.success('新增患者成功')
          goBack()
        } else {
          ElMessage.error(res.data?.message || '新增患者失败')
        }
      }
    } catch (error) {
      console.error('提交患者表单出错:', error)
      ElMessage.error('提交患者表单出错')
    } finally {
      submitting.value = false
    }
  })
}

// 重置表单
const resetForm = () => {
  if (isEdit.value) {
    fetchPatientDetail()
  } else {
    formRef.value.resetFields()
  }
}

// 返回列表页
const goBack = () => {
  router.push('/patients/list')
}

// 禁用未来日期
const disableFutureDates = (time) => {
  return time.getTime() > Date.now()
}

// 初始化
onMounted(() => {
  if (isEdit.value) {
    fetchPatientDetail()
  }
})
</script>

<style scoped>
.patient-form {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style> 