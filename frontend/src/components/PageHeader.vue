<template>
  <div class="page-header">
    <div class="header-left">
      <el-icon v-if="icon" class="header-icon">
        <component :is="icon" />
      </el-icon>
      <slot name="icon" v-else-if="$slots.icon"></slot>
      <h2 class="header-title">{{ title }}</h2>
      <slot name="title-suffix"></slot>
    </div>
    <div class="header-actions">
      <slot></slot>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true
  },
  icon: {
    type: Object,
    default: null
  }
})
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  background-color: white;
  padding: 15px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
}

.header-left {
  display: flex;
  align-items: center;
}

.header-icon {
  margin-right: 10px;
  font-size: 20px;
  color: var(--el-color-primary);
}

.header-title {
  margin: 0;
  color: #303133;
  font-size: 18px;
  font-weight: 600;
}

.header-actions {
  display: flex;
  gap: 10px;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    padding: 12px 16px;
  }
  
  .header-actions {
    margin-top: 10px;
    width: 100%;
    justify-content: flex-end;
  }
}
</style> 