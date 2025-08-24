<template>
  <div class="create-chat-record-page">
    <div class="page-header">
      <h2>添加聊天记录</h2>
      <van-button type="default" @click="goBack">返回</van-button>
    </div>

    <div class="content">
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
          rows="4"
        />
        
        <van-field
          v-if="form.chatType === '自定义'"
          v-model="form.customType"
          label="自定义类型"
          placeholder="请输入自定义聊天类型"
          :rules="[{ required: true, message: '请输入自定义类型' }]"
        />
        
        <div class="form-actions">
          <van-button @click="goBack" type="default" size="large">取消</van-button>
          <van-button type="primary" native-type="submit" size="large" :loading="submitting">保存</van-button>
        </div>
      </van-form>
    </div>

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
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { createChatRecord } from '@/api/admin'
import { showToast } from 'vant'

const router = useRouter()
const submitting = ref(false)
const showTypePicker = ref(false)
const showDatePicker = ref(false)
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
    router.push('/admin/chat-records')
  } catch (error) {
    showToast('添加失败')
    console.error('添加聊天记录失败:', error)
  } finally {
    submitting.value = false
  }
}

// 返回
const goBack = () => {
  router.push('/admin/chat-records')
}
</script>

<style lang="scss" scoped>
.create-chat-record-page {
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
}

.content {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.form-actions {
  display: flex;
  gap: 15px;
  margin-top: 30px;
  
  .van-button {
    flex: 1;
  }
}
</style>
