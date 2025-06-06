import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Home from '../views/Home.vue'
import AiChat from '../views/AiChat.vue'
import { getToken, getUserRole, isTokenExpired } from '../utils/jwt'
import { ElMessage } from 'element-plus'

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
    meta: { 
      title: '首页', 
      requiresAuth: true,
      roles: ['DOCTOR', 'ADMIN']
    }
  },
  {
    path: '/ai-chat',
    name: 'AiChat',
    component: AiChat,
    meta: { 
      title: 'AI智能问诊', 
      requiresAuth: true,
      roles: ['DOCTOR']
    }
  },
  {
    path: '/403',
    name: '403',
    component: () => import('../views/403.vue'),
    meta: {
      title: '权限不足',
    }
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
    const token = getToken()

    if(!token || isTokenExpired()){
      ElMessage.error('请先登录')
      next({
        path: '/login',
        query: {redirect: to.fullPath}
      })
      return 
    }

    // 检查角色权限
    if(to.meta.roles){
      const role = getUserRole()

      if(!to.meta.roles.includes(role)){
        ElMessage.error('您没有权限访问该页面')
        next('/403')
        return 
      }
    }
    next()
  } else {
    next()
  }
})

export default router 