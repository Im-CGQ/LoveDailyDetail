<template>
  <div class="edit-movie">
    <BackButton />
    
    <div class="content">
      <h1 class="title">🎬 编辑电影</h1>
      
      <!-- Loading状态 -->
      <div v-if="loading" class="loading-container">
        <van-loading size="24px" color="#667eea">加载中...</van-loading>
      </div>
      
      <div class="form-container" v-if="!loading">
        <form @submit.prevent="handleEditMovie">
          <div class="form-group">
            <label>电影标题 *</label>
            <input 
              v-model="editMovie.title" 
              type="text" 
              placeholder="请输入电影标题"
              required 
            />
          </div>
          
          <div class="form-group">
            <label>电影描述</label>
            <textarea 
              v-model="editMovie.description" 
              rows="4"
              placeholder="请输入电影描述（可选）"
            ></textarea>
          </div>
          
          <div class="form-group">
            <label>电影文件 *</label>
            <van-uploader
              v-model="movieFiles"
              :max-count="1"
              accept="video/*"
              :after-read="afterMovieRead"
              :before-delete="beforeMovieDelete"
              @oversize="onMovieOversize"
            />
            <div class="upload-tips">
              <p>支持上传视频文件，大小不超过1.5GB</p>
              <p>支持格式：MP4, AVI, MOV, MKV等</p>
              <p>如果不重新上传视频，将保持原有视频文件</p>
            </div>
            
            <!-- 视频预览区域 -->
            <div class="video-preview-section" v-if="movieFiles.length > 0">
              <h4>视频预览</h4>
              <div class="video-preview-item" ref="videoPreviewSectionRef">
                <div class="video-info">
                  <span class="video-name">{{ getVideoName() }}</span>
                  <span class="video-status" :class="getVideoStatus()">
                    {{ getVideoStatusText() }}
                  </span>
                </div>
                <div class="video-player-container" v-if="getVideoUrl()" :style="getVideoStyle()">
                  <video 
                    :src="getVideoUrl()" 
                    :poster="generateVideoPoster(getVideoUrl(), getVideoData())"
                    class="video-preview-player"
                    :style="getVideoStyle()"
                    preload="none"
                    controls
                    @click="playVideo"
                  >
                    您的浏览器不支持视频播放
                  </video>
                </div>
                <div class="video-placeholder" v-else>
                  <div class="uploading-indicator" v-if="getVideoStatus() === 'uploading'">
                    <van-loading size="24px" color="#667eea">上传中...</van-loading>
                  </div>
                  <div class="error-indicator" v-else-if="getVideoStatus() === 'failed'">
                    <van-icon name="warning-o" color="#ff4444" size="24px" />
                    <span>上传失败</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <div class="form-group">
            <label>电影封面</label>
            <van-uploader
              v-model="coverFiles"
              :max-count="1"
              accept="image/*"
              :after-read="afterCoverRead"
              :before-delete="beforeCoverDelete"
              @oversize="onCoverOversize"
              preview-image
            />
            <div class="upload-tips">
              <p>支持上传图片文件，大小不超过20MB</p>
              <p>支持格式：JPG, PNG, GIF等</p>
              <p>如果不重新上传封面，将保持原有封面</p>
            </div>
          </div>
          
          <div class="form-group">
            <label class="checkbox-label">
              <input v-model="editMovie.isPublic" type="checkbox" />
              <span>公开（所有人都可以观看）</span>
            </label>
            <div class="privacy-tips">
              <p v-if="editMovie.isPublic">✅ 公开电影：所有用户都可以观看</p>
              <p v-else>🔒 私密电影：只有你和伴侣可以观看</p>
            </div>
          </div>
          
          <div class="form-actions">
            <button 
              type="submit" 
              class="submit-btn"
              :disabled="updating || hasUploadingFiles || !isFormValid"
            >
              {{ getSubmitButtonText() }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast } from 'vant'
import BackButton from '@/components/BackButton.vue'
import { getMovieById, updateMovie } from '@/api/movie.js'
import { uploadVideo, uploadImage } from '@/api/upload.js'

const route = useRoute()
const router = useRouter()

const updating = ref(false)
const loading = ref(false)
const movieFiles = ref([])
const coverFiles = ref([])
const videoPreviewSectionRef = ref(null)
const containerWidth = ref(400)

const editMovie = reactive({
  title: '',
  description: '',
  isPublic: false
})

// 更新容器宽度
const updateContainerWidth = () => {
  if (videoPreviewSectionRef.value) {
    containerWidth.value = videoPreviewSectionRef.value.offsetWidth
    containerWidth.value = Math.max(300, Math.min(containerWidth.value, 800))
  }
}

// 表单验证
const isFormValid = computed(() => {
  return editMovie.title.trim() !== '' && movieFiles.value.length > 0
})

// 计算是否有正在上传的文件
const hasUploadingFiles = computed(() => {
  const uploadingMovies = movieFiles.value.some(file => file.status === 'uploading')
  const uploadingCovers = coverFiles.value.some(file => file.status === 'uploading')
  return uploadingMovies || uploadingCovers
})

// 获取提交按钮文本
const getSubmitButtonText = () => {
  if (updating.value) {
    return '更新中...'
  }
  if (hasUploadingFiles.value) {
    return '等待上传完成'
  }
  if (!isFormValid.value) {
    return '请填写必填信息'
  }
  return '更新电影'
}

// 加载电影数据
const loadMovie = async () => {
  loading.value = true
  try {
    const movieId = route.params.id
    console.log('开始加载电影数据，ID:', movieId)
    const movieData = await getMovieById(movieId)
    
    // 填充表单数据
    editMovie.title = movieData.title || ''
    editMovie.description = movieData.description || ''
    editMovie.isPublic = movieData.isPublic || false
    
    // 如果有视频文件，添加到movieFiles
    if (movieData.movieUrl) {
      movieFiles.value = [{
        url: movieData.movieUrl,
        fileName: movieData.fileName || movieData.movieUrl.split('/').pop() || 'original-movie.mp4',
        status: 'done',
        message: '原有视频',
        width: movieData.width || 0,
        height: movieData.height || 0,
        duration: movieData.durationMinutes ? movieData.durationMinutes * 60 : null,
        fileSize: movieData.fileSize || 0
      }]
    }
    
    // 如果有封面，添加到coverFiles
    if (movieData.coverUrl) {
      coverFiles.value = [{
        url: movieData.coverUrl,
        fileName: movieData.coverFileName || movieData.coverUrl.split('/').pop() || 'original-cover.jpg',
        status: 'done',
        message: '原有封面'
      }]
    }
    
    loading.value = false
    console.log('电影数据加载完成:', movieData)
    console.log('回显的movieFiles:', movieFiles.value)
    console.log('回显的coverFiles:', coverFiles.value)
  } catch (error) {
    console.error('加载电影数据失败:', error)
    showToast('加载电影数据失败')
    router.push('/movies')
  }
}

// 获取视频名称
const getVideoName = () => {
  if (movieFiles.value.length > 0) {
    return movieFiles.value[0].fileName || movieFiles.value[0].file?.name || '电影文件'
  }
  return '电影文件'
}

// 获取视频状态
const getVideoStatus = () => {
  if (movieFiles.value.length > 0) {
    return movieFiles.value[0].status || 'default'
  }
  return 'default'
}

// 获取视频状态文本
const getVideoStatusText = () => {
  const status = getVideoStatus()
  switch (status) {
    case 'uploading':
      return '上传中...'
    case 'done':
      return movieFiles.value[0]?.file ? '上传成功' : '原有视频'
    case 'failed':
      return '上传失败'
    default:
      return '待上传'
  }
}

// 获取视频URL
const getVideoUrl = () => {
  if (movieFiles.value.length > 0 && movieFiles.value[0].url) {
    return movieFiles.value[0].url
  }
  return ''
}

// 获取视频数据
const getVideoData = () => {
  if (movieFiles.value.length > 0) {
    return movieFiles.value[0]
  }
  return null
}

// 电影文件上传处理
const afterMovieRead = async (file) => {
  if (Array.isArray(file)) {
    for (const singleFile of file) {
      await processMovieFile(singleFile)
    }
  } else {
    await processMovieFile(file)
  }
}

const processMovieFile = async (file) => {
  try {
    // 检查文件大小
    if (file.file.size > 1.5 * 1024 * 1024 * 1024) {
      showToast('视频大小不能超过1.5GB')
      const index = movieFiles.value.findIndex(f => f.file === file.file)
      if (index > -1) {
        movieFiles.value.splice(index, 1)
      }
      return
    }
    
    // 检查文件类型
    if (!file.file.type.startsWith('video/')) {
      showToast('只能上传视频文件')
      const index = movieFiles.value.findIndex(f => f.file === file.file)
      if (index > -1) {
        movieFiles.value.splice(index, 1)
      }
      return
    }
    
    // 显示上传进度
    file.status = 'uploading'
    file.message = '上传中...'
    
    // 获取视频尺寸和时长
    const videoInfo = await getVideoInfo(file.file)
    
    const url = await uploadVideo(file.file)
    file.url = url
    file.fileName = file.file.name
    file.fileSize = file.file.size
    file.width = videoInfo.width
    file.height = videoInfo.height
    file.duration = videoInfo.duration
    file.status = 'done'
    file.message = '上传成功'
    showToast('视频上传成功')
  } catch (error) {
    console.error('视频上传失败:', error)
    file.status = 'failed'
    file.message = '上传失败'
    showToast('视频上传失败')
    const index = movieFiles.value.findIndex(f => f.file === file.file)
    if (index > -1) {
      movieFiles.value.splice(index, 1)
    }
  }
}

const beforeMovieDelete = (file) => {
  return new Promise(resolve => {
    resolve(true)
  })
}

const onMovieOversize = (file) => {
  showToast('视频大小不能超过1.5GB')
  return false
}

// 视频播放方法
const playVideo = () => {
  const videoUrl = getVideoUrl()
  if (!videoUrl) {
    return
  }
  
  const videoElement = document.querySelector('.video-preview-player')
  
  if (videoElement) {
    if (videoElement.paused) {
      videoElement.play().catch(error => {
        console.error('视频播放失败:', error)
        showToast('视频播放失败')
      })
    } else {
      videoElement.pause()
    }
  }
}

// 获取视频信息（尺寸和时长）
const getVideoInfo = (file) => {
  return new Promise((resolve, reject) => {
    const video = document.createElement('video')
    video.onloadedmetadata = () => {
      resolve({
        width: video.videoWidth,
        height: video.videoHeight,
        duration: Math.round(video.duration)
      })
      URL.revokeObjectURL(video.src)
    }
    video.onerror = () => {
      reject(new Error('无法获取视频信息'))
    }
    video.src = URL.createObjectURL(file)
  })
}

// 生成视频封面URL
const generateVideoPoster = (videoUrl, video) => {
  if (!videoUrl) return ''
  
  if (videoUrl.includes('aliyuncs.com') || videoUrl.includes('oss-')) {
    let posterWidth = 800
    let posterHeight = 600
    
    if (video && video.width && video.height) {
      const aspectRatio = video.width / video.height
      posterWidth = 800
      posterHeight = Math.round(800 / aspectRatio)
    }
    
    return videoUrl + `?x-oss-process=video/snapshot,t_1000,f_jpg,w_${posterWidth},h_${posterHeight},m_fast`
  }
  
  return videoUrl
}

// 获取视频自适应样式
const getVideoStyle = () => {
  const videoData = getVideoData()
  if (!videoData || !videoData.width || !videoData.height) {
    return {
      width: '100%',
      height: '300px'
    }
  }
  
  const aspectRatio = videoData.width / videoData.height
  const height = containerWidth.value / aspectRatio
  
  return {
    width: '100%',
    height: `${height}px`,
    objectFit: 'cover'
  }
}

// 封面文件上传处理
const afterCoverRead = async (file) => {
  if (Array.isArray(file)) {
    for (const singleFile of file) {
      await processCoverFile(singleFile)
    }
  } else {
    await processCoverFile(file)
  }
}

const processCoverFile = async (file) => {
  try {
    if (file.file.size > 20 * 1024 * 1024) {
      showToast('图片大小不能超过20MB')
      const index = coverFiles.value.findIndex(f => f.file === file.file)
      if (index > -1) {
        coverFiles.value.splice(index, 1)
      }
      return
    }
    
    if (!file.file.type.startsWith('image/')) {
      showToast('只能上传图片文件')
      const index = coverFiles.value.findIndex(f => f.file === file.file)
      if (index > -1) {
        coverFiles.value.splice(index, 1)
      }
      return
    }
    
    file.status = 'uploading'
    file.message = '上传中...'
    
    const url = await uploadImage(file.file)
    file.url = url
    file.fileName = file.file.name
    file.status = 'done'
    file.message = '上传成功'
    showToast('封面上传成功')
  } catch (error) {
    console.error('封面上传失败:', error)
    file.status = 'failed'
    file.message = '上传失败'
    showToast('封面上传失败')
    const index = coverFiles.value.findIndex(f => f.file === file.file)
    if (index > -1) {
      coverFiles.value.splice(index, 1)
    }
  }
}

const beforeCoverDelete = (file) => {
  return new Promise(resolve => {
    resolve(true)
  })
}

const onCoverOversize = (file) => {
  showToast('图片大小不能超过20MB')
  return false
}

const handleEditMovie = async () => {
  if (!isFormValid.value) {
    showToast('请填写必填信息')
    return
  }
  
  const uploadingMovies = movieFiles.value.some(file => file.status === 'uploading')
  const uploadingCovers = coverFiles.value.some(file => file.status === 'uploading')
  
  if (uploadingMovies || uploadingCovers) {
    showToast('请等待文件上传完成后再提交')
    return
  }
  
  updating.value = true
  try {
    const movieData = {
      title: editMovie.title.trim(),
      description: editMovie.description.trim(),
      isPublic: editMovie.isPublic
    }
    
    // 处理视频文件数据
    if (movieFiles.value.length > 0) {
      const movieFile = movieFiles.value[0]
      if (movieFile.status === 'done') {
        if (movieFile.file) {
          movieData.movieUrl = movieFile.url
          movieData.fileName = movieFile.fileName
          movieData.fileSize = movieFile.fileSize
          movieData.width = movieFile.width
          movieData.height = movieFile.height
          movieData.durationMinutes = movieFile.duration ? Math.round(movieFile.duration / 60) : null
        } else {
          movieData.movieUrl = movieFile.url
          movieData.fileName = movieFile.fileName
          movieData.fileSize = movieFile.fileSize
          movieData.width = movieFile.width
          movieData.height = movieFile.height
          movieData.durationMinutes = movieFile.duration ? Math.round(movieFile.duration / 60) : null
        }
      }
    }
    
    // 处理封面文件数据
    if (coverFiles.value.length > 0) {
      const coverFile = coverFiles.value[0]
      if (coverFile.status === 'done') {
        if (coverFile.file) {
          movieData.coverUrl = coverFile.url
        } else {
          movieData.coverUrl = coverFile.url
        }
      }
    }
    
    console.log('提交的电影数据:', movieData)
    const movieId = route.params.id
    await updateMovie(movieId, movieData)
    
    showToast('电影更新成功')
    router.push('/movies')
  } catch (error) {
    console.error('更新电影失败:', error)
    showToast(error.message || '更新失败，请重试')
  } finally {
    updating.value = false
  }
}

// 监听movieFiles变化，在DOM更新后更新容器宽度
watch(movieFiles, () => {
  if (movieFiles.value.length > 0) {
    nextTick(() => {
      updateContainerWidth()
    })
  }
}, { immediate: true })

onMounted(() => {
  console.log('EditMovie组件挂载，开始加载电影数据...')
  console.log('路由参数:', route.params)
  loadMovie()
  updateContainerWidth()
})

</script>

<style scoped>
.edit-movie {
  padding: 20px;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.content {
  max-width: 800px;
  margin: 0 auto;
  padding-top: 60px;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
  background: rgba(255,255,255,0.95);
  border-radius: 20px;
  backdrop-filter: blur(10px);
  box-shadow: 0 10px 30px rgba(0,0,0,0.2);
}

.title {
  text-align: center;
  color: white;
  font-size: 2.5rem;
  margin-bottom: 30px;
  text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
}

.form-container {
  background: rgba(255,255,255,0.95);
  border-radius: 20px;
  padding: 40px;
  backdrop-filter: blur(10px);
  box-shadow: 0 10px 30px rgba(0,0,0,0.2);
}

.form-group {
  margin-bottom: 30px;
}

.form-group label {
  display: block;
  margin-bottom: 10px;
  color: #333;
  font-weight: 600;
  font-size: 16px;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 15px;
  border: 2px solid #e1e5e9;
  border-radius: 12px;
  font-size: 16px;
  transition: all 0.3s;
  background: white;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}

.upload-tips {
  margin-top: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #667eea;
}

.upload-tips p {
  margin: 5px 0;
  color: #666;
  font-size: 14px;
}

.checkbox-label {
  display: flex !important;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  font-weight: normal !important;
}

.checkbox-label input[type="checkbox"] {
  width: auto !important;
  margin: 0;
  transform: scale(1.2);
}

.privacy-tips {
  margin-top: 10px;
  padding: 10px;
  background: #e8f5e8;
  border-radius: 8px;
  border-left: 4px solid #4caf50;
}

.privacy-tips p {
  margin: 0;
  color: #2e7d32;
  font-size: 14px;
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
    border-radius: 6px;
    background: #000;
    display: block;
    cursor: pointer;
    overflow: hidden;
    width: 100%;
    height: auto;
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

.form-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
  margin-top: 40px;
}

.submit-btn {
  padding: 15px 30px;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  min-width: 120px;
}

.submit-btn {
  background: #667eea;
  color: white;
}

.submit-btn:hover:not(:disabled) {
  background: #5a6fd8;
  transform: translateY(-2px);
}

.submit-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
}

@media (max-width: 768px) {
  .content {
    padding-top: 40px;
  }
  
  .title {
    font-size: 2rem;
  }
  
  .form-container {
    padding: 25px;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .submit-btn {
    width: 100%;
  }
}
</style>
