<template>
  <div class="user-list">
    <h2>用户管理</h2>
    <p>这是用户管理页面，内容待开发</p>
    
    <el-card class="coming-soon-card">
      <div class="coming-soon">
        <el-icon class="coming-soon-icon"><User /></el-icon>
        <h3>功能开发中</h3>
        <p>用户管理功能正在开发中，敬请期待！</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { User } from '@element-plus/icons-vue'
// 用户管理页面
</script>

<style scoped>
.user-list {
  padding: 20px;
}

.coming-soon-card {
  margin-top: 30px;
}

.coming-soon {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  text-align: center;
}

.coming-soon-icon {
  font-size: 48px;
  color: #3f8cda;
  margin-bottom: 20px;
}

.coming-soon h3 {
  font-size: 24px;
  margin-bottom: 10px;
}

.coming-soon p {
  font-size: 16px;
  color: #606266;
}
</style> 