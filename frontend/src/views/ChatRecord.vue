<template>
  <div class="chat-record-page page-container">
    <!-- 爱心装饰 -->
    <div class="heart-decoration heart-1">💕</div>
    <div class="heart-decoration heart-2">💖</div>
    <div class="heart-decoration heart-3">💝</div>
    
                                                                                                   <div class="page-header">
                  <div class="placeholder"></div>
                  <div class="total-duration-display">
                    <span class="duration-number">{{ totalMinutes }}</span>
                    <span class="duration-unit">分钟</span>
                  </div>
                  <van-button type="primary" @click="showAddDialog = true" class="create-btn">
            <span class="btn-icon">📝</span>
          </van-button>
       </div>

    <div class="content">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="loadChatRecords"
      >
        <div class="chat-record-item" v-for="record in chatRecords" :key="record.id">
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
                         </div>
        </van-list>
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
      <van-date-picker
        v-model="selectedDate"
        title="选择日期"
        :min-date="new Date(2020, 0, 1)"
        :max-date="new Date(2030, 11, 31)"
        @confirm="onDateConfirm"
        @cancel="showDatePicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getAllChatRecords, createChatRecord } from '@/api/chatRecord'
import { showToast } from 'vant'

const chatRecords = ref([])
const loading = ref(false)
const finished = ref(false)
const showAddDialog = ref(false)
const showTypePicker = ref(false)
const showDatePicker = ref(false)
const submitting = ref(false)
// 初始化当前日期为数组格式，用于日期选择器
const selectedDate = ref([
  new Date().getFullYear().toString(),
  (new Date().getMonth() + 1).toString().padStart(2, '0'),
  new Date().getDate().toString().padStart(2, '0')
])

const form = ref({
  chatType: '',
  durationMinutes: '',
  date: '',
  description: '',
  customType: ''
})

const chatTypeOptions = [
  { text: '微信语音', value: '微信语音' },
  { text: '微信聊天', value: '微信聊天' },
  { text: '小红书聊天', value: '小红书聊天' },
  { text: '自定义', value: '自定义' }
]

// 计算总分钟时长
const totalMinutes = computed(() => {
  return chatRecords.value.reduce((total, record) => {
    return total + (record.durationMinutes || 0)
  }, 0)
})

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

// 聊天类型确认
const onTypeConfirm = (value) => {
  form.value.chatType = value.selectedValues[0]
  showTypePicker.value = false
}

// 日期确认
const onDateConfirm = (val) => {
  try {
    console.log('日期确认值:', val, '类型:', typeof val, '是否为数组:', Array.isArray(val))
    
    // 处理日期选择器返回的数组格式 ['2021', '02', '01']
    let selectedDate
    if (Array.isArray(val)) {
      // 如果是数组格式，将其转换为日期字符串
      const [year, month, day] = val
      selectedDate = new Date(parseInt(year), parseInt(month) - 1, parseInt(day))
    } else if (val && val.selectedValues && Array.isArray(val.selectedValues)) {
      // 如果是对象格式，获取selectedValues数组
      const [year, month, day] = val.selectedValues
      selectedDate = new Date(parseInt(year), parseInt(month) - 1, parseInt(day))
    } else if (val instanceof Date) {
      selectedDate = val
    } else {
      selectedDate = new Date(val)
    }
    
    // 验证日期是否有效
    if (isNaN(selectedDate.getTime())) {
      throw new Error('无效的日期值')
    }
    
    // 更新表单日期
    form.value.date = selectedDate.toISOString().split('T')[0]
    showDatePicker.value = false
    
    console.log('处理后的日期:', form.value.date)
  } catch (error) {
    console.error('日期处理错误:', error)
    // 使用当前日期作为默认值
    const now = new Date()
    form.value.date = now.toISOString().split('T')[0]
    showDatePicker.value = false
  }
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
  // 重置日期选择器为当前日期
  selectedDate.value = [
    new Date().getFullYear().toString(),
    (new Date().getMonth() + 1).toString().padStart(2, '0'),
    new Date().getDate().toString().padStart(2, '0')
  ]
}

onMounted(() => {
  loadChatRecords()
})
</script>

<style lang="scss" scoped>
.chat-record-page {
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  
  .total-duration-display {
    display: flex;
    align-items: center;
    gap: 4px;
    
    .duration-number {
      font-size: 24px;
      font-weight: bold;
      color: #ff6b9d;
    }
    
    .duration-unit {
      color: white;
      font-size: 14px;
    }
  }
  
  .placeholder {
    width: 40px;
  }
  
  .create-btn {
    height: 40px;
    width: 40px;
    font-size: 16px;
    font-weight: 600;
    border-radius: 50%;
    padding: 0;
    background: transparent;
    border: none;
    color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    
    &:hover {
      background: rgba(255, 255, 255, 0.1);
    }
    
    .btn-icon {
      margin: 0;
    }
  }
}

.content {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.chat-record-item {
  border: 1px solid #eee;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 15px;
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
  background: #f8f9fa;
  padding: 10px;
  border-radius: 8px;
}

.add-chat-dialog {
  padding: 20px;
  background: white;
  border-radius: 15px;
  
  :deep(.van-cell) {
    padding: 8px 0;
  }
  
  :deep(.van-field) {
    .van-field__label {
      margin-bottom: 8px;
      display: block;
      width: 100%;
      text-align: left;
      font-weight: 500;
      color: #333;
      
      &::after {
        content: ':';
        margin-left: 2px;
      }
    }
    
    .van-field__control {
      margin-top: 0;
      width: 100%;
    }
    
    .van-field__body {
      flex-direction: column;
      align-items: flex-start;
    }
  }
  
  .form-actions {
    display: flex;
    gap: 15px;
    margin-top: 25px;
    justify-content: center;
    
    .van-button {
      flex: 1;
      height: 45px;
      border-radius: 25px;
      font-size: 16px;
      font-weight: 600;
      border: none;
      transition: all 0.3s ease;
      
      &.van-button--default {
        background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
        color: #6c757d;
        
        &:hover {
          background: linear-gradient(135deg, #e9ecef 0%, #dee2e6 100%);
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(108, 117, 125, 0.2);
        }
      }
      
      &.van-button--primary {
        background: linear-gradient(135deg, #ff6b9d 0%, #ff8e9e 100%);
        color: white;
        
        &:hover {
          background: linear-gradient(135deg, #ff5a8c 0%, #ff7d8e 100%);
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(255, 107, 157, 0.3);
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .chat-record-page {
    padding: 15px;
  }
  
  .page-header h2 {
    font-size: 20px;
  }
  
  .content {
    padding: 15px;
  }
}
</style>

