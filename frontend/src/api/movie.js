import api from './http.js'

// 获取所有电影
export const getAllMovies = async () => {
  try {
    const response = await api.get('/movies')
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取电影列表失败')
    }
  } catch (error) {
    console.error('获取电影列表失败:', error.message)
    throw new Error(error.message || '获取电影列表失败，请检查网络连接')
  }
}

// 获取公开电影
export const getPublicMovies = async () => {
  try {
    const response = await api.get('/movies/public')
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取公开电影失败')
    }
  } catch (error) {
    console.error('获取公开电影失败:', error.message)
    throw new Error(error.message || '获取公开电影失败，请检查网络连接')
  }
}

// 获取我的电影
export const getMyMovies = async () => {
  try {
    const response = await api.get('/movies/my')
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取我的电影失败')
    }
  } catch (error) {
    console.error('获取我的电影失败:', error.message)
    throw new Error(error.message || '获取我的电影失败，请检查网络连接')
  }
}

// 根据ID获取电影
export const getMovieById = async (id) => {
  try {
    const response = await api.get(`/movies/${id}`)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '获取电影失败')
    }
  } catch (error) {
    console.error('获取电影失败:', error.message)
    throw new Error(error.message || '获取电影失败，请检查网络连接')
  }
}

// 创建电影
export const createMovie = async (movieData) => {
  try {
    const response = await api.post('/movies', movieData)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '创建电影失败')
    }
  } catch (error) {
    console.error('创建电影失败:', error.message)
    throw new Error(error.message || '创建电影失败，请检查网络连接')
  }
}

// 更新电影
export const updateMovie = async (id, movieData) => {
  try {
    const response = await api.put(`/movies/${id}`, movieData)
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '更新电影失败')
    }
  } catch (error) {
    console.error('更新电影失败:', error.message)
    throw new Error(error.message || '更新电影失败，请检查网络连接')
  }
}

// 删除电影
export const deleteMovie = async (id) => {
  try {
    const response = await api.delete(`/movies/${id}`)
    if (response.data.success) {
      return true
    } else {
      throw new Error(response.data.message || '删除电影失败')
    }
  } catch (error) {
    console.error('删除电影失败:', error.message)
    throw new Error(error.message || '删除电影失败，请检查网络连接')
  }
}

// 搜索电影
export const searchMovies = async (title) => {
  try {
    const response = await api.get('/movies/search', { params: { title } })
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '搜索电影失败')
    }
  } catch (error) {
    console.error('搜索电影失败:', error.message)
    throw new Error(error.message || '搜索电影失败，请检查网络连接')
  }
}

// 上传电影文件
export const uploadMovieFile = async (file) => {
  try {
    const formData = new FormData()
    formData.append('file', file)
    const response = await api.post('/movies/upload/movie', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '上传电影文件失败')
    }
  } catch (error) {
    console.error('上传电影文件失败:', error.message)
    throw new Error(error.message || '上传电影文件失败，请检查网络连接')
  }
}

// 上传电影封面
export const uploadMovieCover = async (file) => {
  try {
    const formData = new FormData()
    formData.append('file', file)
    const response = await api.post('/movies/upload/cover', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '上传电影封面失败')
    }
  } catch (error) {
    console.error('上传电影封面失败:', error.message)
    throw new Error(error.message || '上传电影封面失败，请检查网络连接')
  }
}
