import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import AiChat from '../views/AiChat.vue'
import { getToken, getUserRole, isTokenExpired } from '../utils/jwt'
import { ElMessage } from 'element-plus'
import MainLayout from '../components/layout/MainLayout.vue'

// 路由配置
const routes = [
  {
    path: '/',
    name: 'Root',
    redirect: to => {
      const role = getUserRole()
      if (role === 'ADMIN') {
        return '/admin-dashboard'
      } else {
        return '/dashboard'
      }
    }
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
  // 主布局内的路由
  {
    path: '/',
    component: MainLayout,
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { 
          title: '医生工作台', 
          requiresAuth: true,
          roles: ['DOCTOR']
        }
      },
      {
        path: '/admin-dashboard',
        name: 'AdminDashboard',
        component: () => import('../views/system/AdminDashboard.vue'),
        meta: { 
          title: '管理控制台', 
          requiresAuth: true,
          roles: ['ADMIN']
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
      // 患者管理路由
      {
        path: '/patients/list',
        name: 'PatientList',
        component: () => import('../views/patients/PatientList.vue'),
        meta: { 
          title: '患者列表', 
          requiresAuth: true,
          roles: ['DOCTOR']
        }
      },
      {
        path: '/patients/add',
        name: 'PatientAdd',
        component: () => import('../views/patients/PatientForm.vue'),
        meta: { 
          title: '新增患者', 
          requiresAuth: true,
          roles: ['DOCTOR']
        }
      },
      {
        path: '/patients/edit/:id',
        name: 'PatientEdit',
        component: () => import('../views/patients/PatientForm.vue'),
        meta: { 
          title: '编辑患者', 
          requiresAuth: true,
          roles: ['DOCTOR']
        }
      },
      {
        path: '/patients/detail/:id',
        name: 'PatientDetail',
        component: () => import('../views/patients/PatientDetail.vue'),
        meta: { 
          title: '患者详情', 
          requiresAuth: true,
          roles: ['DOCTOR']
        }
      },
      // 诊断管理路由
      {
        path: '/diagnoses/list',
        name: 'DiagnosisList',
        component: () => import('../views/diagnoses/DiagnosisList.vue'),
        meta: { 
          title: '诊断列表', 
          requiresAuth: true,
          roles: ['DOCTOR']
        }
      },
      {
        path: '/diagnoses/add',
        name: 'DiagnosisAdd',
        component: () => import('../views/diagnoses/DiagnosisForm.vue'),
        meta: { 
          title: '新增诊断', 
          requiresAuth: true,
          roles: ['DOCTOR']
        }
      },
      {
        path: '/diagnoses/edit/:id',
        name: 'DiagnosisEdit',
        component: () => import('../views/diagnoses/DiagnosisForm.vue'),
        meta: { 
          title: '编辑诊断', 
          requiresAuth: true,
          roles: ['DOCTOR']
        }
      },
      {
        path: '/diagnoses/detail/:id',
        name: 'DiagnosisDetail',
        component: () => import('../views/diagnoses/DiagnosisDetail.vue'),
        meta: { 
          title: '诊断详情', 
          requiresAuth: true,
          roles: ['DOCTOR']
        }
      },
      // 血常规检查路由
      {
        path: '/bloodtests/list',
        name: 'BloodTestList',
        component: () => import('../views/bloodTests/BloodTestList.vue'),
        meta: { 
          title: '检查列表', 
          requiresAuth: true,
          roles: ['DOCTOR']
        }
      },
      {
        path: '/bloodtests/add',
        name: 'BloodTestAdd',
        component: () => import('../views/bloodTests/BloodTestForm.vue'),
        meta: { 
          title: '新增检查', 
          requiresAuth: true,
          roles: ['DOCTOR']
        }
      },
      {
        path: '/bloodtests/edit/:id',
        name: 'BloodTestEdit',
        component: () => import('../views/bloodTests/BloodTestForm.vue'),
        meta: { 
          title: '编辑检查', 
          requiresAuth: true,
          roles: ['DOCTOR']
        }
      },
      {
        path: '/bloodtests/detail/:id',
        name: 'BloodTestDetail',
        component: () => import('../views/bloodTests/BloodTestDetail.vue'),
        meta: { 
          title: '检查详情', 
          requiresAuth: true,
          roles: ['DOCTOR']
        }
      },
      // 医学知识库路由 - 查看
      {
        path: '/knowledge',
        name: 'KnowledgeList',
        component: () => import('../views/knowledge/KnowledgeList.vue'),
        meta: { 
          title: '医学知识库', 
          requiresAuth: true,
          roles: ['DOCTOR', 'ADMIN']
        }
      },
      {
        path: '/knowledge/detail/:id',
        name: 'KnowledgeDetail',
        component: () => import('../views/knowledge/KnowledgeDetail.vue'),
        meta: { 
          title: '知识详情', 
          requiresAuth: true,
          roles: ['DOCTOR', 'ADMIN']
        }
      },
      // 知识库管理路由 - 管理员专用
      {
        path: '/knowledge/manage',
        name: 'KnowledgeManage',
        component: () => import('../views/knowledge/KnowledgeManage.vue'),
        meta: { 
          title: '知识库管理', 
          requiresAuth: true,
          roles: ['ADMIN']
        }
      },
      {
        path: '/knowledge/add',
        name: 'KnowledgeAdd',
        component: () => import('../views/knowledge/KnowledgeForm.vue'),
        meta: { 
          title: '添加知识', 
          requiresAuth: true,
          roles: ['ADMIN']
        }
      },
      {
        path: '/knowledge/edit/:id',
        name: 'KnowledgeEdit',
        component: () => import('../views/knowledge/KnowledgeForm.vue'),
        meta: { 
          title: '编辑知识', 
          requiresAuth: true,
          roles: ['ADMIN']
        }
      },
      // 系统管理路由
      {
        path: '/system/users',
        name: 'UserList',
        component: () => import('../views/system/UserList.vue'),
        meta: { 
          title: '用户管理', 
          requiresAuth: true,
          roles: ['ADMIN']
        }
      },
      {
        path: '/system/roles',
        name: 'RoleList',
        component: () => import('../views/system/RoleList.vue'),
        meta: { 
          title: '角色管理', 
          requiresAuth: true,
          roles: ['ADMIN']
        }
      },
      {
        path: '/system/logs',
        name: 'LogList',
        component: () => import('../views/system/LogList.vue'),
        meta: { 
          title: '系统日志', 
          requiresAuth: true,
          roles: ['ADMIN']
        }
      }
    ]
  },
  {
    path: '/403',
    name: '403',
    component: () => import('../views/403.vue'),
    meta: {
      title: '权限不足',
    }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/NotFound.vue'),
    meta: {
      title: '页面不存在',
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