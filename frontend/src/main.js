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
// const env = import.meta.env.VITE_APP_ENV;
// console.log(env,'env');

// if (env === 'uat') {
new VConsole();
// }
const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(Vant)

app.mount('#app') 