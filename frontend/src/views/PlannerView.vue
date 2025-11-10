<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElInputNumber, ElSlider, ElCheckboxGroup, ElCheckbox, ElRow, ElCol } from 'element-plus'
import { Microphone } from '@element-plus/icons-vue'

const router = useRouter()

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
  specialNeeds: [] as string[],
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

// 特殊需求选项
const specialNeedOptions = [
  { label: '带孩子', value: 'children' },
  { label: '老人同行', value: 'elderly' },
  { label: '无障碍设施', value: 'accessible' },
  { label: '素食', value: 'vegetarian' },
  { label: '宠物友好', value: 'petFriendly' }
]

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

// 生成行程
const generating = ref(false)
const generateTrip = async () => {
  // 使用表单引用而不是document.querySelector
  if (formRef.value) {
    formRef.value.validate(async (valid: boolean) => {
      if (valid) {
        generating.value = true
        try {
          // 模拟AI生成行程
          await new Promise(resolve => setTimeout(resolve, 2000))
          
          ElMessage.success('行程生成成功！')
          router.push('/trip/1') // 跳转到模拟的行程详情页
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
    <div class="container">
      <h2 class="page-title">创建智能行程</h2>
      
      <!-- 语音输入区域 -->
      <div class="voice-input-section">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Microphone /></el-icon>
              <span>语音输入需求</span>
            </div>
          </template>
          
          <div class="voice-content">
            <el-button
              type="primary"
              :icon="Microphone"
              @click="voiceRecording ? stopVoiceRecording() : startVoiceRecording()"
              :loading="voiceRecording"
            >
              {{ voiceRecording ? '停止录音' : '开始录音' }}
            </el-button>
            
            <div v-if="voiceText" class="voice-result">
              <p>识别结果: {{ voiceText }}</p>
              <el-button type="text" @click="applyVoiceResult">应用到表单</el-button>
            </div>
          </div>
        </el-card>
      </div>
      
      <!-- 行程信息表单 - 添加ref属性 -->
      <el-form
        ref="formRef"
        :model="tripForm"
        :rules="rules"
        class="trip-form"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="目的地" prop="destination">
              <el-input v-model="tripForm.destination" placeholder="请输入目的地" />
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="旅行人数" prop="peopleCount">
              <el-input-number
                v-model="tripForm.peopleCount"
                :min="1"
                :max="20"
                placeholder="请输入人数"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker
                v-model="tripForm.startDate"
                type="date"
                placeholder="选择开始日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker
                v-model="tripForm.endDate"
                type="date"
                placeholder="选择结束日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row>
          <el-col :span="24">
            <el-form-item label="预算范围（元）">
              <div class="budget-control">
                <el-slider
                  v-model="tripForm.budget"
                  :min="500"
                  :max="20000"
                  :step="100"
                />
                <span class="budget-value">¥{{ tripForm.budget }}</span>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row>
          <el-col :span="24">
            <el-form-item label="旅行偏好">
              <el-select
                v-model="tripForm.preferences"
                multiple
                placeholder="请选择您的旅行偏好"
                style="width: 100%"
              >
                <el-option
                  v-for="option in preferenceOptions"
                  :key="option.value"
                  :label="option.label"
                  :value="option.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row>
          <el-col :span="24">
            <el-form-item label="特殊需求">
              <div class="special-needs">
                <el-checkbox-group v-model="tripForm.specialNeeds">
                  <el-checkbox
                    v-for="option in specialNeedOptions"
                    :key="option.value"
                    :value="option.value"
                    style="margin-right: 20px"
                  >
                    {{ option.label }}
                  </el-checkbox>
                </el-checkbox-group>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row>
          <el-col :span="24">
            <el-form-item label="其他说明">
              <el-input
                v-model="tripForm.description"
                type="textarea"
                :rows="4"
                placeholder="请输入其他需求说明"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="generate-btn"
            :loading="generating"
            @click="generateTrip"
          >
            生成行程
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.planner-container {
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

.voice-input-section {
  margin-bottom: 30px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.voice-content {
  padding: 20px 0;
}

.voice-result {
  margin-top: 20px;
  padding: 10px;
  background-color: #f0f9ff;
  border: 1px solid #b3d8ff;
  border-radius: 4px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.trip-form {
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.budget-control {
  display: flex;
  align-items: center;
  gap: 20px;
}

.budget-value {
  font-size: 18px;
  font-weight: 600;
  color: #409eff;
  min-width: 80px;
}

.special-needs {
  display: flex;
  flex-wrap: wrap;
}

.generate-btn {
  width: 200px;
}
</style>