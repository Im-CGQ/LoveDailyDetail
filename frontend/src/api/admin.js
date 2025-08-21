import api from './http.js'

// 获取后台统计数据
export const getAdminStats = async () => {
  try {
    const response = await api.get('/admin/stats')
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取统计数据失败')
    }
  } catch (error) {
    console.error('获取统计数据失败:', error.message)
    throw new Error(error.response?.data?.message || '获取统计数据失败，请检查网络连接')
  }
}

// 获取分页日记列表
export const getDiariesWithPagination = async (page = 1, size = 10) => {
  try {
    const response = await api.get(`/admin/diaries?page=${page}&size=${size}`)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取日记列表失败')
    }
  } catch (error) {
    console.error('获取日记列表失败:', error.message)
    throw new Error(error.response?.data?.message || '获取日记列表失败，请检查网络连接')
  }
}

// 获取最近日记
export const getRecentDiaries = async (limit = 5) => {
  try {
    const response = await api.get(`/admin/diaries/recent?limit=${limit}`)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取最近日记失败')
    }
  } catch (error) {
    console.error('获取最近日记失败:', error.message)
    throw new Error(error.response?.data?.message || '获取最近日记失败，请检查网络连接')
  }
}

// 根据ID获取日记详情
export const getDiaryById = async (id) => {
  try {
    const response = await api.get(`/admin/diaries/${id}`)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取日记详情失败')
    }
  } catch (error) {
    console.error('获取日记详情失败:', error.message)
    throw new Error(error.response?.data?.message || '获取日记详情失败，请检查网络连接')
  }
}

// 创建日记
export const createDiary = async (diaryData) => {
  try {
    // 确保videos字段是数组格式
    const processedData = {
      ...diaryData,
      videos: Array.isArray(diaryData.videos) ? diaryData.videos : 
             (diaryData.video ? [diaryData.video] : [])
    }
    
    const response = await api.post('/admin/diaries', processedData)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '创建日记失败')
    }
  } catch (error) {
    console.error('创建日记失败:', error.message)
    throw new Error(error.response?.data?.message || '创建日记失败，请检查网络连接')
  }
}

// 更新日记
export const updateDiary = async (id, diaryData) => {
  try {
    // 确保videos字段是数组格式
    const processedData = {
      ...diaryData,
      videos: Array.isArray(diaryData.videos) ? diaryData.videos : 
             (diaryData.video ? [diaryData.video] : [])
    }
    
    const response = await api.put(`/admin/diaries/${id}`, processedData)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '更新日记失败')
    }
  } catch (error) {
    console.error('更新日记失败:', error.message)
    throw new Error(error.response?.data?.message || '更新日记失败，请检查网络连接')
  }
}

// 删除日记
export const deleteDiary = async (id) => {
  try {
    const response = await api.delete(`/admin/diaries/${id}`)
    if (response.data.success) {
      return true
    } else {
      throw new Error(response.data.message || '删除日记失败')
    }
  } catch (error) {
    console.error('删除日记失败:', error.message)
    throw new Error(error.response?.data?.message || '删除日记失败，请检查网络连接')
  }
}

// 获取聊天记录列表
export const getChatRecordsWithPagination = async (page = 1, size = 10) => {
  try {
    const response = await api.get(`/admin/chat-records?page=${page}&size=${size}`)
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

// 创建聊天记录
export const createChatRecord = async (chatRecordData) => {
  try {
    const response = await api.post('/admin/chat-records', chatRecordData)
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
    const response = await api.put(`/admin/chat-records/${id}`, chatRecordData)
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
    const response = await api.delete(`/admin/chat-records/${id}`)
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

// 获取信件列表
export const getLettersWithPagination = async (page = 1, size = 10) => {
  try {
    const response = await api.get(`/admin/letters?page=${page}&size=${size}`)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取信件列表失败')
    }
  } catch (error) {
    console.error('获取信件列表失败:', error.message)
    throw new Error(error.response?.data?.message || '获取信件列表失败，请检查网络连接')
  }
}

// 创建信件
export const createLetter = async (letterData) => {
  try {
    const response = await api.post('/admin/letters', letterData)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '创建信件失败')
    }
  } catch (error) {
    console.error('创建信件失败:', error.message)
    throw new Error(error.response?.data?.message || '创建信件失败，请检查网络连接')
  }
}

// 更新信件
export const updateLetter = async (id, letterData) => {
  try {
    const response = await api.put(`/admin/letters/${id}`, letterData)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '更新信件失败')
    }
  } catch (error) {
    console.error('更新信件失败:', error.message)
    throw new Error(error.response?.data?.message || '更新信件失败，请检查网络连接')
  }
}

// 删除信件
export const deleteLetter = async (id) => {
  try {
    const response = await api.delete(`/admin/letters/${id}`)
    if (response.data.success) {
      return true
    } else {
      throw new Error(response.data.message || '删除信件失败')
    }
  } catch (error) {
    console.error('删除信件失败:', error.message)
    throw new Error(error.response?.data?.message || '删除信件失败，请检查网络连接')
  }
} 