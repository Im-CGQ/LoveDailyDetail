<template>
  <div class="letter-detail-page">
    <div class="letter-paper" v-if="letter">
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
            <div 
              class="content" 
              :class="{ 'typing-complete': typingComplete }"
              @click="showFullText"
            >
              {{ displayText }}
            </div>
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
       <!-- <div class="unlock-info" v-if="letter.unlockTime && !letter.isRead">
         <div class="unlock-content">
           <van-icon name="clock-o" />
           <span>解锁时间：{{ formatCountdown(letter.unlockTime) }}</span>
         </div>
       </div> -->

      <!-- 分享页面不需要操作按钮 -->
    </div>
  

    <div v-else-if="loading" class="loading-state">
      <van-loading type="spinner" size="24px">加载中...</van-loading>
    </div>
    
    <div v-else-if="error" class="error-container">
      <div class="error-content">
        <div class="error-icon">⚠️</div>
        <h2 class="error-title">分享链接已过期或不存在</h2>
        <p class="error-message">很抱歉，您访问的分享链接已经失效，或者该内容已被删除。</p>
        <van-button 
          type="primary" 
          size="large" 
          @click="goToHome" 
          class="home-btn"
        >
          <span class="btn-icon">🏠</span>
          返回首页
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getSharedLetter } from '@/api/share'
import { showToast } from 'vant'

const route = useRoute()
const router = useRouter()

const letter = ref(null)
const countdownTimer = ref(null)
const displayText = ref('')
const typingComplete = ref(false)
const loading = ref(true)
const error = ref(false)
let typingTimer = null

const fetchLetterDetail = async () => {
  try {
    loading.value = true
    error.value = false
    
    const shareToken = route.params.shareToken
    if (!shareToken) {
      error.value = true
      return
    }
    
    letter.value = await getSharedLetter(shareToken)
    // 启动打字机效果
    if (letter.value && letter.value.content) {
      startTyping(letter.value.content)
    }
    // 获取信件详情后启动倒计时
    startCountdown()
  } catch (err) {
    console.error('获取分享信件失败:', err)
    error.value = true
  } finally {
    loading.value = false
  }
}

// 跳转到系统欢迎页
const goToHome = () => {
  router.push('/')
}

// 分享页面不需要标记已读功能

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
  if (letter.value && letter.value.content) {
    displayText.value = letter.value.content
    typingComplete.value = true
    if (typingTimer) {
      clearTimeout(typingTimer)
    }
  }
}

onMounted(() => {
  fetchLetterDetail()
})

onUnmounted(() => {
  stopCountdown()
  if (typingTimer) {
    clearTimeout(typingTimer) // 清理打字机定时器
  }
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
    color: #ffffff;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
    border-radius: 50%;
    padding: 10px;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
    
    &:hover {
      background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 50%, #e085e8 100%);
      transform: scale(1.1);
      box-shadow: 0 6px 16px rgba(102, 126, 234, 0.6);
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
  margin-bottom: 15px;
  padding-bottom: 10px;
  
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
  margin-bottom: 10px;
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
    white-space: pre-wrap;
    word-wrap: break-word;
    cursor: pointer;
    position: relative;
    transition: all 0.3s ease;
    
    &:hover {
      // background: rgba(139, 69, 19, 0.05);
      // border-radius: 8px;
      // padding: 8px;
      // margin: -8px;
    }
    
    &.typing-complete {
      cursor: default;
    }
    
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

.error-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  padding: 20px;
  
  .error-content {
    text-align: center;
    max-width: 400px;
    
    .error-icon {
      font-size: 64px;
      margin-bottom: 20px;
      animation: pulse 2s ease-in-out infinite;
    }
    
    .error-title {
      color: #F5DEB3;
      font-size: 24px;
      font-weight: bold;
      margin-bottom: 15px;
      font-family: 'Times New Roman', serif;
      text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
    }
    
    .error-message {
      color: rgba(245, 222, 179, 0.8);
      font-size: 16px;
      line-height: 1.6;
      margin-bottom: 30px;
      font-family: 'Times New Roman', serif;
      text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
    }
    
    .home-btn {
      height: 48px;
      border-radius: 24px;
      font-size: 16px;
      font-weight: 500;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      background: linear-gradient(135deg, #8B4513, #A0522D);
      border: 2px solid #654321;
      box-shadow: 
        0 6px 20px rgba(139, 69, 19, 0.4),
        inset 0 2px 4px rgba(255, 255, 255, 0.2);
      color: #F5DEB3;
      text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
      font-family: 'Times New Roman', serif;
      
      .btn-icon {
        font-size: 18px;
      }
      
      &:hover {
        background: linear-gradient(135deg, #A0522D, #CD853F);
        box-shadow: 
          0 8px 25px rgba(139, 69, 19, 0.5),
          inset 0 2px 4px rgba(255, 255, 255, 0.2);
        transform: translateY(-2px);
      }
    }
  }
}

@media (max-width: 768px) {
  .back-button {
    top: 15px;
    left: 15px;
    
    .van-icon {
      font-size: 20px;
      padding: 8px;
    }
  }
  
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

/* 动画关键帧 */
@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}
</style>
