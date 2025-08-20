<template>
  <div class="edit-diary">
    <h2>编辑回忆</h2>
    
    <van-form @submit="onSubmit" v-if="form">
      <van-cell-group inset>
        <van-field
          v-model="form.title"
          name="title"
          label="标题"
          placeholder="请输入回忆标题"
          :rules="[{ required: true, message: '请输入标题' }]"
        />
        
        <van-field
          v-model="form.date"
          name="date"
          label="日期"
          placeholder="请选择日期"
          readonly
          @click="showDatePicker = true"
          :rules="[{ required: true, message: '请选择日期' }]"
        />
        
        <van-field
          v-model="form.description"
          name="description"
          label="描述"
          type="textarea"
          placeholder="请输入回忆描述"
          rows="4"
          :rules="[{ required: true, message: '请输入描述' }]"
        />
      </van-cell-group>
      
      <div class="upload-section">
        <h3>图片上传</h3>
        <van-uploader
          v-model="form.images"
          multiple
          :max-count="5"
          :after-read="afterRead"
        />
      </div>
      
      <div class="upload-section">
        <h3>视频上传</h3>
        <van-uploader
          v-model="form.video"
          :max-count="1"
          accept="video/*"
          :after-read="afterVideoRead"
        />
      </div>
      
      <div class="upload-section">
        <h3>背景音乐</h3>
        <van-uploader
          v-model="form.backgroundMusic"
          :max-count="1"
          accept="audio/*"
          :after-read="afterMusicRead"
        />
      </div>
      
      <div class="submit-section">
        <van-button 
          round 
          block 
          type="primary" 
          native-type="submit"
          :loading="loading"
        >
          保存修改
        </van-button>
      </div>
    </van-form>
    
    <van-popup v-model:show="showDatePicker" position="bottom">
      <van-date-picker
        v-model="currentDate"
        title="选择日期"
        @confirm="onDateConfirm"
        @cancel="showDatePicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast } from 'vant'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const showDatePicker = ref(false)
const currentDate = ref(new Date())
const form = ref(null)

const onDateConfirm = (value) => {
  form.value.date = dayjs(value).format('YYYY-MM-DD')
  showDatePicker.value = false
}

const afterRead = (file) => {
  file.url = URL.createObjectURL(file.file)
  showToast('图片上传成功')
}

const afterVideoRead = (file) => {
  file.url = URL.createObjectURL(file.file)
  showToast('视频上传成功')
}

const afterMusicRead = (file) => {
  file.url = URL.createObjectURL(file.file)
  showToast('音乐上传成功')
}

const onSubmit = async (values) => {
  loading.value = true
  
  try {
    await new Promise(resolve => setTimeout(resolve, 1000))
    showToast('保存成功')
    router.push('/admin/diary/list')
  } catch (error) {
    showToast('保存失败，请重试')
  } finally {
    loading.value = false
  }
}

const loadDiary = () => {
  // 模拟加载数据
  const mockDiary = {
    id: route.params.id,
    title: '我们的第一次约会',
    date: '2024-01-15',
    description: '今天是我们第一次约会，一起去看了电影，吃了火锅，度过了美好的一天。',
    images: [],
    video: [],
    backgroundMusic: []
  }
  
  form.value = mockDiary
  currentDate.value = new Date(mockDiary.date)
}

onMounted(() => {
  loadDiary()
})
</script>

<style scoped>
.edit-diary {
  h2 {
    text-align: center;
    margin-bottom: 20px;
    background: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    font-size: 28px;
    font-weight: bold;
  }
}

.upload-section {
  margin: 20px 0;
  padding: 0 16px;
  
  h3 {
    margin-bottom: 10px;
    color: #333;
    font-size: 16px;
    font-weight: 500;
    position: relative;
    
    &::before {
      content: '';
      position: absolute;
      left: -8px;
      top: 50%;
      transform: translateY(-50%);
      width: 4px;
      height: 16px;
      background: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 100%);
      border-radius: 2px;
    }
  }
}

.submit-section {
  margin: 30px 16px;
  
  :deep(.van-button--primary) {
    background: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 100%);
    border: none;
    height: 48px;
    border-radius: 24px;
    font-size: 16px;
    font-weight: 500;
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(255, 107, 157, 0.3);
    }
  }
}
</style> 