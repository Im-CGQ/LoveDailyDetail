import api from './http.js'

// 创建分享链接
export const createShareLink = async (diaryId) => {
  try {
    const response = await api.post(`/share/create/${diaryId}`)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '创建分享链接失败')
    }
  } catch (error) {
    console.error('创建分享链接失败:', error.message)
    throw new Error(error.message || '创建分享链接失败，请检查网络连接')
  }
}

// 通过分享链接获取日记
export const getSharedDiary = async (shareToken) => {
  try {
    const response = await api.get(`/share/diary/${shareToken}`)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取分享日记失败')
    }
  } catch (error) {
    console.error('获取分享日记失败:', error.message)
    throw new Error(error.message || '获取分享日记失败，请检查网络连接')
  }
}

// 验证分享链接是否有效
export const validateShareLink = async (shareToken) => {
  try {
    const response = await api.get(`/share/validate/${shareToken}`)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '验证分享链接失败')
    }
  } catch (error) {
    console.error('验证分享链接失败:', error.message)
    throw new Error(error.message || '验证分享链接失败，请检查网络连接')
  }
}

// 通过分享链接获取信件
export const getSharedLetter = async (shareToken) => {
  try {
    const response = await api.get(`/share/letter/${shareToken}`)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取分享信件失败')
    }
  } catch (error) {
    console.error('获取分享信件失败:', error.message)
    throw new Error(error.message || '获取分享信件失败，请检查网络连接')
  }
}

// 创建信件分享链接
export const createLetterShareLink = async (letterId) => {
  try {
    const response = await api.post(`/share/letter/create/${letterId}`)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '创建信件分享链接失败')
    }
  } catch (error) {
    console.error('创建信件分享链接失败:', error.message)
    throw new Error(error.message || '创建信件分享链接失败，请检查网络连接')
  }
}
