<template>
    <div class="calendar romantic-bg page-container">
    <!-- 返回按钮 -->
    <BackButton />
    
    <!-- 爱心装饰 -->
    <div class="heart-decoration heart-1">💕</div>
    <div class="heart-decoration heart-2">💖</div>
    <div class="heart-decoration heart-3">💝</div>
    

    

    <div class="content">
      <div class="header float">
        <p class="subtitle pulse">记录我们的每一个美好瞬间</p>
        
        <!-- 视图切换按钮 -->
        <div class="view-toggle">
          <van-button 
            :type="viewMode === 'list' ? 'primary' : 'default'"
            size="small"
            @click="switchView('list')"
            class="toggle-btn"
          >
            <span class="btn-icon">📋</span>
            列表
          </van-button>
          <van-button 
            :type="viewMode === 'calendar' ? 'primary' : 'default'"
            size="small"
            @click="switchView('calendar')"
            class="toggle-btn"
          >
            <span class="btn-icon">📅</span>
            日历
          </van-button>
        </div>
      </div>

      <!-- 列表视图 -->
      <div v-if="viewMode === 'list'" class="diary-list">
        <div 
          v-for="diary in diaries" 
          :key="diary.id"
          class="diary-item hover-lift glow"
          @click="viewDetail(diary)"
        >
          <div class="diary-content">
            <div class="diary-info">
              <div class="diary-date">
                <span class="date-number">{{ formatDate(diary.date) }}</span>
                <span class="date-icon">💕</span>
              </div>
              <div class="diary-title">{{ diary.title }}</div>
              <div class="diary-desc">{{ diary.description }}</div>
              
              <!-- 媒体标记 -->
              <div class="media-badges">
                <div v-if="diary.images && diary.images.length > 0" class="media-badge image-badge" title="包含图片">
                  <van-icon name="photo-o" />
                  <span>{{ diary.images.length }} 张图片</span>
                </div>
                <div v-if="diary.videos && diary.videos.length > 0" class="media-badge video-badge" title="包含视频">
                  <van-icon name="video-o" />
                  <span>{{ diary.videos.length }} 个视频</span>
                </div>
                <div v-if="diary.backgroundMusic && diary.backgroundMusic.length > 0" class="media-badge music-badge" title="包含音乐">
                  <van-icon name="music-o" />
                  <span>音乐</span>
                </div>
              </div>
            </div>
            
            <div class="diary-media" v-if="diary.images && diary.images.length > 0">
              <img 
                :src="diary.images[0].imageUrl" 
                :alt="diary.title" 
                class="diary-image" 
                @click="previewImage(diary.images)"
              />
            </div>
          </div>
          
          <div class="diary-arrow">
            <van-icon name="arrow" class="arrow-icon" />
          </div>
        </div>
      </div>

      <!-- 日历视图 -->
      <div v-else class="calendar-view">
        <div class="calendar-header">
          <van-button 
            icon="arrow-left" 
            size="small" 
            @click="previousMonth"
            class="month-nav-btn"
          />
          <h3 class="current-month">{{ displayMonth }}</h3>
          <van-button 
            icon="arrow" 
            size="small" 
            @click="nextMonth"
            class="month-nav-btn"
          />
        </div>
        
        <div class="calendar-grid">
          <!-- 星期标题 -->
          <div class="weekdays">
            <div v-for="day in weekdays" :key="day" class="weekday">{{ day }}</div>
          </div>
          
          <!-- 日期网格 -->
          <div class="days-grid">
            <div 
              v-for="day in calendarDays" 
              :key="day.key"
              class="day-cell"
              :class="{
                'other-month': !day.isCurrentMonth,
                'has-diary': day.hasDiary,
                'today': day.isToday,
                'clickable': day.isCurrentMonth
              }"
              @click="day.isCurrentMonth ? selectDate(day.date) : null"
            >
              <span class="day-number">{{ day.dayNumber }}</span>
              
              <!-- 媒体标记 -->
              <div v-if="day.hasDiary" class="media-indicators">
                <div v-if="day.hasImage" class="media-icon image-icon" title="包含图片">📷</div>
                <div v-if="day.hasVideo" class="media-icon video-icon" title="包含视频">🎥</div>
                <div v-if="day.hasMusic" class="media-icon music-icon" title="包含音乐">🎵</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="diaries.length === 0" class="empty-state">
        <div class="empty-icon heartbeat">💕</div>
        <p class="empty-text">还没有回忆记录</p>
        <van-button 
          type="primary" 
          size="large" 
          @click="$router.push('/admin/diary/create')"
          class="create-btn btn-primary"
        >
          <span class="btn-icon">✨</span>
          创建第一个回忆
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showImagePreview, showToast } from 'vant'
import dayjs from 'dayjs'
import { getAllDiaries } from '@/api/diary'
import BackButton from '@/components/BackButton.vue'

const router = useRouter()
const route = useRoute()
const viewMode = ref('list') // 'list' 或 'calendar'
const selectedDate = ref(dayjs())

const goBack = () => {
  router.go(-1)
}

const diaries = ref([])

// 加载日记数据
const loadDiaries = async () => {
  try {
    const allDiaries = await getAllDiaries()
    diaries.value = allDiaries
  } catch (error) {
    console.error('加载日记失败:', error)
    showToast('加载日记失败，请稍后重试')
  }
}

// 路由状态同步：将视图模式与月份写入路由查询参数
const syncRouteQuery = () => {
  const month = selectedDate.value.format('YYYY-MM')
  const view = viewMode.value
  router.replace({ query: { view, month } })
}

// 初始化：从路由查询参数恢复视图模式与月份
const initFromRoute = () => {
  const { view, month } = route.query
  if (view === 'calendar' || view === 'list') {
    viewMode.value = view
  }
  if (typeof month === 'string' && month.match(/^\d{4}-\d{2}$/)) {
    const parsed = dayjs(`${month}-01`)
    if (parsed.isValid()) {
      selectedDate.value = parsed
    }
  }
  // 确保当前路由包含最新状态
  syncRouteQuery()
}

// 星期标题
const weekdays = ['日', '一', '二', '三', '四', '五', '六']

// 计算属性：显示月份
const displayMonth = computed(() => {
  return selectedDate.value.format('YYYY年MM月')
})

// 计算属性：日历天数
const calendarDays = computed(() => {
  const year = selectedDate.value.year()
  const month = selectedDate.value.month()
  const firstDay = dayjs().year(year).month(month).date(1)
  const lastDay = dayjs().year(year).month(month).endOf('month')
  
  // 获取上个月的最后几天
  const firstDayOfWeek = firstDay.day()
  const prevMonthDays = []
  for (let i = firstDayOfWeek - 1; i >= 0; i--) {
    const date = firstDay.subtract(i + 1, 'day')
    prevMonthDays.push({
      key: `prev-${date.format('YYYY-MM-DD')}`,
      date: date,
      dayNumber: date.date(),
      isCurrentMonth: false,
      hasDiary: hasDiaryOnDate(date),
      hasImage: hasImageOnDate(date),
      hasVideo: hasVideoOnDate(date),
      hasMusic: hasBackgroundMusicOnDate(date),
      isToday: date.isSame(dayjs(), 'day')
    })
  }
  
  // 获取当前月的天数
  const currentMonthDays = []
  for (let i = 1; i <= lastDay.date(); i++) {
    const date = dayjs().year(year).month(month).date(i)
    currentMonthDays.push({
      key: `current-${date.format('YYYY-MM-DD')}`,
      date: date,
      dayNumber: i,
      isCurrentMonth: true,
      hasDiary: hasDiaryOnDate(date),
      hasImage: hasImageOnDate(date),
      hasVideo: hasVideoOnDate(date),
      hasMusic: hasBackgroundMusicOnDate(date),
      isToday: date.isSame(dayjs(), 'day')
    })
  }
  
  // 获取下个月的前几天
  const lastDayOfWeek = lastDay.day()
  const nextMonthDays = []
  for (let i = 1; i <= 6 - lastDayOfWeek; i++) {
    const date = lastDay.add(i, 'day')
    nextMonthDays.push({
      key: `next-${date.format('YYYY-MM-DD')}`,
      date: date,
      dayNumber: date.date(),
      isCurrentMonth: false,
      hasDiary: hasDiaryOnDate(date),
      hasImage: hasImageOnDate(date),
      hasVideo: hasVideoOnDate(date),
      hasMusic: hasBackgroundMusicOnDate(date),
      isToday: date.isSame(dayjs(), 'day')
    })
  }
  
  return [...prevMonthDays, ...currentMonthDays, ...nextMonthDays]
})

// 检查指定日期是否有日记
const hasDiaryOnDate = (date) => {
  const dateStr = date.format('YYYY-MM-DD')
  return diaries.value.some(diary => diary.date === dateStr)
}

// 获取指定日期的所有日记
const getDiariesOnDate = (date) => {
  const dateStr = date.format('YYYY-MM-DD')
  return diaries.value.filter(diary => diary.date === dateStr)
}

// 获取指定日期的日记（兼容旧代码）
const getDiaryOnDate = (date) => {
  const dateStr = date.format('YYYY-MM-DD')
  return diaries.value.find(diary => diary.date === dateStr)
}

// 检查指定日期的所有日记是否有图片
const hasImageOnDate = (date) => {
  const diariesOnDate = getDiariesOnDate(date)
  return diariesOnDate.some(diary => diary.images && diary.images.length > 0)
}

// 检查指定日期的所有日记是否有视频
const hasVideoOnDate = (date) => {
  const diariesOnDate = getDiariesOnDate(date)
  return diariesOnDate.some(diary => diary.videos && diary.videos.length > 0)
}

// 检查指定日期的所有日记是否有背景音乐
const hasBackgroundMusicOnDate = (date) => {
  const diariesOnDate = getDiariesOnDate(date)
  return diariesOnDate.some(diary => diary.backgroundMusic && diary.backgroundMusic.length > 0)
}

// 检查单个日记是否有图片（兼容旧代码）
const hasImage = (diary) => {
  return diary.images && diary.images.length > 0
}

// 检查单个日记是否有视频（兼容旧代码）
const hasVideo = (diary) => {
  return diary.videos && diary.videos.length > 0
}

// 检查单个日记是否有背景音乐（兼容旧代码）
const hasBackgroundMusic = (diary) => {
  return diary.backgroundMusic && diary.backgroundMusic.length > 0
}

// 切换视图模式
const switchView = (mode) => {
  viewMode.value = mode
}

// 上个月
const previousMonth = () => {
  selectedDate.value = selectedDate.value.subtract(1, 'month')
}

// 下个月
const nextMonth = () => {
  selectedDate.value = selectedDate.value.add(1, 'month')
}

// 选择日期
const selectDate = (date) => {
  const dateStr = date.format('YYYY-MM-DD')
  const diary = diaries.value.find(d => d.date === dateStr)
  
  if (diary) {
    // 如果有日记，跳转到日记列表页
    router.push(`/diaries/date/${dateStr}`)
  } else {
    // 如果没有日记，显示提示信息
    showToast('这一天还没有回忆记录')
  }
}

const formatDate = (date) => {
  return dayjs(date).format('MM月DD日')
}

const viewDetail = (diary) => {
  // 列表视图点击 - 跳转到详情页
  router.push(`/diary/${diary.id}`)
}

// 图片预览功能
const previewImage = (images) => {
  if (images && images.length > 0) {
    const imageUrls = images.map(image => image.imageUrl)
    showImagePreview({
      images: imageUrls,
      startPosition: 0,
      closeable: true,
      closeIconPosition: 'top-right',
      showIndex: true,
      swipeDuration: 300,
      showIndicators: true,
      indicatorColor: '#ff6b9d'
    })
  }
}
// 监听变化，实时写入路由，保证返回后还能恢复
watch(viewMode, () => syncRouteQuery())
watch(selectedDate, () => syncRouteQuery())

onMounted(() => {
  initFromRoute()
  loadDiaries()
})

</script>

<style scoped>
.calendar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  position: relative;
}





.content {
  padding: 20px;
  position: relative;
  z-index: 2;
  padding-bottom: 40px;
}

  .header {
    text-align: center;
    margin-bottom: 30px;
    
    .subtitle {
      font-size: 16px;
      color: rgba(255, 255, 255, 0.8);
      margin-bottom: 20px;
    }
    
    .view-toggle {
      display: flex;
      justify-content: center;
      gap: 10px;
      
      .toggle-btn {
        background: rgba(255, 255, 255, 0.2);
        border: 2px solid rgba(255, 255, 255, 0.3);
        color: white;
        backdrop-filter: blur(10px);
        border-radius: 20px;
        padding: 8px 16px;
        font-size: 14px;
        
        &:hover {
          background: rgba(255, 255, 255, 0.3);
          transform: translateY(-2px);
        }
        
        &.van-button--primary {
          background: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 100%);
          border-color: #ff6b9d;
        }
        
        .btn-icon {
          margin-right: 4px;
        }
      }
    }
  }

.diary-list {
  .diary-item {
    background: rgba(255, 255, 255, 0.15);
    border-radius: 20px;
    padding: 20px;
    margin-bottom: 15px;
    cursor: pointer;
    transition: all 0.3s ease;
    backdrop-filter: blur(15px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    position: relative;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
      transition: left 0.5s;
    }
    
    &:hover {
      background: rgba(255, 255, 255, 0.2);
      transform: translateY(-5px) scale(1.02);
      
      &::before {
        left: 100%;
      }
      
      .arrow-icon {
        transform: translateX(5px);
      }
    }
    
    .diary-content {
      display: flex;
      gap: 15px;
      align-items: center;
      
      .diary-info {
        flex: 1;
        
        .diary-date {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 8px;
          
          .date-number {
            color: #ff6b9d;
            font-size: 18px;
            font-weight: bold;
          }
          
          .date-icon {
            font-size: 16px;
            animation: heartbeat 2s ease-in-out infinite;
          }
        }
        
        .diary-title {
          color: white;
          font-size: 18px;
          font-weight: bold;
          margin-bottom: 8px;
        }
        
        .diary-desc {
          color: rgba(255, 255, 255, 0.8);
          font-size: 14px;
          line-height: 1.5;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
          white-space: pre-wrap;
          word-wrap: break-word;
        }
        
        .media-badges {
          display: flex;
          gap: 6px;
          margin-top: 10px;
          
          .media-badge {
            display: flex;
            align-items: center;
            gap: 3px;
            padding: 3px 6px;
            border-radius: 8px;
            font-size: 11px;
            font-weight: 500;
            backdrop-filter: blur(10px);
            border: 1px solid rgba(255, 255, 255, 0.2);
            transition: all 0.3s ease;
            
            .van-icon {
              font-size: 12px;
            }
            
            span {
              color: white;
              font-weight: 600;
            }
            
            &.image-badge {
              background: rgba(52, 152, 219, 0.3);
              border-color: rgba(52, 152, 219, 0.5);
              
              &:hover {
                background: rgba(52, 152, 219, 0.4);
                transform: translateY(-1px);
                box-shadow: 0 2px 8px rgba(52, 152, 219, 0.3);
              }
            }
            
            &.video-badge {
              background: rgba(255, 71, 87, 0.3);
              border-color: rgba(255, 71, 87, 0.5);
              
              &:hover {
                background: rgba(255, 71, 87, 0.4);
                transform: translateY(-1px);
                box-shadow: 0 2px 8px rgba(255, 71, 87, 0.3);
              }
            }
            
            &.music-badge {
              background: rgba(46, 213, 115, 0.3);
              border-color: rgba(46, 213, 115, 0.5);
              
              &:hover {
                background: rgba(46, 213, 115, 0.4);
                transform: translateY(-1px);
                box-shadow: 0 2px 8px rgba(46, 213, 115, 0.3);
              }
            }
          }
        }
      }
      
      .diary-media {
                 .diary-image {
           width: 80px;
           height: 80px;
           object-fit: cover;
           border-radius: 12px;
           box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
           cursor: pointer;
           transition: transform 0.3s ease;
           
           &:hover {
             transform: scale(1.05);
           }
         }
      }
    }
    
    .diary-arrow {
      position: absolute;
      right: 20px;
      top: 50%;
      transform: translateY(-50%);
      
      .arrow-icon {
        color: rgba(255, 255, 255, 0.6);
        font-size: 20px;
        transition: transform 0.3s ease;
      }
    }
  }
}

.calendar-view {
  background: rgba(255, 255, 255, 0.15);
  border-radius: 20px;
  padding: 20px;
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  margin-bottom: 20px;
  
  .calendar-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;
    
    .current-month {
      color: white;
      font-size: 20px;
      font-weight: bold;
      margin: 0;
    }
    
    .month-nav-btn {
      background: rgba(255, 255, 255, 0.2);
      border: 1px solid rgba(255, 255, 255, 0.3);
      color: white;
      border-radius: 50%;
      width: 36px;
      height: 36px;
      display: flex;
      align-items: center;
      justify-content: center;
      
      &:hover {
        background: rgba(255, 255, 255, 0.3);
        transform: scale(1.1);
      }
    }
  }
  
  .calendar-grid {
    .weekdays {
      display: grid;
      grid-template-columns: repeat(7, 1fr);
      gap: 8px;
      margin-bottom: 10px;
      
      .weekday {
        text-align: center;
        color: rgba(255, 255, 255, 0.8);
        font-size: 14px;
        font-weight: 600;
        padding: 8px 0;
      }
    }
    
    .days-grid {
      display: grid;
      grid-template-columns: repeat(7, 1fr);
      gap: 8px;
      
      .day-cell {
        aspect-ratio: 1;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        border-radius: 12px;
        position: relative;
        transition: all 0.3s ease;
        cursor: pointer;
        
        .day-number {
          color: white;
          font-size: 16px;
          font-weight: 600;
          z-index: 2;
        }
        

        
        .media-indicators {
          position: absolute;
          bottom: 2px;
          left: 50%;
          transform: translateX(-50%);
          display: flex;
          flex-direction: row;
          gap: 2px;
          
          .media-icon {
            font-size: 8px;
            animation: pulse 2s ease-in-out infinite;
            
            &.image-icon {
              color: #3498db;
            }
            
            &.video-icon {
              color: #ff4757;
            }
            
            &.music-icon {
              color: #2ed573;
            }
          }
        }
        
        &.other-month {
          opacity: 0.4;
          cursor: default;
          
          .day-number {
            color: rgba(255, 255, 255, 0.5);
          }
        }
        
        &.has-diary {
          background: linear-gradient(135deg, rgba(255, 107, 157, 0.3) 0%, rgba(255, 143, 171, 0.3) 100%);
          border: 2px solid rgba(255, 107, 157, 0.5);
          
          &:hover {
            background: linear-gradient(135deg, rgba(255, 107, 157, 0.4) 0%, rgba(255, 143, 171, 0.4) 100%);
            transform: scale(1.05);
            box-shadow: 0 4px 12px rgba(255, 107, 157, 0.3);
          }
        }
        
        &.today {
          background: linear-gradient(135deg, rgba(255, 193, 7, 0.3) 0%, rgba(255, 235, 59, 0.3) 100%);
          border: 2px solid rgba(255, 193, 7, 0.5);
          
          .day-number {
            color: #fff;
            font-weight: 800;
          }
        }
        
        &.clickable:not(.has-diary):not(.today) {
          &:hover {
            background: rgba(255, 255, 255, 0.1);
            transform: scale(1.05);
          }
        }
      }
    }
  }
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  
  .empty-icon {
    font-size: 80px;
    margin-bottom: 20px;
  }
  
  .empty-text {
    color: rgba(255, 255, 255, 0.8);
    font-size: 18px;
    margin-bottom: 30px;
  }
  
  .create-btn {
    height: 56px;
    border-radius: 28px;
    font-size: 18px;
    font-weight: 500;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    
    .btn-icon {
      font-size: 20px;
    }
  }
}

@media (max-width: 768px) {
  .back-button {
    top: 15px;
    left: 15px;
    
    .van-icon {
      font-size: 20px;
      padding: 8px;
    }
  }
  
  .content {
    padding: 15px;
  }
  

  
  .header .view-toggle .toggle-btn {
    font-size: 12px;
    padding: 6px 12px;
  }
  
  .diary-list .diary-item {
    padding: 15px;
    
    .diary-content {
      gap: 12px;
      
      .diary-info .diary-title {
        font-size: 16px;
      }
      
      .diary-media .diary-image {
        width: 60px;
        height: 60px;
      }
    }
  }
  
  .calendar-view {
    padding: 15px;
    
    .calendar-header .current-month {
      font-size: 18px;
    }
    
    .calendar-grid .days-grid .day-cell {
      .day-number {
        font-size: 14px;
      }
      
      .diary-indicator {
        font-size: 10px;
      }
    }
  }
  
  .empty-state .create-btn {
    height: 48px;
    font-size: 16px;
  }
}
</style> 