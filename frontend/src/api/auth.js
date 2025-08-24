import api from './http.js'

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
    console.error('注册失败:', error)
    
    // 优先显示后端返回的错误信息
    if (error.response && error.response.data) {
      const errorData = error.response.data
      if (errorData.message) {
        throw new Error(errorData.message)
      } else if (errorData.error) {
        throw new Error(errorData.error)
      }
    }
    
    // 根据HTTP状态码提供更具体的错误信息
    if (error.response) {
      const status = error.response.status
      switch (status) {
        case 400:
          throw new Error('请求参数错误，请检查输入信息')
        case 409:
          throw new Error('用户名已存在，请选择其他用户名')
        case 500:
          throw new Error('服务器内部错误，请稍后重试')
        default:
          throw new Error(`注册失败 (${status})`)
      }
    }
    
    // 如果是网络错误或其他错误
    if (error.code === 'NETWORK_ERROR' || error.code === 'ERR_NETWORK') {
      throw new Error('网络连接失败，请检查网络设置')
    }
    
    if (error.code === 'ECONNABORTED' || error.message.includes('timeout')) {
      throw new Error('请求超时，请稍后重试')
    }
    
    // 默认错误信息
    throw new Error(error.message || '注册失败，请重试')
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
    console.error('登录失败:', error)
    
    // 优先显示后端返回的错误信息
    if (error.response && error.response.data) {
      const errorData = error.response.data
      if (errorData.message) {
        throw new Error(errorData.message)
      } else if (errorData.error) {
        throw new Error(errorData.error)
      }
    }
    
    // 根据HTTP状态码提供更具体的错误信息
    if (error.response) {
      const status = error.response.status
      switch (status) {
        case 401:
          throw new Error('用户名或密码错误')
        case 400:
          throw new Error('请求参数错误')
        case 500:
          throw new Error('服务器内部错误，请稍后重试')
        default:
          throw new Error(`请求失败 (${status})`)
      }
    }
    
    // 如果是网络错误或其他错误
    if (error.code === 'NETWORK_ERROR' || error.code === 'ERR_NETWORK') {
      throw new Error('网络连接失败，请检查网络设置')
    }
    
    if (error.code === 'ECONNABORTED' || error.message.includes('timeout')) {
      throw new Error('请求超时，请稍后重试')
    }
    
    // 默认错误信息
    throw new Error(error.message || '登录失败，请重试')
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
    throw new Error(error.message || '获取用户信息失败')
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
    throw new Error(error.message || '刷新token失败')
  }
} 