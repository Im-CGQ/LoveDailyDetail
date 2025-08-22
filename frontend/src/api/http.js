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

// 请求拦截器 - 添加token和检查token有效性
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('auth_token')
    const expires = localStorage.getItem('auth_expires')
    
    // 检查token是否存在且未过期
    if (token && expires) {
      const now = Date.now()
      const expirationTime = parseInt(expires)
      
      if (now < expirationTime) {
        // token有效，添加到请求头
        config.headers.Authorization = `Bearer ${token}`
      } else {
        // token已过期，清除登录状态并跳转到登录页面
        console.log('Token已过期，清除登录状态')
        localStorage.removeItem('auth_token')
        localStorage.removeItem('auth_role')
        localStorage.removeItem('auth_username')
        localStorage.removeItem('auth_remember')
        localStorage.removeItem('auth_expires')
        
        if (window.location.pathname !== '/login') {
          window.location.href = '/login'
        }
      }
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器 - 处理token过期和权限问题
api.interceptors.response.use(
  response => {
    return response
  },
  error => {
    // 处理401未授权和403禁止访问
    if (error.response && (error.response.status === 401 || error.response.status === 403)) {
      console.log(`用户认证失败 (${error.response.status}):`, error.response.data)
      
      // 清除所有登录状态
      localStorage.removeItem('auth_token')
      localStorage.removeItem('auth_role')
      localStorage.removeItem('auth_username')
      localStorage.removeItem('auth_remember')
      localStorage.removeItem('auth_expires')
      
      // 如果当前不在登录页面，则跳转到登录页面
      if (window.location.pathname !== '/login') {
        // 保存当前页面路径，登录后可以跳转回来
        const currentPath = window.location.pathname + window.location.search
        if (currentPath !== '/login') {
          localStorage.setItem('redirect_after_login', currentPath)
        }
        
        // 跳转到登录页面
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

export default api
