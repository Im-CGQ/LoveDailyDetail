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
        <p class="subtitle pulse">记录和女朋友的每一天</p>
      </div>

      <div class="welcome-content">
        <div class="feature-list">
          <div class="feature-item">
            <span class="feature-icon">📅</span>
            <div class="feature-text">
              <h3>时光日历</h3>
              <p>记录每一个重要的日子</p>
            </div>
          </div>
          
          <div class="feature-item">
            <span class="feature-icon">📸</span>
            <div class="feature-text">
              <h3>照片回忆</h3>
              <p>珍藏美好的瞬间</p>
            </div>
          </div>
          
          <div class="feature-item">
            <span class="feature-icon">💌</span>
            <div class="feature-text">
              <h3>情感记录</h3>
              <p>写下内心的感受</p>
            </div>
          </div>
        </div>
      </div>

      <div class="welcome-actions">
        <van-button 
          type="primary" 
          size="large" 
          @click="goToLogin"
          class="btn-primary ripple"
          style="z-index: 1000; position: relative;"
        >
          <span class="btn-icon">💕</span>
          开始使用
        </van-button>
        
        <van-button 
          type="default" 
          size="large" 
          @click="goToAdmin"
          class="admin-btn"
        >
          <span class="btn-icon">⚙️</span>
          后台管理
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { checkLoginState } from '@/utils/auth'

const router = useRouter()

const goToLogin = () => {
  if (checkLoginState()) {
    // 已登录，直接跳转到首页
    router.push('/home')
  } else {
    // 未登录，跳转到登录页面
    router.push('/login?mode=user')
  }
}

const goToAdmin = () => {
  if (checkLoginState()) {
    // 已登录，直接跳转到后台管理
    router.push('/admin')
  } else {
    // 未登录，跳转到登录页面
    router.push('/login?mode=admin')
  }
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
  border-radius: 25px;
  padding: 40px;
  width: 100%;
  max-width: 500px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  position: relative;
  z-index: 10;
}

.welcome-header {
  text-align: center;
  margin-bottom: 35px;
  
  .logo {
    font-size: 48px;
    margin-bottom: 15px;
    display: block;
  }
  
  .title {
    font-size: 28px;
    font-weight: bold;
    margin-bottom: 10px;
  }
  
  .subtitle {
    color: #666;
    font-size: 16px;
    opacity: 0.8;
  }
}

.welcome-content {
  margin-bottom: 35px;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: rgba(255, 107, 157, 0.05);
  border-radius: 15px;
  border: 1px solid rgba(255, 107, 157, 0.1);
  
  .feature-icon {
    font-size: 32px;
    width: 50px;
    height: 50px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(255, 107, 157, 0.1);
    border-radius: 12px;
  }
  
  .feature-text {
    flex: 1;
    
    h3 {
      color: #333;
      font-size: 18px;
      font-weight: 600;
      margin: 0 0 5px 0;
    }
    
    p {
      color: #666;
      font-size: 14px;
      margin: 0;
    }
  }
}

.welcome-actions {
  display: flex;
  flex-direction: column;
  gap: 15px;
  
  .van-button {
    height: 50px;
    font-size: 18px;
    font-weight: 600;
    
    .btn-icon {
      margin-right: 8px;
    }
  }
  
  .admin-btn {
    background: rgba(102, 126, 234, 0.1);
    border: 1px solid rgba(102, 126, 234, 0.2);
    color: #667eea;
    
    &:hover {
      background: rgba(102, 126, 234, 0.2);
    }
  }
}

@media (max-width: 768px) {
  .welcome-container {
    padding: 30px 25px;
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
  
  .welcome-actions .van-button {
    height: 48px;
    font-size: 16px;
  }
}
</style> 