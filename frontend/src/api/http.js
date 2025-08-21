import axios from 'axios'

// 动态获取API地址
const getApiBaseUrl = () => {
  // 如果在开发环境且是本地访问，使用localhost
  if (import.meta.env.DEV && window.location.hostname === 'localhost') {
    return 'http://localhost:8080/api'
  }
  
  // 如果在开发环境且是IP访问（手机端），使用当前主机IP
  if (import.meta.env.DEV) {
    const hostname = window.location.hostname
    return `http://${hostname}:8080/api`
  }
  
  // 生产环境使用相对路径
  return '/api'
}

// 创建axios实例
const api = axios.create({
  baseURL: getApiBaseUrl(),
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器 - 添加token
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('auth_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器 - 处理token过期
api.interceptors.response.use(
  response => {
    return response
  },
  error => {
    if (error.response && error.response.status === 401) {
      // token过期，清除登录状态
      localStorage.removeItem('auth_token')
      localStorage.removeItem('auth_role')
      localStorage.removeItem('auth_username')
      localStorage.removeItem('auth_remember')
      localStorage.removeItem('auth_expires')
      
      // 跳转到登录页面
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default api
