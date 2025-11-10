<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from './store'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 计算是否显示导航栏
const showNavBar = computed(() => {
  const noNavRoutes = ['login', 'register']
  return !noNavRoutes.includes(route.name as string)
})

// 处理登录和注册
const handleLogin = () => {
  router.push('/login')
}

const handleRegister = () => {
  router.push('/register')
}

const handleLogout = () => {
  userStore.logout()
  router.push('/')
}
</script>

<template>
  <div class="app-container">
    <!-- 导航栏 -->
    <el-header v-if="showNavBar" height="60px">
      <div class="header-content">
        <div class="logo">
          <router-link to="/">
            <h1>AI旅行规划助手</h1>
          </router-link>
        </div>
        <el-menu
          :default-active="route.path"
          class="nav-menu"
          mode="horizontal"
          router
        >
          <el-menu-item index="/">首页</el-menu-item>
          <el-menu-item v-if="userStore.token" index="/planner">规划行程</el-menu-item>
          <el-menu-item v-if="userStore.token" index="/trips">我的行程</el-menu-item>
          <el-menu-item v-if="userStore.token" index="/profile">个人中心</el-menu-item>
        </el-menu>
        <div class="user-actions">
          <el-button v-if="!userStore.token" @click="handleLogin" type="primary" size="small">登录</el-button>
          <el-button v-if="!userStore.token" @click="handleRegister" size="small">注册</el-button>
          <el-button v-else @click="handleLogout" size="small">退出</el-button>
        </div>
      </div>
    </el-header>

    <!-- 主内容区 -->
    <el-main>
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </el-main>

    <!-- 页脚 -->
    <el-footer height="60px">
      <div class="footer-content">
        <p>© 2025 AI旅行规划助手. 保留所有权利。</p>
      </div>
    </el-footer>
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen,
    Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
  background-color: #f5f7fa;
}

.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.logo h1 {
  font-size: 24px;
  font-weight: 600;
  color: #409eff;
  margin: 0;
}

.nav-menu {
  flex: 1;
  margin: 0 40px;
}

.footer-content {
  text-align: center;
  line-height: 60px;
  color: #909399;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
