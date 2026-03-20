import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import axios from 'axios'

import App from './App.vue'
import router from './router'

// 配置axios默认设置
axios.defaults.timeout = 10000 // 10秒超时
axios.defaults.headers.common['Accept'] = 'application/json'

const app = createApp(App)

// 将axios实例挂载到全局，方便组件使用
app.config.globalProperties.$axios = axios

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

app.mount('#app')
