<template>
  <div class="chat-record-page page-container">
    <!-- 爱心装饰 -->
    <div class="heart-decoration heart-1">💕</div>
    <div class="heart-decoration heart-2">💖</div>
    <div class="heart-decoration heart-3">💝</div>
    
    <div class="content" v-if="currentChatRecord">
      <div class="title-section float">
        <h1 class="main-title text-gradient-romantic">{{ getChatTypeDisplayName(currentChatRecord.chatType) }}</h1>
        <p class="subtitle pulse">{{ formatDate(currentChatRecord.date) }}</p>
        <div class="duration-counter">
          <span class="counter-number">{{ currentChatRecord.durationMinutes }}</span>
          <span class="counter-text">分钟</span>
        </div>
      </div>

      <div class="description-section hover-lift">
        <div class="description-content glow">
          <h3 class="section-title">💬 聊天内容</h3>
          <p class="description-text">{{ currentChatRecord.description || '暂无描述' }}</p>
        </div>
      </div>

      <div class="actions-section">
        <van-button 
          type="primary" 
          size="large" 
          @click="showAddDialog = true"
          class="btn-primary ripple"
        >
          <span class="btn-icon">📝</span>
          添加聊天记录
        </van-button>
      </div>
    </div>

    <div v-else class="empty-state">
      <div class="empty-content">
        <div class="empty-icon">💬</div>
        <h2 class="empty-title">还没有聊天记录</h2>
        <p class="empty-subtitle">记录和伴侣的每一次聊天时光</p>
        <van-button 
          type="primary" 
          size="large" 
          @click="showAddDialog = true"
          class="btn-primary ripple"
        >
          <span class="btn-icon">📝</span>
          添加第一条记录
        </van-button>
      </div>
    </div>

    <!-- 添加聊天记录弹窗 -->
    <van-dialog v-model:show="showAddDialog" title="添加聊天记录" :show-confirm-button="false">
      <div class="add-chat-dialog">
        <van-form @submit="handleSubmit">
          <van-field
            v-model="form.chatType"
            label="聊天类型"
            placeholder="请选择聊天类型"
            readonly
            @click="showTypePicker = true"
            :rules="[{ required: true, message: '请选择聊天类型' }]"
          />
          
          <van-field
            v-model="form.durationMinutes"
            label="聊天时长(分钟)"
            type="number"
            placeholder="请输入聊天时长"
            :rules="[{ required: true, message: '请输入聊天时长' }]"
          />
          
          <van-field
            v-model="form.date"
            label="聊天日期"
            placeholder="请选择日期"
            readonly
            @click="showDatePicker = true"
            :rules="[{ required: true, message: '请选择日期' }]"
          />
          
          <van-field
            v-model="form.description"
            label="聊天描述"
            type="textarea"
            placeholder="描述一下这次聊天内容..."
            rows="3"
          />
          
          <van-field
            v-if="form.chatType === '自定义'"
            v-model="form.customType"
            label="自定义类型"
            placeholder="请输入自定义聊天类型"
            :rules="[{ required: true, message: '请输入自定义类型' }]"
          />
          
          <div class="form-actions">
            <van-button @click="showAddDialog = false" type="default">取消</van-button>
            <van-button type="primary" native-type="submit" :loading="submitting">保存</van-button>
          </div>
        </van-form>
      </div>
    </van-dialog>

    <!-- 聊天类型选择器 -->
    <van-popup v-model:show="showTypePicker" position="bottom">
      <van-picker
        :columns="chatTypeOptions"
        @confirm="onTypeConfirm"
        @cancel="showTypePicker = false"
      />
    </van-popup>

    <!-- 日期选择器 -->
    <van-popup v-model:show="showDatePicker" position="bottom">
      <van-datetime-picker
        v-model="selectedDate"
        type="date"
        @confirm="onDateConfirm"
        @cancel="showDatePicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAllChatRecords, createChatRecord } from '@/api/chatRecord'
import { showToast } from 'vant'

const currentChatRecord = ref(null)
const showAddDialog = ref(false)
const showTypePicker = ref(false)
const showDatePicker = ref(false)
const submitting = ref(false)
const selectedDate = ref(new Date())

const form = ref({
  chatType: '',
  durationMinutes: '',
  date: '',
  description: '',
  customType: ''
})

const chatTypeOptions = [
  '微信语音',
  '微信聊天', 
  '小红书聊天',
  '自定义'
]

// 获取聊天类型显示名称
const getChatTypeDisplayName = (type) => {
  const typeMap = {
    '微信语音': '💬 微信语音聊天',
    '微信聊天': '💬 微信文字聊天',
    '小红书聊天': '📱 小红书聊天',
    '自定义': form.value.customType || '💬 自定义聊天'
  }
  return typeMap[type] || type
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
  try {
    const records = await getAllChatRecords()
    if (records && records.length > 0) {
      currentChatRecord.value = records[0] // 显示最新的记录
    }
  } catch (error) {
    console.error('加载聊天记录失败:', error)
  }
}

// 聊天类型确认
const onTypeConfirm = (value) => {
  form.value.chatType = value
  showTypePicker.value = false
}

// 日期确认
const onDateConfirm = (value) => {
  form.value.date = value.toISOString().split('T')[0]
  showDatePicker.value = false
}

// 提交表单
const handleSubmit = async () => {
  if (!form.value.chatType) {
    showToast('请选择聊天类型')
    return
  }
  
  if (!form.value.durationMinutes) {
    showToast('请输入聊天时长')
    return
  }
  
  if (!form.value.date) {
    showToast('请选择日期')
    return
  }
  
  if (form.value.chatType === '自定义' && !form.value.customType) {
    showToast('请输入自定义类型')
    return
  }
  
  submitting.value = true
  try {
    await createChatRecord({
      chatType: form.value.chatType,
      durationMinutes: parseInt(form.value.durationMinutes),
      date: form.value.date,
      description: form.value.description,
      customType: form.value.chatType === '自定义' ? form.value.customType : null
    })
    
    showToast('添加成功')
    showAddDialog.value = false
    resetForm()
    await loadChatRecords()
  } catch (error) {
    showToast('添加失败')
    console.error('添加聊天记录失败:', error)
  } finally {
    submitting.value = false
  }
}

// 重置表单
const resetForm = () => {
  form.value = {
    chatType: '',
    durationMinutes: '',
    date: '',
    description: '',
    customType: ''
  }
}

onMounted(() => {
  loadChatRecords()
})
</script>

<style lang="scss" scoped>
.chat-record-page {
  min-height: 100vh;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.content {
  max-width: 800px;
  margin: 0 auto;
  padding-top: 40px;
}

.title-section {
  text-align: center;
  margin-bottom: 40px;
  
  .main-title {
    font-size: 32px;
    font-weight: bold;
    margin-bottom: 10px;
    background: linear-gradient(45deg, #ff6b9d, #c44569);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }
  
  .subtitle {
    font-size: 18px;
    color: rgba(255, 255, 255, 0.9);
    margin-bottom: 20px;
  }
  
  .duration-counter {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    
    .counter-number {
      font-size: 48px;
      font-weight: bold;
      color: #ff6b9d;
    }
    
    .counter-text {
      font-size: 20px;
      color: rgba(255, 255, 255, 0.9);
    }
  }
}

.description-section {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  
  .section-title {
    font-size: 24px;
    color: #333;
    margin-bottom: 15px;
  }
  
  .description-text {
    font-size: 16px;
    line-height: 1.6;
    color: #666;
  }
}

.actions-section {
  text-align: center;
  
  .btn-primary {
    height: 50px;
    font-size: 18px;
    font-weight: 600;
    border-radius: 25px;
    padding: 0 30px;
    
    .btn-icon {
      margin-right: 8px;
    }
  }
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  
  .empty-content {
    text-align: center;
    color: white;
    
    .empty-icon {
      font-size: 80px;
      margin-bottom: 20px;
    }
    
    .empty-title {
      font-size: 28px;
      font-weight: bold;
      margin-bottom: 10px;
    }
    
    .empty-subtitle {
      font-size: 16px;
      opacity: 0.8;
      margin-bottom: 30px;
    }
  }
}

.add-chat-dialog {
  padding: 20px;
  
  .form-actions {
    display: flex;
    gap: 10px;
    margin-top: 20px;
    justify-content: center;
    
    .van-button {
      flex: 1;
    }
  }
}

// 动画效果
.float {
  animation: float 3s ease-in-out infinite;
}

.pulse {
  animation: pulse 2s ease-in-out infinite;
}

.ripple {
  position: relative;
  overflow: hidden;
  
  &::after {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    width: 0;
    height: 0;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.3);
    transform: translate(-50%, -50%);
    transition: width 0.6s, height 0.6s;
  }
  
  &:active::after {
    width: 300px;
    height: 300px;
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}

// 响应式设计
@media (max-width: 768px) {
  .chat-record-page {
    padding: 15px;
  }
  
  .title-section .main-title {
    font-size: 24px;
  }
  
  .description-section {
    padding: 20px;
  }
}
</style>
