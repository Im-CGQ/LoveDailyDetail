import api from './http.js'

// 获取用户配置
export const getUserConfigs = async (userId) => {
  try {
    const response = await api.get(`/system-config/user/${userId}`)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取用户配置失败')
    }
  } catch (error) {
    console.error('获取用户配置失败:', error.message)
    throw new Error(error.message || '获取用户配置失败，请检查网络连接')
  }
}

// 获取用户配置映射
export const getUserConfigMap = async (userId) => {
  try {
    const response = await api.get(`/system-config/user/${userId}/map`)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取用户配置映射失败')
    }
  } catch (error) {
    console.error('获取用户配置映射失败:', error.message)
    throw new Error(error.message || '获取用户配置映射失败，请检查网络连接')
  }
}

// 保存配置
export const saveConfig = async (configDTO) => {
  try {
    const response = await api.post('/system-config/save', configDTO)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '保存配置失败')
    }
  } catch (error) {
    console.error('保存配置失败:', error.message)
    throw new Error(error.message || '保存配置失败，请检查网络连接')
  }
}

// 删除配置
export const deleteConfig = async (configKey, userId) => {
  try {
    const response = await api.delete(`/system-config/delete/${configKey}/${userId}`)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '删除配置失败')
    }
  } catch (error) {
    console.error('删除配置失败:', error.message)
    throw new Error(error.message || '删除配置失败，请检查网络连接')
  }
}

// ========== 用户特定配置API ==========

/**
 * 根据用户ID获取在一起的时间
 */
export const getTogetherDateByUserId = async (userId) => {
  try {
    const response = await api.get(`/system-config/together-date/${userId}`)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取在一起时间失败')
    }
  } catch (error) {
    console.error('获取在一起时间失败:', error.message)
    throw new Error(error.message || '获取在一起时间失败，请检查网络连接')
  }
}

/**
 * 根据用户ID设置在一起的时间（同步伴侣配置）
 */
export const setTogetherDateByUserId = async (userId, togetherDate) => {
  try {
    const response = await api.post(`/system-config/together-date/${userId}`, { togetherDate })
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '设置在一起时间失败')
    }
  } catch (error) {
    console.error('设置在一起时间失败:', error.message)
    throw new Error(error.message || '设置在一起时间失败，请检查网络连接')
  }
}

/**
 * 根据用户ID获取背景音乐自动播放配置
 */
export const getBackgroundMusicAutoplayByUserId = async (userId) => {
  try {
    const response = await api.get(`/system-config/background-music-autoplay/${userId}`)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取背景音乐自动播放配置失败')
    }
  } catch (error) {
    console.error('获取背景音乐自动播放配置失败:', error.message)
    throw new Error(error.message || '获取背景音乐自动播放配置失败，请检查网络连接')
  }
}

/**
 * 根据用户ID获取背景音乐自动播放配置（公开接口，无需登录）
 */
export const getBackgroundMusicAutoplayByUserIdPublic = async (userId) => {
  try {
    const response = await api.get(`/system-config/public/background-music-autoplay/${userId}`)
    if (response.data.success) {
      return response.data.data
    } else {
      console.error('获取背景音乐自动播放配置失败:', response.data.message)
      return true // 默认返回 true
    }
  } catch (error) {
    console.error('获取背景音乐自动播放配置失败:', error)
    return true // 默认返回 true
  }
}

/**
 * 根据用户ID设置背景音乐自动播放配置
 */
export const setBackgroundMusicAutoplayByUserId = async (userId, autoplay) => {
  try {
    const response = await api.post(`/system-config/background-music-autoplay/${userId}`, { autoplay })
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '设置背景音乐自动播放配置失败')
    }
  } catch (error) {
    console.error('设置背景音乐自动播放配置失败:', error.message)
    throw new Error(error.message || '设置背景音乐自动播放配置失败，请检查网络连接')
  }
}

/**
 * 根据用户ID获取分享过期时间配置
 */
export const getShareExpireMinutesByUserId = async (userId) => {
  try {
    const response = await api.get(`/system-config/share-expire-minutes/${userId}`)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取分享过期时间配置失败')
    }
  } catch (error) {
    console.error('获取分享过期时间配置失败:', error.message)
    throw new Error(error.message || '获取分享过期时间配置失败，请检查网络连接')
  }
}

/**
 * 根据用户ID设置分享过期时间配置
 */
export const setShareExpireMinutesByUserId = async (userId, minutes) => {
  try {
    const response = await api.post(`/system-config/share-expire-minutes/${userId}`, { minutes })
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '设置分享过期时间配置失败')
    }
  } catch (error) {
    console.error('设置分享过期时间配置失败:', error.message)
    throw new Error(error.message || '设置分享过期时间配置失败，请检查网络连接')
  }
}

/**
 * 根据用户ID获取纪念日列表
 */
export const getAnniversaryDatesByUserId = async (userId) => {
  try {
    const response = await api.get(`/system-config/anniversary-dates/${userId}`)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取纪念日列表失败')
    }
  } catch (error) {
    console.error('获取纪念日列表失败:', error.message)
    throw new Error(error.message || '获取纪念日列表失败，请检查网络连接')
  }
}

/**
 * 根据用户ID设置纪念日列表（同步伴侣配置）
 */
export const setAnniversaryDatesByUserId = async (userId, anniversaryDates) => {
  try {
    const response = await api.post(`/system-config/anniversary-dates/${userId}`, { anniversaryDates })
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '设置纪念日列表失败')
    }
  } catch (error) {
    console.error('设置纪念日列表失败:', error.message)
    throw new Error(error.message || '设置纪念日列表失败，请检查网络连接')
  }
}

/**
 * 根据用户ID获取下次见面日期
 */
export const getNextMeetingDateByUserId = async (userId) => {
  try {
    const response = await api.get(`/system-config/next-meeting-date/${userId}`)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取下次见面日期失败')
    }
  } catch (error) {
    console.error('获取下次见面日期失败:', error.message)
    throw new Error(error.message || '获取下次见面日期失败，请检查网络连接')
  }
}

/**
 * 根据用户ID设置下次见面日期（同步伴侣配置）
 */
export const setNextMeetingDateByUserId = async (userId, nextMeetingDate) => {
  try {
    const response = await api.post(`/system-config/next-meeting-date/${userId}`, { nextMeetingDate })
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '设置下次见面日期失败')
    }
  } catch (error) {
    console.error('设置下次见面日期失败:', error.message)
    throw new Error(error.message || '设置下次见面日期失败，请检查网络连接')
  }
}

/**
 * 根据用户ID获取看信背景音乐
 */
export const getLetterBackgroundMusicByUserId = async (userId) => {
  try {
    const response = await api.get(`/system-config/letter-background-music/${userId}`)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取看信背景音乐失败')
    }
  } catch (error) {
    console.error('获取看信背景音乐失败:', error.message)
    throw new Error(error.message || '获取看信背景音乐失败，请检查网络连接')
  }
}

/**
 * 设置看信背景音乐配置
 */
export const setLetterBackgroundMusic = async (userId, musicUrl) => {
  try {
    const response = await api.post('/system-config/letter-background-music', { userId, musicUrl })
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '设置看信背景音乐配置失败')
    }
  } catch (error) {
    console.error('设置看信背景音乐配置失败:', error.message)
    throw new Error(error.message || '设置看信背景音乐配置失败，请检查网络连接')
  }
}

// ========== 默认配置API（用于没有用户上下文的情况） ==========

/**
 * 获取默认分享过期时间
 */
export const getShareExpireMinutes = async () => {
  try {
    const response = await api.get('/system-config/default/share-expire-minutes')
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取默认分享过期时间失败')
    }
  } catch (error) {
    console.error('获取默认分享过期时间失败:', error.message)
    throw new Error(error.message || '获取默认分享过期时间失败，请检查网络连接')
  }
}

/**
 * 获取默认背景音乐自动播放配置
 */
export const getBackgroundMusicAutoplay = async () => {
  try {
    const response = await api.get('/system-config/default/background-music-autoplay')
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取默认背景音乐自动播放配置失败')
    }
  } catch (error) {
    console.error('获取默认背景音乐自动播放配置失败:', error.message)
    throw new Error(error.message || '获取默认背景音乐自动播放配置失败，请检查网络连接')
  }
}

/**
 * 获取默认在一起时间
 */
export const getTogetherDate = async () => {
  try {
    const response = await api.get('/system-config/default/together-date')
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取默认在一起时间失败')
    }
  } catch (error) {
    console.error('获取默认在一起时间失败:', error.message)
    throw new Error(error.message || '获取默认在一起时间失败，请检查网络连接')
  }
}

/**
 * 获取默认纪念日列表
 */
export const getAnniversaryDates = async () => {
  try {
    const response = await api.get('/system-config/default/anniversary-dates')
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取默认纪念日列表失败')
    }
  } catch (error) {
    console.error('获取默认纪念日列表失败:', error.message)
    throw new Error(error.message || '获取默认纪念日列表失败，请检查网络连接')
  }
}

/**
 * 获取默认下次见面日期
 */
export const getNextMeetingDate = async () => {
  try {
    const response = await api.get('/system-config/default/next-meeting-date')
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取默认下次见面日期失败')
    }
  } catch (error) {
    console.error('获取默认下次见面日期失败:', error.message)
    throw new Error(error.message || '获取默认下次见面日期失败，请检查网络连接')
  }
}