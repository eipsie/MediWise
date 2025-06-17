<template>
  <div class="user-list">
    <PageHeader title="医生管理" />
    
    <el-card class="user-table-card">
      <template #header>
        <div class="card-header">
          <div class="left">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索医生姓名/用户名"
              class="search-input"
              clearable
              @clear="loadUserList"
              @keyup.enter="loadUserList"
            >
              <template #append>
                <el-button @click="loadUserList">
                  <el-icon><Search /></el-icon>
                </el-button>
              </template>
            </el-input>
          </div>
        </div>
      </template>
      
      <el-table :data="userList" border v-loading="loading" style="width: 100%">
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="department" label="科室" width="120" />
        <el-table-column prop="title" label="职称" width="120" />
        <el-table-column prop="phone" label="电话" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="role" label="角色" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.role === 'ADMIN' ? 'danger' : 'primary'">
              {{ scope.row.role === 'ADMIN' ? '管理员' : '医生' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="200">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button link type="primary" size="small" @click="handleResetPassword(scope.row)" :disabled="scope.row.role === 'ADMIN'">
              重置密码
            </el-button>
            <el-button
              link
              :type="scope.row.status === 1 ? 'warning' : 'success'"
              size="small"
              @click="handleStatusChange(scope.row)"
              :disabled="scope.row.role === 'ADMIN'"
            >
              {{ scope.row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button
              link
              type="danger"
              size="small"
              @click="handleDelete(scope.row)"
              :disabled="scope.row.role === 'ADMIN'"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑用户对话框 -->
    <el-dialog
      v-model="userDialogVisible"
      title="编辑医生信息"
      width="580px"
      destroy-on-close
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userFormRules"
        label-width="80px"
        style="max-width: 500px; margin: 0 auto;"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" disabled />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="userForm.realName" />
        </el-form-item>
        <el-form-item label="科室" prop="department">
          <el-input v-model="userForm.department" />
        </el-form-item>
        <el-form-item label="职称" prop="title">
          <el-input v-model="userForm.title" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="userForm.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="医生" value="DOCTOR" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="userDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitUserForm" :loading="submitLoading">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { 
  getAllUsers, 
  updateUserStatus, 
  resetUserPassword, 
  deleteUser 
} from '../../api/user'

// 搜索关键字
const searchKeyword = ref('')

// 用户列表数据
const userList = ref([])
const loading = ref(false)

// 用户表单相关
const userDialogVisible = ref(false)
const submitLoading = ref(false)
const userFormRef = ref(null)

// 表单数据
const userForm = reactive({
  id: null,
  username: '',
  realName: '',
  department: '',
  title: '',
  phone: '',
  email: '',
  role: 'DOCTOR'
})

// 表单校验规则
const userFormRules = {
  realName: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  department: [
    { required: true, message: '请输入科室', trigger: 'blur' }
  ],
  title: [
    { required: true, message: '请输入职称', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入电话号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

// 加载用户列表
const loadUserList = async () => {
  loading.value = true
  try {
    const response = await getAllUsers()
    if (response.data && response.data.code === 1) {
      userList.value = response.data.data
    } else {
      ElMessage.error(response.data?.message || '加载医生列表失败')
    }
  } catch (error) {
    console.error('加载医生列表出错:', error)
    ElMessage.error('加载医生列表失败')
  } finally {
    loading.value = false
  }
}

// 处理编辑用户
const handleEdit = (row) => {
  // 填充表单数据
  Object.keys(userForm).forEach(key => {
    if (key in row) {
      userForm[key] = row[key]
    }
  })
  userDialogVisible.value = true
}

// 提交用户表单
const submitUserForm = async () => {
  // 表单验证
  await userFormRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        // 编辑用户信息功能
        ElMessage({
          type: 'info',
          message: '编辑用户功能待实现，需要后端开发相应API'
        })
        userDialogVisible.value = false
      } catch (error) {
        console.error('提交表单出错:', error)
        ElMessage.error('操作失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 处理重置密码
const handleResetPassword = (row) => {
  ElMessageBox.confirm(
    `确定要重置 ${row.realName}(${row.username}) 的密码吗？`,
    '重置密码',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await resetUserPassword(row.id)
      if (response.data && response.data.code === 1) {
        ElMessage({
          type: 'success',
          message: `密码已重置为: ${response.data.data}`
        })
      } else {
        ElMessage.error(response.data?.message || '重置密码失败')
      }
    } catch (error) {
      console.error('重置密码出错:', error)
      ElMessage.error('重置密码失败')
    }
  }).catch(() => {})
}

// 处理状态变更
const handleStatusChange = (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const statusText = newStatus === 1 ? '启用' : '禁用'
  
  ElMessageBox.confirm(
    `确定要${statusText} ${row.realName}(${row.username}) 吗？`,
    `${statusText}用户`,
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await updateUserStatus(row.id, { status: newStatus })
      if (response.data && response.data.code === 1) {
        ElMessage.success(`${statusText}成功`)
        row.status = newStatus
      } else {
        ElMessage.error(response.data?.message || `${statusText}失败`)
      }
    } catch (error) {
      console.error(`${statusText}出错:`, error)
      ElMessage.error(`${statusText}失败`)
    }
  }).catch(() => {})
}

// 处理删除用户
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除医生 ${row.realName}(${row.username}) 吗？删除后无法恢复！`,
    '删除医生',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'danger'
    }
  ).then(async () => {
    try {
      const response = await deleteUser(row.id)
      if (response.data && response.data.code === 1) {
        ElMessage.success('删除成功')
        loadUserList() // 重新加载列表
      } else {
        ElMessage.error(response.data?.message || '删除失败')
      }
    } catch (error) {
      console.error('删除用户出错:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 页面加载时获取用户列表
onMounted(() => {
  loadUserList()
})
</script>

<style scoped>
.user-list {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.left {
  flex: 1;
  display: flex;
  align-items: center;
}

.search-input {
  max-width: 300px;
}

.user-table-card {
  margin-top: 20px;
}

:deep(.el-dialog__body) {
  padding-top: 10px;
}
</style> 