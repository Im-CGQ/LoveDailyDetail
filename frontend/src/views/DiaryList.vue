<template>
  <div class="diary-list-page romantic-bg page-container">
    <!-- 返回按钮 -->
    <BackButton />
    
    <!-- 爱心装饰 -->
    <div class="heart-decoration heart-1">💕</div>
    <div class="heart-decoration heart-2">💖</div>
    <div class="heart-decoration heart-3">💝</div>

    <div class="content">
      <div class="header float">
        <h2 class="page-title">{{ formatDate(selectedDate) }} 的日记</h2>
        <p class="subtitle pulse">记录我们的每一个美好瞬间</p>
                 <div class="diary-count">
           <span class="count-label">共</span>
           <span class="count-number">{{ diaries.length }}</span>
           <span class="count-label">条日记</span>
         </div>
      </div>
      
      <div v-if="loading" class="loading">
        <div class="spinner"></div>
        <p>加载中...</p>
      </div>
      
      <div v-else-if="error" class="error-state">
        <div class="error-icon">❌</div>
        <h3>加载失败</h3>
        <p>{{ error }}</p>
        <button @click="loadDiaries" class="btn-primary">重试</button>
      </div>
      
      <div v-else class="diary-list">
        <div v-if="diaries.length === 0" class="empty-state">
          <div class="empty-icon heartbeat">💕</div>
          <p class="empty-text">该日期暂无日记</p>
          <van-button 
            type="primary" 
            size="large" 
            @click="createNewDiary"
            class="create-btn btn-primary"
          >
            <span class="btn-icon">✨</span>
            创建第一个回忆
          </van-button>
        </div>
        
        <div v-else class="diary-items">
          <div 
            v-for="diary in diaries" 
            :key="diary.id" 
            class="diary-item hover-lift glow"
            @click="viewDiaryDetail(diary.id)"
          >
            <div class="diary-content">
              <div class="diary-info">
                <div class="diary-date">
                  <span class="date-number">{{ formatTime(diary.createdAt) }}</span>
                  <span class="date-icon">💕</span>
                </div>
                <div class="diary-title">{{ diary.title }}</div>
                <div class="diary-desc">{{ diary.description ? diary.description.substring(0, 100) + '...' : '暂无描述' }}</div>
                
                <!-- 媒体标记 -->
                <div class="media-badges">
                  <div v-if="diary.images && diary.images.length > 0" class="media-badge image-badge" title="包含图片">
                    <van-icon name="photo-o" />
                    <span>{{ diary.images.length }} 张图片</span>
                  </div>
                  <div v-if="diary.videos && diary.videos.length > 0" class="media-badge video-badge" title="包含视频">
                    <van-icon name="video-o" />
                    <span>{{ diary.videos.length }} 个视频</span>
                  </div>
                  <div v-if="diary.backgroundMusic && diary.backgroundMusic.length > 0" class="media-badge music-badge" title="包含音乐">
                    <van-icon name="music-o" />
                    <span>音乐</span>
                  </div>
                </div>
              </div>
              
              <div class="diary-media" v-if="diary.images && diary.images.length > 0">
                <img 
                  :src="diary.images[0].imageUrl" 
                  :alt="diary.title" 
                  class="diary-image" 
                  @click.stop="previewImage(diary.images)"
                />
              </div>
            </div>
            
            <div class="diary-arrow">
              <van-icon name="arrow" class="arrow-icon" />
            </div>
          </div>
        </div>
      </div>
      
      <div class="page-actions">
        <van-button 
          type="primary" 
          size="large" 
          @click="createNewDiary"
          class="create-btn btn-primary"
        >
          <span class="btn-icon">✨</span>
          创建新日记
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast, showImagePreview } from 'vant'
import { getDiariesByDate } from '@/api/diary'
import BackButton from '@/components/BackButton.vue'

const route = useRoute()
const router = useRouter()

const selectedDate = ref('')
const diaries = ref([])
const loading = ref(false)
const error = ref(null)



const loadDiaries = async () => {
  if (!selectedDate.value) {
    error.value = '日期参数无效'
    return
  }
  
  loading.value = true
  error.value = null
  
  try {
    const response = await getDiariesByDate(selectedDate.value)
    
    // 确保response是数组
    if (Array.isArray(response)) {
      diaries.value = response
    } else if (response && Array.isArray(response.data)) {
      diaries.value = response.data
    } else {
      diaries.value = []
    }
  } catch (err) {
    console.error('加载日记失败:', err)
    error.value = err.message || '加载日记失败'
    
    if (err.message && err.message.includes('401')) {
      router.push('/login')
    } else {
      showToast('加载日记失败')
    }
  } finally {
    loading.value = false
  }
}

const formatDate = (dateString) => {
  if (!dateString) return '未知日期'
  
  try {
    const date = new Date(dateString)
    if (isNaN(date.getTime())) return '无效日期'
    
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}年${month}月${day}日`
  } catch (err) {
    console.error('日期格式化错误:', err)
    return '日期错误'
  }
}

const formatTime = (dateTimeString) => {
  if (!dateTimeString) return ''
  
  try {
    const date = new Date(dateTimeString)
    if (isNaN(date.getTime())) return ''
    
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    return `${hours}:${minutes}`
  } catch (err) {
    console.error('时间格式化错误:', err)
    return ''
  }
}

const viewDiaryDetail = (diaryId) => {
  router.push(`/diary/${diaryId}`)
}

const createNewDiary = () => {
  router.push({
    path: '/admin/diary/create',
    query: { date: selectedDate.value }
  })
}

// 图片预览功能
const previewImage = (images) => {
  if (images && images.length > 0) {
    const imageUrls = images.map(image => image.imageUrl)
    showImagePreview({
      images: imageUrls,
      startPosition: 0,
      closeable: true,
      closeIconPosition: 'top-right',
      showIndex: true,
      swipeDuration: 300,
      showIndicators: true,
      indicatorColor: '#ff6b9d'
    })
  }
}

onMounted(() => {
  selectedDate.value = route.params.date
  
  if (selectedDate.value) {
    loadDiaries()
  } else {
    error.value = '缺少日期参数'
  }
})
</script>

<style scoped>
.diary-list-page {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  position: relative;
  min-height: 100vh;
}



.content {
  padding: 20px;
  padding-top: 100px;
  position: relative;
  z-index: 2;
  padding-bottom: 40px;
}

.header {
  text-align: center;
  margin-bottom: 30px;
}

.page-title {
  color: white;
  font-size: 28px;
  font-weight: bold;
  margin: 0 0 10px 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.subtitle {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 15px;
}

.diary-count {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 10px;
  padding: 8px 16px;
  transition: all 0.3s ease;
}

.diary-count:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 255, 255, 0.2);
}

.count-label {
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  font-weight: 500;
}

.count-number {
  color: #ff6b9d;
  font-size: 18px;
  font-weight: 700;
  text-shadow: 0 2px 4px rgba(255, 107, 157, 0.3);
  animation: pulse 2s ease-in-out infinite;
  min-width: 24px;
  text-align: center;
}

.loading {
  text-align: center;
  padding: 60px 20px;
  color: white;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(255, 255, 255, 0.3);
  border-top: 4px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-state {
  text-align: center;
  padding: 60px 20px;
  color: white;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 20px;
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.error-icon {
  font-size: 48px;
  margin-bottom: 20px;
}

.error-state h3 {
  margin: 0 0 10px 0;
  color: #ff6b9d;
}

.error-state p {
  margin: 0 0 20px 0;
  color: rgba(255, 255, 255, 0.8);
}

.diary-list {
  margin-bottom: 30px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 20px;
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 20px;
}

.empty-text {
  color: rgba(255, 255, 255, 0.8);
  font-size: 18px;
  margin-bottom: 30px;
}

.diary-items {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.diary-item {
  background: rgba(255, 255, 255, 0.15);
  border-radius: 20px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  overflow: hidden;
}

.diary-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.diary-item:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-5px) scale(1.02);
}

.diary-item:hover::before {
  left: 100%;
}

.diary-item:hover .arrow-icon {
  transform: translateX(5px);
}

.diary-content {
  display: flex;
  gap: 15px;
  align-items: center;
}

.diary-info {
  flex: 1;
}

.diary-date {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.date-number {
  color: #ff6b9d;
  font-size: 18px;
  font-weight: bold;
}

.date-icon {
  font-size: 16px;
  animation: heartbeat 2s ease-in-out infinite;
}

.diary-title {
  color: white;
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 8px;
}

.diary-desc {
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  margin-bottom: 10px;
}

.media-badges {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.media-badge {
  display: flex;
  align-items: center;
  gap: 3px;
  padding: 3px 6px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 500;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.media-badge .van-icon {
  font-size: 12px;
}

.media-badge span {
  color: white;
  font-weight: 600;
}

.media-badge.image-badge {
  background: rgba(52, 152, 219, 0.3);
  border-color: rgba(52, 152, 219, 0.5);
}

.media-badge.image-badge:hover {
  background: rgba(52, 152, 219, 0.4);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(52, 152, 219, 0.3);
}

.media-badge.video-badge {
  background: rgba(255, 71, 87, 0.3);
  border-color: rgba(255, 71, 87, 0.5);
}

.media-badge.video-badge:hover {
  background: rgba(255, 71, 87, 0.4);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(255, 71, 87, 0.3);
}

.media-badge.music-badge {
  background: rgba(46, 213, 115, 0.3);
  border-color: rgba(46, 213, 115, 0.5);
}

.media-badge.music-badge:hover {
  background: rgba(46, 213, 115, 0.4);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(46, 213, 115, 0.3);
}

.diary-media {
  flex-shrink: 0;
}

.diary-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  cursor: pointer;
  transition: transform 0.3s ease;
}

.diary-image:hover {
  transform: scale(1.05);
}

.diary-arrow {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
}

.arrow-icon {
  color: rgba(255, 255, 255, 0.6);
  font-size: 20px;
  transition: transform 0.3s ease;
}

.page-actions {
  text-align: center;
  padding: 20px 0;
}

.create-btn {
  height: 56px;
  border-radius: 28px;
  font-size: 18px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  background: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 100%);
  border: none;
  box-shadow: 0 4px 16px rgba(255, 107, 157, 0.3);
}

.create-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 157, 0.4);
}

.btn-icon {
  font-size: 20px;
}

/* 动画效果 */
@keyframes heartbeat {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}

@media (max-width: 768px) {
  
  .content {
    padding: 15px;
    padding-top: 80px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .diary-count {
    padding: 6px 12px;
    gap: 6px;
    
    .count-label {
      font-size: 12px;
    }
    
    .count-number {
      font-size: 16px;
      padding: 1px 6px;
      min-width: 20px;
    }
  }
  
  .diary-item {
    padding: 15px;
  }
  
  .diary-content {
    gap: 12px;
  }
  
  .diary-info .diary-title {
    font-size: 16px;
  }
  
  .diary-media .diary-image {
    width: 60px;
    height: 60px;
  }
  
  .create-btn {
    height: 48px;
    font-size: 16px;
  }
}
</style>
