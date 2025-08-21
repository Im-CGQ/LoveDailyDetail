import api from './auth.js'

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
    throw error
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
    // 如果后端未启动，使用mock数据
    console.log('后端连接失败，使用mock数据:', error.message)
    return getMockSentLetters()
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
    // 如果后端未启动，使用mock数据
    console.log('后端连接失败，使用mock数据:', error.message)
    return getMockReceivedLetters()
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
    // 如果后端未启动，使用mock数据
    console.log('后端连接失败，使用mock数据:', error.message)
    return getMockUnlockedLetters()
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
    // 如果后端未启动，使用mock数据
    console.log('后端连接失败，使用mock数据:', error.message)
    return getMockLockedLetters()
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
    throw error
  }
}

// 标记信件为已读
export const markAsRead = async (letterId) => {
  try {
    const response = await api.put(`/letters/${letterId}/read`)
    if (response.data.success) {
      return true
    } else {
      throw new Error(response.data.message || '标记已读失败')
    }
  } catch (error) {
    console.error('标记已读失败:', error.message)
    throw error
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
    throw error
  }
}

// Mock数据
const getMockSentLetters = () => {
  return [
    {
      id: 1,
      title: '给未来的自己',
      content: '亲爱的自己，希望未来的你能看到这封信时，已经实现了所有的梦想。记住要永远保持初心，勇敢前行！',
      unlockTime: '2025-01-01T00:00:00',
      senderId: 1,
      receiverId: 1,
      isRead: false,
      createdAt: '2024-01-15T10:30:00',
      updatedAt: '2024-01-15T10:30:00'
    }
  ]
}

const getMockReceivedLetters = () => {
  return [
    {
      id: 2,
      title: '给伴侣的信',
      content: '亲爱的，感谢你一直以来的陪伴和支持。我们的爱情故事还在继续，期待与你一起创造更多美好的回忆。',
      unlockTime: '2024-12-25T00:00:00',
      senderId: 1,
      receiverId: 1,
      isRead: false,
      createdAt: '2024-01-14T20:15:00',
      updatedAt: '2024-01-14T20:15:00'
    }
  ]
}

const getMockUnlockedLetters = () => {
  return getMockReceivedLetters().filter(letter => new Date(letter.unlockTime) <= new Date())
}

const getMockLockedLetters = () => {
  return getMockReceivedLetters().filter(letter => new Date(letter.unlockTime) > new Date())
}
