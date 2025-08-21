import api from './http.js'

// 获取所有日记
export const getAllDiaries = async () => {
  try {
    const response = await api.get('/diaries')
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

// 获取最新日记
export const getLatestDiary = async () => {
  try {
    const response = await api.get('/diaries/latest')
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取最新日记失败')
    }
  } catch (error) {
    console.error('获取最新日记失败:', error.message)
    throw new Error(error.response?.data?.message || '获取最新日记失败，请检查网络连接')
  }
}

// 根据日期获取日记
export const getDiaryByDate = async (date) => {
  try {
    const response = await api.get(`/diaries/date/${date}`)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取日记失败')
    }
  } catch (error) {
    console.error('获取日记失败:', error.message)
    throw new Error(error.response?.data?.message || '获取日记失败，请检查网络连接')
  }
}

// 创建日记
export const createDiary = async (diaryData) => {
  try {
    const response = await api.post('/diaries', diaryData)
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
    const response = await api.put(`/diaries/${id}`, diaryData)
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
    const response = await api.delete(`/diaries/${id}`)
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