<template>
  <div class="edit-chat-record-page">
    <div class="page-header">
      <h2>编辑聊天记录</h2>
      <van-button type="default" @click="goBack">返回</van-button>
    </div>

    <div class="content" v-if="!loading">
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

    <div v-else class="loading-content">
      <van-loading type="spinner" size="24px">加载中...</van-loading>
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
import { useRouter, useRoute } from 'vue-router'
import { getChatRecordById, updateChatRecord } from '@/api/chatRecord'
import { showToast } from 'vant'

const router = useRouter()
const route = useRoute()
const loading = ref(true)
const submitting = ref(false)
const showTypePicker = ref(false)
const showDatePicker = ref(false)
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

// 加载聊天记录详情
const loadChatRecord = async () => {
  const id = route.params.id
  if (!id) {
    showToast('参数错误')
    router.push('/admin/chat-records')
    return
  }
  
  try {
    const record = await getChatRecordById(id)
    if (record) {
      form.value = {
        chatType: record.chatType,
        durationMinutes: record.durationMinutes.toString(),
        date: record.date,
        description: record.description || '',
        customType: record.customType || ''
      }
      selectedDate.value = new Date(record.date)
    }
  } catch (error) {
    console.error('加载聊天记录失败:', error)
    showToast('加载失败')
    router.push('/admin/chat-records')
  } finally {
    loading.value = false
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
    await updateChatRecord(route.params.id, {
      chatType: form.value.chatType,
      durationMinutes: parseInt(form.value.durationMinutes),
      date: form.value.date,
      description: form.value.description,
      customType: form.value.chatType === '自定义' ? form.value.customType : null
    })
    
    showToast('更新成功')
    router.push('/admin/chat-records')
  } catch (error) {
    showToast('更新失败')
    console.error('更新聊天记录失败:', error)
  } finally {
    submitting.value = false
  }
}

// 返回
const goBack = () => {
  router.push('/admin/chat-records')
}

onMounted(() => {
  loadChatRecord()
})
</script>

<style lang="scss" scoped>
.edit-chat-record-page {
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

.loading-content {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  background: white;
  border-radius: 12px;
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
