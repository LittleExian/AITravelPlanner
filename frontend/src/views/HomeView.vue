<template>
  <div class="home-container">
    <!-- 英雄区 -->
    <section class="hero-section">
      <div class="hero-background"></div>
      <div class="hero-content">
        <h2 class="hero-title">智能旅行，轻松规划</h2>
        <p class="hero-description">AI旅行规划助手帮您快速生成个性化行程，让每一次旅行都充满惊喜</p>
        <el-button type="primary" size="large" @click="startPlanning" class="hero-button">
          开始规划
        </el-button>
      </div>
    </section>

    <!-- 功能介绍 -->
    <section class="features-section">
      <div class="container">
        <h3 class="section-title">核心功能</h3>
        <div class="features-grid">
          <div class="feature-card">
            <el-card shadow="hover" class="animated-card">
              <template #header>
                <div class="card-header">
                  <div class="icon-wrapper">
                    <el-icon><Plan /></el-icon>
                  </div>
                  <span>智能行程规划</span>
                </div>
              </template>
              <p>基于AI技术，根据您的需求自动生成最优旅行路线</p>
            </el-card>
          </div>
          <div class="feature-card">
            <el-card shadow="hover" class="animated-card">
              <template #header>
                <div class="card-header">
                  <div class="icon-wrapper">
                    <el-icon><Wallet /></el-icon>
                  </div>
                  <span>预算管理</span>
                </div>
              </template>
              <p>实时跟踪旅行开销，避免超支，让旅行更加安心</p>
            </el-card>
          </div>
          <div class="feature-card">
            <el-card shadow="hover" class="animated-card">
              <template #header>
                <div class="card-header">
                  <div class="icon-wrapper">
                    <el-icon><Map /></el-icon>
                  </div>
                  <span>地图展示</span>
                </div>
              </template>
              <p>直观的地图界面，清晰展示行程地点和路线</p>
            </el-card>
          </div>
          <div class="feature-card">
            <el-card shadow="hover" class="animated-card">
              <template #header>
                <div class="card-header">
                  <div class="icon-wrapper">
                    <el-icon><Cloud /></el-icon>
                  </div>
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
        <h3 class="section-title">简单三步，完成旅行规划</h3>
        <div class="process-steps">
          <div class="step">
            <div class="step-number">1</div>
            <h4>输入需求</h4>
            <p>告诉我们您的目的地、日期、预算和偏好</p>
          </div>
          <div class="step-arrow">→</div>
          <div class="step">
            <div class="step-number">2</div>
            <h4>AI生成</h4>
            <p>智能算法为您生成个性化行程方案</p>
          </div>
          <div class="step-arrow">→</div>
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
  position: relative;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 100px 0;
  text-align: center;
  overflow: hidden;
}

.hero-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: 
    radial-gradient(circle at 20% 50%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 80% 80%, rgba(255, 255, 255, 0.1) 0%, transparent 50%);
  animation: pulse 8s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 0.5; }
  50% { opacity: 1; }
}

.hero-content {
  position: relative;
  z-index: 1;
}

.hero-title {
  font-size: 48px;
  margin-bottom: 20px;
  font-weight: 700;
  letter-spacing: 1px;
  animation: fadeInDown 1s ease-out;
}

.hero-description {
  font-size: 20px;
  margin-bottom: 40px;
  max-width: 700px;
  margin-left: auto;
  margin-right: auto;
  line-height: 1.6;
  opacity: 0.95;
  animation: fadeInUp 1s ease-out 0.2s both;
}

.hero-button {
  padding: 16px 48px;
  font-size: 18px;
  border-radius: 50px;
  font-weight: 600;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
  animation: fadeInUp 1s ease-out 0.4s both;
}

.hero-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.3);
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 功能区 */
.features-section {
  padding: 80px 0;
  background: linear-gradient(to bottom, #ffffff 0%, #f8f9fa 100%);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.section-title {
  text-align: center;
  font-size: 36px;
  margin-bottom: 60px;
  color: #303133;
  font-weight: 700;
  position: relative;
  padding-bottom: 20px;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 4px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(240px, 1fr));
  gap: 30px;
  justify-content: center;
}

.feature-card {
  animation: fadeInUp 0.6s ease-out both;
}

.feature-card:nth-child(1) { animation-delay: 0.1s; }
.feature-card:nth-child(2) { animation-delay: 0.2s; }
.feature-card:nth-child(3) { animation-delay: 0.3s; }
.feature-card:nth-child(4) { animation-delay: 0.4s; }

.animated-card {
  border-radius: 12px;
  transition: all 0.3s ease;
  height: 100%;
  border: 1px solid #e8e9eb;
}

.animated-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.icon-wrapper {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.icon-wrapper .el-icon {
  font-size: 24px;
  color: white;
}

.animated-card p {
  color: #606266;
  line-height: 1.8;
  font-size: 15px;
}

/* 流程区 */
.process-section {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  padding: 80px 0;
}

.process-steps {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.step {
  flex: 1;
  min-width: 250px;
  max-width: 280px;
  text-align: center;
  padding: 40px 30px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  animation: fadeInUp 0.6s ease-out both;
}

.step:nth-child(1) { animation-delay: 0.1s; }
.step:nth-child(3) { animation-delay: 0.2s; }
.step:nth-child(5) { animation-delay: 0.3s; }

.step:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.step-number {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: bold;
  margin: 0 auto 24px;
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
}

.step:hover .step-number {
  transform: scale(1.1);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.5);
}

.step-arrow {
  font-size: 32px;
  color: #667eea;
  font-weight: bold;
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateX(0); }
  50% { transform: translateX(10px); }
}

.step h4 {
  font-size: 22px;
  margin-bottom: 12px;
  color: #303133;
  font-weight: 600;
}

.step p {
  color: #606266;
  line-height: 1.8;
  font-size: 15px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-title {
    font-size: 36px;
  }
  
  .hero-description {
    font-size: 16px;
  }
  
  .section-title {
    font-size: 28px;
  }
  
  .step-arrow {
    display: none;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
  }
}
</style>