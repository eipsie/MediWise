<template>
  <div class="patient-form">
    <h2>新增患者</h2>
    <p>这是新增患者页面，内容待开发</p>
    
    <el-card class="coming-soon-card">
      <div class="coming-soon">
        <el-icon class="coming-soon-icon"><Edit /></el-icon>
        <h3>功能开发中</h3>
        <p>患者信息表单功能正在开发中，敬请期待！</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { Edit } from '@element-plus/icons-vue'
// 患者表单页面
</script>

<style scoped>
.patient-form {
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
  color: #409EFF;
  margin-bottom: 20px;
}

.coming-soon h3 {
  font-size: 24px;
  margin-bottom: 10px;
  color: #303133;
}

.coming-soon p {
  font-size: 16px;
  color: #606266;
}
</style> 