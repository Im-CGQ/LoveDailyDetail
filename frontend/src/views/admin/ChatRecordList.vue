<template>
  <div class="chat-record-list-page">
    <div class="page-header">
      <h2>聊天记录</h2>
      <van-button type="primary" @click="goToCreate" class="create-btn">
        添加
      </van-button>
    </div>

    <div class="content">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="loadChatRecords"
      >
        <div class="chat-record-item" v-for="record in chatRecords" :key="record.id" @click="goToEdit(record.id)">
          <div class="record-header">
            <div class="chat-type">
              <span class="type-icon">{{ getChatTypeIcon(record.chatType) }}</span>
              <span class="type-text" :class="{ 'custom-type': isCustomType(record) }">{{ getDisplayChatType(record) }}</span>
            </div>
            <div class="duration">
              <span class="duration-number">{{ record.durationMinutes }}</span>
              <span class="duration-unit">分钟</span>
            </div>
          </div>
          
          <div class="record-date">{{ formatDate(record.date) }}</div>
          
          <div v-if="record.description" class="record-description">
            {{ record.description }}
          </div>
          
          <div class="record-actions">
            <van-button size="small" type="primary" @click.stop="goToEdit(record.id)">编辑</van-button>
            <van-button size="small" type="danger" @click.stop="handleDelete(record.id)">删除</van-button>
          </div>
        </div>
      </van-list>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onActivated } from 'vue'
import { useRouter } from 'vue-router'
import { getChatRecordsWithPagination, deleteChatRecord } from '@/api/admin'
import { showToast, showConfirmDialog } from 'vant'

const router = useRouter()
const chatRecords = ref([])
const loading = ref(false)
const finished = ref(false)
const page = ref(1)
const pageSize = 10

// 获取聊天类型图标
const getChatTypeIcon = (type) => {
  const iconMap = {
    '微信语音': '🎤',
    '微信聊天': '💬',
    '小红书聊天': '📱'
  }
  // 如果是预设类型，返回对应图标；否则返回默认图标
  return iconMap[type] || '💭'
}

// 获取显示的聊天类型
const getDisplayChatType = (record) => {
  // 如果chatType是"自定义"且有customType，显示customType
  if (record.chatType === '自定义' && record.customType) {
    return record.customType
  }
  // 如果chatType不是预设类型，说明是自定义类型，直接显示
  const presetTypes = ['微信语音', '微信聊天', '小红书聊天']
  if (!presetTypes.includes(record.chatType)) {
    return record.chatType
  }
  // 否则直接显示chatType
  return record.chatType
}

// 判断是否为自定义类型
const isCustomType = (record) => {
  const presetTypes = ['微信语音', '微信聊天', '小红书聊天']
  return !presetTypes.includes(record.chatType) || (record.chatType === '自定义' && record.customType)
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
}

// 加载聊天记录
const loadChatRecords = async () => {
  if (loading.value) return
  
  loading.value = true
  try {
    const response = await getChatRecordsWithPagination(page.value, pageSize)
    if (response && response.content && response.content.length > 0) {
      if (page.value === 1) {
        chatRecords.value = response.content
      } else {
        chatRecords.value.push(...response.content)
      }
      page.value++
      
      // 检查是否还有更多数据
      if (response.content.length < pageSize) {
        finished.value = true
      }
    } else {
      finished.value = true
    }
  } catch (error) {
    console.error('加载聊天记录失败:', error)
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

// 跳转到创建页面
const goToCreate = () => {
  router.push('/admin/chat-record/create')
}

// 跳转到编辑页面
const goToEdit = (id) => {
  router.push(`/admin/chat-record/edit/${id}`)
}

// 删除聊天记录
const handleDelete = async (id) => {
  try {
    await showConfirmDialog({
      title: '确认删除',
      message: '确定要删除这条聊天记录吗？'
    })
    
    await deleteChatRecord(id)
    showToast('删除成功')
    
    // 重置分页状态并重新加载
    page.value = 1
    finished.value = false
    chatRecords.value = []
    await loadChatRecords()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除聊天记录失败:', error)
      showToast('删除失败')
    }
  }
}

onMounted(() => {
  loadChatRecords()
})

// 当页面被激活时（从其他页面返回）重新加载数据
onActivated(() => {
  // 重置分页状态并重新加载
  page.value = 1
  finished.value = false
  chatRecords.value = []
  loadChatRecords()
})
</script>

<style lang="scss" scoped>
.chat-record-list-page {
  padding: 20px;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  
  h2 {
    margin: 0;
    color: #333;
    font-size: 18px;
    font-weight: 600;
  }
  
  .create-btn {
    height: 32px;
    padding: 0 16px;
    font-size: 14px;
    border-radius: 16px;
  }
}

.content {
  background: #ffffff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.chat-record-item {
  border: 1px solid #eee;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
  }
  
  &:last-child {
    margin-bottom: 0;
  }
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.chat-type {
  display: flex;
  align-items: center;
  gap: 8px;
  
  .type-icon {
    font-size: 20px;
  }
  
  .type-text {
    font-weight: 600;
    color: #333;
  }
  
  .custom-type {
    color: #666;
    font-size: 14px;
  }
}

.duration {
  display: flex;
  align-items: center;
  gap: 4px;
  
  .duration-number {
    font-size: 24px;
    font-weight: bold;
    color: #ff6b9d;
  }
  
  .duration-unit {
    color: #666;
    font-size: 14px;
  }
}

.record-date {
  color: #666;
  font-size: 14px;
  margin-bottom: 10px;
}

.record-description {
  color: #333;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 15px;
  background: #f8f9fa;
  padding: 10px;
  border-radius: 8px;
}

.record-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}
</style>
