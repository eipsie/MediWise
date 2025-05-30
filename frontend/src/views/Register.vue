<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '../api/auth'

// 获取路由实例
const router = useRouter()

// 注册表单数据
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: ''
})

// 表单校验规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ],
  email: [
    { required: false, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

// 表单引用
const registerFormRef = ref(null)
// 加载状态
const loading = ref(false)

// 注册方法
const handleRegister = async () => {
  // 表单验证
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        // 移除确认密码字段，后端不需要
        const registerData = {
          username: registerForm.username,
          password: registerForm.password,
          email: registerForm.email
        }
        // 调用注册API
        const res = await register(registerData)
        if (res.data.code === 1) {
          // 注册成功
          ElMessage.success('注册成功，请登录')
          // 跳转到登录页
          await router.push('/login')
        } else {
          // 注册失败
          ElMessage.error(res.data.msg || '注册失败')
        }
      } catch (error) {
        console.error('注册异常:', error)
        ElMessage.error('注册异常，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}

// 返回登录页
const goToLogin = () => {
  router.push('/login')
}
</script>

<template>
  <div class="register-container">
    <div class="register-background">
      <!-- 添加装饰性元素 -->
      <div class="bg-decoration bg-decoration-1"></div>
      <div class="bg-decoration bg-decoration-2"></div>
      <div class="bg-decoration bg-decoration-3"></div>
    </div>

    <!-- 左侧品牌展示区域 -->
    <div class="brand-section">
      <div class="brand-content">
        <h1 class="brand-title">MediWise</h1>
        <p class="brand-subtitle">智慧医疗诊断系统</p>
        <div class="brand-features">
          <div class="feature-item">
            <div class="feature-icon">🏥</div>
            <p>专业医疗诊断</p>
          </div>
          <div class="feature-item">
            <div class="feature-icon">⚡</div>
            <p>快速智能分析</p>
          </div>
          <div class="feature-item">
            <div class="feature-icon">🔒</div>
            <p>安全数据保护</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧注册表单区域 -->
    <div class="register-section">
      <div class="register-box">
        <div class="register-content">
          <div class="register-title">
            <h3>医生注册</h3>
            <p>创建您的账号，开始使用MediWise</p>
          </div>

          <el-form
              ref="registerFormRef"
              :model="registerForm"
              :rules="rules"
              label-position="top"
              class="register-form"
          >
            <el-form-item label="用户名" prop="username">
              <el-input
                  v-model="registerForm.username"
                  placeholder="请输入用户名"
                  size="large"
              >
                <template #prefix>
                  <i class="el-icon-user">👤</i>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="密码" prop="password">
              <el-input
                  v-model="registerForm.password"
                  type="password"
                  placeholder="请输入密码"
                  size="large"
              >
                <template #prefix>
                  <i class="el-icon-lock">🔒</i>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                  v-model="registerForm.confirmPassword"
                  type="password"
                  placeholder="请再次输入密码"
                  size="large"
              >
                <template #prefix>
                  <i class="el-icon-lock">🔒</i>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="邮箱" prop="email">
              <el-input
                  v-model="registerForm.email"
                  placeholder="请输入邮箱"
                  size="large"
              >
                <template #prefix>
                  <i class="el-icon-message">✉️</i>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item style="margin-top: 20px;">
              <el-button
                  type="primary"
                  :loading="loading"
                  size="large"
                  class="register-button"
                  @click="handleRegister"
              >
                注册
              </el-button>
            </el-form-item>

            <div class="login-link">
              已有账号？<a href="javascript:void(0)" @click="goToLogin">立即登录</a>
            </div>
          </el-form>
        </div>

        <div class="register-footer">
          <p>© {{ new Date().getFullYear() }} MediWise医疗系统 - 版权所有</p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 重置和基础样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.register-container {
  display: flex;
  width: 100%;
  height: 100vh;
  min-height: 600px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
}

/* 背景层 */
.register-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  z-index: 0;
}

/* 背景装饰元素 */
.bg-decoration {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.bg-decoration-1 {
  width: 300px;
  height: 300px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.bg-decoration-2 {
  width: 200px;
  height: 200px;
  bottom: 20%;
  right: 15%;
  animation-delay: 2s;
}

.bg-decoration-3 {
  width: 150px;
  height: 150px;
  top: 60%;
  left: 5%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(10deg); }
}

/* 左侧品牌区域 */
.brand-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  position: relative;
  z-index: 1;
}

.brand-content {
  text-align: center;
  color: white;
  max-width: 500px;
}

.brand-title {
  font-size: 4rem;
  font-weight: 700;
  margin-bottom: 20px;
  background: linear-gradient(45deg, #fff, #f0f8ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
}

.brand-subtitle {
  font-size: 1.5rem;
  margin-bottom: 50px;
  opacity: 0.9;
}

.brand-features {
  display: flex;
  gap: 40px;
  justify-content: center;
  flex-wrap: wrap;
}

.feature-item {
  text-align: center;
  opacity: 0.8;
}

.feature-icon {
  font-size: 2.5rem;
  margin-bottom: 15px;
}

.feature-item p {
  font-size: 1rem;
  font-weight: 500;
}

/* 右侧注册区域 */
.register-section {
  flex: 0 0 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  position: relative;
  z-index: 1;
}

.register-box {
  width: 100%;
  max-width: 400px;
  padding: 40px 40px;
  animation: slideInRight 0.8s ease-out;
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.register-content {
  margin-bottom: 20px;
}

.register-title {
  text-align: center;
  margin-bottom: 30px;
}

.register-title h3 {
  font-size: 2rem;
  color: #303133;
  margin-bottom: 10px;
  font-weight: 600;
}

.register-title p {
  font-size: 1rem;
  color: #606266;
  opacity: 0.8;
}

.register-form {
  margin-top: 20px;
}

.register-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #303133;
  margin-top: 15px;
  margin-bottom: 8px;
}

.register-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}

.register-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.register-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

.login-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #606266;
}

.login-link a {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
}

.login-link a:hover {
  text-decoration: underline;
}

.register-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  margin-top: 5px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: all 0.3s;
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
  background: linear-gradient(135deg, #5a67d8 0%, #6b46c1 100%);
}

.register-footer {
  text-align: center;
  color: #909399;
  font-size: 12px;
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

/* 响应式设计 */
@media (max-width: 968px) {
  .register-container {
    flex-direction: column;
  }

  .brand-section {
    flex: 0 0 auto;
    padding: 40px 20px;
  }

  .brand-title {
    font-size: 2.5rem;
  }

  .brand-features {
    gap: 20px;
  }

  .register-section {
    flex: 1;
    padding: 20px;
  }

  .register-box {
    padding: 30px 20px;
  }
}

@media (max-width: 640px) {
  .brand-section {
    padding: 20px;
  }

  .brand-title {
    font-size: 2rem;
  }

  .brand-subtitle {
    font-size: 1.2rem;
  }

  .brand-features {
    flex-direction: column;
    gap: 15px;
  }

  .register-section {
    flex: 0 0 auto;
  }

  .register-box {
    padding: 30px 15px;
  }
}
</style> 