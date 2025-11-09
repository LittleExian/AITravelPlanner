<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../store'

const router = useRouter()
const userStore = useUserStore()

const loginForm = ref({
  username: '',
  password: ''
})

const loading = ref(false)
const showPassword = ref(false)

// 添加测试账号信息
const testAccount = {
  username: 'test_user',
  password: '123456'
}

const handleLogin = async () => {
  // 表单验证
  if (!loginForm.value.username || !loginForm.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }

  loading.value = true
  try {
    // 模拟登录请求
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 模拟成功响应
    const mockToken = 'mock-jwt-token-' + Date.now()
    const mockUserInfo = {
      id: '1',
      username: loginForm.value.username,
      email: `${loginForm.value.username}@example.com`
    }
    
    // 保存用户信息和token
    userStore.setToken(mockToken)
    userStore.setUserInfo(mockUserInfo)
    
    ElMessage.success('登录成功')
    router.push('/planner')
  } catch (error) {
    ElMessage.error('登录失败，请检查用户名和密码')
    console.error('登录错误:', error)
  } finally {
    loading.value = false
  }
}

// 添加测试登录功能
const handleTestLogin = () => {
  // 填充测试账号信息
  loginForm.value.username = testAccount.username
  loginForm.value.password = testAccount.password
  
  ElMessage.info('已填充测试账号信息')
  // 直接调用登录函数
  handleLogin()
}

const goToRegister = () => {
  router.push('/register')
}

// 表单验证规则
const rules: Record<string, any[]> = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6个字符', trigger: 'blur' }
  ]
}
</script>

<template>
  <div class="login-container">
    <div class="login-form-wrapper">
      <h2 class="login-title">登录</h2>
      <el-form
        :model="loginForm"
        :rules="rules"
        ref="loginFormRef"
        label-width="0px"
        class="login-form"
      >
        <el-form-item>
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            autocomplete="username"
          />
        </el-form-item>
        
        <el-form-item>
          <el-input
            v-model="loginForm.password"
            :type="showPassword ? 'text' : 'password'"
            placeholder="请输入密码"
            prefix-icon="Lock"
            :show-password="showPassword"
            @click:password="showPassword = !showPassword"
            autocomplete="current-password"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
        
        <!-- 添加测试登录按钮 -->
        <el-form-item>
          <el-button
            type="success"
            size="large"
            class="test-login-btn"
            :loading="loading"
            @click="handleTestLogin"
          >
            测试登录
          </el-button>
        </el-form-item>
        
        <div class="login-footer">
          <span>还没有账号？</span>
          <el-button type="text" @click="goToRegister">立即注册</el-button>
        </div>
      </el-form>
      
      <div class="third-party-login">
        <div class="divider">
          <span>其他登录方式</span>
        </div>
        <div class="third-party-icons">
          <el-button icon="Wechat" circle size="large" type="success" />
          <el-button icon="ChatLineSquare" circle size="large" type="primary" />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-form-wrapper {
  background: white;
  border-radius: 12px;
  padding: 40px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  font-size: 28px;
  color: #303133;
  margin-bottom: 10px;
}

.login-header p {
  color: #909399;
  font-size: 14px;
}

.login-form {
  margin-bottom: 20px;
}

.login-btn {
  width: 100%;
  margin-bottom: 10px;
}

.test-login-btn {
  width: 100%;
  margin-bottom: 20px;
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #606266;
}

.login-footer span {
  margin-right: 8px;
}

.third-party-login {
  margin-top: 30px;
}

.divider {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  font-size: 14px;
  color: #909399;
}

.divider::before,
.divider::after {
  content: '';
  flex: 1;
  border-bottom: 1px solid #e0e0e0;
}

.divider span {
  padding: 0 10px;
}

.third-party-icons {
  display: flex;
  justify-content: center;
  gap: 20px;
}
</style>