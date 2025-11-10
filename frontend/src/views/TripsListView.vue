<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElCard, ElMessageBox, ElTable, ElTableColumn, ElTag, ElButton, ElEmpty, ElSkeleton } from 'element-plus'
import { useUserStore, useTripStore } from '../store'

const router = useRouter()
const userStore = useUserStore()
const tripStore = useTripStore()

const userTrips = ref<any[]>([])
const loading = ref(true)

onMounted(async () => {
  await loadUserTrips()
})

const loadUserTrips = async () => {
  try {
    loading.value = true
    // 获取用户的行程列表
    if (userStore.userInfo && userStore.userInfo.id) {
      console.log('使用用户ID获取行程:', userStore.userInfo.id)
      await tripStore.fetchUserTrips(userStore.userInfo.id)
      userTrips.value = tripStore.trips
    } else {
      console.error('用户信息或用户ID不存在')
      ElMessage.warning('用户信息不完整，无法获取行程')
      userTrips.value = []
    }
  } catch (error: any) {
    console.error('加载行程列表错误:', error)
    if (error?.includes('403')) {
      ElMessage.error('没有权限获取行程信息，请重新登录')
      // 尝试重新获取用户信息
      try {
        await userStore.fetchProfile()
        // 重新尝试获取行程
        if (userStore.userInfo && userStore.userInfo.id) {
          await tripStore.fetchUserTrips(userStore.userInfo.id)
          userTrips.value = tripStore.trips
        }
      } catch (retryError) {
        console.error('重试获取行程失败:', retryError)
      }
    } else {
      ElMessage.error('加载行程列表失败')
    }
  } finally {
    loading.value = false
  }
}

const handleDeleteTrip = async (tripId: string) => {
  try {
    await ElMessageBox.confirm('确定要删除该行程吗？此操作不可恢复。', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 调用API删除行程
    await tripStore.deleteTrip(tripId)
    // 同步更新本地列表
    userTrips.value = userTrips.value.filter(trip => trip.id !== tripId)
    ElMessage.success('行程删除成功')
  } catch (error) {
    // 用户取消删除或其他错误
    console.log('删除行程取消或失败')
  }
}

const goToTripDetail = (tripId: string) => {
  router.push(`/trip/${tripId}`)
}

const goToBudget = (tripId: string) => {
  router.push(`/budget/${tripId}`)
}

// 格式化日期显示
const formatDate = (dateString: string) => {
  if (!dateString) return ''
  try {
    const date = new Date(dateString)
    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    })
  } catch (error) {
    return dateString
  }
}
</script>

<template>
  <div class="trips-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="decoration-circle circle-1"></div>
      <div class="decoration-circle circle-2"></div>
      <div class="floating-element element-1">🗺️</div>
      <div class="floating-element element-2">✈️</div>
      <div class="floating-element element-3">🏨</div>
    </div>

    <div class="container">
      <div class="page-header">
        <div class="header-content">
          <h2 class="page-title">我的行程</h2>
          <p class="page-subtitle">管理您的旅行计划和行程记录</p>
        </div>
        <el-button 
          type="primary" 
          size="large" 
          @click="router.push('/planner')"
          class="create-trip-btn"
        >
          <i class="el-icon-plus"></i>
          创建新行程
        </el-button>
      </div>
      
      <!-- 行程列表卡片 -->
      <el-card shadow="hover" class="trips-card">
        <template #header>
          <div class="card-header">
            <div class="header-left">
              <span class="card-title">行程列表</span>
              <span class="trip-count">共 {{ userTrips.length }} 个行程</span>
            </div>
            <div class="header-actions">
              <el-button 
                text 
                icon="el-icon-refresh" 
                @click="loadUserTrips"
                class="refresh-btn"
              >
                刷新
              </el-button>
            </div>
          </div>
        </template>
        
        <div v-if="loading" class="loading-state">
          <div class="skeleton-container">
            <el-skeleton 
              v-for="i in 3" 
              :key="i" 
              animated 
              class="skeleton-item"
            >
              <template #template>
                <div class="skeleton-row">
                  <el-skeleton-item variant="h3" style="width: 30%" />
                  <el-skeleton-item variant="text" style="width: 20%" />
                  <el-skeleton-item variant="text" style="width: 25%" />
                  <el-skeleton-item variant="text" style="width: 15%" />
                  <el-skeleton-item variant="text" style="width: 10%" />
                </div>
              </template>
            </el-skeleton>
          </div>
        </div>
        
        <div v-else>
          <div class="table-container">
            <el-table 
              :data="userTrips" 
              style="width: 100%"
              class="trips-table"
              empty-text="暂无行程记录"
            >
              <el-table-column prop="title" label="行程标题" min-width="180">
                <template #default="scope">
                  <div class="trip-title-cell">
                    <span class="trip-name" @click="goToTripDetail(scope.row.id)">
                      {{ scope.row.title }}
                    </span>
                  </div>
                </template>
              </el-table-column>
              
              <el-table-column prop="destination" label="目的地" min-width="120" />
              
              <el-table-column label="时间" min-width="200">
                <template #default="scope">
                  <div class="date-cell">
                    <i class="el-icon-date date-icon"></i>
                    <span>{{ formatDate(scope.row.startDate) }} 至 {{ formatDate(scope.row.endDate) }}</span>
                  </div>
                </template>
              </el-table-column>
              
              <el-table-column prop="description" label="行程描述" min-width="220" show-overflow-tooltip />
              
              <el-table-column prop="tags" label="标签" min-width="150">
                <template #default="scope">
                  <div class="tags-cell">
                    <el-tag 
                      v-for="(tag, index) in scope.row.tags" 
                      :key="index" 
                      size="small" 
                      class="custom-tag"
                    >
                      {{ tag }}
                    </el-tag>
                  </div>
                </template>
              </el-table-column>
              
              <el-table-column label="操作" width="200" fixed="right">
                <template #default="scope">
                  <div class="action-buttons">
                    <el-button
                      type="primary"
                      text
                      size="small"
                      @click="goToTripDetail(scope.row.id)"
                      class="action-btn"
                    >
                      <i class="el-icon-view"></i>
                      详情
                    </el-button>
                    <el-button
                      type="success"
                      text
                      size="small"
                      @click="goToBudget(scope.row.id)"
                      class="action-btn"
                    >
                      <i class="el-icon-money"></i>
                      预算
                    </el-button>
                    <el-button
                      type="danger"
                      text
                      size="small"
                      @click="handleDeleteTrip(scope.row.id)"
                      class="action-btn"
                    >
                      <i class="el-icon-delete"></i>
                      删除
                    </el-button>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
          
          <div v-if="userTrips.length === 0" class="empty-trips">
            <el-empty description="暂无行程记录" :image-size="120">
              <el-button 
                type="primary" 
                @click="router.push('/planner')"
                class="create-first-btn"
              >
                创建第一个行程
              </el-button>
            </el-empty>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<style scoped>
.trips-container {
  min-height: calc(100vh - 120px);
  background: linear-gradient(135deg, #f5f7fa 0%, #e4efe9 100%);
  padding: 20px;
  position: relative;
  overflow-x: hidden;
}

/* 背景装饰 */
.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 0;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(102, 126, 234, 0.05);
}

.circle-1 {
  width: 200px;
  height: 200px;
  top: 10%;
  right: 5%;
}

.circle-2 {
  width: 150px;
  height: 150px;
  bottom: 20%;
  left: 8%;
}

.floating-element {
  position: absolute;
  font-size: 2rem;
  animation: float 6s ease-in-out infinite;
}

.element-1 {
  top: 15%;
  left: 5%;
  animation-delay: 0s;
}

.element-2 {
  top: 60%;
  right: 10%;
  animation-delay: 2s;
}

.element-3 {
  bottom: 10%;
  right: 20%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  position: relative;
  z-index: 1;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 30px;
  padding-top: 20px;
}

.header-content {
  flex: 1;
}

.page-title {
  font-size: 32px;
  margin-bottom: 8px;
  color: #303133;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.page-subtitle {
  font-size: 16px;
  color: #909399;
  margin: 0;
}

.create-trip-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 8px;
  padding: 12px 24px;
  font-weight: 500;
  box-shadow: 0 4px 15px rgba(64, 158, 255, 0.3);
  transition: all 0.3s ease;
}

.create-trip-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(64, 158, 255, 0.4);
}

.trips-card {
  background-color: white;
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  margin-bottom: 40px;
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
  background: linear-gradient(90deg, #f8f9fa 0%, #ffffff 100%);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.trip-count {
  font-size: 14px;
  color: #909399;
  background: #f0f2f5;
  padding: 4px 8px;
  border-radius: 4px;
}

.refresh-btn {
  color: #409eff;
  font-weight: 500;
}

.refresh-btn:hover {
  background-color: #f0f7ff;
}

.loading-state {
  padding: 20px;
}

.skeleton-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.skeleton-item {
  padding: 16px 0;
}

.skeleton-row {
  display: flex;
  gap: 20px;
  align-items: center;
}

.table-container {
  padding: 0;
}

.trips-table {
  border: none;
}

.trips-table :deep(.el-table__header) {
  background: #f8f9fa;
}

.trips-table :deep(.el-table__header th) {
  background: #f8f9fa;
  color: #303133;
  font-weight: 600;
  border-bottom: 1px solid #ebeef5;
}

.trips-table :deep(.el-table__row) {
  transition: background-color 0.3s ease;
}

.trips-table :deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}

.trip-title-cell {
  display: flex;
  align-items: center;
}

.trip-name {
  color: #409eff;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
  padding: 4px 8px;
  border-radius: 4px;
}

.trip-name:hover {
  background-color: #f0f7ff;
  text-decoration: none;
  transform: translateX(2px);
}

.date-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #606266;
}

.date-icon {
  color: #909399;
}

.tags-cell {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}

.custom-tag {
  background: linear-gradient(135deg, #f0f7ff 0%, #e6f7ff 100%);
  border: 1px solid #d1e9ff;
  color: #1890ff;
  border-radius: 6px;
  font-size: 12px;
}

.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: flex-start;
}

.action-btn {
  padding: 6px 8px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.action-btn:hover {
  background-color: #f5f7fa;
  transform: translateY(-1px);
}

.empty-trips {
  padding: 60px 0;
  text-align: center;
}

.create-first-btn {
  margin-top: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 8px;
  padding: 10px 20px;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
    gap: 20px;
  }
  
  .header-left {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .action-buttons {
    flex-direction: column;
    gap: 4px;
  }
  
  .skeleton-row {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
  
  .skeleton-row :deep(.el-skeleton__item) {
    width: 100% !important;
  }
}
</style>