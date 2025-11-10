import axios from 'axios';
import type { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse } from 'axios';

// 创建axios实例
const apiClient: AxiosInstance = axios.create({
  baseURL: 'http://localhost:8080/api', // 后端API基础URL
  timeout: 10000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 从localStorage获取token
    const token = localStorage.getItem('token');
    
    // 如果有token，添加到请求头
    if (token) {
      config.headers.set('Authorization', `Bearer ${token}`);
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
apiClient.interceptors.response.use(
  (response: AxiosResponse) => {
    return response.data;
  },
  (error) => {
    // 处理错误响应
    if (error.response) {
      // 服务器返回错误状态码
      const { status, data } = error.response;
      
      // 处理401未授权错误
      if (status === 401) {
        // 清除token和userId并跳转到登录页
        localStorage.removeItem('token');
        localStorage.removeItem('userId');
        window.location.href = '/login';
      }
      
      // 返回错误信息
      return Promise.reject(data?.message || '请求失败');
    } else if (error.request) {
      // 请求已发出但没有收到响应
      return Promise.reject('网络错误，请检查您的网络连接');
    } else {
      // 请求配置出错
      return Promise.reject(error.message);
    }
  }
);

export default apiClient;