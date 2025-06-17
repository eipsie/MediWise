<template>
  <div class="logo" :style="{ width: size + 'px', height: size + 'px' }">
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="100%" height="100%">
      <!-- 主体圆形背景 -->
      <circle cx="12" cy="12" r="11" :fill="color" />
      
      <!-- 医疗十字 -->
      <rect x="5" y="11" width="14" height="2" rx="1" fill="white" />
      <rect x="11" y="5" width="2" height="14" rx="1" fill="white" />
      
      <!-- 心电图线 -->
      <path d="M5,9 L7,9 L8,7 L10,12 L12,5 L14,9 L15,9 L16,7 L18,9 L19,9" 
            stroke="white" 
            fill="none" 
            stroke-width="1" 
            stroke-linecap="round" 
            stroke-linejoin="round" />
    </svg>
  </div>
</template>

<script setup>
import { defineProps } from 'vue'

const props = defineProps({
  size: {
    type: Number,
    default: 32
  },
  color: {
    type: String,
    default: '#3498db'
  }
})
</script>

<style scoped>
.logo {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
</style> 