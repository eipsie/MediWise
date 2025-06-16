<template>
  <div class="side-menu">
    <el-menu
      :default-active="activeMenu"
      class="sidebar-menu"
      :collapse="isCollapsed"
      background-color="transparent"
      text-color="#5c6b7a"
      active-text-color="#3f8cda"
      router
      :collapse-transition="false"
    >
      <!-- 医生可见菜单 -->
      <template v-if="isDoctorRole">
        <el-menu-item index="/dashboard">
        <el-icon><HomeFilled /></el-icon>
          <template #title>医生工作台</template>
      </el-menu-item>
      
      <el-sub-menu index="/patients">
        <template #title>
          <el-icon><User /></el-icon>
          <span>患者管理</span>
        </template>
          <el-menu-item index="/patients/list">
            <el-icon><List /></el-icon>
            <span>患者列表</span>
          </el-menu-item>
          <el-menu-item index="/patients/add">
            <el-icon><Plus /></el-icon>
            <span>新增患者</span>
          </el-menu-item>
      </el-sub-menu>
      
      <el-sub-menu index="/diagnoses">
        <template #title>
          <el-icon><Stethoscope /></el-icon>
          <span>诊断管理</span>
        </template>
          <el-menu-item index="/diagnoses/list">
            <el-icon><Document /></el-icon>
            <span>诊断列表</span>
          </el-menu-item>
          <el-menu-item index="/diagnoses/add">
            <el-icon><Plus /></el-icon>
            <span>新增诊断</span>
          </el-menu-item>
      </el-sub-menu>
      
      <el-sub-menu index="/bloodtests">
        <template #title>
          <el-icon><FirstAidKit /></el-icon>
          <span>血常规检查</span>
        </template>
          <el-menu-item index="/bloodtests/list">
            <el-icon><Files /></el-icon>
            <span>检查列表</span>
          </el-menu-item>
          <el-menu-item index="/bloodtests/add">
            <el-icon><Plus /></el-icon>
            <span>新增检查</span>
          </el-menu-item>
        </el-sub-menu>
        
        <el-menu-item index="/ai-chat">
          <el-icon><ChatDotRound /></el-icon>
          <template #title>AI智能问诊</template>
        </el-menu-item>
      </template>
      
      <!-- 管理员可见菜单 -->
      <template v-if="hasAdminRole">
        <el-menu-item index="/admin-dashboard">
          <el-icon><HomeFilled /></el-icon>
          <template #title>管理控制台</template>
        </el-menu-item>
        
        <el-sub-menu index="/system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/system/users">
            <el-icon><UserFilled /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/system/roles">
            <el-icon><Avatar /></el-icon>
            <span>角色管理</span>
          </el-menu-item>
          <el-menu-item index="/system/logs">
            <el-icon><Notebook /></el-icon>
            <span>系统日志</span>
          </el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="/knowledge-manage">
          <template #title>
            <el-icon><Edit /></el-icon>
            <span>知识库管理</span>
          </template>
          <el-menu-item index="/knowledge/manage">
            <el-icon><Collection /></el-icon>
            <span>知识列表</span>
          </el-menu-item>
          <el-menu-item index="/knowledge/add">
            <el-icon><DocumentAdd /></el-icon>
            <span>添加知识</span>
          </el-menu-item>
      </el-sub-menu>
      </template>
      
      <!-- 医生和管理员都可见的菜单 -->
      <el-menu-item index="/knowledge" v-if="isDoctorRole">
        <el-icon><Reading /></el-icon>
        <template #title>医学知识库</template>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup>
import { computed, defineComponent, h } from 'vue'
import { useRoute } from 'vue-router'
import { 
  HomeFilled, 
  User, 
  ChatDotRound, 
  Setting,
  Reading,
  Edit,
  List,
  Plus,
  Document,
  Files,
  UserFilled,
  Avatar,
  Notebook,
  Collection,
  DocumentAdd
} from '@element-plus/icons-vue'
import { getUserRole } from '../../utils/jwt'

// 图标定义
const Stethoscope = defineComponent({
  name: 'Stethoscope',
  setup() {
    return () => h('svg', {
      viewBox: '0 0 24 24',
      width: '1em',
      height: '1em'
    }, [
      h('path', {
        fill: 'currentColor',
        d: 'M19 8V6h-2v2h2M3 3h18v18H3V3m11 3c-1.66 0-3 1.34-3 3c0 1.66 1.34 3 3 3s3-1.34 3-3c0-1.66-1.34-3-3-3m-7 3c0-1.66-1.34-3-3-3V8c1.66 0 3-1.34 3-3h-2c0 1.66-1.34 3-3 3v2c1.66 0 3 1.34 3 3h2c0-1.66 1.34-3 3-3v-2c-1.66 0-3 1.34-3 3H7Z'
      })
    ])
  }
})

const FirstAidKit = defineComponent({
  name: 'FirstAidKit',
  setup() {
    return () => h('svg', {
      viewBox: '0 0 24 24',
      width: '1em',
      height: '1em'
    }, [
      h('path', {
        fill: 'currentColor',
        d: 'M20 6h-3V4c0-1.1-.9-2-2-2h-6c-1.1 0-2 .9-2 2v2H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2zM9 4h6v2H9V4zm11 16H4V8h16v12z'
      }),
      h('path', {
        fill: 'currentColor',
        d: 'M13 10h-2v3H8v2h3v3h2v-3h3v-2h-3z'
      })
    ])
  }
})

// 接收父组件传递的属性
defineProps({
  isCollapsed: {
    type: Boolean,
    default: false
  }
})

const route = useRoute()

// 当前活动菜单
const activeMenu = computed(() => {
  return route.path
})

// 判断是否有管理员角色
const hasAdminRole = computed(() => {
  const role = getUserRole()
  return role === 'ADMIN'
})

// 判断是否有医生角色
const isDoctorRole = computed(() => {
  const role = getUserRole()
  return role === 'DOCTOR'
})
</script>

<style scoped>
.side-menu {
  height: 100%;
  overflow-y: auto;
  /* 隐藏滚动条但保留滚动功能 */
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE and Edge */
}

.side-menu::-webkit-scrollbar {
  display: none; /* Chrome, Safari, Opera */
}

.sidebar-menu {
  border-right: none;
  height: 100%;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 220px;
}

/* 自定义菜单项样式 */
:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  height: 50px;
  line-height: 50px;
  border-radius: 12px;
  margin: 6px 10px;
  font-weight: 500; /* 加粗字体 */
  color: #3a4a5c; /* 更深的颜色提高对比度 */
}

:deep(.el-menu-item .el-icon),
:deep(.el-sub-menu__title .el-icon) {
  margin-right: 14px;
  font-size: 18px;
  transition: transform 0.2s ease;
  color: #3f8cda;
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background-color: rgba(63, 140, 218, 0.08);
  color: #3f8cda;
}

:deep(.el-menu-item:hover .el-icon),
:deep(.el-sub-menu__title:hover .el-icon) {
  transform: scale(1.1);
}

:deep(.el-menu-item.is-active) {
  background-color: rgba(63, 140, 218, 0.15);
  color: #3f8cda;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(63, 140, 218, 0.1);
}

/* 修复二级菜单跳动问题 */
:deep(.el-sub-menu .el-menu) {
  overflow: hidden;
  background-color: transparent;
  padding-top: 2px;
  padding-bottom: 2px;
  margin-top: -6px; /* 减少与父菜单的间距 */
  border-left: 2px solid rgba(63, 140, 218, 0.15); /* 添加左侧边框作为视觉区分 */
  margin-left: 22px; /* 为边框留出空间 */
}

/* 二级菜单项样式 */
:deep(.el-sub-menu .el-menu-item) {
  min-width: auto;
  margin-left: 4px;
  margin-right: 8px;
  height: 50px; /* 与一级菜单高度一致 */
  line-height: 50px; /* 与一级菜单行高一致 */
  font-weight: 500; /* 保持一致的字体粗细 */
  margin-top: 1px;
  margin-bottom: 1px;
  padding-left: 38px !important; /* 调整缩进 */
  font-size: 14px; /* 稍微小一点的字体 */
  border-radius: 8px; /* 圆角稍小一些 */
  background-color: rgba(63, 140, 218, 0.02); /* 轻微的背景色区分 */
  position: relative;
}

/* 二级菜单项前的小圆点 */
:deep(.el-sub-menu .el-menu-item)::before {
  content: '';
  position: absolute;
  left: 24px;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background-color: rgba(63, 140, 218, 0.4);
}

/* 二级菜单项激活状态前的小圆点 */
:deep(.el-sub-menu .el-menu-item.is-active)::before {
  background-color: #3f8cda;
}

/* 二级菜单项图标样式 */
:deep(.el-sub-menu .el-menu-item .el-icon) {
  margin-right: 8px;
  margin-left: 8px;
  font-size: 14px;
  color: #3f8cda;
  opacity: 0.8;
}

/* 二级菜单项悬停效果 */
:deep(.el-sub-menu .el-menu-item:hover) {
  background-color: rgba(63, 140, 218, 0.06);
  padding-left: 40px !important; /* 悬停时微调缩进 */
}

:deep(.el-sub-menu .el-menu-item:hover .el-icon) {
  transform: scale(1.1);
}

/* 二级菜单项激活样式 */
:deep(.el-sub-menu .el-menu-item.is-active) {
  background-color: rgba(63, 140, 218, 0.12);
  color: #3f8cda;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(63, 140, 218, 0.1);
}

:deep(.el-sub-menu .el-menu-item.is-active .el-icon) {
  color: #3f8cda;
  opacity: 1;
  transform: scale(1.05);
}

/* 调整相邻菜单项间距 */
:deep(.el-menu-item) + :deep(.el-sub-menu),
:deep(.el-sub-menu) + :deep(.el-menu-item) {
  margin-top: 6px; /* 增加不同类型菜单项之间的间距 */
}
</style>