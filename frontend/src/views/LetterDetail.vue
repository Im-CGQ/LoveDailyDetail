<template>
  <div class="letter-detail-page">
    <div class="letter-paper" v-if="letter">
      <div class="back-button">
        <van-icon name="arrow-left" @click="goBack" />
      </div>
      <div class="paper-border">
        <div class="paper-content">
          <div class="letter-header">
            <div class="letter-title">
              <h1>{{ letter.title }}</h1>
            </div>
            
            <div class="letter-address">
              <div class="to-section">
                <span class="label">To:</span>
                <span class="name">{{ letter.receiverName || '亲爱的' }}</span>
              </div>
            </div>
          </div>

          <div class="letter-body">
            <div class="content" v-html="letter.content"></div>
          </div>

          <div class="letter-footer">
            <div class="signature">
              <span v-if="letter.senderName" class="author">From: {{ letter.senderName }}</span>
              <span class="signature-date">{{ formatDate(letter.createdAt) }}</span>
            </div>
          </div>
        </div>
      </div>

             <!-- 解锁时间信息 -->
       <div class="unlock-info" v-if="letter.unlockTime && !letter.isRead">
         <div class="unlock-content">
           <van-icon name="clock-o" />
           <span>解锁时间：{{ formatCountdown(letter.unlockTime) }}</span>
         </div>
       </div>

      <div class="letter-actions" v-if="!letter.isRead && letter.senderName">
        <van-button 
          type="primary" 
          size="large"
          @click="markAsReadHandler"
          :loading="markingAsRead"
          round
        >
          标记已读
        </van-button>
      </div>
    </div>

    <div v-else class="loading-state">
      <van-loading type="spinner" size="24px">加载中...</van-loading>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getLetterById, markAsRead } from '@/api/letter'
import { showToast } from 'vant'

const route = useRoute()
const router = useRouter()

const letter = ref(null)
const markingAsRead = ref(false)
const countdownTimer = ref(null)

const fetchLetterDetail = async () => {
  try {
    const letterId = route.params.id
    if (!letterId) {
      showToast('信件ID不存在')
      return
    }
    
    letter.value = await getLetterById(letterId)
    // 获取信件详情后启动倒计时
    startCountdown()
  } catch (error) {
    showToast('获取信件详情失败')
    console.error('获取信件详情失败:', error)
  }
}

const markAsReadHandler = async () => {
  if (!letter.value) return
  
  markingAsRead.value = true
  try {
    await markAsRead(letter.value.id)
    letter.value.isRead = true
    showToast('已标记为已读')
  } catch (error) {
    showToast('标记已读失败')
  } finally {
    markingAsRead.value = false
  }
}

const goBack = () => {
  router.back()
}

const formatDate = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const formatCountdown = (unlockTime) => {
  if (!unlockTime) return ''
  const now = new Date()
  const unlock = new Date(unlockTime)
  const diff = unlock - now
  
  if (diff <= 0) {
    return '已解锁'
  }
  
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
  const seconds = Math.floor((diff % (1000 * 60)) / 1000)
  
  if (days > 0) {
    return `${days}天${hours}小时${minutes}分${seconds}秒`
  } else if (hours > 0) {
    return `${hours}小时${minutes}分${seconds}秒`
  } else if (minutes > 0) {
    return `${minutes}分${seconds}秒`
  } else {
    return `${seconds}秒`
  }
}

const startCountdown = () => {
  if (letter.value?.unlockTime && !letter.value?.isRead) {
    // 清除之前的定时器
    if (countdownTimer.value) {
      clearInterval(countdownTimer.value)
    }
    
    // 启动新的定时器，每秒更新一次
    countdownTimer.value = setInterval(() => {
      // 强制更新组件，触发倒计时重新计算
      letter.value = { ...letter.value }
    }, 1000)
  }
}

const stopCountdown = () => {
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value)
    countdownTimer.value = null
  }
}

onMounted(() => {
  fetchLetterDetail()
})

onUnmounted(() => {
  stopCountdown()
})
</script>

<style lang="scss" scoped>
.letter-detail-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #8B4513 0%, #A0522D 50%, #CD853F 100%);
  padding: 20px;
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-image: 
      radial-gradient(circle at 25% 25%, rgba(255, 255, 255, 0.1) 2px, transparent 2px),
      radial-gradient(circle at 75% 75%, rgba(139, 69, 19, 0.1) 2px, transparent 2px);
    background-size: 100px 100px, 150px 150px;
    opacity: 0.3;
    pointer-events: none;
  }
}

.letter-paper {
  max-width: 800px;
  margin: 0 auto;
  padding: 60px 0 20px 0;
  position: relative;
}

.back-button {
  position: fixed;
  top: 20px;
  left: 20px;
  z-index: 1000;
  
  .van-icon {
    font-size: 24px;
    color: #F5DEB3;
    background: rgba(139, 69, 19, 0.9);
    border-radius: 50%;
    padding: 10px;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.4);
    
    &:hover {
      background: rgba(139, 69, 19, 1);
      transform: scale(1.1);
      box-shadow: 0 6px 16px rgba(0, 0, 0, 0.5);
    }
  }
}

.paper-border {
  background: linear-gradient(135deg, #F5DEB3 0%, #DEB887 50%, #D2B48C 100%);
  border: 3px solid #8B4513;
  border-radius: 12px;
  box-shadow: 
    0 8px 32px rgba(139, 69, 19, 0.3),
    0 16px 64px rgba(139, 69, 19, 0.2),
    inset 0 2px 4px rgba(255, 255, 255, 0.3);
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-image: 
      radial-gradient(circle at 20% 20%, rgba(255, 255, 255, 0.1) 1px, transparent 1px),
      radial-gradient(circle at 80% 80%, rgba(139, 69, 19, 0.05) 1px, transparent 1px);
    background-size: 40px 40px, 60px 60px;
    opacity: 0.6;
    pointer-events: none;
  }
  
  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(45deg, transparent 49%, rgba(139, 69, 19, 0.03) 50%, transparent 51%);
    background-size: 20px 20px;
    pointer-events: none;
  }
  
  // 添加羊皮卷的纹理效果
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-image: 
      linear-gradient(90deg, transparent 98%, rgba(139, 69, 19, 0.1) 99%, transparent 100%),
      linear-gradient(0deg, transparent 98%, rgba(139, 69, 19, 0.1) 99%, transparent 100%);
    background-size: 20px 20px;
    opacity: 0.4;
    pointer-events: none;
  }
}

.paper-content {
  position: relative;
  z-index: 1;
  padding: 50px 40px;
  min-height: 600px;
  background: linear-gradient(135deg, #F5DEB3 0%, #DEB887 100%);
}

.letter-header {
  margin-bottom: 40px;
}

.letter-title {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  
  h1 {
    font-family: 'Times New Roman', serif;
    font-size: 32px;
    font-weight: bold;
    color: #8B4513;
    margin: 0;
    text-shadow: 2px 2px 4px rgba(139, 69, 19, 0.3);
    letter-spacing: 3px;
  }
}

.letter-address {
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
  margin-bottom: 30px;
  font-family: 'Times New Roman', serif;
  
  .to-section {
    display: flex;
    align-items: center;
    gap: 8px;
    
    .label {
      font-size: 18px;
      font-weight: bold;
      color: #8B4513;
      text-transform: uppercase;
      letter-spacing: 1px;
    }
    
    .name {
      font-size: 20px;
      font-weight: 600;
      color: #654321;
      font-style: italic;
    }
  }
}

.letter-body {
  margin-bottom: 50px;
  
  .content {
    font-family: 'Times New Roman', serif;
    font-size: 18px;
    line-height: 2.4;
    color: #654321;
    text-align: justify;
    text-indent: 2em;
    letter-spacing: 1px;
    
    :deep(p) {
      margin-bottom: 1.8em;
      text-indent: 2em;
    }
    
    :deep(h1, h2, h3, h4, h5, h6) {
      font-family: 'Times New Roman', serif;
      font-weight: bold;
      color: #8B4513;
      margin: 2em 0 1em 0;
      text-indent: 0;
      text-align: center;
    }
    
    :deep(strong, b) {
      font-weight: bold;
      color: #8B4513;
    }
    
    :deep(em, i) {
      font-style: italic;
      color: #A0522D;
    }
    
    :deep(u) {
      text-decoration: underline;
      text-decoration-color: #8B4513;
      text-decoration-thickness: 2px;
    }
  }
}

.letter-footer {
  text-align: right;
  margin-top: 20px;
  padding-top: 10px;
  position: relative;
  
  .signature {
    font-family: 'Brush Script MT', cursive;
    font-size: 20px;
    color: #8B4513;
    line-height: 1.8;
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    position: absolute;
    bottom: 0;
    right: 0;
    
    .author {
      font-family: 'Times New Roman', serif;
      font-size: 18px;
      font-weight: 600;
      color: #8B4513;
      margin-bottom: 2px;
    }
    
    .signature-date {
      font-family: 'Times New Roman', serif;
      font-size: 16px;
      color: #A0522D;
      font-style: italic;
    }
  }
}

.unlock-info {
  background: linear-gradient(135deg, rgba(139, 69, 19, 0.1), rgba(160, 82, 45, 0.1));
  border: 2px solid #8B4513;
  border-radius: 12px;
  padding: 20px;
  margin: 25px 0;
  text-align: center;
  font-family: 'Times New Roman', serif;
  color: #ffffff;
  font-size: 14px;
  box-shadow: 0 4px 12px rgba(139, 69, 19, 0.2);
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(45deg, transparent 49%, rgba(139, 69, 19, 0.05) 50%, transparent 51%);
    background-size: 10px 10px;
    border-radius: 10px;
    pointer-events: none;
  }
  
  .unlock-content {
    position: relative;
    z-index: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
  }
  
  .van-icon {
    font-size: 18px;
    color: #ffffff;
  }
}

.letter-actions {
  text-align: center;
  margin-top: 30px;
  padding: 20px;
  
  .van-button {
    min-width: 140px;
    height: 48px;
    font-size: 16px;
    font-weight: 500;
    font-family: 'Times New Roman', serif;
    background: linear-gradient(135deg, #8B4513, #A0522D);
    border: 2px solid #654321;
    border-radius: 24px;
    box-shadow: 
      0 6px 20px rgba(139, 69, 19, 0.4),
      inset 0 2px 4px rgba(255, 255, 255, 0.2);
    color: #F5DEB3;
    text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
    transition: all 0.3s ease;
    
    &:active {
      transform: translateY(2px);
      box-shadow: 
        0 4px 12px rgba(139, 69, 19, 0.4),
        inset 0 1px 2px rgba(255, 255, 255, 0.1);
    }
    
    &:hover {
      background: linear-gradient(135deg, #A0522D, #CD853F);
      box-shadow: 
        0 8px 25px rgba(139, 69, 19, 0.5),
        inset 0 2px 4px rgba(255, 255, 255, 0.2);
    }
  }
}

.loading-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  color: #F5DEB3;
  font-size: 16px;
  font-family: 'Times New Roman', serif;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
  
  .van-loading {
    color: #F5DEB3;
  }
}

@media (max-width: 768px) {
  .letter-detail-page {
    padding: 10px;
  }
  
  .paper-content {
    padding: 30px 20px;
    min-height: 500px;
  }
  
  .letter-title h1 {
    font-size: 26px;
  }
  
  .letter-body .content {
    font-size: 16px;
    line-height: 2.2;
  }
  
  .letter-address {
    .to-section {
      .label {
        font-size: 16px;
      }
      
      .name {
        font-size: 18px;
      }
    }
  }
}
</style>
