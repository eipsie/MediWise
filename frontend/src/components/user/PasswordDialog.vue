<template>
  <el-dialog
    v-model="dialogVisible"
    title="修改密码"
    width="500px"
    :before-close="handleClose"
    destroy-on-close
  >
    <el-form
      ref="passwordFormRef"
      :model="passwordForm"
      :rules="rules"
      label-width="120px"
      class="password-form"
    >
      <el-form-item label="当前密码" prop="oldPassword">
        <el-input 
          v-model="passwordForm.oldPassword" 
          type="password" 
          placeholder="请输入当前密码"
          show-password
        />
      </el-form-item>
      
      <el-form-item label="新密码" prop="newPassword">
        <el-input 
          v-model="passwordForm.newPassword" 
          type="password" 
          placeholder="请输入新密码"
          show-password
        />
      </el-form-item>
      
      <el-form-item label="确认新密码" prop="confirmPassword">
        <el-input 
          v-model="passwordForm.confirmPassword" 
          type="password" 
          placeholder="请再次输入新密码"
          show-password
        />
      </el-form-item>
    </el-form>
    
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="loading">
          确认修改
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { changePassword } from '../../api/user'
import { useRouter } from 'vue-router'
import { removeToken } from '../../utils/jwt'

const router = useRouter()

// 弹窗可见性
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:visible'])

// 表单引用
const passwordFormRef = ref(null)
// 加载状态
const loading = ref(false)
// 对话框可见状态
const dialogVisible = ref(false)

// 表单数据
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码匹配验证
const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

// 表单验证规则
const rules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 监听visible属性变化
watch(() => props.visible, (newVal) => {
  dialogVisible.value = newVal
  if (newVal) {
    resetForm()
  }
})

// 监听dialogVisible变化
watch(dialogVisible, (newVal) => {
  emit('update:visible', newVal)
})

// 重置表单
const resetForm = () => {
  if (passwordFormRef.value) {
    passwordFormRef.value.resetFields()
  }
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
}

// 提交表单
const submitForm = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await changePassword({
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        })
        
        if (res.data && res.data.code === 1) {
          ElMessage.success('密码修改成功，请重新登录')
          handleClose()
          
          // 清除token并跳转到登录页
          removeToken()
          setTimeout(() => {
            router.push('/login')
          }, 1500)
        } else {
          ElMessage.error(res.data?.msg || '密码修改失败')
        }
      } catch (error) {
        console.error('密码修改异常:', error)
        if (error.response && error.response.data) {
          ElMessage.error(error.response.data.msg || '密码修改失败')
        } else {
          ElMessage.error('网络异常，请稍后重试')
        }
      } finally {
        loading.value = false
      }
    }
  })
}

// 关闭对话框
const handleClose = () => {
  dialogVisible.value = false
  resetForm()
}
</script>

<style scoped>
.password-form {
  margin: 20px 0;
}
</style> 