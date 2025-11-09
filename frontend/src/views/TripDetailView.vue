<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElButton, ElTabs, ElTabPane, ElCard, ElDivider, ElTag, ElEmpty, ElDialog, ElInput, ElForm, ElFormItem } from 'element-plus'
// import { useTripStore } from '../store'

const route = useRoute()
const router = useRouter()
// const tripStore = useTripStore()

const tripId = route.params.id as string
const trip = ref<any>(null)
const loading = ref(true)
const activeTab = ref('timeline')

// 分享对话框
const shareDialogVisible = ref(false)
const shareLink = ref('')

// 编辑行程对话框
const editDialogVisible = ref(false)
const editForm = ref({
  name: '',
  description: ''
})

onMounted(async () => {
  // 加载行程数据
  await loadTripData()
})

const loadTripData = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 模拟行程数据
    trip.value = {
      id: tripId,
      name: '北京三日游',
      destination: '北京',
      startDate: '2025-12-01',
      endDate: '2025-12-03',
      budget: 3000,
      peopleCount: 2,
      preferences: ['美食', '文化', '历史'],
      days: [
        {
          date: '2025-12-01',
          activities: [
            {
              time: '09:00',
              type: '景点',
              name: '天安门广场',
              description: '世界上最大的城市广场，参观升旗仪式',
              address: '北京市东城区东长安街'
            },
            {
              time: '11:00',
              type: '景点',
              name: '故宫博物院',
              description: '世界文化遗产，中国明清两代的皇家宫殿',
              address: '北京市东城区景山前街4号',
              ticket: '60元'
            },
            {
              time: '17:00',
              type: '餐饮',
              name: '全聚德烤鸭店',
              description: '百年老字号，品尝正宗北京烤鸭',
              address: '北京市东城区前门大街30号',
              budget: '人均200元'
            },
            {
              time: '19:00',
              type: '住宿',
              name: '北京王府井希尔顿酒店',
              description: '五星级酒店，位置便利',
              address: '北京市东城区王府井东街8号'
            }
          ]
        },
        {
          date: '2025-12-02',
          activities: [
            {
              time: '09:00',
              type: '景点',
              name: '长城',
              description: '不到长城非好汉',
              address: '北京市延庆区八达岭镇'
            },
            {
              time: '15:00',
              type: '景点',
              name: '颐和园',
              description: '皇家园林博物馆',
              address: '北京市海淀区新建宫门路19号'
            }
          ]
        },
        {
          date: '2025-12-03',
          activities: [
            {
              time: '09:00',
              type: '景点',
              name: '天坛',
              description: '明清两代皇帝祭天的场所',
              address: '北京市东城区天坛内东里7号'
            },
            {
              time: '14:00',
              type: '购物',
              name: '王府井大街',
              description: '北京著名商业街',
              address: '北京市东城区王府井大街'
            }
          ]
        }
      ]
    }
  } catch (error) {
    ElMessage.error('加载行程失败')
    console.error('加载行程错误:', error)
  } finally {
    loading.value = false
  }
}

// 获取活动类型对应的标签颜色
const getActivityTypeColor = (type: string): 'primary' | 'success' | 'warning' | 'info' | 'danger' => {
  const colorMap: Record<string, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = {
    '景点': 'primary',
    '餐饮': 'success',
    '住宿': 'warning',
    '交通': 'info',
    '购物': 'danger'
  }
  return colorMap[type] || 'primary'
}

// 分享行程
const shareTrip = () => {
  shareLink.value = `${window.location.origin}/trip/${tripId}?share=true`
  shareDialogVisible.value = true
}

// 复制分享链接
const copyShareLink = () => {
  navigator.clipboard.writeText(shareLink.value)
    .then(() => {
      ElMessage.success('链接已复制')
    })
    .catch(() => {
      ElMessage.error('复制失败')
    })
}

// 编辑行程
const editTrip = () => {
  if (trip.value) {
    editForm.value = {
      name: trip.value.name,
      description: trip.value.description || ''
    }
    editDialogVisible.value = true
  }
}

// 保存编辑
const saveEdit = () => {
  if (trip.value) {
    trip.value.name = editForm.value.name
    trip.value.description = editForm.value.description
    editDialogVisible.value = false
    ElMessage.success('行程已更新')
  }
}

// 查看预算
const viewBudget = () => {
  router.push(`/budget/${tripId}`)
}
</script>

<template>
  <div class="trip-detail-container">
    <div class="container">
      <div v-if="loading" class="loading-state">
        <el-skeleton :rows="5" animated />
      </div>
      
      <div v-else-if="trip" class="trip-content">
        <!-- 行程标题和操作 -->
        <div class="trip-header">
          <div class="trip-title">
            <h1>{{ trip.name }}</h1>
            <div class="trip-meta">
              <el-tag>{{ trip.destination }}</el-tag>
              <span>{{ trip.startDate }} 至 {{ trip.endDate }}</span>
              <span>{{ trip.peopleCount }}人同行</span>
              <span>预算: ¥{{ trip.budget }}</span>
            </div>
          </div>
          
          <div class="trip-actions">
            <el-button type="primary" @click="viewBudget">管理预算</el-button>
            <el-button @click="editTrip">编辑行程</el-button>
            <el-button @click="shareTrip">分享行程</el-button>
          </div>
        </div>
        
        <!-- 行程标签 -->
        <div class="trip-tags">
          <h3>旅行偏好</h3>
          <div class="tags">
            <el-tag v-for="pref in trip.preferences" :key="pref" size="large" effect="dark">
              {{ pref }}
            </el-tag>
          </div>
        </div>
        
        <!-- 行程详情选项卡 -->
        <el-tabs v-model="activeTab" class="trip-tabs">
          <el-tab-pane label="日程安排" name="timeline">
            <div class="timeline-container">
              <div v-for="(day, index) in trip.days" :key="index" class="day-section">
                <h3>第{{ index + 1 }}天 ({{ day.date }})</h3>
                
                <div class="activities">
                  <el-card
                    v-for="(activity, actIndex) in day.activities"
                    :key="actIndex"
                    shadow="hover"
                    class="activity-card"
                  >
                    <div class="activity-header">
                      <div class="activity-time">{{ activity.time }}</div>
                      <div class="activity-title">
                        <h4>{{ activity.name }}</h4>
                        <el-tag :type="getActivityTypeColor(activity.type)">
                          {{ activity.type }}
                        </el-tag>
                      </div>
                    </div>
                    
                    <div class="activity-content">
                      <p class="activity-description">{{ activity.description }}</p>
                      <p class="activity-address">{{ activity.address }}</p>
                      <div v-if="activity.ticket" class="activity-info">
                        <span>门票: {{ activity.ticket }}</span>
                      </div>
                      <div v-if="activity.budget" class="activity-info">
                        <span>预算: {{ activity.budget }}</span>
                      </div>
                    </div>
                  </el-card>
                </div>
              </div>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="地图视图" name="map">
            <div class="map-container">
              <el-empty description="地图功能开发中" :image-size="200" />
              <!-- 实际项目中这里会集成地图API -->
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="详细信息" name="info">
            <div class="info-container">
              <el-card>
                <h3>行程概览</h3>
                <el-divider />
                <div class="info-item">
                  <span class="info-label">目的地:</span>
                  <span class="info-value">{{ trip.destination }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">旅行时间:</span>
                  <span class="info-value">{{ trip.startDate }} 至 {{ trip.endDate }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">旅行天数:</span>
                  <span class="info-value">{{ trip.days.length }}天</span>
                </div>
                <div class="info-item">
                  <span class="info-label">人数:</span>
                  <span class="info-value">{{ trip.peopleCount }}人</span>
                </div>
                <div class="info-item">
                  <span class="info-label">总预算:</span>
                  <span class="info-value">¥{{ trip.budget }}</span>
                </div>
              </el-card>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
      
      <div v-else class="no-data">
        <el-empty description="未找到行程信息" />
        <el-button type="primary" @click="router.push('/planner')">创建新行程</el-button>
      </div>
    </div>
    
    <!-- 分享对话框 -->
    <el-dialog title="分享行程" v-model="shareDialogVisible" width="400px">
      <div class="share-dialog-content">
        <el-input v-model="shareLink" readonly placeholder="分享链接" />
        <el-button type="primary" @click="copyShareLink">复制链接</el-button>
      </div>
    </el-dialog>
    
    <!-- 编辑对话框 -->
    <el-dialog title="编辑行程" v-model="editDialogVisible" width="500px">
      <el-form :model="editForm">
        <el-form-item label="行程名称">
          <el-input v-model="editForm.name" placeholder="请输入行程名称" />
        </el-form-item>
        <el-form-item label="行程描述">
          <el-input
            v-model="editForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入行程描述（可选）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.trip-detail-container {
  min-height: calc(100vh - 120px);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.loading-state {
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.trip-header {
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.trip-title h1 {
  font-size: 32px;
  margin-bottom: 10px;
}

.trip-meta {
  display: flex;
  align-items: center;
  gap: 20px;
  color: #606266;
}

.trip-actions {
  display: flex;
  gap: 10px;
}

.trip-tags {
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.tags {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-top: 10px;
}

.trip-tabs {
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.day-section {
  margin-bottom: 40px;
}

.day-section h3 {
  font-size: 24px;
  margin-bottom: 20px;
  color: #303133;
}

.activities {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.activity-card {
  position: relative;
  padding-left: 20px;
}

.activity-card:not(:last-child)::before {
  content: '';
  position: absolute;
  left: 8px;
  top: 100%;
  width: 2px;
  height: calc(100% + 15px);
  background-color: #dcdfe6;
}

.activity-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 15px;
}

.activity-time {
  font-size: 18px;
  font-weight: 600;
  color: #409eff;
  min-width: 60px;
}

.activity-title {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.activity-title h4 {
  margin: 0;
  font-size: 18px;
}

.activity-content {
  padding-left: 80px;
}

.activity-description {
  margin-bottom: 10px;
  color: #606266;
}

.activity-address {
  margin-bottom: 10px;
  color: #909399;
  font-size: 14px;
}

.activity-info {
  display: flex;
  gap: 20px;
  color: #606266;
  font-size: 14px;
}

.map-container,
.info-container {
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.info-container .el-card {
  width: 100%;
  max-width: 600px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  font-weight: 500;
  color: #303133;
}

.info-value {
  color: #606266;
}

.no-data {
  background: white;
  padding: 60px 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  text-align: center;
}

.share-dialog-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
</style>