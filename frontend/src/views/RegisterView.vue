<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElForm, ElFormItem, ElInput, ElButton } from 'element-plus'
import { User, UserFilled, Message, Lock, ArrowLeft, Phone } from '@element-plus/icons-vue'
import { useUserStore } from '../store'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref<InstanceType<typeof ElForm>>()

const registerForm = reactive({
  username: '',
  email: '',
  phone: '',
  password: '',
  confirmPassword: '',
  fullName: ''
})

const loading = ref(false)
const showPassword = ref(false)
const showConfirmPassword = ref(false)

// 表单验证规则
const rules: Record<string, any[]> = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符之间', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email' as const, message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入电话号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号码', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { 
      validator: (_rule: any, value: string, callback: Function) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  fullName: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 50, message: '姓名长度在 2 到 50 个字符之间', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (formRef.value) {
    formRef.value.validate(async (valid: boolean) => {
      if (valid) {
        loading.value = true
        try {
          await userStore.register(
            registerForm.username,
            registerForm.email,
            registerForm.phone,
            registerForm.password,
            registerForm.fullName
          )
          
          ElMessage.success('注册成功，请登录')
          router.push('/login')
        } catch (error: any) {
          ElMessage.error(error || '注册失败，请稍后重试')
          console.error('注册错误:', error)
        } finally {
          loading.value = false
        }
      }
    })
  }
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<template>
  <div class="register-container">
    <div class="register-content">
      <!-- 左侧欢迎信息 -->
      <div class="welcome-section">
        <div class="welcome-content">
          <div class="brand-logo">
            <span class="brand-name">AI 旅行助手</span>
          </div>
          
          <h1 class="welcome-title">
            <span class="title-main">加入我们</span>
            <span class="title-sub">开启智能旅行新体验</span>
          </h1>
          
          <p class="welcome-description">
            注册 AI 旅行助手，体验智能行程规划<br/>让每一次旅行都成为美好回忆
          </p>
          
          <div class="feature-list">
            <div class="feature-item">
              <div class="feature-text">
                <h4>个性化行程规划</h4>
                <p>AI 智能分析偏好，定制专属路线</p>
              </div>
            </div>
            
            <div class="feature-item">
              <div class="feature-text">
                <h4>智能预算管理</h4>
                <p>实时跟踪开销，合理规划花费</p>
              </div>
            </div>
            
            <div class="feature-item">
              <div class="feature-text">
                <h4>实时路线优化</h4>
                <p>动态调整行程，节省时间成本</p>
              </div>
            </div>
            
            <div class="feature-item">
              <div class="feature-text">
                <h4>多设备云端同步</h4>
                <p>随时随地访问，数据安全可靠</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧注册表单 -->
      <div class="form-section">
        <div class="register-form-wrapper">
          <div class="form-header">
            <h2 class="form-title">创建账户</h2>
            <p class="form-subtitle">填写以下信息，开启智能旅行体验</p>
          </div>

          <el-form
            ref="formRef"
            :model="registerForm"
            :rules="rules"
            class="register-form"
            @submit.prevent="handleRegister"
          >
            <el-form-item prop="username">
              <div class="input-wrapper">
                <label class="input-label">用户名</label>
                <div class="input-group">
                  <div class="input-icon">
                    <el-icon><User /></el-icon>
                  </div>
                  <el-input
                    v-model="registerForm.username"
                    placeholder="请输入用户名"
                    autocomplete="username"
                    class="custom-input"
                  />
                </div>
              </div>
            </el-form-item>
            
            <el-form-item prop="fullName">
              <div class="input-wrapper">
                <label class="input-label">真实姓名</label>
                <div class="input-group">
                  <div class="input-icon">
                    <el-icon><UserFilled /></el-icon>
                  </div>
                  <el-input
                    v-model="registerForm.fullName"
                    placeholder="请输入真实姓名"
                    autocomplete="name"
                    class="custom-input"
                  />
                </div>
              </div>
            </el-form-item>
            
            <el-form-item prop="email">
              <div class="input-wrapper">
                <label class="input-label">邮箱地址</label>
                <div class="input-group">
                  <div class="input-icon">
                    <el-icon><Message /></el-icon>
                  </div>
                  <el-input
                    v-model="registerForm.email"
                    placeholder="请输入邮箱地址"
                    type="email"
                    autocomplete="email"
                    class="custom-input"
                  />
                </div>
              </div>
            </el-form-item>
            
            <el-form-item prop="phone">
              <div class="input-wrapper">
                <label class="input-label">手机号码</label>
                <div class="input-group">
                  <div class="input-icon">
                    <el-icon><Phone /></el-icon>
                  </div>
                  <el-input
                    v-model="registerForm.phone"
                    placeholder="请输入手机号码"
                    type="tel"
                    autocomplete="tel"
                    class="custom-input"
                  />
                </div>
              </div>
            </el-form-item>
            
            <el-form-item prop="password">
              <div class="input-wrapper">
                <label class="input-label">登录密码</label>
                <div class="input-group">
                  <div class="input-icon">
                    <el-icon><Lock /></el-icon>
                  </div>
                  <el-input
                    v-model="registerForm.password"
                    :type="showPassword ? 'text' : 'password'"
                    placeholder="请输入登录密码"
                    show-password
                    autocomplete="new-password"
                    class="custom-input"
                  />
                </div>
              </div>
            </el-form-item>
            
            <el-form-item prop="confirmPassword">
              <div class="input-wrapper">
                <label class="input-label">确认密码</label>
                <div class="input-group">
                  <div class="input-icon">
                    <el-icon><Lock /></el-icon>
                  </div>
                  <el-input
                    v-model="registerForm.confirmPassword"
                    :type="showConfirmPassword ? 'text' : 'password'"
                    placeholder="请再次输入密码"
                    show-password
                    autocomplete="new-password"
                    class="custom-input"
                  />
                </div>
              </div>
            </el-form-item>
            
            <el-form-item class="submit-item">
              <el-button
                type="primary"
                size="large"
                class="register-btn"
                :loading="loading"
                @click="handleRegister"
              >
                {{ loading ? '注册中...' : '立即注册' }}
              </el-button>
            </el-form-item>
          </el-form>

          <div class="form-divider">
            <span>已有账户？</span>
          </div>
          
          <div class="form-footer">
            <el-button 
              size="large"
              class="login-link-btn"
              @click="goToLogin"
            >
              <el-icon><ArrowLeft /></el-icon>
              返回登录
            </el-button>
          </div>

          <!-- 注册协议 -->
          <div class="agreement-section">
            <p class="agreement-text">
              注册即表示您同意我们的
              <a href="#" class="agreement-link">《服务条款》</a>
              和
              <a href="#" class="agreement-link">《隐私政策》</a>
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
* {
  box-sizing: border-box;
}

.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  position: relative;
}

/* 主要内容布局 */
.register-content {
  display: grid;
  grid-template-columns: 520px 480px;
  max-width: 1000px;
  width: 100%;
  background: white;
  border-radius: 20px;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.2);
  overflow: hidden;
}

/* 左侧欢迎区域 */
.welcome-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 50px 45px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.welcome-content {
  max-width: 100%;
}

/* Logo品牌 */
.brand-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 35px;
}

.logo-icon {
  width: 56px;
  height: 56px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
}

.brand-name {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: 0.5px;
}

/* 标题区 */
.welcome-title {
  margin-bottom: 20px;
}

.title-main {
  display: block;
  font-size: 36px;
  font-weight: 800;
  line-height: 1.2;
  margin-bottom: 8px;
  letter-spacing: -0.5px;
}

.title-sub {
  display: block;
  font-size: 24px;
  font-weight: 600;
  opacity: 0.95;
  background: linear-gradient(90deg, #fff 0%, #ffd700 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-description {
  font-size: 15px;
  line-height: 1.7;
  opacity: 0.9;
  margin-bottom: 35px;
}

/* 功能列表 */
.feature-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 35px;
}

.feature-item {
  display: flex;
  align-items: flex-start;
  gap: 15px;
}

.feature-icon {
  width: 44px;
  height: 44px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  backdrop-filter: blur(10px);
}

.feature-icon .el-icon {
  font-size: 22px;
}

.feature-text h4 {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 4px 0;
  line-height: 1.3;
}

.feature-text p {
  font-size: 13px;
  margin: 0;
  opacity: 0.85;
  line-height: 1.5;
}

/* 统计数据 */
.welcome-stats {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 30px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  opacity: 0.85;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: rgba(255, 255, 255, 0.2);
}

/* 右侧表单区域 */
.form-section {
  padding: 50px 45px;
  display: flex;
  align-items: center;
  background: #fafafa;
}

.register-form-wrapper {
  width: 100%;
}

.form-header {
  margin-bottom: 30px;
}

.form-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0 0 8px 0;
  letter-spacing: -0.5px;
}

.form-subtitle {
  color: #666;
  font-size: 14px;
  margin: 0;
}

/* 表单样式 */
.register-form :deep(.el-form-item) {
  margin-bottom: 22px;
}

.register-form :deep(.el-form-item__error) {
  padding-top: 4px;
  font-size: 12px;
}

.input-wrapper {
  width: 100%;
}

.input-label {
  display: block;
  font-size: 13px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.input-group {
  position: relative;
  width: 100%;
}

.input-icon {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
  z-index: 2;
  display: flex;
  align-items: center;
  justify-content: center;
}

.input-icon .el-icon {
  font-size: 18px;
}

.custom-input {
  width: 100%;
}

.custom-input :deep(.el-input__wrapper) {
  padding: 0 15px 0 46px;
  height: 48px;
  border-radius: 10px;
  border: 2px solid #e5e5e5;
  background: white;
  box-shadow: none;
  transition: all 0.2s ease;
}

.custom-input :deep(.el-input__inner) {
  font-size: 14px;
  color: #333;
}

.custom-input :deep(.el-input__wrapper:hover) {
  border-color: #667eea;
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

/* 提交按钮 */
.submit-item {
  margin-top: 28px;
  margin-bottom: 0 !important;
}

.register-btn {
  width: 100%;
  height: 50px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 0.5px;
  transition: all 0.2s ease;
}

.register-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.35);
}

.register-btn:active:not(:disabled) {
  transform: translateY(0);
}

/* 分割线 */
.form-divider {
  text-align: center;
  margin: 28px 0 20px;
  position: relative;
}

.form-divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: #e0e0e0;
}

.form-divider span {
  position: relative;
  background: #fafafa;
  padding: 0 16px;
  color: #999;
  font-size: 13px;
}

/* 返回登录按钮 */
.form-footer {
  margin-bottom: 25px;
}

.login-link-btn {
  width: 100%;
  height: 48px;
  background: white;
  border: 2px solid #e5e5e5;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  color: #667eea;
  transition: all 0.2s ease;
}

.login-link-btn:hover {
  border-color: #667eea;
  background: #f5f7ff;
}

.login-link-btn .el-icon {
  margin-right: 6px;
}

/* 注册协议 */
.agreement-section {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #e5e5e5;
}

.agreement-text {
  color: #999;
  font-size: 12px;
  line-height: 1.6;
  margin: 0;
}

.agreement-link {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
  transition: opacity 0.2s ease;
}

.agreement-link:hover {
  opacity: 0.8;
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 968px) {
  .register-content {
    grid-template-columns: 1fr;
    max-width: 480px;
  }
  
  .welcome-section {
    display: none;
  }
  
  .form-section {
    background: white;
    padding: 40px 30px;
  }
  
  .form-divider span {
    background: white;
  }
}

@media (max-width: 480px) {
  .register-container {
    padding: 15px;
  }
  
  .form-section {
    padding: 30px 20px;
  }
  
  .form-title {
    font-size: 24px;
  }
  
  .register-btn,
  .login-link-btn {
    height: 46px;
  }
}
</style>