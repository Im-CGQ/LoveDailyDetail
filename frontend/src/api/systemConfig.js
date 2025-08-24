import api from './http.js'

// 获取所有配置
export const getAllConfigs = async () => {
  try {
    const response = await api.get('/system-config/all')
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取配置失败')
    }
  } catch (error) {
    console.error('获取配置失败:', error.message)
    throw new Error(error.message || '获取配置失败，请检查网络连接')
  }
}

// 获取用户配置（包括全局配置）
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

// 获取全局配置
export const getGlobalConfigs = async () => {
  try {
    const response = await api.get('/system-config/global')
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取全局配置失败')
    }
  } catch (error) {
    console.error('获取全局配置失败:', error.message)
    throw new Error(error.message || '获取全局配置失败，请检查网络连接')
  }
}

// 根据配置键获取配置
export const getConfigByKey = async (configKey) => {
  try {
    const response = await api.get(`/system-config/key/${configKey}`)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取配置失败')
    }
  } catch (error) {
    console.error('获取配置失败:', error.message)
    throw new Error(error.message || '获取配置失败，请检查网络连接')
  }
}

// 保存或更新配置
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

// 获取在一起的时间
export const getTogetherDate = async () => {
  try {
    const response = await api.get('/system-config/together-date')
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

// 设置在一起的时间
export const setTogetherDate = async (togetherDate) => {
  try {
    const response = await api.post('/system-config/together-date', { togetherDate })
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

// 获取背景音乐自动播放配置
export const getBackgroundMusicAutoplay = async () => {
  try {
    const response = await api.get('/system-config/background-music-autoplay')
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

// 设置背景音乐自动播放配置
export const setBackgroundMusicAutoplay = async (autoplay) => {
  try {
    const response = await api.post('/system-config/background-music-autoplay', { autoplay })
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

// 获取配置的Map形式
export const getConfigMap = async (userId) => {
  try {
    const response = await api.get(`/system-config/config-map/${userId}`)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取配置映射失败')
    }
  } catch (error) {
    console.error('获取配置映射失败:', error.message)
    throw new Error(error.message || '获取配置映射失败，请检查网络连接')
  }
}
