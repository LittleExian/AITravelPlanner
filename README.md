# AI旅行规划助手 (AITravelPlanner)

## 项目简介

AITravelPlanner是一款基于AI的智能旅行规划平台，旨在通过AI技术简化用户的旅行规划过程。用户可以通过语音或文字输入旅行需求，系统会自动生成个性化的旅行路线和建议，并提供实时旅行辅助功能。

## 核心功能

1. **智能行程规划**：通过语音（或文字）输入旅行需求，AI自动生成详细的旅行路线
2. **费用预算与管理**：AI进行预算分析，支持语音记录旅行开销
3. **用户管理与数据存储**：支持注册登录，行程云端同步，多设备访问
4. **实时旅行辅助**：集成地图导航，提供旅行安全提示

## 技术栈

- **前端**：React.js, Ant Design, 高德地图API
- **后端**：Node.js + Express
- **数据库**：MongoDB
- **AI服务**：科大讯飞语音识别API, OpenAI API
- **容器化**：Docker

## 快速开始

### 使用Docker运行

```bash
# 克隆项目
git clone https://github.com/yourusername/AITravelPlanner.git
cd AITravelPlanner

# 配置API Key
cp .env.example .env
# 编辑.env文件，填入所需的API Key

# 构建并启动Docker容器
docker-compose up -d
```

### 本地开发

#### 前端开发

```bash
cd frontend
npm install
npm run dev
```

#### 后端开发

```bash
cd backend
npm install
npm run dev
```

## API Key配置

本项目需要以下API Key：

1. **科大讯飞语音识别API Key**
2. **高德地图/百度地图API Key**
3. **OpenAI API Key** (或其他大语言模型API Key)
4. **数据库连接信息**

请将这些配置放在`.env`文件中，或通过项目内的设置页面进行配置。

## 项目结构

```
AITravelPlanner/
├── frontend/          # 前端代码
│   ├── public/
│   ├── src/
│   ├── package.json
│   └── Dockerfile
├── backend/           # 后端代码
│   ├── src/
│   ├── package.json
│   └── Dockerfile
├── .env.example       # 环境变量示例
├── docker-compose.yml # Docker Compose配置
├── PRD.md             # 产品需求文档
└── README.md          # 项目说明文档
```

## 注意事项

- 请勿将API Key直接写入代码中
- 本项目使用的API Key请确保至少3个月内有效
- 开发环境需要Node.js 16+和npm 8+

## 许可证

MIT License

## 联系方式

如有问题，请联系项目维护者。