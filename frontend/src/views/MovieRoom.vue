<template>
  <div class="movie-room">
    <BackButton />
    
    <div class="content">
      <div v-if="loading" class="loading">加载中...</div>
      
      <div v-else-if="!room" class="error">
        <p>房间不存在或已被删除</p>
        <button @click="$router.push('/movies')">返回电影列表</button>
      </div>
      
      <div v-else class="room-content" ref="roomContentRef">
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
               :style="getVideoStyle"
               :poster="generateVideoPoster(room.movie.movieUrl, room.movie)"
               @loadedmetadata="onVideoLoaded"
               @timeupdate="onTimeUpdate"
               @play="onPlay"
               @pause="onPause"
               @seeking="onSeeking"
               @seeked="onSeeked"
               @error="onVideoError"
               @canplay="onVideoCanPlay"
               @click="handleVideoClick"
             ></video>
            
                         <!-- 视频中心的播放按钮 -->
             <div class="video-overlay" v-if="showVideoOverlay" @click="handleVideoOverlayClick">
               <div class="play-button" @click.stop="togglePlay">
                 <span class="play-icon">{{ isPlaying ? '⏸' : '▶' }}</span>
               </div>
             </div>
             
                           <!-- 全屏按钮 - 只在非全屏时显示 -->
              <div v-if="!isFullscreen && showVideoOverlay" class="fullscreen-btn" @click="toggleFullscreen" title="全屏">
                <span class="fullscreen-icon">⛶</span>
              </div>
             
             <!-- 全屏控制界面 -->
             <div v-if="isFullscreen && showFullscreenControls" class="fullscreen-controls">
                               <div class="fullscreen-progress-bar" 
                     @click="seekToFullscreen"
                     @mousedown="onFullscreenProgressMouseDown"
                     title="点击或拖拽调整播放进度">
                 <div class="fullscreen-progress-fill" :style="{ width: progressPercent + '%' }"></div>
                 <div class="fullscreen-progress-handle" :style="{ left: progressPercent + '%' }"></div>
               </div>
               
               <div class="fullscreen-control-row">
                 <div class="fullscreen-control-left">
                   <button class="fullscreen-sync-btn" @click="syncVideoProgress" title="同步视频进度">
                     ⚡ 同步进度
                   </button>
                   <span class="fullscreen-time-display">{{ formatTime(currentTime) }} / {{ formatTime(duration) }}</span>
                 </div>
                 
                 <div class="fullscreen-control-right">
                   <button class="fullscreen-play-btn" @click="togglePlay" title="播放/暂停">
                     {{ isPlaying ? '⏸' : '▶' }}
                   </button>
                   <button class="fullscreen-exit-btn" @click="toggleFullscreen" title="退出全屏">
                     ⛶
                   </button>
                 </div>
               </div>
             </div>
          </div>
          
          <!-- 播放控制 -->
          <div class="video-controls">
                         <div class="progress-bar" 
                  @click="seekTo"
                  @mousedown="onProgressMouseDown"
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
import { ref, reactive, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
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
const isFullscreen = ref(false) // 跟踪全屏状态
const showFullscreenControls = ref(false) // 控制全屏控制界面的显示
const fullscreenControlsTimer = ref(null) // 全屏控制界面隐藏定时器
const showVideoOverlay = ref(true) // 控制视频蒙层显示，默认显示
const videoOverlayTimer = ref(null) // 视频蒙层隐藏定时器
const progressBarRect = ref(null) // 保存进度条位置信息
const fullscreenProgressBarRect = ref(null) // 保存全屏进度条位置信息
const roomContentRef = ref(null) // 房间内容容器引用
const containerWidth = ref(400) // 默认容器宽度

const progressPercent = computed(() => {
  if (duration.value === 0) return 0
  return (currentTime.value / duration.value) * 100
})

const roomCode = computed(() => route.params.roomCode)

// 更新容器宽度
const updateContainerWidth = () => {
  if (roomContentRef.value) {
    containerWidth.value = roomContentRef.value.offsetWidth
    // 确保容器宽度在合理范围内
    containerWidth.value = Math.max(300, Math.min(containerWidth.value, 1200))
  }
}



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
    
    // 确保显示播放按钮
    showVideoOverlay.value = true
  } catch (error) {
    showToast(error.message)
  } finally {
    loading.value = false
  }
}

// 监听room变化，在DOM更新后更新容器宽度
watch(room, () => {
  if (room.value) {
    nextTick(() => {
      updateContainerWidth()
    })
  }
}, { immediate: true })

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
    
    // 确保视频加载完成后显示封面
    if (room.value && room.value.movie && !isPlaying.value) {
      videoPlayer.value.poster = generateVideoPoster(room.value.movie.movieUrl, room.value.movie)
    }
    
    // 视频加载完成后，启动定时器，2秒后隐藏播放按钮
    startVideoOverlayTimer()
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
  
  // 视频开始播放时，移除封面
  if (videoPlayer.value) {
    videoPlayer.value.poster = ''
  }
  
  // 视频开始播放时，隐藏播放按钮
  showVideoOverlay.value = false
  clearVideoOverlayTimer()
}

const onPause = () => {
  isPlaying.value = false
  isVideoPlaying.value = false
  // 暂停时不发起任何请求
  
  // 视频暂停时，重新显示封面
  if (videoPlayer.value && room.value && room.value.movie) {
    videoPlayer.value.poster = generateVideoPoster(room.value.movie.movieUrl, room.value.movie)
  }
  
  // 视频暂停时，显示播放按钮并启动定时器
  showVideoOverlay.value = true
  startVideoOverlayTimer()
}

const onVideoError = (event) => {
  console.error('视频播放错误:', event)
  showToast('视频加载失败，请刷新页面重试')
  isPlaying.value = false
}

const onVideoCanPlay = () => {
  // 视频可以开始播放时，如果还没有开始播放，启动定时器
  if (!isPlaying.value && showVideoOverlay.value) {
    startVideoOverlayTimer()
  }
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
  console.log('开始拖拽进度条')
  event.preventDefault() // 阻止默认行为
  event.stopPropagation() // 阻止事件冒泡
  
  isDragging.value = true
  // 保存进度条的位置信息
  const progressBar = event.currentTarget
  const rect = progressBar.getBoundingClientRect()
  progressBarRect.value = rect
  console.log('进度条位置信息:', rect)
  
  // 添加全局鼠标事件监听
  document.addEventListener('mousemove', onProgressMouseMove, { passive: false })
  document.addEventListener('mouseup', onProgressMouseUp, { passive: false })
  
  // 立即处理当前鼠标位置
  if (videoPlayer.value && progressBarRect.value) {
    const clickX = event.clientX - progressBarRect.value.left
    const percent = Math.max(0, Math.min(1, clickX / progressBarRect.value.width))
    const newTime = percent * duration.value
    
    console.log('初始拖拽位置 - 鼠标位置:', event.clientX, '进度条左边界:', progressBarRect.value.left, '计算位置:', clickX, '百分比:', percent, '新时间:', newTime)
    
    videoPlayer.value.currentTime = newTime
    currentTime.value = newTime
  }
}

const onProgressMouseMove = (event) => {
  console.log('mousemove事件触发, isDragging:', isDragging.value)
  
  if (!isDragging.value) {
    console.log('isDragging为false，退出')
    return
  }
  
  event.preventDefault() // 阻止默认行为
  
  if (!videoPlayer.value) {
    console.log('videoPlayer不存在，退出')
    return
  }
  
  if (!progressBarRect.value) {
    console.log('progressBarRect不存在，退出')
    return
  }
  
  const clickX = event.clientX - progressBarRect.value.left
  const percent = Math.max(0, Math.min(1, clickX / progressBarRect.value.width))
  const newTime = percent * duration.value
  
  console.log('拖拽中 - 鼠标位置:', event.clientX, '进度条左边界:', progressBarRect.value.left, '计算位置:', clickX, '百分比:', percent, '新时间:', newTime)
  
  videoPlayer.value.currentTime = newTime
  currentTime.value = newTime
}

const onProgressMouseUp = (event) => {
  console.log('mouseup事件触发, isDragging:', isDragging.value)
  
  if (isDragging.value) {
    console.log('结束拖拽进度条')
    isDragging.value = false
    // 清理保存的位置信息
    progressBarRect.value = null
    // 移除全局鼠标事件监听
    document.removeEventListener('mousemove', onProgressMouseMove)
    document.removeEventListener('mouseup', onProgressMouseUp)
    // 拖拽结束时更新远程状态
    updateRemotePlayback()
  }
  
  if (event) {
    event.preventDefault()
  }
}



const handleVideoClick = () => {
  // 视频点击控制蒙层显示
  if (isFullscreen.value) {
    // 全屏模式下控制工具栏显示
    showFullscreenControls.value = !showFullscreenControls.value
    if (showFullscreenControls.value) {
      startFullscreenControlsTimer()
    }
  } else {
    // 非全屏模式下控制蒙层显示
    showVideoOverlay.value = !showVideoOverlay.value
    if (showVideoOverlay.value) {
      startVideoOverlayTimer()
    }
    
    // 确保视频样式保持正确
    nextTick(() => {
      if (videoPlayer.value && room.value && room.value.movie && !isFullscreen.value) {
        const currentStyle = getVideoStyle.value
        // 只更新必要的样式属性，避免覆盖其他样式
        if (currentStyle.width) videoPlayer.value.style.width = currentStyle.width
        if (currentStyle.height) videoPlayer.value.style.height = currentStyle.height
        if (currentStyle.objectFit) videoPlayer.value.style.objectFit = currentStyle.objectFit
        if (currentStyle.maxHeight) videoPlayer.value.style.maxHeight = currentStyle.maxHeight
      }
    })
  }
}

const handleVideoOverlayClick = () => {
  // 点击蒙层但不点击播放按钮时，隐藏蒙层
  showVideoOverlay.value = false
  clearVideoOverlayTimer()
}

const startVideoOverlayTimer = () => {
  clearVideoOverlayTimer()
  videoOverlayTimer.value = setTimeout(() => {
    showVideoOverlay.value = false
  }, 2000) // 2秒后隐藏蒙层
}

const clearVideoOverlayTimer = () => {
  if (videoOverlayTimer.value) {
    clearTimeout(videoOverlayTimer.value)
    videoOverlayTimer.value = null
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
  if (!room.value || !room.value.movie || !room.value.movie.height || !room.value.movie.width) {
    return {
      width: '100%',
      height: '400px'
    }
  }
  
  const movie = room.value.movie
  
  let currentContainerWidth = 400 // 默认值
  
  if (isFullscreen.value) {
    // 全屏模式下获取屏幕宽度和高度
    currentContainerWidth = window.innerWidth
    const screenHeight = window.innerHeight
    
    // 根据屏幕宽高比和视频宽高比计算最佳显示尺寸
    const screenAspectRatio = currentContainerWidth / screenHeight
    const videoAspectRatio = movie.width / movie.height
    
    if (videoAspectRatio > screenAspectRatio) {
      // 视频更宽，以宽度为准
      currentContainerWidth = window.innerWidth
    } else {
      // 视频更高，以高度为准
      currentContainerWidth = screenHeight * videoAspectRatio
    }
  } else {
    // 非全屏模式下使用响应式的容器宽度
    currentContainerWidth = containerWidth.value
  }
  
  let result
  if (isFullscreen.value) {
    // 全屏模式下，让视频自适应居中显示
    result = {
      width: 'auto',
      height: 'auto',
      maxWidth: '100%',
      maxHeight: '100%',
      objectFit: 'contain'
    }
  } else {
    // 非全屏模式下，根据视频原始宽高比计算高度，宽度占满
    const aspectRatio = movie.width / movie.height
    
    const height = Math.max(200, Math.min(currentContainerWidth / aspectRatio, 600)) // 确保高度在合理范围内
    console.log(height);
    result = {
      width: '100%',
      height: `${height}px`,
      objectFit: 'cover',
      maxHeight: '600px' // 限制最大高度
    }
  }
  
  return result
})

// 全屏功能
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    // 进入全屏
    const videoContainer = document.querySelector('.video-container')
    if (videoContainer) {
      videoContainer.requestFullscreen().then(() => {
        isFullscreen.value = true
      }).catch(err => {
        console.error('全屏失败:', err)
        showToast('全屏失败')
      })
    }
  } else {
    // 退出全屏
    document.exitFullscreen().then(() => {
      isFullscreen.value = false
    }).catch(err => {
      console.error('退出全屏失败:', err)
    })
  }
}

// 监听全屏状态变化
const handleFullscreenChange = () => {
  isFullscreen.value = !!document.fullscreenElement
  // 更新容器宽度
  updateContainerWidth()
  if (isFullscreen.value) {
    showFullscreenControls.value = true
    startFullscreenControlsTimer()
    // 全屏时添加视频容器的鼠标移动监听
    const videoContainer = document.querySelector('.video-container')
    if (videoContainer) {
      videoContainer.addEventListener('mousemove', handleFullscreenMouseMove)
      videoContainer.addEventListener('mouseleave', handleFullscreenMouseLeave)
    }
  } else {
    showFullscreenControls.value = false
    clearFullscreenControlsTimer()
    // 退出全屏时移除视频容器的鼠标移动监听
    const videoContainer = document.querySelector('.video-container')
    if (videoContainer) {
      videoContainer.removeEventListener('mousemove', handleFullscreenMouseMove)
      videoContainer.removeEventListener('mouseleave', handleFullscreenMouseLeave)
    }
    
    // 退出全屏时，强制重新计算视频样式
    // 使用 nextTick 确保 DOM 更新完成后再重新计算
    nextTick(() => {
      if (videoPlayer.value && room.value && room.value.movie) {
        // 强制重新应用样式
        const newStyle = getVideoStyle.value
        Object.assign(videoPlayer.value.style, newStyle)
        
        // 确保视频容器样式正确重置
        const videoContainer = document.querySelector('.video-container')
        if (videoContainer) {
          videoContainer.style.width = '100%'
          videoContainer.style.height = 'auto'
          videoContainer.style.position = 'relative'
          videoContainer.style.top = 'auto'
          videoContainer.style.left = 'auto'
          videoContainer.style.zIndex = 'auto'
        }
        
        // 延迟再次检查，确保样式完全重置
        setTimeout(() => {
          if (videoPlayer.value && room.value && room.value.movie) {
            const finalStyle = getVideoStyle.value
            Object.assign(videoPlayer.value.style, finalStyle)
          }
        }, 100)
      }
    })
  }
  
  // 全屏状态改变时，videoStyle computed 会自动重新计算
  // 这里可以添加一些额外的处理逻辑
}



const clearFullscreenControlsTimer = () => {
  if (fullscreenControlsTimer.value) {
    clearTimeout(fullscreenControlsTimer.value)
    fullscreenControlsTimer.value = null
  }
}

const handleFullscreenMouseMove = () => {
  if (isFullscreen.value) {
    showFullscreenControls.value = true
    startFullscreenControlsTimer()
  }
}

const handleFullscreenMouseLeave = () => {
  if (isFullscreen.value && !isDragging.value) {
    showFullscreenControls.value = false
    clearFullscreenControlsTimer()
  }
}

// 处理窗口大小改变
const handleResize = () => {
  // 更新容器宽度
  updateContainerWidth()
  // videoStyle computed 会自动重新计算
}

// 修改全屏控制界面的显示逻辑
const startFullscreenControlsTimer = () => {
  clearFullscreenControlsTimer()
  fullscreenControlsTimer.value = setTimeout(() => {
    if (isFullscreen.value && !isDragging.value) {
      showFullscreenControls.value = false
    }
  }, 2000) // 2秒后隐藏控制界面
}

// 监听键盘事件
const handleKeydown = (event) => {
  if (isFullscreen.value) {
    switch (event.key) {
      case 'Escape':
        toggleFullscreen()
        break
      case ' ':
        event.preventDefault()
        togglePlay()
        break
      case 'ArrowLeft':
        event.preventDefault()
        if (videoPlayer.value) {
          videoPlayer.value.currentTime = Math.max(0, videoPlayer.value.currentTime - 10)
        }
        break
      case 'ArrowRight':
        event.preventDefault()
        if (videoPlayer.value) {
          videoPlayer.value.currentTime = Math.min(videoPlayer.value.duration, videoPlayer.value.currentTime + 10)
        }
        break
    }
  }
}

// 全屏模式下的进度条点击
const seekToFullscreen = (event) => {
  if (!videoPlayer.value || isDragging.value) return
  
  const rect = event.currentTarget.getBoundingClientRect()
  const clickX = event.clientX - rect.left
  const percent = clickX / rect.width
  const newTime = percent * duration.value
  
  videoPlayer.value.currentTime = newTime
  currentTime.value = newTime
  updateRemotePlayback()
}

// 全屏模式下的进度条拖拽功能
const onFullscreenProgressMouseDown = (event) => {
  console.log('开始拖拽全屏进度条')
  event.preventDefault() // 阻止默认行为
  isDragging.value = true
  // 保存全屏进度条的位置信息
  const progressBar = event.currentTarget
  const rect = progressBar.getBoundingClientRect()
  fullscreenProgressBarRect.value = rect
  console.log('全屏进度条位置信息:', rect)
  // 添加全局鼠标事件监听
  document.addEventListener('mousemove', onFullscreenProgressMouseMove)
  document.addEventListener('mouseup', onFullscreenProgressMouseUp)
  onFullscreenProgressMouseMove(event)
}

const onFullscreenProgressMouseMove = (event) => {
  if (!isDragging.value) return
  
  if (!videoPlayer.value) return
  
  if (!fullscreenProgressBarRect.value) return
  
  const clickX = event.clientX - fullscreenProgressBarRect.value.left
  const percent = Math.max(0, Math.min(1, clickX / fullscreenProgressBarRect.value.width))
  const newTime = percent * duration.value
  
  console.log('全屏拖拽中 - 鼠标位置:', event.clientX, '进度条左边界:', fullscreenProgressBarRect.value.left, '计算位置:', clickX, '百分比:', percent, '新时间:', newTime)
  
  videoPlayer.value.currentTime = newTime
  currentTime.value = newTime
  
  // 拖拽时显示控制界面
  if (isFullscreen.value) {
    showFullscreenControls.value = true
    startFullscreenControlsTimer()
  }
}

const onFullscreenProgressMouseUp = () => {
  if (isDragging.value) {
    console.log('结束拖拽全屏进度条')
    isDragging.value = false
    // 清理保存的位置信息
    fullscreenProgressBarRect.value = null
    // 移除全局鼠标事件监听
    document.removeEventListener('mousemove', onFullscreenProgressMouseMove)
    document.removeEventListener('mouseup', onFullscreenProgressMouseUp)
    // 拖拽结束时更新远程状态
    updateRemotePlayback()
  }
}



onMounted(() => {
  loadRoom()
  
  // 添加事件监听器
  document.addEventListener('fullscreenchange', handleFullscreenChange)
  document.addEventListener('keydown', handleKeydown)
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  // 移除自动离开房间的逻辑，只有手动点击离开房间按钮才会离开
  
  // 移除事件监听器
  document.removeEventListener('fullscreenchange', handleFullscreenChange)
  document.removeEventListener('keydown', handleKeydown)
  window.removeEventListener('resize', handleResize)
  
  // 移除视频容器的鼠标移动监听（如果还在全屏状态）
  const videoContainer = document.querySelector('.video-container')
  if (videoContainer) {
    videoContainer.removeEventListener('mousemove', handleFullscreenMouseMove)
    videoContainer.removeEventListener('mouseleave', handleFullscreenMouseLeave)
  }
  
  // 移除可能存在的全局拖拽事件监听器
  document.removeEventListener('mousemove', onProgressMouseMove)
  document.removeEventListener('mouseup', onProgressMouseUp)
  document.removeEventListener('mousemove', onFullscreenProgressMouseMove)
  document.removeEventListener('mouseup', onFullscreenProgressMouseUp)
  
  // 清理拖拽相关的变量
  progressBarRect.value = null
  fullscreenProgressBarRect.value = null
  
  // 清理定时器
  clearFullscreenControlsTimer()
  clearVideoOverlayTimer()
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
  border-radius: 20px;
  background: #000;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.video-container:hover {
  transform: scale(1.02);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.3);
}

/* 全屏模式下的视频容器样式 */
.video-container:fullscreen {
  width: 100vw;
  height: 100vh;
  border-radius: 0;
  background: #000;
  display: flex;
  align-items: center;
  justify-content: center;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 9999;
}

.video-container:fullscreen .video-player {
  max-width: 100%;
  max-height: 100%;
  width: auto;
  height: auto;
  border-radius: 0;
  object-fit: contain;
  display: block;
  margin: auto;
}

.video-container:fullscreen .video-overlay {
  border-radius: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.video-player {
  width: 100%;
  object-fit: cover;
  cursor: pointer;
  background: #000;
  border-radius: 20px;
  overflow: hidden;
  transition: all 0.3s ease;
  display: block;
}

.video-player:hover {
  transform: scale(1.02);
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
  background: rgba(0, 0, 0, 0.3);
  border-radius: 20px;
  cursor: pointer;
}

.play-button {
  width: 80px;
  height: 80px;
  background: transparent;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  border: 2px solid rgba(255, 255, 255, 0.3);
  transition: all 0.3s ease;
}

.play-button:hover {
  transform: scale(1.05);
  border-color: rgba(255, 255, 255, 0.6);
}

.play-icon {
  font-size: 32px;
  margin-left: 4px;
  color: white;
}

.fullscreen-btn {
  position: absolute;
  top: 15px;
  right: 15px;
  width: 40px;
  height: 40px;
  background: transparent;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  z-index: 10;
}

.fullscreen-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

.fullscreen-icon {
  font-size: 16px;
  color: white;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.8);
}

/* 全屏控制界面样式 */
.fullscreen-controls {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(transparent, transparent, rgba(0, 0, 0, 0.8));
  padding: 20px;
  opacity: 1;
  transition: opacity 0.3s ease;
  z-index: 1000;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
}

.fullscreen-progress-bar {
  width: 100%;
  height: 6px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
  cursor: pointer;
  margin-bottom: 15px;
  position: relative;
  user-select: none;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
}

.fullscreen-progress-bar:hover {
  background: rgba(255, 255, 255, 0.5);
}

.fullscreen-progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  border-radius: 3px;
  transition: width 0.1s ease;
  pointer-events: none;
}

.fullscreen-progress-handle {
  position: absolute;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 16px;
  height: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  border: 2px solid white;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
  transition: all 0.2s ease;
  pointer-events: none;
}

.fullscreen-progress-handle:hover {
  transform: translate(-50%, -50%) scale(1.2);
  box-shadow: 0 3px 8px rgba(0, 0, 0, 0.4);
}

.fullscreen-control-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.fullscreen-control-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.fullscreen-control-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.fullscreen-sync-btn {
  padding: 6px 10px;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.2s;
  font-weight: 500;
}

.fullscreen-sync-btn:hover {
  background: rgba(0, 0, 0, 0.7);
}

.fullscreen-time-display {
  font-family: 'Courier New', monospace;
  font-size: 14px;
  color: white;
  font-weight: 500;
  min-width: 100px;
  text-align: center;
  background: rgba(0, 0, 0, 0.5);
  padding: 4px 8px;
  border-radius: 4px;
}

.fullscreen-play-btn {
  width: 40px;
  height: 40px;
  background: rgba(0, 0, 0, 0.5);
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.fullscreen-play-btn:hover {
  background: rgba(0, 0, 0, 0.7);
}

.fullscreen-exit-btn {
  width: 40px;
  height: 40px;
  background: rgba(0, 0, 0, 0.5);
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.fullscreen-exit-btn:hover {
  background: rgba(0, 0, 0, 0.7);
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
  user-select: none;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
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
  pointer-events: none;
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
  pointer-events: none;
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
     justify-content: flex-end;
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

  .video-container {
    border-radius: 15px;
  }
  
  .video-player {
    border-radius: 15px;
  }
  
  .video-overlay {
    border-radius: 15px;
  }

  .play-button {
    width: 60px;
    height: 60px;
  }

  .play-icon {
    font-size: 26px;
  }
  
  .fullscreen-btn {
    width: 35px;
    height: 35px;
  }
  
  .fullscreen-icon {
    font-size: 16px;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.8);
  }
  
  .fullscreen-controls {
    padding: 15px;
  }
  
  .fullscreen-sync-btn {
    padding: 6px 10px;
    font-size: 12px;
  }
  
  .fullscreen-time-display {
    font-size: 14px;
    min-width: 100px;
  }
  
  .fullscreen-play-btn {
    width: 40px;
    height: 40px;
    font-size: 16px;
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


