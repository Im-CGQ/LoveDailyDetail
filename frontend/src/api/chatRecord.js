import api from './http.js'

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
    console.error('获取聊天记录列表失败:', error.message)
    throw new Error(error.response?.data?.message || '获取聊天记录列表失败，请检查网络连接')
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
    console.error('获取聊天记录失败:', error.message)
    throw new Error(error.response?.data?.message || '获取聊天记录失败，请检查网络连接')
  }
}

// 根据ID获取聊天记录
export const getChatRecordById = async (id) => {
  try {
    const response = await api.get(`/chat-records/${id}`)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取聊天记录失败')
    }
  } catch (error) {
    console.error('获取聊天记录失败:', error.message)
    throw new Error(error.response?.data?.message || '获取聊天记录失败，请检查网络连接')
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
    console.error('获取总聊天时长失败:', error.message)
    throw new Error(error.response?.data?.message || '获取总聊天时长失败，请检查网络连接')
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
    console.error('获取聊天类型时长统计失败:', error.message)
    throw new Error(error.response?.data?.message || '获取聊天类型时长统计失败，请检查网络连接')
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
    throw new Error(error.response?.data?.message || '创建聊天记录失败，请检查网络连接')
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
    throw new Error(error.response?.data?.message || '更新聊天记录失败，请检查网络连接')
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
    throw new Error(error.response?.data?.message || '删除聊天记录失败，请检查网络连接')
  }
}
