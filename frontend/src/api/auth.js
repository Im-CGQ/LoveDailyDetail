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

// 登录接口
export const login = async (username, password, role = 'user') => {
  try {
    // 尝试调用真实API
    const response = await api.post('/auth/login', {
      username,
      password,
      role
    })
    
    if (response.data.success) {
      return response.data
    } else {
      throw new Error(response.data.message || '登录失败')
    }
  } catch (error) {
    // 如果后端未启动，使用mock数据
    console.log('后端连接失败，使用mock数据:', error.message)
    
    // 模拟API调用延迟
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    if (username === 'admin' && password === 'admin') {
      return {
        success: true,
        data: {
          token: 'mock_token_' + Date.now(),
          user: {
            username,
            role,
            avatar: null
          }
        }
      }
    } else {
      throw new Error('用户名或密码错误')
    }
  }
}

// 登出接口
export const logout = async () => {
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    return { success: true }
  } catch (error) {
    throw error
  }
}

// 获取用户信息
export const getUserInfo = async () => {
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    const username = localStorage.getItem('auth_username')
    const role = localStorage.getItem('auth_role')
    
    if (!username || !role) {
      throw new Error('用户未登录')
    }
    
    return {
      success: true,
      data: {
        username,
        role,
        avatar: null
      }
    }
  } catch (error) {
    throw error
  }
}

// 刷新token
export const refreshToken = async () => {
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    const username = localStorage.getItem('auth_username')
    const role = localStorage.getItem('auth_role')
    
    if (!username || !role) {
      throw new Error('用户未登录')
    }
    
    const newToken = 'mock_token_' + Date.now()
    
    return {
      success: true,
      data: {
        token: newToken
      }
    }
  } catch (error) {
    throw error
  }
}

export default api 