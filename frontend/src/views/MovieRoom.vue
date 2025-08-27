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
            @error="onVideoError"
            @canplay="onVideoCanPlay"
          ></video>
          
          <!-- 播放控制 -->
          <div class="video-controls">
            <div class="progress-bar" 
                 @click="seekTo"
                 @mousedown="onProgressMouseDown"
                 @mousemove="onProgressMouseMove"
                 @mouseup="onProgressMouseUp"
                 @mouseleave="onProgressMouseLeave"
                 title="点击或拖拽调整播放进度">
              <div class="progress-fill" :style="{ width: progressPercent + '%' }"></div>
              <div class="progress-handle" :style="{ left: progressPercent + '%' }"></div>
            </div>
            
                         <div class="control-buttons">
               <button class="control-btn" 
                       @click="togglePlay"
                       title="播放/暂停">
                 {{ isPlaying ? '⏸️' : '▶️' }}
               </button>
               <span class="time-display">{{ formatTime(currentTime) }} / {{ formatTime(duration) }}</span>
               <button class="control-btn sync-progress-btn" @click="syncVideoProgress" title="同步视频进度">
                 🔄 同步进度
               </button>
               <button class="control-btn" @click="leaveRoom">离开房间</button>
             </div>
             <div class="sync-tip">
               💡 提示：拖拽进度条或点击同步按钮可同步进度给其他用户，其他用户需要手动点击同步按钮接收更新
             </div>
          </div>
        </div>

        <!-- 房间成员 -->
        <div class="members-section">
          <div class="members-header">
            <h3>房间成员 ({{ members.length }})</h3>
            <button class="sync-btn" @click="manualSyncMembers" :disabled="syncing">
              {{ syncing ? '同步中...' : '🔄 同步' }}
            </button>
          </div>
          <div class="members-list">
            <div 
              v-for="member in members" 
              :key="member.id"
              class="member-item"
            >
              <div class="member-avatar">
                {{ member.user?.displayName?.charAt(0) || member.displayName?.charAt(0) || 'U' }}
              </div>
              <div class="member-info">
                <span class="member-name">{{ member.user?.displayName || member.displayName || '用户' }}</span>
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
import { getUserInfo } from '@/api/auth.js'

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
const isDragging = ref(false)
const membersSyncInterval = ref(null)
const syncing = ref(false)
const currentUser = ref(null)
const lastSyncTime = ref(0) // 记录最后同步时间，避免自己同步自己
const isVideoPlaying = ref(false) // 跟踪视频是否正在播放

const progressPercent = computed(() => {
  if (duration.value === 0) return 0
  return (currentTime.value / duration.value) * 100
})

const roomCode = computed(() => route.params.roomCode)

const loadRoom = async () => {
  loading.value = true
  try {
    // 获取当前用户信息
    const userData = await getUserInfo()
    currentUser.value = userData.data
    
    const roomData = await getRoom(roomCode.value)
    room.value = roomData
    
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

const manualSyncMembers = async () => {
  if (syncing.value) return
  
  syncing.value = true
  try {
    await loadMembers()
    showToast('成员列表已同步')
  } catch (error) {
    showToast('同步失败')
  } finally {
    syncing.value = false
  }
}

const startSync = () => {
  // 定期同步成员列表 - 每10秒同步一次
  membersSyncInterval.value = setInterval(async () => {
    try {
      await loadMembers()
    } catch (error) {
      console.error('同步成员列表失败:', error)
    }
  }, 10000)
  
  // 移除自动同步播放状态，只保留手动同步功能
  // 避免频繁的自动同步导致播放中断
}

const onVideoLoaded = () => {
  if (videoPlayer.value) {
    duration.value = videoPlayer.value.duration
    // 视频加载完成后，不自动设置播放位置，让用户自由控制
  }
}

const onTimeUpdate = () => {
  if (videoPlayer.value) {
    currentTime.value = videoPlayer.value.currentTime
  }
}

const onPlay = () => {
  isPlaying.value = true
  isVideoPlaying.value = true
  // 播放时不发起任何请求
}

const onPause = () => {
  isPlaying.value = false
  isVideoPlaying.value = false
  // 暂停时不发起任何请求
}

const onVideoError = (event) => {
  console.error('视频播放错误:', event)
  showToast('视频加载失败，请刷新页面重试')
  isPlaying.value = false
}

const onVideoCanPlay = () => {
  // 视频可以开始播放，不做任何处理
}

const onSeeking = () => {
  isSeeking.value = true
}

const onSeeked = () => {
  isSeeking.value = false
  if (videoPlayer.value) {
    currentTime.value = videoPlayer.value.currentTime
    // 只有拖拽进度条时才更新远程状态
    if (isDragging.value) {
      updateRemotePlayback()
    }
  }
}

const onProgressMouseDown = (event) => {
  isDragging.value = true
  onProgressMouseMove(event)
}

const onProgressMouseMove = (event) => {
  if (!isDragging.value) return
  
  if (!videoPlayer.value) return
  
  const rect = event.currentTarget.getBoundingClientRect()
  const clickX = event.clientX - rect.left
  const percent = Math.max(0, Math.min(1, clickX / rect.width))
  const newTime = percent * duration.value
  
  videoPlayer.value.currentTime = newTime
  currentTime.value = newTime
}

const onProgressMouseUp = () => {
  if (isDragging.value) {
    isDragging.value = false
    // 拖拽结束时更新远程状态
    updateRemotePlayback()
  }
}

const onProgressMouseLeave = () => {
  if (isDragging.value) {
    isDragging.value = false
    // 拖拽结束时更新远程状态
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
  // 点击进度条时也更新远程状态
  updateRemotePlayback()
}

const updateRemotePlayback = async () => {
  try {
    await updatePlayback(roomCode.value, {
      currentTime: currentTime.value,
      isPlaying: isPlaying.value
    })
    // 记录同步时间，避免自己同步自己
    lastSyncTime.value = Date.now()
  } catch (error) {
    console.error('更新播放状态失败:', error)
  }
}

const syncVideoProgress = async () => {
  try {
    // 先获取远程进度
    const playbackData = await getPlaybackStatus(roomCode.value)
    const timeDiff = Math.abs(currentTime.value - playbackData.currentTime)
    
    if (timeDiff > 1) {
      // 如果有差异，同步远程进度
      currentTime.value = playbackData.currentTime
      if (videoPlayer.value && videoPlayer.value.readyState >= 2) {
        videoPlayer.value.currentTime = playbackData.currentTime
        showToast(`已同步到 ${formatTime(playbackData.currentTime)}`)
      }
    } else {
      // 如果没有差异，发送当前进度
      await updateRemotePlayback()
      showToast('视频进度已同步')
    }
  } catch (error) {
    showToast('同步失败')
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
  // 清理定时器
  if (membersSyncInterval.value) {
    clearInterval(membersSyncInterval.value)
  }
  
  // 移除自动离开房间的逻辑，只有手动点击离开房间按钮才会离开
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
  transition: opacity 0.3s;
}

.progress-bar:hover {
  opacity: 0.8;
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

.sync-progress-btn {
  background: #28a745;
}

.sync-progress-btn:hover {
  background: #218838;
}

.time-display {
  font-family: monospace;
  font-size: 14px;
  color: #666;
}

.sync-tip {
  text-align: center;
  font-size: 12px;
  color: #999;
  margin-top: 10px;
  padding: 8px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 3px solid #28a745;
}

.members-section {
  border-top: 1px solid #eee;
  padding-top: 20px;
}

.members-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 15px;
}

.members-header h3 {
  color: #333;
  margin: 0;
}

.sync-btn {
  padding: 8px 12px;
  background: #28a745;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.3s;
}

.sync-btn:hover:not(:disabled) {
  background: #218838;
}

.sync-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
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
  
  .members-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
}
</style>


