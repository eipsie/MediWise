<template>
  <div class="not-found">
    <h1>404</h1>
    <h2>页面不存在</h2>
    <el-button type="primary" @click="$router.push('/home')">返回首页</el-button>
  </div>
</template>

<script setup>
// 404页面组件
</script>

<style scoped>
.not-found {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 100px 0;
}

h1 {
  font-size: 72px;
  color: #909399;
  margin-bottom: 20px;
}

h2 {
  font-size: 24px;
  color: #606266;
  margin-bottom: 30px;
}
</style> 