<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElCard, ElTag, ElButton, ElDivider } from 'element-plus'

const router = useRouter()

const props = defineProps<{
  trip: {
    id: string
    name: string
    destination: string
    startDate: string
    endDate: string
    status: 'upcoming' | 'ongoing' | 'completed'
    budget?: number
    image?: string
    days?: number
  }
  showActions?: boolean
  hoverable?: boolean
}>()

// 默认值
const showActions = computed(() => props.showActions !== false)
const hoverable = computed(() => props.hoverable !== false)

// 计算行程天数
const tripDays = computed(() => {
  if (props.trip.days) return props.trip.days
  
  const start = new Date(props.trip.startDate)
  const end = new Date(props.trip.endDate)
  const diffTime = Math.abs(end.getTime() - start.getTime())
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  return diffDays + 1 // +1 因为包含开始日期
})

// 格式化日期显示
const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

// 获取状态信息
const getStatusInfo = (status: string) => {
  switch (status) {
    case 'upcoming':
      return { text: '即将开始', type: 'primary' as const }
    case 'ongoing':
      return { text: '进行中', type: 'success' as const }
    case 'completed':
      return { text: '已完成', type: 'info' as const }
    default:
      return { text: status, type: 'info' as const } // 将'default'改为'info'
  }
}

// 计算剩余天数（对于即将开始的行程）
const daysRemaining = computed(() => {
  if (props.trip.status !== 'upcoming') return null
  
  const today = new Date()
  const start = new Date(props.trip.startDate)
  const diffTime = start.getTime() - today.getTime()
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  
  if (diffDays < 0) return 0
  return diffDays
})

// 导航到行程详情
const goToTripDetail = () => {
  router.push(`/trip-detail/${props.trip.id}`)
}

// 导航到预算页面
const goToBudget = () => {
  router.push(`/budget/${props.trip.id}`)
}
</script>

<template>
  <el-card
    :class="['trip-card', { 'hoverable': hoverable }]"
    :body-style="{ padding: 0 }"
    @click="goToTripDetail"
  >
    <!-- 行程图片 -->
    <div class="trip-image-container">
      <img
        :src="trip.image || `https://picsum.photos/seed/${trip.id}/600/300`"
        :alt="trip.name"
        class="trip-image"
      />
      <div class="trip-overlay">
        <el-tag :type="getStatusInfo(props.trip.status).type">
          {{ getStatusInfo(props.trip.status).text }}
        </el-tag>
      </div>
    </div>
    
    <!-- 行程信息 -->
    <div class="trip-content">
      <h3 class="trip-name">{{ trip.name }}</h3>
      
      <div class="trip-meta">
        <div class="meta-item">
          <i class="el-icon-location-outline"></i>
          <span>{{ trip.destination }}</span>
        </div>
        
        <div class="meta-item">
          <i class="el-icon-date"></i>
          <span>{{ formatDate(trip.startDate) }} - {{ formatDate(trip.endDate) }}</span>
        </div>
        
        <div class="meta-item">
          <i class="el-icon-time"></i>
          <span>{{ tripDays }}天</span>
        </div>
        
        <div v-if="trip.budget" class="meta-item">
          <i class="el-icon-money"></i>
          <span>预算 ¥{{ trip.budget.toLocaleString() }}</span>
        </div>
      </div>
      
      <el-divider style="margin: 15px 0;"></el-divider>
      
      <!-- 行程状态信息 -->
      <div class="trip-status-info">
        <div v-if="trip.status === 'upcoming' && daysRemaining !== null" class="remaining-days">
          <span v-if="daysRemaining > 0" class="text-primary">
            距离出发还有 {{ daysRemaining }} 天
          </span>
          <span v-else class="text-warning">
            即将出发
          </span>
        </div>
        
        <div v-else-if="trip.status === 'ongoing'" class="ongoing-info">
          <span class="text-success">行程进行中</span>
        </div>
        
        <div v-else class="completed-info">
          <span class="text-info">行程已完成</span>
        </div>
      </div>
      
      <!-- 操作按钮 -->
      <div v-if="showActions" class="trip-actions">
        <el-button type="primary" size="small" @click.stop="goToTripDetail">
          查看详情
        </el-button>
        <el-button type="success" size="small" @click.stop="goToBudget">
          预算管理
        </el-button>
      </div>
    </div>
  </el-card>
</template>

<style scoped>
.trip-card {
  transition: all 0.3s ease;
  border-radius: 8px;
  overflow: hidden;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.trip-card.hoverable {
  cursor: pointer;
}

.trip-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.trip-image-container {
  position: relative;
  height: 180px;
  overflow: hidden;
}

.trip-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.trip-card:hover .trip-image {
  transform: scale(1.05);
}

.trip-overlay {
  position: absolute;
  top: 10px;
  right: 10px;
}

.trip-content {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.trip-name {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 15px 0;
  color: #303133;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.trip-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 10px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
}

.meta-item i {
  font-size: 16px;
  color: #909399;
}

.trip-status-info {
  margin-top: auto;
}

.remaining-days,
.ongoing-info,
.completed-info {
  font-size: 14px;
  font-weight: 500;
  padding: 5px 0;
}

.trip-actions {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}

.trip-actions .el-button {
  flex: 1;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .trip-image-container {
    height: 150px;
  }
  
  .trip-content {
    padding: 15px;
  }
  
  .trip-name {
    font-size: 16px;
  }
  
  .meta-item {
    font-size: 13px;
  }
  
  .trip-actions {
    flex-direction: column;
  }
}

@media (max-width: 480px) {
  .trip-image-container {
    height: 120px;
  }
  
  .trip-meta {
    gap: 5px;
  }
  
  .trip-status-info {
    font-size: 13px;
  }
}
</style>