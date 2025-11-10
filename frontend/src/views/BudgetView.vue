<script setup lang="ts">
import { ref, onMounted, reactive, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElButton, ElTabs, ElTabPane, ElCard, ElForm, ElFormItem, ElInput, ElDatePicker, ElSelect, ElOption, ElDialog, ElTable, ElTableColumn, ElInputNumber, ElProgress, ElTag } from 'element-plus'
import { useTripStore } from '../store'
import budgetAPI from '../api/budgets'

const route = useRoute()
const router = useRouter()
// const budgetStore = useBudgetStore()
const tripStore = useTripStore()

const tripId = route.params.tripId as string
const tripName = ref('')
const loading = ref(true)

// 预算数据
const budgetData = ref<any>(null)
const expenses = ref<any[]>([])

// 添加费用对话框
const addExpenseDialogVisible = ref(false)
const expenseForm = reactive({
  name: '',
  date: '',
  category: '',
  amount: 0,
  description: ''
})

// 活动标签页
const activeTab = ref('allocations')

// 修改预算分配对话框
const updateAllocationDialogVisible = ref(false)
const allocationForm = reactive<Record<string, number>>({})

// 预算分类选项
const allocationCategories = [
  { label: '交通', value: '交通', color: '#2E8B57', icon: '🚗' },
  { label: '住宿', value: '住宿', color: '#1E90FF', icon: '🏨' },
  { label: '餐饮', value: '餐饮', color: '#FF8C00', icon: '🍽️' },
  { label: '门票', value: '门票', color: '#DC143C', icon: '🎫' },
  { label: '购物', value: '购物', color: '#6A5ACD', icon: '🛍️' },
  { label: '其他', value: '其他', color: '#696969', icon: '📝' }
]

// 费用分类选项
const expenseCategories = [
  { label: '交通', value: '交通', color: '#2E8B57' },
  { label: '住宿', value: '住宿', color: '#1E90FF' },
  { label: '餐饮', value: '餐饮', color: '#FF8C00' },
  { label: '门票', value: '门票', color: '#DC143C' },
  { label: '购物', value: '购物', color: '#6A5ACD' },
  { label: '其他', value: '其他', color: '#696969' }
]

// 从预算分配对象中提取预算项数组
const budgetAllocations = computed(() => {
  if (!budgetData.value || !budgetData.value.allocations) {
    return []
  }
  
  return Object.entries(budgetData.value.allocations).map(([category, amount]) => ({
    category,
    amount: Number(amount) || 0,
    categoryInfo: allocationCategories.find(cat => cat.value === category)
  })).filter(item => item.amount > 0)
})

// 计算总预算和总支出
const totalBudget = computed(() => {
  return budgetData.value?.totalBudget || 0
})

const totalExpense = computed(() => {
  return expenses.value.reduce((sum: number, item: any) => sum + item.amount, 0)
})

const remainingBudget = computed(() => {
  return totalBudget.value - totalExpense.value
})

// 计算预算使用率
const budgetUsageRate = computed(() => {
  return totalBudget.value > 0 ? Math.round((totalExpense.value / totalBudget.value) * 100) : 0
})

onMounted(async () => {
  await loadBudgetData()
})

const loadBudgetData = async () => {
  loading.value = true
  try {
    const budget = await budgetAPI.getTripBudget(tripId)
    budgetData.value = budget
    expenses.value = budget.expenses || []
    await loadTripName()
  } catch (error) {
    ElMessage.error('加载预算数据失败')
    console.error('加载预算错误:', error)
  } finally {
    loading.value = false
  }
}

const loadTripName = async () => {
  try {
    const trip = await tripStore.fetchTripById(tripId)
    if (trip) {
      tripName.value = trip.title || '行程'
    }
  } catch (error) {
    console.error('获取行程名称失败:', error)
    tripName.value = '行程'
  }
}

// 添加费用
const addExpense = async () => {
  if (!expenseForm.name || !expenseForm.date || !expenseForm.category || expenseForm.amount <= 0) {
    ElMessage.warning('请填写完整的费用信息，名称、日期、类别和金额为必填项')
    return
  }
  
  try {
    const newExpense = await budgetAPI.addExpense(tripId, { ...expenseForm })
    expenses.value.push(newExpense)
    await loadBudgetData()
    
    addExpenseDialogVisible.value = false
    Object.assign(expenseForm, {
      name: '',
      date: '',
      category: '',
      amount: 0,
      description: ''
    })
    
    ElMessage.success('费用添加成功')
  } catch (error) {
    console.error('添加费用失败:', error)
    ElMessage.error('添加费用失败，请重试')
  }
}

// 删除费用
const deleteExpense = async (expenseId: string) => {
  try {
    await budgetAPI.deleteExpense(tripId, expenseId)
    const index = expenses.value.findIndex(item => item.id === expenseId)
    if (index > -1) {
      expenses.value.splice(index, 1)
    }
    ElMessage.success('费用删除成功')
  } catch (error) {
    console.error('删除费用失败:', error)
    ElMessage.error('删除费用失败，请重试')
  }
}

// 获取预算使用百分比
const getBudgetUsagePercentage = (category: string) => {
  const categoryBudget = budgetData.value?.allocations?.[category] || 0
  const categoryExpense = expenses.value
    .filter(item => item.category === category)
    .reduce((sum, item) => sum + item.amount, 0)
  
  return categoryBudget > 0 ? Math.round((categoryExpense / categoryBudget) * 100) : 0
}

// 获取类别颜色
const getCategoryColor = (category: string) => {
  const categoryInfo = allocationCategories.find(cat => cat.value === category)
  return categoryInfo?.color || '#409EFF'
}

// 获取类别图标
const getCategoryIcon = (category: string) => {
  const categoryInfo = allocationCategories.find(cat => cat.value === category)
  return categoryInfo?.icon || '📝'
}

// 打开修改预算分配对话框
const openUpdateAllocationDialog = () => {
  Object.keys(allocationForm).forEach(key => {
    delete allocationForm[key]
  })
  
  if (budgetData.value && budgetData.value.allocations) {
    Object.entries(budgetData.value.allocations).forEach(([category, amount]) => {
      allocationForm[category] = Number(amount) || 0
    })
  }
  
  allocationCategories.forEach(category => {
    if (!(category.value in allocationForm)) {
      allocationForm[category.value] = 0
    }
  })
  
  updateAllocationDialogVisible.value = true
}

// 更新预算分配
const updateBudgetAllocations = async () => {
  try {
    const filteredAllocations = Object.entries(allocationForm)
      .filter(([_, amount]) => amount > 0)
      .reduce((acc, [category, amount]) => {
        acc[category] = amount
        return acc
      }, {} as Record<string, number>)
    
    const updatedBudget = await budgetAPI.updateBudgetAllocations(tripId, filteredAllocations)
    budgetData.value = updatedBudget
    updateAllocationDialogVisible.value = false
    ElMessage.success('预算分配更新成功')
  } catch (error) {
    console.error('更新预算分配失败:', error)
    ElMessage.error('更新预算分配失败，请重试')
  }
}
</script>

<template>
  <div class="budget-container">
    <div class="container">
      <!-- 头部区域 -->
      <div class="budget-header">
        <div class="header-title">
          <h1 class="page-title">{{ tripName }}</h1>
          <p class="page-subtitle">预算管理</p>
        </div>
        <el-button 
          type="text" 
          icon="el-icon-arrow-left" 
          @click="router.push('/trips')"
          class="back-btn"
        >
          返回行程列表
        </el-button>
      </div>
      
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <el-skeleton :rows="6" animated />
      </div>
      
      <div v-else class="budget-content">
        <!-- 预算概览卡片 -->
        <div class="budget-overview">
          <el-card shadow="hover" class="overview-card total-budget">
            <div class="overview-content">
              <div class="card-icon">💰</div>
              <div class="card-info">
                <h3>总预算</h3>
                <p class="amount">¥{{ totalBudget.toLocaleString() }}</p>
              </div>
            </div>
          </el-card>
          
          <el-card shadow="hover" class="overview-card expense">
            <div class="overview-content">
              <div class="card-icon">💸</div>
              <div class="card-info">
                <h3>已支出</h3>
                <p class="amount">¥{{ totalExpense.toLocaleString() }}</p>
                <div class="usage-rate">
                  <el-progress 
                    :percentage="budgetUsageRate" 
                    :stroke-width="6"
                    :show-text="false"
                  />
                  <span class="rate-text">{{ budgetUsageRate }}%</span>
                </div>
              </div>
            </div>
          </el-card>
          
          <el-card shadow="hover" class="overview-card remaining" :class="remainingBudget < 0 ? 'danger' : ''">
            <div class="overview-content">
              <div class="card-icon">💎</div>
              <div class="card-info">
                <h3>剩余预算</h3>
                <p class="amount">¥{{ remainingBudget.toLocaleString() }}</p>
                <div class="budget-status">
                  <el-tag :type="remainingBudget < 0 ? 'danger' : 'success'" size="small">
                    {{ remainingBudget < 0 ? '超支' : '正常' }}
                  </el-tag>
                </div>
              </div>
            </div>
          </el-card>
        </div>
        
        <!-- 预算管理选项卡 -->
        <el-tabs v-model="activeTab" class="budget-tabs">
          <!-- 预算分配标签页 -->
          <el-tab-pane label=" 预算分配详情" name="allocations">
            <el-card shadow="hover" class="section-card">
              <template #header>
                <div class="section-header">
                  <h3>预算分配详情</h3>
                  <el-button type="primary" @click="openUpdateAllocationDialog" round>
                    更改预算分配
                  </el-button>
                </div>
              </template>
              
              <div class="budget-allocation">
                <div v-for="(item, index) in budgetAllocations" :key="item.category || `budget-${index}`" class="allocation-item">
                  <div class="allocation-header">
                    <div class="category-info">
                      <span class="category-icon">{{ getCategoryIcon(item.category) }}</span>
                      <span class="category-name">{{ item.category }}</span>
                    </div>
                    <span class="allocation-amount">¥{{ item.amount.toLocaleString() }}</span>
                  </div>
                  <div class="progress-container">
                    <div class="progress-info">
                      <el-progress
                        :percentage="getBudgetUsagePercentage(item.category)"
                        :color="getCategoryColor(item.category)"
                        :stroke-width="16"
                        :show-text="false"
                      />
                      <span class="usage-text">
                        {{ getBudgetUsagePercentage(item.category) }}%
                      </span>
                    </div>
                    <div class="expense-amount">
                      ¥{{ expenses.filter(e => e.category === item.category).reduce((sum, e) => sum + e.amount, 0).toLocaleString() }}
                    </div>
                  </div>
                </div>
                
                <div v-if="budgetAllocations.length === 0" class="empty-allocation">
                  <el-empty description="暂无预算分配" :image-size="100">
                    <el-button type="primary" @click="openUpdateAllocationDialog">
                      设置预算分配
                    </el-button>
                  </el-empty>
                </div>
              </div>
            </el-card>
          </el-tab-pane>
          
          <!-- 费用记录标签页 -->
          <el-tab-pane label="费用记录" name="expenses">
            <el-card shadow="hover" class="section-card">
              <template #header>
                <div class="section-header">
                  <h3>费用记录</h3>
                  <el-button type="primary" @click="addExpenseDialogVisible = true" round>
                    添加费用
                  </el-button>
                </div>
              </template>
              
              <el-table :data="expenses" style="width: 100%" class="expenses-table">
                <el-table-column prop="date" label="日期" width="120" />
                <el-table-column prop="category" label="类别" width="100">
                  <template #default="scope">
                    <el-tag 
                      :color="getCategoryColor(scope.row.category)" 
                      effect="dark"
                      size="small"
                      class="category-tag"
                    >
                      {{ scope.row.category }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="name" label="名称" min-width="150" />
                <el-table-column prop="amount" label="金额" width="120">
                  <template #default="scope">
                    <span class="expense-amount">¥{{ scope.row.amount.toLocaleString() }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="description" label="描述" min-width="180" />
                <el-table-column label="操作" width="80" align="center">
                  <template #default="scope">
                    <el-button
                      type="danger"
                      text
                      size="small"
                      @click="deleteExpense(scope.row.id)"
                      icon="el-icon-delete"
                    >
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
              
              <div v-if="expenses.length === 0" class="empty-expenses">
                <el-empty description="暂无费用记录" :image-size="100">
                  <el-button type="primary" @click="addExpenseDialogVisible = true">
                    添加第一条费用记录
                  </el-button>
                </el-empty>
              </div>
            </el-card>
          </el-tab-pane>
        </el-tabs>

      </div>
    </div>
    
    <!-- 添加费用对话框 -->
    <el-dialog title="添加费用记录" v-model="addExpenseDialogVisible" width="500px" class="custom-dialog">
      <el-form :model="expenseForm" label-width="80px">
        <el-form-item label="日期">
          <el-date-picker
            v-model="expenseForm.date"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="名称">
          <el-input
            v-model="expenseForm.name"
            placeholder="请输入费用名称"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="类别">
          <el-select
            v-model="expenseForm.category"
            placeholder="选择费用类别"
            style="width: 100%"
          >
            <el-option
              v-for="category in expenseCategories"
              :key="category.value"
              :label="category.label"
              :value="category.value"
            >
              <span class="category-option">
                <span class="category-dot" :style="{backgroundColor: category.color}"></span>
                {{ category.label }}
              </span>
            </el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="金额">
          <el-input-number
            v-model="expenseForm.amount"
            :min="0"
            :step="0.01"
            placeholder="请输入金额"
            style="width: 100%"
            controls-position="right"
          />
        </el-form-item>
        
        <el-form-item label="描述">
          <el-input
            v-model="expenseForm.description"
            placeholder="请输入费用描述（可选）"
            type="textarea"
            :rows="3"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="addExpenseDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addExpense">确认添加</el-button>
      </template>
    </el-dialog>
    
    <!-- 修改预算分配对话框 -->
    <el-dialog title="更改预算分配" v-model="updateAllocationDialogVisible" width="600px" class="custom-dialog">
      <div class="allocation-form">
        <div v-for="category in allocationCategories" :key="category.value" class="allocation-form-item">
          <div class="allocation-label">
            <span class="category-icon">{{ category.icon }}</span>
            <span>{{ category.label }}</span>
          </div>
          <el-input-number
            v-model="allocationForm[category.value]"
            :min="0"
            :step="100"
            placeholder="0"
            controls-position="right"
            style="width: 200px"
          />
        </div>
      </div>
      
      <div class="allocation-total">
        总预算: ¥{{ Object.values(allocationForm).reduce((sum, amount) => sum + amount, 0).toLocaleString() }}
      </div>
      
      <template #footer>
        <el-button @click="updateAllocationDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updateBudgetAllocations">确认更新</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.budget-container {
  min-height: calc(100vh - 120px);
  background: linear-gradient(135deg, #f5f7fa 0%, #e4efe9 100%);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 头部区域 */
.budget-header {
    background: white;
    padding: 30px;
    border-radius: 16px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    margin-bottom: 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

.back-btn {
  color: #606266 !important;
  font-size: 14px;
  margin-top: 8px;
}

.header-title {
  flex: 1;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 8px 0;
  color: #303133;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-subtitle {
  font-size: 16px;
  color: #909399;
  margin: 0;
}

.loading-state {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

/* 预算概览 */
.budget-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.overview-card {
  border: none;
  border-radius: 16px;
  transition: all 0.3s ease;
  overflow: hidden;
}

.overview-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.overview-content {
  padding: 30px;
  display: flex;
  align-items: center;
  gap: 20px;
}

.card-icon {
  font-size: 48px;
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.total-budget .card-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.expense .card-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.remaining .card-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.remaining.danger .card-icon {
  background: linear-gradient(135deg, #f5576c 0%, #f093fb 100%);
}

.card-info {
  flex: 1;
}

.card-info h3 {
  font-size: 16px;
  color: #606266;
  margin: 0 0 8px 0;
  font-weight: 500;
}

.amount {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 12px 0;
  color: #303133;
}

.usage-rate {
  display: flex;
  align-items: center;
  gap: 12px;
}

.rate-text {
  font-size: 14px;
  color: #909399;
  font-weight: 500;
}

.budget-status {
  margin-top: 8px;
}

/* 选项卡区域 */
.budget-tabs {
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.section-card {
  border: none;
  border-radius: 0;
  box-shadow: none;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0;
}

.section-header h3 {
  margin: 0;
  font-size: 18px;
  color: #303133;
  font-weight: 600;
}

/* 预算分配 */
.budget-allocation {
  padding: 20px 0;
}

.allocation-item {
  margin-bottom: 30px;
  padding: 20px;
  border-radius: 12px;
  background: #f8f9fa;
  transition: all 0.3s ease;
}

.allocation-item:hover {
  background: #f0f7ff;
  transform: translateX(5px);
}

.allocation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.category-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.category-icon {
  font-size: 20px;
}

.category-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.allocation-amount {
  font-size: 18px;
  font-weight: 700;
  color: #409EFF;
}

.progress-container {
  display: flex;
  align-items: center;
  gap: 20px;
}

.progress-info {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 15px;
}

.progress-info .el-progress {
  flex: 1;
}

.usage-text {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
  min-width: 50px;
}

.expense-amount {
    font-size: 14px;
    color: #f56c6c;
    font-weight: 500;
  }
  
  .category-tag {
    font-weight: 500;
    padding: 4px 8px;
    border-radius: 4px;
    border: none !important;
  }

.empty-allocation {
  padding: 60px 0;
  text-align: center;
}

/* 费用表格 */
.expenses-table {
  margin-top: 10px;
}

.expenses-table :deep(.el-table__row) {
  transition: all 0.3s ease;
}

.expenses-table :deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}

.empty-expenses {
  padding: 60px 0;
  text-align: center;
}

/* 表单样式 */
.allocation-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-height: 400px;
  overflow-y: auto;
  padding-right: 10px;
}

.allocation-form-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.allocation-label {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.category-icon {
  font-size: 20px;
}

.allocation-total {
  margin-top: 20px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  text-align: center;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

/* 对话框样式 */
.custom-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

.custom-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  margin: 0;
  padding: 20px;
}

.custom-dialog :deep(.el-dialog__title) {
  color: white;
  font-weight: 600;
}

.custom-dialog :deep(.el-dialog__headerbtn) {
  top: 20px;
}

.custom-dialog :deep(.el-dialog__headerbtn .el-dialog__close) {
  color: white;
}

.category-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.category-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  display: inline-block;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .container {
    padding: 15px;
  }
  
  .budget-header {
    padding: 20px;
    flex-direction: column;
    gap: 15px;
  }
  
  .header-left {
    flex-direction: column;
    gap: 10px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .budget-overview {
    grid-template-columns: 1fr;
  }
  
  .overview-content {
    padding: 20px;
    flex-direction: column;
    text-align: center;
  }
  
  .card-icon {
    width: 60px;
    height: 60px;
    font-size: 32px;
  }
  
  .progress-container {
    flex-direction: column;
    gap: 10px;
    align-items: stretch;
  }
  
  .allocation-form-item {
    flex-direction: column;
    gap: 10px;
    align-items: stretch;
  }
}
</style>