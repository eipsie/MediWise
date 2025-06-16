<template>
  <el-card class="base-card" :class="className" :shadow="shadow" v-loading="loading">
    <template #header v-if="title || $slots.header">
      <div class="card-header">
        <div class="header-left">
          <el-icon v-if="icon" class="header-icon">
            <component :is="icon" />
          </el-icon>
          <slot name="icon" v-else-if="$slots.icon"></slot>
          <span class="header-title">{{ title }}</span>
          <slot name="title-suffix"></slot>
        </div>
        <div class="header-right">
          <slot name="header-right"></slot>
        </div>
      </div>
    </template>

    <div class="card-body">
      <slot></slot>
    </div>

    <div class="card-footer" v-if="$slots.footer">
      <slot name="footer"></slot>
    </div>
  </el-card>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    default: ''
  },
  icon: {
    type: Object,
    default: null
  },
  loading: {
    type: Boolean,
    default: false
  },
  shadow: {
    type: String,
    default: 'hover'
  },
  className: {
    type: String,
    default: ''
  }
})
</script>

<style scoped>
.base-card {
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-icon {
  margin-right: 8px;
  font-size: 18px;
}

.header-title {
  font-weight: bold;
  font-size: 16px;
  color: #303133;
}

.card-body {
  padding: 0;
}

.card-footer {
  padding-top: 15px;
  margin-top: 15px;
  border-top: 1px solid #ebeef5;
}

:deep(.el-card__header) {
  padding: 15px 20px;
  border-bottom: 1px solid #ebeef5;
}

:deep(.el-card__body) {
  padding: 20px;
}
</style> 