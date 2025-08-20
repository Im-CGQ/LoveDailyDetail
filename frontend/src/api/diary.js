import api from './auth.js'

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
    // 如果后端未启动，使用mock数据
    console.log('后端连接失败，使用mock数据:', error.message)
    return getMockDiaries()
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
    // 如果后端未启动，使用mock数据
    console.log('后端连接失败，使用mock数据:', error.message)
    return getMockLatestDiary()
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
    // 如果后端未启动，使用mock数据
    console.log('后端连接失败，使用mock数据:', error.message)
    return getMockDiaryByDate(date)
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
    throw error
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
    throw error
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
    throw error
  }
}

// Mock数据
const getMockDiaries = () => {
  return [
    {
      id: 1,
      title: '我们的第一次约会',
      date: '2024-01-15',
      description: '今天是我们第一次约会，一起去看了电影，吃了火锅，度过了美好的一天。我们聊了很多，发现彼此有很多共同话题，感觉时间过得特别快。',
      images: [
        'https://picsum.photos/400/300?random=1',
        'https://picsum.photos/400/300?random=2'
      ],
      backgroundMusic: 'https://www.soundjay.com/misc/sounds/bell-ringing-05.wav',
      videos: []
    },
    {
      id: 2,
      title: '春天的野餐',
      date: '2024-03-20',
      description: '今天天气很好，我们一起去公园野餐。准备了水果、三明治和饮料，在草地上铺了毯子，一边吃东西一边聊天，非常惬意。',
      images: [
        'https://picsum.photos/400/300?random=3',
        'https://picsum.photos/400/300?random=4'
      ],
      backgroundMusic: null,
      videos: ['https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_1mb.mp4']
    },
    {
      id: 3,
      title: '夏日海滩之旅',
      date: '2024-06-15',
      description: '今天去了海边，阳光明媚，海风轻拂。我们一起在沙滩上散步，捡贝壳，拍照留念。海水很蓝，天空也很蓝，一切都那么美好。',
      images: [
        'https://picsum.photos/400/300?random=5',
        'https://picsum.photos/400/300?random=6'
      ],
      backgroundMusic: 'https://www.soundjay.com/nature/sounds/rain-01.wav',
      videos: []
    },
    {
      id: 4,
      title: '秋日枫叶之旅',
      date: '2024-10-20',
      description: '秋天到了，枫叶红了。我们一起去山里看枫叶，漫山遍野的红叶美不胜收。拍了很多照片，留下了美好的回忆。',
      images: [
        'https://picsum.photos/400/300?random=7',
        'https://picsum.photos/400/300?random=8'
      ],
      backgroundMusic: null,
      videos: ['https://sample-videos.com/zip/10/mp4/SampleVideo_640x360_1mb.mp4']
    },
    {
      id: 5,
      title: '冬日雪景',
      date: '2024-12-25',
      description: '圣诞节下雪了！我们一起堆雪人，打雪仗，在雪地里留下了我们的脚印。白色的世界很美，但最美的还是你的笑容。',
      images: [
        'https://picsum.photos/400/300?random=9',
        'https://picsum.photos/400/300?random=10'
      ],
      backgroundMusic: 'https://www.soundjay.com/misc/sounds/bell-ringing-05.wav',
      videos: ['https://sample-videos.com/zip/10/mp4/SampleVideo_1280x720_2mb.mp4']
    }
  ]
}

const getMockLatestDiary = () => {
  return getMockDiaries()[0]
}

const getMockDiaryByDate = (date) => {
  const mockDiaries = getMockDiaries()
  return mockDiaries.find(diary => diary.date === date) || null
} 