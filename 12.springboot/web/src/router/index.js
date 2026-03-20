import { createRouter, createWebHistory } from 'vue-router'
import GameLogin from '../components/GameLogin.vue'

// 路由配置
const routes = [
  {
    path: '/',
    redirect: '/login' // 重定向到登录页面
  },
  {
    path: '/login',
    name: 'GameLogin',
    component: GameLogin
  },
  {
    path: '/game-lobby',
    name: 'GameLobby',
    // 使用懒加载提高性能
    component: () => import('../components/GameLobby.vue')
  },
  {
    path: '/game/guess-number',
    name: 'GuessNumberGame',
    component: () => import('../components/GuessNumberGame.vue')
  },
  {
    path: '/game/2048',
    name: 'Game2048',
    component: () => import('../components/Game2048.vue')
  },
  {
    path: '/game-select',
    name: 'GameSelect',
    // 使用懒加载提高性能
    component: () => import('../components/GameSelect.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../components/Register.vue')
  },
  {
    // 404页面
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../components/NotFound.vue')
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：检查用户登录状态
router.beforeEach((to, from, next) => {
  // 需要登录的路由名称
  const authRequiredRoutes = ['GameLobby', 'GuessNumberGame', 'Game2048', 'GameSelect']
  
  // 检查当前路由是否需要登录
  const authRequired = authRequiredRoutes.includes(to.name)
  
  // 从localStorage检查用户是否已登录
  const userJson = localStorage.getItem('game-user')
  const isAuthenticated = userJson !== null && userJson !== 'null'
  
  if (authRequired && !isAuthenticated) {
    // 如果需要登录但未登录，重定向到登录页面
    next({ name: 'GameLogin' })
  } else if (to.name === 'GameLogin' && isAuthenticated) {
    // 如果已登录但访问登录页面，重定向到游戏大厅
    next({ name: 'GameLobby' })
  } else {
    // 其他情况正常放行
    next()
  }
})

export default router