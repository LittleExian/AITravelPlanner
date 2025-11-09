<script setup lang="ts">
import { ref } from 'vue'

const props = defineProps<{
  size?: number
  color?: string
  text?: string
  fullScreen?: boolean
}>()

// 默认值
const size = ref(props.size || 40)
const color = ref(props.color || '#409eff')
const text = ref(props.text || '加载中...')
const fullScreen = ref(props.fullScreen || false)

// 动态样式
const spinnerStyle = {
  width: `${size.value}px`,
  height: `${size.value}px`,
  borderWidth: `${Math.max(2, size.value / 10)}px`,
  borderColor: `${color.value} transparent transparent transparent`
}

const containerStyle = {
  backgroundColor: fullScreen.value ? 'rgba(255, 255, 255, 0.9)' : 'transparent'
}
</script>

<template>
  <div :class="['loading-spinner', { 'full-screen': fullScreen }]" :style="containerStyle">
    <div class="spinner-wrapper">
      <div class="spinner" :style="spinnerStyle"></div>
      <p v-if="text" class="loading-text">{{ text }}</p>
    </div>
  </div>
</template>

<style scoped>
.loading-spinner {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  position: relative;
}

.loading-spinner.full-screen {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
  width: 100vw;
  height: 100vh;
}

.spinner-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.spinner {
  border: 3px solid #f3f3f3;
  border-top: 3px solid #409eff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.loading-text {
  color: #606266;
  font-size: 14px;
  margin: 0;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .loading-text {
    font-size: 16px;
  }
}
</style>