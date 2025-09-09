<template>
  <div class="welcome-page romantic-bg page-container">
    <!-- 爱心装饰 -->
    <div class="heart-decoration heart-1">💕</div>
    <div class="heart-decoration heart-2">💖</div>
    <div class="heart-decoration heart-3">💝</div>
    
    <div class="welcome-container glass-effect">
      
      <div class="welcome-header float">
        <div class="logo heartbeat">💕</div>
        <h1 class="title text-gradient-romantic">美好回忆</h1>
      </div>

      <!-- 伴侣状态和倒计时轮播区域 -->
      <div v-if="isLoggedIn" class="carousel-section">
        <van-swipe 
          :autoplay="3000" 
          :show-indicators="true" 
          :loop="true"
          class="love-carousel"
          indicator-color="rgba(255, 255, 255, 0.5)"
        >
          <!-- 回忆记录卡片 - 第一个位置 -->
          <van-swipe-item v-if="currentDiary">
            <div class="carousel-card memory-card glass-effect shimmer" @click="goToHome">
              <div class="memory-image-container">
                <img 
                  v-if="currentDiary.images && currentDiary.images.length > 0" 
                  :src="currentDiary.images[0].imageUrl" 
                  :alt="currentDiary.title"
                  class="memory-image"
                />
                <div v-else class="memory-placeholder">
                  <div class="card-icon">📸</div>
                </div>
              </div>
              <h3 class="card-title">{{ currentDiary.title }}</h3>
              <p class="card-subtitle">{{ formatDate(currentDiary.date) }}</p>
            </div>
          </van-swipe-item>

          <!-- 最近信件卡片 - 第二个位置 -->
          <van-swipe-item v-if="latestLetter">
            <div class="carousel-card letter-card glass-effect shimmer" @click="goToLetterDetail">
              <div class="letter-icon-container">
                <div class="card-icon">💌</div>
                <div v-if="!latestLetter.isRead" class="unread-badge">未读</div>
              </div>
              <h3 class="card-title">{{ latestLetter.title }}</h3>
              <p class="card-subtitle">来自: {{ latestLetter.senderName }}</p>
              <p class="card-date">{{ formatDateTime(latestLetter.createdAt) }}</p>
            </div>
          </van-swipe-item>

          <!-- 在一起时间倒计时卡片 -->
          <van-swipe-item v-if="loveCountdown">
            <div class="carousel-card countdown-card glass-effect shimmer">
              <div class="card-icon">💕</div>
              <h3 class="card-title">在一起</h3>
              <div class="countdown-display">{{ loveCountdown }}</div>
              <p class="card-subtitle">每一天都是珍贵的回忆</p>
            </div>
          </van-swipe-item>
          
          <!-- 纪念日倒计时卡片 -->
          <van-swipe-item v-if="anniversaryCountdown">
            <div class="carousel-card countdown-card glass-effect shimmer" @click="goToAnniversaryList">
              <div class="card-icon">🎉</div>
              <h3 class="card-title">最近纪念日</h3>
              <div class="countdown-display">{{ anniversaryCountdown }}</div>
              <p class="card-subtitle">{{ nextAnniversaryName }}</p>
            </div>
          </van-swipe-item>
          
          <!-- 下次见面倒计时卡片 -->
          <van-swipe-item v-if="nextMeetingCountdown">
            <div class="carousel-card countdown-card glass-effect shimmer">
              <div class="card-icon">💕</div>
              <h3 class="card-title">下次见面</h3>
              <div class="countdown-display">{{ nextMeetingCountdown }}</div>
            </div>
          </van-swipe-item>

          <!-- 伴侣状态卡片 - 只在没有伴侣关系时显示 -->
          <van-swipe-item v-if="!partnerInfo.hasPartner">
            <div class="carousel-card partner-card glass-effect" @click="handlePartnerCardClick">
              <!-- 没有伴侣时显示邀请图标 -->
              <div v-if="!partnerInfo.hasPendingInvitation && !partnerInfo.hasSentInvitation" 
                   class="partner-invite-content">
                <div class="card-icon">💝</div>
                <h3 class="card-title">邀请伴侣</h3>
                <p class="card-subtitle">与心爱的人一起记录美好时光</p>
              </div>

              <!-- 有邀请时显示邀请信息 -->
              <div v-else-if="partnerInfo.hasPendingInvitation" class="partner-invitation-content">
                <div class="card-icon">💌</div>
                <h3 class="card-title">收到邀请</h3>
                <p class="card-subtitle">{{ partnerInfo.pendingInvitation.fromDisplayName || partnerInfo.pendingInvitation.fromUsername }} 邀请您成为伴侣</p>
              </div>
              
              <!-- 已发送邀请时显示邀请信息 -->
              <div v-else-if="partnerInfo.hasSentInvitation" class="partner-sent-invitation-content">
                <div class="card-icon">📤</div>
                <h3 class="card-title">已发送邀请</h3>
                <p class="card-subtitle">等待 {{ partnerInfo.sentInvitation.toDisplayName || partnerInfo.sentInvitation.toUsername }} 回复</p>
              </div>
            </div>
          </van-swipe-item>
        </van-swipe>
      </div>

      <div class="welcome-content">
        <div class="feature-grid-compact">
          <div class="feature-item-compact" @click="goToCalendar">
            <span class="feature-icon-compact">📅</span>
            <div class="feature-text-compact">
              <h3>时光日历</h3>
              <p>记录每一个重要的日子</p>
            </div>
          </div>
          
          <div class="feature-item-compact" @click="goToLetterBox">
            <span class="feature-icon-compact">📮</span>
            <div class="feature-text-compact">
              <h3>我的信箱</h3>
              <p>查看收到的信件</p>
            </div>
          </div>
          
          <div class="feature-item-compact" @click="goToChatRecord">
            <span class="feature-icon-compact">💬</span>
            <div class="feature-text-compact">
              <h3>聊天记录</h3>
              <p>记录聊天时光</p>
            </div>
          </div>
          
          <div class="feature-item-compact" @click="goToMovies">
            <span class="feature-icon-compact">🎬</span>
            <div class="feature-text-compact">
              <h3>一起看电影</h3>
              <p>与伴侣同步观看电影</p>
            </div>
          </div>
          
          <!-- 管理功能 -->
          <div v-if="isLoggedIn" class="feature-item-compact admin-feature-compact" @click="goToEditProfile">
            <span class="feature-icon-compact">👤</span>
            <div class="feature-text-compact">
              <h3>个人信息</h3>
              <p>编辑个人资料</p>
            </div>
          </div>
          
          <div v-if="isLoggedIn" class="feature-item-compact admin-feature-compact" @click="goToAdmin">
            <span class="feature-icon-compact">🎛️</span>
            <div class="feature-text-compact">
              <h3>后台管理</h3>
              <p>系统配置管理</p>
            </div>
          </div>
        </div>
        
        <!-- 未登录时显示登录按钮 -->
        <div v-if="!isLoggedIn" class="login-section-compact">
          <van-button 
            type="primary" 
            size="large" 
            @click="goToLogin"
            class="btn-primary ripple"
          >
            <span class="btn-icon">💕</span>
            开始使用
          </van-button>
        </div>
        
        <!-- 退出登录按钮 -->
        <div v-if="isLoggedIn" class="logout-section-compact">
          <van-button 
            size="small" 
            type="default" 
            @click="handleLogout" 
            class="logout-btn-compact"
          >
            <span class="btn-icon">🚶</span>
            退出登录
          </van-button>
        </div>
      </div>
    </div>

    <!-- 邀请伴侣弹窗 -->
    <van-dialog v-model:show="showInviteDialog" title="邀请伴侣" :show-confirm-button="false">
      <div class="invite-dialog-content">
        <p class="invite-tip">请输入对方的用户名来发送邀请</p>
        <van-field
          v-model="inviteUsername"
          placeholder="请输入用户名"
          :rules="[{ required: true, message: '请输入用户名' }]"
        />
        <div class="invite-actions">
          <van-button @click="showInviteDialog = false" type="default">取消</van-button>
          <van-button @click="sendInvite" type="primary" :loading="inviteLoading">发送邀请</van-button>
        </div>
      </div>
    </van-dialog>

                                                                                                                       <!-- 伴侣信息弹窗 -->
         <van-dialog v-model:show="showPartnerDialog" title="伴侣信息" :show-confirm-button="false" :close-on-click-overlay="true">
                       <div class="partner-dialog-content">
              <div class="partner-detail">
                <div class="partner-avatar-large" @click="confirmUnbindPartner" :class="{ 'unbind-loading': unbindLoading }">💑</div>
                <h3>{{ partnerInfo.partnerDisplayName || partnerInfo.partnerUsername }}</h3>
              </div>
                                         <div class="partner-actions">
                 <van-button @click="goToHome" class="enter-space-btn">进入我们的空间</van-button>
               </div>
           </div>
         </van-dialog>

         <!-- 邀请处理弹窗 -->
     <van-dialog v-model:show="showInvitationDialog" title="伴侣邀请" :show-confirm-button="false">
       <div class="invitation-dialog-content">
         <div class="invitation-detail">
           <div class="invitation-avatar">💌</div>
           <h3>{{ partnerInfo.pendingInvitation?.fromDisplayName || partnerInfo.pendingInvitation?.fromUsername }}</h3>
           <p>邀请您成为伴侣</p>
           <p class="invitation-time">{{ partnerInfo.pendingInvitation?.createdAt }}</p>
         </div>
                  <div class="invitation-actions">
            <van-button @click="handleRejectInvitation" type="default" :loading="rejectLoading">拒绝</van-button>
            <van-button @click="handleAcceptInvitation" type="primary" :loading="acceptLoading">接受</van-button>
          </div>
       </div>
     </van-dialog>

     <!-- 已发送邀请弹窗 -->
     <van-dialog v-model:show="showSentInvitationDialog" title="已发送邀请" :show-confirm-button="false">
       <div class="invitation-dialog-content">
         <div class="invitation-detail">
           <div class="invitation-avatar">📤</div>
           <h3>{{ partnerInfo.sentInvitation?.toDisplayName || partnerInfo.sentInvitation?.toUsername }}</h3>
           <p>等待对方回复</p>
           <p class="invitation-time">{{ partnerInfo.sentInvitation?.createdAt }}</p>
         </div>
                  <div class="invitation-actions">
            <van-button @click="showSentInvitationDialog = false" type="default">关闭</van-button>
            <van-button @click="handleCancelInvitation" type="danger" :loading="cancelLoading">取消邀请</van-button>
          </div>
       </div>
     </van-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { checkLoginState, clearLoginState } from '@/utils/auth'
import { getPartnerInfo, invitePartner, acceptInvitation, rejectInvitation, unbindPartner, cancelInvitation } from '@/api/partner'
import { getAnniversaryDatesByUserId, getNextMeetingDateByUserId, getTogetherDateByUserId } from '@/api/systemConfig'
import { getLatestDiary } from '@/api/diary'
import { getUnlockedLetters, getReceivedLetters, getSentLetters } from '@/api/letter'
import { showToast, showDialog } from 'vant'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'

const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const partnerInfo = ref({
  hasPartner: false,
  hasPendingInvitation: false,
  partnerId: null,
  partnerUsername: '',
  partnerDisplayName: '',
  pendingInvitation: null
})

// 倒计时相关数据
const loveCountdown = ref('')
const anniversaryCountdown = ref('')
const nextMeetingCountdown = ref('')
const nextAnniversaryName = ref('')
const anniversaryDates = ref([])
const nextMeetingDate = ref('')
const togetherDate = ref('2025-05-30 14:30:00') // 在一起的时间，从后台配置读取

// 回忆记录数据
const currentDiary = ref(null)

// 信件数据
const latestLetter = ref(null)

const showInviteDialog = ref(false)
const showPartnerDialog = ref(false)
const showInvitationDialog = ref(false)
const showSentInvitationDialog = ref(false)
const inviteUsername = ref('')
const inviteLoading = ref(false)
const acceptLoading = ref(false)
const rejectLoading = ref(false)
const unbindLoading = ref(false)
const cancelLoading = ref(false)

// 计算属性
const isLoggedIn = computed(() => checkLoginState())

// 倒计时相关方法
const calculateLoveCountdown = () => {
  if (!togetherDate.value) {
    console.log('没有在一起时间，跳过计算')
    return
  }
  
  const now = dayjs()
  const startDate = dayjs(togetherDate.value)
  const diff = now.diff(startDate, 'second')
  
  const days = Math.floor(diff / (24 * 60 * 60))
  const hours = Math.floor((diff % (24 * 60 * 60)) / (60 * 60))
  const minutes = Math.floor((diff % (60 * 60)) / 60)
  const seconds = diff % 60
  
  if (days > 0) {
    loveCountdown.value = `${days}天${hours}时${minutes}分${seconds}秒`
  } else if (hours > 0) {
    loveCountdown.value = `${hours}时${minutes}分${seconds}秒`
  } else if (minutes > 0) {
    loveCountdown.value = `${minutes}分${seconds}秒`
  } else {
    loveCountdown.value = `${seconds}秒`
  }
  
  console.log('在一起倒计时计算完成:', loveCountdown.value)
}

const calculateAnniversaryCountdown = () => {
  if (!anniversaryDates.value || anniversaryDates.value.length === 0) {
    anniversaryCountdown.value = ''
    nextAnniversaryName.value = ''
    return
  }
  
  const now = dayjs()
  let nextAnniversary = null
  let minTime = Infinity
  
  // 找到最近的纪念日
  anniversaryDates.value.forEach(anniversary => {
    const anniversaryDate = dayjs(anniversary.date)
    
    // 计算到今年纪念日的时间
    let targetDate = anniversaryDate.year(now.year())
    if (targetDate.isBefore(now)) {
      targetDate = anniversaryDate.year(now.year() + 1)
    }
    
    const diff = targetDate.diff(now)
    if (diff < minTime) {
      minTime = diff
      nextAnniversary = anniversary
    }
  })
  
  if (nextAnniversary && minTime !== Infinity) {
    // 保存最近纪念日的名称
    nextAnniversaryName.value = nextAnniversary.name
    
    if (minTime <= 0) {
      anniversaryCountdown.value = '就是今天！🎉'
    } else {
      const days = Math.floor(minTime / (1000 * 60 * 60 * 24))
      const hours = Math.floor((minTime % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
      const minutes = Math.floor((minTime % (1000 * 60 * 60)) / (1000 * 60))
      const seconds = Math.floor((minTime % (1000 * 60)) / 1000)
      
      if (days > 0) {
        anniversaryCountdown.value = `${days}天${hours}时${minutes}分${seconds}秒`
      } else if (hours > 0) {
        anniversaryCountdown.value = `${hours}时${minutes}分${seconds}秒`
      } else if (minutes > 0) {
        anniversaryCountdown.value = `${minutes}分${seconds}秒`
      } else {
        anniversaryCountdown.value = `${seconds}秒`
      }
    }
  }
}

const calculateNextMeetingCountdown = () => {
  if (!nextMeetingDate.value) {
    nextMeetingCountdown.value = ''
    return
  }
  
  const now = dayjs()
  const meetingDate = dayjs(nextMeetingDate.value)
  
  if (meetingDate.isBefore(now)) {
    nextMeetingCountdown.value = '已过期'
    return
  }
  
  const diff = meetingDate.diff(now)
  
  if (diff <= 0) {
    nextMeetingCountdown.value = '就是今天！💕'
  } else {
    const days = Math.floor(diff / (1000 * 60 * 60 * 24))
    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
    const seconds = Math.floor((diff % (1000 * 60)) / 1000)
    
    if (days > 0) {
      nextMeetingCountdown.value = `${days}天${hours}时${minutes}分${seconds}秒`
    } else if (hours > 0) {
      nextMeetingCountdown.value = `${hours}时${minutes}分${seconds}秒`
    } else if (minutes > 0) {
      nextMeetingCountdown.value = `${minutes}分${seconds}秒`
    } else {
      nextMeetingCountdown.value = `${seconds}秒`
    }
  }
}

// 启动倒计时定时器
let countdownTimer = null
const startCountdownTimer = () => {
  // 立即计算一次
  calculateLoveCountdown()
  calculateAnniversaryCountdown()
  calculateNextMeetingCountdown()
  
  // 每秒更新一次
  countdownTimer = setInterval(() => {
    calculateLoveCountdown()
    calculateAnniversaryCountdown()
    calculateNextMeetingCountdown()
  }, 1000)
}

// 停止倒计时定时器
const stopCountdownTimer = () => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
}

// 加载倒计时配置
const loadCountdownConfigs = async () => {
  if (!isLoggedIn.value) return
  
  try {
    // 加载在一起时间配置
    try {
      if (userStore.userId) {
        const togetherDateConfig = await getTogetherDateByUserId(userStore.userId)
        if (togetherDateConfig) {
          // 如果后台返回的是日期格式，转换为完整的日期时间格式
          if (togetherDateConfig.includes('-') && !togetherDateConfig.includes(':')) {
            togetherDate.value = togetherDateConfig + ' 00:00:00'
          } else {
            togetherDate.value = togetherDateConfig
          }
        }
      }
    } catch (error) {
      console.warn('加载在一起时间配置失败，使用默认值:', error)
      // 保持默认值不变
    }
    
    // 加载纪念日列表
    try {
      if (userStore.userId) {
        const anniversaryDatesValue = await getAnniversaryDatesByUserId(userStore.userId)
        try {
          anniversaryDates.value = JSON.parse(anniversaryDatesValue)
        } catch (e) {
          anniversaryDates.value = []
        }
      }
    } catch (error) {
      console.warn('加载纪念日配置失败:', error)
      anniversaryDates.value = []
    }
    
    // 加载下次见面日期
    try {
      if (userStore.userId) {
        const nextMeetingDateValue = await getNextMeetingDateByUserId(userStore.userId)
        nextMeetingDate.value = nextMeetingDateValue
      }
    } catch (error) {
      console.warn('加载下次见面日期配置失败:', error)
      nextMeetingDate.value = ''
    }
    
    // 启动倒计时
    startCountdownTimer()
  } catch (error) {
    console.error('加载倒计时配置失败:', error)
  }
}

// 方法

const goToAdmin = () => {
  if (checkLoginState()) {
    // 已登录，直接跳转到后台管理
    router.push('/admin')
  } else {
    // 未登录，跳转到登录页面
    router.push('/login?mode=admin')
  }
}

const goToEditProfile = () => {
  if (checkLoginState()) {
    // 已登录，直接跳转到编辑个人信息页面
    router.push('/edit-profile')
  } else {
    // 未登录，跳转到登录页面
    router.push('/login?mode=user')
  }
}

const goToCalendar = () => {
  if (checkLoginState()) {
    // 已登录，直接跳转到日历页面
    router.push('/calendar')
  } else {
    // 未登录，跳转到登录页面
    router.push('/login?mode=user')
  }
}

const goToLetterBox = () => {
  if (checkLoginState()) {
    // 已登录，直接跳转到信箱页面
    router.push('/letters')
  } else {
    // 未登录，跳转到登录页面
    router.push('/login?mode=user')
  }
}

// 跳转到信件详情页
const goToLetterDetail = () => {
  if (checkLoginState()) {
    // 已登录，如果有最新信件，跳转到信件详情页
    if (latestLetter.value && latestLetter.value.id) {
      router.push(`/letter/${latestLetter.value.id}`)
    } else {
      // 没有信件，跳转到信箱页面
      router.push('/letters')
    }
  } else {
    // 未登录，跳转到登录页面
    router.push('/login?mode=user')
  }
}

const goToChatRecord = () => {
  if (checkLoginState()) {
    // 已登录，直接跳转到聊天记录页面
    router.push('/chat-record')
  } else {
    // 未登录，跳转到登录页面
    router.push('/login?mode=user')
  }
}

const goToMovies = () => {
  if (checkLoginState()) {
    // 已登录，直接跳转到电影列表页面
    router.push('/movies')
  } else {
    // 未登录，跳转到登录页面
    router.push('/login?mode=user')
  }
}

const goToAnniversaryList = () => {
  if (checkLoginState()) {
    // 已登录，直接跳转到纪念日列表页面
    router.push('/anniversary-list')
  } else {
    // 未登录，跳转到登录页面
    router.push('/login?mode=user')
  }
}







const goToHome = () => {
  if (checkLoginState() && partnerInfo.value.hasPartner) {
    // 已登录且有伴侣，跳转到首页
    router.push('/home')
  } else {
    showToast('需要先建立伴侣关系')
  }
}

const goToLogin = () => {
  if (checkLoginState()) {
    // 已登录，直接跳转到首页
    router.push('/home')
  } else {
    // 未登录，跳转到登录页面
    router.push('/login?mode=user')
  }
}





// 防抖变量和缓存
let loadPartnerInfoTimer = null
let lastLoadTime = 0
const CACHE_DURATION = 5000 // 5秒缓存

// 获取伴侣信息
const loadPartnerInfo = async () => {
  if (!isLoggedIn.value) return
  
  // 检查缓存，避免频繁请求
  const now = Date.now()
  if (now - lastLoadTime < CACHE_DURATION) {
    return
  }
  
  // 防抖处理，避免频繁调用
  if (loadPartnerInfoTimer) {
    clearTimeout(loadPartnerInfoTimer)
  }
  
  loadPartnerInfoTimer = setTimeout(async () => {
    try {
      const response = await getPartnerInfo()
      partnerInfo.value = response.data
      lastLoadTime = Date.now() // 更新最后加载时间
      
      // 更新用户store中的伴侣信息
      if (partnerInfo.value.hasPartner && partnerInfo.value.partnerId) {
        userStore.updatePartnerRelationship(partnerInfo.value.partnerId)
      } else {
        // 如果没有伴侣，清除partnerId
        userStore.updatePartnerRelationship(null)
      }
    } catch (error) {
      // 获取后端返回的错误信息
      const errorMessage = error.response?.data?.message || error.message || '获取伴侣信息失败'
      console.error('获取伴侣信息失败:', errorMessage, error)
      // 不显示toast，因为这是后台静默加载
    }
  }, 100) // 100ms防抖延迟
}

// 发送邀请
const sendInvite = async () => {
  if (!inviteUsername.value.trim()) {
    showToast('请输入用户名')
    return
  }
  
  inviteLoading.value = true
  try {
    await invitePartner(inviteUsername.value.trim())
    showToast('邀请发送成功')
    showInviteDialog.value = false
    inviteUsername.value = ''
    await loadPartnerInfo()
  } catch (error) {
    // 获取后端返回的错误信息
    const errorMessage = error.response?.data?.message || error.message || '邀请发送失败'
    showToast(errorMessage)
    console.error('发送邀请失败:', error)
  } finally {
    inviteLoading.value = false
  }
}

// 接受邀请
const handleAcceptInvitation = async () => {
  if (!partnerInfo.value.pendingInvitation) return
  
  acceptLoading.value = true
  try {
    await acceptInvitation(partnerInfo.value.pendingInvitation.id)
    showToast('伴侣关系建立成功')
    showInvitationDialog.value = false
    
    // 重新加载伴侣信息并更新store
    await loadPartnerInfo()
    
    // 强制刷新用户信息以确保store同步
    await userStore.initUserState()
  } catch (error) {
    // 获取后端返回的错误信息
    const errorMessage = error.response?.data?.message || error.message || '接受邀请失败'
    showToast(errorMessage)
    console.error('接受邀请失败:', error)
  } finally {
    acceptLoading.value = false
  }
}

// 拒绝邀请
const handleRejectInvitation = async () => {
  if (!partnerInfo.value.pendingInvitation) return
  
  rejectLoading.value = true
  try {
    await rejectInvitation(partnerInfo.value.pendingInvitation.id)
    showToast('已拒绝邀请')
    showInvitationDialog.value = false
    await loadPartnerInfo()
  } catch (error) {
    // 获取后端返回的错误信息
    const errorMessage = error.response?.data?.message || error.message || '拒绝邀请失败'
    showToast(errorMessage)
    console.error('拒绝邀请失败:', error)
  } finally {
    rejectLoading.value = false
  }
}

// 二次确认解除伴侣关系
const confirmUnbindPartner = () => {
  showDialog({
    title: '确认解除关系',
    message: '确定要解除与伴侣的关系吗？此操作不可撤销。',
    showCancelButton: true,
    confirmButtonText: '确定解除',
    cancelButtonText: '取消',
    confirmButtonColor: '#ff6b9d'
  }).then(() => {
    handleUnbindPartner()
  }).catch(() => {
    // 用户取消操作
  })
}

// 解除伴侣关系
const handleUnbindPartner = async () => {
  unbindLoading.value = true
  try {
    await unbindPartner()
    showToast('伴侣关系已解除')
    showPartnerDialog.value = false
    
    // 重新加载伴侣信息并更新store
    await loadPartnerInfo()
    
    // 强制刷新用户信息以确保store同步
    await userStore.initUserState()
  } catch (error) {
    // 获取后端返回的错误信息
    const errorMessage = error.response?.data?.message || error.message || '解除关系失败'
    showToast(errorMessage)
    console.error('解除关系失败:', error)
  } finally {
    unbindLoading.value = false
  }
}

// 取消邀请
const handleCancelInvitation = async () => {
  if (!partnerInfo.value.sentInvitation) return
  
  cancelLoading.value = true
  try {
    await cancelInvitation(partnerInfo.value.sentInvitation.id)
    showToast('邀请已取消')
    showSentInvitationDialog.value = false
    await loadPartnerInfo()
  } catch (error) {
    // 获取后端返回的错误信息
    const errorMessage = error.response?.data?.message || error.message || '取消邀请失败'
    showToast(errorMessage)
    console.error('取消邀请失败:', error)
  } finally {
    cancelLoading.value = false
  }
}

// 监听登录状态变化
watch(isLoggedIn, async (newValue, oldValue) => {
  // 只有在从未登录变为已登录时才加载伴侣信息
  if (newValue && !oldValue) {
    await loadPartnerInfo()
    await loadCountdownConfigs()
  }
})

// 生命周期
onMounted(async () => {
  // 初始化用户状态
  await userStore.initUserState()
  // 只有在登录状态下才加载伴侣信息
  if (isLoggedIn.value) {
    await loadPartnerInfo()
  }
  // 加载倒计时配置
  await loadCountdownConfigs()
})

onUnmounted(() => {
  stopCountdownTimer()
})

// 退出登录
const handleLogout = async () => {
  try {
    // 调用store的登出方法
    await userStore.userLogout()
    showToast('已退出登录')
    router.push('/login?mode=user')
  } catch (error) {
    console.error('退出登录失败:', error)
    // 即使后端调用失败，也清除本地状态
    userStore.clearUserState()
    showToast('已退出登录')
    router.push('/login?mode=user')
  }
}

// 获取回忆记录
const loadCurrentDiary = async () => {
  if (!isLoggedIn.value) return
  
  try {
    const response = await getLatestDiary() // 获取最新的1条记录
    if (response) {
      currentDiary.value = response
    }
  } catch (error) {
    console.error('获取回忆记录失败:', error)
  }
}

// 获取最近信件
const loadLatestLetter = async () => {
  if (!isLoggedIn.value) return
  
  try {
    // 优先获取收到的最近一封解锁的信件
    const unlockedLetters = await getUnlockedLetters()
    if (unlockedLetters && unlockedLetters.length > 0) {
      // 获取最新的信件（按创建时间排序）
      latestLetter.value = unlockedLetters[0]
      return
    }
    
    // 如果没有解锁的信件，获取自己写的第一封信
    const sentLetters = await getSentLetters()
    if (sentLetters && sentLetters.length > 0) {
      // 获取最新的信件（按创建时间排序）
      latestLetter.value = sentLetters[0]
    }
  } catch (error) {
    console.error('获取最近信件失败:', error)
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  return dayjs(dateString).format('YYYY年MM月DD日')
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return dayjs(dateTime).format('MM月DD日 HH:mm')
}


// 处理伴侣卡片点击
const handlePartnerCardClick = () => {
  if (!partnerInfo.value.hasPendingInvitation && !partnerInfo.value.hasSentInvitation) {
    // 没有伴侣时显示邀请对话框
    showInviteDialog.value = true
  } else if (partnerInfo.value.hasPendingInvitation) {
    // 有邀请时显示邀请处理对话框
    showInvitationDialog.value = true
  } else if (partnerInfo.value.hasSentInvitation) {
    // 已发送邀请时显示邀请状态对话框
    showSentInvitationDialog.value = true
  }
}

// 页面加载时执行
onMounted(() => {
  if (isLoggedIn.value) {
    loadPartnerInfo()
    loadCountdownConfigs()
    loadCurrentDiary()
    loadLatestLetter()
  }
})

// 监听登录状态变化
watch(isLoggedIn, (newValue) => {
  if (newValue) {
    loadPartnerInfo()
    loadCountdownConfigs()
    loadCurrentDiary()
    loadLatestLetter()
  } else {
    // 登出时清除定时器
    stopCountdownTimer()
  }
})

// 组件卸载时清除定时器
onUnmounted(() => {
  stopCountdownTimer()
})
</script>

<style lang="scss" scoped>
.welcome-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 15px;
  position: relative;
}

.welcome-container {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 18px;
  padding: 18px;
  width: 100%;
  max-width: 460px;
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.12);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  position: relative;
  z-index: 10;
}

.admin-actions {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 0px;
  
  .van-button {
    height: 36px;
    padding: 0 14px;
    font-size: 12px;
    border-radius: 18px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 5px;
    border: none;
    transition: all 0.3s ease;
    
    .btn-icon {
      font-size: 14px;
    }
    
    .btn-text {
      font-size: 12px;
      font-weight: 500;
    }
  }
  
  .admin-btn {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    
    &:hover {
      background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
      transform: translateY(-2px);
      box-shadow: 0 6px 16px rgba(102, 126, 234, 0.3);
    }
  }
  
  .profile-btn {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
    color: white;
    
    &:hover {
      background: linear-gradient(135deg, #3e9bed 0%, #00e1ed 100%);
      transform: translateY(-2px);
      box-shadow: 0 6px 16px rgba(79, 172, 254, 0.3);
    }
  }
  
  .logout-btn {
    background: linear-gradient(135deg, #ff6b9d 0%, #f093fb 100%);
    color: white;
    
    &:hover {
      background: linear-gradient(135deg, #f55a8b 0%, #e085e8 100%);
      transform: translateY(-2px);
      box-shadow: 0 6px 16px rgba(255, 107, 157, 0.3);
    }
  }
}

.welcome-header {
  text-align: center;
  margin-bottom: 12px;
  margin-top: 5px;
  
  .logo {
    font-size: 36px;
    margin-bottom: 8px;
    display: block;
  }
  
  .title {
    font-size: 22px;
    font-weight: bold;
    margin-bottom: 6px;
  }
  
  .subtitle {
    color: #666;
    font-size: 13px;
    opacity: 0.8;
  }
}

// 倒计时区域样式
.countdown-section-compact {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 8px;
}

.countdown-card-compact {
  padding: 10px;
  border-radius: 8px;
  text-align: center;
  transition: all 0.3s ease;
  cursor: pointer;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 15px rgba(255, 107, 157, 0.15);
  }
}

.countdown-header-compact {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-bottom: 8px;
}

.countdown-emoji {
  font-size: 16px;
}

.countdown-title-compact {
  color: #333;
  font-size: 13px;
  font-weight: 600;
  margin: 0;
}

.countdown-time-compact {
  font-size: 15px;
  font-weight: bold;
  color: #ff6b9d;
  margin-bottom: 3px;
  line-height: 1.2;
}

.countdown-description-compact {
  font-size: 12px;
  color: #666;
  font-style: italic;
  margin-bottom: 3px;
}

.click-hint-compact {
  font-size: 11px;
  color: #ff6b9d;
  text-align: center;
  margin-top: 4px;
  font-style: italic;
  opacity: 0.8;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-5px);
  }
}

.welcome-content {
  margin-bottom: 20px;
}

.feature-grid-compact {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
  margin-bottom: 12px;
}

.feature-item-compact {
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(255, 107, 157, 0.1);
  border-radius: 12px;
  padding: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 10px;
  
  &:hover {
    background: rgba(255, 107, 157, 0.05);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(255, 107, 157, 0.15);
  }
}

.feature-icon-compact {
  font-size: 20px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 107, 157, 0.1);
  border-radius: 10px;
  flex-shrink: 0;
}

.feature-text-compact {
  flex: 1;
  
  h3 {
    color: #333;
    font-size: 14px;
    font-weight: 600;
    margin: 0 0 4px 0;
    line-height: 1.2;
  }
  
  p {
    color: #666;
    font-size: 11px;
    margin: 0;
    line-height: 1.3;
  }
}

.admin-feature-compact {
  background: rgba(79, 172, 254, 0.05) !important;
  border: 1px solid rgba(79, 172, 254, 0.1) !important;
  
  &:hover {
    background: rgba(79, 172, 254, 0.1) !important;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(79, 172, 254, 0.2);
  }
  
  .feature-icon-compact {
    background: rgba(79, 172, 254, 0.1) !important;
  }
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  background: rgba(255, 107, 157, 0.05);
  border-radius: 12px;
  border: 1px solid rgba(255, 107, 157, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    background: rgba(255, 107, 157, 0.1);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(255, 107, 157, 0.2);
  }
  
  &:active {
    transform: translateY(0);
  }
  
  .feature-icon {
    font-size: 28px;
    width: 45px;
    height: 45px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(255, 107, 157, 0.1);
    border-radius: 10px;
  }
  
  .feature-text {
    flex: 1;
    
    h3 {
      color: #333;
      font-size: 16px;
      font-weight: 600;
      margin: 0 0 3px 0;
    }
    
    p {
      color: #666;
      font-size: 13px;
      margin: 0;
    }
  }
}



@media (max-width: 768px) {
  .welcome-container {
    padding: 18px 15px;
    margin: 8px;
    max-width: calc(100vw - 16px);
  }
  
  .welcome-header {
    margin-bottom: 10px;
    margin-top: 3px;
    
    .logo {
      font-size: 34px;
      margin-bottom: 6px;
    }
    
    .title {
      font-size: 20px;
      margin-bottom: 4px;
    }
    
    .subtitle {
      font-size: 12px;
    }
  }
  
  .status-countdown-section {
    margin-bottom: 8px;
    gap: 6px;
  }
  
  .partner-status-compact {
    padding: 6px;
  }
  
  .partner-invite-compact,
  .partner-info-compact,
  .partner-invitation-compact,
  .partner-sent-invitation-compact {
    padding: 5px;
    gap: 6px;
  }
  
  .partner-invite-icon,
  .partner-avatar,
  .invitation-icon {
    font-size: 20px;
    width: 32px;
    height: 32px;
  }
  
  .partner-invite-text h3,
  .partner-info h3,
  .invitation-info h3 {
    font-size: 13px;
    margin-bottom: 2px;
  }
  
  .partner-invite-text p,
  .partner-info p,
  .invitation-info p {
    font-size: 11px;
  }
  
  .countdown-section-compact {
    grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
    gap: 6px;
  }
  
  .countdown-card-compact {
    padding: 4px;
  }
  
  .countdown-title-compact {
    font-size: 12px;
  }
  
  .countdown-time-compact {
    font-size: 14px;
  }
  
  .feature-grid-compact {
    gap: 6px;
    margin-bottom: 10px;
  }
  
  .feature-item-compact {
    padding: 10px;
    gap: 8px;
  }
  
  .feature-icon-compact {
    font-size: 18px;
    width: 36px;
    height: 36px;
  }
  
  .feature-text-compact h3 {
    font-size: 13px;
    margin-bottom: 3px;
  }
  
  .feature-text-compact p {
    font-size: 10px;
  }
  
  .login-section-compact {
    margin: 6px 0;
  }
  
  .logout-section-compact {
    margin-top: 6px;
  }
}

// 伴侣状态和倒计时区域样式
.status-countdown-section {
  margin-bottom: 10px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.partner-status-compact {
  padding: 8px;
  background: rgba(255, 107, 157, 0.05);
  border-radius: 10px;
  border: 1px solid rgba(255, 107, 157, 0.1);
}

.partner-invite-compact,
.partner-info-compact,
.partner-invitation-compact,
.partner-sent-invitation-compact {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 6px;
  border-radius: 6px;
  transition: all 0.3s ease;
  
  &:hover {
    background: rgba(255, 107, 157, 0.1);
    transform: translateY(-1px);
  }
}

.partner-invite-icon,
.partner-avatar,
.invitation-icon {
  font-size: 24px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 107, 157, 0.1);
  border-radius: 8px;
}

.partner-invite-text,
.partner-info,
.invitation-info {
  flex: 1;
  
  h3 {
    color: #333;
    font-size: 14px;
    font-weight: 600;
    margin: 0 0 3px 0;
  }
  
  p {
    color: #666;
    font-size: 12px;
    margin: 0;
    line-height: 1.3;
  }
  
  .click-hint {
    color: #667eea;
    font-size: 11px;
    font-style: italic;
    margin-top: 3px;
  }
}

// 登录区域样式
.login-section-compact {
  display: flex;
  justify-content: center;
  margin: 8px 0;
  
  .van-button {
    height: 44px;
    font-size: 16px;
    font-weight: 600;
    padding: 0 24px;
    
    .btn-icon {
      margin-right: 6px;
    }
  }
}

// 弹窗样式
.invite-dialog-content,
.partner-dialog-content,
.invitation-dialog-content {
  padding: 15px;
  position: relative;
}



.invite-tip {
  color: #666;
  font-size: 14px;
  margin-bottom: 15px;
  text-align: center;
}

.invite-actions,
.invitation-actions {
  display: flex;
  gap: 10px;
  margin-top: 15px;
  justify-content: center;
  
  .van-button {
    flex: 1;
  }
}

.partner-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 15px;
  
  .van-button {
    width: 100%;
  }
  
  .enter-space-btn {
    background: linear-gradient(135deg, #ff6b9d 0%, #f093fb 100%);
    color: white;
    border: none;
    
    &:hover {
      background: linear-gradient(135deg, #f55a8b 0%, #e085e8 100%);
    }
  }
}

.partner-detail,
.invitation-detail {
  text-align: center;
  margin-bottom: 20px;
}

.partner-avatar-large,
.invitation-avatar {
  font-size: 48px;
  margin-bottom: 15px;
}

.partner-avatar-large {
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 50%;
  padding: 10px;
  
  &:hover {
    background: rgba(255, 107, 157, 0.1);
    transform: scale(1.1);
  }
  
  &.unbind-loading {
    opacity: 0.6;
    pointer-events: none;
  }
}



.partner-detail h3,
.invitation-detail h3 {
  color: #333;
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 10px 0;
}

.partner-detail p,
.invitation-detail p {
  color: #666;
  font-size: 14px;
  margin: 5px 0;
}

.invitation-time {
  color: #999 !important;
  font-size: 12px !important;
}

.login-section-compact {
  display: flex;
  justify-content: center;
  margin: 8px 0;
}

.logout-section-compact {
  display: flex;
  justify-content: center;
  margin-top: 8px;
}

.logout-btn-compact {
  height: 32px;
  padding: 0 14px;
  font-size: 11px;
  border-radius: 16px;
  background: rgba(255, 107, 157, 0.1);
  color: #ff6b9d;
  border: 1px solid rgba(255, 107, 157, 0.2);
  
  &:hover {
    background: rgba(255, 107, 157, 0.2);
    transform: translateY(-1px);
  }
  
  .btn-icon {
    margin-right: 4px;
  }
}

// 轮播图样式
.carousel-section {
  margin: 20px 0;
  padding: 0 10px;
}

.love-carousel {
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  
  :deep(.van-swipe__indicators) {
    bottom: 15px;
    
    .van-swipe__indicator {
      width: 8px;
      height: 8px;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.4);
      margin: 0 4px;
      transition: all 0.3s ease;
      
      &.van-swipe__indicator--active {
        background: rgba(255, 107, 157, 0.8);
        transform: scale(1.2);
      }
    }
  }
}

.carousel-card {
  height: 200px;
  margin: 0 10px;
  border-radius: 20px;
  padding: 25px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  position: relative;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-5px) scale(1.02);
    box-shadow: 0 15px 40px rgba(0, 0, 0, 0.15);
  }
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, 
      rgba(255, 107, 157, 0.1) 0%, 
      rgba(255, 138, 171, 0.1) 50%, 
      rgba(255, 194, 209, 0.1) 100%);
    z-index: 1;
  }
  
  > * {
    position: relative;
    z-index: 2;
  }
}

.partner-card {
  background: linear-gradient(135deg, #ff6b9d 0%, #ff8fab 50%, #ffc2d1 100%);
  color: white;
  
  .card-icon {
    font-size: 48px;
    margin-bottom: 15px;
    animation: heartbeat 2s ease-in-out infinite;
  }
  
  .card-title {
    font-size: 20px;
    font-weight: 600;
    margin-bottom: 8px;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
  
  .card-subtitle {
    font-size: 14px;
    opacity: 0.9;
    margin-bottom: 12px;
    line-height: 1.4;
  }
  
  .card-action {
    font-size: 12px;
    opacity: 0.8;
    padding: 6px 12px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 15px;
    backdrop-filter: blur(10px);
  }
}

.countdown-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  color: white;
  
  .card-icon {
    font-size: 40px;
    margin-bottom: 12px;
    animation: pulse 2s ease-in-out infinite;
  }
  
  .card-title {
    font-size: 18px;
    font-weight: 600;
    margin-bottom: 8px;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
  
  .countdown-display {
    font-size: 24px;
    font-weight: 700;
    margin-bottom: 8px;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
    background: rgba(255, 255, 255, 0.2);
    padding: 8px 16px;
    border-radius: 20px;
    backdrop-filter: blur(10px);
  }
  
  .card-subtitle {
    font-size: 13px;
    opacity: 0.9;
    margin-bottom: 8px;
    line-height: 1.4;
  }
  
  .card-action {
    font-size: 11px;
    opacity: 0.8;
    padding: 4px 10px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 12px;
    backdrop-filter: blur(10px);
  }
}

.memory-card {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 50%, #fecfef 100%);
  color: white;
  
  .memory-image-container {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    overflow: hidden;
    margin-bottom: 15px;
    background: rgba(255, 255, 255, 0.2);
    display: flex;
    align-items: center;
    justify-content: center;
    
    .memory-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
      border-radius: 50%;
    }
  }
  
  .memory-placeholder {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    
    .card-icon {
      font-size: 36px;
      opacity: 0.8;
    }
  }
  
  .card-title {
    font-size: 18px;
    font-weight: 600;
    margin-bottom: 8px;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 200px;
  }
  
  .card-subtitle {
    font-size: 13px;
    opacity: 0.9;
    line-height: 1.4;
  }
}

.letter-card {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 50%, #fecfef 100%);
  color: white;
  
  .letter-icon-container {
    position: relative;
    margin-bottom: 15px;
    
    .card-icon {
      font-size: 48px;
      animation: heartbeat 2s ease-in-out infinite;
    }
    
    .unread-badge {
      position: absolute;
      top: -5px;
      right: -5px;
      background: #ff4757;
      color: white;
      font-size: 10px;
      padding: 2px 6px;
      border-radius: 10px;
      font-weight: bold;
    }
  }
  
  .card-title {
    font-size: 18px;
    font-weight: 600;
    margin-bottom: 8px;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 200px;
  }
  
  .card-subtitle {
    font-size: 13px;
    opacity: 0.9;
    margin-bottom: 4px;
    line-height: 1.4;
  }
  
  .card-date {
    font-size: 11px;
    opacity: 0.8;
    line-height: 1.4;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .carousel-section {
    margin: 15px 0;
    padding: 0 5px;
  }
  
  .carousel-card {
    height: 180px;
    margin: 0 5px;
    padding: 20px;
    
    .card-icon {
      font-size: 36px;
      margin-bottom: 12px;
    }
    
    .card-title {
      font-size: 16px;
    }
    
    .countdown-display {
      font-size: 20px;
      padding: 6px 12px;
    }
    
    .card-subtitle {
      font-size: 12px;
    }
  }
  
  .memory-card {
    .memory-image-container {
      width: 60px;
      height: 60px;
      margin-bottom: 12px;
      
      .memory-placeholder .card-icon {
        font-size: 28px;
      }
    }
    
    .card-title {
      font-size: 16px;
      max-width: 150px;
    }
  }
  
  .letter-card {
    .letter-icon-container {
      margin-bottom: 12px;
      
      .card-icon {
        font-size: 36px;
      }
      
      .unread-badge {
        font-size: 9px;
        padding: 1px 4px;
      }
    }
    
    .card-title {
      font-size: 16px;
      max-width: 150px;
    }
    
    .card-subtitle {
      font-size: 12px;
    }
    
    .card-date {
      font-size: 10px;
    }
  }
  
  .love-carousel {
    :deep(.van-swipe__indicators) {
      bottom: 10px;
      
      .van-swipe__indicator {
        width: 6px;
        height: 6px;
        margin: 0 3px;
      }
    }
  }
}
</style> 