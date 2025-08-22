<template>
  <div class="write-letter">
    <div class="header">
      <h2>写一封信</h2>
      <p class="subtitle" v-if="hasPartner">给你的伴侣写一封信，选择解锁时间，让爱在特定时刻绽放</p>
      <p class="subtitle" v-else>给自己写一封信，选择解锁时间，让未来的自己感受此刻的心情</p>
    </div>

    <div class="form-container">
      <van-form @submit="submitForm">
        <van-field
          v-model="form.title"
          name="title"
          label="信件标题"
          placeholder="给你的信起一个温暖的标题"
          :rules="[{ required: true, message: '请输入信件标题' }]"
          maxlength="50"
          show-word-limit
        />

        <van-field
          v-model="form.unlockTime"
          name="unlockTime"
          label="解锁时间"
          placeholder="选择信件解锁的时间"
          readonly
          :rules="[{ required: true, message: '请选择解锁时间' }]"
          @click="showTimePicker = true"
        />

        <van-field
          v-model="form.content"
          name="content"
          label="信件内容"
          type="textarea"
          :placeholder="hasPartner ? '在这里写下你想对伴侣说的话...' : '在这里写下你想对未来的自己说的话...'"
          :rules="[{ required: true, message: '请输入信件内容' }]"
          rows="8"
          autosize
        />

        <div class="toolbar">
          <van-button 
            size="small" 
            @click="insertText('❤️')"
            type="primary"
            plain
          >
            ❤️ 爱心
          </van-button>
          <van-button 
            size="small" 
            @click="insertText('🌸')"
            type="primary"
            plain
          >
            🌸 花朵
          </van-button>
          <van-button 
            size="small" 
            @click="insertText('✨')"
            type="primary"
            plain
          >
            ✨ 星星
          </van-button>
          <van-button 
            size="small" 
            @click="insertText('💕')"
            type="primary"
            plain
          >
            💕 爱心
          </van-button>
        </div>

        <div class="form-actions">
          <van-button 
            type="primary" 
            native-type="submit" 
            :loading="loading"
            block
          >
            {{ hasPartner ? '发送给伴侣' : '发送给自己' }}
          </van-button>
        </div>
      </van-form>
    </div>

    <!-- 时间选择器 -->
    <van-popup v-model:show="showTimePicker" position="bottom">
      <van-date-picker
        v-model="currentDate"
        title="选择解锁时间"
        :min-date="minDate"
        @confirm="onTimeConfirm"
        @cancel="showTimePicker = false"
      />
    </van-popup>

    <!-- 预览模态框 -->
    <van-popup v-model:show="previewVisible" position="center" :style="{ width: '90%', maxWidth: '500px' }">
      <div class="preview-content">
        <div class="preview-header">
          <h3>{{ form.title }}</h3>
          <p class="unlock-time">解锁时间：{{ formatDateTime(form.unlockTime) }}</p>
          <p class="receiver-info">{{ hasPartner ? '收件人：伴侣' : '收件人：自己' }}</p>
        </div>
        <div class="content" v-html="form.content"></div>
        <div class="preview-actions">
          <van-button @click="previewVisible = false">取消</van-button>
          <van-button type="primary" @click="confirmSend" :loading="loading">
            确认发送
          </van-button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { createLetter } from '@/api/letter'
import { showToast } from 'vant'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'

const router = useRouter()
const userStore = useUserStore()

// 计算是否有伴侣关系
const hasPartner = computed(() => userStore.hasPartner)

// 设置默认解锁时间为明天
const getDefaultTime = () => {
  const tomorrow = new Date()
  tomorrow.setDate(tomorrow.getDate() + 1)
  tomorrow.setHours(12, 0, 0, 0) // 设置为明天中午12点
  return tomorrow
}

// 响应式数据
const form = reactive({
  title: '',
  content: '',
  unlockTime: '',
  receiverId: null
})

const loading = ref(false)
const previewVisible = ref(false)
const showTimePicker = ref(false)
// 初始化当前日期为数组格式，用于日期选择器
const currentDate = ref([
  new Date().getFullYear().toString(),
  (new Date().getMonth() + 1).toString().padStart(2, '0'),
  new Date().getDate().toString().padStart(2, '0')
])

const minDate = ref(new Date())

// 格式化时间显示
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN')
}

// 格式化时间用于API
const formatDateTimeForAPI = (date) => {
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 插入表情符号
const insertText = (text) => {
  const textarea = document.querySelector('textarea[name="content"]')
  if (textarea) {
    const start = textarea.selectionStart
    const end = textarea.selectionEnd
    const value = textarea.value
    form.content = value.substring(0, start) + text + value.substring(end)
    
    // 设置光标位置
    nextTick(() => {
      textarea.focus()
      textarea.setSelectionRange(start + text.length, start + text.length)
    })
  }
}

// 时间选择确认
const onTimeConfirm = (val) => {
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
    
    // 将选择的日期设置为当天的中午12点
    selectedDate.setHours(12, 0, 0, 0)
    form.unlockTime = formatDateTimeForAPI(selectedDate)
    showTimePicker.value = false
    
    console.log('处理后的日期:', form.unlockTime)
  } catch (error) {
    console.error('日期处理错误:', error)
    // 使用明天中午12点作为默认值
    const tomorrow = getDefaultTime()
    form.unlockTime = formatDateTimeForAPI(tomorrow)
    showTimePicker.value = false
  }
}

// 提交表单
const submitForm = async () => {
  if (!form.title.trim()) {
    showToast('请输入信件标题')
    return
  }
  
  if (!form.unlockTime) {
    showToast('请选择解锁时间')
    return
  }
  
  if (!form.content.trim()) {
    showToast('请输入信件内容')
    return
  }
  
  previewVisible.value = true
}

// 重置表单
const resetForm = () => {
  form.title = ''
  form.content = ''
  form.receiverId = null
  
  // 重置为明天中午12点
  const tomorrow = getDefaultTime()
  form.unlockTime = formatDateTimeForAPI(tomorrow)
  
  // 更新日期选择器的初始值
  currentDate.value = [
    tomorrow.getFullYear().toString(),
    (tomorrow.getMonth() + 1).toString().padStart(2, '0'),
    tomorrow.getDate().toString().padStart(2, '0')
  ]
}

// 确认发送
const confirmSend = async () => {
  loading.value = true
  try {
    console.log('用户信息:', userStore.userInfo)
    console.log('是否有伴侣:', userStore.hasPartner)
    console.log('用户ID:', userStore.userId)
    console.log('伴侣ID:', userStore.partnerId)
    
    if (userStore.hasPartner) {
      // 有伴侣关系，发送给伴侣
      form.receiverId = userStore.partnerId
    } else {
      // 没有伴侣关系，发送给自己
      form.receiverId = userStore.userId
    }
    
    console.log('发送信件数据:', form)
    
    await createLetter(form)
    showToast(hasPartner.value ? '信件发送给伴侣成功！' : '信件发送给自己成功！')
    previewVisible.value = false
    resetForm()
    router.push('/admin/letters')
  } catch (error) {
    console.error('发送信件失败:', error)
    showToast(error.message || '发送失败')
  } finally {
    loading.value = false
  }
}



// 组件挂载时设置默认时间
onMounted(async () => {
  // 确保用户状态已初始化
  if (!userStore.userInfo) {
    await userStore.initUserState()
  }
  
  console.log('组件挂载时的用户信息:', userStore.userInfo)
  console.log('是否有伴侣:', userStore.hasPartner)
  
  const tomorrow = getDefaultTime()
  form.unlockTime = formatDateTimeForAPI(tomorrow)
  
  // 更新日期选择器的初始值
  currentDate.value = [
    tomorrow.getFullYear().toString(),
    (tomorrow.getMonth() + 1).toString().padStart(2, '0'),
    tomorrow.getDate().toString().padStart(2, '0')
  ]
})
</script>

<style lang="scss" scoped>
.write-letter {
  padding: 20px;
  
  .header {
    text-align: center;
    margin-bottom: 20px;
    
    h2 {
      color: #333;
      margin-bottom: 10px;
      font-size: 24px;
    }
    
    .subtitle {
      color: #666;
      font-size: 14px;
    }
  }
  
  .form-container {
    border-radius: 8px;
    overflow: hidden;
  }
  
  .toolbar {
    padding: 15px;
    border-top: 1px solid #eee;
    display: flex;
    justify-content: center;
    gap: 10px;
    
    :deep(.van-button) {
      font-size: 14px;
      padding: 8px 12px;
      border-radius: 6px;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateY(-1px);
        box-shadow: 0 2px 8px rgba(255, 107, 157, 0.2);
      }
    }
  }
  
  .form-actions {
    padding: 20px;
  }
  
  .preview-content {
    padding: 20px;
    
    .preview-header {
      text-align: center;
      margin-bottom: 20px;
      
      h3 {
        color: #333;
        margin-bottom: 10px;
        font-size: 18px;
      }
      
      .unlock-time {
        color: #666;
        font-size: 14px;
        margin-bottom: 5px;
      }
      
      .receiver-info {
        color: #ff6b9d;
        font-size: 14px;
        font-weight: 500;
      }
    }
    
    .content {
      line-height: 1.8;
      color: #333;
      max-height: 300px;
      overflow-y: auto;
      padding: 15px;
      background: #f8f9fa;
      border-radius: 8px;
      margin-bottom: 20px;
    }
    
    .preview-actions {
      display: flex;
      gap: 10px;
      justify-content: center;
    }
  }
}

:deep(.van-field__label) {
  color: #333;
  font-weight: 500;
}

:deep(.van-field__control) {
  color: #333;
}

:deep(.van-button--primary) {
  background: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 100%);
  border: none;
}

:deep(.van-button--primary.van-button--plain) {
  background: transparent;
  color: #ff6b9d;
  border-color: #ff6b9d;
  font-size: 16px;
  padding: 8px 12px;
  
  &:hover {
    background: rgba(255, 107, 157, 0.1);
    color: #ff6b9d;
  }
  
  &:active {
    background: transparent;
    color: #ff6b9d;
  }
  
  &:focus {
    background: transparent;
    color: #ff6b9d;
  }
}
</style>
