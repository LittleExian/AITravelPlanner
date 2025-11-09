# API 接口文档

## 1. 概述

本文档详细描述了AITravelPlanner后端服务提供的所有RESTful API接口，包括认证、行程管理和预算管理等功能。

### 1.1 基础URL

所有API接口的基础URL为：`http://localhost:8080`

### 1.2 认证方式

大部分接口需要JWT认证。登录成功后，在请求头中携带：
```
Authorization: Bearer {token}
```

## 2. 认证接口 (Auth)

### 2.1 用户注册

**路径**: `/api/auth/register`

**方法**: `POST`

**描述**: 创建新用户账号

**请求体**:
```json
{
  "username": "用户名",
  "email": "邮箱地址",
  "password": "密码",
  "fullName": "姓名"
}
```

**成功响应** (200 OK):
```json
{
  "id": "用户ID",
  "username": "用户名",
  "email": "邮箱地址",
  "fullName": "姓名",
  "createdAt": "创建时间",
  "avatar": "头像URL",
  "active": true
}
```

### 2.2 用户登录

**路径**: `/api/auth/login`

**方法**: `POST`

**描述**: 用户登录并获取JWT令牌

**请求体**:
```json
{
  "email": "邮箱地址",
  "password": "密码"
}
```

**成功响应** (200 OK):
```json
{
  "token": "JWT令牌",
  "user": {
    "id": "用户ID",
    "username": "用户名",
    "email": "邮箱地址",
    "fullName": "姓名",
    "createdAt": "创建时间",
    "avatar": "头像URL",
    "active": true
  }
}
```

### 2.3 获取用户信息

**路径**: `/api/auth/profile`

**方法**: `GET`

**描述**: 获取当前登录用户的信息

**需要认证**: 是

**成功响应** (200 OK):
```json
{
  "id": "用户ID",
  "username": "用户名",
  "email": "邮箱地址",
  "fullName": "姓名",
  "createdAt": "创建时间",
  "avatar": "头像URL",
  "active": true
}
```

### 2.4 更新用户信息

**路径**: `/api/auth/profile`

**方法**: `PUT`

**描述**: 更新当前登录用户的信息

**需要认证**: 是

**请求体**:
```json
{
  "fullName": "新姓名",
  "avatar": "新头像URL"
}
```

**成功响应** (200 OK):
```json
{
  "id": "用户ID",
  "username": "用户名",
  "email": "邮箱地址",
  "fullName": "更新后的姓名",
  "createdAt": "创建时间",
  "avatar": "更新后的头像URL",
  "active": true
}
```

### 2.5 删除用户

**路径**: `/api/auth/profile`

**方法**: `DELETE`

**描述**: 删除当前登录用户的账号

**需要认证**: 是

**成功响应** (200 OK):
```
"用户已删除"
```

## 3. 行程管理接口 (Trips)

### 3.1 创建行程

**路径**: `/api/trips`

**方法**: `POST`

**描述**: 创建新的旅行行程

**需要认证**: 是

**请求体**:
```json
{
  "title": "行程标题",
  "destination": "目的地",
  "startDate": "开始日期",
  "endDate": "结束日期",
  "description": "行程描述",
  "isPublic": true
}
```

**成功响应** (200 OK):
```json
{
  "id": "行程ID",
  "title": "行程标题",
  "destination": "目的地",
  "startDate": "开始日期",
  "endDate": "结束日期",
  "description": "行程描述",
  "isPublic": true,
  "userId": "用户ID",
  "createdAt": "创建时间",
  "updatedAt": "更新时间"
}
```

### 3.2 获取用户行程列表

**路径**: `/api/trips/user/{userId}`

**方法**: `GET`

**描述**: 获取指定用户的所有行程

**需要认证**: 是

**权限**: 只能获取自己的行程

**路径参数**:
- `userId`: 用户ID

**成功响应** (200 OK):
```json
[
  {
    "id": "行程ID1",
    "title": "行程标题1",
    "destination": "目的地1",
    "startDate": "开始日期1",
    "endDate": "结束日期1",
    "description": "行程描述1",
    "isPublic": true,
    "userId": "用户ID",
    "createdAt": "创建时间1",
    "updatedAt": "更新时间1"
  },
  {
    "id": "行程ID2",
    "title": "行程标题2",
    "destination": "目的地2",
    "startDate": "开始日期2",
    "endDate": "结束日期2",
    "description": "行程描述2",
    "isPublic": false,
    "userId": "用户ID",
    "createdAt": "创建时间2",
    "updatedAt": "更新时间2"
  }
]
```

### 3.3 获取行程详情

**路径**: `/api/trips/{tripId}`

**方法**: `GET`

**描述**: 获取指定行程的详细信息

**需要认证**: 是

**权限**: 可以访问自己的行程或公开的行程

**路径参数**:
- `tripId`: 行程ID

**成功响应** (200 OK):
```json
{
  "id": "行程ID",
  "title": "行程标题",
  "destination": "目的地",
  "startDate": "开始日期",
  "endDate": "结束日期",
  "description": "行程描述",
  "isPublic": true,
  "userId": "用户ID",
  "createdAt": "创建时间",
  "updatedAt": "更新时间"
}
```

### 3.4 更新行程

**路径**: `/api/trips/{tripId}`

**方法**: `PUT`

**描述**: 更新指定行程的信息

**需要认证**: 是

**权限**: 只能更新自己的行程

**路径参数**:
- `tripId`: 行程ID

**请求体**:
```json
{
  "title": "新标题",
  "destination": "新目的地",
  "startDate": "新开始日期",
  "endDate": "新结束日期",
  "description": "新描述",
  "isPublic": false
}
```

**成功响应** (200 OK):
```json
{
  "id": "行程ID",
  "title": "更新后的标题",
  "destination": "更新后的目的地",
  "startDate": "更新后的开始日期",
  "endDate": "更新后的结束日期",
  "description": "更新后的描述",
  "isPublic": false,
  "userId": "用户ID",
  "createdAt": "创建时间",
  "updatedAt": "更新时间"
}
```

### 3.5 删除行程

**路径**: `/api/trips/{tripId}`

**方法**: `DELETE`

**描述**: 删除指定行程

**需要认证**: 是

**权限**: 只能删除自己的行程

**路径参数**:
- `tripId`: 行程ID

**成功响应** (200 OK):
```
"行程已删除"
```

### 3.6 获取所有公开行程

**路径**: `/api/trips/public/all`

**方法**: `GET`

**描述**: 获取所有用户标记为公开的行程

**需要认证**: 是

**成功响应** (200 OK):
```json
[
  {
    "id": "行程ID1",
    "title": "公开行程1",
    "destination": "目的地1",
    "startDate": "开始日期1",
    "endDate": "结束日期1",
    "description": "行程描述1",
    "isPublic": true,
    "userId": "用户ID1",
    "createdAt": "创建时间1",
    "updatedAt": "更新时间1"
  }
]
```

### 3.7 搜索公开行程

**路径**: `/api/trips/public/search`

**方法**: `GET`

**描述**: 根据目的地搜索公开行程

**需要认证**: 是

**查询参数**:
- `destination`: 目的地关键词

**成功响应** (200 OK):
```json
[
  {
    "id": "行程ID",
    "title": "行程标题",
    "destination": "匹配的目的地",
    "startDate": "开始日期",
    "endDate": "结束日期",
    "description": "行程描述",
    "isPublic": true,
    "userId": "用户ID",
    "createdAt": "创建时间",
    "updatedAt": "更新时间"
  }
]
```

## 4. 预算管理接口 (Budgets)

### 4.1 获取行程预算

**路径**: `/api/budgets/trip/{tripId}`

**方法**: `GET`

**描述**: 获取指定行程的预算信息

**需要认证**: 是

**权限**: 可以访问自己的行程预算或公开行程的预算

**路径参数**:
- `tripId`: 行程ID

**成功响应** (200 OK):
```json
{
  "id": "预算ID",
  "tripId": "行程ID",
  "totalBudget": 5000,
  "allocations": {
    "交通": 1000,
    "住宿": 2000,
    "餐饮": 1000,
    "景点门票": 500,
    "其他": 500
  },
  "expenses": [
    {
      "id": "费用ID",
      "category": "餐饮",
      "amount": 200,
      "description": "午餐",
      "date": "2023-10-01",
      "createdAt": "创建时间"
    }
  ],
  "createdAt": "创建时间",
  "updatedAt": "更新时间"
}
```

### 4.2 添加费用

**路径**: `/api/budgets/trip/{tripId}/expenses`

**方法**: `POST`

**描述**: 为行程添加费用记录

**需要认证**: 是

**权限**: 只能为自己的行程添加费用

**路径参数**:
- `tripId`: 行程ID

**请求体**:
```json
{
  "category": "费用类别",
  "amount": 200,
  "description": "费用描述",
  "date": "费用发生日期"
}
```

**成功响应** (200 OK):
```json
{
  "id": "费用ID",
  "category": "费用类别",
  "amount": 200,
  "description": "费用描述",
  "date": "费用发生日期",
  "createdAt": "创建时间"
}
```

### 4.3 删除费用

**路径**: `/api/budgets/trip/{tripId}/expenses/{expenseId}`

**方法**: `DELETE`

**描述**: 删除行程的指定费用记录

**需要认证**: 是

**权限**: 只能删除自己行程的费用

**路径参数**:
- `tripId`: 行程ID
- `expenseId`: 费用ID

**成功响应** (200 OK):
```
"费用已删除"
```

### 4.4 更新预算分配

**路径**: `/api/budgets/trip/{tripId}/allocations`

**方法**: `PUT`

**描述**: 更新行程的预算分配方案

**需要认证**: 是

**权限**: 只能更新自己行程的预算分配

**路径参数**:
- `tripId`: 行程ID

**请求体**:
```json
{
  "交通": 1200,
  "住宿": 1800,
  "餐饮": 1500,
  "景点门票": 500,
  "其他": 500
}
```

**成功响应** (200 OK):
```json
{
  "id": "预算ID",
  "tripId": "行程ID",
  "totalBudget": 5500,
  "allocations": {
    "交通": 1200,
    "住宿": 1800,
    "餐饮": 1500,
    "景点门票": 500,
    "其他": 500
  },
  "expenses": [...],
  "createdAt": "创建时间",
  "updatedAt": "更新时间"
}
```

## 5. 系统接口

### 5.1 健康检查

**路径**: `/api/health`

**方法**: `GET`

**描述**: 检查服务是否正常运行

**需要认证**: 否

**成功响应** (200 OK):
```json
{
  "status": "ok",
  "service": "AITravelPlanner Backend"
}
```

## 6. 权限说明

| 权限级别 | 可访问的接口 |
|---------|------------|
| 未认证用户 | 注册、登录、健康检查 |
| 已认证用户 | 所有接口，但受以下限制：<br>- 只能访问/修改/删除自己的行程和预算<br>- 可以访问公开行程和预算 |

## 7. 错误响应格式

当请求失败时，系统会返回相应的错误信息：

```json
{
  "timestamp": "错误发生时间",
  "status": 错误状态码,
  "error": "错误类型",
  "message": "错误描述",
  "path": "请求路径"
}
```

常见错误状态码：
- 400 Bad Request: 请求参数错误
- 401 Unauthorized: 未认证
- 403 Forbidden: 无权限访问
- 404 Not Found: 资源不存在
- 500 Internal Server Error: 服务器内部错误