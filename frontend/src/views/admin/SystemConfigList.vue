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
      
      <!-- 看信背景音乐 -->
      <van-cell-group title="看信背景音乐">
        <van-cell title="背景音乐">
          <template #right-icon>
            <div class="music-upload-section">
                             <van-uploader
                 v-model="letterBackgroundMusic"
                 :max-count="1"
                 :after-read="onMusicUpload"
                 :before-delete="onMusicDelete"
                 accept="audio/*"
                 :show-upload="letterBackgroundMusic.length === 0"
               >
                 <van-button size="small" type="primary">上传音乐</van-button>
               </van-uploader>
               <van-button 
                 size="small" 
                 type="default" 
                 style="margin-left: 8px;"
                 @click="debugUserInfo"
               >
                 调试用户信息
               </van-button>
            </div>
          </template>
        </van-cell>
        
        <!-- 音乐预览 -->
        <div class="music-preview-section" v-if="letterBackgroundMusic.length > 0">
          <van-cell title="当前音乐">
            <template #right-icon>
              <span class="music-name">{{ letterBackgroundMusic[0].file?.name || '背景音乐' }}</span>
              <span class="music-status" :class="letterBackgroundMusic[0].status">
                {{ getMusicStatusText(letterBackgroundMusic[0].status) }}
              </span>
            </template>
          </van-cell>
          
          <!-- 音乐播放器 -->
          <div class="music-player-container" v-if="letterBackgroundMusic[0].url">
            <audio 
              :src="letterBackgroundMusic[0].url" 
              ref="audioElement"
              preload="metadata"
              @loadedmetadata="onMusicLoaded"
              @error="onMusicError"
            ></audio>
            <div class="music-controls">
              <button 
                type="button"
                class="play-btn" 
                @click.stop="toggleMusic"
                :class="{ 'playing': isMusicPlaying }"
              >
                <span v-if="isMusicPlaying">⏸️</span>
                <span v-else>▶️</span>
              </button>
              <div class="music-progress">
                <div class="progress-bar" @click.stop="seekMusic" ref="musicProgressBar">
                  <div class="progress-fill" :style="{ width: musicProgress + '%' }"></div>
                </div>
                <span class="time-display">{{ formatTime(currentMusicTime) }} / {{ formatTime(musicDuration) }}</span>
              </div>
            </div>
          </div>
          
          <!-- 上传状态指示器 -->
          <div class="uploading-indicator" v-if="letterBackgroundMusic[0].status === 'uploading'">
            <van-loading size="20px" color="#1989fa">上传中...</van-loading>
          </div>
          <div class="error-indicator" v-else-if="letterBackgroundMusic[0].status === 'failed'">
            <van-icon name="warning-o" color="#ee0a24" />
            <span style="color: #ee0a24; margin-left: 5px;">上传失败</span>
          </div>
        </div>
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

      <!-- 纪念日管理 -->
      <van-cell-group title="纪念日管理">
        <van-cell title="纪念日列表" @click="showAnniversaryDialog = true" />
        <div class="anniversary-list" v-if="anniversaryDates.length > 0">
          <div 
            v-for="(date, index) in anniversaryDates" 
            :key="index" 
            class="anniversary-item"
          >
            <span class="anniversary-date">{{ date.date }}</span>
            <span class="anniversary-name">{{ date.name }}</span>
            <div class="anniversary-actions">
              <van-button 
                size="mini" 
                type="primary" 
                @click="editAnniversary(index)"
                style="margin-right: 8px;"
              >
                编辑
              </van-button>
              <van-button 
                size="mini" 
                type="danger" 
                @click="removeAnniversary(index)"
              >
                删除
              </van-button>
            </div>
          </div>
        </div>
      </van-cell-group>

      <!-- 下次见面日 -->
      <van-cell-group title="下次见面日">
        <van-cell title="下次见面日期" :value="nextMeetingDate" @click="showNextMeetingDatePicker = true" />
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

    <!-- 下次见面日期选择器 -->
    <van-popup v-model:show="showNextMeetingDatePicker" position="bottom">
      <van-date-picker
        v-model="selectedNextMeetingDate"
        title="选择下次见面日期"
        :min-date="new Date()"
        :max-date="maxDate"
        @confirm="onNextMeetingDateConfirm"
        @cancel="showNextMeetingDatePicker = false"
      />
    </van-popup>

    <!-- 纪念日添加对话框 -->
    <van-dialog
      v-model:show="showAnniversaryDialog"
      title="添加纪念日"
      show-cancel-button
      @confirm="addAnniversary"
    >
      <div class="anniversary-form">
        <van-field
          v-model="newAnniversaryName"
          label="纪念日名称"
          placeholder="请输入纪念日名称"
          :border="false"
        />
        <van-field
          v-model="newAnniversaryDate"
          label="纪念日日期"
          placeholder="请选择日期"
          readonly
          @click="showNewAnniversaryDatePicker = true"
          :border="false"
        />
      </div>
    </van-dialog>

    <!-- 纪念日编辑对话框 -->
    <van-dialog
      v-model:show="showEditAnniversaryDialog"
      title="编辑纪念日"
      show-cancel-button
      @confirm="updateAnniversary"
    >
      <div class="anniversary-form">
        <van-field
          v-model="editingAnniversaryName"
          label="纪念日名称"
          placeholder="请输入纪念日名称"
          :border="false"
        />
        <van-field
          v-model="editingAnniversaryDate"
          label="纪念日日期"
          placeholder="请选择日期"
          readonly
          @click="showEditAnniversaryDatePicker = true"
          :border="false"
        />
      </div>
    </van-dialog>

    <!-- 新纪念日日期选择器 -->
    <van-popup v-model:show="showNewAnniversaryDatePicker" position="bottom">
      <van-date-picker
        v-model="selectedNewAnniversaryDate"
        title="选择纪念日日期"
        :min-date="minDate"
        :max-date="maxDate"
        @confirm="onNewAnniversaryDateConfirm"
        @cancel="showNewAnniversaryDatePicker = false"
      />
    </van-popup>

    <!-- 编辑纪念日日期选择器 -->
    <van-popup v-model:show="showEditAnniversaryDatePicker" position="bottom">
      <van-date-picker
        v-model="selectedEditAnniversaryDate"
        title="选择纪念日日期"
        :min-date="minDate"
        :max-date="maxDate"
        @confirm="onEditAnniversaryDateConfirm"
        @cancel="showEditAnniversaryDatePicker = false"
      />
    </van-popup>
    

  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showConfirmDialog } from 'vant'
import { getTogetherDate, setTogetherDate, getBackgroundMusicAutoplay, setBackgroundMusicAutoplay, getShareExpireMinutes, setShareExpireMinutes, getAnniversaryDates, setAnniversaryDates, getNextMeetingDate, setNextMeetingDate } from '@/api/systemConfig'
import { getLetterBackgroundMusicByUserIdPublic, setLetterBackgroundMusicByUserId } from '@/api/music'
import { uploadMusic } from '@/api/upload.js'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const togetherDate = ref('2024-01-01')
const backgroundMusicAutoplay = ref(true)
const shareExpireMinutes = ref(60)
const letterBackgroundMusic = ref([])
const showDatePicker = ref(false)
const nextMeetingDate = ref('')
const anniversaryDates = ref([])

// 纪念日相关
const showAnniversaryDialog = ref(false)
const showNewAnniversaryDatePicker = ref(false)
const newAnniversaryName = ref('')
const newAnniversaryDate = ref('')
const selectedNewAnniversaryDate = ref([
  new Date().getFullYear().toString(),
  (new Date().getMonth() + 1).toString().padStart(2, '0'),
  new Date().getDate().toString().padStart(2, '0')
])

// 编辑纪念日相关
const showEditAnniversaryDialog = ref(false)
const showEditAnniversaryDatePicker = ref(false)
const editingAnniversaryIndex = ref(-1)
const editingAnniversaryName = ref('')
const editingAnniversaryDate = ref('')
const selectedEditAnniversaryDate = ref([
  new Date().getFullYear().toString(),
  (new Date().getMonth() + 1).toString().padStart(2, '0'),
  new Date().getDate().toString().padStart(2, '0')
])

// 下次见面日相关
const showNextMeetingDatePicker = ref(false)
const selectedNextMeetingDate = ref([
  new Date().getFullYear().toString(),
  (new Date().getMonth() + 1).toString().padStart(2, '0'),
  new Date().getDate().toString().padStart(2, '0')
])

// 音乐播放相关
const audioElement = ref(null)
const musicProgressBar = ref(null)
const isMusicPlaying = ref(false)
const currentMusicTime = ref(0)
const musicDuration = ref(0)
const musicProgress = ref(0)
const musicProgressTimer = ref(null)
// 初始化当前日期为数组格式，用于日期选择器
const selectedDate = ref([
  new Date().getFullYear().toString(),
  (new Date().getMonth() + 1).toString().padStart(2, '0'),
  new Date().getDate().toString().padStart(2, '0')
])

const minDate = new Date(2020, 0, 1)
const maxDate = new Date(2030, 11, 31) // 允许选择到2030年底



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
    
    // 加载纪念日列表
    const anniversaryDatesValue = await getAnniversaryDates()
    try {
      anniversaryDates.value = JSON.parse(anniversaryDatesValue)
    } catch (e) {
      anniversaryDates.value = []
    }
    
    // 加载下次见面日期
    const nextMeetingDateValue = await getNextMeetingDate()
    nextMeetingDate.value = nextMeetingDateValue
    
    // 加载当前用户的看信背景音乐配置
    const currentUserId = getCurrentUserId()
    if (currentUserId) {
      const musicUrl = await getLetterBackgroundMusicByUserIdPublic(currentUserId)
      if (musicUrl) {
        letterBackgroundMusic.value = [{
          url: musicUrl,
          status: 'done',
          fileName: musicUrl.split('/').pop()
        }]
        // 初始化音乐播放器
        nextTick(() => {
          initAudioListeners()
        })
      }
    } else {
      console.warn('无法获取当前用户ID，背景音乐功能将不可用')
      showToast('无法获取用户信息，请重新登录')
    }
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

// 音乐上传处理
const onMusicUpload = async (file) => {
  try {
    // 设置上传状态
    file.status = 'uploading'
    
    // 上传音乐文件
    const url = await uploadMusic(file.file)
    
    // 更新文件状态和URL
    file.url = url
    file.status = 'done'
    file.fileName = file.file.name
    
    // 保存到当前用户的系统配置
    const currentUserId = getCurrentUserId()
    if (currentUserId) {
      await setLetterBackgroundMusicByUserId(currentUserId, url)
      showToast('音乐上传成功')
    } else {
      showToast('无法获取用户信息，请重新登录')
      console.error('音乐上传失败：无法获取用户ID')
      return
    }
    
    // 初始化音乐播放器
    nextTick(() => {
      initAudioListeners()
    })
  } catch (error) {
    file.status = 'failed'
    showToast(error.message || '音乐上传失败')
  }
}

// 音乐删除处理
const onMusicDelete = async (file) => {
  try {
    // 清空当前用户的看信背景音乐配置
    const currentUserId = getCurrentUserId()
    if (currentUserId) {
      await setLetterBackgroundMusicByUserId(currentUserId, '')
      letterBackgroundMusic.value = []
      showToast('音乐删除成功')
    } else {
      showToast('无法获取用户信息，请重新登录')
      console.error('音乐删除失败：无法获取用户ID')
      return
    }
  } catch (error) {
    showToast(error.message || '音乐删除失败')
  }
}

// 获取音乐状态文本
const getMusicStatusText = (status) => {
  switch (status) {
    case 'uploading':
      return '上传中'
    case 'done':
      return '已完成'
    case 'failed':
      return '上传失败'
    default:
      return '未知状态'
  }
}

// 音乐播放相关方法
const onMusicLoaded = () => {
  if (audioElement.value) {
    musicDuration.value = audioElement.value.duration
  }
}

const onMusicError = () => {
  showToast('音乐加载失败')
}

const toggleMusic = () => {
  if (!audioElement.value) return
  
  if (isMusicPlaying.value) {
    audioElement.value.pause()
  } else {
    audioElement.value.play()
  }
}

const startMusicProgressTimer = () => {
  if (musicProgressTimer.value) return
  
  musicProgressTimer.value = setInterval(() => {
    if (audioElement.value && !audioElement.value.paused) {
      currentMusicTime.value = audioElement.value.currentTime
      musicProgress.value = (audioElement.value.currentTime / audioElement.value.duration) * 100
    }
  }, 100)
}

const stopMusicProgressTimer = () => {
  if (musicProgressTimer.value) {
    clearInterval(musicProgressTimer.value)
    musicProgressTimer.value = null
  }
}

const formatTime = (seconds) => {
  if (!seconds || isNaN(seconds)) return '0:00'
  const mins = Math.floor(seconds / 60)
  const secs = Math.floor(seconds % 60)
  return `${mins}:${secs.toString().padStart(2, '0')}`
}

const seekMusic = (event) => {
  if (!audioElement.value || !musicProgressBar.value) return
  
  const rect = musicProgressBar.value.getBoundingClientRect()
  const clickX = event.clientX - rect.left
  const progressWidth = rect.width
  const seekTime = (clickX / progressWidth) * audioElement.value.duration
  
  audioElement.value.currentTime = seekTime
}

const initAudioListeners = () => {
  if (!audioElement.value) return
  
  audioElement.value.addEventListener('play', () => {
    isMusicPlaying.value = true
    startMusicProgressTimer()
  })
  
  audioElement.value.addEventListener('pause', () => {
    isMusicPlaying.value = false
    stopMusicProgressTimer()
  })
  
  audioElement.value.addEventListener('ended', () => {
    isMusicPlaying.value = false
    stopMusicProgressTimer()
    currentMusicTime.value = 0
    musicProgress.value = 0
  })
}

// 清理音频事件监听器
const cleanupAudioListeners = () => {
  if (!audioElement.value) return
  
  audioElement.value.removeEventListener('play', () => {})
  audioElement.value.removeEventListener('pause', () => {})
  audioElement.value.removeEventListener('ended', () => {})
}



const formatDate = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 获取当前用户ID的方法
const getCurrentUserId = () => {
  console.log('从store获取用户ID...')
  
  // 首先尝试从store获取
  if (userStore.userId) {
    console.log('从store成功获取用户ID:', userStore.userId)
    return userStore.userId
  }
  
  // 如果store中没有，尝试初始化store
  if (!userStore.isLoggedIn) {
    console.log('用户未登录，尝试初始化用户状态...')
    userStore.initUserState()
    
    // 等待一下再检查
    setTimeout(() => {
      if (userStore.userId) {
        console.log('初始化后获取到用户ID:', userStore.userId)
        return userStore.userId
      }
    }, 100)
  }
  
  // 如果还是没有，显示调试信息
  console.log('当前store状态:', {
    isLoggedIn: userStore.isLoggedIn,
    userId: userStore.userId,
    userInfo: userStore.userInfo
  })
  
  return userStore.userId
}

// 纪念日相关方法
const onNewAnniversaryDateConfirm = (value) => {
  try {
    // 处理日期选择器返回的数组格式 ['2021', '02', '01']
    let selectedDate
    if (Array.isArray(value)) {
      const [year, month, day] = value
      selectedDate = new Date(parseInt(year), parseInt(month) - 1, parseInt(day))
    } else if (value && value.selectedValues && Array.isArray(value.selectedValues)) {
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
    newAnniversaryDate.value = dateStr
    showNewAnniversaryDatePicker.value = false
  } catch (error) {
    console.error('日期处理错误:', error)
    showToast(error.message || '日期设置失败')
  }
}

const addAnniversary = async () => {
  try {
    if (!newAnniversaryName.value.trim()) {
      showToast('请输入纪念日名称')
      return
    }
    
    if (!newAnniversaryDate.value) {
      showToast('请选择纪念日日期')
      return
    }
    
    const newAnniversary = {
      name: newAnniversaryName.value.trim(),
      date: newAnniversaryDate.value
    }
    
    anniversaryDates.value.push(newAnniversary)
    
    // 保存到后端
    await setAnniversaryDates(JSON.stringify(anniversaryDates.value))
    
    // 清空表单
    newAnniversaryName.value = ''
    newAnniversaryDate.value = ''
    showAnniversaryDialog.value = false
    
    showToast('纪念日添加成功')
  } catch (error) {
    showToast(error.message || '添加失败')
  }
}

// 重置添加纪念日表单
const resetAddAnniversaryForm = () => {
  newAnniversaryName.value = ''
  newAnniversaryDate.value = ''
  selectedNewAnniversaryDate.value = [
    new Date().getFullYear().toString(),
    (new Date().getMonth() + 1).toString().padStart(2, '0'),
    new Date().getDate().toString().padStart(2, '0')
  ]
}

// 重置编辑纪念日表单
const resetEditAnniversaryForm = () => {
  editingAnniversaryIndex.value = -1
  editingAnniversaryName.value = ''
  editingAnniversaryDate.value = ''
  selectedEditAnniversaryDate.value = [
    new Date().getFullYear().toString(),
    (new Date().getMonth() + 1).toString().padStart(2, '0'),
    new Date().getDate().toString().padStart(2, '0')
  ]
}

const removeAnniversary = async (index) => {
  try {
    await showConfirmDialog({
      title: '确认删除',
      message: `确定要删除纪念日"${anniversaryDates.value[index].name}"吗？`
    })
    
    anniversaryDates.value.splice(index, 1)
    
    // 保存到后端
    await setAnniversaryDates(JSON.stringify(anniversaryDates.value))
    
    showToast('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      showToast(error.message || '删除失败')
    }
  }
}

// 编辑纪念日
const editAnniversary = (index) => {
  const anniversary = anniversaryDates.value[index]
  editingAnniversaryIndex.value = index
  editingAnniversaryName.value = anniversary.name
  editingAnniversaryDate.value = anniversary.date
  
  // 将日期字符串转换为数组格式
  const parts = anniversary.date.split('-')
  if (parts.length === 3) {
    selectedEditAnniversaryDate.value = [
      parts[0],
      parts[1].padStart(2, '0'),
      parts[2].padStart(2, '0')
    ]
  }
  
  showEditAnniversaryDialog.value = true
}

// 编辑纪念日日期确认
const onEditAnniversaryDateConfirm = (value) => {
  try {
    // 处理日期选择器返回的数组格式 ['2021', '02', '01']
    let selectedDate
    if (Array.isArray(value)) {
      const [year, month, day] = value
      selectedDate = new Date(parseInt(year), parseInt(month) - 1, parseInt(day))
    } else if (value && value.selectedValues && Array.isArray(value.selectedValues)) {
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
    editingAnniversaryDate.value = dateStr
    showEditAnniversaryDatePicker.value = false
  } catch (error) {
    console.error('日期处理错误:', error)
    showToast(error.message || '日期设置失败')
  }
}

// 更新纪念日
const updateAnniversary = async () => {
  try {
    if (!editingAnniversaryName.value.trim()) {
      showToast('请输入纪念日名称')
      return
    }
    
    if (!editingAnniversaryDate.value) {
      showToast('请选择纪念日日期')
      return
    }
    
    const index = editingAnniversaryIndex.value
    if (index === -1) {
      showToast('编辑索引无效')
      return
    }
    
    // 更新纪念日数据
    anniversaryDates.value[index] = {
      name: editingAnniversaryName.value.trim(),
      date: editingAnniversaryDate.value
    }
    
    // 保存到后端
    await setAnniversaryDates(JSON.stringify(anniversaryDates.value))
    
    // 清空编辑状态
    editingAnniversaryIndex.value = -1
    editingAnniversaryName.value = ''
    editingAnniversaryDate.value = ''
    showEditAnniversaryDialog.value = false
    
    showToast('纪念日更新成功')
  } catch (error) {
    showToast(error.message || '更新失败')
  }
}

// 下次见面日相关方法
const onNextMeetingDateConfirm = async (value) => {
  try {
    // 处理日期选择器返回的数组格式 ['2021', '02', '01']
    let selectedDate
    if (Array.isArray(value)) {
      const [year, month, day] = value
      selectedDate = new Date(parseInt(year), parseInt(month) - 1, parseInt(day))
    } else if (value && value.selectedValues && Array.isArray(value.selectedValues)) {
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
    await setNextMeetingDate(dateStr)
    nextMeetingDate.value = dateStr
    showNextMeetingDatePicker.value = false
    showToast('设置成功')
  } catch (error) {
    console.error('日期处理错误:', error)
    showToast(error.message || '设置失败')
  }
}

// 调试用户信息
const debugUserInfo = () => {
  console.log('=== 调试用户信息 ===')
  console.log('Store状态:', {
    isLoggedIn: userStore.isLoggedIn,
    userId: userStore.userId,
    userInfo: userStore.userInfo,
    token: userStore.token
  })
  
  console.log('localStorage中的用户相关数据:')
  const userKeys = ['auth_token', 'auth_user_id', 'auth_display_name', 'auth_partner_id', 'auth_email']
  userKeys.forEach(key => {
    const value = localStorage.getItem(key)
    if (value) {
      console.log(`${key}:`, value)
    }
  })
  
  showToast(`用户ID: ${userStore.userId || '未获取到'}`)
}

// 生命周期
onMounted(() => {
  loadConfigs()
})

onUnmounted(() => {
  // 清理定时器
  stopMusicProgressTimer()
  // 清理音频事件监听器
  cleanupAudioListeners()
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

.music-upload-section {
  display: flex;
  align-items: center;
}

.music-preview-section {
  margin-top: 8px;
}

.music-name {
  font-size: 14px;
  color: #323233;
  margin-right: 8px;
}

.music-status {
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 4px;
  
  &.done {
    background-color: #f0f9ff;
    color: #1989fa;
  }
  
  &.uploading {
    background-color: #fff7e6;
    color: #ff9500;
  }
  
  &.failed {
    background-color: #fff2f0;
    color: #ee0a24;
  }
}

.music-player-container {
  margin: 8px 0;
  padding: 8px;
  background-color: #f7f8fa;
  border-radius: 8px;
}

.music-controls {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 8px;
}

.play-btn {
  width: 40px;
  height: 40px;
  border: none;
  border-radius: 50%;
  background-color: #1989fa;
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  transition: all 0.3s ease;
  
  &:hover {
    background-color: #1677ff;
    transform: scale(1.05);
  }
  
  &.playing {
    background-color: #ff6b9d;
    
    &:hover {
      background-color: #ff4d7d;
    }
  }
}

.music-progress {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.progress-bar {
  width: 100%;
  height: 6px;
  background-color: #e4e7ed;
  border-radius: 3px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  
  &:hover {
    .progress-fill {
      background-color: #ff6b9d;
    }
  }
}

.progress-fill {
  height: 100%;
  background-color: #1989fa;
  border-radius: 3px;
  transition: width 0.1s ease;
}

.time-display {
  font-size: 12px;
  color: #969799;
  text-align: center;
}

.uploading-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px;
  color: #1989fa;
}

.error-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px;
  color: #ee0a24;
}

.anniversary-list {
  margin-top: 8px;
}

.anniversary-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  margin: 4px 0;
  background-color: #f7f8fa;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
}

.anniversary-date {
  font-size: 14px;
  color: #323233;
  font-weight: 500;
}

.anniversary-name {
  font-size: 14px;
  color: #646566;
  margin: 0 8px;
  flex: 1;
}

.anniversary-actions {
  display: flex;
  align-items: center;
  gap: 4px;
}

.anniversary-form {
  padding: 16px;
}

.anniversary-form .van-field {
  margin-bottom: 12px;
}
</style>
