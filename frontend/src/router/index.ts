import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: { title: 'AI旅行规划助手' }
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
      meta: { title: '登录' }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/RegisterView.vue'),
      meta: { title: '注册' }
    },
    {
      path: '/planner',
      name: 'planner',
      component: () => import('../views/PlannerView.vue'),
      meta: { title: '行程规划', requiresAuth: true }
    },
    {
      path: '/trip/:id',
      name: 'tripDetail',
      component: () => import('../views/TripDetailView.vue'),
      meta: { title: '行程详情', requiresAuth: true }
    },
    {
      path: '/budget/:tripId',
      name: 'budget',
      component: () => import('../views/BudgetView.vue'),
      meta: { title: '预算管理', requiresAuth: true }
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/ProfileView.vue'),
      meta: { title: '个人中心', requiresAuth: true }
    },
    {
      path: '/trips',
      name: 'trips',
      component: () => import('../views/TripsListView.vue'),
      meta: { title: '我的行程', requiresAuth: true }
    }
  ]
})

// 路由守卫
router.beforeEach((to, _from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title as string
  }
  
  // 检查是否需要登录
  const requiresAuth = to.meta.requiresAuth
  const isAuthenticated = localStorage.getItem('token') !== null
  
  if (requiresAuth && !isAuthenticated) {
    next({ name: 'login' })
  } else {
    next()
  }
})

export default router