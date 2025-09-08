<template>
  <div class="letter-box-page page-container">
    <!-- 返回按钮 -->
    <BackButton />
    
    <div class="page-header">
      <h1 class="page-title">💌 我的信箱</h1>
      <p class="page-subtitle">查看收到的信件和我写的信件</p>
    </div>

    <van-tabs v-model:active="activeTab" @change="handleTabChange" class="letter-tabs">
      <van-tab title="已解锁" name="unlocked">
        <div class="letter-list">
          <div v-if="unlockedLetters.length === 0" class="empty-state">
            <van-icon name="envelop-o" size="48" color="#ccc" />
            <p>暂无已解锁的信件</p>
          </div>
          <van-cell
            v-for="letter in unlockedLetters"
            :key="letter.id"
            :title="letter.title"
            :label="`来自: ${letter.senderName} | ${formatDateTime(letter.createdAt)}`"
            is-link
            @click="viewLetter(letter)"
            class="letter-item"
          >
            <template #right-icon>
              <van-tag v-if="!letter.isRead" type="primary" size="small">未读</van-tag>
            </template>
          </van-cell>
        </div>
      </van-tab>

      <van-tab title="待解锁" name="locked">
        <div class="letter-list">
          <div v-if="lockedLetters.length === 0" class="empty-state">
            <van-icon name="clock-o" size="48" color="#ccc" />
            <p>暂无待解锁的信件</p>
          </div>
          <van-cell
            v-for="letter in lockedLetters"
            :key="letter.id"
            :title="letter.title"
            :label="`来自: ${letter.senderName} | 解锁时间: ${formatDateTime(letter.unlockTime)}`"
            class="letter-item locked"
            @click="viewLockedLetter(letter)"
          >
            <template #right-icon>
              <div class="countdown-overlay">
                <div class="countdown-text">
                  <div class="countdown-label">解锁倒计时</div>
                  <div class="countdown-time">
                    {{ formatCountdown(letter.remainingSeconds) }}
                  </div>
                </div>
              </div>
            </template>
          </van-cell>
        </div>
      </van-tab>

      <van-tab title="我写的信" name="sent">
        <div class="letter-list">
          <div v-if="sentLetters.length === 0" class="empty-state">
            <van-icon name="edit" size="48" color="#ccc" />
            <p>暂无我写的信件</p>
          </div>
          <van-cell
            v-for="letter in sentLetters"
            :key="letter.id"
            :title="letter.title"
            :label="`收件人: ${letter.receiverName} | 解锁时间: ${formatDateTime(letter.unlockTime)}`"
            is-link
            @click="viewSentLetter(letter)"
            class="letter-item sent"
          >
            <template #right-icon>
              <div class="status-badge">
                <van-tag v-if="isLetterLocked(letter)" type="warning" size="small" round>待解锁</van-tag>
                <van-tag v-else type="success" size="small" round>已解锁</van-tag>
              </div>
            </template>
          </van-cell>
        </div>
      </van-tab>
    </van-tabs>

    <!-- 未解锁信件提示弹窗 -->
    <van-popup
      v-model:show="lockedLetterVisible"
      position="center"
      round
      :style="{ width: '80%', padding: '20px' }"
    >
      <div class="locked-letter-info" v-if="selectedLetter">
        <van-icon name="clock-o" size="48" color="#ff6b6b" />
        <h3>信件尚未解锁</h3>
        <p class="letter-title">{{ selectedLetter.title }}</p>
        <p class="unlock-time">解锁时间: {{ formatDateTime(selectedLetter.unlockTime) }}</p>
        <div class="countdown-info">
          <div class="countdown-label">剩余时间:</div>
          <div class="countdown-time-large">
            {{ formatCountdown(selectedLetter.remainingSeconds) }}
          </div>
        </div>
        <van-button type="primary" @click="lockedLetterVisible = false">
          知道了
        </van-button>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { getUnlockedLetters, getLockedLetters, getSentLetters } from '@/api/letter'
import { showToast } from 'vant'
import { useRouter } from 'vue-router'
import BackButton from '@/components/BackButton.vue'

const activeTab = ref('unlocked')
const unlockedLetters = ref([])
const lockedLetters = ref([])
const sentLetters = ref([]) // 新增：我写的信件列表
const selectedLetter = ref(null)
const lockedLetterVisible = ref(false)
const countdownTimer = ref(null)

const router = useRouter()



// 加载信件列表
const loadLetters = async () => {
  try {
    if (activeTab.value === 'unlocked') {
      unlockedLetters.value = await getUnlockedLetters()
    } else if (activeTab.value === 'locked') {
      lockedLetters.value = await getLockedLetters()
    } else if (activeTab.value === 'sent') {
      // 加载我写的信件列表，假设有一个API端点
      // 这里需要根据实际API结构调整，例如：sentLetters.value = await getSentLetters()
      // 目前假设有一个getSentLetters函数
      sentLetters.value = await getSentLetters() // 示例：从API获取我写的信件
    }
  } catch (error) {
    showToast('加载信件失败')
  }
}

// 切换标签页
const handleTabChange = (name) => {
  activeTab.value = name
  loadLetters()
}

// 查看信件详情
const viewLetter = async (letter) => {
  router.push(`/letter/${letter.id}`)
}

// 查看未解锁信件
const viewLockedLetter = (letter) => {
  selectedLetter.value = letter
  lockedLetterVisible.value = true
}

// 查看我写的信件详情
const viewSentLetter = async (letter) => {
  router.push(`/letter/${letter.id}`)
}

// 判断信件是否待解锁
const isLetterLocked = (letter) => {
  if (letter.unlockTime) {
    const unlockTime = new Date(letter.unlockTime).getTime()
    const now = new Date().getTime()
    return unlockTime > now
  }
  return false
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 格式化倒计时
const formatCountdown = (seconds) => {
  if (!seconds || seconds <= 0) return '已解锁'
  
  const days = Math.floor(seconds / 86400)
  const hours = Math.floor((seconds % 86400) / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60
  
  if (days > 0) {
    return `${days}天${hours}小时`
  } else if (hours > 0) {
    return `${hours}小时${minutes}分钟`
  } else if (minutes > 0) {
    return `${minutes}分钟${secs}秒`
  } else {
    return `${secs}秒`
  }
}

// 开始倒计时
const startCountdown = () => {
  countdownTimer.value = setInterval(() => {
    // 更新未解锁信件的剩余时间
    lockedLetters.value.forEach(letter => {
      if (letter.remainingSeconds > 0) {
        letter.remainingSeconds--
        // 如果时间到了，重新加载信件列表
        if (letter.remainingSeconds <= 0) {
          loadLetters()
        }
      }
    })
  }, 1000)
}

onMounted(() => {
  loadLetters()
  startCountdown()
})

onBeforeUnmount(() => {
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value)
  }
})
</script>

<style lang="scss" scoped>
.letter-box-page {
  padding: 20px;
  padding-top: 100px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
}



.page-header {
  text-align: center;
  margin-bottom: 30px;
  color: white;
  
  .page-title {
    font-size: 28px;
    font-weight: bold;
    margin-bottom: 8px;
  }
  
  .page-subtitle {
    font-size: 16px;
    opacity: 0.9;
  }
}

.letter-tabs {
  background: white;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.letter-list {
  padding: 20px;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  color: #999;
  
  p {
    margin-top: 10px;
    font-size: 16px;
  }
}

.letter-item {
  margin-bottom: 10px;
  border-radius: 10px;
  background: #f8f9fa;
  
  &.locked {
    position: relative;
    opacity: 0.7;
  }

  &.sent {
    background: linear-gradient(135deg, #e3f2fd 0%, #f3e5f5 100%);
    border: 1px solid #bbdefb;
    box-shadow: 0 2px 8px rgba(187, 222, 251, 0.3);
    transition: all 0.3s ease;
    
    &:hover {
      background: linear-gradient(135deg, #bbdefb 0%, #e1bee7 100%);
      border-color: #90caf9;
      box-shadow: 0 4px 12px rgba(187, 222, 251, 0.4);
      transform: translateY(-1px);
    }
    
    .van-cell__title {
      font-weight: 600;
      color: #1976d2;
    }
    
    .van-cell__label {
      color: #666;
      font-size: 13px;
    }
  }
}

.countdown-overlay {
  display: flex;
  align-items: center;
  background: rgba(255, 107, 107, 0.1);
  padding: 8px 12px;
  border-radius: 20px;
  border: 1px solid rgba(255, 107, 107, 0.3);
  
  .countdown-text {
    text-align: center;
    
    .countdown-label {
      font-size: 10px;
      color: #ff6b6b;
      margin-bottom: 2px;
    }
    
    .countdown-time {
      font-size: 12px;
      font-weight: bold;
      color: #ff6b6b;
    }
  }
}

.status-badge {
  display: flex;
  gap: 5px; /* 标签之间的间距 */
  
  .van-tag {
    font-size: 11px;
    font-weight: 500;
    padding: 4px 8px;
    border-radius: 12px;
    
    &.van-tag--warning {
      background: linear-gradient(135deg, #ff9500 0%, #ff6b35 100%);
      color: white;
      border: none;
      box-shadow: 0 2px 4px rgba(255, 149, 0, 0.3);
    }
    
    &.van-tag--success {
      background: linear-gradient(135deg, #4caf50 0%, #45a049 100%);
      color: white;
      border: none;
      box-shadow: 0 2px 4px rgba(76, 175, 80, 0.3);
    }
  }
}

.locked-letter-info {
  text-align: center;
  
  h3 {
    margin: 15px 0 10px 0;
    color: #333;
  }
  
  .letter-title {
    font-size: 16px;
    font-weight: bold;
    margin: 10px 0;
    color: #333;
  }
  
  .unlock-time {
    color: #666;
    margin-bottom: 15px;
  }
  
  .countdown-info {
    background: #f8f9fa;
    padding: 15px;
    border-radius: 10px;
    margin: 15px 0;
    
    .countdown-label {
      font-size: 14px;
      color: #666;
      margin-bottom: 5px;
    }
    
    .countdown-time-large {
      font-size: 18px;
      font-weight: bold;
      color: #ff6b6b;
    }
  }
}

@media (max-width: 768px) {
  .letter-box-page {
    padding-top: 80px;
  }
  
  .content {
    padding: 15px;
  }
  
  .page-header .page-title {
    font-size: 24px;
  }
  
  .letter-list {
    padding: 15px;
  }
}
</style>
