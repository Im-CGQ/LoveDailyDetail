import axios from 'axios'
import { getToken } from '@/utils/auth'

// 动态获取API地址
const getApiBaseUrl = () => {
  // 如果在开发环境且是本地访问，使用localhost
  if (import.meta.env.DEV && window.location.hostname === 'localhost') {
    return 'http://localhost:8080'
  }
  
  // 如果在开发环境且是IP访问（手机端），使用当前主机IP
  if (import.meta.env.DEV) {
    const hostname = window.location.hostname
    return `http://${hostname}:8080`
  }
  
  // 生产环境使用相对路径
  return ''
}

// 创建axios实例
const service = axios.create({
  baseURL: getApiBaseUrl(),
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    console.error('响应错误:', error)
    if (error.response) {
      // 服务器返回错误状态码
      const { status, data } = error.response
      if (status === 401) {
        // 未授权，清除token并跳转到登录页
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        window.location.href = '/login'
      }
      return Promise.reject(data || error)
    } else if (error.request) {
      // 请求已发出但没有收到响应
      return Promise.reject(new Error('网络连接失败'))
    } else {
      // 其他错误
      return Promise.reject(error)
    }
  }
)

export default service
