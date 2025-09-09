<template>
  <div class="letter-detail-page">
    <!-- 倒计时显示 -->
    <div class="countdown-section" v-if="countdown">
      <div class="countdown-card">
        <div class="countdown-icon">⏰</div>
        <div class="countdown-info">
          <span class="countdown-label">分享链接剩余时间</span>
          <span class="countdown-time">{{ countdown }}</span>
        </div>
      </div>
    </div>
    
    <div class="letter-paper" v-if="letter">
      <div class="paper-border">
        <div class="paper-content">
          <div class="letter-header">
            <div class="letter-title">
              <h1>{{ letter.title }}</h1>
            </div>
            
            <div class="letter-address">
              <div class="to-section">
                <span class="label">To:</span>
                <span class="name">{{ letter.receiverName || '亲爱的' }}</span>
              </div>
            </div>
          </div>

          <div class="letter-body">
            <div 
              class="content" 
              :class="{ 'typing-complete': typingComplete }"
              @click="showFullText"
            >
              {{ displayText }}
            </div>
          </div>

          <div class="letter-footer">
            <div class="signature">
              <span v-if="letter.senderName" class="author">From: {{ letter.senderName }}</span>
              <span class="signature-date">{{ formatDate(letter.createdAt) }}</span>
            </div>
          </div>
        </div>
      </div>

             <!-- 解锁时间信息 -->
       <!-- <div class="unlock-info" v-if="letter.unlockTime && !letter.isRead">
         <div class="unlock-content">
           <van-icon name="clock-o" />
           <span>解锁时间：{{ formatCountdown(letter.unlockTime) }}</span>
         </div>
       </div> -->

      <!-- 分享页面不需要操作按钮 -->
    </div>

    <!-- 全局悬浮音乐播放器 -->
    <div class="global-floating-music-player" v-if="letterBackgroundMusic?.url">
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
          <span class="music-title">{{ getShortFileName(letterBackgroundMusic.fileName) || '背景音乐' }}</span>
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
  

    <div v-else-if="loading" class="loading-state">
      <van-loading type="spinner" size="24px">加载中...</van-loading>
    </div>
    
    <div v-else-if="error" class="error-container">
      <div class="error-content">
        <div class="error-icon">⚠️</div>
        <h2 class="error-title">分享链接已过期或不存在</h2>
        <p class="error-message">很抱歉，您访问的分享链接已经失效，或者该内容已被删除。</p>
        <van-button 
          type="primary" 
          size="large" 
          @click="goToHome" 
          class="home-btn"
        >
          <span class="btn-icon">🏠</span>
          返回首页
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getSharedLetter } from '@/api/share'
import { getShareExpireMinutes } from '@/api/systemConfig'
import { showToast } from 'vant'
import { getLetterBackgroundMusicByUserIdPublic } from '@/api/music'
import { getBackgroundMusicAutoplayByUserId } from '@/api/systemConfig'

const route = useRoute()
const router = useRouter()

const letter = ref(null)
const displayText = ref('')
const typingComplete = ref(false)
const loading = ref(true)
const error = ref(false)
let typingTimer = null

// 倒计时相关
const countdown = ref(null)
const countdownTimer = ref(null)
const expiresAt = ref(null)

// 背景音乐
const letterBackgroundMusic = ref(null)
const musicAutoplay = ref(false) // 默认不自动播放，只有发送者开启才自动播放

// 音乐播放器相关
const audioElement = ref(null)
const isMusicPlaying = ref(false)
const showMusicControls = ref(false)
const currentTime = ref(0)
const duration = ref(0)
const musicProgress = ref(0)
const progressBar = ref(null)
const progressTimer = ref(null)
const isDragging = ref(false)

const fetchLetterDetail = async () => {
  try {
    loading.value = true
    error.value = false
    
    const shareToken = route.params.shareToken
    if (!shareToken) {
      error.value = true
      return
    }
    
    const letterData = await getSharedLetter(shareToken)
    letter.value = letterData
    
    // 从分享链接数据中获取过期时间
    if (letterData.expiresAt) {
      expiresAt.value = new Date(letterData.expiresAt)
    } else {
      // 如果没有过期时间，使用系统配置的默认时间
      const expireMinutes = await getShareExpireMinutes()
      expiresAt.value = new Date(Date.now() + expireMinutes * 60 * 1000)
    }
    
    // 启动倒计时
    startCountdown()
    
    // 启动打字机效果
    if (letter.value && letter.value.content) {
      startTyping(letter.value.content)
    }

    // 加载看信背景音乐配置（根据发送者ID获取）
    if (letter.value && letter.value.senderId) {
      try {
        // 获取发送者的背景音乐自动播放配置
        const autoplayConfig = await getBackgroundMusicAutoplayByUserId(letter.value.senderId)
        musicAutoplay.value = autoplayConfig
        
        // 获取背景音乐URL
        const musicUrl = await getLetterBackgroundMusicByUserIdPublic(letter.value.senderId)
        if (musicUrl) {
          letterBackgroundMusic.value = {
            url: musicUrl,
            fileName: musicUrl.split('/').pop()
          }
          // 初始化音乐播放器
          nextTick(() => {
            initAudio()
          })
        }
      } catch (error) {
        console.warn('获取发送者音乐配置失败:', error)
        // 使用默认配置：不自动播放
        musicAutoplay.value = false
      }
    }
  } catch (err) {
    console.error('获取分享信件失败:', err)
    error.value = true
  } finally {
    loading.value = false
  }
}

// 跳转到系统欢迎页
const goToHome = () => {
  router.push('/')
}

// 分享页面不需要标记已读功能

const goBack = () => {
  router.back()
}

const formatDate = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const formatCountdown = (unlockTime) => {
  if (!unlockTime) return ''
  const now = new Date()
  const unlock = new Date(unlockTime)
  const diff = unlock - now
  
  if (diff <= 0) {
    return '已解锁'
  }
  
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
  const seconds = Math.floor((diff % (1000 * 60)) / 1000)
  
  if (days > 0) {
    return `${days}天${hours}小时${minutes}分${seconds}秒`
  } else if (hours > 0) {
    return `${hours}小时${minutes}分${seconds}秒`
  } else if (minutes > 0) {
    return `${minutes}分${seconds}秒`
  } else {
    return `${seconds}秒`
  }
}

// 计算倒计时
const calculateCountdown = () => {
  if (!expiresAt.value) return
  
  const now = new Date().getTime()
  const expireTime = new Date(expiresAt.value).getTime()
  const diff = expireTime - now
  
  if (diff <= 0) {
    // 倒计时结束，直接显示错误页面
    countdown.value = '00:00:00'
    clearInterval(countdownTimer.value)
    error.value = true
    letter.value = null
    return
  }
  
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
  const seconds = Math.floor((diff % (1000 * 60)) / 1000)
  
  countdown.value = `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
}

// 启动倒计时
const startCountdown = () => {
    if (countdownTimer.value) {
      clearInterval(countdownTimer.value)
    }
  calculateCountdown() // 立即计算一次
  countdownTimer.value = setInterval(calculateCountdown, 1000)
}

// 停止倒计时
const stopCountdown = () => {
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value)
    countdownTimer.value = null
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
  if (letter.value && letter.value.content) {
    displayText.value = letter.value.content
    typingComplete.value = true
    if (typingTimer) {
      clearTimeout(typingTimer)
    }
    
    // 点击信件内容时播放音乐（根据配置决定）
    if (letterBackgroundMusic.value?.url && audioElement.value && musicAutoplay.value) {
      if (audioElement.value.paused) {
        audioElement.value.play().catch(error => {
          console.warn('播放音乐失败:', error)
        })
      }
    }
  }
}

// 音乐播放器相关方法
const toggleMusicControls = () => {
  showMusicControls.value = !showMusicControls.value
}

const toggleMusic = () => {
  if (!audioElement.value) return
  
  if (isMusicPlaying.value) {
    audioElement.value.pause()
  } else {
    audioElement.value.play()
  }
}

const stopMusic = () => {
  if (!audioElement.value) return
  audioElement.value.pause()
  audioElement.value.currentTime = 0
  isMusicPlaying.value = false
}

const initAudio = () => {
  if (!letterBackgroundMusic.value?.url) return
  
  audioElement.value = new Audio(letterBackgroundMusic.value.url)
  audioElement.value.loop = true
  
  audioElement.value.addEventListener('loadedmetadata', () => {
    duration.value = audioElement.value.duration
    // 根据配置决定是否自动播放音乐
    if (musicAutoplay.value) {
      audioElement.value.play().catch(error => {
        console.warn('自动播放失败:', error)
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
  
  audioElement.value.addEventListener('error', () => {
    showToast('音乐加载失败')
  })
}

const startProgressTimer = () => {
  if (progressTimer.value) {
    clearInterval(progressTimer.value)
  }
  
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

const formatTime = (time) => {
  if (!time || isNaN(time)) return '0:00'
  const minutes = Math.floor(time / 60)
  const seconds = Math.floor(time % 60)
  return `${minutes}:${seconds.toString().padStart(2, '0')}`
}

const seekMusic = (event) => {
  if (!audioElement.value || !progressBar.value) return
  
  const rect = progressBar.value.getBoundingClientRect()
  const clickX = event.clientX - rect.left
  const progressBarWidth = rect.width
  const percentage = (clickX / progressBarWidth) * 100
  
  const clampedPercentage = Math.max(0, Math.min(100, percentage))
  const newTime = (clampedPercentage / 100) * audioElement.value.duration
  
  audioElement.value.currentTime = newTime
  currentTime.value = newTime
  musicProgress.value = clampedPercentage
}

const startDrag = (event) => {
  if (!audioElement.value) return
  isDragging.value = true
  document.addEventListener('mousemove', onDrag)
  document.addEventListener('mouseup', stopDrag)
  document.addEventListener('touchmove', onDrag)
  document.addEventListener('touchend', stopDrag)
}

const onDrag = (event) => {
  if (!isDragging.value || !audioElement.value || !progressBar.value) return
  
  event.preventDefault()
  const rect = progressBar.value.getBoundingClientRect()
  const clientX = event.clientX || (event.touches && event.touches[0].clientX)
  const clickX = clientX - rect.left
  const progressBarWidth = rect.width
  const percentage = (clickX / progressBarWidth) * 100
  
  const clampedPercentage = Math.max(0, Math.min(100, percentage))
  const newTime = (clampedPercentage / 100) * audioElement.value.duration
  
  audioElement.value.currentTime = newTime
  currentTime.value = newTime
  musicProgress.value = clampedPercentage
}

const stopDrag = () => {
  isDragging.value = false
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', stopDrag)
  document.removeEventListener('touchmove', onDrag)
  document.removeEventListener('touchend', stopDrag)
}

const getShortFileName = (fileName) => {
  if (!fileName) return ''
  if (fileName.length <= 15) return fileName
  return '...' + fileName.slice(-15)
}

onMounted(() => {
  fetchLetterDetail()
})

onUnmounted(() => {
  if (typingTimer) {
    clearTimeout(typingTimer) // 清理打字机定时器
  }
  
  // 清理倒计时定时器
  stopCountdown()
  
  // 清理音乐播放器
  if (audioElement.value) {
    audioElement.value.pause()
    audioElement.value = null
  }
  stopProgressTimer()
})
</script>

<style lang="scss" scoped>
.letter-detail-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #8B4513 0%, #A0522D 50%, #CD853F 100%);
  padding: 20px;
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-image: 
      radial-gradient(circle at 25% 25%, rgba(255, 255, 255, 0.1) 2px, transparent 2px),
      radial-gradient(circle at 75% 75%, rgba(139, 69, 19, 0.1) 2px, transparent 2px);
    background-size: 100px 100px, 150px 150px;
    opacity: 0.3;
    pointer-events: none;
  }
}

/* 倒计时样式 */
.countdown-section {
  margin-bottom: 20px;
  
  .countdown-card {
    background: rgba(245, 222, 179, 0.1);
    backdrop-filter: blur(10px);
    border-radius: 15px;
    padding: 15px 20px;
    display: flex;
    align-items: center;
    gap: 15px;
    border: 1px solid rgba(245, 222, 179, 0.2);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    
    .countdown-icon {
      font-size: 24px;
      animation: pulse 2s ease-in-out infinite;
    }
    
    .countdown-info {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 5px;
      
      .countdown-label {
        color: rgba(245, 222, 179, 0.8);
        font-size: 14px;
        font-weight: 500;
        font-family: 'Times New Roman', serif;
      }
      
      .countdown-time {
        color: #F5DEB3;
        font-size: 20px;
        font-weight: bold;
        font-family: 'Courier New', monospace;
        letter-spacing: 2px;
        text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
      }
    }
  }
}

.letter-paper {
  max-width: 800px;
  margin: 0 auto;
  padding: 60px 0 20px 0;
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

.paper-border {
  background: linear-gradient(135deg, #F5DEB3 0%, #DEB887 50%, #D2B48C 100%);
  border: 3px solid #8B4513;
  border-radius: 12px;
  box-shadow: 
    0 8px 32px rgba(139, 69, 19, 0.3),
    0 16px 64px rgba(139, 69, 19, 0.2),
    inset 0 2px 4px rgba(255, 255, 255, 0.3);
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-image: 
      radial-gradient(circle at 20% 20%, rgba(255, 255, 255, 0.1) 1px, transparent 1px),
      radial-gradient(circle at 80% 80%, rgba(139, 69, 19, 0.05) 1px, transparent 1px);
    background-size: 40px 40px, 60px 60px;
    opacity: 0.6;
    pointer-events: none;
  }
  
  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(45deg, transparent 49%, rgba(139, 69, 19, 0.03) 50%, transparent 51%);
    background-size: 20px 20px;
    pointer-events: none;
  }
  
  // 添加羊皮卷的纹理效果
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-image: 
      linear-gradient(90deg, transparent 98%, rgba(139, 69, 19, 0.1) 99%, transparent 100%),
      linear-gradient(0deg, transparent 98%, rgba(139, 69, 19, 0.1) 99%, transparent 100%);
    background-size: 20px 20px;
    opacity: 0.4;
    pointer-events: none;
  }
}

.paper-content {
  position: relative;
  z-index: 1;
  padding: 50px 40px;
  min-height: 600px;
  background: linear-gradient(135deg, #F5DEB3 0%, #DEB887 100%);
}

.letter-header {
  margin-bottom: 40px;
}

.letter-title {
  text-align: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  
  h1 {
    font-family: 'Times New Roman', serif;
    font-size: 32px;
    font-weight: bold;
    color: #8B4513;
    margin: 0;
    text-shadow: 2px 2px 4px rgba(139, 69, 19, 0.3);
    letter-spacing: 3px;
  }
}

.letter-address {
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
  margin-bottom: 10px;
  font-family: 'Times New Roman', serif;
  
  .to-section {
    display: flex;
    align-items: center;
    gap: 8px;
    
    .label {
      font-size: 18px;
      font-weight: bold;
      color: #8B4513;
      text-transform: uppercase;
      letter-spacing: 1px;
    }
    
    .name {
      font-size: 20px;
      font-weight: 600;
      color: #654321;
      font-style: italic;
    }
  }
}

.letter-body {
  margin-bottom: 50px;
  
  .content {
    font-family: 'Times New Roman', serif;
    font-size: 18px;
    line-height: 2.4;
    color: #654321;
    text-align: justify;
    letter-spacing: 1px;
    white-space: pre-wrap;
    word-wrap: break-word;
    cursor: pointer;
    position: relative;
    transition: all 0.3s ease;
    
    &:hover {
      // background: rgba(139, 69, 19, 0.05);
      // border-radius: 8px;
      // padding: 8px;
      // margin: -8px;
    }
    
    &.typing-complete {
      cursor: default;
    }
    
    :deep(p) {
      margin-bottom: 1.8em;
      // text-indent: 2em;
    }
    
    :deep(h1, h2, h3, h4, h5, h6) {
      font-family: 'Times New Roman', serif;
      font-weight: bold;
      color: #8B4513;
      margin: 2em 0 1em 0;
      text-indent: 0;
      text-align: center;
    }
    
    :deep(strong, b) {
      font-weight: bold;
      color: #8B4513;
    }
    
    :deep(em, i) {
      font-style: italic;
      color: #A0522D;
    }
    
    :deep(u) {
      text-decoration: underline;
      text-decoration-color: #8B4513;
      text-decoration-thickness: 2px;
    }
  }
}

.letter-footer {
  text-align: right;
  margin-top: 20px;
  padding-top: 10px;
  position: relative;
  
  .signature {
    font-family: 'Brush Script MT', cursive;
    font-size: 20px;
    color: #8B4513;
    line-height: 1.8;
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    position: absolute;
    bottom: 0;
    right: 0;
    
    .author {
      font-family: 'Times New Roman', serif;
      font-size: 18px;
      font-weight: 600;
      color: #8B4513;
      margin-bottom: 2px;
    }
    
    .signature-date {
      font-family: 'Times New Roman', serif;
      font-size: 16px;
      color: #A0522D;
      font-style: italic;
    }
  }
}

.unlock-info {
  background: linear-gradient(135deg, rgba(139, 69, 19, 0.1), rgba(160, 82, 45, 0.1));
  border: 2px solid #8B4513;
  border-radius: 12px;
  padding: 20px;
  margin: 25px 0;
  text-align: center;
  font-family: 'Times New Roman', serif;
  color: #ffffff;
  font-size: 14px;
  box-shadow: 0 4px 12px rgba(139, 69, 19, 0.2);
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(45deg, transparent 49%, rgba(139, 69, 19, 0.05) 50%, transparent 51%);
    background-size: 10px 10px;
    border-radius: 10px;
    pointer-events: none;
  }
  
  .unlock-content {
    position: relative;
    z-index: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
  }
  
  .van-icon {
    font-size: 18px;
    color: #ffffff;
  }
}

.letter-actions {
  text-align: center;
  margin-top: 30px;
  padding: 20px;
  
  .van-button {
    min-width: 140px;
    height: 48px;
    font-size: 16px;
    font-weight: 500;
    font-family: 'Times New Roman', serif;
    background: linear-gradient(135deg, #8B4513, #A0522D);
    border: 2px solid #654321;
    border-radius: 24px;
    box-shadow: 
      0 6px 20px rgba(139, 69, 19, 0.4),
      inset 0 2px 4px rgba(255, 255, 255, 0.2);
    color: #F5DEB3;
    text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
    transition: all 0.3s ease;
    
    &:active {
      transform: translateY(2px);
      box-shadow: 
        0 4px 12px rgba(139, 69, 19, 0.4),
        inset 0 1px 2px rgba(255, 255, 255, 0.1);
    }
    
    &:hover {
      background: linear-gradient(135deg, #A0522D, #CD853F);
      box-shadow: 
        0 8px 25px rgba(139, 69, 19, 0.5),
        inset 0 2px 4px rgba(255, 255, 255, 0.2);
    }
  }
}

.loading-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  color: #F5DEB3;
  font-size: 16px;
  font-family: 'Times New Roman', serif;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
  
  .van-loading {
    color: #F5DEB3;
  }
}

.error-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  padding: 20px;
  position: relative;
  z-index: 10;
  
  .error-content {
    text-align: center;
    max-width: 400px;
    position: relative;
    z-index: 11;
    
    .error-icon {
      font-size: 64px;
      margin-bottom: 20px;
      animation: pulse 2s ease-in-out infinite;
    }
    
    .error-title {
      color: #F5DEB3;
      font-size: 24px;
      font-weight: bold;
      margin-bottom: 15px;
      font-family: 'Times New Roman', serif;
      text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
    }
    
    .error-message {
      color: rgba(245, 222, 179, 0.8);
      font-size: 16px;
      line-height: 1.6;
      margin-bottom: 30px;
      font-family: 'Times New Roman', serif;
      text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
    }
    
    .home-btn {
      height: 48px;
      border-radius: 24px;
      font-size: 16px;
      font-weight: 500;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      background: linear-gradient(135deg, #8B4513, #A0522D);
      border: 2px solid #654321;
      box-shadow: 
        0 6px 20px rgba(139, 69, 19, 0.4),
        inset 0 2px 4px rgba(255, 255, 255, 0.2);
      color: #F5DEB3;
      text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
      font-family: 'Times New Roman', serif;
      position: relative;
      z-index: 12;
      cursor: pointer;
      
      .btn-icon {
        font-size: 18px;
      }
      
      &:hover {
        background: linear-gradient(135deg, #A0522D, #CD853F);
        box-shadow: 
          0 8px 25px rgba(139, 69, 19, 0.5),
          inset 0 2px 4px rgba(255, 255, 255, 0.2);
        transform: translateY(-2px);
      }
    }
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
  
  .letter-detail-page {
    padding: 10px;
  }
  
  .paper-content {
    padding: 30px 20px;
    min-height: 500px;
  }
  
  .letter-title h1 {
    font-size: 22px;
  }
  
  .letter-body .content {
    font-family: 'Times New Roman', serif;
    font-size: 16px;
    line-height: 2.2;
    letter-spacing: 1px;
    color: #654321;
  }
  
  .letter-address {
    .to-section {
      .label {
        font-size: 16px;
      }
      
      .name {
        font-size: 18px;
      }
    }
  }
  
  // 移动端音乐播放器样式
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

/* 动画关键帧 */
@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
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
    user-select: none;
    
    &:hover {
      transform: scale(1.1);
      box-shadow: 0 6px 25px rgba(255, 107, 157, 0.4);
      background: linear-gradient(135deg, rgba(255, 143, 171, 0.8) 0%, rgba(255, 107, 157, 0.8) 100%);
    }
    
    &.playing {
      animation: rotate 3s linear infinite;
    }
    
    &.show-controls {
      transform: scale(1.05);
      box-shadow: 0 6px 25px rgba(255, 107, 157, 0.5);
    }
    
    .music-emoji {
      font-size: 20px;
      color: white;
      user-select: none;
      pointer-events: none;
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
    transform-origin: top right;
    transition: all 0.3s ease;
    
    &:hover {
      box-shadow: 0 12px 40px rgba(0, 0, 0, 0.25);
      border-color: rgba(255, 255, 255, 0.4);
    }
    
    .music-info {
      margin-bottom: 12px;
      
      .music-title {
        display: block;
        font-size: 14px;
        font-weight: 600;
        color: #333;
        margin-bottom: 8px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        user-select: none;
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
          transition: all 0.2s ease;
          
          &:hover {
            .progress-fill {
              background: linear-gradient(90deg, #ff8fab 0%, #ff6b9d 100%);
            }
            
            .progress-handle {
              transform: translate(-50%, -50%) scale(1.1);
            }
          }
          
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
            user-select: none;
            
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
          text-align: center;
          user-select: none;
        }
      }
    }
    
    .music-buttons {
      display: flex;
      gap: 8px;
      justify-content: center;
      margin-top: 8px;
      
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
        user-select: none;
        
        &:hover {
          transform: scale(1.1);
          box-shadow: 0 4px 12px rgba(255, 107, 157, 0.4);
          background: linear-gradient(135deg, #ff8fab 0%, #ff6b9d 100%);
        }
        
        &:active {
          transform: scale(0.95);
          box-shadow: 0 2px 8px rgba(255, 107, 157, 0.3);
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

@keyframes heartbeat {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}
</style>
