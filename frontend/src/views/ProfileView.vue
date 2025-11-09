<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElInput, ElButton, ElForm, ElFormItem, ElCard, ElAvatar, ElSwitch, ElMessageBox } from 'element-plus'
import { useUserStore, useTripStore } from '../store'

const router = useRouter()
const userStore = useUserStore()
const tripStore = useTripStore()

const userInfo = reactive({
  username: '',
  nickname: '',
  email: '',
  phone: '',
  avatar: '',
  bio: '',
  preferences: {
    notifications: true,
    autoSync: true,
    darkMode: false
  }
})

const userTrips = ref<any[]>([])
const loading = ref(true)
const editMode = ref(false)
const changePasswordDialogVisible = ref(false)

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

onMounted(async () => {
  await loadUserData()
  await loadUserTrips()
})

const loadUserData = async () => {
  try {
    // 在实际项目中，这里会从API获取用户信息
    // 模拟用户数据
    const mockUser = {  // 直接使用模拟数据，避免访问不存在的属性
      id: '1',
      username: 'traveler123',
      nickname: '旅行爱好者',
      email: 'traveler@example.com',
      phone: '13800138000',
      avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
      bio: '热爱旅行，探索世界的每一个角落',
      preferences: {
        notifications: true,
        autoSync: true,
        darkMode: false
      }
    }
    
    Object.assign(userInfo, mockUser)
  } catch (error) {
    ElMessage.error('加载用户信息失败')
    console.error('加载用户信息错误:', error)
  }
}

const loadUserTrips = async () => {
  try {
    loading.value = true
    // 获取用户的行程列表
    if (userStore.userInfo) {
      await tripStore.fetchUserTrips(userStore.userInfo.id)
    }
    userTrips.value = tripStore.trips
    
    // 如果没有行程数据，显示模拟数据
    if (userTrips.value.length === 0) {
      userTrips.value = [
        {
          id: '1',
          name: '北京三日游',
          destination: '北京',
          startDate: '2024-10-01',
          endDate: '2024-10-03',
          status: 'completed'
        },
        {
          id: '2',
          name: '三亚度假之旅',
          destination: '三亚',
          startDate: '2024-12-20',
          endDate: '2024-12-25',
          status: 'upcoming'
        }
      ]
    }
  } catch (error) {
    ElMessage.error('加载行程列表失败')
    console.error('加载行程列表错误:', error)
  } finally {
    loading.value = false
  }
}

const handleUpdateProfile = async () => {
  try {
    // 在实际项目中，这里会调用API更新用户信息
    // 模拟更新成功
    if (userStore.userInfo) {
      // 创建一个符合UserResponse类型的对象
      const updatedUser = {
        ...userStore.userInfo,
        username: userInfo.username,
        email: userInfo.email
      }
      userStore.setUserInfo(updatedUser)
    }
    editMode.value = false
    ElMessage.success('个人信息更新成功')
  } catch (error) {
    ElMessage.error('更新个人信息失败')
    console.error('更新个人信息错误:', error)
  }
}

const handleChangePassword = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.warning('两次输入的新密码不一致')
    return
  }
  
  try {
    // 在实际项目中，这里会调用API修改密码
    // 模拟修改成功
    ElMessage.success('密码修改成功')
    changePasswordDialogVisible.value = false
    
    // 重置表单
    Object.assign(passwordForm, {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    })
  } catch (error) {
    ElMessage.error('密码修改失败')
    console.error('密码修改错误:', error)
  }
}

const handleDeleteTrip = async (tripId: string) => {
  try {
    await ElMessageBox.confirm('确定要删除该行程吗？此操作不可恢复。', '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 在实际项目中，这里会调用API删除行程
    // await tripStore.deleteTrip(tripId)
    userTrips.value = userTrips.value.filter(trip => trip.id !== tripId)
    ElMessage.success('行程删除成功')
  } catch (error) {
    // 用户取消删除或其他错误
    console.log('删除行程取消或失败')
  }
}

const goToTripDetail = (tripId: string) => {
  router.push(`/trip-detail/${tripId}`)
}

const goToBudget = (tripId: string) => {
  router.push(`/budget/${tripId}`)
}

const handleAvatarUpload = (_uploadFile: any) => {
  // 模拟上传成功
  const mockUrl = `https://cube.elemecdn.com/${Math.floor(Math.random() * 10)}/88/03b0d39583f48206768a7534e55bcpng.png`
  userInfo.avatar = mockUrl
  ElMessage.success('头像上传成功')
  return false // 阻止默认上传行为
}

const logout = () => {
  userStore.logout()
  router.push('/login')
  ElMessage.success('已成功退出登录')
}

const getTripStatusText = (status: string) => {
  const statusMap = {
    upcoming: '即将开始',
    ongoing: '进行中',
    completed: '已完成'
  }
  return statusMap[status as keyof typeof statusMap] || status
}

const getTripStatusColor = (status: string) => {
  const colorMap = {
    upcoming: 'primary',
    ongoing: 'success',
    completed: 'info'
  }
  return colorMap[status as keyof typeof colorMap] || 'default'
}

// 添加深色模式切换处理方法
const handleDarkModeToggle = () => {
  try {
    // 只在浏览器环境中执行
    if (typeof window !== 'undefined' && document) {
      if (userInfo.preferences.darkMode) {
        document.documentElement.classList.add('dark')
      } else {
        document.documentElement.classList.remove('dark')
      }
      // 更新用户配置
      handleUpdateProfile()
    }
  } catch (error) {
    console.error('切换深色模式失败:', error)
  }
}
</script>

<template>
  <div class="profile-container">
    <div class="container">
      <h2 class="page-title">个人中心</h2>
      
      <!-- 个人信息卡片 -->
      <el-card shadow="hover" class="profile-card">
        <div class="profile-header">
          <!-- 头像区域 -->
          <div class="avatar-section">
            <el-avatar :src="userInfo.avatar" size="large" class="user-avatar">
              {{ userInfo.username?.charAt(0) || '游' }}
            </el-avatar>
            
            <el-upload
              v-if="editMode"
              class="avatar-uploader"
              :show-file-list="false"
              :before-upload="handleAvatarUpload"
            >
              <el-button type="primary" icon="el-icon-upload" size="small">
                更换头像
              </el-button>
            </el-upload>
          </div>
          
          <!-- 用户基本信息 -->
          <div class="user-info">
            <div class="info-row">
              <h3>{{ userInfo.nickname }}</h3>
              <el-button
                v-if="!editMode"
                type="primary"
                @click="editMode = true"
                size="small"
              >
                编辑资料
              </el-button>
              <el-button
                v-if="!editMode"
                @click="changePasswordDialogVisible = true"
                size="small"
              >
                修改密码
              </el-button>
            </div>
            
            <div class="info-content">
              <el-form :model="userInfo" label-width="80px">
                <el-form-item label="用户名">
                  <el-input
                    v-model="userInfo.username"
                    :disabled="!editMode"
                    placeholder="用户名"
                  />
                </el-form-item>
                
                <el-form-item label="昵称">
                  <el-input
                    v-model="userInfo.nickname"
                    :disabled="!editMode"
                    placeholder="昵称"
                  />
                </el-form-item>
                
                <el-form-item label="邮箱">
                  <el-input
                    v-model="userInfo.email"
                    :disabled="!editMode"
                    type="email"
                    placeholder="邮箱"
                  />
                </el-form-item>
                
                <el-form-item label="手机号">
                  <el-input
                    v-model="userInfo.phone"
                    :disabled="!editMode"
                    placeholder="手机号"
                  />
                </el-form-item>
                
                <el-form-item label="个人简介">
                  <el-input
                    v-model="userInfo.bio"
                    :disabled="!editMode"
                    type="textarea"
                    placeholder="个人简介"
                    :rows="2"
                  />
                </el-form-item>
              </el-form>
            </div>
          </div>
        </div>
        
        <!-- 保存/取消按钮 -->
        <div v-if="editMode" class="action-buttons">
          <el-button type="primary" @click="handleUpdateProfile">
            保存修改
          </el-button>
          <el-button @click="editMode = false; loadUserData()">
            取消
          </el-button>
        </div>
      </el-card>
      
      <!-- 偏好设置卡片 -->
      <el-card shadow="hover" class="preferences-card">
        <h3>偏好设置</h3>
        <div class="preferences-list">
          <div class="preference-item">
            <span class="preference-label">接收通知</span>
            <el-switch
              v-model="userInfo.preferences.notifications"
              @change="handleUpdateProfile"
            />
          </div>
          
          <div class="preference-item">
            <span class="preference-label">自动同步数据</span>
            <el-switch
              v-model="userInfo.preferences.autoSync"
              @change="handleUpdateProfile"
            />
          </div>
          
          <div class="preference-item">
            <span class="preference-label">深色模式</span>
            <el-switch
              v-model="userInfo.preferences.darkMode"
              @change="handleDarkModeToggle"
            />
          </div>
        </div>
      </el-card>
      
      <!-- 我的行程卡片 -->
      <el-card shadow="hover" class="trips-card">
        <h3>我的行程</h3>
        
        <div v-if="loading" class="loading-state">
          <el-skeleton :rows="3" animated />
        </div>
        
        <div v-else>
          <el-table :data="userTrips" style="width: 100%">
            <el-table-column prop="name" label="行程名称">
              <template #default="scope">
                <span class="trip-name" @click="goToTripDetail(scope.row.id)">
                  {{ scope.row.name }}
                </span>
              </template>
            </el-table-column>
            
            <el-table-column prop="destination" label="目的地" />
            
            <el-table-column label="时间">
              <template #default="scope">
                {{ scope.row.startDate }} 至 {{ scope.row.endDate }}
              </template>
            </el-table-column>
            
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="getTripStatusColor(scope.row.status)">
                  {{ getTripStatusText(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            
            <el-table-column label="操作" width="180">
              <template #default="scope">
                <el-button
                  type="primary"
                  text
                  @click="goToTripDetail(scope.row.id)"
                >
                  查看详情
                </el-button>
                <el-button
                  type="success"
                  text
                  @click="goToBudget(scope.row.id)"
                >
                  预算管理
                </el-button>
                <el-button
                  type="danger"
                  text
                  @click="handleDeleteTrip(scope.row.id)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <div v-if="userTrips.length === 0" class="empty-trips">
            <el-empty description="暂无行程记录" />
            <el-button type="primary" @click="router.push('/planner')">
              创建第一个行程
            </el-button>
          </div>
        </div>
      </el-card>
      
      <!-- 退出登录按钮 -->
      <div class="logout-section">
        <el-button type="danger" @click="logout">
          退出登录
        </el-button>
      </div>
    </div>
    
    <!-- 修改密码对话框 -->
    <el-dialog title="修改密码" v-model="changePasswordDialogVisible" width="400px">
      <el-form :model="passwordForm">
        <el-form-item label="旧密码">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            placeholder="请输入旧密码"
          />
        </el-form-item>
        
        <el-form-item label="新密码">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码"
          />
        </el-form-item>
        
        <el-form-item label="确认密码">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="changePasswordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleChangePassword">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.profile-container {
  min-height: calc(100vh - 120px);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.page-title {
  font-size: 28px;
  margin-bottom: 30px;
  color: #303133;
  text-align: center;
}

.profile-card,
.preferences-card,
.trips-card {
  margin-bottom: 30px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.profile-header {
  display: flex;
  gap: 40px;
  align-items: flex-start;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.user-avatar {
  border: 3px solid #409eff;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.user-avatar:hover {
  transform: scale(1.05);
}

.user-info {
  flex: 1;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.info-row h3 {
  font-size: 24px;
  color: #303133;
  margin: 0;
}

.info-content {
  width: 100%;
}

.action-buttons {
  margin-top: 20px;
  display: flex;
  gap: 10px;
  justify-content: center;
}

.preferences-card h3,
.trips-card h3 {
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 20px;
  color: #303133;
}

.preferences-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.preference-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.preference-label {
  font-size: 16px;
  color: #606266;
}

.loading-state {
  padding: 20px 0;
}

.trip-name {
  color: #409eff;
  cursor: pointer;
  text-decoration: underline;
}

.empty-trips {
  text-align: center;
  padding: 40px 0;
}

.empty-trips .el-button {
  margin-top: 20px;
}

.logout-section {
  text-align: center;
  margin-top: 40px;
  margin-bottom: 60px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .info-row {
    flex-direction: column;
    gap: 15px;
  }
  
  .preference-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>