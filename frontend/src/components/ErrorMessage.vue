<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'

const props = defineProps<{
  message?: string
  type?: 'error' | 'warning' | 'info' | 'success'
  showIcon?: boolean
  autoClose?: boolean
  duration?: number
  closable?: boolean
}>()

const emit = defineEmits<{
  close: []
}>()

// 默认值
const message = ref(props.message || '发生错误，请稍后重试')
const type = ref(props.type || 'error')
const showIcon = ref(props.showIcon !== false)
const autoClose = ref(props.autoClose !== false)
const duration = ref(props.duration || 3000)
const closable = ref(props.closable !== false)

// 显示状态
const isVisible = ref(true)

// 获取图标类名
const getIconClass = () => {
  const iconMap = {
    error: 'el-icon-error',
    warning: 'el-icon-warning',
    info: 'el-icon-info',
    success: 'el-icon-success'
  }
  return iconMap[type.value]
}

// 获取样式类名
const getTypeClass = () => {
  return `error-message-${type.value}`
}

// 关闭提示
const handleClose = () => {
  isVisible.value = false
  emit('close')
}

// 自动关闭逻辑
watch(autoClose, (newVal) => {
  if (newVal && isVisible.value) {
    setupAutoClose()
  }
})

const setupAutoClose = () => {
  if (autoClose.value) {
    setTimeout(() => {
      handleClose()
    }, duration.value)
  }
}

onMounted(() => {
  setupAutoClose()
})
</script>

<template>
  <div 
    v-if="isVisible" 
    :class="['error-message', getTypeClass()]"
  >
    <div class="message-content">
      <i v-if="showIcon" :class="['icon', getIconClass()]"></i>
      <span class="text">{{ message }}</span>
    </div>
    <button 
      v-if="closable" 
      class="close-btn" 
      @click="handleClose"
      aria-label="关闭"
    >
      <i class="el-icon-close"></i>
    </button>
  </div>
</template>

<style scoped>
.error-message {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
  margin-bottom: 16px;
  border-radius: 4px;
  font-size: 14px;
  line-height: 1.5;
  transition: opacity 0.3s, transform 0.3s;
  max-width: 100%;
  word-wrap: break-word;
}

.message-content {
  display: flex;
  align-items: center;
  flex: 1;
}

.icon {
  margin-right: 10px;
  font-size: 18px;
}

.text {
  flex: 1;
}

.close-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: inherit;
  font-size: 16px;
  padding: 0;
  margin-left: 10px;
  opacity: 0.7;
  transition: opacity 0.3s;
}

.close-btn:hover {
  opacity: 1;
}

/* 错误类型样式 */
.error-message-error {
  background-color: #fef0f0;
  color: #f56c6c;
  border: 1px solid #fbc4c4;
}

.error-message-warning {
  background-color: #fdf6ec;
  color: #e6a23c;
  border: 1px solid #fde2b2;
}

.error-message-info {
  background-color: #edf2fc;
  color: #409eff;
  border: 1px solid #adc6ff;
}

.error-message-success {
  background-color: #f0f9eb;
  color: #67c23a;
  border: 1px solid #b7eb8f;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .error-message {
    padding: 10px 15px;
    font-size: 13px;
  }
  
  .icon {
    font-size: 16px;
  }
}
</style>