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

             <!-- 伴侣状态显示区域 -->
       <div v-if="isLoggedIn" class="partner-status-section">
                  <!-- 没有伴侣时显示邀请图标 -->
         <div v-if="!partnerInfo.hasPartner && !partnerInfo.hasPendingInvitation && !partnerInfo.hasSentInvitation" 
              class="partner-invite-section" @click="showInviteDialog = true">
           <div class="partner-invite-icon">💝</div>
           <div class="partner-invite-text">
             <h3>邀请伴侣</h3>
             <p>与心爱的人一起记录美好时光</p>
           </div>
         </div>

                             <!-- 有伴侣时显示伴侣信息，点击显示伴侣信息弹窗 -->
           <div v-else-if="partnerInfo.hasPartner" 
                class="partner-info-section" @click="showPartnerDialog = true">
             <div class="partner-avatar">💑</div>
             <div class="partner-info">
               <h3>我的伴侣</h3>
               <p>{{ partnerInfo.partnerDisplayName || partnerInfo.partnerUsername }}</p>
             </div>
           </div>

                 <!-- 有邀请时显示邀请信息 -->
         <div v-else-if="partnerInfo.hasPendingInvitation" 
              class="partner-invitation-section" @click="showInvitationDialog = true">
           <div class="invitation-icon">💌</div>
           <div class="invitation-info">
             <h3>收到邀请</h3>
             <p>{{ partnerInfo.pendingInvitation.fromDisplayName || partnerInfo.pendingInvitation.fromUsername }} 邀请您成为伴侣</p>
           </div>
         </div>
         
         <!-- 已发送邀请时显示邀请信息 -->
         <div v-else-if="partnerInfo.hasSentInvitation" 
              class="partner-sent-invitation-section" @click="showSentInvitationDialog = true">
           <div class="invitation-icon">📤</div>
           <div class="invitation-info">
             <h3>已发送邀请</h3>
             <p>等待 {{ partnerInfo.sentInvitation.toDisplayName || partnerInfo.sentInvitation.toUsername }} 回复</p>
           </div>
         </div>
      </div>

      <div class="welcome-content">
        <div class="feature-list">
          <div class="feature-item" @click="goToCalendar">
            <span class="feature-icon">📅</span>
            <div class="feature-text">
              <h3>时光日历</h3>
              <p>记录每一个重要的日子</p>
            </div>
          </div>
          

          
          <div class="feature-item" @click="goToLetterBox">
            <span class="feature-icon">📮</span>
            <div class="feature-text">
              <h3>我的信箱</h3>
              <p>查看收到的信件</p>
            </div>
          </div>
          

          
          <div class="feature-item" @click="goToChatRecord">
            <span class="feature-icon">💬</span>
            <div class="feature-text">
              <h3>聊天记录</h3>
              <p>记录聊天时光</p>
            </div>
          </div>
          
          <div class="feature-item" @click="goToMovies">
            <span class="feature-icon">🎬</span>
            <div class="feature-text">
              <h3>一起看电影</h3>
              <p>与伴侣同步观看电影</p>
            </div>
          </div>
          

                 </div>
       </div>

       <!-- 未登录时显示登录按钮 -->
       <div v-if="!isLoggedIn" class="login-section">
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

             <!-- 管理按钮组 -->
         <div v-if="isLoggedIn" class="admin-actions">
           <van-button size="small" type="default" @click="goToAdmin" class="admin-btn" title="后台管理">
             <span class="btn-icon">🎛️</span>
             <span class="btn-text">管理</span>
           </van-button>
           <van-button size="small" type="default" @click="handleLogout" class="logout-btn" title="退出登录">
             <span class="btn-icon">🚶</span>
             <span class="btn-text">退出</span>
           </van-button>
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
               <p>用户名: {{ partnerInfo.partnerUsername }}</p>
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
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { checkLoginState, clearLoginState } from '@/utils/auth'
import { getPartnerInfo, invitePartner, acceptInvitation, rejectInvitation, unbindPartner, cancelInvitation } from '@/api/partner'
import { showToast, showDialog } from 'vant'

const router = useRouter()

// 响应式数据
const partnerInfo = ref({
  hasPartner: false,
  hasPendingInvitation: false,
  partnerId: null,
  partnerUsername: '',
  partnerDisplayName: '',
  pendingInvitation: null
})

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



// 获取伴侣信息
const loadPartnerInfo = async () => {
  if (!isLoggedIn.value) return
  
  try {
    const response = await getPartnerInfo()
    partnerInfo.value = response.data
  } catch (error) {
    console.error('获取伴侣信息失败:', error)
  }
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
    showToast('邀请发送失败')
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
    await loadPartnerInfo()
  } catch (error) {
    showToast('接受邀请失败')
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
    showToast('拒绝邀请失败')
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
    await loadPartnerInfo()
  } catch (error) {
    showToast('解除关系失败')
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
    showToast('取消邀请失败')
    console.error('取消邀请失败:', error)
  } finally {
    cancelLoading.value = false
  }
}

// 生命周期
onMounted(() => {
  loadPartnerInfo()
})

// 退出登录
const handleLogout = () => {
  clearLoginState()
  showToast('已退出登录')
  router.push('/login?mode=user')
}
</script>

<style lang="scss" scoped>
.welcome-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 20px;
  position: relative;
}

.welcome-container {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 25px;
  width: 100%;
  max-width: 480px;
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
  margin-bottom: 15px;
  margin-top: 10px;
  
  .logo {
    font-size: 40px;
    margin-bottom: 10px;
    display: block;
  }
  
  .title {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 8px;
  }
  
  .subtitle {
    color: #666;
    font-size: 14px;
    opacity: 0.8;
  }
}

.welcome-content {
  margin-bottom: 20px;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
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
    padding: 25px 20px;
    margin: 10px;
  }
  
  .welcome-header {
    .logo {
      font-size: 40px;
    }
    
    .title {
      font-size: 24px;
    }
  }
  

  
     .admin-actions {
     gap: 10px;
     margin-top: 0px;
     
     .van-button {
       height: 36px;
       padding: 0 12px;
       
       .btn-text {
         font-size: 12px;
       }
       
       .btn-icon {
         font-size: 14px;
       }
     }
   }
   
   .login-section .van-button {
     height: 48px;
     font-size: 16px;
     padding: 0 25px;
   }
 }

// 伴侣状态区域样式
.partner-status-section {
  margin-bottom: 15px;
  padding: 12px;
  background: rgba(255, 107, 157, 0.05);
  border-radius: 12px;
  border: 1px solid rgba(255, 107, 157, 0.1);
}

.partner-invite-section,
.partner-info-section,
.partner-invitation-section,
.partner-sent-invitation-section {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 10px;
  border-radius: 10px;
  transition: all 0.3s ease;
  
  &:hover {
    background: rgba(255, 107, 157, 0.1);
    transform: translateY(-2px);
  }
}

.partner-invite-icon,
.partner-avatar,
.invitation-icon {
  font-size: 28px;
  width: 45px;
  height: 45px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 107, 157, 0.1);
  border-radius: 10px;
}

.partner-invite-text,
.partner-info,
.invitation-info {
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
  
  .click-hint {
    color: #667eea;
    font-size: 12px;
    font-style: italic;
    margin-top: 3px;
  }
}

// 登录区域样式
.login-section {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  
  .van-button {
    height: 50px;
    font-size: 18px;
    font-weight: 600;
    padding: 0 30px;
    
    .btn-icon {
      margin-right: 8px;
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
</style> 