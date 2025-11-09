<template>
  <div class="home-container">
    <!-- 英雄区 -->
    <section class="hero-section">
      <div class="hero-content">
        <h2>智能旅行，轻松规划</h2>
        <p>AI旅行规划助手帮您快速生成个性化行程，让每一次旅行都充满惊喜</p>
        <el-button type="primary" size="large" @click="startPlanning">
          开始规划
        </el-button>
      </div>
    </section>

    <!-- 功能介绍 -->
    <section class="features-section">
      <div class="container">
        <h3>核心功能</h3>
        <div class="features-grid">
          <div class="feature-card">
            <el-card shadow="hover">
              <template #header>
                <div class="card-header">
                  <el-icon><Plan /></el-icon>
                  <span>智能行程规划</span>
                </div>
              </template>
              <p>基于AI技术，根据您的需求自动生成最优旅行路线</p>
            </el-card>
          </div>
          <div class="feature-card">
            <el-card shadow="hover">
              <template #header>
                <div class="card-header">
                  <el-icon><Wallet /></el-icon>
                  <span>预算管理</span>
                </div>
              </template>
              <p>实时跟踪旅行开销，避免超支，让旅行更加安心</p>
            </el-card>
          </div>
          <div class="feature-card">
            <el-card shadow="hover">
              <template #header>
                <div class="card-header">
                  <el-icon><Map /></el-icon>
                  <span>地图展示</span>
                </div>
              </template>
              <p>直观的地图界面，清晰展示行程地点和路线</p>
            </el-card>
          </div>
          <div class="feature-card">
            <el-card shadow="hover">
              <template #header>
                <div class="card-header">
                  <el-icon><Cloud /></el-icon>
                  <span>云端同步</span>
                </div>
              </template>
              <p>多设备同步，随时随地查看和编辑您的行程</p>
            </el-card>
          </div>
        </div>
      </div>
    </section>

    <!-- 使用流程 -->
    <section class="process-section">
      <div class="container">
        <h3>简单三步，完成旅行规划</h3>
        <div class="process-steps">
          <div class="step">
            <div class="step-number">1</div>
            <h4>输入需求</h4>
            <p>告诉我们您的目的地、日期、预算和偏好</p>
          </div>
          <div class="step">
            <div class="step-number">2</div>
            <h4>AI生成</h4>
            <p>智能算法为您生成个性化行程方案</p>
          </div>
          <div class="step">
            <div class="step-number">3</div>
            <h4>开始旅程</h4>
            <p>获取详细攻略，轻松开启完美旅行</p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>
<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUserStore } from '../store'

const router = useRouter()
const userStore = useUserStore()

const startPlanning = () => {
  if (userStore.token) {
    router.push('/planner')
  } else {
    router.push('/login')
  }
}
</script>
<style scoped>
.home-container {
  min-height: calc(100vh - 120px);
}

/* 英雄区 */
.hero-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 80px 0;
  text-align: center;
}

.hero-content h2 {
  font-size: 42px;
  margin-bottom: 20px;
  font-weight: 600;
}

.hero-content p {
  font-size: 18px;
  margin-bottom: 30px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

/* 功能区 */
.features-section {
  padding: 60px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.features-section h3,
.process-section h3 {
  text-align: center;
  font-size: 32px;
  margin-bottom: 40px;
  color: #303133;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
}

.card-header .el-icon {
  font-size: 20px;
  color: #409eff;
}

/* 流程区 */
.process-section {
  background-color: #f0f2f5;
  padding: 60px 0;
}

.process-steps {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 20px;
}

.step {
  flex: 1;
  min-width: 250px;
  text-align: center;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.step-number {
  width: 50px;
  height: 50px;
  background-color: #409eff;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: bold;
  margin: 0 auto 20px;
}

.step h4 {
  font-size: 20px;
  margin-bottom: 10px;
  color: #303133;
}

.step p {
  color: #606266;
}
</style>