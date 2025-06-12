import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './style.css'
import './styles/index.css'
import App from './App.vue'
import router from './router'
import permissionDirective from './directives/permission'
import zhCn from 'element-plus/es/locale/lang/zh-cn'

const app = createApp(App)

app.use(router)
app.use(ElementPlus,{
  locale: zhCn,
})
app.use(permissionDirective)
app.mount('#app')
