import apiClient from './axios';

// 类型定义
export interface ExpenseAddRequest {
  category: string;
  amount: number;
  description: string;
  date: string;
}

export interface Expense {
  id: string;
  category: string;
  amount: number;
  description: string;
  date: string;
  createdAt: string;
}

export interface Budget {
  id: string;
  tripId: string;
  totalBudget: number;
  allocations: Record<string, number>;
  expenses: Expense[];
  createdAt: string;
  updatedAt: string;
}

class BudgetAPI {
  /**
   * 获取行程预算
   * @param tripId 行程ID
   * @returns 行程预算信息
   */
  async getTripBudget(tripId: string): Promise<Budget> {
    return apiClient.get(`/budgets/trip/${tripId}`);
  }

  /**
   * 添加费用
   * @param tripId 行程ID
   * @param data 费用信息
   * @returns 添加的费用信息
   */
  async addExpense(tripId: string, data: ExpenseAddRequest): Promise<Expense> {
    return apiClient.post(`/budgets/trip/${tripId}/expenses`, data);
  }

  /**
   * 删除费用
   * @param tripId 行程ID
   * @param expenseId 费用ID
   * @returns 删除结果
   */
  async deleteExpense(tripId: string, expenseId: string): Promise<string> {
    return apiClient.delete(`/budgets/trip/${tripId}/expenses/${expenseId}`);
  }

  /**
   * 更新预算分配
   * @param tripId 行程ID
   * @param allocations 预算分配方案
   * @returns 更新后的预算信息
   */
  async updateBudgetAllocations(tripId: string, allocations: Record<string, number>): Promise<Budget> {
    return apiClient.put(`/budgets/trip/${tripId}/allocations`, allocations);
  }
}

export default new BudgetAPI();