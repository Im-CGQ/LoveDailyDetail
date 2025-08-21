import api from './http.js'

// 获取当前用户信息
export const getCurrentUserInfo = async () => {
  try {
    const response = await api.get('/auth/profile')
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取用户信息失败')
    }
  } catch (error) {
    console.error('获取用户信息失败:', error.message)
    throw new Error(error.response?.data?.message || '获取用户信息失败，请检查网络连接')
  }
}

// 更新用户信息
export const updateUserInfo = async (data) => {
  try {
    const response = await api.put('/auth/profile', data)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '更新用户信息失败')
    }
  } catch (error) {
    console.error('更新用户信息失败:', error.message)
    throw new Error(error.response?.data?.message || '更新用户信息失败，请检查网络连接')
  }
}

// 获取用户统计信息
export const getUserStats = async () => {
  try {
    const response = await api.get('/auth/stats')
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取用户统计信息失败')
    }
  } catch (error) {
    console.error('获取用户统计信息失败:', error.message)
    throw new Error(error.response?.data?.message || '获取用户统计信息失败，请检查网络连接')
  }
}
