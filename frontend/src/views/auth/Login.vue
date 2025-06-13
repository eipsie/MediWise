<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../../api/auth'
import { setToken } from '../../utils/jwt'
import { jwtDecode } from 'jwt-decode'

// 获取路由实例
const router = useRouter()

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: ''
})

// 表单校验规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

// 表单引用
const loginFormRef = ref(null)
// 加载状态
const loading = ref(false)

// 登录方法
const handleLogin = async () => {
  // 表单验证
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        // 调用登录API
        const res = await login(loginForm)
        if (res.data.code === 1) {
          // 设置jwt令牌
          const token = res.data.data.token
          setToken(token)
          
          ElMessage.success('登录成功')
      
          // 解析token获取用户角色
          const decoded = jwtDecode(token)
          const userRole = decoded.role
          
          // 根据角色重定向到不同的首页
          if (userRole === 'ADMIN') {
            await router.push('/admin-dashboard')
          } else {
            await router.push('/dashboard')
          }
        } else {
          ElMessage.error(res.data.msg || '登录失败')
        }
      } catch (error) {
        console.error('登录异常:', error)
        ElMessage.error('登录异常，请稍后重试')
      } finally {
        loading.value = false
      }
    }
  })
}

// 跳转到注册页
const goToRegister = () => {
  router.push('/register')
}
</script>

<template>
  <div class="login-container">
    <div class="login-background">
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

    <!-- 右侧登录表单区域 -->
    <div class="login-section">
      <div class="login-box">
        <div class="login-content">
          <div class="login-title">
            <h3>医生登录</h3>
            <p>欢迎回来，请登录您的账号</p>
          </div>

          <el-form
              ref="loginFormRef"
              :model="loginForm"
              :rules="rules"
              label-position="top"
              class="login-form"
          >
            <el-form-item label="用户名" prop="username">
              <el-input
                  v-model="loginForm.username"
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
                  v-model="loginForm.password"
                  type="password"
                  placeholder="请输入密码"
                  size="large"
                  @keyup.enter="handleLogin"
              >
                <template #prefix>
                  <i class="el-icon-lock">🔒</i>
                </template>
              </el-input>
            </el-form-item>

            <div class="remember-forgot">
              <el-checkbox>记住我</el-checkbox>
              <a href="#" class="forgot-link">忘记密码?</a>
            </div>

            <el-form-item style="margin-top: 20px;">
              <el-button
                  type="primary"
                  :loading="loading"
                  size="large"
                  class="login-button"
                  @click="handleLogin"
              >
                登录
              </el-button>
            </el-form-item>

            <div class="register-link">
              还没有账号？<a href="javascript:void(0)" @click="goToRegister">立即注册</a>
            </div>
          </el-form>
        </div>

        <div class="login-footer">
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

.login-container {
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
.login-background {
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

/* 右侧登录区域 */
.login-section {
  flex: 0 0 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  position: relative;
  z-index: 1;
}

.login-box {
  width: 100%;
  max-width: 400px;
  padding: 60px 40px;
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

.login-content {
  margin-bottom: 30px;
}

.login-title {
  text-align: center;
  margin-bottom: 40px;
}

.login-title h3 {
  font-size: 2rem;
  color: #303133;
  margin-bottom: 10px;
  font-weight: 600;
}

.login-title p {
  font-size: 1rem;
  color: #606266;
  opacity: 0.8;
}

.login-form {
  margin-top: 30px;
}

.login-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #303133;
  margin-top: 7px;
  margin-bottom: 4px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

.remember-forgot {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 25px 0;
}

.forgot-link {
  color: #667eea;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
}

.forgot-link:hover {
  text-decoration: underline;
}

.login-button {
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

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
  background: linear-gradient(135deg, #5a67d8 0%, #6b46c1 100%);
}

.register-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #606266;
}

.register-link a {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
}

.register-link a:hover {
  text-decoration: underline;
}

.login-footer {
  text-align: center;
  color: #909399;
  font-size: 12px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

/* 响应式设计 */
@media (max-width: 968px) {
  .login-container {
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

  .login-section {
    flex: 1;
    padding: 20px;
  }

  .login-box {
    padding: 40px 20px;
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

  .login-section {
    flex: 0 0 auto;
  }

  .login-box {
    padding: 30px 15px;
  }
}
</style>