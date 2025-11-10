<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElButton, ElTabs, ElTabPane, ElCard, ElDivider, ElTag, ElEmpty, ElDialog, ElInput, ElForm, ElFormItem, ElSkeleton } from 'element-plus'
import routeAPI from '../api/routes'
import tripAPI from '../api/trips'

const route = useRoute()
const router = useRouter()

const tripId = route.params.id as string
const trip = ref<any>(null)
const routes = ref<any[]>([])
const loading = ref(true)
const activeTab = ref('timeline')
const activeDayTab = ref('1') // 默认显示第一天

const newTag = ref('')

// 编辑行程对话框
const editDialogVisible = ref(false)
const editForm = ref({
  title: '',
  destination: '',
  startDate: '',
  endDate: '',
  description: '',
  tags: [] as string[],
  budgetAmount: 0,
  peopleCount: 1,
  travelPreferences: [] as string[]
})

// 编辑日程对话框
const editRouteDialogVisible = ref(false)
const editRouteForm = ref({
  id: '',
  tripId: '',
  dayNumber: 1,
  transportation: '',
  attractions: [] as string[],
  restaurants: [] as string[],
  description: '',
  estimatedCost: 0
})
const newAttraction = ref('')
const newRestaurant = ref('')

onMounted(async () => {
  await loadTripData()
})

const loadTripData = async () => {
  loading.value = true
  try {
    // 并行请求行程基本信息和路线数据
    const [tripInfo, routeData] = await Promise.all([
      tripAPI.getTripById(tripId),
      routeAPI.getRoutesByTripId(tripId) || []
    ])
    
    routes.value = routeData
    
    // 直接使用后端数据构造days格式
    const days = routeData.map((route: any) => {
      // 创建活动列表
      const activities: any[] = []
      
      // 添加景点活动
      if (route.attractions && Array.isArray(route.attractions)) {
        route.attractions.forEach((attraction: string) => {
          activities.push({
            type: '景点',
            name: attraction,
            description: '',
            address: ''
          })
        })
      }
      
      // 添加餐厅活动
      if (route.restaurants && Array.isArray(route.restaurants)) {
        route.restaurants.forEach((restaurant: string) => {
          activities.push({
            type: '餐饮',
            name: restaurant,
            description: '',
            address: ''
          })
        })
      }
      
      return {
        dayNumber: route.dayNumber || 1,
        date: route.createdAt ? new Date(route.createdAt).toISOString().split('T')[0] : '',
        activities: activities
      }
    })
    
    // 构造完整的行程数据
    trip.value = {
      id: tripInfo.id,
      name: tripInfo.title || '我的行程',
      destination: tripInfo.destination || '未知目的地',
      startDate: tripInfo.startDate ? new Date(tripInfo.startDate).toISOString().split('T')[0] : new Date().toISOString().split('T')[0],
      endDate: tripInfo.endDate ? new Date(tripInfo.endDate).toISOString().split('T')[0] : new Date().toISOString().split('T')[0],
      budget: tripInfo.budgetAmount || 0,
      peopleCount: tripInfo.peopleCount || 1,
      preferences: tripInfo.travelPreferences || (tripInfo as any).tags || ['旅行'],
      tags: tripInfo.tags || [],
      description: tripInfo.description || '',
      days: days.length > 0 ? days : [{
        dayNumber: 1,
        date: new Date().toISOString().split('T')[0],
        activities: []
      }]
    }
    
  } catch (error) {
    ElMessage.error('加载行程失败')
    console.error('加载行程错误:', error)
    
    // 发生错误时提供默认数据
    trip.value = {
      id: tripId,
      name: '我的行程',
      destination: '未知目的地',
      startDate: new Date().toISOString().split('T')[0],
      endDate: new Date().toISOString().split('T')[0],
      budget: 0,
      peopleCount: 1,
      preferences: ['旅行'],
      tags: [],
      description: '',
      days: [{
        dayNumber: 1,
        date: new Date().toISOString().split('T')[0],
        activities: []
      }]
    }
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
    '购物': 'danger',
    '备注': 'info'
  }
  return colorMap[type] || 'primary'
}

// 获取活动图标
const getActivityIcon = (type: string): string => {
  const iconMap: Record<string, string> = {
    '景点': 'el-icon-view',
    '餐饮': 'el-icon-food',
    '住宿': 'el-icon-house',
    '交通': 'el-icon-truck',
    '购物': 'el-icon-shopping-bag-2',
    '备注': 'el-icon-document'
  }
  return iconMap[type] || 'el-icon-place'
}



// 添加标签
const addTag = () => {
  if (newTag.value && !editForm.value.tags.includes(newTag.value)) {
    editForm.value.tags.push(newTag.value)
    newTag.value = ''
  }
}

// 编辑行程
const editTrip = () => {
  if (trip.value) {
    editForm.value = {
      title: trip.value.name,
      destination: trip.value.destination,
      startDate: trip.value.startDate,
      endDate: trip.value.endDate,
      description: trip.value.description || '',
      tags: trip.value.tags || [],
      budgetAmount: trip.value.budget || 0,
      peopleCount: trip.value.peopleCount || 1,
      travelPreferences: trip.value.preferences || []
    }
    editDialogVisible.value = true
  }
}

// 保存编辑
const saveEdit = async () => {
  try {
    const updateData = {
      title: editForm.value.title,
      destination: editForm.value.destination,
      startDate: editForm.value.startDate,
      endDate: editForm.value.endDate,
      description: editForm.value.description,
      tags: editForm.value.tags,
      budgetAmount: editForm.value.budgetAmount,
      peopleCount: editForm.value.peopleCount,
      travelPreferences: editForm.value.travelPreferences
    }
    
    // 调用API更新行程
    const updatedTrip = await tripAPI.updateTrip(tripId, updateData)
    
    // 更新本地数据
    if (trip.value) {
      trip.value.name = updatedTrip.title
      trip.value.destination = updatedTrip.destination
      trip.value.startDate = new Date(updatedTrip.startDate).toISOString().split('T')[0]
      trip.value.endDate = new Date(updatedTrip.endDate).toISOString().split('T')[0]
      trip.value.description = updatedTrip.description
      trip.value.tags = updatedTrip.tags || []
      trip.value.budget = updatedTrip.budgetAmount || 0
      trip.value.peopleCount = updatedTrip.peopleCount || 1
      trip.value.preferences = updatedTrip.travelPreferences || []
    }
    
    editDialogVisible.value = false
    ElMessage.success('行程已更新')
  } catch (error) {
    console.error('更新行程失败:', error)
    ElMessage.error('更新行程失败，请重试')
  }
}

// 编辑日程
const editRoute = (dayNumber: number) => {
  // 查找对应天数的路线
  const route = routes.value.find(r => r.dayNumber === dayNumber)
  if (route) {
    editRouteForm.value = {
      id: route.id,
      tripId: route.tripId,
      dayNumber: route.dayNumber,
      transportation: route.transportation || '',
      attractions: route.attractions || [],
      restaurants: route.restaurants || [],
      description: route.description || '',
      estimatedCost: route.estimatedCost || 0
    }
  } else {
    // 如果没有找到路线，初始化一个新的
    editRouteForm.value = {
      id: '',
      tripId: tripId,
      dayNumber: dayNumber,
      transportation: '',
      attractions: [],
      restaurants: [],
      description: '',
      estimatedCost: 0
    }
  }
  editRouteDialogVisible.value = true
}

// 保存编辑日程
const saveEditRoute = async () => {
  try {
    const updateData = {
      tripId: editRouteForm.value.tripId,
      dayNumber: editRouteForm.value.dayNumber,
      transportation: editRouteForm.value.transportation,
      attractions: editRouteForm.value.attractions,
      restaurants: editRouteForm.value.restaurants,
      description: editRouteForm.value.description,
      estimatedCost: editRouteForm.value.estimatedCost
    }
    
    let updatedRoute
    if (editRouteForm.value.id) {
      // 更新现有路线
      updatedRoute = await routeAPI.updateRoute(editRouteForm.value.id, updateData)
    } else {
      // 创建新路线
      updatedRoute = await routeAPI.createRoute(updateData)
    }
    
    // 更新本地数据
    const index = routes.value.findIndex(r => r.id === updatedRoute.id)
    if (index >= 0) {
      routes.value[index] = updatedRoute
    } else {
      routes.value.push(updatedRoute)
    }
    
    // 重新加载行程数据以更新天数显示
    await loadTripData()
    
    editRouteDialogVisible.value = false
    ElMessage.success('日程已更新')
  } catch (error) {
    console.error('更新日程失败:', error)
    ElMessage.error('更新日程失败，请重试')
  }
}

// 添加景点
const addAttraction = () => {
  if (newAttraction.value && !editRouteForm.value.attractions.includes(newAttraction.value)) {
    editRouteForm.value.attractions.push(newAttraction.value)
    newAttraction.value = ''
  }
}

// 删除景点
const removeAttraction = (attraction: string) => {
  editRouteForm.value.attractions = editRouteForm.value.attractions.filter(a => a !== attraction)
}

// 添加餐厅
const addRestaurant = () => {
  if (newRestaurant.value && !editRouteForm.value.restaurants.includes(newRestaurant.value)) {
    editRouteForm.value.restaurants.push(newRestaurant.value)
    newRestaurant.value = ''
  }
}

// 删除餐厅
const removeRestaurant = (restaurant: string) => {
  editRouteForm.value.restaurants = editRouteForm.value.restaurants.filter(r => r !== restaurant)
}

// 查看预算
const viewBudget = () => {
  router.push(`/budget/${tripId}`)
}
</script>

<template>
  <div class="trip-detail-container">
    <div class="container">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <div class="skeleton-header">
          <el-skeleton animated style="width: 200px; height: 32px; margin-bottom: 20px;" />
          <el-skeleton animated style="width: 300px; height: 20px;" />
        </div>
        <el-skeleton :rows="5" animated />
      </div>
      
      <!-- 行程内容 -->
      <div v-else-if="trip" class="trip-content">
        <!-- 行程头部 - 移除背景图片 -->
        <!-- 行程头部 - 移除背景图片 -->
        <div class="trip-header">
          <div class="header-content">
            <!-- 返回按钮移到右上方 -->
            <div class="header-top">
              <el-button 
                type="text" 
                icon="el-icon-arrow-left" 
                @click="router.push('/trips')"
                class="back-btn"
              >
                返回我的行程
              </el-button>
            </div>
            
            <div class="trip-main-info">
              <h1 class="trip-title">{{ trip.name }}</h1>
              <p class="trip-description" v-if="trip.description">{{ trip.description }}</p>
              
              <!-- 旅行偏好和行程标签放在顶部 -->
              <div class="trip-tags-top">
                <el-tag 
                  v-for="pref in trip.preferences" 
                  :key="pref" 
                  size="large" 
                  effect="dark"
                  class="preference-tag"
                >
                  {{ pref }}
                </el-tag>
                <el-tag 
                  v-for="tag in trip.tags" 
                  :key="tag" 
                  size="large"
                  class="custom-tag"
                >
                  {{ tag }}
                </el-tag>
              </div>
              
              <div class="trip-meta-grid">
                <div class="meta-item">
                  <i class="el-icon-location-outline"></i>
                  <span>{{ trip.destination }}</span>
                </div>
                <div class="meta-item">
                  <i class="el-icon-date"></i>
                  <span>{{ trip.startDate }} 至 {{ trip.endDate }}</span>
                </div>
                <div class="meta-item">
                  <i class="el-icon-user"></i>
                  <span>{{ trip.peopleCount }}人同行</span>
                </div>
                <div class="meta-item">
                  <i class="el-icon-money"></i>
                  <span>¥{{ trip.budget.toLocaleString() }}</span>
                </div>
              </div>
            </div>
            
            <div class="trip-actions">
              <el-button 
                type="primary" 
                @click="viewBudget" 
                class="action-btn budget-btn"
              >
                <i class="el-icon-money"></i>
                管理预算
              </el-button>
              <el-button 
                @click="editTrip" 
                class="action-btn edit-btn"
              >
                <i class="el-icon-edit"></i>
                编辑行程
              </el-button>

            </div>
          </div>
        </div>
        
        <!-- 行程详情选项卡 -->
        <div class="trip-tabs-section">
          <el-tabs v-model="activeTab" class="trip-tabs">
            <el-tab-pane label="日程安排" name="timeline">
              <div class="timeline-layout">
                <!-- 左侧：景点和餐饮活动 -->
                <div class="itinerary-section">
                  <div class="timeline-container">
                    <!-- 按天展示的tabs -->
                    <el-tabs v-model="activeDayTab" class="day-tabs">
                      <el-tab-pane 
                        v-for="day in trip.days" 
                        :key="day.dayNumber" 
                        :label="`第${day.dayNumber}天`" 
                        :name="day.dayNumber.toString()"
                      >
                        <div class="day-header-tab">
                          <div class="day-header-content">
                            <div class="day-badge">
                              <span class="day-number">第{{ day.dayNumber }}天</span>
                              <span class="day-date">{{ day.date }}</span>
                            </div>
                            <el-button 
                              @click="editRoute(day.dayNumber)" 
                              class="day-edit-btn"
                              size="small"
                            >
                              <i class="el-icon-edit"></i>
                              编辑日程
                            </el-button>
                          </div>
                        </div>
                      
                      <div class="activities-container">
                        <!-- 只显示景点和餐饮活动 -->
                        <div 
                          v-for="(activity, actIndex) in day.activities" 
                          :key="actIndex" 
                          class="activity-item"
                        >
                          <div class="activity-icon">
                            <i :class="getActivityIcon(activity.type)" class="icon"></i>
                          </div>
                          
                          <el-card shadow="hover" class="activity-card">
                            <div class="activity-header">
                              <div class="activity-title">
                                <h4>{{ activity.name }}</h4>
                                <el-tag 
                                  :type="getActivityTypeColor(activity.type)"
                                  class="activity-type-tag"
                                >
                                  {{ activity.type }}
                                </el-tag>
                              </div>
                            </div>
                            
                            <div class="activity-content">
                              <p v-if="activity.description" class="activity-description">
                                {{ activity.description }}
                              </p>
                              <p v-if="activity.address" class="activity-address">
                                <i class="el-icon-location"></i>
                                {{ activity.address }}
                              </p>
                              <div v-if="activity.ticket || activity.budget" class="activity-info">
                                <span v-if="activity.ticket" class="info-item">
                                  <i class="el-icon-ticket"></i>
                                  门票: {{ activity.ticket }}
                                </span>
                                <span v-if="activity.budget" class="info-item">
                                  <i class="el-icon-money"></i>
                                  预算: {{ activity.budget }}
                                </span>
                              </div>
                            </div>
                          </el-card>
                        </div>
                        
                        <div v-if="day.activities.length === 0" class="no-activities">
                          <el-empty description="暂无活动安排" :image-size="80" />
                        </div>
                      </div>
                      </el-tab-pane>
                    </el-tabs>
                  </div>
                </div>

                <!-- 右侧：备注和交通信息 -->
                <div class="sidebar-section">
                  <!-- 备注信息 -->
                  <el-card class="sidebar-card" v-if="routes.find(route => route.dayNumber === parseInt(activeDayTab) && route.description)">
                    <template #header>
                      <div class="card-header">
                        <i class="el-icon-document"></i>
                        <span>行程备注</span>
                      </div>
                    </template>
                    <div class="notes-content">
                      <div 
                        v-for="route in routes.filter(route => route.dayNumber === parseInt(activeDayTab))" 
                        :key="route.id"
                        class="note-item"
                      >
                        <div class="note-day">第{{ route.dayNumber }}天</div>
                        <div class="note-text">{{ route.description }}</div>
                      </div>
                    </div>
                  </el-card>

                  <!-- 交通信息 -->
                  <el-card class="sidebar-card" v-if="routes.find(route => route.dayNumber === parseInt(activeDayTab) && route.transportation)">
                    <template #header>
                      <div class="card-header">
                        <i class="el-icon-truck"></i>
                        <span>交通信息</span>
                      </div>
                    </template>
                    <div class="transport-content">
                      <div 
                        v-for="route in routes.filter(route => route.dayNumber === parseInt(activeDayTab) && route.transportation)" 
                        :key="route.id"
                        class="transport-item"
                      >
                        <div class="transport-day">第{{ route.dayNumber }}天</div>
                        <div class="transport-details">
                          <div class="transport-type">交通方式</div>
                          <div class="transport-info">{{ route.transportation }}</div>
                        </div>
                      </div>
                    </div>
                  </el-card>

                  <!-- 费用估算 -->
                  <el-card class="sidebar-card" v-if="routes.find(route => route.dayNumber === parseInt(activeDayTab) && route.estimatedCost)">
                    <template #header>
                      <div class="card-header">
                        <i class="el-icon-money"></i>
                        <span>费用估算</span>
                      </div>
                    </template>
                    <div class="cost-content">
                      <div 
                        v-for="route in routes.filter(route => route.dayNumber === parseInt(activeDayTab) && route.estimatedCost)" 
                        :key="route.id"
                        class="cost-item"
                      >
                        <div class="cost-day">第{{ route.dayNumber }}天</div>
                        <div class="cost-amount">¥{{ route.estimatedCost }}</div>
                      </div>
                    </div>
                  </el-card>
                </div>
              </div>
            </el-tab-pane>
            
            <el-tab-pane label="地图视图" name="map">
              <div class="map-container">
                <div class="map-placeholder">
                  <i class="el-icon-map-location"></i>
                  <h3>地图功能开发中</h3>
                  <p>即将为您展示行程路线地图</p>
                </div>
              </div>
            </el-tab-pane>
            
            <el-tab-pane label="详细信息" name="info">
              <div class="info-container">
                <el-card class="info-card">
                  <h3 class="info-title">
                    <i class="el-icon-document"></i>
                    行程概览
                  </h3>
                  <el-divider />
                  <div class="info-grid">
                    <div class="info-item">
                      <div class="info-icon">
                        <i class="el-icon-location-outline"></i>
                      </div>
                      <div class="info-content">
                        <span class="info-label">目的地</span>
                        <span class="info-value">{{ trip.destination }}</span>
                      </div>
                    </div>
                    <div class="info-item">
                      <div class="info-icon">
                        <i class="el-icon-date"></i>
                      </div>
                      <div class="info-content">
                        <span class="info-label">旅行时间</span>
                        <span class="info-value">{{ trip.startDate }} 至 {{ trip.endDate }}</span>
                      </div>
                    </div>
                    <div class="info-item">
                      <div class="info-icon">
                        <i class="el-icon-timer"></i>
                      </div>
                      <div class="info-content">
                        <span class="info-label">旅行天数</span>
                        <span class="info-value">{{ trip.days.length }}天</span>
                      </div>
                    </div>
                    <div class="info-item">
                      <div class="info-icon">
                        <i class="el-icon-user"></i>
                      </div>
                      <div class="info-content">
                        <span class="info-label">人数</span>
                        <span class="info-value">{{ trip.peopleCount }}人</span>
                      </div>
                    </div>
                    <div class="info-item">
                      <div class="info-icon">
                        <i class="el-icon-money"></i>
                      </div>
                      <div class="info-content">
                        <span class="info-label">总预算</span>
                        <span class="info-value">¥{{ trip.budget.toLocaleString() }}</span>
                      </div>
                    </div>
                  </div>
                </el-card>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
      
      <!-- 无数据状态 -->
      <div v-else class="no-data">
        <el-empty description="未找到行程信息" :image-size="200">
          <el-button type="primary" @click="router.push('/planner')">创建新行程</el-button>
        </el-empty>
      </div>
    </div>
    

    
    <!-- 编辑日程对话框 -->
    <el-dialog title="编辑日程" v-model="editRouteDialogVisible" width="600px" class="edit-route-dialog">
      <el-form :model="editRouteForm" label-width="100px" :rules="{
        transportation: [{ required: true, message: '请输入交通方式', trigger: 'blur' }],
        estimatedCost: [{ required: true, message: '请输入费用估算', trigger: 'blur' }]
      }">
        <el-form-item label="天数" disabled>
          <el-input v-model="editRouteForm.dayNumber" placeholder="天数" />
        </el-form-item>
        
        <el-form-item label="交通方式" prop="transportation">
          <el-input v-model="editRouteForm.transportation" placeholder="请输入交通方式" />
        </el-form-item>
        
        <el-form-item label="景点">
          <div class="tag-list">
            <el-tag
              v-for="attraction in editRouteForm.attractions"
              :key="attraction"
              closable
              @close="removeAttraction(attraction)"
              class="mr-2 mb-2"
            >
              {{ attraction }}
            </el-tag>
          </div>
          <el-input
            v-model="newAttraction"
            placeholder="输入景点后按回车添加"
            @keyup.enter.native="addAttraction"
            style="margin-top: 10px; width: 200px;"
          />
        </el-form-item>
        
        <el-form-item label="餐厅">
          <div class="tag-list">
            <el-tag
              v-for="restaurant in editRouteForm.restaurants"
              :key="restaurant"
              closable
              @close="removeRestaurant(restaurant)"
              class="mr-2 mb-2"
            >
              {{ restaurant }}
            </el-tag>
          </div>
          <el-input
            v-model="newRestaurant"
            placeholder="输入餐厅后按回车添加"
            @keyup.enter.native="addRestaurant"
            style="margin-top: 10px; width: 200px;"
          />
        </el-form-item>
        
        <el-form-item label="费用估算" prop="estimatedCost">
          <el-input
            v-model.number="editRouteForm.estimatedCost"
            placeholder="请输入费用估算"
            prefix-icon="el-icon-money"
          />
        </el-form-item>
        
        <el-form-item label="行程备注">
          <el-input
            v-model="editRouteForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入行程备注（可选）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editRouteDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEditRoute">保存修改</el-button>
      </template>
    </el-dialog>
    
    <!-- 编辑行程对话框 -->
    <el-dialog title="编辑行程" v-model="editDialogVisible" width="600px" class="edit-dialog">
      <el-form :model="editForm" label-width="100px" :rules="{
        title: [{ required: true, message: '请输入行程名称', trigger: 'blur' }],
        destination: [{ required: true, message: '请输入目的地', trigger: 'blur' }],
        startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
        endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }]
      }">
        <el-form-item label="行程名称" prop="title">
          <el-input v-model="editForm.title" placeholder="请输入行程名称" />
        </el-form-item>
        
        <el-form-item label="目的地" prop="destination">
          <el-input v-model="editForm.destination" placeholder="请输入目的地" />
        </el-form-item>
        
        <div style="display: flex; gap: 20px;">
          <el-form-item label="开始日期" prop="startDate" style="flex: 1;">
            <el-date-picker
              v-model="editForm.startDate"
              type="date"
              placeholder="选择日期"
              style="width: 100%;"
            />
          </el-form-item>
          
          <el-form-item label="结束日期" prop="endDate" style="flex: 1;">
            <el-date-picker
              v-model="editForm.endDate"
              type="date"
              placeholder="选择日期"
              style="width: 100%;"
            />
          </el-form-item>
        </div>
        
        <div style="display: flex; gap: 20px;">
          <el-form-item label="预算金额" style="flex: 1;">
            <el-input
              v-model.number="editForm.budgetAmount"
              placeholder="请输入预算金额"
              prefix-icon="el-icon-money"
            />
          </el-form-item>
          
          <el-form-item label="出行人数" style="flex: 1;">
            <el-input
              v-model.number="editForm.peopleCount"
              placeholder="请输入出行人数"
              prefix-icon="el-icon-user"
            />
          </el-form-item>
        </div>
        
        <el-form-item label="旅行偏好">
          <el-select
            v-model="editForm.travelPreferences"
            multiple
            placeholder="请选择旅行偏好"
            style="width: 100%;"
          >
            <el-option label="博物馆" value="博物馆" />
            <el-option label="美食" value="美食" />
            <el-option label="购物" value="购物" />
            <el-option label="自然风光" value="自然风光" />
            <el-option label="历史古迹" value="历史古迹" />
            <el-option label="文化体验" value="文化体验" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="行程标签">
          <el-tag
            v-for="tag in editForm.tags"
            :key="tag"
            closable
            @close="editForm.tags = editForm.tags.filter(t => t !== tag)"
            class="mr-2 mb-2"
          >
            {{ tag }}
          </el-tag>
          <el-input
            v-model="newTag"
            placeholder="输入标签后按回车添加"
            @keyup.enter.native="addTag"
            style="width: 200px;"
          />
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
        <el-button type="primary" @click="saveEdit">保存修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.trip-detail-container {
  min-height: calc(100vh - 120px);
  background: linear-gradient(135deg, #f5f7fa 0%, #e4efe9 100%);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0;
}

/* 加载状态 */
.loading-state {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  margin: 20px;
}

.skeleton-header {
  margin-bottom: 30px;
}

/* 行程头部 - 移除背景图片 */
.trip-header {
  background: white;
  margin: 20px 20px 24px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  padding: 20px 30px; /* 压缩内边距 */
}

.header-content {
  position: relative;
}

.header-top {
  position: absolute;
  top: 0;
  right: 0;
}

.back-btn {
  color: #606266 !important;
  font-size: 14px;
}

.back-btn:hover {
  background: #f5f7fa;
}

.trip-main-info {
  margin-bottom: 20px; /* 减少底部间距 */
  padding-right: 120px; /* 为返回按钮留出空间 */
}

.trip-title {
  font-size: 28px; /* 稍微减小字体大小 */
  font-weight: 700;
  margin: 0 0 12px 0; /* 减少底部间距 */
  color: #303133;
}

.trip-description {
  font-size: 14px; /* 减小字体大小 */
  margin: 0 0 16px 0; /* 减少底部间距 */
  color: #606266;
  max-width: 600px;
}

/* 顶部标签 */
.trip-tags-top {
  display: flex;
  gap: 8px; /* 减小间距 */
  flex-wrap: wrap;
  margin-bottom: 16px; /* 减少底部间距 */
}

.trip-meta-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); /* 调整最小宽度 */
  gap: 16px; /* 减小间距 */
  max-width: 600px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px; /* 减小间距 */
  font-size: 14px; /* 减小字体大小 */
  color: #606266;
}

.trip-actions {
  display: flex;
  gap: 10px; /* 减小间距 */
  flex-wrap: wrap;
}

.action-btn {
  border-radius: 6px; /* 减小圆角 */
  padding: 10px 16px; /* 减小内边距 */
  font-size: 14px; /* 减小字体大小 */
  font-weight: 500;
  transition: all 0.3s ease;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.budget-btn {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  border: none;
  color: white;
}

.edit-btn {
  background: #f0f2f5;
  border: 1px solid #dcdfe6;
  color: #606266;
}



/* 选项卡区域 */
.trip-tabs-section {
  background: white;
  margin: 0 20px 40px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.trip-tabs {
  padding: 0;
}

.trip-tabs :deep(.el-tabs__header) {
  padding: 0 30px;
  margin: 0;
}

.trip-tabs :deep(.el-tabs__content) {
  padding: 30px;
}

/* 新的布局结构 */
.timeline-layout {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 24px;
  align-items: flex-start;
}

.itinerary-section {
  min-height: 400px;
}

.sidebar-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
  position: sticky;
  top: 20px;
}

.sidebar-card {
  border: none;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: #303133;
}

.card-header i {
  color: #409eff;
}

/* 备注和交通信息样式 */
.notes-content,
.transport-content,
.cost-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.note-item,
.transport-item,
.cost-item {
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.note-day,
.transport-day,
.cost-day {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
  font-weight: 500;
}

.note-text {
  color: #606266;
  line-height: 1.5;
}

.transport-details {
  color: #606266;
}

.transport-type {
  font-weight: 500;
  margin-bottom: 4px;
}

.transport-info {
  font-size: 14px;
  color: #909399;
}

.cost-amount {
  font-size: 16px;
  font-weight: 600;
  color: #67c23a;
}

/* 按天展示的tabs样式 */
.day-tabs {
  margin-bottom: 20px;
}

.day-tabs .el-tabs__header {
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid #f0f0f0;
}

.day-tabs .el-tabs__item {
  font-size: 16px;
  padding: 0 20px;
}

.day-tabs .el-tabs__item.is-active {
  color: #409eff;
  font-weight: 600;
}

.day-tabs .el-tabs__active-bar {
  height: 3px;
  background-color: #409eff;
}

.day-header-tab {
  margin-bottom: 20px;
}

.day-header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.day-edit-btn {
  background: #ecf5ff;
  border: 1px solid #d9ecff;
  color: #409eff;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
}

/* 时间线样式 */
.timeline-container {
  width: 100%;
}

.day-section {
  margin-bottom: 40px;
}

.day-header {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
}

.day-badge {
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
  color: white;
  padding: 12px 20px;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 120px;
}

.day-number {
  font-size: 16px;
  font-weight: 600;
}

.day-date {
  font-size: 14px;
  opacity: 0.9;
}

.day-divider {
  flex: 1;
  height: 2px;
  background: linear-gradient(90deg, #409eff 0%, transparent 100%);
  margin-left: 16px;
}

.activities-container {
  padding-left: 40px;
}

.activity-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 16px;
  position: relative;
}

.activity-item:not(:last-child)::after {
  content: '';
  position: absolute;
  left: 20px;
  top: 40px;
  bottom: -16px;
  width: 2px;
  background: #e4e7ed;
}

.activity-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
  z-index: 1;
}

.activity-icon .icon {
  font-size: 18px;
}

.activity-card {
  flex: 1;
  border: none;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.activity-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.activity-title {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.activity-title h4 {
  margin: 0;
  font-size: 16px;
  color: #303133;
  font-weight: 600;
}

.activity-type-tag {
  border: none;
  border-radius: 12px;
  font-size: 12px;
}

.activity-content {
  padding-left: 0;
}

.activity-description {
  color: #606266;
  margin-bottom: 8px;
  line-height: 1.5;
}

.activity-address {
  color: #909399;
  font-size: 14px;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.activity-info {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #606266;
  font-size: 14px;
}

.no-activities {
  padding: 40px 0;
}

/* 地图容器 */
.map-container {
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.map-placeholder {
  text-align: center;
  color: #909399;
}

.map-placeholder i {
  font-size: 64px;
  color: #409eff;
  margin-bottom: 16px;
}

.map-placeholder h3 {
  margin: 0 0 8px 0;
  color: #303133;
}

.map-placeholder p {
  margin: 0;
  color: #606266;
}

/* 信息容器 */
.info-container {
  display: flex;
  justify-content: center;
}

.info-card {
  width: 100%;
  max-width: 600px;
  border: none;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.info-title {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #303133;
  margin: 0;
}

.info-title i {
  color: #409eff;
}

.info-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.info-item:hover {
  background: #f0f7ff;
  transform: translateX(4px);
}

.info-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.info-icon i {
  font-size: 18px;
}

.info-content {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-label {
  font-weight: 500;
  color: #303133;
}

.info-value {
  color: #606266;
}

/* 无数据状态 */
.no-data {
  background: white;
  padding: 80px 30px;
  margin: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  text-align: center;
}



/* 响应式设计 */
@media (max-width: 768px) {
  .timeline-layout {
    grid-template-columns: 1fr;
  }
  
  .sidebar-section {
    position: static;
  }
  
  .header-content {
    padding: 20px;
  }
  
  .trip-title {
    font-size: 24px;
  }
  
  .trip-meta-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .trip-actions {
    flex-direction: column;
  }
  
  .action-btn {
    width: 100%;
  }
  
  .activities-container {
    padding-left: 20px;
  }
  
  .activity-item {
    flex-direction: column;
    gap: 12px;
  }
  
  .activity-item:not(:last-child)::after {
    left: 20px;
    top: 60px;
    bottom: -12px;
  }
  
  .trip-tabs :deep(.el-tabs__content) {
    padding: 20px;
  }
  
  .trip-tabs :deep(.el-tabs__header) {
    padding: 0 20px;
  }
}
</style>