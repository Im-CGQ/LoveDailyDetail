<template>
  <div class="create-diary">
    
    <van-form @submit="onSubmit">
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
          v-model="form.videos"
          :max-count="3"
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
          创建回忆
        </van-button>
      </div>
    </van-form>
    
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
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import dayjs from 'dayjs'
import { createDiary } from '@/api/admin.js'
import { uploadImage, uploadVideo, uploadMusic } from '@/api/upload.js'

const router = useRouter()

const loading = ref(false)
const showDatePicker = ref(false)
// 初始化当前日期为数组格式，用于日期选择器
const currentDate = ref([
  new Date().getFullYear().toString(),
  (new Date().getMonth() + 1).toString().padStart(2, '0'),
  new Date().getDate().toString().padStart(2, '0')
])

const form = ref({
  title: '',
  date: dayjs().format('YYYY-MM-DD'),
  description: '',
  images: [],
  videos: [],
  backgroundMusic: []
})

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
    form.value.date = dayjs(selectedDate).format('YYYY-MM-DD')
    showDatePicker.value = false
    
    console.log('处理后的日期:', form.value.date)
  } catch (error) {
    console.error('日期处理错误:', error)
    // 使用当前日期作为默认值
    const now = new Date()
    form.value.date = dayjs(now).format('YYYY-MM-DD')
    showDatePicker.value = false
  }
}

const afterRead = async (file) => {
  try {
    const url = await uploadImage(file.file)
    file.url = url
    showToast('图片上传成功')
  } catch (error) {
    console.error('图片上传失败:', error)
    showToast('图片上传失败')
    // 移除上传失败的文件
    const index = form.value.images.findIndex(f => f.file === file.file)
    if (index > -1) {
      form.value.images.splice(index, 1)
    }
  }
}

const afterVideoRead = async (file) => {
  try {
    const url = await uploadVideo(file.file)
    file.url = url
    showToast('视频上传成功')
  } catch (error) {
    console.error('视频上传失败:', error)
    showToast('视频上传失败')
    // 移除上传失败的文件
    const index = form.value.videos.findIndex(f => f.file === file.file)
    if (index > -1) {
      form.value.videos.splice(index, 1)
    }
  }
}

const afterMusicRead = async (file) => {
  try {
    const url = await uploadMusic(file.file)
    file.url = url
    showToast('音乐上传成功')
  } catch (error) {
    console.error('音乐上传失败:', error)
    showToast('音乐上传失败')
    // 移除上传失败的文件
    const index = form.value.backgroundMusic.findIndex(f => f.file === file.file)
    if (index > -1) {
      form.value.backgroundMusic.splice(index, 1)
    }
  }
}

const onSubmit = async (values) => {
  loading.value = true
  
  try {
    // 准备提交的数据
    const diaryData = {
      title: form.value.title,
      date: form.value.date,
      description: form.value.description,
      images: form.value.images.map(file => file.url),
      videos: form.value.videos.map(file => file.url),
      backgroundMusic: form.value.backgroundMusic.length > 0 ? form.value.backgroundMusic[0].url : null
    }
    
    await createDiary(diaryData)
    showToast('创建成功')
    router.push('/admin/diary/list')
  } catch (error) {
    console.error('创建失败:', error)
    showToast('创建失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.create-diary {
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
    background: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 100%) !important;
    border: none !important;
    height: 48px;
    border-radius: 24px;
    font-size: 16px;
    font-weight: 500;
    transition: all 0.3s ease;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 20px rgba(255, 107, 157, 0.3);
      background: linear-gradient(135deg, #ff5a8c 0%, #ff7a9a 100%) !important;
    }
  }
}
</style> 