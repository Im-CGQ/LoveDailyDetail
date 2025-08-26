<template>
  <div class="system-config-list">
    
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
      
      <!-- 分享设置 -->
      <van-cell-group title="分享设置">
        <van-cell title="分享链接过期时间">
          <template #right-icon>
            <van-field
              v-model="shareExpireMinutes"
              type="number"
              placeholder="请输入分钟数"
              :border="false"
              style="width: 120px; text-align: right;"
              @blur="onExpireMinutesChange"
            />
            <span style="margin-left: 5px; color: #969799;">分钟</span>
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
import { getTogetherDate, setTogetherDate, getBackgroundMusicAutoplay, setBackgroundMusicAutoplay, getShareExpireMinutes, setShareExpireMinutes } from '@/api/systemConfig'

const router = useRouter()

// 响应式数据
const togetherDate = ref('2024-01-01')
const backgroundMusicAutoplay = ref(true)
const shareExpireMinutes = ref(60)
const showDatePicker = ref(false)
// 初始化当前日期为数组格式，用于日期选择器
const selectedDate = ref([
  new Date().getFullYear().toString(),
  (new Date().getMonth() + 1).toString().padStart(2, '0'),
  new Date().getDate().toString().padStart(2, '0')
])

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
    
    // 将日期字符串转换为数组格式
    if (togetherDateValue) {
      const parts = togetherDateValue.split('-')
      if (parts.length === 3) {
        selectedDate.value = [
          parts[0],
          parts[1].padStart(2, '0'),
          parts[2].padStart(2, '0')
        ]
      }
    }
    
    // 加载背景音乐自动播放配置
    const autoplayValue = await getBackgroundMusicAutoplay()
    backgroundMusicAutoplay.value = autoplayValue
    
    // 加载分享过期时间配置
    const expireValue = await getShareExpireMinutes()
    shareExpireMinutes.value = expireValue
  } catch (error) {
    showToast(error.message)
  }
}

const onDateConfirm = async (value) => {
  try {
    console.log('日期确认值:', value, '类型:', typeof value, '是否为数组:', Array.isArray(value))
    
    // 处理日期选择器返回的数组格式 ['2021', '02', '01']
    let selectedDate
    if (Array.isArray(value)) {
      // 如果是数组格式，将其转换为Date对象
      const [year, month, day] = value
      selectedDate = new Date(parseInt(year), parseInt(month) - 1, parseInt(day))
    } else if (value && value.selectedValues && Array.isArray(value.selectedValues)) {
      // 如果是对象格式，获取selectedValues数组
      const [year, month, day] = value.selectedValues
      selectedDate = new Date(parseInt(year), parseInt(month) - 1, parseInt(day))
    } else if (value instanceof Date) {
      selectedDate = value
    } else {
      selectedDate = new Date(value)
    }
    
    // 验证日期是否有效
    if (isNaN(selectedDate.getTime())) {
      throw new Error('无效的日期值')
    }
    
    const dateStr = formatDate(selectedDate)
    await setTogetherDate(dateStr)
    togetherDate.value = dateStr
    showDatePicker.value = false
    showToast('设置成功')
  } catch (error) {
    console.error('日期处理错误:', error)
    showToast(error.message || '设置失败')
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

const onExpireMinutesChange = async () => {
  try {
    const minutes = parseInt(shareExpireMinutes.value)
    
    // 验证输入值
    if (isNaN(minutes) || minutes <= 0) {
      showToast('请输入有效的分钟数（大于0）')
      return
    }
    
    if (minutes > 10080) { // 7天 = 7 * 24 * 60 = 10080分钟
      showToast('过期时间不能超过7天')
      return
    }
    
    await setShareExpireMinutes(minutes)
    showToast('设置成功')
  } catch (error) {
    showToast(error.message)
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
