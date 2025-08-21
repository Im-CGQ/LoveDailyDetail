<template>
  <div class="system-config-list">
    <van-nav-bar
      title="回忆配置"
      left-text="返回"
      left-arrow
      @click-left="goBack"
    >
      <template #title>
        <van-icon name="star-o" style="margin-right: 4px;" />
        回忆配置
      </template>
    </van-nav-bar>
    
    <div class="content">
      <!-- 在一起时间 -->
      <van-cell-group title="在一起时间">
        <van-cell title="在一起的时间" :value="togetherDate" @click="showDatePicker = true" />
      </van-cell-group>
      
      <!-- 背景音乐 -->
      <van-cell-group title="背景音乐">
        <van-cell title="自动播放背景音乐">
          <template #right-icon>
            <van-switch v-model="backgroundMusicAutoplay" @change="onAutoplayChange" />
          </template>
        </van-cell>
      </van-cell-group>
      

    </div>
    
    <!-- 日期选择器 -->
    <van-popup v-model:show="showDatePicker" position="bottom">
      <van-date-picker
        v-model="selectedDate"
        title="选择在一起的时间"
        :min-date="minDate"
        :max-date="maxDate"
        @confirm="onDateConfirm"
        @cancel="showDatePicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import { getTogetherDate, setTogetherDate, getBackgroundMusicAutoplay, setBackgroundMusicAutoplay } from '@/api/systemConfig'

const router = useRouter()

// 响应式数据
const togetherDate = ref('2024-01-01')
const backgroundMusicAutoplay = ref(true)
const showDatePicker = ref(false)
const selectedDate = ref(new Date(2024, 0, 1))

const minDate = new Date(2020, 0, 1)
const maxDate = new Date()

// 方法
const goBack = () => {
  router.back()
}

const loadConfigs = async () => {
  try {
    // 加载在一起时间
    const togetherDateValue = await getTogetherDate()
    togetherDate.value = togetherDateValue
    selectedDate.value = new Date(togetherDateValue)
    
    // 加载背景音乐自动播放配置
    const autoplayValue = await getBackgroundMusicAutoplay()
    backgroundMusicAutoplay.value = autoplayValue
  } catch (error) {
    showToast(error.message)
  }
}

const onDateConfirm = async (value) => {
  try {
    const dateStr = formatDate(value)
    await setTogetherDate(dateStr)
    togetherDate.value = dateStr
    showDatePicker.value = false
    showToast('设置成功')
  } catch (error) {
    showToast(error.message)
  }
}

const onAutoplayChange = async (value) => {
  try {
    await setBackgroundMusicAutoplay(value)
    showToast('设置成功')
  } catch (error) {
    showToast(error.message)
    // 恢复原值
    backgroundMusicAutoplay.value = !value
  }
}



const formatDate = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 生命周期
onMounted(() => {
  loadConfigs()
})
</script>

<style lang="scss" scoped>
.system-config-list {
  min-height: 100vh;
  background-color: #f7f8fa;
}

.content {
  padding: 16px;
}



:deep(.van-cell-group__title) {
  font-size: 14px;
  color: #969799;
  margin-bottom: 8px;
}

:deep(.van-cell) {
  border-radius: 8px;
  margin-bottom: 8px;
}
</style>
