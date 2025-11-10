import { createPinia } from 'pinia'
import { defineStore } from 'pinia'
import { authAPI, tripAPI, budgetAPI } from '../api'
import type { UserResponse } from '../api/auth'
import type { Trip } from '../api/trips'
import type { Budget, Expense, ExpenseAddRequest } from '../api/budgets'

// 用户状态管理
export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null as UserResponse | null,
    token: localStorage.getItem('token') || null,
    userId: localStorage.getItem('userId') || null,
    loading: false,
    error: null as string | null
  }),
  actions: {
    setUserInfo(userInfo: UserResponse | null) {
      this.userInfo = userInfo
      // 同时设置userId
      if (userInfo) {
        this.setUserId(userInfo.id)
      }
    },
    setUserId(userId: string | null) {
      this.userId = userId
      if (userId) {
        localStorage.setItem('userId', userId)
      } else {
        localStorage.removeItem('userId')
      }
    },
    setToken(token: string | null) {
      this.token = token
      if (token) {
        localStorage.setItem('token', token)
      } else {
        localStorage.removeItem('token')
      }
    },
    setError(error: string | null) {
      this.error = error
    },
    async login(email: string, password: string) {
      this.loading = true
      this.error = null
      try {
        const response = await authAPI.login({ email, password })
        this.setToken(response.token)
        this.setUserInfo(response.user)
        return response
      } catch (error: any) {
        this.setError(error || '登录失败，请检查邮箱和密码')
        throw error
      } finally {
        this.loading = false
      }
    },
    async register(username: string, email: string, phone: string, password: string, fullName: string) {
      this.loading = true
      this.error = null
      try {
        const user = await authAPI.register({ username, email, password, fullName, phone })
        return user
      } catch (error: any) {
        this.setError(error || '注册失败')
        throw error
      } finally {
        this.loading = false
      }
    },
    async fetchProfile() {
      this.loading = true
      this.error = null
      try {
        const userInfo = await authAPI.getProfile()
        
        // 设置用户信息和userId
        if (userInfo && userInfo.id) {
          this.setUserId(userInfo.id)
          this.setUserInfo(userInfo)
        }
        
        return userInfo
      } catch (error: any) {
        this.setError(error || '获取用户信息失败')
        // 如果获取用户信息失败，可能是token过期，清除token
        if (error?.includes('401')) {
          this.logout()
        }
        throw error
      } finally {
        this.loading = false
      }
    },
    logout() {
      this.userInfo = null
      this.token = null
      this.userId = null
      localStorage.removeItem('token')
      localStorage.removeItem('userId')
    }
  },
  getters: {
    isAuthenticated(): boolean {
      return !!this.token && !!this.userId
    }
  }
})

// 行程状态管理
export const useTripStore = defineStore('trip', {
  state: () => ({
    trips: [] as Trip[],
    currentTrip: null as Trip | null,
    loading: false,
    error: null as string | null
  }),
  actions: {
    async fetchUserTrips(userId: string) {
      this.loading = true
      this.error = null
      try {
        this.trips = await tripAPI.getUserTrips(userId)
        return this.trips
      } catch (error: any) {
        this.error = error || '获取行程列表失败'
        throw error
      } finally {
        this.loading = false
      }
    },
    async fetchTripById(tripId: string) {
      this.loading = true
      this.error = null
      try {
        const trip = await tripAPI.getTripById(tripId)
        this.currentTrip = trip
        return trip
      } catch (error: any) {
        this.error = error || '获取行程详情失败'
        throw error
      } finally {
        this.loading = false
      }
    },
    async createTrip(tripData: {
      title: string;
      destination: string;
      startDate: string;
      endDate: string;
      description: string;
      budgetAmount?: number;
      peopleCount?: number;
      travelPreferences?: string[];
    }) {
      this.loading = true
      this.error = null
      try {
        const newTrip = await tripAPI.createTrip(tripData)
        this.trips.push(newTrip)
        return newTrip
      } catch (error: any) {
        this.error = error || '创建行程失败'
        throw error
      } finally {
        this.loading = false
      }
    },
    async updateTrip(tripId: string, tripData: Partial<Trip>) {
      this.loading = true
      this.error = null
      try {
        const updatedTrip = await tripAPI.updateTrip(tripId, tripData)
        // 更新本地行程列表中的数据
        const index = this.trips.findIndex((t: Trip) => t.id === tripId)
        if (index !== -1) {
          this.trips[index] = updatedTrip
        }
        // 如果是当前行程，也更新当前行程
        if (this.currentTrip?.id === tripId) {
          this.currentTrip = updatedTrip
        }
        return updatedTrip
      } catch (error: any) {
        this.error = error || '更新行程失败'
        throw error
      } finally {
        this.loading = false
      }
    },
    async deleteTrip(tripId: string) {
      this.loading = true
      this.error = null
      try {
        await tripAPI.deleteTrip(tripId)
        // 从本地行程列表中删除
        this.trips = this.trips.filter((t: Trip) => t.id !== tripId)
        // 如果是当前行程，清除当前行程
        if (this.currentTrip?.id === tripId) {
          this.currentTrip = null
        }
      } catch (error: any) {
        this.error = error || '删除行程失败'
        throw error
      } finally {
        this.loading = false
      }
    },
    async fetchPublicTrips() {
      this.loading = true
      this.error = null
      try {
        const trips = await tripAPI.getPublicTrips()
        return trips
      } catch (error: any) {
        this.error = error || '获取公开行程失败'
        throw error
      } finally {
        this.loading = false
      }
    },
    async searchPublicTrips(destination: string) {
      this.loading = true
      this.error = null
      try {
        const trips = await tripAPI.searchPublicTrips(destination)
        return trips
      } catch (error: any) {
        this.error = error || '搜索公开行程失败'
        throw error
      } finally {
        this.loading = false
      }
    },
    setCurrentTrip(trip: Trip | null) {
      this.currentTrip = trip
    },
    clearError() {
      this.error = null
    }
  }
})

// 预算状态管理
export const useBudgetStore = defineStore('budget', {
  state: () => ({
    budgets: {} as Record<string, Budget>,
    expenses: {} as Record<string, Expense[]>,
    loading: false,
    error: null as string | null
  }),
  actions: {
    async fetchBudget(tripId: string) {
      this.loading = true
      this.error = null
      try {
        const budget = await budgetAPI.getTripBudget(tripId)
        this.budgets[tripId] = budget
        this.expenses[tripId] = budget.expenses || []
        return budget
      } catch (error: any) {
        this.error = error || '获取预算信息失败'
        throw error
      } finally {
        this.loading = false
      }
    },
    async addExpense(tripId: string, expenseData: ExpenseAddRequest) {
      this.loading = true
      this.error = null
      try {
        const newExpense = await budgetAPI.addExpense(tripId, expenseData)
        if (!this.expenses[tripId]) {
          this.expenses[tripId] = []
        }
        this.expenses[tripId].push(newExpense)
        // 重新获取预算以更新总预算和分配信息
        await this.fetchBudget(tripId)
        return newExpense
      } catch (error: any) {
        this.error = error || '添加费用失败'
        throw error
      } finally {
        this.loading = false
      }
    },
    async deleteExpense(tripId: string, expenseId: string) {
      this.loading = true
      this.error = null
      try {
        await budgetAPI.deleteExpense(tripId, expenseId)
        // 从本地费用列表中删除
        if (this.expenses[tripId]) {
          this.expenses[tripId] = this.expenses[tripId].filter(e => e.id !== expenseId)
        }
        // 重新获取预算以更新总预算和分配信息
        await this.fetchBudget(tripId)
      } catch (error: any) {
        this.error = error || '删除费用失败'
        throw error
      } finally {
        this.loading = false
      }
    },
    async updateBudgetAllocations(tripId: string, allocations: Record<string, number>) {
      this.loading = true
      this.error = null
      try {
        const updatedBudget = await budgetAPI.updateBudgetAllocations(tripId, allocations)
        this.budgets[tripId] = updatedBudget
        return updatedBudget
      } catch (error: any) {
        this.error = error || '更新预算分配失败'
        throw error
      } finally {
        this.loading = false
      }
    },
    clearError() {
      this.error = null
    }
  },
  getters: {
    getBudgetForTrip: (state) => (tripId: string) => {
      return state.budgets[tripId] || null
    },
    getExpensesForTrip: (state) => (tripId: string) => {
      return state.expenses[tripId] || []
    }
  }
})

const pinia = createPinia()
export default pinia