<template>
  <div class="create-diary">
    
    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field
          v-model="form.title"
          name="title"
          label="标题"
          placeholder="请输入回忆标题"
          :rules="[{ required: true, message: '请输入标题' }]"
        />
        
        <van-field
          v-model="form.date"
          name="date"
          label="日期"
          placeholder="请选择日期"
          readonly
          @click="showDatePicker = true"
          :rules="[{ required: true, message: '请选择日期' }]"
        />
        
        <van-field
          v-model="form.description"
          name="description"
          label="描述"
          type="textarea"
          placeholder="请输入回忆描述"
          autosize
          :rules="[{ required: true, message: '请输入描述' }]"
        />
      </van-cell-group>
      
      <div class="upload-section">
        <h3>图片上传</h3>
        <van-uploader
          v-model="form.images"
          multiple
          :max-count="20"
          :after-read="afterRead"
          :before-delete="beforeDelete"
          @oversize="onOversize"
          accept="image/*"
        />
        <div class="upload-tips">
          <p>支持上传最多20张图片，每张图片大小不超过20MB</p>
        </div>
      </div>
      
      <div class="upload-section">
        <h3>视频上传</h3>
        <van-uploader
          v-model="form.videos"
          :max-count="3"
          accept="video/*"
          :after-read="afterVideoRead"
          :before-delete="beforeVideoDelete"
          @oversize="onVideoOversize"
        />
        <div class="upload-tips">
          <p>支持上传最多3个视频，每个视频大小不超过100MB</p>
        </div>
        
        <!-- 视频预览区域 -->
        <div class="video-preview-section" v-if="form.videos.length > 0">
          <h4>视频预览</h4>
          <div class="video-preview-list">
            <div 
              v-for="(video, index) in form.videos" 
              :key="index" 
              class="video-preview-item"
            >
              <div class="video-info">
                <span class="video-name">{{ video.file?.name || `视频${index + 1}` }}</span>
                <span class="video-status" :class="video.status">
                  {{ getVideoStatusText(video.status) }}
                </span>
              </div>
              <div class="video-player-container" v-if="video.url">
                <video 
                  :src="video.url" 
                  class="video-preview-player"
                  preload="metadata"
                >
                  您的浏览器不支持视频播放
                </video>
                <div class="play-overlay" @click.stop="playVideo(index)">
                  <div class="play-button">▶</div>
                </div>
              </div>
              <div class="video-placeholder" v-else>
                <div class="uploading-indicator" v-if="video.status === 'uploading'">
                  <van-loading size="24px" color="#ff6b9d">上传中...</van-loading>
                </div>
                <div class="error-indicator" v-else-if="video.status === 'failed'">
                  <van-icon name="warning-o" color="#ff4444" size="24px" />
                  <span>上传失败</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="upload-section">
        <h3>背景音乐</h3>
        <van-uploader
          v-model="form.backgroundMusic"
          :max-count="1"
          accept="audio/*"
          :after-read="afterMusicRead"
        />
        
        <!-- 音乐预览区域 -->
        <div class="music-preview-section" v-if="form.backgroundMusic.length > 0">
          <h4>音乐预览</h4>
          <div class="music-preview-item">
            <div class="music-info">
              <span class="music-name">{{ form.backgroundMusic[0].file?.name || '背景音乐' }}</span>
              <span class="music-status" :class="form.backgroundMusic[0].status">
                {{ getMusicStatusText(form.backgroundMusic[0].status) }}
              </span>
            </div>
            <div class="music-player-container" v-if="form.backgroundMusic[0].url">
              <audio 
                :src="form.backgroundMusic[0].url" 
                ref="audioElement"
                preload="metadata"
                @loadedmetadata="onMusicLoaded"
                @error="onMusicError"
              ></audio>
              <div class="music-controls">
                <button 
                  type="button"
                  class="play-btn" 
                  @click.stop="toggleMusic"
                  :class="{ 'playing': isMusicPlaying }"
                >
                  <span v-if="isMusicPlaying">⏸️</span>
                  <span v-else>▶️</span>
                </button>
                <div class="music-progress">
                  <div class="progress-bar" @click.stop="seekMusic" ref="musicProgressBar">
                    <div class="progress-fill" :style="{ width: musicProgress + '%' }"></div>
                  </div>
                  <span class="time-display">{{ formatTime(currentMusicTime) }} / {{ formatTime(musicDuration) }}</span>
                </div>
              </div>
            </div>
            <div class="music-placeholder" v-else>
              <div class="uploading-indicator" v-if="form.backgroundMusic[0].status === 'uploading'">
                <van-loading size="24px" color="#ff6b9d">上传中...</van-loading>
              </div>
              <div class="error-indicator" v-else-if="form.backgroundMusic[0].status === 'failed'">
                <van-icon name="warning-o" color="#ff4444" size="24px" />
                <span>上传失败</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="submit-section">
        <van-button 
          round 
          block 
          type="primary" 
          native-type="submit"
          :loading="loading"
        >
          创建回忆
        </van-button>
      </div>
    </van-form>
    
    <van-popup v-model:show="showDatePicker" position="bottom">
      <van-date-picker
        v-model="currentDate"
        title="选择日期"
        :min-date="new Date(2020, 0, 1)"
        :max-date="new Date(2030, 11, 31)"
        @confirm="onDateConfirm"
        @cancel="showDatePicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import dayjs from 'dayjs'
import { createDiary } from '@/api/admin.js'
import { uploadImage, uploadVideo, uploadMusic } from '@/api/upload.js'

const router = useRouter()

const loading = ref(false)
const showDatePicker = ref(false)
// 初始化当前日期为数组格式，用于日期选择器
const currentDate = ref([
  new Date().getFullYear().toString(),
  (new Date().getMonth() + 1).toString().padStart(2, '0'),
  new Date().getDate().toString().padStart(2, '0')
])

const form = ref({
  title: '',
  date: dayjs().format('YYYY-MM-DD'),
  description: '',
  images: [],
  videos: [],
  backgroundMusic: []
})

// 音乐播放相关
const audioElement = ref(null)
const musicProgressBar = ref(null)
const isMusicPlaying = ref(false)
const currentMusicTime = ref(0)
const musicDuration = ref(0)
const musicProgress = ref(0)
const musicProgressTimer = ref(null)

const onDateConfirm = (val) => {
  try {
    console.log('日期确认值:', val, '类型:', typeof val, '是否为数组:', Array.isArray(val))
    
    // 处理日期选择器返回的数组格式 ['2021', '02', '01']
    let selectedDate
    if (Array.isArray(val)) {
      // 如果是数组格式，将其转换为日期字符串
      const [year, month, day] = val
      selectedDate = new Date(parseInt(year), parseInt(month) - 1, parseInt(day))
    } else if (val && val.selectedValues && Array.isArray(val.selectedValues)) {
      // 如果是对象格式，获取selectedValues数组
      const [year, month, day] = val.selectedValues
      selectedDate = new Date(parseInt(year), parseInt(month) - 1, parseInt(day))
    } else if (val instanceof Date) {
      selectedDate = val
    } else {
      selectedDate = new Date(val)
    }
    
    // 验证日期是否有效
    if (isNaN(selectedDate.getTime())) {
      throw new Error('无效的日期值')
    }
    
    // 更新表单日期
    form.value.date = dayjs(selectedDate).format('YYYY-MM-DD')
    showDatePicker.value = false
    
    console.log('处理后的日期:', form.value.date)
  } catch (error) {
    console.error('日期处理错误:', error)
    // 使用当前日期作为默认值
    const now = new Date()
    form.value.date = dayjs(now).format('YYYY-MM-DD')
    showDatePicker.value = false
  }
}

const afterRead = async (file) => {
  // 如果是多选上传，file可能是一个数组
  if (Array.isArray(file)) {
    // 批量处理多个文件
    for (const singleFile of file) {
      await processSingleFile(singleFile)
    }
  } else {
    // 单个文件处理
    await processSingleFile(file)
  }
}

const processSingleFile = async (file) => {
  try {
    // 检查文件大小
    if (file.file.size > 20 * 1024 * 1024) {
      showToast('图片大小不能超过20MB')
      // 移除超大的文件
      const index = form.value.images.findIndex(f => f.file === file.file)
      if (index > -1) {
        form.value.images.splice(index, 1)
      }
      return
    }
    
    // 检查文件类型
    if (!file.file.type.startsWith('image/')) {
      showToast('只能上传图片文件')
      // 移除非图片文件
      const index = form.value.images.findIndex(f => f.file === file.file)
      if (index > -1) {
        form.value.images.splice(index, 1)
      }
      return
    }
    
    // 显示上传进度
    file.status = 'uploading'
    file.message = '上传中...'
    
    const url = await uploadImage(file.file)
    file.url = url
    file.status = 'done'
    file.message = '上传成功'
    showToast('图片上传成功')
  } catch (error) {
    console.error('图片上传失败:', error)
    file.status = 'failed'
    file.message = '上传失败'
    showToast('图片上传失败')
    // 移除上传失败的文件
    const index = form.value.images.findIndex(f => f.file === file.file)
    if (index > -1) {
      form.value.images.splice(index, 1)
    }
  }
}

const beforeDelete = (file) => {
  return new Promise(resolve => {
    // 允许删除图片
    resolve(true)
  })
}

const onOversize = (file) => {
  showToast('图片大小不能超过20MB')
  return false
}

const beforeVideoDelete = (file) => {
  return new Promise(resolve => {
    // 允许删除视频
    resolve(true)
  })
}

const onVideoOversize = (file) => {
  showToast('视频大小不能超过100MB')
  return false
}

const getVideoStatusText = (status) => {
  switch (status) {
    case 'uploading':
      return '上传中...'
    case 'done':
      return '上传成功'
    case 'failed':
      return '上传失败'
    default:
      return '待上传'
  }
}

const playVideo = (index) => {
  console.log('playVideo called with index:', index)
  const video = form.value.videos[index]
  console.log('video object:', video)
  if (!video || !video.url) {
    console.log('No video or URL found')
    return
  }
  
  // 创建全屏视频播放器
  const videoElement = document.createElement('video')
  videoElement.src = video.url
  videoElement.controls = true
  videoElement.autoplay = true
  videoElement.muted = true // 静音播放，避免自动播放策略限制
  videoElement.style.cssText = `
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background: #000;
    z-index: 9999;
    object-fit: contain;
  `
  
  // 添加加载事件监听
  const loadedmetadataHandler = () => {
    console.log('Video metadata loaded, attempting to play')
    videoElement.play().then(() => {
      console.log('Video started playing successfully')
      // 播放成功后立即取消静音
      videoElement.muted = false
      console.log('Video unmuted')
    }).catch(error => {
      console.error('Failed to autoplay video:', error)
      // 如果自动播放失败，显示提示
      showToast('点击播放按钮开始播放')
    })
  }
  
  const errorHandler = (e) => {
    console.error('Video error:', e)
    showToast('视频加载失败')
  }
  
  videoElement._loadedmetadataHandler = loadedmetadataHandler
  videoElement._errorHandler = errorHandler
  
  videoElement.addEventListener('loadedmetadata', loadedmetadataHandler)
  videoElement.addEventListener('error', errorHandler)
  
  // 添加关闭按钮
  const closeButton = document.createElement('div')
  closeButton.innerHTML = '✕'
  closeButton.style.cssText = `
    position: fixed;
    top: 20px;
    right: 20px;
    width: 40px;
    height: 40px;
    background: rgba(0, 0, 0, 0.7);
    color: white;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    font-size: 20px;
    z-index: 10000;
    transition: all 0.3s ease;
  `
  
  // 关闭按钮悬停效果
  closeButton.addEventListener('mouseenter', () => {
    closeButton.style.background = 'rgba(255, 255, 255, 0.2)'
    closeButton.style.transform = 'scale(1.1)'
  })
  
  closeButton.addEventListener('mouseleave', () => {
    closeButton.style.background = 'rgba(0, 0, 0, 0.7)'
    closeButton.style.transform = 'scale(1)'
  })
  
  // 关闭功能
  const closeVideo = () => {
    videoElement.pause()
    // 清理事件监听器
    videoElement.removeEventListener('loadedmetadata', videoElement._loadedmetadataHandler)
    videoElement.removeEventListener('error', videoElement._errorHandler)
    videoElement.removeEventListener('click', videoElement._unmuteOnInteraction)
    videoElement.removeEventListener('play', videoElement._unmuteOnInteraction)
    document.removeEventListener('keydown', handleEscKey)
    // 移除元素
    document.body.removeChild(videoElement)
    document.body.removeChild(closeButton)
    document.body.style.overflow = 'auto'
  }
  
  closeButton.addEventListener('click', closeVideo)
  
  // ESC键关闭
  const handleEscKey = (event) => {
    if (event.key === 'Escape') {
      closeVideo()
      document.removeEventListener('keydown', handleEscKey)
    }
  }
  
  document.addEventListener('keydown', handleEscKey)
  
  // 点击视频背景关闭
  videoElement.addEventListener('click', (event) => {
    if (event.target === videoElement) {
      closeVideo()
      document.removeEventListener('keydown', handleEscKey)
    }
  })
  
  // 添加用户交互事件来确保取消静音
  const unmuteOnInteraction = () => {
    if (videoElement.muted) {
      videoElement.muted = false
      console.log('Video unmuted on user interaction')
    }
    // 移除事件监听器，避免重复触发
    videoElement.removeEventListener('click', unmuteOnInteraction)
    videoElement.removeEventListener('play', unmuteOnInteraction)
  }
  
  videoElement._unmuteOnInteraction = unmuteOnInteraction
  videoElement.addEventListener('click', unmuteOnInteraction)
  videoElement.addEventListener('play', unmuteOnInteraction)
  
  // 添加到页面并禁止滚动
  document.body.appendChild(videoElement)
  document.body.appendChild(closeButton)
  document.body.style.overflow = 'hidden'
}

const afterVideoRead = async (file) => {
  // 如果是多选上传，file可能是一个数组
  if (Array.isArray(file)) {
    // 批量处理多个文件
    for (const singleFile of file) {
      await processSingleVideo(singleFile)
    }
  } else {
    // 单个文件处理
    await processSingleVideo(file)
  }
}

const processSingleVideo = async (file) => {
  try {
    // 检查文件大小
    if (file.file.size > 100 * 1024 * 1024) {
      showToast('视频大小不能超过100MB')
      // 移除超大的文件
      const index = form.value.videos.findIndex(f => f.file === file.file)
      if (index > -1) {
        form.value.videos.splice(index, 1)
      }
      return
    }
    
    // 检查文件类型
    if (!file.file.type.startsWith('video/')) {
      showToast('只能上传视频文件')
      // 移除非视频文件
      const index = form.value.videos.findIndex(f => f.file === file.file)
      if (index > -1) {
        form.value.videos.splice(index, 1)
      }
      return
    }
    
    // 显示上传进度
    file.status = 'uploading'
    file.message = '上传中...'
    
    const url = await uploadVideo(file.file)
    file.url = url
    file.status = 'done'
    file.message = '上传成功'
    showToast('视频上传成功')
  } catch (error) {
    console.error('视频上传失败:', error)
    file.status = 'failed'
    file.message = '上传失败'
    showToast('视频上传失败')
    // 移除上传失败的文件
    const index = form.value.videos.findIndex(f => f.file === file.file)
    if (index > -1) {
      form.value.videos.splice(index, 1)
    }
  }
}

const afterMusicRead = async (file) => {
  try {
    // 显示上传进度
    file.status = 'uploading'
    file.message = '上传中...'
    
    const url = await uploadMusic(file.file)
    file.url = url
    file.status = 'done'
    file.message = '上传成功'
    showToast('音乐上传成功')
  } catch (error) {
    console.error('音乐上传失败:', error)
    file.status = 'failed'
    file.message = '上传失败'
    showToast('音乐上传失败')
    // 移除上传失败的文件
    const index = form.value.backgroundMusic.findIndex(f => f.file === file.file)
    if (index > -1) {
      form.value.backgroundMusic.splice(index, 1)
    }
  }
}

const getMusicStatusText = (status) => {
  switch (status) {
    case 'uploading':
      return '上传中...'
    case 'done':
      return '上传成功'
    case 'failed':
      return '上传失败'
    default:
      return '待上传'
  }
}

// 音乐播放相关方法
const onMusicLoaded = () => {
  if (audioElement.value) {
    musicDuration.value = audioElement.value.duration
  }
}

const onMusicError = () => {
  showToast('音乐加载失败')
}

const toggleMusic = () => {
  if (!audioElement.value) return
  
  if (isMusicPlaying.value) {
    audioElement.value.pause()
  } else {
    audioElement.value.play()
  }
}

const startMusicProgressTimer = () => {
  if (musicProgressTimer.value) return
  
  musicProgressTimer.value = setInterval(() => {
    if (audioElement.value && !audioElement.value.paused) {
      currentMusicTime.value = audioElement.value.currentTime
      musicProgress.value = (audioElement.value.currentTime / audioElement.value.duration) * 100
    }
  }, 100)
}

const stopMusicProgressTimer = () => {
  if (musicProgressTimer.value) {
    clearInterval(musicProgressTimer.value)
    musicProgressTimer.value = null
  }
}

const formatTime = (seconds) => {
  if (!seconds || isNaN(seconds)) return '0:00'
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins}:${secs.toString().padStart(2, '0')}`
}

const seekMusic = (event) => {
  if (!audioElement.value || !musicProgressBar.value) return
  
  const rect = musicProgressBar.value.getBoundingClientRect()
  const clickX = event.clientX - rect.left
  const progressBarWidth = rect.width
  const percentage = (clickX / progressBarWidth) * 100
  
  // 限制百分比在0-100之间
  const clampedPercentage = Math.max(0, Math.min(100, percentage))
  const newTime = (clampedPercentage / 100) * audioElement.value.duration
  
  audioElement.value.currentTime = newTime
  currentMusicTime.value = newTime
  musicProgress.value = clampedPercentage
}

// 监听音频事件
const initAudioListeners = () => {
  if (!audioElement.value) return
  
  audioElement.value.addEventListener('play', () => {
    isMusicPlaying.value = true
    startMusicProgressTimer()
  })
  
  audioElement.value.addEventListener('pause', () => {
    isMusicPlaying.value = false
    stopMusicProgressTimer()
  })
  
  audioElement.value.addEventListener('ended', () => {
    isMusicPlaying.value = false
    stopMusicProgressTimer()
    currentMusicTime.value = 0
    musicProgress.value = 0
  })
}

// 清理音频事件监听器
const cleanupAudioListeners = () => {
  if (!audioElement.value) return
  
  audioElement.value.removeEventListener('play', () => {})
  audioElement.value.removeEventListener('pause', () => {})
  audioElement.value.removeEventListener('ended', () => {})
}

// 监听音频元素变化
watch(audioElement, (newElement) => {
  if (newElement) {
    initAudioListeners()
  }
})

onMounted(() => {
  // 组件挂载时的初始化
})

onUnmounted(() => {
  // 清理定时器
  stopMusicProgressTimer()
  // 清理音频事件监听器
  cleanupAudioListeners()
})

const onSubmit = async (values) => {
  loading.value = true
  
  try {
    // 准备提交的数据
    const diaryData = {
      title: form.value.title,
      date: form.value.date,
      description: form.value.description,
      images: form.value.images.map(file => file.url),
      videos: form.value.videos.map(file => file.url),
      backgroundMusic: form.value.backgroundMusic.length > 0 ? form.value.backgroundMusic[0].url : null
    }
    
    await createDiary(diaryData)
    showToast('创建成功')
    router.push('/admin/diary/list')
  } catch (error) {
    console.error('创建失败:', error)
    showToast('创建失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.create-diary {
  :deep(.van-cell) {
    padding: 8px 0;
  }
  
      :deep(.van-field) {
      .van-field__label {
        margin-bottom: 8px;
        display: block;
        width: 100%;
        text-align: left;
        font-weight: 500;
        color: #333;
        
        &::after {
          content: ':';
          margin-left: 2px;
        }
      }
    
    .van-field__control {
      margin-top: 0;
      width: 100%;
    }
    
    .van-field__body {
      flex-direction: column;
      align-items: flex-start;
    }
  }
}

.upload-section {
  margin: 20px 0;
  padding: 0 16px;
  
  h3 {
    margin-bottom: 10px;
    color: #333;
    font-size: 16px;
    font-weight: 500;
    position: relative;
    
    &::before {
      content: '';
      position: absolute;
      left: -8px;
      top: 50%;
      transform: translateY(-50%);
      width: 4px;
      height: 16px;
      background: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 100%);
      border-radius: 2px;
    }
  }
}

.upload-tips {
  margin-top: 10px;
  padding: 8px 12px;
  background-color: #f5f5f5;
  border-radius: 8px;
  font-size: 12px;
  color: #666;
  line-height: 1.5;
}

/* 视频预览样式 */
.video-preview-section {
  margin-top: 20px;
  
  h4 {
    margin-bottom: 15px;
    color: #333;
    font-size: 16px;
    font-weight: 500;
  }
}

.video-preview-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.video-preview-item {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  padding: 12px;
  background: #fafafa;
  
  .video-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
    
    .video-name {
      font-size: 14px;
      color: #333;
      font-weight: 500;
      flex: 1;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    
    .video-status {
      font-size: 12px;
      padding: 2px 8px;
      border-radius: 12px;
      margin-left: 10px;
      
      &.uploading {
        background: #e6f7ff;
        color: #1890ff;
      }
      
      &.done {
        background: #f6ffed;
        color: #52c41a;
      }
      
      &.failed {
        background: #fff2f0;
        color: #ff4d4f;
      }
      
      &.default {
        background: #f5f5f5;
        color: #666;
      }
    }
  }
}

.video-player-container {
  width: 100%;
  position: relative;
  
  .video-preview-player {
    width: 100%;
    height: 200px;
    border-radius: 6px;
    background: #000;
    cursor: pointer;
    transition: transform 0.2s ease;
    pointer-events: none;
    
    &:hover {
      transform: scale(1.02);
    }
    
    &::-webkit-media-controls {
      background: rgba(0, 0, 0, 0.7);
    }
    
    &::-webkit-media-controls-panel {
      background: rgba(0, 0, 0, 0.7);
    }
  }
  
  .play-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.3);
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.3s ease;
    pointer-events: auto;
    
    &:hover {
      background: rgba(0, 0, 0, 0.5);
      
      .play-button {
        transform: scale(1.2);
        background: rgba(255, 107, 157, 0.9);
      }
    }
    
    .play-button {
      width: 60px;
      height: 60px;
      background: rgba(255, 107, 157, 0.8);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-size: 24px;
      transition: all 0.3s ease;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
    }
  }
}

.video-placeholder {
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f0f0;
  border-radius: 6px;
  border: 2px dashed #d9d9d9;
  
  .uploading-indicator {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    color: #666;
    
    span {
      font-size: 14px;
    }
  }
  
  .error-indicator {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    color: #ff4444;
    
    span {
      font-size: 14px;
    }
  }
}

/* 音乐预览区域样式 */
.music-preview-section {
  margin-top: 15px;
  
  h4 {
    margin-bottom: 10px;
    color: #333;
    font-size: 14px;
    font-weight: 500;
  }
  
  .music-preview-item {
    background: #f8f9fa;
    border-radius: 8px;
    padding: 15px;
    border: 1px solid #e9ecef;
    
    .music-info {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 10px;
      
      .music-name {
        font-size: 14px;
        color: #333;
        font-weight: 500;
      }
      
      .music-status {
        font-size: 12px;
        padding: 2px 8px;
        border-radius: 12px;
        
        &.uploading {
          background: #fff3cd;
          color: #856404;
        }
        
        &.done {
          background: #d4edda;
          color: #155724;
        }
        
        &.failed {
          background: #f8d7da;
          color: #721c24;
        }
      }
    }
    
    .music-player-container {
      audio {
        display: none;
      }
      
      .music-controls {
        display: flex;
        align-items: center;
        gap: 12px;
        
        .play-btn {
          width: 40px;
          height: 40px;
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
          
          &.playing {
            animation: pulse 2s infinite;
          }
        }
        
        .music-progress {
          flex: 1;
          
          .progress-bar {
            width: 100%;
            height: 4px;
            background: rgba(0, 0, 0, 0.1);
            border-radius: 2px;
            cursor: pointer;
            position: relative;
            margin-bottom: 4px;
            
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
    }
    
    .music-placeholder {
      height: 80px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: #f0f0f0;
      border-radius: 6px;
      border: 2px dashed #d9d9d9;
      
      .uploading-indicator {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 8px;
        color: #666;
        
        span {
          font-size: 14px;
        }
      }
      
      .error-indicator {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 8px;
        color: #ff4444;
        
        span {
          font-size: 14px;
        }
      }
    }
  }
}

@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
  100% {
    transform: scale(1);
  }
}

.submit-section {
  margin: 30px 16px;
  
  :deep(.van-button--primary) {
    background: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 100%) !important;
    border: none !important;
    height: 48px;
    border-radius: 24px;
    font-size: 16px;
    font-weight: 500;
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(255, 107, 157, 0.3);
      background: linear-gradient(135deg, #ff5a8c 0%, #ff7a9a 100%) !important;
    }
  }
}
</style> 