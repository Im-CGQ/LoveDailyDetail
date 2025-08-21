import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import App from './App.vue'
import VConsole from 'vconsole';
// 引入Vant组件库
import Vant from 'vant'
import 'vant/lib/index.css'

// 引入触摸模拟器（用于PC端开发）
import '@vant/touch-emulator'

// 引入全局样式
import './styles/index.scss'

// 只在开发环境且需要调试时启用VConsole
const env = import.meta.env.MODE;
const enableVConsole = import.meta.env.VITE_ENABLE_VCONSOLE === 'true';

if (env === 'development' && enableVConsole) {
  new VConsole();
}

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.use(Vant)

app.mount('#app') 