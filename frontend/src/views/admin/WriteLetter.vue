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
          v-model="form.receiver"
          name="receiver"
          label="收件人"
          placeholder="请选择收件人"
          readonly
          :rules="[{ required: true, message: '请选择收件人' }]"
          @click="showReceiverPicker = true"
        />

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
          v-model="form.unlockDate"
          name="unlockDate"
          label="解锁日期"
          placeholder="选择信件解锁的日期"
          readonly
          :rules="[{ required: true, message: '请选择解锁日期' }]"
          @click="showDatePicker = true"
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

        <!-- <van-field
          v-model="form.unlockDateTime"
          name="unlockDateTime"
          label="完整时间"
          placeholder="自动生成的完整日期时间"
          readonly
          :rules="[{ required: true, message: '请先选择日期和时间' }]"
        /> -->

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
            发送给{{ form.receiver || (hasPartner ? '伴侣' : '自己') }}
          </van-button>
        </div>
      </van-form>
    </div>

    <!-- 日期选择器 -->
    <van-popup v-model:show="showDatePicker" position="bottom">
      <van-date-picker
        v-model="currentDate"
        title="选择日期"
        :min-date="new Date(2020, 0, 1)"
        :max-date="new Date(2030, 11, 31)"
        @confirm="onDateConfirm"
        @cancel="showDatePicker = false"
      />
    </van-popup>

    <!-- 时间选择器 -->
    <van-popup v-model:show="showTimePicker" position="bottom">
      <van-time-picker
        v-model="currentTime"
        title="选择时间"
        @confirm="onTimeConfirm"
        :columns-type="['hour', 'minute', 'second']"
        @cancel="showTimePicker = false"
      />
    </van-popup>

    <!-- 收件人选择器 -->
    <van-popup v-model:show="showReceiverPicker" position="bottom">
      <van-picker
        :columns="receiverOptions"
        @confirm="onReceiverConfirm"
        @cancel="showReceiverPicker = false"
      />
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
  unlockDate: '',
  unlockTime: '',
  unlockDateTime: '', // 组合后的完整日期时间
  receiverId: null,
  receiver: '' // 新增：收件人显示文本
})

const loading = ref(false)
const showDatePicker = ref(false)
const showTimePicker = ref(false)
const showReceiverPicker = ref(false) // 新增：收件人选择器

// 初始化当前日期为数组格式，用于日期选择器
const currentDate = ref([
  new Date().getFullYear().toString(),
  (new Date().getMonth() + 1).toString().padStart(2, '0'),
  new Date().getDate().toString().padStart(2, '0')
])

// 初始化当前时间为数组格式，用于时间选择器 [小时, 分钟, 秒]
const currentTime = ref([
  '12',
  '00',
  '00'
])

const minDate = ref(new Date())

// 收件人选项
const receiverOptions = computed(() => {
  const options = [
    { text: '自己', value: '自己' }
  ]
  
  if (hasPartner.value) {
    options.unshift({ text: '伴侣', value: '伴侣' })
  }
  
  return options
})

// 格式化时间显示
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN')
}

// 格式化时间用于API - 保持 "YYYY-MM-DD HH:mm:ss" 格式
const formatDateTimeForAPI = (dateTimeString) => {
  if (!dateTimeString) return null
  // 直接返回 "YYYY-MM-DD HH:mm:ss" 格式，与后端Jackson配置匹配
  return dateTimeString
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

// 日期选择确认
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
    
    // 格式化日期显示
    form.unlockDate = dayjs(selectedDate).format('YYYY-MM-DD')
    showDatePicker.value = false
    
    // 组合日期和时间
    combineDateTime()
    
    console.log('选择的日期:', form.unlockDate)
  } catch (error) {
    console.error('日期处理错误:', error)
    showDatePicker.value = false
  }
}

// 时间选择确认
const onTimeConfirm = (val) => {
  try {
    console.log('时间确认值:', val, '类型:', typeof val, '是否为数组:', Array.isArray(val))
    
    // 处理时间选择器返回的数组格式 ['12', '30', '00']
    let hours, minutes, seconds
    if (Array.isArray(val)) {
      [hours, minutes, seconds] = val
    } else if (val && val.selectedValues && Array.isArray(val.selectedValues)) {
      [hours, minutes, seconds] = val.selectedValues
    } else {
      throw new Error('无效的时间格式')
    }
    
    // 格式化时间显示
    form.unlockTime = `${hours.padStart(2, '0')}:${minutes.padStart(2, '0')}:${seconds.padStart(2, '0')}`
    showTimePicker.value = false
    
    // 组合日期和时间
    combineDateTime()
    
    console.log('选择的时间:', form.unlockTime)
  } catch (error) {
    console.error('时间处理错误:', error)
    showTimePicker.value = false
  }
}

// 收件人选择确认
const onReceiverConfirm = (value) => {
  form.receiver = value.selectedValues[0]
  showReceiverPicker.value = false
  
  // 根据选择的收件人设置receiverId
  if (form.receiver === '伴侣') {
    form.receiverId = userStore.partnerId
  } else {
    form.receiverId = userStore.userId
  }
  
  console.log('选择的收件人:', form.receiver, '收件人ID:', form.receiverId)
}

// 组合日期和时间
const combineDateTime = () => {
  if (form.unlockDate && form.unlockTime) {
    const combinedDateTime = `${form.unlockDate} ${form.unlockTime}`
    form.unlockDateTime = combinedDateTime
    console.log('组合后的日期时间:', form.unlockDateTime)
  } else {
    // 如果日期或时间未选择，清空完整时间
    form.unlockDateTime = ''
  }
}

// 提交表单
const submitForm = async () => {
  if (!form.receiver) {
    showToast('请选择收件人')
    return
  }
  
  if (!form.title.trim()) {
    showToast('请输入信件标题')
    return
  }
  
  if (!form.unlockDateTime) {
    showToast('请选择完整的解锁时间')
    return
  }
  
  if (!form.content.trim()) {
    showToast('请输入信件内容')
    return
  }
  
  // 直接发送信件，不需要预览确认
  await confirmSend()
}

// 重置表单
const resetForm = () => {
  form.title = ''
  form.content = ''
  form.receiver = ''
  form.receiverId = null
  
  // 重置为明天中午12点
  const tomorrow = getDefaultTime()
  form.unlockDate = dayjs(tomorrow).format('YYYY-MM-DD')
  form.unlockTime = '12:00:00'
  form.unlockDateTime = `${form.unlockDate} ${form.unlockTime}`
  
  // 更新日期选择器的初始值
  currentDate.value = [
    tomorrow.getFullYear().toString(),
    (tomorrow.getMonth() + 1).toString().padStart(2, '0'),
    tomorrow.getDate().toString().padStart(2, '0')
  ]
  
  // 更新时间选择器的初始值
  currentTime.value = ['12', '00', '00']
  
  console.log('表单重置完成，完整时间:', form.unlockDateTime)
}

// 确认发送
const confirmSend = async () => {
  loading.value = true
  try {
    console.log('用户信息:', userStore.userInfo)
    console.log('是否有伴侣:', userStore.hasPartner)
    console.log('用户ID:', userStore.userId)
    console.log('伴侣ID:', userStore.partnerId)
    console.log('选择的收件人:', form.receiver)
    console.log('收件人ID:', form.receiverId)
    
    console.log('发送信件数据:', form)
    
    // 只提交必要的字段，使用组合后的完整日期时间
    const letterData = {
      title: form.title,
      content: form.content,
      unlockTime: formatDateTimeForAPI(form.unlockDateTime), // 转换为ISO格式
      receiverId: form.receiverId
    }
    
    await createLetter(letterData)
    showToast(`信件发送给${form.receiver}成功！`)
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
  
  // 设置默认收件人
  if (userStore.hasPartner) {
    form.receiver = '伴侣'
    form.receiverId = userStore.partnerId
  } else {
    form.receiver = '自己'
    form.receiverId = userStore.userId
  }
  
  const tomorrow = getDefaultTime()
  form.unlockDate = dayjs(tomorrow).format('YYYY-MM-DD')
  form.unlockTime = '12:00:00'
  form.unlockDateTime = `${form.unlockDate} ${form.unlockTime}`
  
  // 更新日期选择器的初始值
  currentDate.value = [
    tomorrow.getFullYear().toString(),
    (tomorrow.getMonth() + 1).toString().padStart(2, '0'),
    tomorrow.getDate().toString().padStart(2, '0')
  ]
  
  // 更新时间选择器的初始值
  currentTime.value = ['12', '00', '00']
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
