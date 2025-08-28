<template>
  <div class="detail romantic-bg page-container">
    <!-- 返回按钮 -->
    <BackButton />
    
    <!-- 爱心装饰 -->
    <div class="heart-decoration heart-1">💕</div>
    <div class="heart-decoration heart-2">💖</div>
    <div class="heart-decoration heart-3">💝</div>
    

    
    <!-- 全局悬浮音乐播放器 -->
    <div class="global-floating-music-player" v-if="diary && diary.backgroundMusic && diary.backgroundMusic.length > 0">
      <div 
        class="music-icon" 
        :class="{ 'playing': isMusicPlaying, 'show-controls': showMusicControls }"
        @click="toggleMusicControls"
      >
                 <span class="music-emoji">🎶</span>
        
      </div>
      
      <!-- 音乐控制面板 -->
      <div class="music-controls" v-show="showMusicControls">
        <div class="music-info">
          <span class="music-title">{{ diary.backgroundMusic[0].fileName || '背景音乐' }}</span>
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
    
    <div class="content" v-if="diary">
      <div class="header float">
        <h1 class="title text-gradient-romantic">{{ diary.title }}</h1>
        <p class="date pulse">{{ formatDate(diary.date) }}</p>
      </div>

      <div class="media hover-lift">
        <!-- 图片展示 -->
        <div v-if="diary.images && diary.images.length > 0" class="image-section">
          <div class="image-header">
            <span class="image-emoji">📸</span>
            <h3 class="image-title">美好照片</h3>
          </div>
          <div class="image-container">
            <div 
              v-for="(image, index) in diary.images" 
              :key="index"
              class="image-wrapper"
            >
            <img 
              :src="image.imageUrl" 
              class="image" 
                :style="getImageStyle(image)"
              @click="previewImage(index)"
            />
            </div>
          </div>
        </div>
        
        <!-- 视频播放器 -->
        <div v-if="diary.videos && diary.videos.length > 0" class="video-section">
          <div class="video-header">
            <span class="video-emoji">🎬</span>
            <h3 class="video-title">美好视频</h3>
          </div>
          <div class="video-container" ref="videoSectionRef">
            <div 
              v-for="(video, index) in diary.videos" 
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
                ref="videoRefs"
              >
                您的浏览器不支持视频播放
              </video>
            </div>
          </div>
        </div>
      </div>

      <div class="description">
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

      <div class="actions">
        <van-button 
          type="primary" 
          size="large" 
          @click="share" 
          class="action-btn btn-primary ripple"
        >
          <span class="btn-icon">🔗</span>
          分享美好回忆
        </van-button>
        
        <van-button 
          type="default" 
          size="large" 
          @click="goBackToCalendar" 
          class="action-btn calendar-btn"
        >
          <span class="btn-icon">📅</span>
          返回日历
        </van-button>
      </div>
    </div>

    <div v-else class="loading">
      <div class="loading-heart heartbeat">💕</div>
      <p class="loading-text">加载中...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast, showImagePreview } from 'vant'
import { getDiaryById } from '@/api/diary'
import { getBackgroundMusicAutoplay, getShareExpireMinutes } from '@/api/systemConfig'
import { createShareLink } from '@/api/share'
import { copyToClipboard } from '@/utils/clipboard'
import BackButton from '@/components/BackButton.vue'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const diary = ref(null)
const displayText = ref('')
const typingComplete = ref(false)
let typingTimer = null

// 音乐播放相关
const isMusicPlaying = ref(false)
const showMusicControls = ref(false)
const currentTime = ref(0)
const duration = ref(0)
const musicProgress = ref(0)
const progressBar = ref(null)
const isDragging = ref(false)
const musicAutoplay = ref(true) // 音乐自动播放配置
const playingVideoIndex = ref(-1) // 当前播放的视频索引
let audioElement = null
let progressTimer = null
const videoSectionRef = ref(null) // 视频区域容器引用
const containerWidth = ref(400) // 默认容器宽度

// 更新容器宽度
const updateContainerWidth = () => {
  if (videoSectionRef.value) {
    containerWidth.value = videoSectionRef.value.offsetWidth
    // 确保容器宽度在合理范围内
    containerWidth.value = Math.max(300, Math.min(containerWidth.value, 800))
  }
}

const formatDate = (date) => {
  return dayjs(date).format('YYYY年MM月DD日')
}

const share = async () => {
  try {
    // 获取当前日记ID
    const diaryId = route.params.id
    if (!diaryId) {
      showToast('日记ID不存在')
      return
    }
    
    // 创建分享链接
    const shareData = await createShareLink(diaryId)
    
    // 获取分享过期时间配置并显示
    const minutes = await getShareExpireMinutes()
    const hours = Math.floor(minutes / 60)
    const remainingMinutes = minutes % 60
    let timeText = ''
    
    if (hours > 0) {
      timeText = `${hours}小时`
      if (remainingMinutes > 0) {
        timeText += `${remainingMinutes}分钟`
      }
    } else {
      timeText = `${remainingMinutes}分钟`
    }
    
    // 构建完整的分享链接（优先使用后端返回的 shareUrl）
    const baseUrl = window.location.origin
    const fullShareUrl = shareData.shareUrl
      ? `${baseUrl}${shareData.shareUrl}`
      : `${baseUrl}/share/diary/${shareData.shareToken}`
    
    // 复制分享链接到剪贴板（统一工具方法）
    const success = await copyToClipboard(fullShareUrl)
    if (success) {
      showToast(`分享链接已复制到剪贴板，将在${timeText}后过期`)
    } else {
      showToast(`复制失败，请手动复制：${fullShareUrl}`)
    }
    
  } catch (error) {
    console.error('创建分享链接失败:', error)
    showToast('分享功能暂时不可用')
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
  if (diary.value && diary.value.images) {
    const imageUrls = diary.value.images.map(image => image.imageUrl)
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

// 页面可见性变化处理
const handleVisibilityChange = () => {
  if (document.hidden) {
    // 页面隐藏时暂停所有媒体
    stopOtherMedia()
  }
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
  if (diary.value && diary.value.description) {
    displayText.value = diary.value.description
    typingComplete.value = true
    if (typingTimer) {
      clearTimeout(typingTimer)
    }
  }
}



// 音乐播放相关方法
const toggleMusicControls = () => {
  showMusicControls.value = !showMusicControls.value
}

const toggleMusic = () => {
  if (!audioElement) {
    initAudio()
  }
  
  if (isMusicPlaying.value) {
    audioElement.pause()
    isMusicPlaying.value = false
    if (progressTimer) {
      clearInterval(progressTimer)
      progressTimer = null
    }
  } else {

    
    // 播放音乐前，停止所有视频
    stopOtherMedia()
    
    // 然后播放音乐
    audioElement.play().catch(error => {
      console.error('音乐播放失败:', error)
      showToast('音乐播放失败')
    })
    isMusicPlaying.value = true
    startProgressTimer()
  }
}

const stopMusic = () => {
  if (audioElement) {
    audioElement.pause()
    audioElement.currentTime = 0
    isMusicPlaying.value = false
    currentTime.value = 0
    musicProgress.value = 0
    if (progressTimer) {
      clearInterval(progressTimer)
      progressTimer = null
    }
  }
}

// 强制停止音乐播放的专用函数
const forceStopMusic = () => {
  if (audioElement) {
    audioElement.pause()
    audioElement.currentTime = 0
    isMusicPlaying.value = false
    currentTime.value = 0
    musicProgress.value = 0
    if (progressTimer) {
      clearInterval(progressTimer)
      progressTimer = null
    }
  }
}

const initAudio = () => {
  if (!diary.value?.backgroundMusic || diary.value.backgroundMusic.length === 0) return
  
  audioElement = new Audio(diary.value.backgroundMusic[0].musicUrl)
  audioElement.loop = true
  
  audioElement.addEventListener('loadedmetadata', () => {
    duration.value = audioElement.duration
    // 根据配置决定是否自动播放
    if (musicAutoplay.value) {
      audioElement.play().catch(error => {
        console.warn('自动播放失败:', error)
        // 自动播放失败时不显示错误提示，因为可能是浏览器策略限制
      })
    }
  })
  
  audioElement.addEventListener('play', () => {
    isMusicPlaying.value = true
  })
  
  audioElement.addEventListener('pause', () => {
    isMusicPlaying.value = false
  })
  
  audioElement.addEventListener('ended', () => {
    isMusicPlaying.value = false
    if (progressTimer) {
      clearInterval(progressTimer)
      progressTimer = null
    }
  })
  
  audioElement.addEventListener('error', () => {
    showToast('音乐加载失败')
  })
}

const startProgressTimer = () => {
  if (progressTimer) {
    clearInterval(progressTimer)
  }
  
  progressTimer = setInterval(() => {
    if (audioElement && !audioElement.paused && !isDragging.value) {
      currentTime.value = audioElement.currentTime
      musicProgress.value = (audioElement.currentTime / audioElement.duration) * 100
    }
  }, 100)
}

const formatTime = (time) => {
  if (!time || isNaN(time)) return '0:00'
  const minutes = Math.floor(time / 60)
  const seconds = Math.floor(time % 60)
  return `${minutes}:${seconds.toString().padStart(2, '0')}`
}

// 点击进度条跳转
const seekMusic = (event) => {
  if (!audioElement || !progressBar.value) return
  
  const rect = progressBar.value.getBoundingClientRect()
  const clickX = event.clientX - rect.left
  const progressBarWidth = rect.width
  const percentage = (clickX / progressBarWidth) * 100
  
  // 限制百分比在0-100之间
  const clampedPercentage = Math.max(0, Math.min(100, percentage))
  const newTime = (clampedPercentage / 100) * audioElement.duration
  
  audioElement.currentTime = newTime
  currentTime.value = newTime
  musicProgress.value = clampedPercentage
}

// 开始拖拽进度条
const startDrag = (event) => {
  if (!audioElement) return
  isDragging.value = true
  document.addEventListener('mousemove', onDrag)
  document.addEventListener('mouseup', stopDrag)
  document.addEventListener('touchmove', onDrag)
  document.addEventListener('touchend', stopDrag)
}

// 拖拽进度条
const onDrag = (event) => {
  if (!isDragging.value || !audioElement || !progressBar.value) return
  
  event.preventDefault()
  const rect = progressBar.value.getBoundingClientRect()
  const clientX = event.clientX || (event.touches && event.touches[0] ? event.touches[0].clientX : 0)
  const clickX = clientX - rect.left
  const progressBarWidth = rect.width
  const percentage = (clickX / progressBarWidth) * 100
  
  // 限制百分比在0-100之间
  const clampedPercentage = Math.max(0, Math.min(100, percentage))
  const newTime = (clampedPercentage / 100) * audioElement.duration
  
  audioElement.currentTime = newTime
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

const loadDiary = async () => {
  const id = route.params.id
  try {
    // 加载音乐自动播放配置
    try {
      const autoplayConfig = await getBackgroundMusicAutoplay()
      musicAutoplay.value = autoplayConfig
    } catch (error) {
      console.warn('加载音乐自动播放配置失败，使用默认值:', error)
      musicAutoplay.value = true
    }
    
    const diaryData = await getDiaryById(id)
    diary.value = diaryData
    
    // 启动打字机效果
    if (diary.value && diary.value.description) {
      startTyping(diary.value.description)
    }
    
    // 初始化音乐播放器
    if (diary.value?.backgroundMusic && diary.value.backgroundMusic.length > 0) {
      initAudio()
    }
  } catch (error) {
    console.error('加载日记失败:', error)
    showToast('加载日记失败，请稍后重试')
  }
}

// 监听diary变化，在DOM更新后更新容器宽度
watch(diary, () => {
  if (diary.value && diary.value.videos && diary.value.videos.length > 0) {
    nextTick(() => {
      updateContainerWidth()
    })
  }
}, { immediate: true })

onMounted(() => {
  loadDiary()
  updateContainerWidth()
  
  // 监听页面可见性变化
  document.addEventListener('visibilitychange', handleVisibilityChange)
})

// 返回日历页面，保持之前的状态
const goBackToCalendar = () => {
  router.push('/calendar')
}

onUnmounted(() => {
  if (typingTimer) {
    clearTimeout(typingTimer) // 清理打字机定时器
  }
  
  // 停止所有媒体播放
  stopOtherMedia()
  
  // 清理音乐播放器资源
  if (audioElement) {
    audioElement.pause()
    audioElement = null
  }
  if (progressTimer) {
    clearInterval(progressTimer)
    progressTimer = null
  }
  
  // 清理拖拽事件监听器
  stopDrag()
  
  // 清理页面可见性监听器
  document.removeEventListener('visibilitychange', handleVisibilityChange)
})
</script>

<style scoped>
.detail {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  position: relative;
}





.content {
  padding: 20px;
  padding-top: 100px;
  position: relative;
  z-index: 2;
  padding-bottom: 40px;
}

.header {
  text-align: center;
  margin-bottom: 25px;
  
  .title {
    font-size: 28px;
    font-weight: bold;
    margin-bottom: 12px;
  }
  
  .date {
    font-size: 18px;
    color: rgba(255, 255, 255, 0.9);
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

.media {
  margin-bottom: 25px;
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
    overflow: hidden;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
        transition: all 0.3s ease;
        
        &:hover {
          transform: scale(1.02);
          box-shadow: 0 12px 40px rgba(0, 0, 0, 0.3);
        }
    
         .image {
          width: 100% !important;
          height: auto !important;
          max-height: none !important;
          display: block;
          cursor: pointer;
          transition: transform 0.3s ease;
          border-radius: 20px;
          
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

.description {
  margin-bottom: 25px;
  
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

.actions {
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
    
    &.calendar-btn {
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

.loading {
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

@media (max-width: 768px) {
  .content {
    padding: 15px;
    padding-top: 80px;
  }
  
  .header .title {
    font-size: 24px;
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
  
  .media {
    padding-top: 15px;
  }
  
  .media .image-section .image-container .image-wrapper .image {
    max-height: none !important;
  }
  

  
  .actions .action-btn {
    height: 48px;
    font-size: 16px;
  }
}

/* 动画关键帧 */
@keyframes rotate {
  from {
    transform: translateY(-50%) rotate(0deg);
  }
  to {
    transform: translateY(-50%) rotate(360deg);
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



</style> 