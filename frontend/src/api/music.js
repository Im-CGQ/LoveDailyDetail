import api from './http'

/**
 * 根据用户ID获取看信背景音乐（公开接口，无需登录）
 */
export const getLetterBackgroundMusicByUserIdPublic = async (userId) => {
  try {
    const response = await api.get(`/system-config/public/letter-background-music/${userId}`)
    if (response.data.success) {
      return response.data.data
    } else {
      console.error('获取看信背景音乐失败:', response.data.message)
      return ''
    }
  } catch (error) {
    console.error('获取看信背景音乐失败:', error)
    return ''
  }
}

/**
 * 根据用户ID设置看信背景音乐
 */
export const setLetterBackgroundMusicByUserId = async (userId, musicUrl) => {
  try {
    const response = await api.post('/system-config/letter-background-music', { 
      userId, 
      musicUrl 
    })
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '设置看信背景音乐失败')
    }
  } catch (error) {
    console.error('设置看信背景音乐失败:', error.message)
    throw new Error(error.message || '设置看信背景音乐失败，请检查网络连接')
  }
}
