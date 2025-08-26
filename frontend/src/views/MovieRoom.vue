<template>
  <div class="movie-room">
    <BackButton />
    
    <div class="content">
      <div v-if="loading" class="loading">加载中...</div>
      
      <div v-else-if="!room" class="error">
        <p>房间不存在或已被删除</p>
        <button @click="$router.push('/movies')">返回电影列表</button>
      </div>
      
      <div v-else class="room-content">
        <!-- 房间信息 -->
        <div class="room-header">
          <h1 class="room-title">{{ room.roomName }}</h1>
          <div class="room-code">
            房间码: <span class="code">{{ room.roomCode }}</span>
            <button class="copy-btn" @click="copyRoomCode">复制</button>
          </div>
        </div>

        <!-- 电影播放器 -->
        <div class="video-section">
          <video 
            ref="videoPlayer"
            :src="room.movie.movieUrl"
            class="video-player"
            @loadedmetadata="onVideoLoaded"
            @timeupdate="onTimeUpdate"
            @play="onPlay"
            @pause="onPause"
            @seeking="onSeeking"
            @seeked="onSeeked"
          ></video>
          
          <!-- 播放控制 -->
          <div class="video-controls">
            <div class="progress-bar" @click="seekTo">
              <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
              <div class="progress-handle" :style="{ left: progressPercent + '%' }"></div>
            </div>
            
            <div class="control-buttons">
              <button class="control-btn" @click="togglePlay">
                {{ isPlaying ? '⏸️' : '▶️' }}
              </button>
              <span class="time-display">{{ formatTime(currentTime) }} / {{ formatTime(duration) }}</span>
              <button class="control-btn" @click="leaveRoom">离开房间</button>
            </div>
          </div>
        </div>

        <!-- 房间成员 -->
        <div class="members-section">
          <h3>房间成员 ({{ members.length }})</h3>
          <div class="members-list">
            <div 
              v-for="member in members" 
              :key="member.id"
              class="member-item"
            >
              <div class="member-avatar">
                {{ member.user.displayName?.charAt(0) || 'U' }}
              </div>
              <div class="member-info">
                <span class="member-name">{{ member.user.displayName || '用户' }}</span>
                <span class="member-status" :class="{ online: member.isOnline }">
                  {{ member.isOnline ? '在线' : '离线' }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast } from 'vant'
import BackButton from '@/components/BackButton.vue'
import { getRoom, getRoomMembers, leaveRoom as leaveRoomApi, updatePlayback, getPlaybackStatus } from '@/api/movieRoom.js'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const room = ref(null)
const members = ref([])
const videoPlayer = ref(null)
const currentTime = ref(0)
const duration = ref(0)
const isPlaying = ref(false)
const isSeeking = ref(false)
const syncInterval = ref(null)
const playbackInterval = ref(null)

const progressPercent = computed(() => {
  if (duration.value === 0) return 0
  return (currentTime.value / duration.value) * 100
})

const roomCode = computed(() => route.params.roomCode)

const loadRoom = async () => {
  loading.value = true
  try {
    const roomData = await getRoom(roomCode.value)
    room.value = roomData
    
    // 加载播放状态
    const playbackData = await getPlaybackStatus(roomCode.value)
    currentTime.value = playbackData.currentTime || 0
    isPlaying.value = playbackData.isPlaying || false
    
    // 加载成员列表
    loadMembers()
    
    // 开始同步
    startSync()
  } catch (error) {
    showToast(error.message)
  } finally {
    loading.value = false
  }
}

const loadMembers = async () => {
  try {
    const membersData = await getRoomMembers(roomCode.value)
    members.value = membersData
  } catch (error) {
    console.error('加载成员失败:', error)
  }
}

const startSync = () => {
  // 定期同步播放状态
  syncInterval.value = setInterval(async () => {
    try {
      const playbackData = await getPlaybackStatus(roomCode.value)
      
      // 如果本地没有在拖拽进度条，则同步远程状态
      if (!isSeeking.value) {
        const timeDiff = Math.abs(currentTime.value - playbackData.currentTime)
        if (timeDiff > 2) { // 如果时间差大于2秒，则同步
          currentTime.value = playbackData.currentTime
          if (videoPlayer.value) {
            videoPlayer.value.currentTime = playbackData.currentTime
          }
        }
        isPlaying.value = playbackData.isPlaying
      }
      
      // 重新加载成员列表
      loadMembers()
    } catch (error) {
      console.error('同步失败:', error)
    }
  }, 2000) // 每2秒同步一次
}

const onVideoLoaded = () => {
  if (videoPlayer.value) {
    duration.value = videoPlayer.value.duration
    videoPlayer.value.currentTime = currentTime.value
  }
}

const onTimeUpdate = () => {
  if (videoPlayer.value && !isSeeking.value) {
    currentTime.value = videoPlayer.value.currentTime
  }
}

const onPlay = () => {
  isPlaying.value = true
  updateRemotePlayback()
}

const onPause = () => {
  isPlaying.value = false
  updateRemotePlayback()
}

const onSeeking = () => {
  isSeeking.value = true
}

const onSeeked = () => {
  isSeeking.value = false
  if (videoPlayer.value) {
    currentTime.value = videoPlayer.value.currentTime
    updateRemotePlayback()
  }
}

const togglePlay = () => {
  if (videoPlayer.value) {
    if (isPlaying.value) {
      videoPlayer.value.pause()
    } else {
      videoPlayer.value.play()
    }
  }
}

const seekTo = (event) => {
  if (!videoPlayer.value) return
  
  const rect = event.currentTarget.getBoundingClientRect()
  const clickX = event.clientX - rect.left
  const percent = clickX / rect.width
  const newTime = percent * duration.value
  
  videoPlayer.value.currentTime = newTime
  currentTime.value = newTime
  updateRemotePlayback()
}

const updateRemotePlayback = async () => {
  try {
    await updatePlayback(roomCode.value, {
      currentTime: currentTime.value,
      isPlaying: isPlaying.value
    })
  } catch (error) {
    console.error('更新播放状态失败:', error)
  }
}

const leaveRoom = async () => {
  try {
    await leaveRoomApi(roomCode.value)
    showToast('已离开房间')
    router.push('/movies')
  } catch (error) {
    showToast(error.message)
  }
}

const copyRoomCode = async () => {
  try {
    await navigator.clipboard.writeText(roomCode.value)
    showToast('房间码已复制')
  } catch (error) {
    showToast('复制失败')
  }
}

const formatTime = (seconds) => {
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

onMounted(() => {
  loadRoom()
})

onUnmounted(() => {
  if (syncInterval.value) {
    clearInterval(syncInterval.value)
  }
  if (playbackInterval.value) {
    clearInterval(playbackInterval.value)
  }
})
</script>

<style scoped>
.movie-room {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.content {
  max-width: 1200px;
  margin: 0 auto;
  padding-top: 60px;
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

.room-content {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 30px;
  backdrop-filter: blur(10px);
}

.room-header {
  text-align: center;
  margin-bottom: 30px;
}

.room-title {
  font-size: 2rem;
  color: #333;
  margin-bottom: 10px;
}

.room-code {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  font-size: 16px;
  color: #666;
}

.code {
  background: #f0f0f0;
  padding: 5px 10px;
  border-radius: 5px;
  font-family: monospace;
  font-weight: bold;
}

.copy-btn {
  padding: 5px 10px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 12px;
}

.video-section {
  margin-bottom: 30px;
}

.video-player {
  width: 100%;
  max-height: 500px;
  border-radius: 10px;
  background: #000;
}

.video-controls {
  margin-top: 15px;
}

.progress-bar {
  position: relative;
  height: 8px;
  background: #ddd;
  border-radius: 4px;
  cursor: pointer;
  margin-bottom: 15px;
}

.progress-fill {
  height: 100%;
  background: #667eea;
  border-radius: 4px;
  transition: width 0.1s ease;
}

.progress-handle {
  position: absolute;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 16px;
  height: 16px;
  background: #667eea;
  border-radius: 50%;
  border: 2px solid white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.control-buttons {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 15px;
}

.control-btn {
  padding: 10px 15px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  transition: background 0.3s;
}

.control-btn:hover {
  background: #5a6fd8;
}

.time-display {
  font-family: monospace;
  font-size: 14px;
  color: #666;
}

.members-section {
  border-top: 1px solid #eee;
  padding-top: 20px;
}

.members-section h3 {
  color: #333;
  margin-bottom: 15px;
}

.members-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 15px;
}

.member-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 10px;
}

.member-avatar {
  width: 40px;
  height: 40px;
  background: #667eea;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 16px;
}

.member-info {
  flex: 1;
}

.member-name {
  display: block;
  font-weight: 500;
  color: #333;
  margin-bottom: 2px;
}

.member-status {
  font-size: 12px;
  color: #999;
}

.member-status.online {
  color: #4caf50;
}

@media (max-width: 768px) {
  .room-content {
    padding: 20px;
  }
  
  .room-title {
    font-size: 1.5rem;
  }
  
  .control-buttons {
    flex-direction: column;
    gap: 10px;
  }
  
  .members-list {
    grid-template-columns: 1fr;
  }
}
</style>

