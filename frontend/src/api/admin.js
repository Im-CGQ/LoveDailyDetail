import api from './auth.js'

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
    console.log('后端连接失败，使用mock数据:', error.message)
    return getMockAdminStats()
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
    console.log('后端连接失败，使用mock数据:', error.message)
    return getMockDiariesWithPagination(page, size)
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
    console.log('后端连接失败，使用mock数据:', error.message)
    return getMockRecentDiaries(limit)
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
    console.log('后端连接失败，使用mock数据:', error.message)
    return getMockDiaryById(id)
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
    throw error
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
    throw error
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
    throw error
  }
}

// 上传文件
export const uploadFile = async (file, type = 'image') => {
  try {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('type', type)
    
    const response = await api.post('/admin/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    
    if (response.data.success) {
      return response.data.data
    } else {
      throw new Error(response.data.message || '文件上传失败')
    }
  } catch (error) {
    console.error('文件上传失败:', error.message)
    throw error
  }
}

// Mock数据
const getMockAdminStats = () => {
  return {
    totalDiaries: 25,
    thisMonthDiaries: 8,
    thisYearDiaries: 45,
    totalImages: 120,
    totalVideos: 15
  }
}

const getMockDiariesWithPagination = (page, size) => {
  const allDiaries = [
    {
      id: 1,
      title: '我们的第一次约会',
      date: '2024-01-15',
      description: '今天是我们第一次约会，一起去看了电影，吃了火锅，度过了美好的一天。',
      images: ['https://picsum.photos/200/150?random=1'],
      videos: [],
      backgroundMusic: null,
      createdAt: '2024-01-15T10:00:00'
    },
    {
      id: 2,
      title: '情人节特别回忆',
      date: '2024-02-14',
      description: '情人节这天，我们一起去了游乐园，坐了摩天轮，在最高点许下了美好的愿望。',
      images: ['https://picsum.photos/200/150?random=2'],
      videos: ['https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_1mb.mp4'],
      backgroundMusic: null,
      createdAt: '2024-02-14T14:30:00'
    },
    {
      id: 3,
      title: '春天的野餐',
      date: '2024-03-20',
      description: '春天来了，我们一起去公园野餐，享受阳光和美食，还有彼此的陪伴。',
      images: ['https://picsum.photos/200/150?random=3', 'https://picsum.photos/200/150?random=4'],
      videos: [],
      backgroundMusic: 'https://www.soundjay.com/nature/sounds/rain-01.wav',
      createdAt: '2024-03-20T12:00:00'
    },
    {
      id: 4,
      title: '夏日海滩之旅',
      date: '2024-06-15',
      description: '今天去了海边，阳光明媚，海风轻拂。我们一起在沙滩上散步，捡贝壳，拍照留念。',
      images: ['https://picsum.photos/200/150?random=5'],
      videos: ['https://sample-videos.com/zip/10/mp4/SampleVideo_640x360_1mb.mp4'],
      backgroundMusic: null,
      createdAt: '2024-06-15T16:00:00'
    },
    {
      id: 5,
      title: '秋日枫叶之旅',
      date: '2024-10-20',
      description: '秋天到了，枫叶红了。我们一起去山里看枫叶，漫山遍野的红叶美不胜收。',
      images: ['https://picsum.photos/200/150?random=6', 'https://picsum.photos/200/150?random=7'],
      videos: [],
      backgroundMusic: 'https://www.soundjay.com/misc/sounds/bell-ringing-05.wav',
      createdAt: '2024-10-20T09:00:00'
    }
  ]
  
  const start = (page - 1) * size
  const end = start + size
  const diaries = allDiaries.slice(start, end)
  
  return {
    content: diaries,
    totalElements: allDiaries.length,
    totalPages: Math.ceil(allDiaries.length / size),
    currentPage: page,
    size: size
  }
}

const getMockRecentDiaries = (limit) => {
  const allDiaries = [
    {
      id: 1,
      title: '我们的第一次约会',
      date: '2024-01-15'
    },
    {
      id: 2,
      title: '情人节特别回忆',
      date: '2024-02-14'
    },
    {
      id: 3,
      title: '春天的野餐',
      date: '2024-03-20'
    },
    {
      id: 4,
      title: '夏日海滩之旅',
      date: '2024-06-15'
    },
    {
      id: 5,
      title: '秋日枫叶之旅',
      date: '2024-10-20'
    }
  ]
  
  return allDiaries.slice(0, limit)
}

const getMockDiaryById = (id) => {
  const allDiaries = [
    {
      id: 1,
      title: '我们的第一次约会',
      date: '2024-01-15',
      description: '今天是我们第一次约会，一起去看了电影，吃了火锅，度过了美好的一天。我们聊了很多，发现彼此有很多共同话题，感觉时间过得特别快。',
      images: ['https://picsum.photos/400/300?random=1', 'https://picsum.photos/400/300?random=2'],
      videos: [],
      backgroundMusic: 'https://www.soundjay.com/misc/sounds/bell-ringing-05.wav'
    },
    {
      id: 2,
      title: '情人节特别回忆',
      date: '2024-02-14',
      description: '情人节这天，我们一起去了游乐园，坐了摩天轮，在最高点许下了美好的愿望。',
      images: ['https://picsum.photos/400/300?random=3'],
      videos: ['https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_1mb.mp4'],
      backgroundMusic: null
    },
    {
      id: 3,
      title: '春天的野餐',
      date: '2024-03-20',
      description: '春天来了，我们一起去公园野餐，享受阳光和美食，还有彼此的陪伴。',
      images: ['https://picsum.photos/400/300?random=4', 'https://picsum.photos/400/300?random=5'],
      videos: [],
      backgroundMusic: 'https://www.soundjay.com/nature/sounds/rain-01.wav'
    }
  ]
  
  return allDiaries.find(diary => diary.id == id) || null
} 