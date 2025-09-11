<template>
  <div class="movie-detail">
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
    
    <div class="content">
      <div v-if="loading" class="loading">加载中...</div>
      
      <div v-else-if="!movie" class="error">
        <p>电影不存在或已被删除</p>
        <button @click="$router.push('/')">返回首页</button>
      </div>
      
      <div v-else class="movie-content" ref="movieContentRef">
        <div class="movie-header">
          <div class="movie-cover">
            <img 
              v-if="movie.coverUrl" 
              :src="movie.coverUrl" 
              :alt="movie.title"
            />
            <div v-else class="cover-placeholder">🎬</div>
          </div>
          
          <div class="movie-info">
            <h1 class="movie-title">{{ movie.title }}</h1>
            <p class="movie-description">{{ movie.description || '暂无描述' }}</p>
            
            <div class="movie-meta">
              <div class="meta-item">
                <span class="meta-label">时长:</span>
                <span class="meta-value">{{ formatDuration(movie.durationSeconds) }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">文件大小:</span>
                <span class="meta-value">{{ formatFileSize(movie.fileSize) }}</span>
              </div>
              <div class="meta-item">
                <span class="meta-label">可见性:</span>
                <span class="meta-value" :class="movie.isPublic ? 'public' : 'private'">
                  {{ movie.isPublic ? '公开' : '私密' }}
                </span>
              </div>
            </div>
            
            <div class="movie-actions">
              <button class="action-btn primary" @click="createRoom">
                🎬 创建房间一起看
              </button>
              <button class="action-btn secondary" @click="joinRoom">
                🔗 加入房间
              </button>
            </div>
          </div>
        </div>
        
        <div class="movie-player">
          <div class="video-player-container" :style="getVideoStyle">
            <video 
              :src="movie.movieUrl"
              :poster="generateVideoPoster(movie.movieUrl, movie)"
              class="video-player"
              :style="getVideoStyle"
              controls
              preload="metadata"
            ></video>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import { getSharedMovie } from '@/api/share.js'
import { createRoom as createRoomApi, checkUserInMovieRoom } from '@/api/movieRoom.js'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const movie = ref(null)
const movieContentRef = ref(null)
const containerWidth = ref(400) // 默认宽度

// 倒计时相关
const countdown = ref(null)
const countdownTimer = ref(null)
const expiresAt = ref(null)

const loadMovie = async () => {
  loading.value = true
  try {
    const shareToken = route.params.shareToken
    const response = await getSharedMovie(shareToken)
    if (response.success) {
      movie.value = response.data
      
      // 从分享链接数据中获取过期时间
      if (movie.value.expiresAt) {
        expiresAt.value = new Date(movie.value.expiresAt)
        // 启动倒计时
        startCountdown()
      }
    } else {
      throw new Error(response.message || '获取分享电影失败')
    }
  } catch (error) {
    showToast(error.message)
    console.error('加载分享电影失败:', error)
  } finally {
    loading.value = false
  }
}

// 更新容器宽度
const updateContainerWidth = () => {
  if (movieContentRef.value) {
    containerWidth.value = movieContentRef.value.offsetWidth
  }
}

const createRoom = async () => {
  try {
    // 先检查用户是否已经在该电影的房间中
    try {
      const existingRoom = await checkUserInMovieRoom(movie.value.id)
      // 如果用户已经在房间中，拦截创建并提示
      showToast('您当前有房间正在观看这部电影，请先离开现有房间后再创建新房间')
      return
    } catch (checkError) {
      // 用户不在房间中，可以创建新房间
      console.log('用户不在房间中，可以创建新房间')
    }
    
    // 创建新房间
    const roomData = {
      roomName: `观看 ${movie.value.title}`,
      movieId: movie.value.id
    }
    
    const room = await createRoomApi(roomData)
    showToast('房间创建成功')
    router.push(`/movie-room/${room.roomCode}`)
  } catch (error) {
    showToast(error.message)
  }
}

const joinRoom = async () => {
  try {
    // 先检查用户是否已经在该电影的房间中
    try {
      const existingRoom = await checkUserInMovieRoom(movie.value.id)
      // 如果用户已经在房间中，直接进入该房间
      router.push(`/movie-room/${existingRoom.roomCode}`)
      showToast('已进入现有房间')
    } catch (checkError) {
      // 用户不在房间中，跳转到输入房间码页面
      router.push('/join-room')
    }
  } catch (error) {
    showToast(error.message || '操作失败')
  }
}

const formatDuration = (duration) => {
  if (!duration || duration <= 0) return '未知'
  
  // duration是秒数，直接使用
  const totalSeconds = Math.round(duration)
  const hours = Math.floor(totalSeconds / 3600)
  const minutes = Math.floor((totalSeconds % 3600) / 60)
  const seconds = totalSeconds % 60
  
  if (hours > 0) {
    return `${hours}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
  } else {
    return `${minutes}:${seconds.toString().padStart(2, '0')}`
  }
}

const formatFileSize = (bytes) => {
  if (!bytes) return '未知'
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 生成视频封面URL
const generateVideoPoster = (videoUrl, movie) => {
  if (!videoUrl) return ''
  
  // 判断是否为阿里云OSS URL
  if (videoUrl.includes('aliyuncs.com') || videoUrl.includes('oss-')) {
    // 根据视频原始尺寸计算封面尺寸
    let posterWidth = 800
    let posterHeight = 600
    
    if (movie && movie.width && movie.height) {
      const aspectRatio = movie.width / movie.height
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

// 获取视频自适应样式
const getVideoStyle = computed(() => {
  if (!movie.value || !movie.value.width || !movie.value.height) {
    return {
      width: '100%',
      height: '600px'
    }
  }
  
  // 根据视频原始宽高比计算高度，宽度占满
  const aspectRatio = movie.value.width / movie.value.height
  const height = containerWidth.value / aspectRatio
  
  return {
    width: '100%',
    height: `${height}px`,
    objectFit: 'cover' // 让视频内容完全占满容器
  }
})

// 计算倒计时
const calculateCountdown = () => {
  if (!expiresAt.value) return
  
  const now = new Date().getTime()
  const expireTime = new Date(expiresAt.value).getTime()
  const diff = expireTime - now
  
  if (diff <= 0) {
    // 倒计时结束，显示错误页面
    countdown.value = '00:00:00'
    clearInterval(countdownTimer.value)
    movie.value = null
    showToast('分享链接已过期')
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

// 监听movie变化，在DOM更新后更新容器宽度
watch(movie, () => {
  if (movie.value) {
    nextTick(() => {
      updateContainerWidth()
    })
  }
}, { immediate: true })

onMounted(() => {
  loadMovie()
})

// 组件卸载时移除监听器
import { onUnmounted } from 'vue'
onUnmounted(() => {
  // 清理倒计时定时器
  stopCountdown()
})
</script>

<style scoped>
.movie-detail {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

/* 倒计时样式 */
.countdown-section {
  margin-bottom: 20px;
}

.countdown-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  padding: 15px 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.countdown-icon {
  font-size: 24px;
  animation: pulse 2s ease-in-out infinite;
}

.countdown-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.countdown-label {
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  font-weight: 500;
}

.countdown-time {
  color: #ffffff;
  font-size: 20px;
  font-weight: bold;
  font-family: 'Courier New', monospace;
  letter-spacing: 2px;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
}

.content {
  max-width: 1200px;
  margin: 0 auto;
}

.loading, .error {
  text-align: center;
  color: white;
  padding: 100px 20px;
}

.error button {
  margin-top: 20px;
  padding: 10px 20px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.movie-content {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 30px;
  backdrop-filter: blur(10px);
}

.movie-header {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 30px;
  margin-bottom: 30px;
}

.movie-cover {
  width: 100%;
  height: 400px;
  border-radius: 15px;
  overflow: hidden;
}

.movie-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(45deg, #667eea, #764ba2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 60px;
}

.movie-info {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.movie-title {
  font-size: 2.5rem;
  color: #333;
  margin-bottom: 15px;
}

.movie-description {
  font-size: 16px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 20px;
}

.movie-meta {
  margin-bottom: 30px;
}

.meta-item {
  display: flex;
  margin-bottom: 10px;
}

.meta-label {
  font-weight: 500;
  color: #333;
  width: 80px;
}

.meta-value {
  color: #666;
}

.meta-value.public {
  color: #4caf50;
}

.meta-value.private {
  color: #ff9800;
}

.movie-actions {
  display: flex;
  gap: 15px;
}

.action-btn {
  padding: 12px 24px;
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.action-btn.primary {
  background: #667eea;
  color: white;
}

.action-btn.primary:hover {
  background: #5a6fd8;
  transform: translateY(-2px);
}

.action-btn.secondary {
  background: #f5f5f5;
  color: #666;
}

.action-btn.secondary:hover {
  background: #eee;
  transform: translateY(-2px);
}

.movie-player {
  margin-top: 30px;
}

.video-player-container {
  width: 100%;
  position: relative;
  border-radius: 15px;
  overflow: hidden;
  background: #000;
}

.video-player {
  border-radius: 15px;
  background: #000;
  display: block;
  width: 100%;
  height: auto;
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

@media (max-width: 768px) {
  .movie-header {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .movie-cover {
    height: 250px;
  }
  
  .movie-title {
    font-size: 1.8rem;
  }
  
  .movie-actions {
    flex-direction: column;
  }
  
  .action-btn {
    width: 100%;
  }
  
  .video-player-container {
    border-radius: 10px;
  }
  
  .video-player {
    border-radius: 10px;
  }
}
</style>