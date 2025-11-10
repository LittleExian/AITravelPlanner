<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElInputNumber, ElSlider, ElRow, ElCol } from 'element-plus'
import { Microphone, MagicStick } from '@element-plus/icons-vue'
import { useUserStore, useTripStore } from '../store'
import tripAPI from '../api/trips'

const router = useRouter()
const userStore = useUserStore()
const tripStore = useTripStore()

// 添加表单引用
const formRef = ref()

// 表单数据
const tripForm = reactive({
  destination: '',
  startDate: '',
  endDate: '',
  budget: 2000,
  peopleCount: 1,
  preferences: [] as string[],
  specialNeeds: '',
  description: ''
})

// 语音输入状态
const voiceRecording = ref(false)
const voiceText = ref('')

// 表单验证规则
const rules: Record<string, any[]> = {
  destination: [
    { required: true, message: '请输入目的地', trigger: 'blur' }
  ],
  startDate: [
    { required: true, message: '请选择开始日期', trigger: 'change' }
  ],
  endDate: [
    { required: true, message: '请选择结束日期', trigger: 'change' }
  ],
  budget: [
    { required: true, message: '请输入预算金额', trigger: 'blur' },
    { type: 'number' as const, min: 0, message: '预算金额必须大于0', trigger: 'blur' }
  ]
}

// 旅行偏好选项
const preferenceOptions = [
  { label: '美食', value: 'food' },
  { label: '购物', value: 'shopping' },
  { label: '文化', value: 'culture' },
  { label: '自然风景', value: 'nature' },
  { label: '动漫', value: 'anime' },
  { label: '历史', value: 'history' },
  { label: '艺术', value: 'art' },
  { label: '冒险', value: 'adventure' }
]

// 用户输入的新偏好
const newPreference = ref('')

// 添加自定义偏好
const addCustomPreference = () => {
  if (newPreference.value && !tripForm.preferences.includes(newPreference.value)) {
    tripForm.preferences.push(newPreference.value)
    newPreference.value = ''
  }
}

// 移除偏好
const removePreference = (value: string) => {
  const index = tripForm.preferences.indexOf(value)
  if (index > -1) {
    tripForm.preferences.splice(index, 1)
  }
}

// 模拟语音识别
const startVoiceRecording = () => {
  voiceRecording.value = true
  ElMessage.info('开始录音...')
  
  // 模拟录音3秒后自动停止
  setTimeout(() => {
    stopVoiceRecording()
  }, 3000)
}

const stopVoiceRecording = () => {
  voiceRecording.value = false
  
  // 模拟语音识别结果
  voiceText.value = '我想去北京玩3天，预算3000元，希望能体验当地美食和文化景点'
  ElMessage.success('录音结束，已识别语音内容')
}

// 应用语音识别结果到表单
const applyVoiceResult = () => {
  if (voiceText.value) {
    tripForm.description = voiceText.value
    // 简单的关键词提取（实际项目中会有更复杂的NLP处理）
    if (voiceText.value.includes('北京')) {
      tripForm.destination = '北京'
    }
    if (voiceText.value.includes('3000')) {
      tripForm.budget = 3000
    }
  }
}

// 获取偏好标签显示文本
const getPreferenceLabel = (value: string) => {
  const option = preferenceOptions.find(opt => opt.value === value)
  return option ? option.label : value
}

// 生成行程
const generating = ref(false)
const generateTrip = async () => {
    // 使用表单引用而不是document.querySelector
    if (formRef.value) {
      formRef.value.validate(async (valid: boolean) => {
        if (valid) {
          generating.value = true
          try {
            // 检查用户是否已登录并有用户ID
            if (!userStore.userInfo || !userStore.userInfo.id) {
              // 尝试获取用户信息
              await userStore.fetchProfile()
              if (!userStore.userInfo || !userStore.userInfo.id) {
                ElMessage.error('请先登录再生成行程')
                router.push('/login')
                return
              }
            }
            
            // 准备行程数据 - 根据表单字段和后端API需求正确映射参数
            const tripData = {
              title: `${tripForm.destination}旅行计划`,
              destination: tripForm.destination,
              startDate: tripForm.startDate,
              endDate: tripForm.endDate,
              description: tripForm.description,
              budgetAmount: tripForm.budget,
              peopleCount: tripForm.peopleCount,
              travelPreferences: tripForm.preferences,
              specialNeeds: tripForm.specialNeeds,
              userId: userStore.userInfo?.id
            }
            
            console.log('发送到后端的行程数据:', tripData)
            
            // 调用后端API生成行程（这里假设后端有一个专门的AI生成行程接口）
            // 如果没有专门的AI生成接口，可以直接使用createTrip接口
            // const newTrip = await tripStore.createTrip(tripData)
            
            // 尝试使用可能存在的AI生成行程接口
            try {
              // 使用新封装的aiGenerateTrip方法
              const newTrip = await tripAPI.aiGenerateTrip(tripData)
              ElMessage.success('行程生成成功！')
              router.push(`/trip/${newTrip.id}`) // 跳转到实际生成的行程详情页
            } catch (apiError) {
              console.log('AI生成接口调用失败，尝试使用常规创建行程接口', apiError)
              // 回退到使用常规创建行程接口
              const newTrip = await tripStore.createTrip(tripData)
              ElMessage.success('行程创建成功！')
              router.push(`/trip/${newTrip.id}`) // 跳转到行程详情页
            }
        } catch (error) {
          ElMessage.error('行程生成失败，请稍后重试')
          console.error('生成行程错误:', error)
        } finally {
          generating.value = false
        }
      }
    })
  }
}
</script>

<template>
  <div class="planner-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="decoration-circle circle-1"></div>
      <div class="decoration-circle circle-2"></div>
      <div class="floating-element element-1">🗺️</div>
      <div class="floating-element element-2">✈️</div>
      <div class="floating-element element-3">🏨</div>
    </div>

    <div class="container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">智能行程规划</h1>
        <p class="page-subtitle">告诉我们您的需求，AI将为您生成个性化旅行方案</p>
      </div>
      
      <!-- 语音输入区域 -->
      <div class="voice-input-section">
        <el-card shadow="hover" class="voice-card">
          
          <div class="voice-content">
            <el-button
              type="primary"
              :icon="Microphone"
              @click="voiceRecording ? stopVoiceRecording() : startVoiceRecording()"
              :loading="voiceRecording"
              class="voice-button"
              :class="{ recording: voiceRecording }"
            >
              {{ voiceRecording ? '录音中...' : '开始语音输入' }}
            </el-button>
            
            <div v-if="voiceText" class="voice-result">
              <div class="result-icon">🎯</div>
              <div class="result-content">
                <p class="result-text">{{ voiceText }}</p>
                <el-button type="primary" text @click="applyVoiceResult" class="apply-button">
                  应用到表单
                </el-button>
              </div>
            </div>
          </div>
        </el-card>
      </div>
      
      <!-- 行程信息表单 -->
      <el-form
        ref="formRef"
        :model="tripForm"
        :rules="rules"
        class="trip-form"
        label-width="120px"
      >
        <div class="form-section">
          <h3 class="section-title">基本信息</h3>
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="目的地" prop="destination">
                <el-input 
                  v-model="tripForm.destination" 
                  placeholder="请输入目的地，如：北京、上海、东京等"
                  class="custom-input"
                />
              </el-form-item>
            </el-col>
            
            <el-col :span="12">
              <el-form-item label="旅行人数" prop="peopleCount">
                <el-input-number
                  v-model="tripForm.peopleCount"
                  :min="1"
                  :max="20"
                  placeholder="请输入人数"
                  class="custom-input-number"
                />
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="开始日期" prop="startDate">
                <el-date-picker
                  v-model="tripForm.startDate"
                  type="date"
                  placeholder="选择开始日期"
                  class="custom-date-picker"
                />
              </el-form-item>
            </el-col>
            
            <el-col :span="12">
              <el-form-item label="结束日期" prop="endDate">
                <el-date-picker
                  v-model="tripForm.endDate"
                  type="date"
                  placeholder="选择结束日期"
                  class="custom-date-picker"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="form-section">
          <h3 class="section-title">预算设置</h3>
          <el-row>
            <el-col :span="24">
              <el-form-item label="预算金额" prop="budget">
                <el-input-number
                  v-model="tripForm.budget"
                  :min="1"
                  :step="100"
                  placeholder="请输入预算金额"
                  class="custom-input-number"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="form-section">
          <h3 class="section-title">语音输入信息</h3>
          <el-row>
            <el-col :span="24">
              <el-form-item label="行程描述">
                <el-input
                  v-model="tripForm.description"
                  type="textarea"
                  :rows="4"
                  placeholder="请补充其他需求说明，帮助我们为您生成更精准的行程"
                  class="custom-textarea"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="form-section">
          <h3 class="section-title">旅行偏好</h3>
          <el-row>
            <el-col :span="24">
              <el-form-item label="兴趣偏好">
              <div class="custom-tags-input">
                <div class="tags-container">
                  <!-- 显示已选择的标签 -->
                  <el-tag
                    v-for="value in tripForm.preferences"
                    :key="value"
                    closable
                    @close="removePreference(value)"
                    class="preference-tag"
                  >
                    {{ getPreferenceLabel(value) }}
                  </el-tag>
                  
                  <!-- 预定义选项按钮 -->
                  <div class="predefined-options">
                    <el-button
                      v-for="option in preferenceOptions"
                      :key="option.value"
                      :disabled="tripForm.preferences.includes(option.value)"
                      size="small"
                      type="default"
                      plain
                      @click="tripForm.preferences.push(option.value)"
                      class="preference-btn"
                    >
                      {{ option.label }}
                    </el-button>
                  </div>
                  
                  <!-- 自定义输入 -->
                  <div class="custom-input-wrapper">
                    <el-input
                      v-model="newPreference"
                      placeholder="输入自定义偏好，按回车添加"
                      @keyup.enter="addCustomPreference"
                      class="custom-preference-input"
                    />
                    <el-button
                      type="primary"
                      size="small"
                      @click="addCustomPreference"
                      :disabled="!newPreference"
                      class="add-btn"
                    >
                      添加
                    </el-button>
                  </div>
                </div>
              </div>
            </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="form-section">
          <h3 class="section-title">特殊需求</h3>
          <el-row>
            <el-col :span="24">
              <el-form-item label="特殊要求">
                <el-input
                  v-model="tripForm.specialNeeds"
                  type="textarea"
                  placeholder="请输入您的特殊需求，如饮食偏好、行动不便等"
                  :rows="3"
                  class="custom-textarea"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </div>
        
        <div class="form-actions">
          <el-button
            type="primary"
            size="large"
            class="generate-btn"
            :loading="generating"
            @click="generateTrip"
          >
            <el-icon v-if="!generating"><MagicStick /></el-icon>
            {{ generating ? 'AI正在规划中...' : '一键生成智能行程' }}
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.planner-container {
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
  max-width: 1000px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

/* 页面标题 */
.page-header {
  text-align: center;
  margin-bottom: 40px;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #303133;
  margin-bottom: 1rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-subtitle {
  font-size: 1.1rem;
  color: #606266;
  max-width: 600px;
  margin: 0 auto;
}

/* 语音输入区域 */
.voice-input-section {
  margin-bottom: 30px;
}

.voice-card {
  border: none;
  border-radius: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.voice-card :deep(.el-card__header) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  padding: 24px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-icon {
  width: 50px;
  height: 50px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
}

.header-content h3 {
  margin: 0 0 4px 0;
  font-size: 1.3rem;
  font-weight: 600;
}

.header-content p {
  margin: 0;
  opacity: 0.9;
  font-size: 0.9rem;
}

.voice-content {
  padding: 20px 0;
  text-align: center;
}

.voice-button {
  background: rgba(255, 255, 255, 0.2);
  border: 2px solid rgba(255, 255, 255, 0.3);
  color: white;
  padding: 12px 24px;
  font-size: 1rem;
  font-weight: 600;
  border-radius: 50px;
  transition: all 0.3s ease;
}

.voice-button:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
}

.voice-button.recording {
  background: rgba(255, 107, 107, 0.8);
  border-color: rgba(255, 255, 255, 0.5);
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

.voice-result {
  margin-top: 20px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  display: flex;
  align-items: flex-start;
  gap: 12px;
  backdrop-filter: blur(10px);
}

.result-icon {
  font-size: 1.5rem;
  margin-top: 2px;
}

.result-content {
  flex: 1;
  text-align: left;
}

.result-text {
  margin: 0 0 8px 0;
  line-height: 1.5;
  color: white;
}

.apply-button {
  color: #ffd700 !important;
  font-weight: 600;
}

/* 表单样式 */
.trip-form {
  background: white;
  padding: 40px;
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
}

.form-section {
  margin-bottom: 32px;
  padding-bottom: 32px;
  border-bottom: 1px solid #f0f0f0;
}

.form-section:last-of-type {
  border-bottom: none;
  margin-bottom: 0;
}

.section-title {
  font-size: 1.3rem;
  font-weight: 600;
  color: #303133;
  margin-bottom: 20px;
  padding-left: 12px;
  border-left: 4px solid #667eea;
}

/* 自定义表单元素样式 */
.custom-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  border: 2px solid #f0f0f0;
  background: #fafafa;
  transition: all 0.3s ease;
  height: 48px;
}

.custom-input :deep(.el-input__wrapper:hover),
.custom-input :deep(.el-input__wrapper.is-focus) {
  border-color: #667eea;
  background: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.custom-input-number :deep(.el-input-number__decrease),
.custom-input-number :deep(.el-input-number__increase) {
  background: #f5f7fa;
  border: none;
}

.custom-date-picker {
  width: 100%;
}

.custom-date-picker :deep(.el-input__wrapper) {
  border-radius: 12px;
  border: 2px solid #f0f0f0;
  background: #fafafa;
  transition: all 0.3s ease;
}

.custom-date-picker :deep(.el-input__wrapper:hover),
.custom-date-picker :deep(.el-input__wrapper.is-focus) {
  border-color: #667eea;
  background: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

/* 预算滑块 */
.budget-control {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.custom-slider :deep(.el-slider__runway) {
  height: 6px;
  background-color: #f0f0f0;
}

.custom-slider :deep(.el-slider__bar) {
  background: linear-gradient(135deg, #667eea, #764ba2);
  height: 6px;
}

.custom-slider :deep(.el-slider__button) {
  width: 20px;
  height: 20px;
  border: 2px solid #667eea;
  background: white;
}

.budget-display {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f8f9fa;
  border-radius: 12px;
}

.budget-label {
  color: #606266;
  font-weight: 500;
}

.budget-value {
  font-size: 1.3rem;
  font-weight: 700;
  color: #667eea;
}

/* 自定义标签输入样式 */
.custom-tags-input {
  width: 100%;
}

.tags-container {
  border: 2px solid #f0f0f0;
  border-radius: 12px;
  padding: 12px;
  background: #fafafa;
  transition: all 0.3s ease;
}

.tags-container:hover {
  border-color: #667eea;
  background: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.preference-tag {
  margin-right: 8px;
  margin-bottom: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-size: 0.9rem;
}

.predefined-options {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin: 12px 0;
}

.preference-btn {
  font-size: 0.9rem;
  border-radius: 20px;
  padding: 4px 16px;
  transition: all 0.3s ease;
}

.preference-btn:not(:disabled):hover {
  background-color: #667eea;
  border-color: #667eea;
  color: white;
}

.preference-btn:disabled {
  background-color: #e6e6e6;
  color: #999;
  cursor: not-allowed;
}

.custom-input-wrapper {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-top: 12px;
}

.custom-preference-input {
  flex: 1;
}

.custom-preference-input :deep(.el-input__wrapper) {
  border-radius: 12px;
}

.add-btn {
  white-space: nowrap;
}

.custom-textarea :deep(.el-textarea__inner) {
  border-radius: 12px;
  border: 2px solid #f0f0f0;
  background: #fafafa;
  transition: all 0.3s ease;
  resize: vertical;
}

.custom-textarea :deep(.el-textarea__inner:hover),
.custom-textarea :deep(.el-textarea__inner:focus) {
  border-color: #667eea;
  background: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

/* 生成按钮 */
.form-actions {
  text-align: center;
  margin-top: 40px;
  padding-top: 32px;
  border-top: 1px solid #f0f0f0;
}

.generate-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 50px;
  padding: 16px 48px;
  font-size: 1.1rem;
  font-weight: 600;
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.generate-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 35px rgba(102, 126, 234, 0.4);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .container {
    padding: 0 10px;
  }
  
  .page-title {
    font-size: 2rem;
  }
  
  .trip-form {
    padding: 20px;
  }
  
  .form-section {
    margin-bottom: 24px;
    padding-bottom: 24px;
  }
  
  .generate-btn {
    width: 100%;
    padding: 14px 24px;
  }
}
</style>