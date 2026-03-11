import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user.js'
import { api_Login } from '@/api/index.js'
import { ElMessage } from 'element-plus'

//菜单栏路由
const sidebar = [
  {
    path: 'things',
    name: 'things',
    component: () => import('../components/Home/index.vue'),
    meta: { needLogin: true, cnname: "主页", }
  },


  {
    path: 'user',
    name: 'user',
    component: () => import('../components/System/User/index.vue'),
    meta: { needLogin: true, name: "用户管理", cnname: "用户管理", }
  },
  {
    path: 'permission',
    name: 'permission',
    component: () => import('../components/System/Permission/index.vue'),
    meta: { needLogin: true, name: "权限管理", cnname: "权限管理", }
  },
  {
    path: 'dictionary',
    name: 'dictionary',
    component: () => import('../components/System/Dictionary/index.vue'),
    meta: { needLogin: true, name: "字典管理", cnname: "字典管理", }
  },
  {
    path: 'logs',
    name: 'logs',
    component: () => import('../components/System/logs/index.vue'),
    meta: { needLogin: true, name: "运行日志", cnname: "运行日志", }
  },
  // 对账系统路由
  {
    path: 'reconciliation-overview',
    name: 'reconciliation-overview',
    component: () => import('../components/Reconciliation/Overview/index.vue'),
    meta: { needLogin: true, name: "对账总览", cnname: "对账总览", }
  },
  {
    path: 'reconciliation-items',
    name: 'reconciliation-items',
    component: () => import('../components/Reconciliation/Items/index.vue'),
    meta: { needLogin: true, name: "物品管理", cnname: "物品管理", }
  },
  {
    path: 'reconciliation-transactions',
    name: 'reconciliation-transactions',
    component: () => import('../components/Reconciliation/Transactions/index.vue'),
    meta: { needLogin: true, name: "交易记录", cnname: "交易记录", }
  },
  {
    path: 'reconciliation-orders',
    name: 'reconciliation-orders',
    component: () => import('../components/Reconciliation/Orders/index.vue'),
    meta: { needLogin: true, name: "订单管理", cnname: "订单管理", }
  }
]

//子页面路由
const subpage = [
  {
    path: 'typeitem/:dict_type',
    name: 'typeitem',
    component: () => import('../components/System/Dictionary/typeitem.vue'),
    meta: {
      needLogin: true,
      name: "字典项管理",
      father: {
        path: '/home/dictionary',
        name: '字典管理'
      }
    }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'login',
      component: () => import('../views/Login.vue'),
      meta: { needLogin: false }
    },
    {
      path: '/home',
      name: 'home',
      redirect: '/home/things',
      component: () => import('../views/Home.vue'),
      meta: { needLogin: true },
      children: [...sidebar, ...subpage]
    },
    {
      path: '/:catchAll(.*)',
      name: "404",
      component: () => import('../views/Error.vue'),
      meta: { needLogin: true }
    }
  ],
})

// 全局前置守卫，检查登录状态
router.beforeEach((to, from, next) => {
  // 获取用户 store
  const userStore = useUserStore()
  const { token } = userStore

  // 检查路由是否需要登录
  if (to.meta.needLogin) {
    // 检查用户是否已登录
    if (userStore.isAuthenticated) {
      // 已登录，允许访问
      next()
    } else {
      // 未登录，先尝试登录
      if (token) {
        api_Login({ token }).then(res => {
          if (res.data?.token) {
            userStore.loginIn(res.data.token, res.data.user, res.data.permission)
            next()
          } else {
            ElMessage.warning('请先登录')
            next({ name: 'login' })
          }
        })
      } else {
        ElMessage.warning('请先登录')
        next({ name: 'login' })
      }
    }
  } else {
    // 不需要登录的页面，直接访问
    next()
  }
})

export default router
