<template>
  <div class="home romantic-bg page-container">
    <!-- 返回按钮 -->
    <BackButton />
    
    <!-- 爱心装饰 -->
    <div class="heart-decoration heart-1">💕</div>
    <div class="heart-decoration heart-2">💖</div>
    <div class="heart-decoration heart-3">💝</div>
    


    <!-- 全局悬浮音乐播放器 -->
    <div class="global-floating-music-player" v-if="currentDiary && currentDiary.backgroundMusic && currentDiary.backgroundMusic.length > 0">
      <div 
        class="music-icon" 
        :class="{ 'playing': isMusicPlaying, 'show-controls': showMusicControls }"
        @click="toggleMusicControls"
      >
        <span class="music-emoji">🎵</span>
      </div>
      
      <!-- 音乐控制面板 -->
      <div class="music-controls" v-show="showMusicControls">
        <div class="music-info">
          <span class="music-title">{{ currentDiary.backgroundMusic[0].fileName || '背景音乐' }}</span>
          <div class="music-progress">
            <div class="progress-bar" @click="seekMusic" ref="progressBar">
              <div class="progress-fill" :style="{ width: musicProgress + '%' }"></div>
              <div 
                class="progress-handle" 
                :style="{ left: musicProgress + '%' }"
                @mousedown="startDrag"
                @touchstart="startDrag"
              ></div>
            </div>
            <span class="time-display">{{ formatTime(currentTime) }} / {{ formatTime(duration) }}</span>
          </div>
        </div>
        <div class="music-buttons">
          <button class="control-btn" @click="toggleMusic">
            <span v-if="isMusicPlaying">⏸️</span>
            <span v-else>▶️</span>
          </button>
          <button class="control-btn" @click="stopMusic">⏹️</button>
        </div>
      </div>
    </div>
    
    <div class="content" v-if="currentDiary">
      <div class="title-section float">
        <h1 class="main-title text-gradient-romantic">{{ currentDiary.title }}</h1>
        <p class="subtitle pulse">{{ formatDate(currentDiary.date) }}</p>
        <div class="love-counter">
          <span class="counter-number">{{ loveCount }}</span>
          <span class="counter-text">天</span>
          <span class="counter-seconds">{{ loveSeconds }}</span>
        </div>
      </div>

      <div class="media-section hover-lift">
        <!-- 图片展示 -->
        <div v-if="currentDiary.images && currentDiary.images.length > 0" class="image-section">
          <div class="image-header">
            <span class="image-emoji">📸</span>
            <h3 class="image-title">美好照片</h3>
          </div>
          <div class="image-container">
            <div 
              v-for="(image, index) in currentDiary.images" 
              :key="index"
              class="image-wrapper"
            >
            <img 
              :src="image.imageUrl" 
              :alt="`回忆图片 ${index + 1}`" 
              class="memory-image" 
                :style="getImageStyle(image)"
              @click="previewImage(index)"
            />
            </div>
          </div>
        </div>
        
        <!-- 视频播放器 -->
        <div v-if="currentDiary.videos && currentDiary.videos.length > 0" class="video-section">
          <div class="video-header">
            <span class="video-emoji">🎬</span>
            <h3 class="video-title">美好视频</h3>
          </div>
          <div class="video-container" ref="videoSectionRef">
            <div 
              v-for="(video, index) in currentDiary.videos" 
              :key="index"
              class="video-wrapper"
              :style="getVideoStyle(video)"
            >
              <video 
                :src="video.videoUrl"
                class="video-player"
                :style="getVideoStyle(video)"
                preload="metadata"
                :poster="generateVideoPoster(video.videoUrl, video)"
                controls
                @click="playVideo(index)"
                @play="onVideoPlay(index)"
                @pause="onVideoPause(index)"
              >
                您的浏览器不支持视频播放
              </video>
            </div>
          </div>
        </div>
      </div>

      <div class="description-section">
        <div class="description-card glass-effect shimmer">
          <div class="description-header">
            <span class="emoji">💕</span>
            <h3 class="section-title">美好回忆</h3>
          </div>
                     <p 
             class="description-text" 
             :class="{ 'typing-complete': typingComplete }"
             @click="showFullText"
           >
             {{ displayText }}
           </p>
        </div>
      </div>

      <div class="action-section" style="position: relative; z-index: 10;">
        <van-button 
          type="primary" 
          size="large" 
          @click="goToCalendar"
          class="action-btn btn-primary ripple"
          style="position: relative; z-index: 11;"
        >
          <span class="btn-icon">📅</span>
          查看时光日历
        </van-button>
        
        <van-button 
          type="default" 
          size="large" 
          @click="shareMemory"
          class="action-btn share-btn"
          style="position: relative; z-index: 11;"
        >
          <span class="btn-icon">💌</span>
          创建美好回忆
        </van-button>
      </div>
    </div>

    <div v-else-if="isLoading" class="loading-section">
      <div class="loading-heart heartbeat">💕</div>
      <p class="loading-text">正在加载美好回忆...</p>
    </div>
    
    <div v-else class="no-diary-section">
      <div class="no-diary-content">
        <div class="no-diary-icon heartbeat">📝</div>
        <h2 class="no-diary-title">还没有美好回忆</h2>
        <p class="no-diary-subtitle">开始记录你们的美好时光吧</p>
        
        <div class="no-diary-actions" style="position: relative; z-index: 10;">
          <van-button 
            type="primary" 
            size="large" 
            @click="goToCreateDiary"
            class="create-diary-btn ripple"
            style="position: relative; z-index: 11;"
          >
            <span class="btn-icon">✍️</span>
            写第一篇日记
          </van-button>
          
          <van-button 
            type="default" 
            size="large" 
            @click="goToCalendar"
            class="view-calendar-btn"
            style="position: relative; z-index: 11;"
          >
            <span class="btn-icon">📅</span>
            查看时光日历
          </van-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, onBeforeUnmount, onActivated, onDeactivated, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showImagePreview } from 'vant'
import dayjs from 'dayjs'
import { getLatestDiary } from '@/api/diary'
import { getBackgroundMusicAutoplay, getTogetherDate } from '@/api/systemConfig'
import BackButton from '@/components/BackButton.vue'

const router = useRouter()
const currentDiary = ref(null)
const isLoading = ref(true)
const loveCount = ref('')
const loveSeconds = ref('')
const displayText = ref('')
const typingComplete = ref(false)

// 音乐播放相关
const isMusicPlaying = ref(false)
const showMusicControls = ref(false)
const currentTime = ref(0)
const duration = ref(0)
const musicProgress = ref(0)
const progressBar = ref(null)
const isDragging = ref(false)
const audioElement = ref(null)
const progressTimer = ref(null)
const musicAutoplay = ref(true) // 音乐自动播放配置
const playingVideoIndex = ref(-1) // 当前播放的视频索引
const togetherDate = ref('2025-05-30 14:30:00') // 在一起的时间，从后台配置读取
const videoSectionRef = ref(null) // 视频区域容器引用
const containerWidth = ref(400) // 默认容器宽度

let timer = null
let typingTimer = null

// 更新容器宽度
const updateContainerWidth = () => {
  if (videoSectionRef.value) {
    containerWidth.value = videoSectionRef.value.offsetWidth
    // 确保容器宽度在合理范围内
    containerWidth.value = Math.max(300, Math.min(containerWidth.value, 800))
  }
}

// 计算在一起的时间
const calculateLoveTime = () => {
  const now = dayjs()
  const startDate = dayjs(togetherDate.value)
  const diff = now.diff(startDate, 'second')
  
  const days = Math.floor(diff / (24 * 60 * 60))
  const hours = Math.floor((diff % (24 * 60 * 60)) / (60 * 60))
  const minutes = Math.floor((diff % (60 * 60)) / 60)
  const seconds = diff % 60
  
  loveCount.value = days
  loveSeconds.value = `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
}

// 启动定时器，每秒更新一次
const startTimer = () => {
  calculateLoveTime() // 立即计算一次
  timer = setInterval(calculateLoveTime, 1000) // 每秒更新一次
}

// 停止定时器
const stopTimer = () => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
}

const formatDate = (date) => {
  return dayjs(date).format('YYYY年MM月DD日')
}

const goToCalendar = () => {
  router.push('/calendar')
}

const goToCreateDiary = () => {
  router.push('/admin/diary/create')
}

const shareMemory = () => {
  router.push('/admin/diary/create')
}

// 打字机效果
const startTyping = (text) => {
  displayText.value = ''
  typingComplete.value = false
  let index = 0
  
  const typeNextChar = () => {
    if (index < text.length) {
      displayText.value += text[index]
      index++
      typingTimer = setTimeout(typeNextChar, 100) // 每100ms显示一个字
    } else {
      typingComplete.value = true
    }
  }
  
  typeNextChar()
}

// 点击显示全部内容
const showFullText = () => {
  if (currentDiary.value && currentDiary.value.description) {
    displayText.value = currentDiary.value.description
    typingComplete.value = true
    if (typingTimer) {
      clearTimeout(typingTimer)
    }
  }
}

// 生成视频封面URL
const generateVideoPoster = (videoUrl, video) => {
  if (!videoUrl) return ''
  
  // 判断是否为阿里云OSS URL
  if (videoUrl.includes('aliyuncs.com') || videoUrl.includes('oss-')) {
    // 根据视频原始尺寸计算封面尺寸
    let posterWidth = 800
    let posterHeight = 600
    
    if (video && video.width && video.height) {
      const aspectRatio = video.width / video.height
      posterWidth = 800
      posterHeight = Math.round(800 / aspectRatio)
    }
    
    // 直接拼接视频截图参数
    // t_1000: 在1秒处截图
    // f_jpg: 输出JPG格式
    // w_800,h_600: 设置宽高
    // m_fast: 快速模式
    return videoUrl + `?x-oss-process=video/snapshot,t_1000,f_jpg,w_${posterWidth},h_${posterHeight},m_fast`
  }
  
  // 非阿里云OSS URL，返回原URL
  return videoUrl
}

// 图片预览功能
const previewImage = (index) => {
  if (currentDiary.value && currentDiary.value.images) {
    const imageUrls = currentDiary.value.images.map(image => image.imageUrl)
    showImagePreview({
      images: imageUrls,
      startPosition: index,
      closeable: true,
      closeIconPosition: 'top-right',
      showIndex: true,
      swipeDuration: 300,
      showIndicators: true,
      indicatorColor: '#ff6b9d'
    })
  }
}

// 获取图片自适应样式
const getImageStyle = (image) => {
  if (!image || !image.width || !image.height) {
    return {}
  }
  
  // 占满容器宽度，高度自适应
  return {
    width: '100%',
    height: 'auto'
  }
}

// 获取视频自适应样式
const getVideoStyle = (video) => {
  if (!video || !video.height || !video.width) {
    return {
      width: '100%',
      height: '300px'
    }
  }
  
  // 根据视频原始宽高比计算高度，宽度占满
  const aspectRatio = video.width / video.height
  const height = containerWidth.value / aspectRatio
  
  return {
    width: '100%',
    height: `${height}px`,
    objectFit: 'cover' // 让视频内容完全占满容器
  }
}

// 强制停止音乐播放的专用函数
const forceStopMusic = () => {
  if (audioElement.value) {
    audioElement.value.pause()
    audioElement.value.currentTime = 0
    isMusicPlaying.value = false
    currentTime.value = 0
    musicProgress.value = 0
    stopProgressTimer()
  }
}

// 全局媒体管理：停止所有其他媒体播放
const stopOtherMedia = (excludeVideoIndex = null) => {
  // 停止音乐播放 - 强制停止，不管状态如何
  forceStopMusic()
  
  // 停止其他视频播放
  const videoElements = document.querySelectorAll('.video-player')
  videoElements.forEach((video, i) => {
    if (excludeVideoIndex === null || i !== excludeVideoIndex) {
      if (!video.paused) {
        video.pause()
      }
    }
  })
  
  // 更新播放状态
  if (excludeVideoIndex === null) {
    // 如果停止所有视频，清除播放状态
    playingVideoIndex.value = -1
  }
}

// 视频播放功能
const playVideo = (index) => {
  const videoElements = document.querySelectorAll('.video-player')
  const targetVideo = videoElements[index]
  
  if (targetVideo) {
    if (targetVideo.paused) {

      
      // 强制停止音乐播放
      forceStopMusic()
      
      // 停止其他视频播放
      videoElements.forEach((video, i) => {
        if (i !== index && !video.paused) {
          video.pause()
        }
      })
      
      // 播放目标视频
      targetVideo.play().catch(error => {
        console.error('视频播放失败:', error)
        showToast('视频播放失败')
      })
    } else {
      // 暂停目标视频
      targetVideo.pause()
    }
  }
}

// 视频开始播放事件处理
const onVideoPlay = (index) => {
  // 强制停止音乐播放
  forceStopMusic()
  
  // 停止其他视频播放
  const videoElements = document.querySelectorAll('.video-player')
  videoElements.forEach((video, i) => {
    if (i !== index && !video.paused) {
      video.pause()
    }
  })
  
  // 更新当前播放的视频索引
  playingVideoIndex.value = index
}

// 视频暂停事件处理
const onVideoPause = (index) => {
  // 如果暂停的是当前播放的视频，清除播放状态
  if (playingVideoIndex.value === index) {
    playingVideoIndex.value = -1
  }
}





const loadLatestDiary = async () => {
  isLoading.value = true
  try {
    // 加载音乐自动播放配置
    try {
      const autoplayConfig = await getBackgroundMusicAutoplay()
      musicAutoplay.value = autoplayConfig
    } catch (error) {
      console.warn('加载音乐自动播放配置失败，使用默认值:', error)
      musicAutoplay.value = true
    }
    
    // 加载在一起时间配置
    try {
      const togetherDateConfig = await getTogetherDate()
      if (togetherDateConfig) {
        // 如果后台返回的是日期格式，转换为完整的日期时间格式
        if (togetherDateConfig.includes('-') && !togetherDateConfig.includes(':')) {
          togetherDate.value = togetherDateConfig + ' 00:00:00'
        } else {
          togetherDate.value = togetherDateConfig
        }
      }
    } catch (error) {
      console.warn('加载在一起时间配置失败，使用默认值:', error)
      // 保持默认值不变
    }
    
    const diary = await getLatestDiary()
    if (diary) {
      currentDiary.value = diary
      
      // 启动打字机效果
      if (currentDiary.value.description) {
        startTyping(currentDiary.value.description)
      }
      
      // 初始化音乐播放器
      if (currentDiary.value.backgroundMusic) {
        initAudio()
      }
    }
    // 如果没有日记，currentDiary.value 保持为 null，会显示无日记界面
  } catch (error) {
    console.error('加载日记失败:', error)
    showToast('加载失败，请稍后重试')
  } finally {
    isLoading.value = false
  }
}

// 音乐播放相关方法
const toggleMusicControls = () => {
  showMusicControls.value = !showMusicControls.value
}

const toggleMusic = () => {
  if (!audioElement.value) return
  
  // 如果音乐正在播放，停止音乐
  if (isMusicPlaying.value) {
    audioElement.value.pause()
  } else {
    // 播放音乐前，停止所有视频
    stopOtherMedia()
    
    // 然后播放音乐
    audioElement.value.play().catch(error => {
      console.error('音乐播放失败:', error)
      showToast('音乐播放失败')
    })
  }
}

const stopMusic = () => {
  if (!audioElement.value) return
  audioElement.value.pause()
  audioElement.value.currentTime = 0
  isMusicPlaying.value = false
  musicProgress.value = 0
  currentTime.value = 0
}

const initAudio = () => {
  if (!currentDiary.value?.backgroundMusic || currentDiary.value.backgroundMusic.length === 0) return
  
  audioElement.value = new Audio(currentDiary.value.backgroundMusic[0].musicUrl)
  audioElement.value.loop = true
  
  audioElement.value.addEventListener('loadedmetadata', () => {
    duration.value = audioElement.value.duration
    // 根据配置决定是否自动播放
    if (musicAutoplay.value) {
      audioElement.value.play().catch(error => {
        console.warn('自动播放失败:', error)
        // 自动播放失败时不显示错误提示，因为可能是浏览器策略限制
      })
    }
  })
  
  audioElement.value.addEventListener('play', () => {
    isMusicPlaying.value = true
    startProgressTimer()
  })
  
  audioElement.value.addEventListener('pause', () => {
    isMusicPlaying.value = false
    stopProgressTimer()
  })
  
  audioElement.value.addEventListener('ended', () => {
    isMusicPlaying.value = false
    stopProgressTimer()
  })
  
  audioElement.value.addEventListener('error', (e) => {
    console.error('音频播放错误:', e)
    showToast('音乐播放失败')
  })
}

const startProgressTimer = () => {
  if (progressTimer.value) return
  
  progressTimer.value = setInterval(() => {
    if (audioElement.value && !audioElement.value.paused && !isDragging.value) {
      currentTime.value = audioElement.value.currentTime
      musicProgress.value = (audioElement.value.currentTime / audioElement.value.duration) * 100
    }
  }, 100)
}

const stopProgressTimer = () => {
  if (progressTimer.value) {
    clearInterval(progressTimer.value)
    progressTimer.value = null
  }
}

const formatTime = (seconds) => {
  if (!seconds || isNaN(seconds)) return '0:00'
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins}:${secs.toString().padStart(2, '0')}`
}

// 点击进度条跳转
const seekMusic = (event) => {
  if (!audioElement.value || !progressBar.value) return
  
  const rect = progressBar.value.getBoundingClientRect()
  const clickX = event.clientX - rect.left
  const progressBarWidth = rect.width
  const percentage = (clickX / progressBarWidth) * 100
  
  // 限制百分比在0-100之间
  const clampedPercentage = Math.max(0, Math.min(100, percentage))
  const newTime = (clampedPercentage / 100) * audioElement.value.duration
  
  audioElement.value.currentTime = newTime
  currentTime.value = newTime
  musicProgress.value = clampedPercentage
}

// 开始拖拽进度条
const startDrag = (event) => {
  if (!audioElement.value) return
  isDragging.value = true
  document.addEventListener('mousemove', onDrag)
  document.addEventListener('mouseup', stopDrag)
  document.addEventListener('touchmove', onDrag)
  document.addEventListener('touchend', stopDrag)
}

// 拖拽进度条
const onDrag = (event) => {
  if (!isDragging.value || !audioElement.value || !progressBar.value) return
  
  event.preventDefault()
  const rect = progressBar.value.getBoundingClientRect()
  const clientX = event.clientX || (event.touches && event.touches[0] ? event.touches[0].clientX : 0)
  const clickX = clientX - rect.left
  const progressBarWidth = rect.width
  const percentage = (clickX / progressBarWidth) * 100
  
  // 限制百分比在0-100之间
  const clampedPercentage = Math.max(0, Math.min(100, percentage))
  const newTime = (clampedPercentage / 100) * audioElement.value.duration
  
  audioElement.value.currentTime = newTime
  currentTime.value = newTime
  musicProgress.value = clampedPercentage
}

// 停止拖拽
const stopDrag = () => {
  isDragging.value = false
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
  document.removeEventListener('touchmove', onDrag)
  document.removeEventListener('touchend', stopDrag)
}

// 监听currentDiary变化，在DOM更新后更新容器宽度
watch(currentDiary, () => {
  if (currentDiary.value && currentDiary.value.videos && currentDiary.value.videos.length > 0) {
    nextTick(() => {
      updateContainerWidth()
    })
  }
}, { immediate: true })

onMounted(() => {
  loadLatestDiary()
  startTimer() // 启动计时器
  updateContainerWidth()
  
  // 监听页面可见性变化
  document.addEventListener('visibilitychange', handleVisibilityChange)
})

// 页面可见性变化处理
const handleVisibilityChange = () => {
  if (document.hidden) {
    // 页面隐藏时暂停所有媒体
    forceStopMusic()
    playingVideoIndex.value = -1
  }
}

onUnmounted(() => {
  stopTimer() // 组件卸载时停止计时器
  if (typingTimer) {
    clearTimeout(typingTimer) // 清理打字机定时器
  }
  
  // 停止所有媒体播放
  forceStopMusic()
  playingVideoIndex.value = -1
  
  // 清理音乐播放器
  if (audioElement.value) {
    audioElement.value.pause()
    audioElement.value = null
  }
  if (progressTimer.value) {
    clearInterval(progressTimer.value)
    progressTimer.value = null
  }
  
  // 清理拖拽事件监听器
  stopDrag()
  
  // 清理页面可见性监听器
  document.removeEventListener('visibilitychange', handleVisibilityChange)
})

// 添加路由离开守卫
onBeforeUnmount(() => {
  // 停止音乐播放
  forceStopMusic()
  
  // 停止所有视频播放
  const videoElements = document.querySelectorAll('.video-player')
  videoElements.forEach(video => {
    if (!video.paused) {
      video.pause()
    }
  })
  
  // 清理音乐播放器
  if (audioElement.value) {
    audioElement.value.pause()
    audioElement.value.currentTime = 0
    audioElement.value = null
  }
  
  // 清理进度定时器
  if (progressTimer.value) {
    clearInterval(progressTimer.value)
    progressTimer.value = null
  }
  
  // 重置播放状态
  isMusicPlaying.value = false
  showMusicControls.value = false
  currentTime.value = 0
  musicProgress.value = 0
  playingVideoIndex.value = -1
})


</script>

<style lang="scss" scoped>
.home {
  padding: 20px;
  position: relative;
}

.content {
  max-width: 600px;
  margin: 0 auto;
  position: relative;
  z-index: 2;
  padding: 20px;
  padding-top: 100px;
  padding-bottom: 40px;
}

.title-section {
  text-align: center;
  margin-bottom: 30px;
  
  .main-title {
    font-size: 32px;
    font-weight: bold;
    margin-bottom: 15px;
  }
  
  .subtitle {
    font-size: 18px;
    color: white;
    opacity: 0.9;
    margin-bottom: 20px;
  }
  
  .love-counter {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    background: rgba(255, 255, 255, 0.15);
    padding: 12px 20px;
    border-radius: 25px;
    backdrop-filter: blur(15px);
    border: 2px solid rgba(255, 255, 255, 0.2);
    box-shadow: 0 6px 24px rgba(255, 107, 157, 0.2);
    transition: all 0.3s ease;
    margin: 0 auto;
    width: fit-content;
    
    &:hover {
      background: rgba(255, 255, 255, 0.2);
      border-color: rgba(255, 255, 255, 0.3);
      box-shadow: 0 8px 32px rgba(255, 107, 157, 0.3);
      transform: translateY(-2px);
    }
    
    .counter-number {
      font-size: 24px;
      font-weight: 800;
      color: #ff6b9d;
      text-shadow: 0 3px 6px rgba(255, 107, 157, 0.4);
      font-family: 'Arial', sans-serif;
      letter-spacing: 1px;
      background: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
    
    .counter-text {
      font-size: 16px;
      color: white;
      font-weight: 600;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
    }
    
    .counter-seconds {
      font-size: 14px;
      color: rgba(255, 255, 255, 0.9);
      font-weight: 500;
      font-family: 'Courier New', monospace;
      background: rgba(255, 255, 255, 0.1);
      padding: 3px 6px;
      border-radius: 6px;
      border: 1px solid rgba(255, 255, 255, 0.2);
    }
  }
}

.media-section {
  margin-bottom: 30px;
  padding-top: 20px;
  

  
  /* 图片展示样式 */
  .image-section {
    margin-bottom: 20px;
    
    .image-header {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 10px;
      margin-bottom: 15px;
      
      .image-emoji {
        font-size: 24px;
        animation: heartbeat 2s ease-in-out infinite;
      }
      
      .image-title {
        color: white;
        font-size: 20px;
        font-weight: bold;
        margin: 0;
        text-align: center;
      }
    }
    
    .image-container {
      display: flex;
      flex-direction: column;
      gap: 15px;
      
      .image-wrapper {
    border-radius: 20px;
        overflow: visible;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
        transition: all 0.3s ease;
        
        &:hover {
          transform: scale(1.02);
          box-shadow: 0 12px 40px rgba(0, 0, 0, 0.3);
        }
    
    .memory-image {
          width: 100% !important;
          height: auto !important;
          max-height: none !important;
          display: block;
      cursor: pointer;
      transition: transform 0.3s ease;
      
      &:hover {
        transform: scale(1.02);
          }
        }
      }
    }
  }
  
  /* 视频播放器样式 */
  .video-section {
    margin-top: 20px;
    
    .video-header {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 10px;
      margin-bottom: 15px;
      
      .video-emoji {
        font-size: 24px;
        animation: heartbeat 2s ease-in-out infinite;
      }
      
      .video-title {
        color: white;
        font-size: 20px;
        font-weight: bold;
        margin: 0;
        text-align: center;
      }
      
      
    }
    
         .video-container {
       display: flex;
       flex-direction: column;
       gap: 15px;
       
               .video-wrapper {
          position: relative;
          cursor: pointer;
          border-radius: 20px;
          overflow: hidden;
          box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
          transition: all 0.3s ease;
          
          &:hover {
            transform: scale(1.02);
            box-shadow: 0 12px 40px rgba(0, 0, 0, 0.3);
          }
        }
        
        .video-player {
          border-radius: 20px;
          overflow: hidden;
          background: #000;
          transition: all 0.3s ease;
          display: block;
          cursor: pointer;
          
          &:hover {
            transform: scale(1.02);
          }
          

         }
     }
  }
}

.description-section {
  margin-bottom: 30px;
  
  .description-card {
    padding: 25px;
    border-radius: 20px;
    
    .description-header {
      display: flex;
      align-items: center;
      gap: 10px;
      margin-bottom: 15px;
      
      .emoji {
        font-size: 24px;
        animation: heartbeat 2s ease-in-out infinite;
      }
      
      .section-title {
        color: white;
        font-size: 20px;
        font-weight: bold;
        margin: 0;
      }
    }
    
         .description-text {
       font-size: 16px;
       line-height: 1.8;
       color: white;
       text-align: justify;
       cursor: pointer;
       position: relative;
       transition: all 0.3s ease;
       white-space: pre-wrap;
       word-wrap: break-word;
       
       &:hover {
         background: rgba(255, 255, 255, 0.1);
         border-radius: 8px;
         padding: 8px;
         margin: -8px;
       }
       
       
     }
  }
}

.action-section {
  display: flex;
  flex-direction: column;
  gap: 15px;
  
  .action-btn {
    height: 56px;
    border-radius: 28px;
    font-size: 18px;
    font-weight: 500;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    
    .btn-icon {
      font-size: 20px;
    }
    
    &.share-btn {
      background: rgba(255, 255, 255, 0.2);
      border: 2px solid rgba(255, 255, 255, 0.3);
      color: white;
      backdrop-filter: blur(10px);
      
      &:hover {
        background: rgba(255, 255, 255, 0.3);
        transform: translateY(-2px);
      }
    }
    

  }
}

.loading-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 60vh;
  gap: 20px;
  
  .loading-heart {
    font-size: 64px;
  }
  
  .loading-text {
    color: white;
    font-size: 18px;
    font-weight: 500;
  }
}

.no-diary-section {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  padding: 20px;
}

.no-diary-content {
  text-align: center;
  max-width: 400px;
  width: 100%;
  
  .no-diary-icon {
    font-size: 80px;
    margin-bottom: 20px;
    color: #ff6b9d;
  }
  
  .no-diary-title {
    font-size: 24px;
    font-weight: bold;
    color: white;
    margin-bottom: 10px;
  }
  
  .no-diary-subtitle {
    font-size: 16px;
    color: rgba(255, 255, 255, 0.8);
    margin-bottom: 30px;
  }
}

.no-diary-actions {
  display: flex;
  flex-direction: column;
  gap: 15px;
  
  .van-button {
    height: 50px;
    font-size: 16px;
    font-weight: 600;
    border-radius: 25px;
    
    .btn-icon {
      margin-right: 8px;
    }
  }
  
  .create-diary-btn {
    background: linear-gradient(135deg, #ff6b9d 0%, #ff8e9e 100%);
    border: none;
    color: white;
    
    &:hover {
      background: linear-gradient(135deg, #ff5a8c 0%, #ff7d8e 100%);
    }
  }
  
  .view-calendar-btn {
    background: rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
    color: white;
    
    &:hover {
      background: rgba(255, 255, 255, 0.2);
    }
  }
}

@media (max-width: 768px) {
  .home {
    padding: 15px;
  }
  
  .global-floating-music-player {
    right: 15px;
    
    .music-icon {
      width: 35px;
      height: 35px;
      
      .music-emoji {
        font-size: 18px;
      }
    }
    
    .music-controls {
      width: 240px;
      right: -20px;
    }
  }
}

/* 全局悬浮音乐播放器样式 */
.global-floating-music-player {
  position: fixed;
  top: 100px;
  right: 20px;
  transform: translateY(-50%);
  z-index: 1000;
  
  .music-icon {
    width: 40px;
    height: 40px;
    background: linear-gradient(135deg, rgba(255, 107, 157, 0.7) 0%, rgba(255, 143, 171, 0.7) 100%);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    box-shadow: 0 4px 20px rgba(255, 107, 157, 0.3);
    transition: all 0.3s ease;
    position: relative;
    
    &:hover {
      transform: scale(1.1);
      box-shadow: 0 6px 25px rgba(255, 107, 157, 0.4);
    }
    
    &.playing {
      animation: rotate 3s linear infinite;
    }
    
    .music-emoji {
      font-size: 20px;
      color: white;
    }
  }
  
  .music-controls {
    position: absolute;
    top: 60px;
    right: 0;
    width: 280px;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(15px);
    border-radius: 15px;
    padding: 15px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
    border: 1px solid rgba(255, 255, 255, 0.3);
    animation: slideIn 0.3s ease;
    z-index: 1000;
    
    .music-info {
      margin-bottom: 12px;
      
      .music-title {
        display: block;
        font-size: 14px;
        font-weight: 600;
        color: #333;
        margin-bottom: 8px;
      }
      
      .music-progress {
        .progress-bar {
          width: 100%;
          height: 4px;
          background: rgba(0, 0, 0, 0.1);
          border-radius: 2px;
          overflow: visible;
          margin-bottom: 6px;
          position: relative;
          cursor: pointer;
          
          .progress-fill {
            height: 100%;
            background: linear-gradient(90deg, #ff6b9d 0%, #ff8fab 100%);
            border-radius: 2px;
            transition: width 0.1s ease;
          }
          
          .progress-handle {
            position: absolute;
            top: 50%;
            transform: translate(-50%, -50%);
            width: 12px;
            height: 12px;
            background: #ff6b9d;
            border-radius: 50%;
            border: 2px solid white;
            box-shadow: 0 2px 8px rgba(255, 107, 157, 0.4);
            cursor: grab;
            transition: all 0.2s ease;
            z-index: 10;
            
            &:hover {
              transform: translate(-50%, -50%) scale(1.2);
              box-shadow: 0 4px 12px rgba(255, 107, 157, 0.6);
            }
            
            &:active {
              cursor: grabbing;
              transform: translate(-50%, -50%) scale(1.1);
            }
          }
        }
        
        .time-display {
          font-size: 12px;
          color: #666;
          font-family: 'Courier New', monospace;
        }
      }
    }
    
    .music-buttons {
      display: flex;
      gap: 8px;
      justify-content: center;
      
      .control-btn {
        width: 36px;
        height: 36px;
        border: none;
        border-radius: 50%;
        background: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 100%);
        color: white;
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 16px;
        transition: all 0.3s ease;
        
        &:hover {
          transform: scale(1.1);
          box-shadow: 0 4px 12px rgba(255, 107, 157, 0.4);
        }
        
        &:active {
          transform: scale(0.95);
        }
      }
    }
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}



@media (max-width: 768px) {
  .content {
    padding: 15px;
    padding-top: 80px;
  }
  
  .title-section .main-title {
    font-size: 28px;
  }
  
  .media-section {
    padding-top: 15px;
  }
  
  .media-section .image-section .image-container .image-wrapper .memory-image {
    max-height: none !important;
  }
  

  
  .action-section .action-btn {
    height: 48px;
    font-size: 16px;
  }
}


</style> 