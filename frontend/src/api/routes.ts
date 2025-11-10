import apiClient from './axios';

// 类型定义
export interface Route {
  id: string;
  tripId: string;
  dayNumber: number;
  transportation: string;
  attractions: string[];
  restaurants: string[];
  description: string;
  estimatedCost: number;
  createdAt?: string;
  updatedAt?: string;
}

class RouteAPI {
  /**
   * 获取行程的所有路线信息
   * @param tripId 行程ID
   * @returns 路线列表
   */
  async getRoutesByTripId(tripId: string): Promise<Route[]> {
    return apiClient.get(`/routes/trip/${tripId}`);
  }

  /**
   * 获取单日路线信息
   * @param routeId 路线ID
   * @returns 路线详细信息
   */
  async getRouteById(routeId: string): Promise<Route> {
    return apiClient.get(`/routes/${routeId}`);
  }

  /**
   * 创建新路线
   * @param routeData 路线数据
   * @returns 创建的路线信息
   */
  async createRoute(routeData: Partial<Route>): Promise<Route> {
    return apiClient.post('/routes', routeData);
  }

  /**
   * 更新路线信息
   * @param routeId 路线ID
   * @param routeData 更新的路线数据
   * @returns 更新后的路线信息
   */
  async updateRoute(routeId: string, routeData: Partial<Route>): Promise<Route> {
    return apiClient.put(`/routes/${routeId}`, routeData);
  }

  /**
   * 删除路线
   * @param routeId 路线ID
   * @returns 删除结果
   */
  async deleteRoute(routeId: string): Promise<string> {
    return apiClient.delete(`/routes/${routeId}`);
  }
}

export default new RouteAPI();