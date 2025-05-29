import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Home from '../views/Home.vue'

// 路由配置
const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { title: '注册' }
  },
  {
    path: '/home',
    name: 'Home',
    component: Home,
    meta: { title: '首页', requiresAuth: true }
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - MediWise医疗系统` : 'MediWise医疗系统'
  
  // 检查是否需要登录权限
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // 获取本地存储的用户信息
    const userInfo = localStorage.getItem('userInfo')
    
    if (!userInfo) {
      // 未登录，跳转到登录页
      next({ path: '/login' })
    } else {
      // 已登录，允许访问
      next()
    }
  } else {
    // 不需要登录权限的页面
    next()
  }
})

export default router 