import api from './auth.js'

// 获取所有聊天记录
export const getAllChatRecords = async () => {
  try {
    const response = await api.get('/chat-records')
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取聊天记录列表失败')
    }
  } catch (error) {
    // 如果后端未启动，使用mock数据
    console.log('后端连接失败，使用mock数据:', error.message)
    return getMockChatRecords()
  }
}

// 根据日期获取聊天记录
export const getChatRecordByDate = async (date) => {
  try {
    const response = await api.get(`/chat-records/date/${date}`)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取聊天记录失败')
    }
  } catch (error) {
    // 如果后端未启动，使用mock数据
    console.log('后端连接失败，使用mock数据:', error.message)
    return getMockChatRecordByDate(date)
  }
}

// 获取总聊天时长
export const getTotalDuration = async () => {
  try {
    const response = await api.get('/chat-records/total-duration')
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取总聊天时长失败')
    }
  } catch (error) {
    console.log('后端连接失败，使用mock数据:', error.message)
    return getMockTotalDuration()
  }
}

// 获取聊天类型时长统计
export const getDurationByChatType = async () => {
  try {
    const response = await api.get('/chat-records/duration-by-type')
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取聊天类型时长统计失败')
    }
  } catch (error) {
    console.log('后端连接失败，使用mock数据:', error.message)
    return getMockDurationByChatType()
  }
}

// 创建聊天记录
export const createChatRecord = async (chatRecordData) => {
  try {
    const response = await api.post('/chat-records', chatRecordData)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '创建聊天记录失败')
    }
  } catch (error) {
    console.error('创建聊天记录失败:', error.message)
    throw error
  }
}

// 更新聊天记录
export const updateChatRecord = async (id, chatRecordData) => {
  try {
    const response = await api.put(`/chat-records/${id}`, chatRecordData)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '更新聊天记录失败')
    }
  } catch (error) {
    console.error('更新聊天记录失败:', error.message)
    throw error
  }
}

// 删除聊天记录
export const deleteChatRecord = async (id) => {
  try {
    const response = await api.delete(`/chat-records/${id}`)
    if (response.data.success) {
      return true
    } else {
      throw new Error(response.data.message || '删除聊天记录失败')
    }
  } catch (error) {
    console.error('删除聊天记录失败:', error.message)
    throw error
  }
}

// Mock数据
const getMockChatRecords = () => {
  return [
    {
      id: 1,
      chatType: '微信语音',
      durationMinutes: 45,
      date: '2024-01-15',
      description: '今天和女朋友聊了很久，聊了很多有趣的话题',
      customType: null,
      createdAt: '2024-01-15T10:30:00',
      updatedAt: '2024-01-15T10:30:00'
    },
    {
      id: 2,
      chatType: '微信聊天',
      durationMinutes: 30,
      date: '2024-01-14',
      description: '日常聊天，分享今天发生的事情',
      customType: null,
      createdAt: '2024-01-14T20:15:00',
      updatedAt: '2024-01-14T20:15:00'
    },
    {
      id: 3,
      chatType: '小红书聊天',
      durationMinutes: 20,
      date: '2024-01-13',
      description: '分享美食和旅行攻略',
      customType: null,
      createdAt: '2024-01-13T15:45:00',
      updatedAt: '2024-01-13T15:45:00'
    },
    {
      id: 4,
      chatType: '自定义',
      durationMinutes: 60,
      date: '2024-01-12',
      description: '视频通话聊天',
      customType: '视频通话',
      createdAt: '2024-01-12T19:20:00',
      updatedAt: '2024-01-12T19:20:00'
    },
    {
      id: 5,
      chatType: '微信语音',
      durationMinutes: 25,
      date: '2024-01-11',
      description: '睡前语音聊天',
      customType: null,
      createdAt: '2024-01-11T22:30:00',
      updatedAt: '2024-01-11T22:30:00'
    }
  ]
}

const getMockChatRecordByDate = (date) => {
  const mockChatRecords = getMockChatRecords()
  return mockChatRecords.find(record => record.date === date) || null
}

const getMockTotalDuration = () => {
  const mockChatRecords = getMockChatRecords()
  return mockChatRecords.reduce((total, record) => total + record.durationMinutes, 0)
}

const getMockDurationByChatType = () => {
  const mockChatRecords = getMockChatRecords()
  const durationByType = {}
  
  mockChatRecords.forEach(record => {
    const type = record.chatType === '自定义' ? record.customType : record.chatType
    if (durationByType[type]) {
      durationByType[type] += record.durationMinutes
    } else {
      durationByType[type] = record.durationMinutes
    }
  })
  
  return durationByType
}
