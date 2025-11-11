# AITravelPlanner 技术栈和相关管理文档

## 1. 项目技术栈概述

AITravelPlanner项目采用现代全栈开发技术栈，前端使用Vue 3生态系统，后端采用Spring Boot框架，数据库使用MongoDB，集成了地图、阿里云百炼大模型、科大讯飞语音识别等api，构建了一个高效、可扩展的智能旅行规划平台。

### 1.1 整体架构

- **前端**：基于Vue 3的SPA（单页面应用）
- **后端**：基于Spring Boot的RESTful API服务
- **数据库**：MongoDB NoSQL数据库
- **身份认证**：JWT（JSON Web Token）
- **构建工具**：Vite（前端）、Maven（后端）

## 2. 前端技术栈详解

### 2.1 核心技术

- **Vue 3**：渐进式JavaScript框架
  - 响应式系统：基于Proxy的现代响应式实现
  - Composition API：提供更灵活的组件逻辑组织方式
  - 性能优化：虚拟DOM改进，更小的包体积

- **TypeScript**：JavaScript的超集，提供静态类型检查
  - 增强代码可读性和可维护性
  - 提供IDE智能提示和自动补全
  - 在编译时捕获错误，减少运行时问题

- **Vue Router**：Vue官方路由管理器
  - 路由懒加载：按需加载路由组件
  - 导航守卫：控制路由访问权限
  - 嵌套路由：构建复杂页面结构

- **Pinia**：Vue官方状态管理库
  - 简化的API：比Vuex更简洁的设计
  - TypeScript支持：更好的类型推断
  - 模块化设计：每个store都是独立的模块

### 2.2 UI组件库

- **Element Plus**：Vue 3的企业级UI组件库
  - 丰富的组件集：表单、表格、对话框等
  - 主题定制：支持自定义主题
  - 国际化支持：多语言切换

### 2.3 开发工具和构建系统

- **Vite**：下一代前端构建工具
  - 极速的开发服务器启动
  - 按需编译，热模块替换
  - 优化的构建输出

- **ESLint**：代码质量检查工具
  - 统一代码风格
  - 发现潜在问题
  - 确保团队代码质量

- **Prettier**：代码格式化工具
  - 统一代码格式
  - 减少格式相关的代码审查讨论

### 2.4 第三方API集成

- **科大讯飞API**：提供语音识别和合成功能
  - 语音转文字：将用户语音输入转换为文本
  - 文字转语音：提供语音反馈给用户

- **地图API**（计划集成）：
  - 高德地图/百度地图
  - 提供地图显示、路线规划等功能

- **阿里云百炼大模型API**：提供智能对话功能
  - 基于Transformer架构
  - 根据用户需求，生成路线、预算分配信息

## 3. 后端技术栈详解

### 3.1 核心框架

- **Spring Boot 3.2.0**：Java企业级应用开发框架
  - 自动配置：减少样板代码
  - 内嵌服务器：简化部署
  - 依赖注入：灵活的组件管理

- **Spring Security**：安全认证和授权框架
  - 身份验证：支持多种认证方式
  - 授权控制：基于角色的访问控制
  - 安全防护：CSRF保护、XSS防护等

- **Spring Data MongoDB**：MongoDB数据访问框架
  - 自动生成查询方法
  - 实体映射：Java对象到MongoDB文档
  - 事务支持：多文档事务

### 3.2 数据持久层

- **MongoDB**：面向文档的NoSQL数据库
  - 灵活的数据模型：适合旅行计划等复杂数据结构
  - 高性能：适合读多写少的场景
  - 可扩展性：水平扩展能力强

### 3.3 API设计和文档

- **RESTful API**：标准化的API设计
  - 资源导向：以资源为中心的URI设计
  - HTTP方法：GET、POST、PUT、DELETE等
  - 状态码：标准HTTP状态码

- **SpringDoc OpenAPI 3.0**：API文档生成工具
  - 自动生成API文档
  - 交互式API测试界面
  - 支持OpenAPI规范

### 3.4 身份认证和安全

- **JWT**：无状态的身份认证机制
  - 自包含：包含用户信息和权限
  - 可验证：通过签名确保数据完整性
  - 无状态：减轻服务器存储压力

### 3.5 测试框架

- **JUnit 5**：Java单元测试框架
- **Mockito**：模拟测试库
- **Spring Boot Test**：集成测试支持

## 4. 项目管理工具和实践

### 4.1 代码管理

- **Git**：分布式版本控制系统
  - 分支策略：建议使用GitFlow或GitHub Flow
  - 提交规范：清晰的提交信息，关联任务ID
  - 代码审查：Pull Request/Merge Request流程

### 4.2 依赖管理

- **前端**：npm/yarn
  - package.json：定义项目依赖
  - 版本锁定：package-lock.json
  - 依赖审计：定期检查安全漏洞

- **后端**：Maven
  - pom.xml：项目对象模型配置
  - 依赖传递：自动管理间接依赖
  - 版本管理：属性定义统一版本号




## 5. 附录

### 10.1 技术栈版本信息

| 类别 | 技术 | 版本 | 备注 |
|------|------|------|------|
| 前端框架 | Vue | 3.x | - |
| 前端语言 | TypeScript | 5.x | - |
| 路由 | Vue Router | 4.x | - |
| 状态管理 | Pinia | - | - |
| UI组件库 | Element Plus | - | - |
| 构建工具 | Vite | 7.x | - |
| 后端框架 | Spring Boot | 3.2.0 | - |
| 安全框架 | Spring Security | - | - |
| 数据访问 | Spring Data MongoDB | - | - |
| 数据库 | MongoDB | 4.x+ | - |
| API文档 | SpringDoc OpenAPI | - | - |

### 10.2 开发环境设置指南

#### 前端开发环境

```bash
# 安装依赖
cd frontend
npm install

# 启动开发服务器
npm run dev
```

#### 后端开发环境

```bash
# 编译项目
cd backend
mvn clean compile

# 启动应用
mvn spring-boot:run
```