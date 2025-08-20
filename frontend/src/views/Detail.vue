<template>
  <div class="detail romantic-bg page-container">
    <!-- 爱心装饰 -->
    <div class="heart-decoration heart-1">💕</div>
    <div class="heart-decoration heart-2">💖</div>
    <div class="heart-decoration heart-3">💝</div>
    
    <van-nav-bar title="美好回忆" left-arrow @click-left="$router.back()" class="nav-bar" />
    
    <div class="content" v-if="diary">
      <div class="header float">
        <h1 class="title text-gradient-romantic">{{ diary.title }}</h1>
        <p class="date pulse">{{ formatDate(diary.date) }}</p>
      </div>

      <div class="media hover-lift">
                 <van-swipe v-if="diary.images && diary.images.length > 0" class="swipe glow">
           <van-swipe-item v-for="(image, index) in diary.images" :key="index">
             <img 
               :src="image" 
               class="image" 
               @click="previewImage(index)"
             />
           </van-swipe-item>
         </van-swipe>
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
          分享回忆
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
import { getDiaryByDate } from '@/api/diary'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const diary = ref(null)
const displayText = ref('')
const typingComplete = ref(false)
let typingTimer = null

const formatDate = (date) => {
  return dayjs(date).format('YYYY年MM月DD日')
}

const share = () => {
  showToast('分享功能开发中...')
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

const loadDiary = async () => {
  const date = route.params.date
  try {
    const diaryData = await getDiaryByDate(date)
    diary.value = diaryData
    
    // 启动打字机效果
    if (diary.value && diary.value.description) {
      startTyping(diary.value.description)
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
})
</script>

<style scoped>
.detail {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  position: relative;
}

.nav-bar {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(15px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  
  :deep(.van-nav-bar__title) {
    color: white !important;
    font-weight: bold;
    font-size: 18px;
  }
  
  :deep(.van-icon) {
    color: white !important;
    font-size: 20px;
  }
  
  :deep(.van-nav-bar__text) {
    color: white !important;
  }
  
  :deep(.van-nav-bar__content) {
    padding: 12px 16px;
  }
  
  :deep(.van-nav-bar__left) {
    .van-icon {
      color: white !important;
    }
  }
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
  
  .media .swipe .image {
    height: 240px;
  }
  
  .actions .action-btn {
    height: 48px;
    font-size: 16px;
  }
}


</style> 