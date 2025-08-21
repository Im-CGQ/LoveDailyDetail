<template>
  <div class="letter-list">
    <div class="header">
      <h2>我的信件</h2>
      <van-button type="primary" @click="$router.push('/admin/write-letter')" size="small">
        <van-icon name="edit" />
        写新信
      </van-button>
    </div>

    <van-tabs v-model:active="activeTab" @change="handleTabChange">
      <van-tab title="收到的信" name="received">
        <div class="letters-container">
          <div v-if="receivedLetters.length === 0" class="empty-state">
            <van-icon name="envelop-o" size="48" />
            <p>还没有收到信件</p>
            <p class="empty-tip">可以写信给自己，或者等待伴侣的信件</p>
          </div>
          
          <div v-else class="letters-list">
            <van-cell-group>
              <van-cell 
                v-for="letter in receivedLetters" 
                :key="letter.id"
                :title="letter.title"
                :label="`来自：${letter.senderName}${letter.senderId === letter.receiverId ? ' (自己)' : ''} | ${formatDate(letter.createdAt)}`"
                :class="{ 'locked': !letter.isUnlocked, 'unread': !letter.isRead }"
                @click="viewLetter(letter)"
                is-link
              >
                <template #right-icon>
                  <van-tag v-if="!letter.isUnlocked" type="warning" size="small">等待解锁</van-tag>
                  <van-tag v-else-if="!letter.isRead" type="primary" size="small">未读</van-tag>
                  <van-tag v-else type="success" size="small">已读</van-tag>
                </template>
              </van-cell>
            </van-cell-group>
          </div>
        </div>
      </van-tab>

      <van-tab title="发送的信" name="sent">
        <div class="letters-container">
          <div v-if="sentLetters.length === 0" class="empty-state">
            <van-icon name="edit" size="48" />
            <p>还没有发送过信件</p>
            <p class="empty-tip">可以写信给伴侣，或者写给自己</p>
            <van-button type="primary" @click="$router.push('/admin/write-letter')" size="small">
              写第一封信
            </van-button>
          </div>
          
          <div v-else class="letters-list">
            <van-cell-group>
              <van-cell 
                v-for="letter in sentLetters" 
                :key="letter.id"
                :title="letter.title"
                :label="`发送给：${letter.receiverName}${letter.senderId === letter.receiverId ? ' (自己)' : ''} | ${formatDate(letter.createdAt)}`"
                @click="viewLetter(letter)"
                is-link
              >
                <template #right-icon>
                  <van-button 
                    size="small" 
                    type="danger" 
                    @click.stop="deleteLetterHandler(letter.id)"
                    :loading="deletingLetter === letter.id"
                  >
                    删除
                  </van-button>
                </template>
              </van-cell>
            </van-cell-group>
          </div>
        </div>
      </van-tab>
    </van-tabs>

    <!-- 信件详情弹窗 -->
    <van-popup v-model:show="letterDetailVisible" position="center" :style="{ width: '90%', maxWidth: '500px' }">
      <div v-if="selectedLetter" class="letter-detail">
        <div class="detail-header">
          <h3>{{ selectedLetter.title }}</h3>
        </div>
        
        <div class="detail-meta">
          <p><strong>发送者：</strong>{{ selectedLetter.senderName }}{{ selectedLetter.senderId === selectedLetter.receiverId ? ' (自己)' : '' }}</p>
          <p><strong>接收者：</strong>{{ selectedLetter.receiverName }}{{ selectedLetter.senderId === selectedLetter.receiverId ? ' (自己)' : '' }}</p>
          <p><strong>发送时间：</strong>{{ formatDateTime(selectedLetter.createdAt) }}</p>
          <p><strong>解锁时间：</strong>{{ formatDateTime(selectedLetter.unlockTime) }}</p>
          <p v-if="selectedLetter.senderId === selectedLetter.receiverId" class="self-letter-tip">
            <van-icon name="info-o" /> 这是你写给自己的信
          </p>
        </div>
        
        <div class="detail-content" v-html="selectedLetter.content"></div>
        
        <div class="detail-actions">
          <van-button @click="letterDetailVisible = false">关闭</van-button>
          <van-button 
            v-if="selectedLetter && !selectedLetter.isRead && selectedLetter.isUnlocked"
            type="primary" 
            @click="markAsReadHandler(selectedLetter.id)"
          >
            标记为已读
          </van-button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { 
  getReceivedLetters, 
  getSentLetters, 
  getLetterById, 
  markAsRead, 
  deleteLetter 
} from '@/api/letter'
import { showToast, showDialog } from 'vant'

// 响应式数据
const activeTab = ref('received') // 0: received, 1: sent
const receivedLetters = ref([])
const sentLetters = ref([])
const selectedLetter = ref(null)
const letterDetailVisible = ref(false)
const deletingLetter = ref(null)
const countdownTimer = ref(null)

// 加载信件
const loadLetters = async () => {
  try {
    if (activeTab.value === 'received') {
      const response = await getReceivedLetters()
      receivedLetters.value = response.data || []
    } else {
      const response = await getSentLetters()
      sentLetters.value = response.data || []
    }
  } catch (error) {
    showToast('加载信件失败')
    console.error('加载信件失败:', error)
  }
}

// 标签页切换
const handleTabChange = (index) => {
  activeTab.value = index
  loadLetters()
}

// 查看信件详情
const viewLetter = async (letter) => {
  try {
    const response = await getLetterById(letter.id)
    selectedLetter.value = response.data
    letterDetailVisible.value = true
  } catch (error) {
    showToast('获取信件详情失败')
    console.error('获取信件详情失败:', error)
  }
}

// 标记为已读
const markAsReadHandler = async (letterId) => {
  try {
    await markAsRead(letterId)
    showToast('已标记为已读')
    letterDetailVisible.value = false
    loadLetters()
  } catch (error) {
    showToast('操作失败')
    console.error('标记已读失败:', error)
  }
}

// 删除信件
const deleteLetterHandler = async (letterId) => {
  try {
    await showDialog({
      title: '确认删除',
      message: '确定要删除这封信吗？',
      showCancelButton: true,
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    
    deletingLetter.value = letterId
    await deleteLetter(letterId)
    showToast('删除成功')
    loadLetters()
  } catch (error) {
    if (error !== 'cancel') {
      showToast('删除失败')
      console.error('删除信件失败:', error)
    }
  } finally {
    deletingLetter.value = null
  }
}

// 格式化日期
const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

// 格式化日期时间
const formatDateTime = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

// 开始倒计时
const startCountdown = () => {
  countdownTimer.value = setInterval(() => {
    // 更新倒计时
    receivedLetters.value.forEach(letter => {
      if (!letter.isUnlocked && letter.remainingSeconds > 0) {
        letter.remainingSeconds--
        if (letter.remainingSeconds <= 0) {
          letter.isUnlocked = true
        }
      }
    })
  }, 1000)
}

// 组件挂载
onMounted(() => {
  loadLetters()
  startCountdown()
})

// 组件卸载前清理定时器
onBeforeUnmount(() => {
  if (countdownTimer.value) {
    clearInterval(countdownTimer.value)
  }
})
</script>

<style lang="scss" scoped>
.letter-list {
  padding: 20px;
  
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h2 {
      color: #333;
      margin: 0;
      font-size: 20px;
    }
  }
  
  .letters-container {
    .empty-state {
      text-align: center;
      padding: 60px 20px;
      color: #999;
      
      p {
        margin: 20px 0;
        font-size: 16px;
        
        &.empty-tip {
          font-size: 14px;
          color: #999;
          margin-top: 10px;
        }
      }
    }
    
    .letters-list {
      .van-cell {
        &.locked {
          opacity: 0.8;
        }
        
        &.unread {
          .van-cell__title {
            font-weight: bold;
          }
        }
      }
    }
  }
  
  .letter-detail {
    padding: 20px;
    
    .detail-header {
      text-align: center;
      margin-bottom: 20px;
      
      h3 {
        color: #333;
        margin: 0;
        font-size: 18px;
      }
    }
    
    .detail-meta {
      background: #f8f9fa;
      padding: 15px;
      border-radius: 8px;
      margin-bottom: 20px;
      
      p {
        margin: 8px 0;
        color: #666;
        font-size: 14px;
        
        strong {
          color: #333;
        }
        
        &.self-letter-tip {
          background: #e3f2fd;
          color: #1976d2;
          padding: 8px 12px;
          border-radius: 6px;
          margin-top: 10px;
          border-left: 4px solid #1976d2;
          
          .van-icon {
            margin-right: 5px;
          }
        }
      }
    }
    
    .detail-content {
      line-height: 1.8;
      color: #333;
      max-height: 300px;
      overflow-y: auto;
      padding: 15px;
      background: #f8f9fa;
      border-radius: 8px;
      margin-bottom: 20px;
    }
    
    .detail-actions {
      display: flex;
      gap: 10px;
      justify-content: center;
    }
  }
}

:deep(.van-tabs__nav) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

:deep(.van-tab--active) {
  color: white !important;
}

:deep(.van-tabs__line) {
  background-color: white;
}

:deep(.van-button--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}
</style>
