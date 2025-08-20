<template>
  <div class="test-login-page page-container">
    <div class="test-container">
      <h1>登录状态测试</h1>
      
      <div class="status-info">
        <h3>当前登录状态：</h3>
        <p><strong>已登录：</strong>{{ isLoggedIn ? '是' : '否' }}</p>
        <p><strong>用户名：</strong>{{ userInfo?.username || '未登录' }}</p>
        <p><strong>角色：</strong>{{ userInfo?.role || '未登录' }}</p>
        <p><strong>Token：</strong>{{ token ? '存在' : '不存在' }}</p>
        <p><strong>过期时间：</strong>{{ expires || '未设置' }}</p>
      </div>
      
      <div class="actions">
        <van-button type="primary" @click="goToLogin">去登录</van-button>
        <van-button type="default" @click="clearLogin">清除登录</van-button>
        <van-button type="default" @click="refreshPage">刷新页面</van-button>
      </div>
      
      <div class="debug-info">
        <h3>调试信息：</h3>
        <pre>{{ debugInfo }}</pre>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { checkLoginState, getCurrentUser, clearLoginState } from '@/utils/auth'

const router = useRouter()
const isLoggedIn = ref(false)
const userInfo = ref(null)
const token = ref('')
const expires = ref('')
const debugInfo = ref('')

const updateStatus = () => {
  isLoggedIn.value = checkLoginState()
  userInfo.value = getCurrentUser()
  token.value = localStorage.getItem('auth_token') || ''
  expires.value = localStorage.getItem('auth_expires') || ''
  
  debugInfo.value = JSON.stringify({
    auth_token: localStorage.getItem('auth_token'),
    auth_role: localStorage.getItem('auth_role'),
    auth_username: localStorage.getItem('auth_username'),
    auth_remember: localStorage.getItem('auth_remember'),
    auth_expires: localStorage.getItem('auth_expires'),
    admin_token: localStorage.getItem('admin_token')
  }, null, 2)
}

const goToLogin = () => {
  router.push('/login?mode=user')
}

const clearLogin = () => {
  clearLoginState()
  updateStatus()
}

const refreshPage = () => {
  window.location.reload()
}

onMounted(() => {
  updateStatus()
})
</script>

<style lang="scss" scoped>
.test-login-page {
  padding: 20px;
  background: #f5f5f5;
  min-height: 100vh;
}

.test-container {
  max-width: 600px;
  margin: 0 auto;
  background: white;
  padding: 30px;
  border-radius: 15px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  
  h1 {
    text-align: center;
    color: #333;
    margin-bottom: 30px;
  }
}

.status-info {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 10px;
  margin-bottom: 20px;
  
  h3 {
    color: #333;
    margin-bottom: 15px;
  }
  
  p {
    margin: 8px 0;
    color: #666;
  }
}

.actions {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  justify-content: center;
}

.debug-info {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 10px;
  
  h3 {
    color: #333;
    margin-bottom: 15px;
  }
  
  pre {
    background: #2d3748;
    color: #e2e8f0;
    padding: 15px;
    border-radius: 8px;
    overflow-x: auto;
    font-size: 12px;
    line-height: 1.4;
  }
}
</style> 