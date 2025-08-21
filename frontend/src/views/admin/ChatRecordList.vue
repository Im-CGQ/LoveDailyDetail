<template>
  <div class="chat-record-list-page">
    <div class="page-header">
      <h2>聊天记录管理</h2>
      <van-button type="primary" @click="goToCreate" class="create-btn">
        <span class="btn-icon">📝</span>
        添加聊天记录
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
              <span class="type-text">{{ record.chatType }}</span>
              <span v-if="record.customType" class="custom-type">({{ record.customType }})</span>
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getAllChatRecords, deleteChatRecord } from '@/api/chatRecord'
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
    '小红书聊天': '📱',
    '自定义': '💭'
  }
  return iconMap[type] || '💬'
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
    const records = await getAllChatRecords()
    if (records && records.length > 0) {
      chatRecords.value = records
    }
    finished.value = true
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
</script>

<style lang="scss" scoped>
.chat-record-list-page {
  padding: 20px;
  background: #f5f5f5;
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
    font-size: 24px;
  }
  
  .create-btn {
    .btn-icon {
      margin-right: 8px;
    }
  }
}

.content {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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
