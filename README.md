# AITravelPlanner

[![GitHub Repository](https://img.shields.io/badge/GitHub-Repository-blue.svg)](https://github.com/LittleExian/AITravelPlanner.git)

https://github.com/LittleExian/AITravelPlanner.git

## 项目简介

智能旅游规划平台 —— 通过语音/文字输入生成个性化行程、预算管理与云端同步，提供实时旅行辅助。

## 目录

- [项目概述](#项目概述)
- [核心功能](#核心功能)
- [技术栈](#技术栈)
- [仓库结构](#仓库结构)
- [快速开始](#快速开始)
- [Docker部署](#docker部署)
- [联系贡献](#联系贡献)

## 项目概述

AITravelPlanner 旨在简化旅行规划流程：用户可通过语音或文字输入旅行目的地、时间、预算、偏好等信息，系统使用大语言模型自动生成行程（含交通、住宿、景点、餐厅）、做预算估算，并提供费用记录与云端同步功能（支持注册/登录、保存多份行程）。

> 详细配置说明请参考 [CONFIGURATION_SCHEME.md](./CONFIGURATION_SCHEME.md)
> 技术栈详情请参考 [TECHNOLOGY_STACK_MANAGEMENT.md](./TECHNOLOGY_STACK_MANAGEMENT.md)

## 核心功能

### 智能行程规划

- 支持语音与文字输入（语音功能为必须项）
- 自动生成个性化行程（天数拆分、每日路线、交通与时间估计、餐厅/景点推荐等）

### 费用预算与管理

- AI 预算估算（按人数/天数/偏好分配）
- 支持旅行中开销记录（可语音录入）

### 用户与数据管理

- 注册 / 登录（JWT 无状态认证）
- 云端行程同步、偏好设置与费用记录（可在多设备查看/编辑）

### 地图与导航

- 集成高德或百度地图展示与路线规划

## 技术栈

### 前端

- **框架**：Vue 3 + TypeScript
- **构建工具**：Vite
- **状态管理**：Pinia
- **路由**：Vue Router
- **UI组件库**：Element Plus

### 后端

- **框架**：Spring Boot（Java 17）
- **安全**：Spring Security
- **数据访问**：Spring Data MongoDB

### 数据库

- **主数据库**：MongoDB（本地或云端）
  - 建议生产环境使用副本集

### 第三方服务集成

- **语音识别**：科大讯飞 API（语音转文本/文本转语音）
- **大模型**：阿里云百炼（智能行程规划）
- **容器化**：Docker、Docker Compose


## 仓库结构

```
AITravelPlanner/
├── frontend/          # Vue 3 前端项目 (Vite)
├── backend/           # Spring Boot 后端
├── docker/            # Dockerfile / docker-compose.yml 示例
├── .github/workflows/ # GitHub Actions 工作流配置
├── docs/              # 项目说明、设计文档
├── README.md          # 项目主文档
├── CONFIGURATION_SCHEME.md # 配置方案文档
├── TECHNOLOGY_STACK_MANAGEMENT.md # 技术栈管理文档
└── env.example        # 环境变量模板
```

## 快速开始

### 环境要求

- Node.js 20.19+ 或 22.12+
- Java 17 或更高版本
- Maven 3.6.0 或更高版本
- MongoDB 4.0 或更高版本

### 步骤

1. **克隆仓库**

   ```bash
   git clone https://github.com/yourusername/AITravelPlanner.git
   cd AITravelPlanner
   ```

2. **配置环境变量**

   复制 `env.example` 为实际使用的 `.env` 文件，或在后端 `application.yml` 中配置必要的环境变量。

   示例环境变量配置：

   ```dotenv
   # 前端（Vite，变量以 VITE_ 前缀）
   VITE_API_BASE_URL=http://localhost:8080/api
   VITE_XUNFEI_APPID=your_xunfei_appid
   VITE_XUNFEI_APIKEY=your_xunfei_apikey
   VITE_MAP_API_KEY=your_map_api_key
   
   # 后端（通过系统环境或 application.yml 配置）
   MONGODB_URI=mongodb://localhost:27017/aitravelplanner
   ALI_BAILIAN_API_KEY=your_ali_bailian_api_key
   JWT_SECRET=replace_with_a_secure_secret_at_least_32_chars
   ```

3. **启动后端服务**

   ```bash
   cd backend
   mvn clean package
   mvn spring-boot:run
   # 或运行生成的 jar
   # java -jar target/aitravelplanner-backend-1.0.0.jar
   ```

4. **启动前端服务**

   ```bash
   cd ../frontend
   npm install
   npm run dev
   # 默认开发服务器地址：http://localhost:5173
   ```

## Docker部署

本项目已配置完整的Docker容器化解决方案，包含以下组件：

- **MongoDB**：数据存储
- **后端服务**：Spring Boot应用
- **前端服务**：Vue 3应用 + Nginx

### 快速部署步骤

1. **准备环境变量文件**

   在`docker`目录下创建`.env`文件（可选，用于配置敏感信息）：

   ```dotenv
   # 阿里云百炼API密钥
   ALI_BAILIAN_API_KEY=your_ali_bailian_api_key
   
   # JWT密钥
   JWT_SECRET=your_secure_jwt_secret_at_least_32_chars
   
   # 科大讯飞API配置
   VITE_XUNFEI_APPID=your_xunfei_appid
   VITE_XUNFEI_APIKEY=your_xunfei_apikey
   
   # 地图API密钥
   VITE_MAP_API_KEY=your_map_api_key
   ```

2. **构建并启动所有服务**

   ```bash
   cd docker
   docker compose up -d
   ```

   此命令将：
   - 自动构建后端镜像
   - 自动构建前端镜像
   - 启动MongoDB数据库
   - 启动后端服务
   - 启动前端服务并配置Nginx代理

3. **访问应用**

   - 前端应用：http://localhost
   - API文档：http://localhost:8080/swagger-ui.html
   - MongoDB：localhost:27017

### 单独构建镜像

如果需要单独构建镜像：

```bash
# 构建后端镜像
cd backend
docker build -t aitravelplanner-backend:latest .

# 构建前端镜像
cd ../frontend
docker build -t aitravelplanner-frontend:latest .
```

### 容器管理命令

```bash
# 查看容器状态
cd docker
docker compose ps

# 查看服务日志
cd docker
docker compose logs -f

# 停止服务
cd docker
docker compose down

# 重启服务
cd docker
docker compose restart
```

### 环境变量说明

环境变量可以通过以下方式配置：
1. 在`docker/.env`文件中设置（推荐）
2. 在`docker compose up`命令前临时设置
3. 修改`docker-compose.yml`文件中的默认值

### 数据持久化

MongoDB数据通过Docker卷`mongo-data`持久化存储，确保容器重启后数据不会丢失。

### 网络配置

所有服务通过`aitravelplanner-network`网络相互通信，确保安全隔离。



## 联系贡献

欢迎提交 Issue 或 Pull Request！在提交 PR 时请保证提交信息清晰并关联相关任务。

---

## 环境变量示例

```dotenv
# 前端配置
VITE_API_BASE_URL=http://localhost:8080/api
VITE_XUNFEI_APPID=
VITE_XUNFEI_APIKEY=
VITE_MAP_API_KEY=

# 后端配置
MONGODB_URI=mongodb://localhost:27017/aitravelplanner
ALI_BAILIAN_API_KEY=
JWT_SECRET=replace_with_long_random_string
```