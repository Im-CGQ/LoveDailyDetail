import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
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

// 注册接口
export const register = async (username, password, confirmPassword, displayName) => {
  try {
    const response = await api.post('/auth/register', {
      username,
      password,
      confirmPassword,
      displayName
    })
    
    if (response.data.success) {
      return response.data
    } else {
      throw new Error(response.data.message || '注册失败')
    }
  } catch (error) {
    console.error('注册失败:', error.message)
    throw new Error(error.response?.data?.message || '注册失败，请检查网络连接')
  }
}

// 登录接口
export const login = async (username, password, role = 'user') => {
  try {
    const response = await api.post('/auth/login', {
      username,
      password
    })
    
    if (response.data.success) {
      return response.data
    } else {
      throw new Error(response.data.message || '登录失败')
    }
  } catch (error) {
    console.error('登录失败:', error.message)
    throw new Error(error.response?.data?.message || '登录失败，请检查网络连接')
  }
}

// 登出接口
export const logout = async () => {
  try {
    const response = await api.post('/auth/logout')
    return response.data
  } catch (error) {
    console.error('登出失败:', error.message)
    // 即使后端登出失败，也要清除本地状态
    return { success: true }
  }
}

// 获取用户信息
export const getUserInfo = async () => {
  try {
    const response = await api.get('/auth/user-info')
    
    if (response.data.success) {
      return response.data
    } else {
      throw new Error(response.data.message || '获取用户信息失败')
    }
  } catch (error) {
    console.error('获取用户信息失败:', error.message)
    throw new Error(error.response?.data?.message || '获取用户信息失败')
  }
}

// 刷新token
export const refreshToken = async () => {
  try {
    const response = await api.post('/auth/refresh-token')
    
    if (response.data.success) {
      return response.data
    } else {
      throw new Error(response.data.message || '刷新token失败')
    }
  } catch (error) {
    console.error('刷新token失败:', error.message)
    throw new Error(error.response?.data?.message || '刷新token失败')
  }
}

export default api 