<template>
  <div class="anniversary-list-page romantic-bg page-container">
    <!-- 爱心装饰 -->
    <div class="heart-decoration heart-1">💕</div>
    <div class="heart-decoration heart-2">💖</div>
    <div class="heart-decoration heart-3">💝</div>
    
    <div class="anniversary-container glass-effect">
      <!-- 页面头部 -->
      <div class="page-header float">
        <div class="back-button" @click="goBack">
          <span class="back-icon">←</span>
        </div>
        <div class="header-content">
          <h1 class="page-title text-gradient-romantic">我们的纪念日</h1>
          <p class="page-subtitle">每一个重要的日子都值得铭记</p>
        </div>
      </div>

      <!-- 纪念日列表 -->
      <div class="anniversary-list" v-if="anniversaryDates.length > 0">
        <div 
          v-for="(anniversary, index) in sortedAnniversaries" 
          :key="index"
          class="anniversary-item glass-effect shimmer"
          :class="{ 'is-today': isToday(anniversary.date), 'is-upcoming': isUpcoming(anniversary.date) }"
        >
          <div class="anniversary-date-section">
            <div class="date-display">
              <div class="month">{{ formatMonth(anniversary.date) }}</div>
              <div class="day">{{ formatDay(anniversary.date) }}</div>
            </div>
            <div class="date-info">
              <div class="full-date">{{ formatFullDate(anniversary.date) }}</div>
              <div class="days-away" v-if="!isToday(anniversary.date)">
                {{ getDaysAway(anniversary.date) }}
              </div>
            </div>
          </div>
          
          <div class="anniversary-content">
            <h3 class="anniversary-name">{{ anniversary.name }}</h3>
            <p class="anniversary-description" v-if="anniversary.description">
              {{ anniversary.description }}
            </p>
            <div class="anniversary-emoji" v-if="anniversary.emoji">
              {{ anniversary.emoji }}
            </div>
          </div>
          
          <div class="anniversary-status">
            <div v-if="isToday(anniversary.date)" class="today-badge">
              🎉 今天
            </div>
            <div v-else-if="isUpcoming(anniversary.date)" class="upcoming-badge">
              ⏰ 即将到来
            </div>
            <div v-else class="past-badge">
              💕 美好回忆
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-state glass-effect shimmer">
        <div class="empty-icon">💕</div>
        <h3>还没有纪念日</h3>
        <p>去后台管理添加一些重要的纪念日吧</p>
        <van-button 
          type="primary" 
          size="large" 
          @click="goToAdmin"
          class="add-anniversary-btn"
        >
          <span class="btn-icon">➕</span>
          添加纪念日
        </van-button>
      </div>

      <!-- 统计信息 -->
      <div v-if="anniversaryDates.length > 0" class="stats-section glass-effect shimmer">
        <div class="stats-grid">
          <div class="stat-item">
            <div class="stat-number">{{ totalAnniversaries }}</div>
            <div class="stat-label">总纪念日</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ upcomingCount }}</div>
            <div class="stat-label">即将到来</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ pastCount }}</div>
            <div class="stat-label">美好回忆</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getAnniversaryDates } from '@/api/systemConfig'
import dayjs from 'dayjs'

const router = useRouter()

// 响应式数据
const anniversaryDates = ref([])
const isLoading = ref(false)

// 计算属性
const sortedAnniversaries = computed(() => {
  if (!anniversaryDates.value.length) return []
  
  return [...anniversaryDates.value].sort((a, b) => {
    const dateA = dayjs(a.date)
    const dateB = dayjs(b.date)
    
    // 按月份和日期排序
    const monthA = dateA.month()
    const monthB = dateB.month()
    const dayA = dateA.date()
    const dayB = dateB.date()
    
    if (monthA !== monthB) {
      return monthA - monthB
    }
    return dayA - dayB
  })
})

const totalAnniversaries = computed(() => anniversaryDates.value.length)

const upcomingCount = computed(() => {
  return anniversaryDates.value.filter(anniversary => isUpcoming(anniversary.date)).length
})

const pastCount = computed(() => {
  return anniversaryDates.value.filter(anniversary => !isUpcoming(anniversary.date) && !isToday(anniversary.date)).length
})

// 方法
const goBack = () => {
  router.go(-1)
}

const goToAdmin = () => {
  router.push('/admin')
}

const loadAnniversaryDates = async () => {
  isLoading.value = true
  try {
    const anniversaryDatesValue = await getAnniversaryDates()
    try {
      anniversaryDates.value = JSON.parse(anniversaryDatesValue)
    } catch (e) {
      anniversaryDates.value = []
    }
  } catch (error) {
    console.error('加载纪念日失败:', error)
    anniversaryDates.value = []
  } finally {
    isLoading.value = false
  }
}

const formatMonth = (dateStr) => {
  const date = dayjs(dateStr)
  return date.format('MMM')
}

const formatDay = (dateStr) => {
  const date = dayjs(dateStr)
  return date.format('DD')
}

const formatFullDate = (dateStr) => {
  const date = dayjs(dateStr)
  return date.format('YYYY年MM月DD日')
}

const isToday = (dateStr) => {
  const today = dayjs()
  const anniversaryDate = dayjs(dateStr)
  return today.month() === anniversaryDate.month() && today.date() === anniversaryDate.date()
}

const isUpcoming = (dateStr) => {
  const today = dayjs()
  const anniversaryDate = dayjs(dateStr)
  const thisYearAnniversary = anniversaryDate.year(today.year())
  
  // 如果今年的纪念日已经过了，看明年的
  if (thisYearAnniversary.isBefore(today)) {
    const nextYearAnniversary = anniversaryDate.year(today.year() + 1)
    return nextYearAnniversary.diff(today, 'day') <= 30 // 30天内
  }
  
  return thisYearAnniversary.diff(today, 'day') <= 30 // 30天内
}

const getDaysAway = (dateStr) => {
  const today = dayjs()
  const anniversaryDate = dayjs(dateStr)
  const thisYearAnniversary = anniversaryDate.year(today.year())
  
  let targetDate
  if (thisYearAnniversary.isBefore(today)) {
    targetDate = anniversaryDate.year(today.year() + 1)
  } else {
    targetDate = thisYearAnniversary
  }
  
  const diffDays = targetDate.diff(today, 'day')
  
  if (diffDays === 0) {
    return '就是今天！'
  } else if (diffDays === 1) {
    return '明天'
  } else if (diffDays > 1) {
    return `还有 ${diffDays} 天`
  } else {
    return `${Math.abs(diffDays)} 天前`
  }
}

// 生命周期
onMounted(() => {
  loadAnniversaryDates()
})
</script>

<style lang="scss" scoped>
.anniversary-list-page {
  min-height: 100vh;
  padding: 20px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  position: relative;
}

.anniversary-container {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 25px;
  width: 100%;
  max-width: 600px;
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.12);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  position: relative;
  z-index: 10;
}

.page-header {
  display: flex;
  align-items: center;
  margin-bottom: 25px;
  position: relative;
}

.back-button {
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 40px;
  height: 40px;
  background: rgba(255, 107, 157, 0.1);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    background: rgba(255, 107, 157, 0.2);
    transform: translateY(-50%) scale(1.1);
  }
  
  .back-icon {
    font-size: 18px;
    color: #ff6b9d;
    font-weight: bold;
  }
}

.header-content {
  flex: 1;
  text-align: center;
}

.page-title {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 8px;
  background: linear-gradient(135deg, #ff6b9d 0%, #f093fb 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-subtitle {
  color: #666;
  font-size: 16px;
  margin: 0;
  opacity: 0.8;
}

.anniversary-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 25px;
}

.anniversary-item {
  padding: 20px;
  border-radius: 16px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  
  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 25px rgba(255, 107, 157, 0.15);
  }
  
  &.is-today {
    background: linear-gradient(135deg, rgba(255, 107, 157, 0.1) 0%, rgba(240, 147, 251, 0.1) 100%);
    border: 2px solid rgba(255, 107, 157, 0.3);
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 3px;
      background: linear-gradient(90deg, #ff6b9d 0%, #f093fb 100%);
    }
  }
  
  &.is-upcoming {
    background: linear-gradient(135deg, rgba(79, 172, 254, 0.1) 0%, rgba(0, 242, 254, 0.1) 100%);
    border: 2px solid rgba(79, 172, 254, 0.3);
  }
}

.anniversary-date-section {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.date-display {
  text-align: center;
  min-width: 60px;
}

.month {
  font-size: 14px;
  color: #666;
  text-transform: uppercase;
  font-weight: 600;
  margin-bottom: 2px;
}

.day {
  font-size: 24px;
  font-weight: bold;
  color: #ff6b9d;
  line-height: 1;
}

.date-info {
  flex: 1;
}

.full-date {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.days-away {
  font-size: 14px;
  color: #ff6b9d;
  font-weight: 500;
}

.anniversary-content {
  margin-bottom: 15px;
}

.anniversary-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
}

.anniversary-description {
  font-size: 14px;
  color: #666;
  margin: 0 0 8px 0;
  line-height: 1.5;
}

.anniversary-emoji {
  font-size: 20px;
  text-align: center;
}

.anniversary-status {
  display: flex;
  justify-content: flex-end;
}

.today-badge,
.upcoming-badge,
.past-badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  text-align: center;
}

.today-badge {
  background: linear-gradient(135deg, #ff6b9d 0%, #f093fb 100%);
  color: white;
}

.upcoming-badge {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}

.past-badge {
  background: rgba(255, 107, 157, 0.1);
  color: #ff6b9d;
}

.empty-state {
  text-align: center;
  padding: 40px 20px;
  border-radius: 16px;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 20px;
}

.empty-state h3 {
  color: #333;
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 10px 0;
}

.empty-state p {
  color: #666;
  font-size: 16px;
  margin: 0 0 25px 0;
}

.add-anniversary-btn {
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  padding: 0 25px;
  border-radius: 24px;
  background: linear-gradient(135deg, #ff6b9d 0%, #f093fb 100%);
  border: none;
  
  .btn-icon {
    margin-right: 8px;
  }
}

.stats-section {
  padding: 20px;
  border-radius: 16px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #ff6b9d;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

// 爱心装饰动画
.heart-decoration {
  position: absolute;
  font-size: 24px;
  animation: float 6s ease-in-out infinite;
  opacity: 0.6;
  z-index: 1;
}

.heart-1 {
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.heart-2 {
  top: 20%;
  right: 15%;
  animation-delay: 2s;
}

.heart-3 {
  bottom: 15%;
  left: 20%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(10deg);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .anniversary-container {
    padding: 20px;
    margin: 10px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .page-subtitle {
    font-size: 14px;
  }
  
  .anniversary-item {
    padding: 16px;
  }
  
  .stats-grid {
    gap: 15px;
  }
  
  .stat-number {
    font-size: 24px;
  }
}
</style>
