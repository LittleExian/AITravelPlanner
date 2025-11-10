import apiClient from './axios';

// 类型定义
export interface UserRegisterRequest {
  username: string;
  email: string;
  password: string;
  fullName: string;
  phone?: string;
}

export interface UserLoginRequest {
  email: string;
  password: string;
}

export interface UserResponse {
  id: string;
  username: string;
  email: string;
  fullName: string;
  phone?: string | null;
  bio?: string | null;
  createdAt: string;
  avatar?: string | null;
  active: boolean;
}

export interface TokenResponse {
  token: string;
  user: UserResponse;
}

class AuthAPI {
  /**
   * 用户注册
   * @param data 注册信息
   * @returns 注册成功的用户信息
   */
  async register(data: UserRegisterRequest): Promise<UserResponse> {
    return apiClient.post('/auth/register', data);
  }

  /**
   * 用户登录
   * @param data 登录信息
   * @returns 包含token和用户信息的响应
   */
  async login(data: UserLoginRequest): Promise<TokenResponse> {
    // 由于响应拦截器已经返回了response.data，所以直接返回结果即可
    return apiClient.post('/auth/login', data);
  }

  /**
   * 获取当前用户信息
   * @returns 当前登录用户的信息
   */
  async getProfile(): Promise<UserResponse> {
    return apiClient.get('/auth/profile');
  }

  /**
   * 更新用户信息
   * @param data 用户更新信息
   * @returns 更新后的用户信息
   */
  async updateProfile(data: Partial<UserResponse>): Promise<UserResponse> {
    return apiClient.put('/auth/profile', data);
  }

  /**
   * 删除用户账号
   * @returns 删除结果
   */
  async deleteProfile(): Promise<void> {
    // 由于响应拦截器已经返回了response.data，所以直接返回结果即可
    await apiClient.delete('/auth/profile');
    // 删除账号后清除token
    localStorage.removeItem('token');
    return;
  }

  /**
   * 用户登出
   */
  logout(): void {
    localStorage.removeItem('token');
  }

  /**
   * 检查用户是否已登录
   * @returns 是否已登录
   */
  isAuthenticated(): boolean {
    return !!localStorage.getItem('token');
  }
}

export default new AuthAPI();