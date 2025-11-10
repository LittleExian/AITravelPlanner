// 导出所有API模块
import authAPI from './auth';
import tripAPI from './trips';
import budgetAPI from './budgets';
import routeAPI from './routes';
import apiClient from './axios';

// 健康检查API
const healthAPI = {
  async checkHealth() {
    return apiClient.get('/health');
  }
};

// 统一导出所有API
export {
  authAPI,
  tripAPI,
  budgetAPI,
  routeAPI,
  healthAPI,
  apiClient
};

export default {
  auth: authAPI,
  trips: tripAPI,
  budgets: budgetAPI,
  routes: routeAPI,
  health: healthAPI,
  client: apiClient
};