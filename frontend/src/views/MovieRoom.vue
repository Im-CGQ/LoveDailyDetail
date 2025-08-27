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
          <div class="video-container">
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
              @click="togglePlay"
            ></video>
            
                         <!-- 视频中心的播放按钮 -->
             <div class="video-overlay" v-if="!isPlaying" @click="togglePlay">
               <div class="play-button">
                 <span class="play-icon">▶</span>
               </div>
             </div>
          </div>
          
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
            
                                      <div class="control-row">
               <div class="control-left">
                                   <button class="member-sync-btn" @click="syncVideoProgress" title="同步视频进度">
                    ⚡ 同步进度
                  </button>
                 <span class="time-display">{{ formatTime(currentTime) }} / {{ formatTime(duration) }}</span>
               </div>
             </div>
             
             <div class="sync-tip">
               💡 提示：拖拽进度条或点击同步按钮可同步进度给其他用户
             </div>
             
             <div class="leave-section">
               <button class="control-btn leave-btn" @click="leaveRoom">离开房间</button>
             </div>
          </div>
        </div>

                 <!-- 房间成员 -->
         <div class="members-section">
           <div class="members-header">
             <h3>房间成员 ({{ members.length }})</h3>
           </div>
           <div class="members-controls">
                           <button class="member-sync-btn" @click="manualSyncMembers" :disabled="syncing">
                {{ syncing ? '同步中...' : '⚡ 同步成员' }}
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
  // 移除自动同步逻辑，只保留手动同步功能
  // 让用户自己控制何时同步成员列表
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

.video-container {
  position: relative;
  width: 100%;
  max-height: 500px;
  border-radius: 10px;
  background: #000;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.video-player {
  width: 100%;
  height: 100%;
  object-fit: cover;
  cursor: pointer;
}

.video-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 10px;
  cursor: pointer;
}

.play-button {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #333;
  border: 2px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.play-button:hover {
  transform: scale(1.05);
  background: rgba(255, 255, 255, 1);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.4);
}

.play-icon {
  font-size: 32px;
  margin-left: 4px;
  color: #333;
}

.video-controls {
  margin-top: 15px;
}

.control-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
}

.control-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.control-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.progress-bar {
  position: relative;
  height: 10px;
  background: #e9ecef;
  border-radius: 5px;
  cursor: pointer;
  margin-bottom: 15px;
  transition: all 0.3s;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.1);
}

.progress-bar:hover {
  background: #dee2e6;
  transform: translateY(-1px);
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.1), 0 2px 4px rgba(0, 0, 0, 0.1);
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  border-radius: 5px;
  transition: width 0.1s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
}

.progress-handle {
  position: absolute;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 18px;
  height: 18px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  border: 3px solid white;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
  transition: all 0.2s ease;
}

.progress-handle:hover {
  transform: translate(-50%, -50%) scale(1.2);
  box-shadow: 0 3px 8px rgba(0, 0, 0, 0.4);
}

.control-btn {
  padding: 8px 12px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
  min-width: 40px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.control-btn:hover {
  background: #5a6fd8;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}





.leave-btn {
  background: #dc3545;
}

.leave-btn:hover {
  background: #c82333;
}

.time-display {
  font-family: 'Courier New', monospace;
  font-size: 14px;
  color: #666;
  font-weight: 500;
  min-width: 120px;
  text-align: center;
}

.sync-tip {
  text-align: center;
  font-size: 11px;
  color: #6c757d;
  margin-top: 12px;
  padding: 8px 12px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 8px;
  border-left: 3px solid #17a2b8;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.leave-section {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
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

.members-controls {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 15px;
}

.member-sync-btn {
  padding: 6px 12px;
  background: transparent;
  color: #28a745;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s;
  font-weight: 500;
}

.member-sync-btn:hover:not(:disabled) {
  color: #218838;
  transform: scale(1.02);
}

.member-sync-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
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
  
  .control-row {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
     .control-left {
     width: 100%;
     justify-content: space-between;
   }
   
   .leave-section {
     justify-content: center;
   }

  .control-btn {
    padding: 6px 10px;
    font-size: 12px;
    min-width: 36px;
    height: 32px;
  }
  
                 .time-display {
      font-size: 12px;
      min-width: 100px;
    }

  .play-button {
    width: 60px;
    height: 60px;
  }

  .play-icon {
    font-size: 26px;
  }

  .members-list {
    grid-template-columns: 1fr;
  }
  
  .members-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }

  .member-sync-btn {
    font-size: 11px;
    padding: 5px 10px;
  }
}
</style>


