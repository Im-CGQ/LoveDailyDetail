<template>
  <div class="home romantic-bg page-container">
    <!-- 爱心装饰 -->
    <div class="heart-decoration heart-1">💕</div>
    <div class="heart-decoration heart-2">💖</div>
    <div class="heart-decoration heart-3">💝</div>
    
    <!-- 飘落花瓣 -->
    <div class="falling-petals" v-if="isAnimationActive">
      <div v-for="petal in petalStyles" :key="petal.id" class="falling-petal" :style="petal.style">
        {{ petal.emoji }}
      </div>
    </div>

    <div class="content" v-if="currentDiary">
      <div class="title-section float">
        <h1 class="main-title text-gradient-romantic">{{ currentDiary.title }}</h1>
        <p class="subtitle pulse">{{ formatDate(currentDiary.date) }}</p>
        <div class="love-counter">
          <span class="counter-number">{{ loveCount }}</span>
          <span class="counter-text">天</span>
          <span class="counter-seconds">{{ loveSeconds }}</span>
        </div>
      </div>

      <div class="media-section hover-lift">
                 <van-swipe 
           v-if="currentDiary.images && currentDiary.images.length > 0"
           class="image-swipe glow"
           :autoplay="4000"
           indicator-color="#ff6b9d"
         >
           <van-swipe-item v-for="(image, index) in currentDiary.images" :key="index">
             <img 
               :src="image" 
               :alt="`回忆图片 ${index + 1}`" 
               class="memory-image" 
               @click="previewImage(index)"
             />
           </van-swipe-item>
         </van-swipe>
      </div>

      <div class="description-section">
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

      <div class="action-section">
        <van-button 
          type="primary" 
          size="large" 
          @click="goToCalendar"
          class="action-btn btn-primary ripple"
        >
          <span class="btn-icon">📅</span>
          查看时光日历
        </van-button>
        
        <van-button 
          type="default" 
          size="large" 
          @click="shareMemory"
          class="action-btn share-btn"
        >
          <span class="btn-icon">💌</span>
          分享美好回忆
        </van-button>
        
        <van-button 
          type="default" 
          size="large" 
          @click="$router.push('/test-scroll')"
          class="action-btn test-btn"
        >
          <span class="btn-icon">🧪</span>
          测试滚动
        </van-button>
        
        <van-button 
          type="default" 
          size="large" 
          @click="$router.push('/login?mode=admin')"
          class="action-btn admin-btn"
        >
          <span class="btn-icon">⚙️</span>
          后台管理
        </van-button>
        
        <van-button 
          type="default" 
          size="large" 
          @click="$router.push('/test-login')"
          class="action-btn test-login-btn"
        >
          <span class="btn-icon">🔍</span>
          测试登录
        </van-button>
      </div>
    </div>

    <div v-else class="loading-section">
      <div class="loading-heart heartbeat">💕</div>
      <p class="loading-text">正在加载美好回忆...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, onActivated, onDeactivated } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showImagePreview } from 'vant'
import dayjs from 'dayjs'
import { getLatestDiary } from '@/api/diary'

const router = useRouter()
const currentDiary = ref(null)
const loveCount = ref('')
const loveSeconds = ref('')
const displayText = ref('')
const typingComplete = ref(false)
let timer = null
let typingTimer = null

// 设置在一起的日期时间（可以修改这个日期）
// 格式：YYYY-MM-DD HH:mm:ss
// 例如：'2024-01-15 14:30:00' 表示2024年1月15日下午2点30分
const togetherDate = '2024-01-15 14:30:00'

// 计算在一起的时间
const calculateLoveTime = () => {
  const now = dayjs()
  const startDate = dayjs(togetherDate)
  const diff = now.diff(startDate, 'second')
  
  const days = Math.floor(diff / (24 * 60 * 60))
  const hours = Math.floor((diff % (24 * 60 * 60)) / (60 * 60))
  const minutes = Math.floor((diff % (60 * 60)) / 60)
  const seconds = diff % 60
  
  loveCount.value = days
  loveSeconds.value = `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
}

// 启动定时器，每秒更新一次
const startTimer = () => {
  calculateLoveTime() // 立即计算一次
  timer = setInterval(calculateLoveTime, 1000) // 每秒更新一次
}

// 停止定时器
const stopTimer = () => {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
}

const formatDate = (date) => {
  return dayjs(date).format('YYYY年MM月DD日')
}

const goToCalendar = () => {
  router.push('/calendar')
}

const shareMemory = () => {
  showToast('分享功能开发中...')
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
  if (currentDiary.value && currentDiary.value.description) {
    displayText.value = currentDiary.value.description
    typingComplete.value = true
    if (typingTimer) {
      clearTimeout(typingTimer)
    }
  }
}

// 图片预览功能
const previewImage = (index) => {
  if (currentDiary.value && currentDiary.value.images) {
    showImagePreview({
      images: currentDiary.value.images,
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

// 花瓣数据 - 使用computed缓存，避免重复计算
// 动画控制状态
const isAnimationActive = ref(true)

// 检测设备性能，自动调整花瓣数量
const getPetalCount = () => {
  // 检测是否为低性能设备
  const isLowPerformance = navigator.hardwareConcurrency <= 4 || 
                          navigator.deviceMemory <= 4 ||
                          /Android|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)
  
  return isLowPerformance ? 4 : 8
}

// 花瓣数据 - 使用computed缓存，避免重复计算
const petalStyles = computed(() => {
  const petals = ['🌸', '🌺', '🌷', '🌹', '🌻', '🌼', '💐', '🌿']
  const petalCount = getPetalCount()
  
  return Array.from({ length: petalCount }, (_, index) => {
    // 使用固定的随机种子，确保每次返回相同的结果
    const seed = index + 1
    const random = (min, max) => {
      const x = Math.sin(seed) * 10000
      return min + (x - Math.floor(x)) * (max - min)
    }
    
    return {
      id: index,
      emoji: petals[index % petals.length],
      style: {
        left: `${random(0, 100)}%`,
        animationDelay: `${random(0, 10)}s`,
        animationDuration: `${random(8, 14)}s`,
        fontSize: `${random(16, 24)}px`,
        animationPlayState: isAnimationActive.value ? 'running' : 'paused'
      }
    }
  })
})

const loadLatestDiary = async () => {
  try {
    const diary = await getLatestDiary()
    if (diary) {
      currentDiary.value = diary
      
      // 启动打字机效果
      if (currentDiary.value.description) {
        startTyping(currentDiary.value.description)
      }
    } else {
      showToast('没有找到日记数据')
    }
  } catch (error) {
    console.error('加载日记失败:', error)
    showToast('加载失败，请稍后重试')
  }
}

onMounted(() => {
  loadLatestDiary()
  startTimer() // 启动计时器
})

onUnmounted(() => {
  stopTimer() // 组件卸载时停止计时器
  if (typingTimer) {
    clearTimeout(typingTimer) // 清理打字机定时器
  }
})

// 页面激活时恢复动画
onActivated(() => {
  isAnimationActive.value = true
})

// 页面失活时暂停动画
onDeactivated(() => {
  isAnimationActive.value = false
})
</script>

<style lang="scss" scoped>
.home {
  padding: 20px;
  position: relative;
}

.content {
  max-width: 600px;
  margin: 0 auto;
  position: relative;
  z-index: 2;
  padding-bottom: 40px;
}

.title-section {
  text-align: center;
  margin-bottom: 30px;
  
  .main-title {
    font-size: 32px;
    font-weight: bold;
    margin-bottom: 15px;
  }
  
  .subtitle {
    font-size: 18px;
    color: white;
    opacity: 0.9;
    margin-bottom: 20px;
  }
  
  .love-counter {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    background: rgba(255, 255, 255, 0.15);
    padding: 12px 20px;
    border-radius: 25px;
    backdrop-filter: blur(15px);
    border: 2px solid rgba(255, 255, 255, 0.2);
    box-shadow: 0 6px 24px rgba(255, 107, 157, 0.2);
    transition: all 0.3s ease;
    margin: 0 auto;
    width: fit-content;
    
    &:hover {
      background: rgba(255, 255, 255, 0.2);
      border-color: rgba(255, 255, 255, 0.3);
      box-shadow: 0 8px 32px rgba(255, 107, 157, 0.3);
      transform: translateY(-2px);
    }
    
    .counter-number {
      font-size: 24px;
      font-weight: 800;
      color: #ff6b9d;
      text-shadow: 0 3px 6px rgba(255, 107, 157, 0.4);
      font-family: 'Arial', sans-serif;
      letter-spacing: 1px;
      background: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
    
    .counter-text {
      font-size: 16px;
      color: white;
      font-weight: 600;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
    }
    
    .counter-seconds {
      font-size: 14px;
      color: rgba(255, 255, 255, 0.9);
      font-weight: 500;
      font-family: 'Courier New', monospace;
      background: rgba(255, 255, 255, 0.1);
      padding: 3px 6px;
      border-radius: 6px;
      border: 1px solid rgba(255, 255, 255, 0.2);
    }
  }
}

.media-section {
  margin-bottom: 30px;
  
  .image-swipe {
    border-radius: 20px;
    overflow: hidden;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
    
         .memory-image {
       width: 100%;
       height: 300px;
       object-fit: cover;
       cursor: pointer;
       transition: transform 0.3s ease;
       
       &:hover {
         transform: scale(1.02);
       }
     }
  }
}

.description-section {
  margin-bottom: 30px;
  
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

.action-section {
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
    
    &.test-btn {
      background: rgba(255, 193, 7, 0.2);
      border: 2px solid rgba(255, 193, 7, 0.3);
      color: white;
      backdrop-filter: blur(10px);
      
      &:hover {
        background: rgba(255, 193, 7, 0.3);
        transform: translateY(-2px);
      }
    }
    
         &.admin-btn {
       background: rgba(102, 126, 234, 0.2);
       border: 2px solid rgba(102, 126, 234, 0.3);
       color: white;
       backdrop-filter: blur(10px);
       
       &:hover {
         background: rgba(102, 126, 234, 0.3);
         transform: translateY(-2px);
       }
     }
     
     &.test-login-btn {
       background: rgba(34, 197, 94, 0.2);
       border: 2px solid rgba(34, 197, 94, 0.3);
       color: white;
       backdrop-filter: blur(10px);
       
       &:hover {
         background: rgba(34, 197, 94, 0.3);
         transform: translateY(-2px);
       }
     }
  }
}

.loading-section {
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
  .home {
    padding: 15px;
  }
  
  .title-section .main-title {
    font-size: 28px;
  }
  
  .media-section .image-swipe .memory-image {
    height: 250px;
  }
  
  .action-section .action-btn {
    height: 48px;
    font-size: 16px;
  }
}


</style> 