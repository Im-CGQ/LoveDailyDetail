<template>
  <div class="letter-box-page page-container">
    <div class="page-header">
      <h1 class="page-title">💌 我的信箱</h1>
      <p class="page-subtitle">来自伴侣的温暖信件</p>
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
    </van-tabs>

    <!-- 信件详情弹窗 -->
    <van-popup
      v-model:show="letterDetailVisible"
      position="bottom"
      :style="{ height: '80%' }"
      round
      closeable
    >
      <div class="letter-detail" v-if="selectedLetter">
        <div class="letter-header">
          <h2 class="letter-title">{{ selectedLetter.title }}</h2>
          <div class="letter-meta">
            <span>来自: {{ selectedLetter.senderName }}</span>
            <span>{{ formatDateTime(selectedLetter.createdAt) }}</span>
          </div>
        </div>
        <div class="letter-content">
          <div v-html="selectedLetter.content"></div>
        </div>
        <div class="letter-actions">
          <van-button 
            v-if="!selectedLetter.isRead" 
            type="primary" 
            size="small"
            @click="markAsReadHandler"
            :loading="markingAsRead"
          >
            标记已读
          </van-button>
        </div>
      </div>
    </van-popup>

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
import { getUnlockedLetters, getLockedLetters, getLetterById, markAsRead } from '@/api/letter'
import { showToast } from 'vant'

const activeTab = ref('unlocked')
const unlockedLetters = ref([])
const lockedLetters = ref([])
const selectedLetter = ref(null)
const letterDetailVisible = ref(false)
const lockedLetterVisible = ref(false)
const markingAsRead = ref(false)
const countdownTimer = ref(null)

// 加载信件列表
const loadLetters = async () => {
  try {
    if (activeTab.value === 'unlocked') {
      const response = await getUnlockedLetters()
      if (response.success) {
        unlockedLetters.value = response.data
      }
    } else {
      const response = await getLockedLetters()
      if (response.success) {
        lockedLetters.value = response.data
      }
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
  try {
    const response = await getLetterById(letter.id)
    if (response.success) {
      selectedLetter.value = response.data
      letterDetailVisible.value = true
    }
  } catch (error) {
    showToast('获取信件详情失败')
  }
}

// 查看未解锁信件
const viewLockedLetter = (letter) => {
  selectedLetter.value = letter
  lockedLetterVisible.value = true
}

// 标记已读
const markAsReadHandler = async () => {
  if (!selectedLetter.value) return
  
  markingAsRead.value = true
  try {
    await markAsRead(selectedLetter.value.id)
    selectedLetter.value.isRead = true
    showToast('已标记为已读')
    // 更新列表中的状态
    const index = unlockedLetters.value.findIndex(l => l.id === selectedLetter.value.id)
    if (index !== -1) {
      unlockedLetters.value[index].isRead = true
    }
  } catch (error) {
    showToast('标记已读失败')
  } finally {
    markingAsRead.value = false
  }
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

.letter-detail {
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
  
  .letter-header {
    margin-bottom: 20px;
    padding-bottom: 15px;
    border-bottom: 1px solid #eee;
    
    .letter-title {
      font-size: 20px;
      font-weight: bold;
      margin-bottom: 10px;
      color: #333;
    }
    
    .letter-meta {
      display: flex;
      justify-content: space-between;
      font-size: 14px;
      color: #666;
    }
  }
  
  .letter-content {
    flex: 1;
    overflow-y: auto;
    line-height: 1.6;
    color: #333;
    
    :deep(p) {
      margin-bottom: 10px;
    }
    
    :deep(h1, h2, h3, h4, h5, h6) {
      margin: 15px 0 10px 0;
      color: #333;
    }
    
    :deep(strong, b) {
      font-weight: bold;
    }
    
    :deep(em, i) {
      font-style: italic;
    }
    
    :deep(u) {
      text-decoration: underline;
    }
  }
  
  .letter-actions {
    margin-top: 20px;
    text-align: center;
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
</style>
