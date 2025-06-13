<template>
  <div class="forbidden-page">
    <div class="error-container">
      <div class="error-icon">
        <el-icon class="lock-icon"><Lock /></el-icon>
      </div>
      <div class="error-code">403</div>
      <div class="error-title">访问被拒绝</div>
      <div class="error-message">抱歉，您没有权限访问此页面</div>
      <el-button type="primary" class="back-btn" @click="goBack">返回首页</el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { Lock } from '@element-plus/icons-vue'
import { getUserRole } from '../../utils/jwt'

const router = useRouter()

const goBack = () => {
  const role = getUserRole()
  if (role === 'ADMIN') {
    router.push('/admin-dashboard')
  } else {
    router.push('/dashboard')
  }
}
</script>

<style scoped>
.forbidden-page {
  height: 100vh;
  width: 100vw;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
  z-index: 9999;
}

.error-container {
  text-align: center;
  padding: 50px 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
  width: 90%;
  max-width: 500px;
  animation: fadeIn 0.6s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}

.error-icon {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.lock-icon {
  font-size: 50px;
  color: #f56c6c;
  background-color: #fef0f0;
  padding: 20px;
  border-radius: 50%;
}

.error-code {
  font-size: 120px;
  font-weight: bold;
  color: #f56c6c;
  margin-bottom: 10px;
  line-height: 1.2;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.error-title {
  font-size: 32px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #303133;
}

.error-message {
  font-size: 18px;
  color: #606266;
  margin-bottom: 30px;
}

.back-btn {
  padding: 12px 30px;
  font-size: 16px;
  font-weight: 500;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.3);
  transition: all 0.3s ease;
}

.back-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(64, 158, 255, 0.4);
}
</style>