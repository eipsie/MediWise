<template>
  <el-dialog
    v-model="dialogVisible"
    title="个人信息"
    width="500px"
    :before-close="handleClose"
    destroy-on-close
  >
    <el-form
      ref="profileFormRef"
      :model="profileForm"
      :rules="rules"
      label-width="100px"
      class="profile-form"
    >
      <el-form-item label="用户名" prop="username">
        <el-input v-model="profileForm.username" disabled />
      </el-form-item>
      
      <el-form-item label="真实姓名" prop="realName">
        <el-input v-model="profileForm.realName" placeholder="请输入真实姓名" />
      </el-form-item>
      
      <el-form-item label="科室" prop="department">
        <el-input v-model="profileForm.department" placeholder="请输入科室" />
      </el-form-item>
      
      <el-form-item label="职称" prop="title">
        <el-input v-model="profileForm.title" placeholder="请输入职称" />
      </el-form-item>
      
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
      </el-form-item>
      
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
      </el-form-item>
    </el-form>
    
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="loading">
          保存
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getCurrentUserProfile, updateUserProfile } from '../../api/user'

// 弹窗可见性
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  userData: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['update:visible', 'refresh'])

// 表单引用
const profileFormRef = ref(null)
// 加载状态
const loading = ref(false)
// 对话框可见状态
const dialogVisible = ref(false)

// 表单数据
const profileForm = reactive({
  username: '',
  realName: '',
  department: '',
  title: '',
  email: '',
  phone: ''
})

// 表单验证规则
const rules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在2到20个字符', trigger: 'blur' }
  ],
  email: [
    { pattern: /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/, message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ]
}

// 监听visible属性变化
watch(() => props.visible, (newVal) => {
  dialogVisible.value = newVal
  if (newVal) {
    fetchUserProfile()
  }
})

// 监听dialogVisible变化
watch(dialogVisible, (newVal) => {
  emit('update:visible', newVal)
})

// 获取用户信息
const fetchUserProfile = async () => {
  loading.value = true
  try {
    const res = await getCurrentUserProfile()
    if (res.data && res.data.code === 1) {
      const userData = res.data.data
      Object.keys(profileForm).forEach(key => {
        if (userData[key] !== undefined) {
          profileForm[key] = userData[key]
        }
      })
    } else {
      ElMessage.error(res.data?.msg || '获取个人信息失败')
    }
  } catch (error) {
    console.error('获取个人信息异常:', error)
    if (error.response && error.response.data) {
      ElMessage.error(error.response.data.msg || '获取个人信息失败')
    } else {
      ElMessage.error('网络异常，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

// 提交表单
const submitForm = async () => {
  if (!profileFormRef.value) return
  
  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 创建一个新对象，只包含后端需要的字段
        const submitData = {
          realName: profileForm.realName,
          department: profileForm.department,
          title: profileForm.title,
          email: profileForm.email,
          phone: profileForm.phone
        }
        
        const res = await updateUserProfile(submitData)
        if (res.data && res.data.code === 1) {
          ElMessage.success('个人信息更新成功')
          emit('refresh')
          handleClose()
        } else {
          ElMessage.error(res.data?.msg || '更新失败')
        }
      } catch (error) {
        console.error('更新个人信息异常:', error)
        // 检查是否有响应数据
        if (error.response && error.response.data) {
          ElMessage.error(error.response.data.msg || '更新个人信息失败')
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
}
</script>

<style scoped>
.profile-form {
  margin: 20px 0;
}
</style> 