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
                :is-link="letter.isUnlocked"
              >
                <template #right-icon>
                  <div v-if="!letter.isUnlocked" class="locked-info">
                    <van-tag type="warning" size="small">等待解锁</van-tag>
                    <div class="countdown-text" v-if="letter.remainingSeconds > 0">
                      {{ formatCountdown(letter.remainingSeconds) }}
                    </div>
                  </div>
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
                  <div class="action-buttons">
                    <van-button 
                      size="small" 
                      type="primary" 
                      @click.stop="editLetter(letter)"
                    >
                      编辑
                    </van-button>
                    <van-button 
                      size="small" 
                      type="danger" 
                      @click.stop="deleteLetterHandler(letter.id)"
                      :loading="deletingLetter === letter.id"
                    >
                      删除
                    </van-button>
                  </div>
                </template>
              </van-cell>
            </van-cell-group>
          </div>
        </div>
      </van-tab>
    </van-tabs>

  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { 
  getReceivedLetters, 
  getSentLetters, 
  deleteLetter 
} from '@/api/letter'
import { showToast, showDialog } from 'vant'

// 获取路由实例
const router = useRouter()

// 响应式数据
const activeTab = ref('received') // 0: received, 1: sent
const receivedLetters = ref([])
const sentLetters = ref([])
const deletingLetter = ref(null)
const countdownTimer = ref(null)

// 加载信件
const loadLetters = async () => {
  try {
    if (activeTab.value === 'received') {
      receivedLetters.value = await getReceivedLetters()
    } else {
      sentLetters.value = await getSentLetters()
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
const viewLetter = (letter) => {
  // 如果是收到的信件且未解锁，显示提示信息
  if (activeTab.value === 'received' && !letter.isUnlocked) {
    showToast('信件尚未解锁，请等待解锁时间到达')
    return
  }
  
  // 其他情况直接跳转到信件详情页
  router.push(`/letter/${letter.id}`)
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

// 编辑信件
const editLetter = (letter) => {
  // 跳转到编辑信件页面，传递信件数据
  router.push({
    path: `/admin/letter/edit/${letter.id}`
  })
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
  min-height: 100vh;
  
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h2 {
      color: #2c3e50;
      margin: 0;
      font-size: 18px;
      font-weight: 600;
    }
    
    .van-button {
      height: 36px;
      padding: 0 20px;
      font-size: 14px;
      border-radius: 18px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border: none;
      box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
      
      &:hover {
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
      }
      
      .van-icon {
        margin-right: 6px;
      }
    }
  }
  
  .letters-container {
    background: #ffffff;
    border-radius: 16px;
    padding: 20px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    
    .empty-state {
      text-align: center;
      padding: 60px 20px;
      color: #7f8c8d;
      
      .van-icon {
        color: #bdc3c7;
        margin-bottom: 20px;
      }
      
      p {
        margin: 20px 0;
        font-size: 16px;
        color: #34495e;
        
        &.empty-tip {
          font-size: 14px;
          color: #7f8c8d;
          margin-top: 10px;
        }
      }
      
      .van-button {
        margin-top: 20px;
        height: 36px;
        padding: 0 20px;
        font-size: 14px;
        border-radius: 18px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border: none;
        box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
        
        &:hover {
          transform: translateY(-1px);
          box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
        }
      }
    }
    
         .letters-list {
       .van-cell {
         margin-bottom: 8px;
         border-radius: 12px;
         background: #f8f9fa;
         border: 1px solid #e9ecef;
         transition: all 0.3s ease;
         
         &:hover {
           transform: translateY(-2px);
           box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
           background: #ffffff;
         }
        
        &.locked {
          opacity: 0.7;
          background: rgba(255, 193, 7, 0.1);
          border-color: rgba(255, 193, 7, 0.3);
          cursor: not-allowed;
          
          .van-cell__title {
            color: #999;
          }
          
          .van-cell__label {
            color: #ccc;
          }
        }
        
        &.unread {
          .van-cell__title {
            font-weight: 600;
            color: #2c3e50;
          }
          
          background: rgba(52, 152, 219, 0.1);
          border-color: rgba(52, 152, 219, 0.3);
        }
        
        .action-buttons {
          display: flex;
          gap: 8px;
          align-items: center;
          
          .van-button {
            min-width: 50px;
            font-size: 12px;
            height: 28px;
            border-radius: 14px;
            
            &.van-button--primary {
              background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
              border: none;
            }
            
            &.van-button--danger {
              background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
              border: none;
            }
          }
        }
        
        .locked-info {
          display: flex;
          flex-direction: column;
          align-items: flex-end;
          gap: 4px;
          
          .countdown-text {
            font-size: 11px;
            color: #f39c12;
            font-weight: 500;
            background: rgba(243, 156, 18, 0.1);
            padding: 2px 6px;
            border-radius: 8px;
            border: 1px solid rgba(243, 156, 18, 0.3);
          }
        }
      }
    }
  }
  
}

:deep(.van-tabs__nav) {
  background: #ffffff;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #e9ecef;
}

:deep(.van-tab) {
  color: #6c757d;
  font-weight: 500;
}

:deep(.van-tab--active) {
  color: #007bff !important;
  font-weight: 600;
}

:deep(.van-tabs__line) {
  background-color: #007bff;
  height: 3px;
  border-radius: 2px;
}

:deep(.van-button--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
  
  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  }
}

:deep(.van-tag) {
  padding: 4px 8px;
  font-size: 12px;
  line-height: 1.2;
  border-radius: 12px;
  font-weight: 500;
  
  &.van-tag--warning {
    background: linear-gradient(135deg, #f39c12 0%, #e67e22 100%);
    color: white;
  }
  
  &.van-tag--primary {
    background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
    color: white;
  }
  
  &.van-tag--success {
    background: linear-gradient(135deg, #27ae60 0%, #229954 100%);
    color: white;
  }
}
</style>
