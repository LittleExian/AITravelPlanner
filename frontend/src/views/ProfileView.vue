<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElInput, ElButton, ElForm, ElFormItem, ElCard, ElAvatar, ElMessageBox, ElDivider, ElTag } from 'element-plus'
import { useUserStore } from '../store'
import authAPI from '../api/auth'
import type { UserResponse } from '../api/auth'

const router = useRouter()
const userStore = useUserStore()

const userInfo = reactive({
  id: '',
  username: '',
  fullName: '',
  email: '',
  phone: '',
  avatar: '',
  bio: ''
})

const editMode = ref(false)
const loading = ref(false)

onMounted(async () => {
  await loadUserData()
})

const loadUserData = async () => {
  try {
    loading.value = true
    // 从userStore获取真实用户数据
    if (userStore.userInfo) {
      console.log('使用真实用户数据:', userStore.userInfo)
      
      const storeUserInfo = userStore.userInfo as UserResponse;
      // 映射API返回的数据到组件的userInfo对象
      // 注意字段名称的对应关系
      userInfo.id = storeUserInfo.id
      userInfo.username = storeUserInfo.username || ''
      userInfo.fullName = storeUserInfo.fullName || ''  // API返回的是fullName，这里映射到fullName
      userInfo.email = storeUserInfo.email || ''
      userInfo.phone = storeUserInfo.phone || ''  // 添加手机号字段映射
      userInfo.bio = storeUserInfo.bio || ''  // 添加个人简介字段映射
      userInfo.avatar = storeUserInfo.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'  // 默认头像
    } else {
      console.warn('用户信息不可用，尝试重新获取')
      try {
        // 尝试重新获取用户信息
        await userStore.fetchProfile()
        if (userStore.userInfo) {
          const storeUserInfo = userStore.userInfo as UserResponse;
          // 重新映射数据
          userInfo.id = storeUserInfo.id
          userInfo.username = storeUserInfo.username || ''
          userInfo.fullName = storeUserInfo.fullName || ''  // 添加姓名字段映射
          userInfo.email = storeUserInfo.email || ''
          userInfo.phone = storeUserInfo.phone || ''  // 添加手机号字段映射
          userInfo.bio = storeUserInfo.bio || ''  // 添加个人简介字段映射
          userInfo.avatar = storeUserInfo.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
        }
      } catch (retryError) {
        console.error('重新获取用户信息失败:', retryError)
        ElMessage.warning('无法获取用户信息，使用默认设置')
      }
    }
  } catch (error) {
    ElMessage.error('加载用户信息失败')
    console.error('加载用户信息错误:', error)
  } finally {
    loading.value = false
  }
}

const handleUpdateProfile = async () => {
  try {
    loading.value = true
    // 调用API更新用户信息
    const updateData = {
      username: userInfo.username,
      email: userInfo.email,
      fullName: userInfo.fullName,  // 注意：nickname映射到fullName
      phone: userInfo.phone || null,  // 添加手机号字段
      bio: userInfo.bio || null  // 添加个人简介字段
    }
    
    const updatedUser = await authAPI.updateProfile(updateData)
    
    // 更新本地store中的用户信息
    userStore.setUserInfo(updatedUser)
    
    editMode.value = false
    ElMessage.success('个人信息更新成功')
  } catch (error) {
    ElMessage.error('更新个人信息失败')
    console.error('更新个人信息错误:', error)
  } finally {
    loading.value = false
  }
}

const handleAvatarUpload = (_uploadFile: any) => {
  // 模拟上传成功
  const mockUrl = `https://cube.elemecdn.com/${Math.floor(Math.random() * 10)}/88/03b0d39583f48206768a7534e55bcpng.png`
  userInfo.avatar = mockUrl
  ElMessage.success('头像上传成功')
  return false // 阻止默认上传行为
}

const logout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    router.push('/login')
    ElMessage.success('已成功退出登录')
  }).catch(() => {
    // 用户取消操作
  })
}

const cancelEdit = () => {
  editMode.value = false
  loadUserData()
  ElMessage.info('已取消编辑')
}
</script>

<template>
  <div class="profile-container">
    <div class="container">
      <!-- <div class="page-header">
        <h2 class="page-title">个人中心</h2>
        <p class="page-subtitle">管理您的个人信息和账户设置</p>
      </div> -->
      
      <!-- 个人信息卡片 -->
      <el-card shadow="hover" class="profile-card" :loading="loading">
        <template #header>
          <div class="card-header">
            <span class="card-title">个人信息</span>
            <el-button
              v-if="!editMode"
              type="primary"
              @click="editMode = true"
              round
            >
            编辑资料
            </el-button>
          </div>
        </template>
        
        <div class="profile-content">
          <!-- 头像区域 -->
          <div class="avatar-section">
            <div class="avatar-wrapper">
              <el-avatar :src="userInfo.avatar" :size="120" class="user-avatar">
                {{ userInfo.username?.charAt(0) || '游' }}
              </el-avatar>
              
              <el-upload
                v-if="editMode"
                class="avatar-uploader"
                :show-file-list="false"
                :before-upload="handleAvatarUpload"
              >
                <div class="avatar-overlay">
                  <i class="el-icon-camera"></i>
                  <span>更换头像</span>
                </div>
              </el-upload>
            </div>
            
            <div class="avatar-info">
              <h3 class="user-name">{{ userInfo.fullName || userInfo.username || '未设置姓名' }}</h3>
              <p class="user-username">@{{ userInfo.username }}</p>
              <el-tag v-if="userInfo.id" type="success" size="small">用户ID: {{ userInfo.id }}</el-tag>
            </div>
          </div>
          
          <el-divider />
          
          <!-- 用户详细信息 -->
          <div class="user-details">
            <el-form :model="userInfo" label-width="100px" label-position="left">
              <div class="form-row">
                <el-form-item label="用户名" class="form-item">
                  <el-input
                    v-model="userInfo.username"
                    :disabled="!editMode"
                    placeholder="用户名"
                    :class="{ 'edit-mode': editMode }"
                  />
                </el-form-item>
                
                <el-form-item label="姓名" class="form-item">
                  <el-input
                    v-model="userInfo.fullName"
                    :disabled="!editMode"
                    placeholder="姓名"  
                    :class="{ 'edit-mode': editMode }"
                  />
                </el-form-item>
              </div>
              
              <div class="form-row">
                <el-form-item label="邮箱" class="form-item">
                  <el-input
                    v-model="userInfo.email"
                    :disabled="!editMode"
                    type="email"
                    placeholder="邮箱"
                    :class="{ 'edit-mode': editMode }"
                  />
                </el-form-item>
                
                <el-form-item label="手机号" class="form-item">
                  <el-input
                    v-model="userInfo.phone"
                    :disabled="!editMode"
                    placeholder="手机号"
                    :class="{ 'edit-mode': editMode }"
                  />
                </el-form-item>
              </div>
              
              <el-form-item label="个人简介" class="form-item-full">
                <el-input
                  v-model="userInfo.bio"
                  type="textarea"
                  :disabled="!editMode"
                  placeholder="个人简介"
                  :rows="3"
                  maxlength="200"
                  show-word-limit
                  :class="{ 'edit-mode': editMode }"
                />
              </el-form-item>
            </el-form>
          </div>
          
          <!-- 保存/取消按钮 -->
          <div v-if="editMode" class="action-buttons">
            <el-button 
              type="primary" 
              @click="handleUpdateProfile"
              :loading="loading"
              round
            >
              保存修改
            </el-button>
            <el-button 
              @click="cancelEdit"
              :disabled="loading"
              round
            >
              取消
            </el-button>
          </div>
        </div>
      </el-card>
      
      <!-- 功能卡片 -->
      <div class="function-cards">
        <el-card shadow="hover" class="function-card">
          <div class="function-content" @click="router.push('/trips')">
            <div class="function-icon trips-icon">
              <i class="el-icon-location-outline"></i>
            </div>
            <div class="function-info">
              <h4>我的行程</h4>
              <p>查看和管理您的行程计划</p>
            </div>
            <div class="function-arrow">
              <i class="el-icon-arrow-right"></i>
            </div>
          </div>
        </el-card>
        
        <el-card shadow="hover" class="function-card">
          <div class="function-content" @click="logout">
            <div class="function-icon logout-icon">
              <i class="el-icon-switch-button"></i>
            </div>
            <div class="function-info">
              <h4>退出登录</h4>
              <p>安全退出您的账户</p>
            </div>
            <div class="function-arrow">
              <i class="el-icon-arrow-right"></i>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<style scoped>
.profile-container {
  min-height: calc(100vh - 120px);
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 20px 0;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
  padding-top: 20px;
}

.page-title {
  font-size: 32px;
  margin-bottom: 10px;
  color: #303133;
  font-weight: 600;
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.page-subtitle {
  font-size: 16px;
  color: #909399;
  margin: 0;
}

.profile-card {
  margin-bottom: 30px;
  background-color: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: none;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #ebeef5;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.profile-content {
  padding: 30px;
}

.avatar-section {
  display: flex;
  gap: 30px;
  align-items: center;
  margin-bottom: 20px;
}

.avatar-wrapper {
  position: relative;
  border-radius: 50%;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.user-avatar {
  border: 4px solid #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.user-avatar:hover {
  transform: scale(1.05);
}

.avatar-uploader {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  cursor: pointer;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 50%;
}

.avatar-overlay:hover {
  opacity: 1;
}

.avatar-overlay i {
  font-size: 24px;
  margin-bottom: 5px;
}

.avatar-overlay span {
  font-size: 12px;
}

.avatar-info {
  flex: 1;
}

.user-name {
  font-size: 24px;
  color: #303133;
  margin: 0 0 8px 0;
  font-weight: 600;
}

.user-username {
  font-size: 16px;
  color: #909399;
  margin: 0 0 12px 0;
}

.user-details {
  margin-top: 20px;
}

.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.form-item {
  flex: 1;
}

.form-item-full {
  width: 100%;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-input.is-disabled .el-input__inner) {
  background-color: #f5f7fa;
  color: #909399;
  border-color: #e4e7ed;
}

:deep(.el-input.edit-mode .el-input__inner) {
  border-color: #409eff;
  background-color: #f0f7ff;
}

:deep(.el-textarea.is-disabled .el-textarea__inner) {
  background-color: #f5f7fa;
  color: #909399;
  border-color: #e4e7ed;
}

:deep(.el-textarea.edit-mode .el-textarea__inner) {
  border-color: #409eff;
  background-color: #f0f7ff;
}

.action-buttons {
  margin-top: 30px;
  display: flex;
  gap: 12px;
  justify-content: center;
}

.function-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.function-card {
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
  overflow: hidden;
}

.function-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
}

.function-content {
  display: flex;
  align-items: center;
  padding: 20px;
}

.function-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
  color: white;
}

.trips-icon {
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
}

.logout-icon {
  background: linear-gradient(135deg, #f56c6c 0%, #e6a23c 100%);
}

.function-info {
  flex: 1;
}

.function-info h4 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #303133;
  font-weight: 600;
}

.function-info p {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.function-arrow {
  color: #c0c4cc;
  font-size: 18px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-content {
    padding: 20px;
  }
  
  .avatar-section {
    flex-direction: column;
    text-align: center;
    gap: 20px;
  }
  
  .form-row {
    flex-direction: column;
    gap: 0;
  }
  
  .function-cards {
    grid-template-columns: 1fr;
  }
  
  .page-title {
    font-size: 28px;
  }
}
</style>