import apiClient from './axios';

// 类型定义
export interface TripCreateRequest {
  title: string;
  destination: string;
  startDate: string;
  endDate: string;
  description: string;
  isPublic: boolean;
}

export interface Trip {
  id: string;
  title: string;
  destination: string;
  startDate: string;
  endDate: string;
  description: string;
  isPublic: boolean;
  userId: string;
  createdAt: string;
  updatedAt: string;
}

class TripAPI {
  /**
   * 创建行程
   * @param data 行程创建信息
   * @returns 创建的行程信息
   */
  async createTrip(data: TripCreateRequest): Promise<Trip> {
    return apiClient.post('/trips', data);
  }

  /**
   * 获取用户行程列表
   * @param userId 用户ID
   * @returns 用户的行程列表
   */
  async getUserTrips(userId: string): Promise<Trip[]> {
    return apiClient.get(`/trips/user/${userId}`);
  }

  /**
   * 获取行程详情
   * @param tripId 行程ID
   * @returns 行程详细信息
   */
  async getTripById(tripId: string): Promise<Trip> {
    return apiClient.get(`/trips/${tripId}`);
  }

  /**
   * 更新行程
   * @param tripId 行程ID
   * @param data 行程更新信息
   * @returns 更新后的行程信息
   */
  async updateTrip(tripId: string, data: Partial<Trip>): Promise<Trip> {
    return apiClient.put(`/trips/${tripId}`, data);
  }

  /**
   * 删除行程
   * @param tripId 行程ID
   * @returns 删除结果
   */
  async deleteTrip(tripId: string): Promise<string> {
    return apiClient.delete(`/trips/${tripId}`);
  }

  /**
   * 获取所有公开行程
   * @returns 公开行程列表
   */
  async getPublicTrips(): Promise<Trip[]> {
    return apiClient.get('/trips/public/all');
  }

  /**
   * 搜索公开行程
   * @param destination 目的地关键词
   * @returns 匹配的公开行程列表
   */
  async searchPublicTrips(destination: string): Promise<Trip[]> {
    return apiClient.get(`/trips/public/search?destination=${encodeURIComponent(destination)}`);
  }
}

export default new TripAPI();