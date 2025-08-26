<template>
  <div class="edit-letter-page page-container">
    <!-- 返回按钮 -->
    <div class="back-button">
      <van-icon name="arrow-left" @click="goBack" />
    </div>
    
    <div class="page-header">
      <h1 class="page-title">✏️ 编辑信件</h1>
      <p class="page-subtitle">修改你的信件内容</p>
    </div>

    <div class="form-container">
      <van-form @submit="onSubmit" class="letter-form">
        <!-- 信件标题 -->
        <van-field
          v-model="form.title"
          name="title"
          label="信件标题"
          placeholder="请输入信件标题"
          :rules="[{ required: true, message: '请输入信件标题' }]"
          class="form-field"
        />

        <!-- 收件人选择 -->
        <van-field
          v-model="selectedReceiverName"
          name="receiver"
          label="收件人"
          placeholder="请选择收件人"
          readonly
          @click="showReceiverPicker = true"
          :rules="[{ required: true, message: '请选择收件人' }]"
          class="form-field"
        />

        <!-- 信件内容 -->
        <van-field
          v-model="form.content"
          name="content"
          label="信件内容"
          type="textarea"
          placeholder="写下你想说的话..."
          :rules="[{ required: true, message: '请输入信件内容' }]"
          rows="8"
          class="form-field content-field"
        />

        <!-- 解锁时间 -->
        <van-field
          v-model="form.unlockTime"
          name="unlockTime"
          label="解锁时间"
          placeholder="请选择解锁时间"
          readonly
          @click="showTimePicker = true"
          :rules="[{ required: true, message: '请选择解锁时间' }]"
          class="form-field"
        />

        <!-- 提交按钮 -->
        <div class="form-actions">
          <van-button 
            type="primary" 
            size="large" 
            native-type="submit"
            :loading="submitting"
            round
            class="submit-btn"
          >
            <span class="btn-icon">💌</span>
            保存修改
          </van-button>
          
          <van-button 
            type="default" 
            size="large" 
            @click="goBack"
            round
            class="cancel-btn"
          >
            取消
          </van-button>
        </div>
      </van-form>
    </div>

    <!-- 收件人选择弹窗 -->
    <van-popup
      v-model:show="showReceiverPicker"
      position="bottom"
      round
      :style="{ height: '50%' }"
    >
      <van-picker
        :columns="receiverOptions"
        @confirm="onReceiverConfirm"
        @cancel="showReceiverPicker = false"
        title="选择收件人"
      />
    </van-popup>

    <!-- 时间选择弹窗 -->
    <van-datetime-picker
      v-model:show="showTimePicker"
      v-model="selectedTime"
      type="datetime"
      title="选择解锁时间"
      :min-date="minDate"
      @confirm="onTimeConfirm"
      @cancel="showTimePicker = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getLetterById, updateLetter } from '@/api/letter'
import { getUserInfo } from '@/api/auth'
import { showToast } from 'vant'

const route = useRoute()
const router = useRouter()

const form = ref({
  title: '',
  content: '',
  receiverId: null,
  unlockTime: ''
})

const submitting = ref(false)
const showReceiverPicker = ref(false)
const showTimePicker = ref(false)
const selectedTime = ref(new Date())
const currentUser = ref(null)
const receivers = ref([])

// 计算属性
const selectedReceiverName = computed(() => {
  const receiver = receivers.value.find(r => r.id === form.value.receiverId)
  return receiver ? receiver.displayName : ''
})

const receiverOptions = computed(() => {
  return receivers.value.map(user => ({
    text: user.displayName || user.username,
    value: user.id
  }))
})

const minDate = computed(() => {
  return new Date()
})

// 获取当前用户信息
const fetchCurrentUser = async () => {
  try {
    const response = await getUserInfo()
    currentUser.value = response.data
    await loadReceivers()
  } catch (error) {
    console.error('获取用户信息失败:', error)
    showToast('获取用户信息失败')
  }
}

// 加载可选的收件人
const loadReceivers = async () => {
  if (!currentUser.value) return
  
  receivers.value = [currentUser.value] // 可以给自己写信
  
  // 如果有伴侣，也可以给伴侣写信
  if (currentUser.value.partnerId) {
    try {
      // 这里应该调用API获取伴侣信息
      // 暂时使用模拟数据，实际应该从后端获取
      receivers.value.push({
        id: currentUser.value.partnerId,
        displayName: currentUser.value.partnerDisplayName || '我的伴侣',
        username: 'partner'
      })
    } catch (error) {
      console.error('获取伴侣信息失败:', error)
    }
  }
}

// 获取信件详情
const fetchLetterDetail = async () => {
  try {
    const letterId = route.params.id
    if (!letterId) {
      showToast('信件ID不存在')
      return
    }

    const letter = await getLetterById(letterId)
    
    // 填充表单数据
    form.value = {
      title: letter.title,
      content: letter.content,
      receiverId: letter.receiverId,
      unlockTime: letter.unlockTime
    }
    
    // 设置时间选择器的默认值
    selectedTime.value = new Date(letter.unlockTime)
    
  } catch (error) {
    console.error('获取信件详情失败:', error)
    showToast('获取信件详情失败')
  }
}

// 收件人确认
const onReceiverConfirm = (value) => {
  form.value.receiverId = value.value
  showReceiverPicker.value = false
}

// 时间确认
const onTimeConfirm = (value) => {
  form.value.unlockTime = value.toISOString()
  showTimePicker.value = false
}

// 提交表单
const onSubmit = async () => {
  if (submitting.value) return
  
  try {
    submitting.value = true
    
    const letterId = route.params.id
    const response = await updateLetter(letterId, form.value)
    
    showToast('信件更新成功')
    router.push(`/letter/${letterId}`)
    
  } catch (error) {
    console.error('更新信件失败:', error)
    showToast(error.message || '更新信件失败')
  } finally {
    submitting.value = false
  }
}

// 返回上一页
const goBack = () => {
  router.go(-1)
}

onMounted(() => {
  fetchCurrentUser()
  fetchLetterDetail()
})
</script>

<style lang="scss" scoped>
.edit-letter-page {
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
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

.page-header {
  text-align: center;
  margin-bottom: 30px;
  padding-top: 60px;
  
  .page-title {
    font-size: 28px;
    color: white;
    margin-bottom: 10px;
    font-weight: bold;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  }
  
  .page-subtitle {
    font-size: 16px;
    color: rgba(255, 255, 255, 0.8);
    margin: 0;
  }
}

.form-container {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.letter-form {
  .form-field {
    margin-bottom: 20px;
    
    :deep(.van-field__label) {
      color: #333;
      font-weight: 600;
      font-size: 16px;
    }
    
    :deep(.van-field__control) {
      font-size: 16px;
      color: #333;
    }
    
    :deep(.van-field__placeholder) {
      color: #999;
    }
  }
  
  .content-field {
    :deep(.van-field__control) {
      min-height: 120px;
      line-height: 1.6;
    }
  }
}

.form-actions {
  display: flex;
  gap: 15px;
  margin-top: 30px;
  
  .submit-btn {
    flex: 1;
    height: 50px;
    background: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 100%);
    border: none;
    font-size: 18px;
    font-weight: 600;
    
    .btn-icon {
      margin-right: 8px;
    }
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(255, 107, 157, 0.3);
    }
  }
  
  .cancel-btn {
    flex: 1;
    height: 50px;
    background: rgba(255, 255, 255, 0.9);
    border: 2px solid #ddd;
    color: #666;
    font-size: 18px;
    font-weight: 600;
    
    &:hover {
      background: rgba(255, 255, 255, 1);
      border-color: #ccc;
    }
  }
}

@media (max-width: 768px) {
  .edit-letter-page {
    padding: 15px;
  }
  
  .form-container {
    padding: 20px;
  }
  
  .page-header .page-title {
    font-size: 24px;
  }
  
  .form-actions {
    flex-direction: column;
    
    .submit-btn,
    .cancel-btn {
      flex: none;
    }
  }
}
</style>
