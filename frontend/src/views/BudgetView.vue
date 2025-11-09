<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElButton, ElTabs, ElTabPane, ElCard, ElForm, ElFormItem, ElInput, ElDatePicker, ElSelect, ElOption, ElDialog, ElTable, ElTableColumn, ElInputNumber } from 'element-plus'
import { useBudgetStore } from '../store'

const route = useRoute()
const budgetStore = useBudgetStore()

const tripId = route.params.tripId as string
const tripName = ref('北京三日游') // 模拟行程名称
const loading = ref(true)

// 预算数据
const budgets = ref<any>(null)
const expenses = ref<any[]>([])

// 添加费用对话框
const addExpenseDialogVisible = ref(false)
const expenseForm = reactive({
  date: '',
  category: '',
  amount: 0,
  description: ''
})

// 费用分类选项
const expenseCategories = [
  { label: '交通', value: '交通' },
  { label: '住宿', value: '住宿' },
  { label: '餐饮', value: '餐饮' },
  { label: '景点', value: '景点' },
  { label: '购物', value: '购物' },
  { label: '其他', value: '其他' }
]

// 计算总预算和总支出
const totalBudget = ref(0)
const totalExpense = ref(0)
const remainingBudget = ref(0)

onMounted(async () => {
  await loadBudgetData()
})

const loadBudgetData = async () => {
  loading.value = true
  try {
    // 调用store获取预算数据
    await budgetStore.fetchBudget(tripId)
    if (budgetStore.budgets[tripId]) {
      budgets.value = budgetStore.budgets[tripId]
    }
    expenses.value = budgetStore.expenses[tripId] || []
    
    // 计算统计数据
    calculateBudgetStats()
  } catch (error) {
    ElMessage.error('加载预算数据失败')
    console.error('加载预算错误:', error)
  } finally {
    loading.value = false
  }
}

const calculateBudgetStats = () => {
  if (budgets.value && Array.isArray(budgets.value)) {
    totalBudget.value = budgets.value.reduce((sum: number, item: any) => sum + item.amount, 0)
  }
  totalExpense.value = expenses.value.reduce((sum: number, item: any) => sum + item.amount, 0)
  remainingBudget.value = totalBudget.value - totalExpense.value
}

// 添加费用
const addExpense = () => {
  if (!expenseForm.date || !expenseForm.category || expenseForm.amount <= 0) {
    ElMessage.warning('请填写完整的费用信息')
    return
  }
  
  const newExpense = {
    id: Date.now().toString(),
    ...expenseForm
  }
  
  budgetStore.addExpense(tripId, newExpense)
  expenses.value = budgetStore.expenses[tripId] || []
  
  calculateBudgetStats()
  addExpenseDialogVisible.value = false
  
  // 重置表单
  Object.assign(expenseForm, {
    date: '',
    category: '',
    amount: 0,
    description: ''
  })
  
  ElMessage.success('费用添加成功')
}

// 删除费用
const deleteExpense = (expenseId: string) => {
  // 在实际项目中，这里会调用API删除
  const index = expenses.value.findIndex(item => item.id === expenseId)
  if (index > -1) {
    expenses.value.splice(index, 1)
    calculateBudgetStats()
    ElMessage.success('费用删除成功')
  }
}

// 获取预算使用百分比
const getBudgetUsagePercentage = (category: string) => {
  const categoryBudget = budgets.value && Array.isArray(budgets.value) 
    ? budgets.value.find((item: any) => item.category === category)?.amount || 0 
    : 0
  const categoryExpense = expenses.value
    .filter(item => item.category === category)
    .reduce((sum, item) => sum + item.amount, 0)
  
  return categoryBudget > 0 ? Math.round((categoryExpense / categoryBudget) * 100) : 0
}

// 获取剩余预算状态颜色
const getRemainingBudgetColor = () => {
  if (remainingBudget.value < 0) return 'danger'
  if (remainingBudget.value / totalBudget.value < 0.2) return 'warning'
  return 'success'
}
</script>

<template>
  <div class="budget-container">
    <div class="container">
      <h2 class="page-title">{{ tripName }} - 预算管理</h2>
      
      <div v-if="loading" class="loading-state">
        <el-skeleton :rows="6" animated />
      </div>
      
      <div v-else class="budget-content">
        <!-- 预算概览卡片 -->
        <div class="budget-overview">
          <el-card shadow="hover" class="overview-card">
            <div class="overview-content">
              <h3>总预算</h3>
              <p class="amount">¥{{ totalBudget.toLocaleString() }}</p>
            </div>
          </el-card>
          
          <el-card shadow="hover" class="overview-card">
            <div class="overview-content">
              <h3>已支出</h3>
              <p class="amount expense">¥{{ totalExpense.toLocaleString() }}</p>
            </div>
          </el-card>
          
          <el-card shadow="hover" class="overview-card" :class="getRemainingBudgetColor()">
            <div class="overview-content">
              <h3>剩余预算</h3>
              <p class="amount">¥{{ remainingBudget.toLocaleString() }}</p>
            </div>
          </el-card>
        </div>
        
        <!-- 预算管理选项卡 -->
        <el-tabs class="budget-tabs">
          <!-- 预算分配 -->
          <el-tab-pane label="预算分配" name="allocation">
            <el-card shadow="hover">
              <h3>预算分配详情</h3>
              <div class="budget-allocation">
                <div v-for="item in budgets" :key="item.category" class="allocation-item">
                  <div class="allocation-header">
                    <span class="category-name">{{ item.category }}</span>
                    <span class="allocation-amount">¥{{ item.amount }}</span>
                  </div>
                  <div class="progress-container">
                    <el-progress
                      :percentage="getBudgetUsagePercentage(item.category)"
                      :status="getBudgetUsagePercentage(item.category) > 100 ? 'exception' : 'success'"
                      :stroke-width="20"
                    />
                    <span class="usage-text">
                      使用 {{ getBudgetUsagePercentage(item.category) }}%
                    </span>
                  </div>
                </div>
              </div>
            </el-card>
          </el-tab-pane>
          
          <!-- 费用记录 -->
          <el-tab-pane label="费用记录" name="expenses">
            <div class="expenses-section">
              <div class="expenses-header">
                <h3>费用记录</h3>
                <el-button type="primary" @click="addExpenseDialogVisible = true">
                  添加费用
                </el-button>
              </div>
              
              <el-table :data="expenses" style="width: 100%">
                <el-table-column prop="date" label="日期" />
                <el-table-column prop="category" label="类别" />
                <el-table-column prop="amount" label="金额" :formatter="(row) => `¥${row.amount}`" />
                <el-table-column prop="description" label="描述" />
                <el-table-column label="操作" width="100">
                  <template #default="scope">
                    <el-button
                      type="danger"
                      text
                      @click="deleteExpense(scope.row.id)"
                    >
                      删除
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
              
              <div v-if="expenses.length === 0" class="empty-expenses">
                <el-empty description="暂无费用记录" />
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
    
    <!-- 添加费用对话框 -->
    <el-dialog title="添加费用记录" v-model="addExpenseDialogVisible" width="500px">
      <el-form :model="expenseForm">
        <el-form-item label="日期">
          <el-date-picker
            v-model="expenseForm.date"
            type="date"
            placeholder="选择日期"
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
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="金额">
          <el-input-number
            v-model="expenseForm.amount"
            :min="0"
            :step="0.01"
            placeholder="请输入金额"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="描述">
          <el-input
            v-model="expenseForm.description"
            placeholder="请输入费用描述（可选）"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="addExpenseDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addExpense">确认添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.budget-container {
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

.loading-state {
  background: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.budget-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.overview-card {
  text-align: center;
  transition: all 0.3s ease;
}

.overview-card:hover {
  transform: translateY(-5px);
}

.overview-content h3 {
  font-size: 16px;
  color: #606266;
  margin-bottom: 10px;
}

.amount {
  font-size: 32px;
  font-weight: 600;
  margin: 0;
}

.amount.expense {
  color: #f56c6c;
}

.overview-card.danger .amount {
  color: #f56c6c;
}

.overview-card.warning .amount {
  color: #e6a23c;
}

.overview-card.success .amount {
  color: #67c23a;
}

.budget-tabs {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.budget-allocation {
  padding: 20px 0;
}

.allocation-item {
  margin-bottom: 30px;
}

.allocation-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-weight: 500;
}

.progress-container {
  display: flex;
  align-items: center;
  gap: 20px;
}

.progress-container .el-progress {
  flex: 1;
}

.usage-text {
  font-size: 14px;
  color: #606266;
  min-width: 80px;
}

.expenses-section {
  padding: 20px;
}

.expenses-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.empty-expenses {
  padding: 40px 0;
  text-align: center;
}
</style>