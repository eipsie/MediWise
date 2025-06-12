<template>
  <div class="knowledge-manage">
    <h2>知识库管理</h2>
    <p>这是知识库管理页面，仅管理员可见，用于管理医学知识库内容</p>
    
    <el-card class="coming-soon-card">
      <div class="coming-soon">
        <el-icon class="coming-soon-icon"><Management /></el-icon>
        <h3>功能开发中</h3>
        <p>知识库管理功能正在开发中，敬请期待！</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { Management } from '@element-plus/icons-vue'
// 知识库管理页面
</script>

<style scoped>
.knowledge-manage {
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
}

.coming-soon p {
  font-size: 16px;
  color: #606266;
}
</style> 