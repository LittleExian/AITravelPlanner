# API 接口文档

## 1. 概述

本文档详细描述了AITravelPlanner后端服务提供的所有RESTful API接口，包括认证、行程管理和预算管理等功能。

### 1.1 基础URL

所有API接口的基础URL为：`http://localhost:8080`

### 1.2 认证方式

目前所有接口均无需JWT认证，可直接调用。

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

**需要认证**: 是（此接口仍保留认证要求）

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

**需要认证**: 是（此接口仍保留认证要求）

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

**需要认证**: 是（此接口仍保留认证要求）

**成功响应** (200 OK):
```
"行程已删除"
```

## 3. 行程管理接口 (Trips)

### 3.1 创建行程

**路径**: `/api/trips`

**方法**: `POST`

**描述**: 创建新的行程计划

**需要认证**: 否

**请求体**:
```json
{
  "userId": "6910d895128cab7ac1dc25ae",
  "title": "北京五日游",
  "destination": "北京",
  "startDate": "2023-10-01T00:00:00.000Z",
  "endDate": "2023-10-05T00:00:00.000Z",
  "description": "国庆节北京旅行",
  "tags": ["国庆", "城市", "文化"],
  "coverImage": "https://example.com/beijing.jpg",
  "budgetAmount": 5000.0,
  "peopleCount": 2,
  "travelPreferences": ["博物馆", "美食", "购物"]
}
```

**成功响应** (200 OK):

```json
{
  "id": "6911b52378a7695ba09bb99e",
  "userId": "6910d895128cab7ac1dc25ae",
  "title": "北京五日游",
  "destination": "北京",
  "startDate": "2023-10-01T00:00:00.000+00:00",
  "endDate": "2023-10-05T00:00:00.000+00:00",
  "description": "国庆节北京旅行",
  "tags": [
    "国庆",
    "城市",
    "文化"
  ],
  "coverImage": "https://example.com/beijing.jpg",
  "createdAt": "2025-11-10T09:49:23.552+00:00",
  "updatedAt": "2025-11-10T09:49:23.552+00:00",
  "budget": null,
  "budgetAmount": 5000,
  "peopleCount": 2,
  "travelPreferences": [
    "博物馆",
    "美食",
    "购物"
  ]
}
```

### 3.2 获取用户行程

**路径**: `/api/trips/user/{userId}`

**方法**: `GET`

**描述**: 根据用户ID获取用户的所有行程

**需要认证**: 否

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
    "budgetAmount": 5000,
    "peopleCount": 2,
    "travelPreferences": ["美食", "文化", "购物"],
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
    "budgetAmount": 8000,
    "peopleCount": 4,
    "travelPreferences": ["自然风光", "户外探险"],
    "userId": "用户ID",
    "createdAt": "创建时间2",
    "updatedAt": "更新时间2"
  }
]
```

## 行程对象 (Trip)

行程对象包含以下字段：
- `id`: 行程ID
- `title`: 行程标题
- `destination`: 目的地
- `startDate`: 开始日期
- `endDate`: 结束日期
- `description`: 行程描述
- `budgetAmount`: 预算金额
- `peopleCount`: 同行人数
- `travelPreferences`: 旅行偏好（数组）
- `userId`: 用户ID
- `createdAt`: 创建时间
- `updatedAt`: 更新时间

### 3.3 获取行程详情

**路径**: `/api/trips/{tripId}`

**方法**: `GET`

**描述**: 获取指定行程的详细信息

**需要认证**: 否

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
  "budgetAmount": 5000,
  "peopleCount": 2,
  "travelPreferences": ["美食", "文化", "购物"],
  "userId": "用户ID",
  "createdAt": "创建时间",
  "updatedAt": "更新时间"
}
```

### 3.4 更新行程

**路径**: `/api/trips/{tripId}`

**方法**: `PUT`

**描述**: 更新指定行程的信息

**需要认证**: 否

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
  "budgetAmount": 5000,
  "peopleCount": 2,
  "travelPreferences": ["美食", "文化", "购物"]
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
  "budgetAmount": 5000,
  "peopleCount": 2,
  "travelPreferences": ["美食", "文化", "购物"],
  "userId": "用户ID",
  "createdAt": "创建时间",
  "updatedAt": "更新时间"
}
```

### 3.5 删除行程

**路径**: `/api/trips/{tripId}`

**方法**: `DELETE`

**描述**: 删除指定行程

**需要认证**: 否

**路径参数**:
- `tripId`: 行程ID

**成功响应** (200 OK):
```
"行程已删除"
```

### 3.6 获取所有行程

**路径**: `/api/trips/public/all`

**方法**: `GET`

**描述**: 获取所有用户的行程

**需要认证**: 否

**成功响应** (200 OK):
```json
[
  {
    "id": "行程ID1",
    "title": "行程1",
    "destination": "目的地1",
    "startDate": "开始日期1",
    "endDate": "结束日期1",
    "description": "行程描述1",
    "budgetAmount": 5000,
    "peopleCount": 2,
    "travelPreferences": ["美食", "文化", "购物"],
    "userId": "用户ID1",
    "createdAt": "创建时间1",
    "updatedAt": "更新时间1"
  }
]
```

### 3.7 搜索行程

**路径**: `/api/trips/public/search`

**方法**: `GET`

**描述**: 根据目的地搜索所有用户的行程

**需要认证**: 否

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
    "budgetAmount": 5000,
    "peopleCount": 2,
    "travelPreferences": ["美食", "文化", "购物"],
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

**需要认证**: 否

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

**需要认证**: 否

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

**需要认证**: 否

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

**需要认证**: 否

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

## 5. 旅行路线接口 (Travel Routes)

### 5.1 创建旅行路线

**路径**: `/api/routes`

**方法**: `POST`

**描述**: 为指定行程创建一条旅行路线

**需要认证**: 否

**请求体**:
```json
{
  "tripId": "行程ID",
  "dayNumber": 1,
  "transportation": "地铁2号线转公交车",
  "attractions": ["故宫博物院", "天安门广场"],
  "restaurants": ["全聚德烤鸭店", "老北京炸酱面馆"],
  "description": "第一天的行程安排",
  "estimatedCost": 500
}
```

**成功响应** (200 OK):
```json
{
  "id": "路线ID",
  "tripId": "行程ID",
  "dayNumber": 1,
  "transportation": "地铁2号线转公交车",
  "attractions": ["故宫博物院", "天安门广场"],
  "restaurants": ["全聚德烤鸭店", "老北京炸酱面馆"],
  "description": "第一天的行程安排",
  "estimatedCost": 500,
  "createdAt": "创建时间",
  "updatedAt": "更新时间"
}
```

### 5.2 获取行程的所有路线

**路径**: `/api/routes/trip/{tripId}`

**方法**: `GET`

**描述**: 获取指定行程的所有旅行路线，按天数排序

**需要认证**: 否

**路径参数**:
- `tripId`: 行程ID

**成功响应** (200 OK):
```json
[
  {
    "id": "路线ID1",
    "tripId": "行程ID",
    "dayNumber": 1,
    "transportation": "地铁2号线转公交车",
    "attractions": ["故宫博物院", "天安门广场"],
    "restaurants": ["全聚德烤鸭店", "老北京炸酱面馆"],
    "description": "第一天的行程安排",
    "estimatedCost": 500,
    "createdAt": "创建时间1",
    "updatedAt": "更新时间1"
  },
  {
    "id": "路线ID2",
    "tripId": "行程ID",
    "dayNumber": 2,
    "transportation": "出租车",
    "attractions": ["颐和园", "圆明园"],
    "restaurants": ["海底捞火锅", "绿茶餐厅"],
    "description": "第二天的行程安排",
    "estimatedCost": 800,
    "createdAt": "创建时间2",
    "updatedAt": "更新时间2"
  }
]
```

### 5.3 获取单个路线详情

**路径**: `/api/routes/{routeId}`

**方法**: `GET`

**描述**: 获取指定旅行路线的详细信息

**需要认证**: 否

**路径参数**:
- `routeId`: 路线ID

**成功响应** (200 OK):
```json
{
  "id": "路线ID",
  "tripId": "行程ID",
  "dayNumber": 1,
  "transportation": "地铁2号线转公交车",
  "attractions": ["故宫博物院", "天安门广场"],
  "restaurants": ["全聚德烤鸭店", "老北京炸酱面馆"],
  "description": "第一天的行程安排",
  "estimatedCost": 500,
  "createdAt": "创建时间",
  "updatedAt": "更新时间"
}
```

### 5.4 更新旅行路线

**路径**: `/api/routes/{routeId}`

**方法**: `PUT`

**描述**: 更新指定旅行路线的信息

**需要认证**: 否

**路径参数**:
- `routeId`: 路线ID

**请求体**:
```json
{
  "dayNumber": 1,
  "transportation": "地铁2号线",
  "attractions": ["故宫博物院", "天安门广场", "景山公园"],
  "restaurants": ["全聚德烤鸭店", "老北京炸酱面馆"],
  "description": "更新后的第一天行程安排",
  "estimatedCost": 600
}
```

**成功响应** (200 OK):
```json
{
  "id": "路线ID",
  "tripId": "行程ID",
  "dayNumber": 1,
  "transportation": "地铁2号线",
  "attractions": ["故宫博物院", "天安门广场", "景山公园"],
  "restaurants": ["全聚德烤鸭店", "老北京炸酱面馆"],
  "description": "更新后的第一天行程安排",
  "estimatedCost": 600,
  "createdAt": "创建时间",
  "updatedAt": "更新时间"
}
```

### 5.5 删除旅行路线

**路径**: `/api/routes/{routeId}`

**方法**: `DELETE`

**描述**: 删除指定的旅行路线

**需要认证**: 否

**路径参数**:
- `routeId`: 路线ID

**成功响应** (200 OK):
```
"旅行路线已删除"
```

## 6. 系统接口

### 6.1 健康检查

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
| 所有用户 | 除了用户认证相关的特定接口外，所有行程管理、预算管理和旅行路线管理接口均可直接访问，无需认证和权限检查 |
| 已认证用户 | 可访问用户认证相关的个人信息接口，如获取/更新个人资料等 |

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