# AITravelPlanner Backend

## 项目简介

AITravelPlanner后端服务，基于Spring Boot框架开发，提供用户管理、行程规划、预算管理等功能。

## 技术栈

- Java 17
- Spring Boot 3.2.0
- Spring Data MongoDB
- Spring Security + JWT
- Maven

## 环境要求

- JDK 17或更高版本
- Maven 3.6.0或更高版本
- MongoDB 4.0或更高版本

## 快速开始

### 1. 环境准备

- 安装JDK 17
- 安装Maven
- 启动MongoDB服务

### 2. 配置修改

编辑`src/main/resources/application.yml`文件，配置MongoDB连接信息和JWT密钥。

### 3. 构建项目

```bash
mvn clean install
```

### 4. 运行项目

```bash
mvn spring-boot:run
```

## API端点

### 用户管理
- POST /api/auth/register - 用户注册
- POST /api/auth/login - 用户登录
- GET /api/auth/profile - 获取用户信息
- PUT /api/auth/profile - 更新用户信息
- DELETE /api/auth/profile - 删除用户

### 行程管理
- POST /api/trips - 创建行程
- GET /api/trips/user/{userId} - 获取用户行程列表
- GET /api/trips/{tripId} - 获取单个行程
- PUT /api/trips/{tripId} - 更新行程
- DELETE /api/trips/{tripId} - 删除行程

### 预算管理
- GET /api/budgets/trip/{tripId} - 获取行程预算
- POST /api/budgets/trip/{tripId}/expenses - 添加费用
- DELETE /api/budgets/trip/{tripId}/expenses/{expenseId} - 删除费用
- PUT /api/budgets/trip/{tripId}/allocations - 更新预算分配

## 项目结构

```
src/main/java/com/aitravelplanner/
├── Application.java                  # 应用入口
├── config/                           # 配置类
├── controller/                       # 控制器
├── dto/                              # 数据传输对象
├── exception/                        # 异常处理
├── model/                            # 数据模型
├── repository/                       # 数据访问层
├── security/                         # 安全相关
└── service/                          # 业务逻辑层
    └── impl/                         # 业务实现
```

## 注意事项

1. 首次运行时，MongoDB需要处于运行状态
2. 确保JWT密钥在生产环境中使用安全的随机字符串
3. 生产环境中请配置HTTPS以保证通信安全