# AITravelPlanner

[![GitHub Repository](https://img.shields.io/badge/GitHub-Repository-blue.svg)](https://github.com/yourusername/AITravelPlanner.git)

## 项目简介

智能旅游规划平台 —— 通过语音/文字输入生成个性化行程、预算管理与云端同步，提供实时旅行辅助。

## 目录

- [项目概述](#项目概述)
- [核心功能](#核心功能)
- [技术栈](#技术栈)
- [仓库结构](#仓库结构)
- [快速开始](#快速开始)
- [Docker部署](#docker部署)
- [API Key与安全](#api-key与安全说明)
- [助教运行指南](#助教运行评分指南)
- [提交物清单](#提交物清单)
- [开发扩展建议](#开发与扩展建议)
- [常见问题](#常见问题)
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

### 地图与导航（可选增强）

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

### 构建镜像

```bash
# 后端镜像
cd backend
docker build -t aitravelplanner-backend:latest .

# 前端镜像
cd ../frontend
docker build -t aitravelplanner-frontend:latest .
```

### 使用Docker Compose

```bash
cd docker
docker compose up -d
```

### 推送镜像到仓库（示例）

```bash
# 登录阿里云镜像仓库
docker login --username=your-aliyun-username registry.cn-xxx.aliyuncs.com

# 打标签
docker tag aitravelplanner-backend:latest registry.cn-xxx.aliyuncs.com/yourrepo/aitravelplanner-backend:latest

# 推送镜像
docker push registry.cn-xxx.aliyuncs.com/yourrepo/aitravelplanner-backend:latest
```



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