import api from './http.js'

// 创建房间
export const createRoom = async (roomData) => {
  try {
    const response = await api.post('/movie-rooms', roomData)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '创建房间失败')
    }
  } catch (error) {
    console.error('创建房间失败:', error.message)
    throw new Error(error.message || '创建房间失败，请检查网络连接')
  }
}

// 加入房间
export const joinRoom = async (roomCode) => {
  try {
    const response = await api.post(`/movie-rooms/${roomCode}/join`)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '加入房间失败')
    }
  } catch (error) {
    console.error('加入房间失败:', error.message)
    throw new Error(error.message || '加入房间失败，请检查网络连接')
  }
}

// 离开房间
export const leaveRoom = async (roomCode) => {
  try {
    const response = await api.post(`/movie-rooms/${roomCode}/leave`)
    if (response.data.success) {
      return true
    } else {
      throw new Error(response.data.message || '离开房间失败')
    }
  } catch (error) {
    console.error('离开房间失败:', error.message)
    throw new Error(error.message || '离开房间失败，请检查网络连接')
  }
}

// 获取房间信息
export const getRoom = async (roomCode) => {
  try {
    const response = await api.get(`/movie-rooms/${roomCode}`)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取房间信息失败')
    }
  } catch (error) {
    console.error('获取房间信息失败:', error.message)
    throw new Error(error.message || '获取房间信息失败，请检查网络连接')
  }
}

// 获取房间成员
export const getRoomMembers = async (roomCode) => {
  try {
    const response = await api.get(`/movie-rooms/${roomCode}/members`)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取房间成员失败')
    }
  } catch (error) {
    console.error('获取房间成员失败:', error.message)
    throw new Error(error.message || '获取房间成员失败，请检查网络连接')
  }
}

// 更新播放状态
export const updatePlayback = async (roomCode, playbackData) => {
  try {
    const response = await api.post(`/movie-rooms/${roomCode}/playback`, playbackData)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '更新播放状态失败')
    }
  } catch (error) {
    console.error('更新播放状态失败:', error.message)
    throw new Error(error.message || '更新播放状态失败，请检查网络连接')
  }
}

// 获取播放状态
export const getPlaybackStatus = async (roomCode) => {
  try {
    const response = await api.get(`/movie-rooms/${roomCode}/playback`)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取播放状态失败')
    }
  } catch (error) {
    console.error('获取播放状态失败:', error.message)
    throw new Error(error.message || '获取播放状态失败，请检查网络连接')
  }
}

// 获取我的房间
export const getMyRooms = async () => {
  try {
    const response = await api.get('/movie-rooms/my')
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取我的房间失败')
    }
  } catch (error) {
    console.error('获取我的房间失败:', error.message)
    throw new Error(error.message || '获取我的房间失败，请检查网络连接')
  }
}

// 删除房间
export const deleteRoom = async (roomCode) => {
  try {
    const response = await api.delete(`/movie-rooms/${roomCode}`)
    if (response.data.success) {
      return true
    } else {
      throw new Error(response.data.message || '删除房间失败')
    }
  } catch (error) {
    console.error('删除房间失败:', error.message)
    throw new Error(error.message || '删除房间失败，请检查网络连接')
  }
}

