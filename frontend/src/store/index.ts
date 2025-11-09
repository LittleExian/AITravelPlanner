import { createPinia } from 'pinia'
import { defineStore } from 'pinia'

// 用户状态管理
export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null as any,
    token: localStorage.getItem('token') || null
  }),
  actions: {
    setUserInfo(userInfo: any) {
      this.userInfo = userInfo
    },
    setToken(token: string) {
      this.token = token
      localStorage.setItem('token', token)
    },
    logout() {
      this.userInfo = null
      this.token = null
      localStorage.removeItem('token')
    }
  }
})

// 行程状态管理
export const useTripStore = defineStore('trip', {
  state: () => ({
    trips: [] as any[],
    currentTrip: null as any,
    loading: false
  }),
  actions: {
    async fetchTrips() {
      this.loading = true
      // 模拟API调用
      try {
        // 实际项目中这里会调用后端API
        this.trips = [
          {
            id: '1',
            name: '北京三日游',
            destination: '北京',
            startDate: '2025-12-01',
            endDate: '2025-12-03',
            budget: 3000,
            peopleCount: 2
          },
          {
            id: '2',
            name: '上海周末游',
            destination: '上海',
            startDate: '2025-12-15',
            endDate: '2025-12-16',
            budget: 2000,
            peopleCount: 1
          }
        ]
      } catch (error) {
        console.error('获取行程失败:', error)
      } finally {
        this.loading = false
      }
    },
    setCurrentTrip(trip: any) {
      this.currentTrip = trip
    }
  }
})

// 预算状态管理
export const useBudgetStore = defineStore('budget', {
  state: () => ({
    budgets: {} as Record<string, any[]>,
    expenses: {} as Record<string, any[]>
  }),
  actions: {
    async fetchBudget(tripId: string) {
      // 模拟API调用
      try {
        // 实际项目中这里会调用后端API
        this.budgets[tripId] = [
          { category: '交通', amount: 800 },
          { category: '住宿', amount: 1200 },
          { category: '餐饮', amount: 600 },
          { category: '景点', amount: 300 },
          { category: '购物', amount: 100 }
        ]
        this.expenses[tripId] = [
          { id: '1', date: '2025-12-01', category: '交通', amount: 200, description: '地铁卡充值' },
          { id: '2', date: '2025-12-01', category: '餐饮', amount: 150, description: '晚餐' },
          { id: '3', date: '2025-12-02', category: '景点', amount: 120, description: '故宫门票' }
        ]
      } catch (error) {
        console.error('获取预算失败:', error)
      }
    },
    addExpense(tripId: string, expense: any) {
      if (!this.expenses[tripId]) {
        this.expenses[tripId] = []
      }
      this.expenses[tripId].push(expense)
    }
  }
})

const pinia = createPinia()
export default pinia