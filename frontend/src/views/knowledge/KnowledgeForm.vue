<template>
  <div class="knowledge-form">
    <h2>{{ isEdit ? '编辑知识' : '添加知识' }}</h2>
    <p>这是知识库{{ isEdit ? '编辑' : '添加' }}页面，仅管理员可见，用于{{ isEdit ? '编辑' : '添加' }}医学知识库内容</p>
    
    <el-card class="coming-soon-card">
      <div class="coming-soon">
        <el-icon class="coming-soon-icon"><Edit /></el-icon>
        <h3>功能开发中</h3>
        <p>知识库{{ isEdit ? '编辑' : '添加' }}功能正在开发中，敬请期待！</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { Edit } from '@element-plus/icons-vue'
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
// 判断是编辑还是添加
const isEdit = computed(() => {
  return route.name === 'KnowledgeEdit'
})
</script>

<style scoped>
.knowledge-form {
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