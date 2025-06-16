import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './style.css'
import './styles/index.css'
import App from './App.vue'
import router from './router'
import permissionDirective from './directives/permission'
import tooltipDirective from './directives/tooltip'
import zhCn from 'element-plus/es/locale/lang/zh-cn'

// 全局组件
import BaseCard from './components/BaseCard.vue'
import PageHeader from './components/PageHeader.vue'

const app = createApp(App)

// 注册全局组件
app.component('BaseCard', BaseCard)
app.component('PageHeader', PageHeader)

// 注册插件和指令
app.use(router)
app.use(ElementPlus, {
  locale: zhCn,
})
app.use(permissionDirective)
app.use(tooltipDirective)

app.mount('#app')
