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
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: musicProgress + '%' }"></div>
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
        <!-- 图片轮播 -->
                 <van-swipe v-if="diary.images && diary.images.length > 0" class="swipe glow">
           <van-swipe-item v-for="(image, index) in diary.images" :key="index">
             <img 
               :src="image" 
               class="image" 
               @click="previewImage(index)"
             />
           </van-swipe-item>
         </van-swipe>
        
        <!-- 视频播放器 -->
        <div v-if="diary.videos && diary.videos.length > 0" class="video-section">
          <div class="video-header">
            <span class="video-emoji">🎬</span>
            <h3 class="video-title">美好视频</h3>
          </div>
          <div class="video-container">
            <video 
              v-for="(video, index) in diary.videos" 
              :key="index"
              :src="video"
              class="video-player"
              controls
              preload="metadata"
              poster=""
              @click="playVideo(index)"
              @ended="onVideoEnded"
              @play="onVideoPlay"
              @pause="onVideoPause"
            >
              您的浏览器不支持视频播放
            </video>
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
          <span class="btn-icon">💌</span>
          创建美好回忆
        </van-button>
        
        <van-button 
          type="default" 
          size="large" 
          @click="goBackToCalendar" 
          class="action-btn share-btn"
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
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast, showImagePreview } from 'vant'
import { getDiaryById } from '@/api/diary'
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
let audioElement = null
let progressTimer = null

const formatDate = (date) => {
  return dayjs(date).format('YYYY年MM月DD日')
}

const share = () => {
  router.push('/admin/diary/create')
}

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
  console.log('播放视频:', index)
  // 可以在这里添加视频播放逻辑
}

const onVideoEnded = () => {
  console.log('视频播放结束')
  showToast('视频播放完成')
}

const onVideoPlay = () => {
  console.log('视频开始播放')
}

const onVideoPause = () => {
  console.log('视频暂停')
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
    if (audioElement && !audioElement.paused) {
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

const loadDiary = async () => {
  const id = route.params.id
  try {
    const diaryData = await getDiaryById(id)
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
    console.error('加载日记失败:', error)
    showToast('加载日记失败，请稍后重试')
  }
}

onMounted(() => {
  loadDiary()
})

// 返回日历页面，保持之前的状态
const goBackToCalendar = () => {
  router.push('/calendar')
}

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
})
</script>

<style scoped>
.detail {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  position: relative;
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
          overflow: hidden;
          margin-bottom: 6px;
          
          .progress-fill {
            height: 100%;
            background: linear-gradient(90deg, #ff6b9d 0%, #ff8fab 100%);
            border-radius: 2px;
            transition: width 0.1s ease;
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
  
  .swipe {
    border-radius: 20px;
    overflow: hidden;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
    
         .image {
       width: 100%;
       height: 280px;
       object-fit: cover;
       cursor: pointer;
       transition: transform 0.3s ease;
       
       &:hover {
         transform: scale(1.02);
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
      
      .video-player {
        width: 100%;
        height: 280px;
        border-radius: 20px;
        overflow: hidden;
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
        background: #000;
        cursor: pointer;
        transition: all 0.3s ease;
        
        &:hover {
          transform: scale(1.02);
          box-shadow: 0 12px 40px rgba(0, 0, 0, 0.3);
        }
        
        &::-webkit-media-controls {
          background: rgba(0, 0, 0, 0.7);
        }
        
        &::-webkit-media-controls-panel {
          background: rgba(0, 0, 0, 0.7);
        }
        
        &::-webkit-media-controls-play-button {
          background: rgba(255, 255, 255, 0.2);
          border-radius: 50%;
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
  
  .media .swipe .image {
    height: 240px;
  }
  
  .media .video-section .video-container .video-player {
    height: 200px;
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