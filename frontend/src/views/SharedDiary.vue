<template>
  <div class="detail romantic-bg page-container">

    <!-- 爱心装饰 -->
    <div class="heart-decoration heart-1">💕</div>
    <div class="heart-decoration heart-2">💖</div>
    <div class="heart-decoration heart-3">💝</div>
    

    
    <!-- 全局悬浮音乐播放器 -->
    <div class="global-floating-music-player" v-if="diary && diary.backgroundMusic">
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
          <span class="music-title">背景音乐</span>
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
        <div v-if="diary.images && diary.images.length > 0" class="images-section">
          <div class="images-header">
            <span class="images-emoji">📸</span>
            <h3 class="images-title">美好瞬间</h3>
          </div>
          <div class="images-container">
            <div 
              v-for="(image, index) in diary.images" 
              :key="index"
              class="image-wrapper"
            >
              <img 
                :src="image" 
                class="image" 
                @click="previewImage(index)"
                @load="onImageLoad"
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
          <div class="video-container">
                         <div 
               v-for="(video, index) in diary.videos" 
               :key="index"
               class="video-wrapper"
             >
                               <video 
                  :src="video"
                  :poster="getVideoPoster(video)"
                  class="video-player"
                  preload="metadata"
                  controls
                  @ended="onVideoEnded"
                  @play="onVideoPlay"
                  @pause="onVideoPause"
                  @loadstart="onVideoLoadStart"
                  @loadeddata="onVideoLoadedData"
                  @loadedmetadata="onVideoLoadedMetadata"
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

      <!-- 分享页面不需要操作按钮 -->
    </div>
 

    <div v-else class="loading">
      <div class="loading-heart heartbeat">💕</div>
      <p class="loading-text">加载中...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast, showImagePreview } from 'vant'
import { getSharedDiary } from '@/api/share'
import { getBackgroundMusicAutoplay } from '@/api/systemConfig'
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
let audioElement = null
let progressTimer = null

const formatDate = (date) => {
  return dayjs(date).format('YYYY年MM月DD日')
}

// 分享页面不需要分享功能

// 图片预览功能
const previewImage = (index) => {
  if (diary.value && diary.value.images) {
    showImagePreview({
      images: diary.value.images,
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

// 视频播放相关方法
const playVideo = (index) => {
  // 移除全屏播放逻辑，现在视频直接播放
  console.log('视频播放，索引:', index)
}

const onVideoEnded = () => {
  console.log('视频播放结束')
}

const onVideoPlay = (event) => {
  console.log('视频开始播放')
  // 停止背景音乐
  if (audioElement && isMusicPlaying.value) {
    audioElement.pause()
    isMusicPlaying.value = false
    if (progressTimer) {
      clearInterval(progressTimer)
      progressTimer = null
    }
  }
  
  // 停止其他视频
  const currentVideo = event.target
  const allVideos = document.querySelectorAll('.video-player')
  allVideos.forEach(video => {
    if (video !== currentVideo && !video.paused) {
      video.pause()
    }
  })
}

const onVideoPause = () => {
  console.log('视频暂停')
}

// 视频加载开始
const onVideoLoadStart = (event) => {
  console.log('视频开始加载')
}

// 视频数据加载完成
const onVideoLoadedData = (event) => {
  console.log('视频数据加载完成')
}

// 视频元数据加载完成
const onVideoLoadedMetadata = (event) => {
  console.log('视频元数据加载完成')
}

// 图片加载完成事件
const onImageLoad = (event) => {
  // 图片加载完成后的处理逻辑
  console.log('图片加载完成')
}

// 生成视频封面
const getVideoPoster = (videoUrl) => {
  if (!videoUrl) return ''
  
  // 检查是否是OSS URL
  if (videoUrl.includes('aliyuncs.com')) {
    // 添加OSS视频截图参数
    return `${videoUrl}?x-oss-process=video/snapshot,t_1000,f_jpg,w_800,h_600,m_fast`
  }
  
  // 如果不是OSS URL，返回空字符串（使用视频默认封面）
  return ''
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
    // 停止所有视频播放
    const allVideos = document.querySelectorAll('.video-player')
    allVideos.forEach(video => {
      if (!video.paused) {
        video.pause()
      }
    })
    
    audioElement.play()
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

const initAudio = () => {
  if (!diary.value?.backgroundMusic) return
  
  audioElement = new Audio(diary.value.backgroundMusic)
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
  
  // 监听音乐播放事件，停止所有视频
  audioElement.addEventListener('play', () => {
    const allVideos = document.querySelectorAll('.video-player')
    allVideos.forEach(video => {
      if (!video.paused) {
        video.pause()
      }
    })
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
  const shareToken = route.params.shareToken
  try {
    const diaryData = await getSharedDiary(shareToken)
    diary.value = diaryData
    
    // 启动打字机效果
    if (diary.value && diary.value.description) {
      startTyping(diary.value.description)
    }
    
    // 初始化音乐播放器
    if (diary.value?.backgroundMusic) {
      initAudio()
    }
  } catch (error) {
    console.error('加载分享日记失败:', error)
    showToast('分享链接已过期或不存在')
  }
}

onMounted(() => {
  loadDiary()
})

// 返回上一页
const goBack = () => {
  router.go(-1)
}

// 分享页面不需要返回日历功能

onUnmounted(() => {
  if (typingTimer) {
    clearTimeout(typingTimer) // 清理打字机定时器
  }
  
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
})
</script>

<style scoped>
.detail {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  position: relative;
}

.back-button {
  position: fixed;
  top: 20px;
  left: 20px;
  z-index: 1000;
  
  .van-icon {
    font-size: 24px;
    color: #ffffff;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
    border-radius: 50%;
    padding: 10px;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
    
    &:hover {
      background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 50%, #e085e8 100%);
      transform: scale(1.1);
      box-shadow: 0 6px 16px rgba(102, 126, 234, 0.6);
    }
  }
}



.content {
  padding: 20px;
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
  
  /* 图片展示样式 */
  .images-section {
    margin-bottom: 20px;
    
    .images-header {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 10px;
      margin-bottom: 15px;
      
      .images-emoji {
        font-size: 24px;
        animation: heartbeat 2s ease-in-out infinite;
      }
      
      .images-title {
        color: white;
        font-size: 20px;
        font-weight: bold;
        margin: 0;
        text-align: center;
      }
    }
    
    .images-container {
      display: flex;
      flex-direction: column;
      gap: 15px;
      
      .image-wrapper {
        border-radius: 20px;
        overflow: hidden;
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
        transition: all 0.3s ease;
        cursor: pointer;
        
        &:hover {
          transform: scale(1.02);
          box-shadow: 0 12px 40px rgba(0, 0, 0, 0.3);
        }
        
        .image {
          width: 100%;
          height: auto;
          max-height: 400px;
          object-fit: cover;
          display: block;
          transition: transform 0.3s ease;
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
         min-height: 200px;
         background: #000;
         
         &:hover {
           transform: scale(1.02);
           box-shadow: 0 12px 40px rgba(0, 0, 0, 0.3);
         }
       }
      
             .video-player {
         width: 100%;
         height: auto;
         max-height: 600px;
         min-height: 200px;
         border-radius: 20px;
         overflow: hidden;
         background: #000;
         transition: all 0.3s ease;
         object-fit: contain;
         display: block;
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
  .back-button {
    top: 15px;
    left: 15px;
    
    .van-icon {
      font-size: 20px;
      padding: 8px;
    }
  }
  
  .content {
    padding: 15px;
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
  
  .media .images-section .images-container .image-wrapper .image {
    max-height: 300px;
  }
  
           .media .video-section .video-container .video-wrapper {
        min-height: 150px;
      }
      
      .media .video-section .video-container .video-player {
        max-height: 500px;
        min-height: 150px;
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