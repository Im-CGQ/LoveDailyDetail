import api from './http.js'

// 创建信件
export const createLetter = async (data) => {
  try {
    const response = await api.post('/letters', data)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '创建信件失败')
    }
  } catch (error) {
    console.error('创建信件失败:', error.message)
    throw new Error(error.message || '创建信件失败，请检查网络连接')
  }
}

// 获取发送的信件列表
export const getSentLetters = async () => {
  try {
    const response = await api.get('/letters/sent')
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取发送信件列表失败')
    }
  } catch (error) {
    console.error('获取发送信件列表失败:', error.message)
    throw new Error(error.message || '获取发送信件列表失败，请检查网络连接')
  }
}

// 获取接收的信件列表
export const getReceivedLetters = async () => {
  try {
    const response = await api.get('/letters/received')
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取接收信件列表失败')
    }
  } catch (error) {
    console.error('获取接收信件列表失败:', error.message)
    throw new Error(error.message || '获取接收信件列表失败，请检查网络连接')
  }
}

// 获取已解锁的信件列表
export const getUnlockedLetters = async () => {
  try {
    const response = await api.get('/letters/unlocked')
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取已解锁信件列表失败')
    }
  } catch (error) {
    console.error('获取已解锁信件列表失败:', error.message)
    throw new Error(error.message || '获取已解锁信件列表失败，请检查网络连接')
  }
}

// 获取未解锁的信件列表
export const getLockedLetters = async () => {
  try {
    const response = await api.get('/letters/locked')
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取未解锁信件列表失败')
    }
  } catch (error) {
    console.error('获取未解锁信件列表失败:', error.message)
    throw new Error(error.message || '获取未解锁信件列表失败，请检查网络连接')
  }
}

// 获取信件详情
export const getLetterById = async (letterId) => {
  try {
    const response = await api.get(`/letters/${letterId}`)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取信件详情失败')
    }
  } catch (error) {
    console.error('获取信件详情失败:', error.message)
    throw new Error(error.message || '获取信件详情失败，请检查网络连接')
  }
}

// 标记信件为已读
export const markAsRead = async (letterId) => {
  try {
    const response = await api.put(`/letters/${letterId}/read`)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '标记信件为已读失败')
    }
  } catch (error) {
    console.error('标记信件为已读失败:', error.message)
    throw new Error(error.message || '标记信件为已读失败，请检查网络连接')
  }
}

// 更新信件
export const updateLetter = async (letterId, data) => {
  try {
    const response = await api.put(`/letters/${letterId}`, data)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '更新信件失败')
    }
  } catch (error) {
    console.error('更新信件失败:', error.message)
    throw new Error(error.message || '更新信件失败，请检查网络连接')
  }
}

// 删除信件
export const deleteLetter = async (letterId) => {
  try {
    const response = await api.delete(`/letters/${letterId}`)
    if (response.data.success) {
      return true
    } else {
      throw new Error(response.data.message || '删除信件失败')
    }
  } catch (error) {
    console.error('删除信件失败:', error.message)
    throw new Error(error.message || '删除信件失败，请检查网络连接')
  }
}
