# AITravelPlanner 项目总体配置方案

## 1. 项目概述

AITravelPlanner是一款基于AI的智能旅行规划平台，旨在通过AI技术简化用户的旅行规划过程。用户可以通过语音或文字输入旅行需求，系统会自动生成个性化的旅行路线和建议，并提供实时旅行辅助功能。

## 2. 环境配置要求

### 2.1 开发环境要求

- **操作系统**：Windows, macOS, Linux
- **前端环境**：
  - Node.js 20.19+ 或 22.12+
  - npm 8+
- **后端环境**：
  - JDK 17 或更高版本
  - Maven 3.6.0 或更高版本
- **数据库**：
  - MongoDB 4.0 或更高版本

### 2.2 生产环境要求

- **服务器**：支持Java和Node.js的云服务器或物理服务器
- **数据库**：MongoDB 副本集或分片集群（根据规模）
- **容器化**：Docker 环境
- **负载均衡**：可选，根据访问量配置

## 3. 环境变量配置

### 3.1 前端环境变量（.env 文件）

前端项目使用Vite构建工具，环境变量需要以`VITE_`前缀命名。在`frontend/.env`文件中配置以下变量：

```dotenv
# 科大讯飞语音识别API配置
VITE_XUNFEI_APPID=your_app_id
VITE_XUNFEI_APIKEY=your_api_key
VITE_XUNFEI_APISECRET=your_api_secret


### 3.2 后端环境变量

后端项目可以通过`application.yml`文件或系统环境变量配置以下参数：

- MongoDB连接信息
- 阿里百炼大模型API Key（后端配置，在application.yml中）
- JWT密钥
- 服务器端口
- 日志级别

## 4. 前端配置详情

### 4.1 项目配置文件

- **vite.config.ts**：Vite构建工具配置
- **tsconfig.json**：TypeScript配置
- **tsconfig.app.json**：Vue应用TypeScript配置
- **tsconfig.node.json**：Node.js相关TypeScript配置

### 4.2 开发配置

开发服务器配置：
- 默认端口：5173（如果被占用，会自动选择其他端口）
- 热重载：已启用
- 代理配置：可在vite.config.ts中配置，用于解决跨域问题

### 4.3 构建配置

构建命令：`npm run build`
- 构建输出目录：`dist/`
- 构建产物包括压缩后的JavaScript、CSS和HTML文件
- 构建过程包括TypeScript类型检查

## 5. 后端配置详情

### 5.1 应用配置（application.yml）

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/aitravelplanner
      database: aitravelplanner
      # 添加连接超时和重试配置
      socket-timeout: 5000
      connect-timeout: 5000
  
  # MongoDB 驱动日志配置
  logging:
    level:
      org.mongodb.driver: DEBUG
  
  # 允许应用在数据库连接失败时仍能启动,便于调试
  main:
    allow-bean-definition-overriding: true
  
  # SQL 初始化配置(新版本)
  sql:
    init:
      continue-on-error: true

  application:
    name: AITravelPlanner

# JWT配置
jwt:
  # 生产环境中请使用更安全的密钥
  secret: 9qpmEmG86SEStp7Q1Xc089U8zAfU4ZKOP1GAbKQvf4I=
  expiration: 86400000  # 24小时(毫秒)

# 应用程序配置
server:
  port: 8080

# SpringDoc配置
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: method
    tags-sorter: alpha
    doc-expansion: list

# 日志配置
logging:
  level:
    org.mongodb.driver: DEBUG
    com.aitravelplanner: DEBUG
```

### 5.2 数据库配置

- **连接字符串格式**：`mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database][?options]]`
- **数据库名称**：aitravelplanner
- **连接池配置**：可在application.yml中扩展配置

### 5.3 安全配置

- **JWT过期时间**：默认24小时
- **加密算法**：HS256
- **密钥要求**：至少32个字符（256位）


## 6. 附录

### 6.1 环境变量模板

提供.env.example文件作为模板，包含所有必需的环境变量及其说明：

```dotenv
# 科大讯飞语音识别API配置
VITE_XUNFEI_APPID=your_app_id_here
VITE_XUNFEI_APIKEY=your_api_key_here
VITE_XUNFEI_APISECRET=your_api_secret_here

# 应用基础配置
VITE_API_BASE_URL=http://localhost:8080/api

# 高德地图/百度地图API Key
VITE_MAP_API_KEY=your_map_api_key_here


# 阿里百炼大模型API Key（后端配置，在application.yml中）
# ali.bailian.api.key=your_ali_bailian_api_key
```